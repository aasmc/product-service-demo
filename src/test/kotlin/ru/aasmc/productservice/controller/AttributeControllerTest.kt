package ru.aasmc.productservice.controller

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import ru.aasmc.productservice.BaseIntegTest
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.storage.model.CategoryAttribute
import ru.aasmc.productservice.storage.repository.AttributeRepository
import ru.aasmc.productservice.storage.repository.CategoryAttributeRepository
import ru.aasmc.productservice.storage.repository.CategoryRepository
import ru.aasmc.productservice.testdata.*
import ru.aasmc.productservice.utils.CryptoTool

class AttributeControllerTest @Autowired constructor(
    private val cryptoTool: CryptoTool,
    private val categoryRepository: CategoryRepository,
    private val attributeRepository: AttributeRepository,
    private val categoryAttributeRepository: CategoryAttributeRepository
) : BaseIntegTest() {

    @Test
    fun addValueToCompositeAttribute_addsValueToExistingComposite() {
        var attr = dimensAttributeDomain()
        attr = attributeRepository.save(attr)
        val width = attr.subAttributes.first { it.name == DIMENS_WIDTH_NAME }

        val newValue = NumericAttributeValueDto(
            numValue = 40.0,
            numRuValue = null,
            numUnit = "mm"
        )
        webTestClient
            .put()
            .uri("$BASE_ATTRIBUTES_URL/${cryptoTool.hashOf(width.id!!)}/value")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(newValue)
            .exchange()
            .expectStatus().isOk
            .expectBody(AttributeValueDto::class.java)
            .value { dto ->
                dto as NumericAttributeValueDto
                assertThat(dto.numValue).isEqualTo(newValue.numValue)
                assertThat(dto.numRuValue).isNull()
                assertThat(dto.numUnit).isEqualTo(newValue.numUnit)
            }
    }


    @Test
    fun addAttributeValue_addsValueToExistingAttribute() {
        var attr = sizeAttributeDomain()
        attr = attributeRepository.save(attr)
        val newValue = StringAttributeValueDto(
            stringValue = "XXXL",
            stringRuValue = "56"
        )

        webTestClient.put()
            .uri("$BASE_ATTRIBUTES_URL/${cryptoTool.hashOf(attr.id!!)}/value")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(newValue)
            .exchange()
            .expectStatus().isOk
            .expectBody(AttributeValueDto::class.java)
            .value { value ->
                value as StringAttributeValueDto
                assertThat(value).isNotNull()
                assertThat(value.stringValue).isEqualTo(newValue.stringValue)
                assertThat(value.stringRuValue).isEqualTo(newValue.stringRuValue)
            }

        webTestClient.get()
            .uri("$BASE_ATTRIBUTES_URL/${attr.name}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(AttributeDto::class.java)
            .value { attribute ->
                val size = attribute as StringAttributeDto
                assertThat(size.availableValues).hasSize(attr.stringValues.size + 1)
                val result = size.availableValues.filterIsInstance<StringAttributeValueDto>()
                    .first { it.stringValue == newValue.stringValue }
                assertThat(result.stringValue).isEqualTo(newValue.stringValue)
                assertThat(result.stringRuValue).isEqualTo(newValue.stringRuValue)
            }
    }

    @Test
    fun getAttributesForCategory_returnsAttributesForCategory() {
        var category = topLevelCategoryDomain()
        var attr = sizeAttributeDomain()
        category = categoryRepository.save(category)
        attr = attributeRepository.save(attr)
        val cAttr = CategoryAttribute(true, category, attr)
        categoryAttributeRepository.save(cAttr)

        webTestClient.get()
            .uri("$BASE_ATTRIBUTES_URL/category/${category.name}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(AttributesCollection::class.java)
            .value { collection ->
                val attrs = collection.attributes
                assertThat(attrs).hasSize(1)
                val size = attrs[0] as StringAttributeDto
                assertThat(size.attributeName).isEqualTo(attr.name)
                assertThat(size.availableValues).hasSize(attr.stringValues.size)
            }
    }


    @Test
    fun whenAddAttributeWithValues_successfullyAddsAttribute() {
        val attrRequest = dimensionsAttributeDto(false)
        webTestClient.post()
            .uri(BASE_ATTRIBUTES_URL)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(attrRequest)
            .exchange()
            .expectStatus().isCreated
            .expectBody(AttributeDto::class.java)
            .value { response ->
                val dimenAttr = response as CompositeAttributeDto
                assertThat(dimenAttr.attributeName).isEqualTo(DIMENS_ATTR_NAME)
                assertThat(dimenAttr.subAttributes).hasSize(3)
            }
    }

    @Test
    fun whenAddAttributeWithNoValues_successfullyAddsAttribute() {
        val attrRequest = sizeAttributeDtoWithNoValues()

        webTestClient.post()
            .uri(BASE_ATTRIBUTES_URL)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(attrRequest)
            .exchange()
            .expectStatus().isCreated
            .expectBody(AttributeDto::class.java)
            .value { response ->
                val sizeAttr = response as StringAttributeDto
                assertThat(sizeAttr.id).isNotNull()
                assertThat(sizeAttr.attributeName).isEqualTo(CLOTHES_SIZE_ATTR_NAME)
                assertThat(sizeAttr.shortName).isEqualTo(CLOTHES_SIZE_ATTR_SHORT_NAME)
                assertThat(sizeAttr.isFaceted).isTrue()
                assertThat(sizeAttr.isRequired).isNull()
                assertThat(sizeAttr.availableValues).isEmpty()
            }
    }

}