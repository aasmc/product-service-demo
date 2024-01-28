package ru.aasmc.productservice.controller

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import ru.aasmc.productservice.BaseIntegTest
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.testdata.*
import java.time.LocalDateTime

class ProductControllerTest : BaseIntegTest() {

    @Test
    fun createProduct_integTest() {
        val sellerRequest = createSellerRequest()
        val sellerResponse = webTestClient.post()
            .uri(BASE_SELLERS_URL)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(sellerRequest)
            .exchange()
            .expectBody(CreateSellerResponse::class.java)
            .returnResult()
            .responseBody!!

        val shopRequest = createShopRequest(sellerResponse.id)
        val shopResponse = webTestClient.post()
            .uri(BASE_SHOPS_URL)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(shopRequest)
            .exchange()
            .expectBody(ShopResponse::class.java)
            .returnResult()
            .responseBody!!

        val categoryRequest = topLevelCategoryWithAttributes()
        val categoryResponse = webTestClient.post()
            .uri(BASE_CATEGORIES_URL)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(categoryRequest)
            .exchange()
            .expectBody(CategoryResponse::class.java)
            .returnResult()
            .responseBody!!

        val sizeAttr = categoryResponse.attributes
            .first { it.attributeName == CLOTHES_SIZE_ATTR_NAME } as StringAttributeDto

        val colorAttr = categoryResponse.attributes
            .first { it.attributeName == COLOR_ATTR_NAME } as ColorAttributeDto

        val blue = colorAttr.availableValues
            .first { it.colorValue == BLUE }

        val red = colorAttr.availableValues
            .first { it.colorValue == RED }

        val green = colorAttr.availableValues
            .first { it.colorValue == GREEN }

        val blueSizes = sizeAttr.availableValues
            .filter { value ->
                value.stringValue == SIZE_XS_VALUE ||
                        value.stringValue == SIZE_S_VALUE ||
                        value.stringValue == SIZE_M_VALUE
            }

        val redSizes = sizeAttr.availableValues
            .filter { value ->
                value.stringValue == SIZE_M_VALUE ||
                        value.stringValue == SIZE_L_VALUE ||
                        value.stringValue == SIZE_XL_VALUE
            }

        val greenSizes = sizeAttr.availableValues
            .filter {value ->
                value.stringValue == SIZE_S_VALUE ||
                        value.stringValue == SIZE_M_VALUE ||
                        value.stringValue == SIZE_L_VALUE
            }

        val blueAttributes = mutableListOf(
            sizeAttr.copy(availableValues = blueSizes),
            colorAttr.copy(availableValues = listOf(blue))
        )

        val redAttributes = mutableListOf(
            sizeAttr.copy(availableValues = redSizes),
            colorAttr.copy(availableValues = listOf(red))
        )

        val greenAttributes = mutableListOf(
            sizeAttr.copy(availableValues = greenSizes),
            colorAttr.copy(availableValues = listOf(green))
        )

        val tShirtRequest = createTshirtRequest(
            shopResponse.id,
            blueAttributes,
            redAttributes,
            greenAttributes
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
            }

    }


}