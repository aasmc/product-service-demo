package ru.aasmc.productservice.storage.repository

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import ru.aasmc.productservice.BaseJpaTest
import ru.aasmc.productservice.dto.AppImage
import ru.aasmc.productservice.dto.ColorAttributeDto
import ru.aasmc.productservice.dto.ColorAttributeValueDto
import ru.aasmc.productservice.testdata.*
import java.math.BigDecimal

@Sql(
    scripts = ["classpath:insert_product.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
class ProductVariantRepositoryTest @Autowired constructor(
    private val productVariantRepository: ProductVariantRepository,
    private val om: ObjectMapper
): BaseJpaTest() {

    @Test
    fun addAttributeValue_addsValueToAttribute() {
        val blueColorAttrValue = ColorAttributeValueDto(
            colorValue = BLUE,
            colorHex = BLUE_HEX
        )
        val valueStr = om.writeValueAsString(blueColorAttrValue)
        productVariantRepository.addAttributeValue(1, COLOR_ATTR_ID, valueStr)

        val variant = productVariantRepository
            .findById(1)
            .get()

        val availableValues = variant.attributeCollection.attributes
            .first { it.attributeName == COLOR_ATTR_NAME }
            .availableValues

        assertThat(availableValues).hasSize(2)
        assertThat(availableValues).contains(blueColorAttrValue)
    }

    @Test
    fun removeVariantAttribute_removesVariantAttribute() {
        productVariantRepository.removeVariantAttribute(1, COLOR_ATTR_NAME)
        val attributes = productVariantRepository.findById(1)
            .get().attributeCollection.attributes
        assertThat(attributes).isEmpty()
    }

    @Test
    fun addVariantAttribute_addsVariantAttribute() {
        val blueColorAttrValue = ColorAttributeValueDto(
            colorValue = BLUE,
            colorHex = BLUE_HEX
        )
        val colorAttrId = "colorAttrId"
        val colorAttr = ColorAttributeDto(
            id = colorAttrId,
            attributeName = COLOR_ATTR_NAME,
            shortName = COLOR_ATTR_NAME,
            isFaceted = true,
            isRequired = true,
            availableValues = mutableListOf(blueColorAttrValue)
        )
        val colorAttrStr = om.writeValueAsString(colorAttr)
        productVariantRepository.addVariantAttribute(1, colorAttrStr, COLOR_ATTR_NAME)

        val attributes = productVariantRepository.findById(1)
            .get().attributeCollection.attributes
        assertThat(attributes).hasSize(2)
        assertThat(attributes).contains(colorAttr)
    }

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
            isPrimary = false
        )
        val newImageStr = om.writeValueAsString(newImage)
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