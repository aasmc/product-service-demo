package ru.aasmc.productservice.testdata

import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.storage.model.*

fun topLevelCategoryRequest(): CreateCategoryRequest =
    CreateCategoryRequest(
        name = TEST_TOP_LEVEL_CATEGORY_NAME
    )

fun topLevelCategoryRequestWithSelectedAttributes(
    ids: Set<SelectedAttribute>
) = CreateCategoryRequest(
    name = TEST_TOP_LEVEL_CATEGORY_NAME,
    selectedAttributeIds = ids
)

fun subCategoryRequestWithAttributesToCreate(parentId: String) = CreateCategoryRequest(
    name = TEST_SUBCATEGORY_NAME,
    parentId = parentId,
    attributesToCreate = categoryAttributesToCreate()
)

fun topLevelCategoryDomain(): Category =
    Category(
        name = TEST_TOP_LEVEL_CATEGORY_NAME
    )


fun categoryAttributesToCreate(): List<AttributeDto> =
    listOf(
        sizeAttributeDto(true),
        dimensionsAttributeDto(true)
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