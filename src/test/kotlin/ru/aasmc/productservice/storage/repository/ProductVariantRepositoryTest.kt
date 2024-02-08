package ru.aasmc.productservice.storage.repository

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import ru.aasmc.productservice.BaseJpaTest
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.testdata.*
import java.math.BigDecimal

@Sql(
    scripts = ["classpath:insert_product.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
class ProductVariantRepositoryTest @Autowired constructor(
    private val productVariantRepository: ProductVariantRepository,
    private val om: ObjectMapper
) : BaseJpaTest() {

    @Test
    fun removeCompositeAttributeColorValue_removesValue() {
        productVariantRepository.removeCompositeAttributeColorValue(
            1,
            COLOR_COMPOSITE_ATTR_NAME,
            COLOR_SHADE_ATTR_NAME,
            BLUE
        )
        val colorComposite = productVariantRepository.findById(1).get()
            .attributeCollection.attributes.first { it.attributeName == COLOR_COMPOSITE_ATTR_NAME }
            as CompositeAttributeDto
        val values = colorComposite.subAttributes.first { it.attributeName == COLOR_SHADE_ATTR_NAME }
            .availableValues
        assertThat(values).hasSize(1)
        val colorValues = values.map { (it as ColorAttributeValueDto).colorValue }
        assertThat(colorValues).containsExactly(RED)
    }

    @Test
    fun removeCompositeAttributeStringValue_removesValue() {
        productVariantRepository.removeCompositeAttributeStringValue(
            1,
            STRING_COMPOSITE_ATTR_NAME,
            STRING_SUBATTR_NAME,
            SIZE_XS_VALUE
        )
        val stringComposite = productVariantRepository.findById(1).get()
            .attributeCollection.attributes.first { it.attributeName == STRING_COMPOSITE_ATTR_NAME }
            as CompositeAttributeDto
        val values = stringComposite.subAttributes.first { it.attributeName ==  STRING_SUBATTR_NAME}
            .availableValues
        assertThat(values).hasSize(1)
        val strValues = values.map { (it as StringAttributeValueDto).stringValue }
        assertThat(strValues).containsExactly(SIZE_S_VALUE)
    }

    @Test
    fun removeCompositeNumericAttributeValue_removesValue() {
        productVariantRepository.removeCompositeAttributeNumericValue(
            1,
            DIMENS_ATTR_NAME,
            DIMENS_WIDTH_NAME,
            DIMENS_WIDTH_VALUE_10
        )

        val dimens = productVariantRepository.findById(1).get().attributeCollection
            .attributes.first { it.attributeName ==  DIMENS_ATTR_NAME} as CompositeAttributeDto
        val widthValues = dimens.subAttributes
            .first { it.attributeName ==  DIMENS_WIDTH_NAME}.availableValues

        assertThat(widthValues).hasSize(2)
        val numericWidthValues = widthValues.map { (it as NumericAttributeValueDto).numValue }
        assertThat(numericWidthValues).doesNotContain(DIMENS_WIDTH_VALUE_10)
    }

    @Test
    fun removeNumericAttributeValue_removesValue() {
        productVariantRepository.removeNumericAttributeValue(1, WEIGHT_ATTR_NAME, WEIGHT_VALUE_100)
        val attrValues = productVariantRepository.findById(1).get()
            .attributeCollection.attributes
            .first { it.attributeName == WEIGHT_ATTR_NAME }
            .availableValues
        assertThat(attrValues).hasSize(1)
        val numValues = attrValues.map { (it as NumericAttributeValueDto).numValue }
        assertThat(numValues).doesNotContain(WEIGHT_VALUE_100)
    }

    @Test
    fun removeStringAttributeValue_removesValue() {
        // given variant with size attribute with 6 values
        // when removing one value
        productVariantRepository.removeStringAttributeValue(1, CLOTHES_SIZE_ATTR_NAME, SIZE_XS_VALUE)
        // then variant has only 1 value
        val attrValues = productVariantRepository.findById(1).get().attributeCollection.attributes
            .first { it.attributeName == CLOTHES_SIZE_ATTR_NAME }
            .availableValues
        assertThat(attrValues).hasSize(5)
        val stringValues = attrValues.map { (it as StringAttributeValueDto).stringValue }
        assertThat(stringValues).doesNotContain(SIZE_XS_VALUE)
    }

    @Test
    fun removeColorAttributeValue_removesValue() {
        productVariantRepository.removeColorAttributeValue(1, COLOR_ATTR_NAME, RED, RED_HEX)
        val variant = productVariantRepository
            .findById(1)
            .get()

        val availableValues = variant.attributeCollection.attributes
            .first { it.attributeName == COLOR_ATTR_NAME }
            .availableValues

        assertThat(availableValues).isEmpty()
    }

    @Test
    fun addCompositeAttributeValue_addsValue() {
        val width40 = NumericAttributeValueDto(
            numValue = 40.0,
            numRuValue = null,
            numUnit = "mm"
        )
        val widthStr = om.writeValueAsString(width40)
        productVariantRepository.addCompositeAttributeValue(
            1,
            DIMENS_ATTR_NAME,
            DIMENS_WIDTH_NAME,
            widthStr
        )
        val dimens = productVariantRepository.findById(1).get()
            .attributeCollection.attributes
            .first { it.attributeName == DIMENS_ATTR_NAME } as CompositeAttributeDto
        val newValues = dimens.subAttributes.first { it.attributeName == DIMENS_WIDTH_NAME }
            .availableValues
        assertThat(newValues).hasSize(4)
        val nums = newValues.map { (it as NumericAttributeValueDto).numValue }
        assertThat(nums).contains(width40.numValue)
    }

    @Test
    fun addAttributeValue_addsValueToAttribute() {
        val blueColorAttrValue = ColorAttributeValueDto(
            colorValue = BLUE,
            colorHex = BLUE_HEX
        )
        val valueStr = om.writeValueAsString(blueColorAttrValue)
        productVariantRepository.addAttributeValue(1, COLOR_ATTR_NAME, valueStr)

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
        assertThat(attributes).hasSize(5) // size + weight + dimens + colorComposite + stringComposite
    }

    @Test
    fun addOrReplaceVariantAttribute_addsVariantAttributeWithDifferentName() {
        val xsValue = StringAttributeValueDto(
            stringRuValue = "44",
            stringValue = "XS"
        )
        val sizeAttrId = "sizeAttrId"
        val sizeAttr = StringAttributeDto(
            id = sizeAttrId,
            attributeName = CLOTHES_SIZE_ATTR_NAME,
            shortName = CLOTHES_SIZE_ATTR_SHORT_NAME,
            isFaceted = true,
            isRequired = true,
            availableValues = mutableListOf(xsValue)
        )
        val sizeStr = om.writeValueAsString(sizeAttr)
        productVariantRepository.addOrReplaceVariantAttribute(1, sizeStr, CLOTHES_SIZE_ATTR_NAME)

        val attributes = productVariantRepository.findById(1)
            .get().attributeCollection.attributes
        assertThat(attributes).hasSize(6) // color + size + weight + dimens + colorComposite + stringComposite
        assertThat(attributes).contains(sizeAttr)
    }

    @Test
    fun addOrReplaceVariantAttribute_replacesVariantAttributeWithSameName() {
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
        productVariantRepository.addOrReplaceVariantAttribute(1, colorAttrStr, COLOR_ATTR_NAME)

        val variant = productVariantRepository.findById(1)
            .get()
        val attributes = variant.attributeCollection.attributes
        assertThat(attributes).hasSize(6) // color + size + weight + dimens + colorComposite + stringComposite
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