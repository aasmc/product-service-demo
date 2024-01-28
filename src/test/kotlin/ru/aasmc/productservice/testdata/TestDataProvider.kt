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
    subAttributes = dimensAttributeCompositeValues()
)

fun dimensAttributeDomain(): CompositeAttribute {
    val attr = CompositeAttribute(
        name = DIMENS_ATTR_NAME,
        shortName = DIMENS_ATTR_SHORT_NAME,
        isFaceted = true,
        subAttributes = hashSetOf(
            NumericAttribute(
                name = DIMENS_WIDTH_NAME,
                shortName = DIMENS_WIDTH_NAME,
                isFaceted = true,
                numericValues = arrayListOf(
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
            NumericAttribute(
                name = DIMENS_LENGTH_NAME,
                shortName = DIMENS_LENGTH_NAME,
                isFaceted = true,
                numericValues = arrayListOf(
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
            NumericAttribute(
                name = DIMENS_DEPTH_NAME,
                shortName = DIMENS_DEPTH_NAME,
                isFaceted = true,
                numericValues = arrayListOf(
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
            ),
        )
    )
    return attr
}

fun dimensAttributeCompositeValues(): List<AttributeDto> =
    listOf(
        NumericAttributeDto(
            attributeName = DIMENS_WIDTH_NAME,
            shortName = DIMENS_WIDTH_NAME,
            isFaceted = true,
            availableValues = listOf(
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
        NumericAttributeDto(
            attributeName = DIMENS_LENGTH_NAME,
            shortName = DIMENS_LENGTH_NAME,
            isFaceted = true,
            availableValues = listOf(
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
        NumericAttributeDto(
            attributeName = DIMENS_DEPTH_NAME,
            shortName = DIMENS_DEPTH_NAME,
            isFaceted = true,
            availableValues = listOf(
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

fun sizeAttributeDto(isRequired: Boolean) = StringAttributeDto(
    attributeName = CLOTHES_SIZE_ATTR_NAME,
    shortName = CLOTHES_SIZE_ATTR_SHORT_NAME,
    isFaceted = true,
    isRequired = isRequired,
    availableValues = sizeAttributeStringValues()
)

fun sizeAttributeDomain(): StringAttribute {
    val attr = StringAttribute(
        name = CLOTHES_SIZE_ATTR_NAME,
        shortName = CLOTHES_SIZE_ATTR_SHORT_NAME,
        isFaceted = true,
    )
    attr.stringValues.addAll(
        listOf(
            StringAttributeValueDto(
                stringValue = SIZE_XS_VALUE,
                stringRuValue = SIZE_44_VALUE,
            ),
            StringAttributeValueDto(
                stringValue = SIZE_S_VALUE,
                stringRuValue = SIZE_46_VALUE,
            ),
            StringAttributeValueDto(
                stringValue = SIZE_M_VALUE,
                stringRuValue = SIZE_48_VALUE,
            ),
            StringAttributeValueDto(
                stringValue = SIZE_L_VALUE,
                stringRuValue = SIZE_50_VALUE,
            ),
            StringAttributeValueDto(
                stringValue = SIZE_XL_VALUE,
                stringRuValue = SIZE_52_VALUE,
            ),
            StringAttributeValueDto(
                stringValue = SIZE_XXL_VALUE,
                stringRuValue = SIZE_54_VALUE,
            )
        )
    )
    return attr
}

fun sizeAttributeDtoWithNoValues() = StringAttributeDto(
    attributeName = CLOTHES_SIZE_ATTR_NAME,
    shortName = CLOTHES_SIZE_ATTR_SHORT_NAME,
    isFaceted = true,
    availableValues = listOf()
)

fun sizeAttributeStringValues(): List<StringAttributeValueDto> =
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
    blueAttributes: MutableList<AttributeDto>,
    redAttributes: MutableList<AttributeDto>,
    greenAttributes: MutableList<AttributeDto>
) = CreateProductRequest(
    shopId = shopId,
    categoryName = TEST_TOP_LEVEL_CLOTHES_CATEGORY_NAME,
    name = PRODUCT_T_SHIRT_NAME,
    description = PRODUCT_T_SHIRT_DESCRIPTION,
    variants = tShirtVariantDtos(blueAttributes, redAttributes, greenAttributes)
)

fun colorAttributeRequest() = ColorAttributeDto(
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
    val attr = ColorAttribute(
        name = COLOR_ATTR_NAME,
        shortName = COLOR_ATTR_NAME,
        isFaceted = true,
    )
    attr.colorValues.addAll(listOf(
        ColorAttributeValueDto(
            colorValue = BLUE,
            colorHex = BLUE_HEX,
        ),
        ColorAttributeValueDto(
            colorValue = RED,
            colorHex = RED_HEX,
        ),
        ColorAttributeValueDto(
            colorValue = GREEN,
            colorHex = GREEN_HEX,
        )
    ))
    return attr
}

fun tShirtVariantDtos(
    blueAttributes: MutableList<AttributeDto>,
    redAttributes: MutableList<AttributeDto>,
    greenAttributes: MutableList<AttributeDto>
): Set<ProductVariantRequestDto> =
    hashSetOf(
        blueTShirtVariantDto(blueAttributes),
        greenTShirtVariantDto(greenAttributes),
        redTShirtVariantDto(redAttributes)
    )

fun blueTShirtVariantDto(
    attributes: MutableList<AttributeDto>
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
    attributeCollection = AttributeCollection(attributes)
)

fun redTShirtVariantDto(
    attributes: MutableList<AttributeDto>
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
    attributeCollection = AttributeCollection(attributes)
)

fun greenTShirtVariantDto(
    attributes: MutableList<AttributeDto>
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
    attributeCollection = AttributeCollection(attributes)
)