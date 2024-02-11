package ru.aasmc.productservice.controller

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import ru.aasmc.productservice.BaseIntegTest
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.testdata.*
import java.math.BigDecimal
import java.time.LocalDateTime

class ProductControllerTest : BaseIntegTest() {

    @Test
    fun createProduct_integTest() {
        val sellerResponse = createSellerResponse()
        val shopResponse = createShopResponse(sellerResponse)
        val categoryResponse = createCategoryResponse()
        val sizeAttr = getSizeAttribute(categoryResponse)
        val colorAttr = getColorAttribute(categoryResponse)
        val blueAttributes = getBlueAttributes(sizeAttr, colorAttr)
        val redAttributes = getRedAttributes(colorAttr, sizeAttr)
        val greenAttributes = getGreenAttributes(colorAttr, sizeAttr)
        val blueSkuCollection = getBlueSkuCollection(sizeAttr)
        val greenSkuCollection = getGreenSkuCollection(sizeAttr)
        val redSkuCollection = getRedSkuCollection(sizeAttr)

        val tShirtRequest = createTshirtRequest(
            shopResponse.id,
            blueAttributes,
            blueSkuCollection,
            redAttributes,
            redSkuCollection,
            greenAttributes,
            greenSkuCollection
        )

        val now = LocalDateTime.now()

        webTestClient
            .post()
            .uri(BASE_PRODUCTS_URL)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(tShirtRequest)
            .exchange()
            .expectStatus().isCreated
            .expectBody(ProductResponse::class.java)
            .value { response ->
                assertThat(response.productId).isNotNull()
                assertThat(response.shopId).isEqualTo(shopResponse.id)
                assertThat(response.categoryName).isEqualTo(categoryResponse.name)
                assertThat(response.name).isEqualTo(tShirtRequest.name)
                assertThat(response.description).isEqualTo(tShirtRequest.description)
                assertThat(response.createdAt).isAfter(now)
                assertThat(response.variants).hasSize(3)
                val blueVariant = response.variants
                    .first { it.variantName == T_SHIRT_BLUE_VARIANT_NAME }
                val redVariant = response.variants
                    .first { it.variantName == T_SHIRT_RED_VARIANT_NAME }
                val greenVariant = response.variants
                    .first { it.variantName == T_SHIRT_GREEN_VARIANT_NAME }

                assertThat(redVariant.attributesCollection.attributes).hasSize(2)
                assertThat(blueVariant.attributesCollection.attributes).hasSize(2)
                assertThat(greenVariant.attributesCollection.attributes).hasSize(2)
                assertThat(redVariant.skuCollection).isEqualTo(redSkuCollection)
                assertThat(blueVariant.skuCollection).isEqualTo(blueSkuCollection)
                assertThat(greenVariant.skuCollection).isEqualTo(greenSkuCollection)
            }

    }

    private fun getColorAttribute(categoryResponse: CategoryResponse) =
        categoryResponse.attributes
            .first { it.attributeName == COLOR_ATTR_NAME } as ColorAttributeDto

    private fun getSizeAttribute(categoryResponse: CategoryResponse) =
        categoryResponse.attributes
            .first { it.attributeName == CLOTHES_SIZE_ATTR_NAME } as StringAttributeDto

    private fun getBlueAttributes(
        sizeAttr: StringAttributeDto,
        colorAttr: ColorAttributeDto
    ): MutableList<AttributeDto> {
        val blue = colorAttr.availableValues
            .first { it.colorValue == BLUE }

        val blueSizes = sizeAttr.availableValues
            .filter { value ->
                value.stringValue == SIZE_XS_VALUE ||
                        value.stringValue == SIZE_S_VALUE ||
                        value.stringValue == SIZE_M_VALUE
            }.toMutableList()

        val blueAttributes = mutableListOf(
            sizeAttr.copy(availableValues = blueSizes),
            colorAttr.copy(availableValues = mutableListOf(blue))
        )
        return blueAttributes
    }

    private fun getRedAttributes(
        colorAttr: ColorAttributeDto,
        sizeAttr: StringAttributeDto
    ): MutableList<AttributeDto> {
        val red = colorAttr.availableValues
            .first { it.colorValue == RED }

        val redSizes = sizeAttr.availableValues
            .filter { value ->
                value.stringValue == SIZE_M_VALUE ||
                        value.stringValue == SIZE_L_VALUE ||
                        value.stringValue == SIZE_XL_VALUE
            }.toMutableList()

        val redAttributes = mutableListOf(
            sizeAttr.copy(availableValues = redSizes),
            colorAttr.copy(availableValues = mutableListOf(red))
        )
        return redAttributes
    }

