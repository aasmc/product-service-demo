package ru.aasmc.productservice.controller

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import ru.aasmc.productservice.BaseIntegTest
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.storage.model.*
import ru.aasmc.productservice.storage.repository.*
import ru.aasmc.productservice.utils.CryptoTool
import java.math.BigDecimal

class ProductControllerTest @Autowired constructor (
    private val shopRepository: ShopRepository,
    private val sellerRepository: SellerRepository,
    private val categoryRepository: CategoryRepository,
    private val attributeRepository: AttributeRepository,
    private val attributeValueRepository: AttributeValueRepository,
    private val attributeValueComponentRepository: AttributeValueComponentRepository,
    private val productOutboxRepository: ProductOutboxRepository,
    private val categoryAttributeRepository: CategoryAttributeRepository,
    private val cryptoTool: CryptoTool
): BaseIntegTest() {

    @Test
    fun testCreateProduct() {
        val seller = addSeller()
        val shop = addShop(seller)
        val dimensions = addDimensionsAttribute()
        val parentCategory = addTopLevelCategory()
        val subCategory = addSubCategory(parentCategory, dimensions)

        val productDto = CreateProductRequest(
            shopId = cryptoTool.hashOf(shop.id!!),
            categoryName = subCategory.name,
            name = "Kettle",
            description = "Kitchen Kettle",
            variants = setOf(
                ProductVariantRequestDto(
                    variantName = "Blue Kettle",
                    price = BigDecimal.TEN,
                    stock = 10,
                    attributes = mutableMapOf(
                        "DimensionsAttribute" to mapOf(
                            "width" to "10",
                            "height" to "10",
                            "depth" to "10"
                        )
                    ),
                    images = ImageCollection(
                        images = mutableListOf(
                            AppImage(
                                "http://aasmc.ru/blue_kettle.png",
                                isPrimary = true
                            )
                        )
                    )
                )
            )
        )

        webTestClient.post()
            .uri("/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(productDto)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CreateProductResponse::class.java)
            .value { response ->
                println(response)
                assertThat(response.productId).isNotNull()
                assertThat(response.variants).hasSize(1)
                assertThat(response.variants[0].variantName).isEqualTo("Blue Kettle")
            }
    }

    @Test
    fun contextLoads() {

    }

    private fun addTopLevelCategory(): Category {
        val category = Category(
            name = "House"
        )
        return categoryRepository.save(category)
    }

    private fun addSubCategory(parent: Category, attribute: Attribute): Category {
        val category = Category(
            name = "Kitchen",
            parent = parent
        )
        categoryRepository.save(category)
        val categoryAttribute = CategoryAttribute(
            isRequired = true,
            category = category,
            attribute = attribute
        )
        category.categoryAttributes.add(categoryAttribute)
        return categoryRepository.save(category)
    }

    private fun addDimensionsAttribute(): Attribute {
        val attribute = Attribute(
            name = "DimensionsAttribute"
        )
        val attributeValue = AttributeValue(
            attribute = attribute,
            value = "dimensions",
            isComposite = true
        )
        attribute.attributeValues.add(attributeValue)

        val width10Component = AttributeValueComponent(
            attributeValue = attributeValue,
            componentName = "width",
            componentValue = "10"
        )
        val width20Component = AttributeValueComponent(
            attributeValue = attributeValue,
            componentName = "width",
            componentValue = "20"
        )

        val height10Component = AttributeValueComponent(
            attributeValue = attributeValue,
            componentName = "height",
            componentValue = "10"
        )
        val height20Component = AttributeValueComponent(
            attributeValue = attributeValue,
            componentName = "height",
            componentValue = "20"
        )

        val depth10Component = AttributeValueComponent(
            attributeValue = attributeValue,
            componentName = "depth",
            componentValue = "10"
        )
        val depth20Component = AttributeValueComponent(
            attributeValue = attributeValue,
            componentName = "depth",
            componentValue = "20"
        )
        attributeValue.components.add(width10Component)
        attributeValue.components.add(width20Component)
        attributeValue.components.add(height10Component)
        attributeValue.components.add(height20Component)
        attributeValue.components.add(depth10Component)
        attributeValue.components.add(depth20Component)
        return attributeRepository.save(attribute)
    }


    private fun addSeller(): Seller {
        val seller = Seller(
            firstName = "SellerFirstName",
            lastName = "SellerLastName"
        )
        return sellerRepository.save(seller)
    }

    private fun addShop(seller: Seller): Shop {
        val shop = Shop(
            seller = seller,
            name = "Test Shop",
            description = "Test Shop Description"
        )
        return shopRepository.save(shop)
    }

}