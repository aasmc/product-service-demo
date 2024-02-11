package ru.aasmc.productservice.controller

import jakarta.transaction.Transactional
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Testcontainers
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.testdata.BASE_PRODUCT_VARIANTS_URL
import ru.aasmc.productservice.testdata.BLUE_T_SHIRT_XS_SKU
import ru.aasmc.productservice.testdata.sizeAttributeDto
import ru.aasmc.productservice.utils.CryptoTool
import java.math.BigDecimal
import kotlin.math.exp

@ActiveProfiles("integtest")
@SpringBootTest
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup(
    *arrayOf(
        Sql(
            scripts = ["classpath:insert_product.sql"],
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
        ),
        Sql(
            scripts = ["classpath:clear-db.sql"],
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
        )
    )
)
class ProductVariantControllerTest @Autowired constructor(
    private val cryptoTool: CryptoTool,
    private val webTestClient: WebTestClient
) {

    @Test
    fun deleteCompositeAttributeValue_returnsNotFoundForNotExistingSubAttribute() {
        val compositeName = "clothes dimensions"
        val subAttrName = "unknown"
        val value = NumericAttributeValueDto(
            numValue = 10.0,
            numRuValue = null,
            numUnit = "mm"
        )

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/composite-attribute-value-delete?attributeName=$compositeName&subAttributeName=$subAttrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(value)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun deleteCompositeAttributeValue_returnsNotFoundForNotExistingComposite() {
        val compositeName = "unknown"
        val subAttrName = "width"
        val value = NumericAttributeValueDto(
            numValue = 10.0,
            numRuValue = null,
            numUnit = "mm"
        )

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/composite-attribute-value-delete?attributeName=$compositeName&subAttributeName=$subAttrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(value)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun deleteCompositeAttributeValue_returnsNotFoundForNotExistingProductVariant() {
        val compositeName = "clothes dimensions"
        val subAttrName = "width"
        val value = NumericAttributeValueDto(
            numValue = 10.0,
            numRuValue = null,
            numUnit = "mm"
        )

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(100L)}/composite-attribute-value-delete?attributeName=$compositeName&subAttributeName=$subAttrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(value)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun deleteCompositeAttributeValue_deletesValueFromExistingAttributeOfExistingProduct() {
        val compositeName = "clothes dimensions"
        val subAttrName = "width"
        val value = NumericAttributeValueDto(
            numValue = 10.0,
            numRuValue = null,
            numUnit = "mm"
        )

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/composite-attribute-value-delete?attributeName=$compositeName&subAttributeName=$subAttrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(value)
            .exchange()
            .expectStatus().isOk
            .expectBody(ProductVariantResponse::class.java)
            .value { response ->
                val composite = response.attributesCollection.attributes.first { it.attributeName == compositeName } as CompositeAttributeDto
                val widths = composite.subAttributes.first { it.attributeName == subAttrName }.availableValues
                assertThat(widths).doesNotContain(value)
            }
    }

    @Test
    fun deleteAttributeValue_returnsNotFoundForNotExistingProductVariant() {
        val value = ColorAttributeValueDto(
            colorValue = "red",
            colorHex = "FF0000"
        )
        val attrName = "color"

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(100L)}/attribute-value-delete?attributeName=$attrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(value)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun deleteAttributeValue_returnsNotFoundForNotExistingAttribute() {
        val value = ColorAttributeValueDto(
            colorValue = "red",
            colorHex = "FF0000"
        )
        val attrName = "unknown"

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/attribute-value-delete?attributeName=$attrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(value)
            .exchange()
            .expectStatus().isNotFound
    }


    @Test
    fun deleteAttributeValue_deletesValueFromExistingAttributeOfExistingProduct() {
        val value = ColorAttributeValueDto(
            colorValue = "red",
            colorHex = "FF0000"
        )
        val attrName = "color"

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/attribute-value-delete?attributeName=$attrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(value)
            .exchange()
            .expectStatus().isOk
            .expectBody(ProductVariantResponse::class.java)
            .value { response ->
                val colors = response.attributesCollection.attributes
                    .first { it.attributeName == attrName }.availableValues

                assertThat(colors).doesNotContain(value)
            }
    }

    @Test
    fun addCompositeAttributeValue_returnsNotFoundForNotExistingSubAttribute() {
        val compositeName = "clothes dimensions"
        val subAttrName = "unknown subattribute name"
        val value = NumericAttributeValueDto(
            numValue = 10000.0,
            numRuValue = 100000.0,
            numUnit = "mm"
        )

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/composite-attribute-value?attributeName=$compositeName&subAttributeName=$subAttrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(value)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun addCompositeAttributeValue_returnsNotFoundForNotExistingCompositeAttribute() {
        val compositeName = "unknown composite name"
        val subAttrName = "width"
        val value = NumericAttributeValueDto(
            numValue = 10000.0,
            numRuValue = 100000.0,
            numUnit = "mm"
        )

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/composite-attribute-value?attributeName=$compositeName&subAttributeName=$subAttrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(value)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun addCompositeAttributeValue_returnsNotFoundForNotExistingProductVariant() {
        val compositeName = "clothes dimensions"
        val subAttrName = "width"
        val value = NumericAttributeValueDto(
            numValue = 10000.0,
            numRuValue = 100000.0,
            numUnit = "mm"
        )

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(100L)}/composite-attribute-value?attributeName=$compositeName&subAttributeName=$subAttrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(value)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun addCompositeAttributeValue_addsValueToExistingAttributeOfExistingProductVariant() {
        val compositeName = "clothes dimensions"
        val subAttrName = "width"
        val value = NumericAttributeValueDto(
            numValue = 10000.0,
            numRuValue = 100000.0,
            numUnit = "mm"
        )

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/composite-attribute-value?attributeName=$compositeName&subAttributeName=$subAttrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(value)
            .exchange()
            .expectStatus().isOk
            .expectBody(ProductVariantResponse::class.java)
            .value { response ->
                val dimens = response.attributesCollection.attributes.first { it.attributeName == compositeName } as CompositeAttributeDto
                val widths = dimens.subAttributes.first { it.attributeName == subAttrName }.availableValues
                assertThat(widths).contains(value)
            }
    }

    @Test
    fun addAttributeValue_returnsNotFoundForNotExistingAttribute() {
        val newColorValue = ColorAttributeValueDto(
            colorValue = "new color",
            colorHex = "0xff10ad"
        )
        val attrName = "unknown attr name"
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/attribute-value?attributeName=$attrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(newColorValue)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun addAttributeValue_returnsNotFoundForNotExistingProductVariant() {
        val newColorValue = ColorAttributeValueDto(
            colorValue = "new color",
            colorHex = "0xff10ad"
        )
        val attrName = "color"
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(100L)}/attribute-value?attributeName=$attrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(newColorValue)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun addAttributeValue_addsValueToExistingAttributeOfExistingProductVariant() {
        val newColorValue = ColorAttributeValueDto(
            colorValue = "new color",
            colorHex = "0xff10ad"
        )
        val attrName = "color"
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/attribute-value?attributeName=$attrName")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(newColorValue)
            .exchange()
            .expectStatus().isOk
            .expectBody(ProductVariantResponse::class.java)
            .value { response ->
                val availableValues = response.attributesCollection.attributes
                    .first { it.attributeName == attrName }.availableValues
                assertThat(availableValues).contains(newColorValue)
            }
    }

    @Test
    fun deleteVariantAttribute_returnsNotFoundForNotExistingProductVariant() {
        val attrName = "color"
        webTestClient.delete()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(100L)}/attribute?attributeName=$attrName")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun deleteVariantAttribute_deletesExistingAttributeFromExistingProductVariant() {
        val attrName = "color"
        webTestClient.delete()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/attribute?attributeName=$attrName")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(ProductVariantResponse::class.java)
            .value { response ->
                val attrs = response.attributesCollection.attributes
                    .map { it.attributeName }
                assertThat(attrs).doesNotContain(attrName)
            }

    }

    @Test
    fun addVariantAttribute_returnsNotFoundForNonExistingProductVariant() {
        val dto = sizeAttributeDto(false).copy(
            id = "newSizeAttrId",
            attributeName = "New Size Attribute Name"
        )

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(100L)}/attribute")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun addVariantAttribute_addsAttributeToExistingProductVariant() {
        val dto = sizeAttributeDto(false).copy(
            id = "newSizeAttrId",
            attributeName = "New Size Attribute Name"
        )

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/attribute")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody(ProductVariantResponse::class.java)
            .value { response ->
                val attrs = response.attributesCollection.attributes
                assertThat(attrs).contains(dto)
            }
    }

    @Test
    fun deleteVariantPhoto_returnsNotFoundForNonExistingProductVariant() {
        val photoUrl = "http://images.com/old_blue_image.png"
        webTestClient.delete()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(100L)}/photo?photoUrl=$photoUrl")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun deleteVariantPhoto_deletesPhotoFromExistingProductVariant() {
        val photoUrl = "http://images.com/old_blue_image.png"
        webTestClient.delete()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/photo?photoUrl=$photoUrl")
            .exchange()
            .expectStatus().isOk
            .expectBody(ProductVariantResponse::class.java)
            .value { response ->
                val urls = response.images.images.map { it.url }
                assertThat(urls).doesNotContain(photoUrl)
            }
    }

    @Test
    fun addVariantPhoto_returnsNotFoundForNonExistingProductVariant() {
        val photo = AppImage("http://images.com/new_image_url")
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(100L)}/photo")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(photo)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun addVariantPhoto_addsPhotoToExistingVariant() {
        val photo = AppImage("http://images.com/new_image_url")
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1L)}/photo")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(photo)
            .exchange()
            .expectStatus().isOk
            .expectBody(ProductVariantResponse::class.java)
            .value { response ->
                assertThat(response.images.images).contains(photo)
            }
    }

    @Test
    fun updateVariantName_returnsNotFoundForNonExistingProductVariant() {
        val newName = "New Product Variant Name"

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1000L)}/name?newName=$newName")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun updateVariantName_updatesNameOfExistingVariant() {
        val newName = "New Product Variant Name"

        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/${cryptoTool.hashOf(1)}/name?newName=$newName")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(ProductVariantResponse::class.java)
            .value { response ->
                assertThat(response.variantName).isEqualTo(newName)
            }
    }

    @Test
    fun updateVariantPrice_returnsNotFoundForNonExistingProductVariant() {
        val newPrice = BigDecimal.valueOf(100000L)
        val dto = UpdateProductVariantPriceRequest(cryptoTool.hashOf(1000L), newPrice)
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/price")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun updateVariantPrice_updatesPriceOfExistingVariant() {
        val newPrice = BigDecimal.valueOf(100000L)
        val dto = UpdateProductVariantPriceRequest(cryptoTool.hashOf(1L), newPrice)
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/price")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody(ProductVariantResponse::class.java)
            .value { response ->
                assertThat(response.price).isEqualTo(newPrice)
            }
    }

    @Test
    fun updateSkuPrice_returnsNotFoundForNonExistingSku() {
        val newPrice = BigDecimal.valueOf(10000L)
        val dto = UpdateSkuPriceRequest(cryptoTool.hashOf(1L), "Unknown sku", newPrice)
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/sku-stock")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun updateSkuPrice_returnsNotFoundForNonExistingProductVariant() {
        val newPrice = BigDecimal.valueOf(10000L)
        val dto = UpdateSkuPriceRequest(cryptoTool.hashOf(100L), BLUE_T_SHIRT_XS_SKU, newPrice)
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/sku-price")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun updateSkuPrice_updatesPriceOfExistingProductVariant() {
        val newPrice = BigDecimal.valueOf(10000L)
        val dto = UpdateSkuPriceRequest(cryptoTool.hashOf(1L), BLUE_T_SHIRT_XS_SKU, newPrice)
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/sku-price")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody(UpdateSkuPriceResponse::class.java)
            .value { response ->
                assertThat(response.sku).isEqualTo(BLUE_T_SHIRT_XS_SKU)
                assertThat(response.newPrice).isEqualTo(newPrice)
            }
    }

    @Test
    fun updateSkuStock_returnsNotFoundForNonExistingSku() {
        val newStock = 1000
        val dto = UpdateSkuStockRequest(cryptoTool.hashOf(1L), "Unknown sku", newStock)
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/sku-stock")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun updateSkuStock_returnsNotFoundForNonExistingProductVariant() {
        val newStock = 1000
        val dto = UpdateSkuStockRequest(cryptoTool.hashOf(1000L), BLUE_T_SHIRT_XS_SKU, newStock)
        webTestClient.patch()
            .uri("$BASE_PRODUCT_VARIANTS_URL/sku-stock")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun updateSkuStock_updatesStockOfExistingProductVariant() {
        val newStock = 1000
        val dto = UpdateSkuStockRequest(cryptoTool.hashOf(1L), BLUE_T_SHIRT_XS_SKU, newStock)
        webTestClient.patch()
            .uri(BASE_PRODUCT_VARIANTS_URL + "/sku-stock")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody(UpdateSkuStockResponse::class.java)
            .value { response ->
                assertThat(response.sku).isEqualTo(BLUE_T_SHIRT_XS_SKU)
                assertThat(response.newStock).isEqualTo(newStock)
            }
    }

}