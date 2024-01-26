package ru.aasmc.productservice.testdata

import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.storage.model.*
import java.math.BigDecimal

fun topLevelCategoryRequest(): CreateCategoryRequest =
    CreateCategoryRequest(
        name = TEST_TOP_LEVEL_CLOTHES_CATEGORY_NAME
    )

fun topLevelCategoryWithAttributes(): CreateCategoryRequest =
    CreateCategoryRequest(
        name = TEST_TOP_LEVEL_CLOTHES_CATEGORY_NAME,
        attributesToCreate = sizeDimensionsColorsAttributes()
    )

fun topLevelCategoryRequestWithSelectedAttributes(
    ids: Set<SelectedAttribute>
) = CreateCategoryRequest(
    name = TEST_TOP_LEVEL_CLOTHES_CATEGORY_NAME,
    selectedAttributeIds = ids
)

fun subCategoryRequestWithAttributesToCreate(parentId: String) = CreateCategoryRequest(
    name = TEST_SUBCATEGORY_T_SHIRTS_NAME,
    parentId = parentId,
    attributesToCreate = categoryAttributesToCreate()
)

fun topLevelCategoryDomain(): Category =
    Category(
        name = TEST_TOP_LEVEL_CLOTHES_CATEGORY_NAME
    )


fun categoryAttributesToCreate(): List<AttributeDto> =
    listOf(
        sizeAttributeDto(true),
        dimensionsAttributeDto(true)
    )

fun sizeDimensionsColorsAttributes(): List<AttributeDto> = listOf(
    sizeAttributeDto(true),
    dimensionsAttributeDto(true),
    colorAttributeRequest()
)


fun dimensionsAttributeDto(isRequired: Boolean) = CompositeAttributeDto(
    attributeName = DIMENS_ATTR_NAME,
    shortName = DIMENS_ATTR_SHORT_NAME,
    isFaceted = true,
    isRequired = isRequired,
    availableValues = dimensAttributeCompositeValues()
)

fun dimensAttributeDomain(): Attribute {
    val attr = Attribute(
        name = DIMENS_ATTR_NAME,
        shortName = DIMENS_ATTR_SHORT_NAME,
        isFaceted = true,
        isComposite = true
    )

    val values = listOf(
        CompositeAttributeValue(
            name = DIMENS_WIDTH_NAME,
            attribute = attr
        ),
        CompositeAttributeValue(
            name = DIMENS_LENGTH_NAME,
            attribute = attr
        ),
        CompositeAttributeValue(
            name = DIMENS_DEPTH_NAME,
            attribute = attr
        )
    )
    values[0].value.addAll(
        listOf(
            NumericAttributeValue(
                attribute = attr,
                compositeAttributeValue = values[0],
                numValue = DIMENS_WIDTH_VALUE_10,
                numUnit = "mm",
                numRuValue = null
            ),
            NumericAttributeValue(
                attribute = attr,
                compositeAttributeValue = values[0],
                numValue = DIMENS_WIDTH_VALUE_20,
                numUnit = "mm",
                numRuValue = null
            ),
            NumericAttributeValue(
                attribute = attr,
                compositeAttributeValue = values[0],
                numValue = DIMENS_WIDTH_VALUE_30,
                numUnit = "mm",
                numRuValue = null
            ),
        )
    )

    values[1].value.addAll(
        listOf(
            NumericAttributeValue(
                attribute = attr,
                compositeAttributeValue = values[1],
                numValue = DIMENS_LENGTH_VALUE_10,
                numUnit = "mm",
                numRuValue = null
            ),
            NumericAttributeValue(
                attribute = attr,
                compositeAttributeValue = values[1],
                numValue = DIMENS_LENGTH_VALUE_20,
                numUnit = "mm",
                numRuValue = null
            ),
            NumericAttributeValue(
                attribute = attr,
                compositeAttributeValue = values[1],
                numValue = DIMENS_LENGTH_VALUE_30,
                numUnit = "mm",
                numRuValue = null
            ),
        )
    )

    values[2].value.addAll(
        listOf(
            NumericAttributeValue(
                attribute = attr,
                compositeAttributeValue = values[2],
                numValue = DIMENS_DEPTH_VALUE_10,
                numUnit = "mm",
                numRuValue = null
            ),
            NumericAttributeValue(
                attribute = attr,
                compositeAttributeValue = values[2],
                numValue = DIMENS_DEPTH_VALUE_20,
                numUnit = "mm",
                numRuValue = null
            ),
            NumericAttributeValue(
                attribute = attr,
                compositeAttributeValue = values[2],
                numValue = DIMENS_DEPTH_VALUE_30,
                numUnit = "mm",
                numRuValue = null
            ),
        )
    )
    attr.compositeAttributeValues.addAll(values)
    return attr
}

