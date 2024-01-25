package ru.aasmc.productservice.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalDateTime

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
sealed class AttributeDto(
    open val id: String?,
    open val attributeName: String,
    open val shortName: String,
    open val isFaceted: Boolean,
    open val type: AttributeType,
    open val createdAt: LocalDateTime?,
    open val isRequired: Boolean
)

data class PlainAttributeDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val type: AttributeType = AttributeType.PLAIN,
    override val isRequired: Boolean = false,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val createdAt: LocalDateTime? = null,
    val availableValues: List<AttributeValueDto>,
) : AttributeDto(id, attributeName, shortName, isFaceted, type, createdAt, isRequired)

data class CompositeAttributeDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val type: AttributeType = AttributeType.PLAIN,
    override val isRequired: Boolean = false,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val createdAt: LocalDateTime? = null,
    val availableValues: List<CompositeAttributeValueDto>
) : AttributeDto(id, attributeName, shortName, isFaceted, type, createdAt, isRequired)

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
sealed class AttributeValueDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    open val id: String?,
    open val type: AttributeValueType
)

data class StringAttributeValueDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    val stringValue: String,
    val stringRuValue: String?,
    override val type: AttributeValueType = AttributeValueType.STRING_TYPE
) : AttributeValueDto(id, type)

data class NumericAttributeValueDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    val numValue: Number,
    val numRuValue: Number?,
    val numUnit: String,
    override val type: AttributeValueType = AttributeValueType.NUMERIC_TYPE
) : AttributeValueDto(id, type)

data class ColorAttributeValueDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    val colorValue: String,
    val colorHex: String,
    override val type: AttributeValueType = AttributeValueType.COLOR_TYPE
) : AttributeValueDto(id, type)

data class CompositeAttributeValueDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val id: String? = null,
    val name: String,
    val value: AttributeValueDto
)


