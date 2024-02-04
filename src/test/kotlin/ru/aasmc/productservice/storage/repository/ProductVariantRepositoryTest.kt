package ru.aasmc.productservice.storage.repository

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import ru.aasmc.productservice.BaseJpaTest
import ru.aasmc.productservice.dto.AttributeCollection
import ru.aasmc.productservice.dto.ImageCollection
import ru.aasmc.productservice.dto.Sku
import ru.aasmc.productservice.dto.SkuCollection
import ru.aasmc.productservice.storage.model.Product
import ru.aasmc.productservice.storage.model.ProductVariant
import ru.aasmc.productservice.testdata.*
import java.math.BigDecimal

class ProductVariantRepositoryTest @Autowired constructor(
    private val productVariantRepository: ProductVariantRepository,
    private val categoryRepository: CategoryRepository,
    private val sellerRepository: SellerRepository,
    private val shopRepository: ShopRepository,
    private val productRepository: ProductRepository,
): BaseJpaTest() {

    private val om = ObjectMapper()
    @Sql(
        scripts = ["classpath:insert_product.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    fun updateSkuStock_updatesSkuStock() {
        val product = productRepository.findById(1L).get()
        val variant = product.variants.first { it.variantName == BLUE_T_SHIRT_VARIANT_NAME }


        productVariantRepository.update()
        productVariantRepository.flush()
        val updated = productVariantRepository.findById(variant.id!!).get()
        println(updated.skuCollection)
        val updatedSku = updated.skuCollection.skus.first { it.sku == BLUE_T_SHIRT_XS_SKU }
        assertThat(updatedSku.stock).isEqualTo(100)
    }

    private fun saveProductWithVariants(): Product {
        var category = topLevelCategoryDomain()
        category = categoryRepository.save(category)

        var seller = sellerDomain()
        seller = sellerRepository.save(seller)

        var shop = shopDomain(seller)
        shop = shopRepository.save(shop)
        seller.shops.add(shop)

        val sizeAttr = sizeAttributeDto(false)
        val colorAttr = colorAttributeDto()

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
            }.toMutableList()

        val redSizes = sizeAttr.availableValues
            .filter { value ->
                value.stringValue == SIZE_M_VALUE ||
                        value.stringValue == SIZE_L_VALUE ||
                        value.stringValue == SIZE_XL_VALUE
            }.toMutableList()

        val greenSizes = sizeAttr.availableValues
            .filter {value ->
                value.stringValue == SIZE_S_VALUE ||
                        value.stringValue == SIZE_M_VALUE ||
                        value.stringValue == SIZE_L_VALUE
            }.toMutableList()

        val blueAttributes = mutableListOf(
            sizeAttr.copy(availableValues = blueSizes),
            colorAttr.copy(availableValues = mutableListOf(blue))
        )

        val redAttributes = mutableListOf(
            sizeAttr.copy(availableValues = redSizes),
            colorAttr.copy(availableValues = mutableListOf(red))
        )

        val greenAttributes = mutableListOf(
            sizeAttr.copy(availableValues = greenSizes),
            colorAttr.copy(availableValues = mutableListOf(green))
        )

        val blueSkuCollection = SkuCollection(
            attrName = sizeAttr.attributeName,
            skus = listOf(
                Sku(
                    attrValue = SIZE_XS_VALUE,
                    sku = BLUE_T_SHIRT_XS_SKU,
                    price = BigDecimal.TEN,
                    stock = 10
                ),
                Sku(
                    attrValue = SIZE_S_VALUE,
                    sku = BLUE_T_SHIRT_S_SKU,
                    price = BigDecimal.TEN,
                    stock = 10
                ),
                Sku(
                    attrValue = SIZE_M_VALUE,
                    sku = BLUE_T_SHIRT_M_SKU,
                    price = BigDecimal.TEN,
                    stock = 10
                ),
            )
        )

        val greenSkuCollection = SkuCollection(
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

        val redSkuCollection = SkuCollection(
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

        var product = Product(
            shop = shop,
            category = category,
            name = "T-Shirt",
            description = "T-Shirt description"
        )

        val blueVariant = ProductVariant(
            variantName = BLUE_T_SHIRT_VARIANT_NAME,
            price = BigDecimal.TEN,
            attributes = AttributeCollection(blueAttributes),
            images = ImageCollection(),
            skuCollection = blueSkuCollection,
            product = product
        )

        val redVariant = ProductVariant(
            variantName = "Red T-Shirt variant",
            price = BigDecimal.TEN,
            attributes = AttributeCollection(redAttributes),
            images = ImageCollection(),
            skuCollection = redSkuCollection,
            product = product
        )

        val greenVariant = ProductVariant(
            variantName = "Green T-Shirt variant",
            price = BigDecimal.TEN,
            attributes = AttributeCollection(greenAttributes),
            images = ImageCollection(),
            skuCollection = greenSkuCollection,
            product = product
        )

        product.variants.addAll(listOf(blueVariant, redVariant, greenVariant))
        product =  productRepository.save(product)
        sellerRepository.flush()
        shopRepository.flush()
        categoryRepository.flush()
        productRepository.flush()
        productVariantRepository.flush()
        return product
    }

}