fun dimensAttributeCompositeValues(): List<CompositeAttributeValueDto> =
    listOf(
        CompositeAttributeValueDto(
            name = DIMENS_WIDTH_NAME,
            values = listOf(
                NumericAttributeValueDto(
                    numValue = DIMENS_WIDTH_VALUE_10,
                    numRuValue = null,
                    numUnit = "mm"
                ),
                NumericAttributeValueDto(
                    numValue = DIMENS_WIDTH_VALUE_20,
                    numRuValue = null,
                    numUnit = "mm"
                ),
                NumericAttributeValueDto(
                    numValue = DIMENS_WIDTH_VALUE_30,
                    numRuValue = null,
                    numUnit = "mm"
                )
            )
        ),

        CompositeAttributeValueDto(
            name = DIMENS_LENGTH_NAME,
            values = listOf(
                NumericAttributeValueDto(
                    numValue = DIMENS_LENGTH_VALUE_10,
                    numRuValue = null,
                    numUnit = "mm"
                ),
                NumericAttributeValueDto(
                    numValue = DIMENS_LENGTH_VALUE_20,
                    numRuValue = null,
                    numUnit = "mm"
                ),
                NumericAttributeValueDto(
                    numValue = DIMENS_LENGTH_VALUE_30,
                    numRuValue = null,
                    numUnit = "mm"
                )
            )
        ),
        CompositeAttributeValueDto(
            name = DIMENS_DEPTH_NAME,
            values = listOf(
                NumericAttributeValueDto(
                    numValue = DIMENS_DEPTH_VALUE_10,
                    numRuValue = null,
                    numUnit = "mm"
                ),
                NumericAttributeValueDto(
                    numValue = DIMENS_DEPTH_VALUE_20,
                    numRuValue = null,
                    numUnit = "mm"
                ),
                NumericAttributeValueDto(
                    numValue = DIMENS_DEPTH_VALUE_30,
                    numRuValue = null,
                    numUnit = "mm"
                )
            )
        )
    )

fun sizeAttributeDto(isRequired: Boolean) = PlainAttributeDto(
    attributeName = CLOTHES_SIZE_ATTR_NAME,
    shortName = CLOTHES_SIZE_ATTR_SHORT_NAME,
    isFaceted = true,
    isRequired = isRequired,
    availableValues = sizeAttributeStringValues()
)

fun sizeAttributeDomain(): Attribute {
    val attr = Attribute(
        name = CLOTHES_SIZE_ATTR_NAME,
        shortName = CLOTHES_SIZE_ATTR_SHORT_NAME,
        isFaceted = true,
        isComposite = false
    )
    attr.attributeValues.addAll(
        listOf(
            StringAttributeValue(
                attribute = attr,
                stringValue = SIZE_XS_VALUE,
                stringRuValue = SIZE_44_VALUE,
                compositeAttributeValue = null
            ),
            StringAttributeValue(
                attribute = attr,
                stringValue = SIZE_S_VALUE,
                stringRuValue = SIZE_46_VALUE,
                compositeAttributeValue = null
            ),
            StringAttributeValue(
                attribute = attr,
                stringValue = SIZE_M_VALUE,
                stringRuValue = SIZE_48_VALUE,
                compositeAttributeValue = null
            ),
            StringAttributeValue(
                attribute = attr,
                stringValue = SIZE_L_VALUE,
                stringRuValue = SIZE_50_VALUE,
                compositeAttributeValue = null
            ),
            StringAttributeValue(
                attribute = attr,
                stringValue = SIZE_XL_VALUE,
                stringRuValue = SIZE_52_VALUE,
                compositeAttributeValue = null
            ),
            StringAttributeValue(
                attribute = attr,
                stringValue = SIZE_XXL_VALUE,
                stringRuValue = SIZE_54_VALUE,
                compositeAttributeValue = null
            )
        )
    )
    return attr
}

fun sizeAttributeDtoWithNoValues() = PlainAttributeDto(
    attributeName = CLOTHES_SIZE_ATTR_NAME,
    shortName = CLOTHES_SIZE_ATTR_SHORT_NAME,
    isFaceted = true,
    availableValues = listOf()
)

fun sizeAttributeStringValues(): List<AttributeValueDto> =
    listOf(
        StringAttributeValueDto(
            stringValue = SIZE_XS_VALUE,
            stringRuValue = SIZE_44_VALUE
        ),
        StringAttributeValueDto(
            stringValue = SIZE_S_VALUE,
            stringRuValue = SIZE_46_VALUE
        ),
        StringAttributeValueDto(
            stringValue = SIZE_M_VALUE,
            stringRuValue = SIZE_48_VALUE
        ),
        StringAttributeValueDto(
            stringValue = SIZE_L_VALUE,
            stringRuValue = SIZE_50_VALUE
        ),
        StringAttributeValueDto(
            stringValue = SIZE_XL_VALUE,
            stringRuValue = SIZE_52_VALUE
        ),
        StringAttributeValueDto(
            stringValue = SIZE_XXL_VALUE,
            stringRuValue = SIZE_54_VALUE
        ),
    )

