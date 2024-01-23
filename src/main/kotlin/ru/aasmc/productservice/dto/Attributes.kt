package ru.aasmc.productservice.dto

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue

enum class AttributeType(
    @field:JsonValue
    val value: String
) {
    PLAIN("plain"),
    COMPOSITE("composite")
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
abstract class CreateAttributeRequest(
    open val attributeName: String,
    open val shortName: String,
    open val isFaceted: Boolean,
    open val isRequired: Boolean,
    open val type: AttributeType
)

data class CreatePlainAttributeRequest(
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val isRequired: Boolean,
    override val type: AttributeType = AttributeType.PLAIN,
    val values: List<AttributeValueDto>
) : CreateAttributeRequest(attributeName, shortName, isFaceted, isRequired, type)

data class CreateCompositeAttributeRequest(
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val isRequired: Boolean,
    override val type: AttributeType = AttributeType.PLAIN,
    val values: List<CompositeValueDto>
): CreateAttributeRequest(attributeName, shortName, isFaceted, isRequired, type)

enum class AttributeValueType(
    @field:JsonValue
    val value: String
) {
    STRING_TYPE("string_type"),
    NUMERIC_TYPE("numeric_type"),
    COLOR_TYPE("color_type")
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    visible = true,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type"
)
abstract class AttributeValueDto(
    open val type: AttributeValueType
)

data class StringAttributeValueDto(
    val stringValue: String,
    val stringRuValue: String,
    override val type: AttributeValueType = AttributeValueType.STRING_TYPE
) : AttributeValueDto(type)

data class NumericAttributeValueDto(
    val numValue: Number,
    val numRuValue: Number?,
    val numUnit: String,
    override val type: AttributeValueType = AttributeValueType.NUMERIC_TYPE
): AttributeValueDto(type)

data class ColorAttributeValueDto(
    val colorValue: String,
    val colorHex: String,
    override val type: AttributeValueType = AttributeValueType.COLOR_TYPE
): AttributeValueDto(type)

data class CompositeValueDto(
    val name: String,
    val value: AttributeValueDto
)

data class AttributeResponse(
    val id: String,
    val attributeName: String,
    val attributeValues: List<AttributeValueResponseDto>
)

data class AttributeValueResponseDto(
    val id: String,
    val isComposite: Boolean,
    val attributeValueName: String,
    val components: List<AttributeValueComponentResponseDto>
)

data class AttributeValueComponentResponseDto(
    val id: String,
    val componentName: String,
    val componentValue: String
)