    private fun getGreenAttributes(
        colorAttr: ColorAttributeDto,
        sizeAttr: StringAttributeDto
    ): MutableList<AttributeDto> {
        val green = colorAttr.availableValues
            .first { it.colorValue == GREEN }

        val greenSizes = sizeAttr.availableValues
            .filter { value ->
                value.stringValue == SIZE_S_VALUE ||
                        value.stringValue == SIZE_M_VALUE ||
                        value.stringValue == SIZE_L_VALUE
            }.toMutableList()

        val greenAttributes = mutableListOf(
            sizeAttr.copy(availableValues = greenSizes),
            colorAttr.copy(availableValues = mutableListOf(green))
        )
        return greenAttributes
    }

    private fun getRedSkuCollection(sizeAttr: StringAttributeDto) = SkuCollection(
        attrName = sizeAttr.attributeName,
        skus = listOf(
            Sku(
                attrValue = SIZE_M_VALUE,
                sku = "t-shirt/red/M/Brand/236",
                price = BigDecimal.TEN,
                stock = 10
            ),
            Sku(
                attrValue = SIZE_L_VALUE,
                sku = "t-shirt/red/L/Brand/237",
                price = BigDecimal.TEN,
                stock = 10
            ),
            Sku(
                attrValue = SIZE_XL_VALUE,
                sku = "t-shirt/red/XL/Brand/238",
                price = BigDecimal.TEN,
                stock = 10
            ),
        )
    )

    private fun getGreenSkuCollection(sizeAttr: StringAttributeDto) = SkuCollection(
        attrName = sizeAttr.attributeName,
        skus = listOf(
            Sku(
                attrValue = SIZE_S_VALUE,
                sku = "t-shirt/green/S/Brand/233",
                price = BigDecimal.TEN,
                stock = 10
            ),
            Sku(
                attrValue = SIZE_M_VALUE,
                sku = "t-shirt/green/M/Brand/234",
                price = BigDecimal.TEN,
                stock = 10
            ),
            Sku(
                attrValue = SIZE_L_VALUE,
                sku = "t-shirt/green/L/Brand/235",
                price = BigDecimal.TEN,
                stock = 10
            ),
        )
    )

    private fun getBlueSkuCollection(sizeAttr: StringAttributeDto) = SkuCollection(
        attrName = sizeAttr.attributeName,
        skus = listOf(
            Sku(
                attrValue = SIZE_XS_VALUE,
                sku = "t-shirt/blue/XS/Brand/230",
                price = BigDecimal.TEN,
                stock = 10
            ),
            Sku(
                attrValue = SIZE_S_VALUE,
                sku = "t-shirt/blue/S/Brand/231",
                price = BigDecimal.TEN,
                stock = 10
            ),
            Sku(
                attrValue = SIZE_M_VALUE,
                sku = "t-shirt/blue/M/Brand/232",
                price = BigDecimal.TEN,
                stock = 10
            ),
        )
    )

    private fun createCategoryResponse(): CategoryResponse {
        val categoryRequest = topLevelCategoryWithAttributes()
        val categoryResponse = webTestClient.post()
            .uri(BASE_CATEGORIES_URL)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(categoryRequest)
            .exchange()
            .expectBody(CategoryResponse::class.java)
            .returnResult()
            .responseBody!!
        return categoryResponse
    }

    private fun createShopResponse(sellerResponse: CreateSellerResponse): ShopResponse {
        val shopRequest = createShopRequest(sellerResponse.id)
        val shopResponse = webTestClient.post()
            .uri(BASE_SHOPS_URL)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(shopRequest)
            .exchange()
            .expectBody(ShopResponse::class.java)
            .returnResult()
            .responseBody!!
        return shopResponse
    }

    private fun createSellerResponse(): CreateSellerResponse {
        val sellerRequest = createSellerRequest()
        val sellerResponse = webTestClient.post()
            .uri(BASE_SELLERS_URL)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(sellerRequest)
            .exchange()
            .expectBody(CreateSellerResponse::class.java)
            .returnResult()
            .responseBody!!
        return sellerResponse
    }


}