fun createSellerRequest() = CreateSellerRequest(
    firstName = SELLER_TEST_NAME,
    lastName = SELLER_TEST_LASTNAME
)

fun sellerDomain() = Seller(
    firstName = SELLER_TEST_NAME,
    lastName = SELLER_TEST_LASTNAME
)

fun createShopRequest(sellerId: String) = CreateShopRequest(
    sellerId,
    name = SHOP_TEST_NAME,
    description = SHOP_TEST_DESCRIPTION
)

fun shopDomain(seller: Seller) = Shop(
    seller = seller,
    name = SHOP_TEST_NAME,
    description = SHOP_TEST_DESCRIPTION
)

fun createTshirtRequest(
    shopId: String,
    blueAttributes: List<AttributeDto>,
    redAttributes: List<AttributeDto>,
    greenAttributes: List<AttributeDto>
) = CreateProductRequest(
    shopId = shopId,
    categoryName = TEST_TOP_LEVEL_CLOTHES_CATEGORY_NAME,
    name = PRODUCT_T_SHIRT_NAME,
    description = PRODUCT_T_SHIRT_DESCRIPTION,
    variants = tShirtVariantDtos(blueAttributes, redAttributes, greenAttributes)
)

fun colorAttributeRequest(): AttributeDto = PlainAttributeDto(
    attributeName = COLOR_ATTR_NAME,
    shortName = COLOR_ATTR_NAME,
    isFaceted = true,
    availableValues = listOf(
        ColorAttributeValueDto(
            colorValue = BLUE,
            colorHex = BLUE_HEX
        ),
        ColorAttributeValueDto(
            colorValue = RED,
            colorHex = RED_HEX
        ),
        ColorAttributeValueDto(
            colorValue = GREEN,
            colorHex = GREEN_HEX
        )
    )
)

fun colorAttributeDomain(): Attribute {
    val attr = Attribute(
        name = COLOR_ATTR_NAME,
        shortName = COLOR_ATTR_NAME,
        isFaceted = true,
        isComposite = false
    )
    attr.attributeValues.addAll(listOf(
        ColorAttributeValue(
            attribute = attr,
            colorValue = BLUE,
            colorHex = BLUE_HEX,
            compositeAttributeValue = null
        ),
        ColorAttributeValue(
            attribute = attr,
            colorValue = RED,
            colorHex = RED_HEX,
            compositeAttributeValue = null
        ),
        ColorAttributeValue(
            attribute = attr,
            colorValue = GREEN,
            colorHex = GREEN_HEX,
            compositeAttributeValue = null
        )
    ))
    return attr
}

fun tShirtVariantDtos(
    blueAttributes: List<AttributeDto>,
    redAttributes: List<AttributeDto>,
    greenAttributes: List<AttributeDto>
): Set<ProductVariantRequestDto> =
    hashSetOf(
        blueTShirtVariantDto(blueAttributes),
        greenTShirtVariantDto(greenAttributes),
        redTShirtVariantDto(redAttributes)
    )

fun blueTShirtVariantDto(
    attributes: List<AttributeDto>
) = ProductVariantRequestDto(
    variantName = T_SHIRT_BLUE_VARIANT_NAME,
    price = BigDecimal.TEN,
    stock = 10,
    images = ImageCollection(
        images = arrayListOf(
            AppImage(
                url = "http://imageurl.com/blue-image-primary.png",
                isPrimary = true
            ),
            AppImage(
                url = "http://imageurl.com/blue-image-secondary.png"
            )
        )
    ),
    attributes = attributes
)

fun redTShirtVariantDto(
    attributes: List<AttributeDto>
) = ProductVariantRequestDto(
    variantName = T_SHIRT_RED_VARIANT_NAME,
    price = BigDecimal.TEN,
    stock = 10,
    images = ImageCollection(
        images = arrayListOf(
            AppImage(
                url = "http://imageurl.com/red-image-primary.png",
                isPrimary = true
            ),
            AppImage(
                url = "http://imageurl.com/red-image-secondary.png"
            )
        )
    ),
    attributes = attributes
)

fun greenTShirtVariantDto(
    attributes: List<AttributeDto>
) = ProductVariantRequestDto(
    variantName = T_SHIRT_GREEN_VARIANT_NAME,
    price = BigDecimal.TEN,
    stock = 10,
    images = ImageCollection(
        images = arrayListOf(
            AppImage(
                url = "http://imageurl.com/green-image-primary.png",
                isPrimary = true
            ),
            AppImage(
                url = "http://imageurl.com/green-image-secondary.png"
            )
        )
    ),
    attributes = attributes
)