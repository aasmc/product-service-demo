package ru.aasmc.productservice.testdata

import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.storage.model.Category

fun topLevelCategoryRequest(): CreateCategoryRequest =
    CreateCategoryRequest(
        name = TEST_TOP_LEVEL_CATEGORY_NAME
    )

fun subCategoryRequestWithAttributesToCreate(parentId: String) = CreateCategoryRequest(
    name = TEST_SUBCATEGORY_NAME,
    parentId = parentId,
    attributesToCreate = attributesToCreate()
)

fun topLevelCategoryDomain(): Category =
    Category(
        name = TEST_TOP_LEVEL_CATEGORY_NAME
    )


fun attributesToCreate(): List<AttributeDto> =
    listOf(
        sizeAttributeDto(),
        dimensionsAttributeDto()
    )

fun dimensionsAttributeDto() = CompositeAttributeDto(
    attributeName = DIMENS_ATTR_NAME,
    shortName = DIMENS_ATTR_SHORT_NAME,
    isFaceted = true,
    isRequired = true,
    availableValues = dimensAttributeCompositeValues()
)

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

fun sizeAttributeDto() = PlainAttributeDto(
    attributeName = CLOTHES_SIZE_ATTR_NAME,
    shortName = CLOTHES_SIZE_ATTR_SHORT_NAME,
    isFaceted = true,
    isRequired = true,
    availableValues = sizeAttributeStringValues()
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