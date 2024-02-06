package ru.aasmc.productservice.storage.repository

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import ru.aasmc.productservice.BaseJpaTest
import ru.aasmc.productservice.dto.AppImage
import ru.aasmc.productservice.testdata.BLUE_T_SHIRT_XS_SKU
import ru.aasmc.productservice.testdata.OLD_BLUE_IMAGE_URL
import java.math.BigDecimal

@Sql(
    scripts = ["classpath:insert_product.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class ProductVariantRepositoryTest @Autowired constructor(
    private val productVariantRepository: ProductVariantRepository,
    private val om: ObjectMapper
): BaseJpaTest() {

    @Test
    fun removeImage_removesImage() {
        productVariantRepository.removeImage(1, OLD_BLUE_IMAGE_URL)
        val updated = productVariantRepository.findById(1).get()
        assertThat(updated.imageCollection.images).isEmpty()
    }

    @Test
    fun addImage_addsImage() {
        val newImage = AppImage(
            url = "http://images.com/new_image.png",
            isPrimary = true
        )
        val newImageStr = om.writeValueAsString(newImage)
        println(newImageStr)
        productVariantRepository.addImage(1, newImageStr)
        val updated = productVariantRepository.findById(1).get()
        assertThat(updated.imageCollection.images).hasSize(2)
        assertThat(updated.imageCollection.images).contains(newImage)
    }

    @Test
    fun updateSkuStock_updatesSkuStock() {
        productVariantRepository.updateSkuStock(BLUE_T_SHIRT_XS_SKU, 1, 100)
        val updated = productVariantRepository.findById(1).get()
        val updatedSku = updated.skuCollection.skus.first { it.sku == BLUE_T_SHIRT_XS_SKU }
        assertThat(updatedSku.stock).isEqualTo(100)
    }

    @Test
    fun updateSkuPrice_updatesSkuPrice() {
        val newPrice = BigDecimal.valueOf(1000)
        productVariantRepository.updateSkuPrice(BLUE_T_SHIRT_XS_SKU, 1, newPrice)
        val updated = productVariantRepository.findById(1).get()
        val updatedSku = updated.skuCollection.skus.first { it.sku == BLUE_T_SHIRT_XS_SKU }
        assertThat(updatedSku.price).isEqualTo(newPrice)
    }

}