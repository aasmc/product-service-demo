package ru.aasmc.productservice.controller

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import ru.aasmc.productservice.BaseIntegTest
import ru.aasmc.productservice.dto.CategoryResponse
import ru.aasmc.productservice.dto.CompositeAttributeDto
import ru.aasmc.productservice.dto.CompositeAttributeValueDto
import ru.aasmc.productservice.dto.PlainAttributeDto
import ru.aasmc.productservice.storage.repository.CategoryRepository
import ru.aasmc.productservice.testdata.*
import ru.aasmc.productservice.utils.CryptoTool
import java.time.LocalDateTime

class CategoryControllerTest @Autowired constructor (
    private val cryptoTool: CryptoTool,
    private val categoryRepository: CategoryRepository
): BaseIntegTest() {

    @Test
    fun testCreateSubCategory() {
        var topLevel = topLevelCategoryDomain()
        topLevel = categoryRepository.save(topLevel)
        val topLevelIdStr = cryptoTool.hashOf(topLevel.id!!)

        val request = subCategoryRequestWithAttributesToCreate(topLevelIdStr)
        val now = LocalDateTime.now()

        webTestClient.post()
            .uri(BASE_CATEGORIES_URL)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CategoryResponse::class.java)
            .value { response ->
                assertThat(response.categoryId).isNotNull()
                assertThat(response.name).isEqualTo(TEST_SUBCATEGORY_NAME)
                assertThat(response.parentId).isEqualTo(topLevelIdStr)
                assertThat(response.subcategoryNames).isEmpty()
                assertThat(response.createdAt).isNotNull()
                assertThat(response.createdAt).isAfter(now)
                assertThat(response.attributes).hasSize(2)
                val sortedAttrs = response.attributes.sortedBy { it.attributeName }
                val dimensAttr = sortedAttrs[0] as CompositeAttributeDto
                val sizeAttr = sortedAttrs[1] as PlainAttributeDto
                assertThat(dimensAttr.attributeName).isEqualTo(DIMENS_ATTR_NAME)
                assertThat(dimensAttr.availableValues).hasSize(3)
                assertThat(sizeAttr.attributeName).isEqualTo(CLOTHES_SIZE_ATTR_NAME)
                assertThat(sizeAttr.availableValues).hasSize(6)

                val dimenValues = dimensAttr.availableValues
                    .sortedBy { it.name }
                val depth = dimenValues[0]
                val length = dimenValues[1]
                val width = dimenValues[2]
                assertThat(depth.name).isEqualTo(DIMENS_DEPTH_NAME)
                assertThat(depth.values).hasSize(3)
                assertThat(width.name).isEqualTo(DIMENS_WIDTH_NAME)
                assertThat(width.values).hasSize(3)
                assertThat(length.name).isEqualTo(DIMENS_LENGTH_NAME)
                assertThat(length.values).hasSize(3)
            }
    }

    @Test
    fun testCreateTopLevelCategory() {
        val topLevelRequest = topLevelCategoryRequest()
        val now = LocalDateTime.now()
        webTestClient.post()
            .uri(BASE_CATEGORIES_URL)
            .bodyValue(topLevelRequest)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CategoryResponse::class.java)
            .value { response ->
                assertThat(response.categoryId).isNotNull()
                assertThat(response.name).isEqualTo(TEST_TOP_LEVEL_CATEGORY_NAME)
                assertThat(response.subcategoryNames).isEmpty()
                assertThat(response.createdAt).isAfter(now)
            }
    }

    @Test
    fun contextLoads() {

    }



}