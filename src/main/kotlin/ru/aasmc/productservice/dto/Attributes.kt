package ru.aasmc.productservice.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalDateTime

enum class AttributeType(
    @field:JsonValue
    val value: String
) {
    STRING_ATTR("string"),
    NUMERIC_ATTR("numeric"),
    COLOR_ATTR("color"),
    COMPOSITE_ATTR("composite")
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(*arrayOf(
    JsonSubTypes.Type(
        value = StringAttributeDto::class,
        name = "string"
    ),
    JsonSubTypes.Type(
        value = NumericAttributeDto::class,
        name = "numeric"
    ),
    JsonSubTypes.Type(
        value = ColorAttributeDto::class,
        name = "color"
    ),
    JsonSubTypes.Type(
        value = CompositeAttributeDto::class,
        name = "composite"
    )
))
sealed class AttributeDto(
    open val id: String?,
    open val attributeName: String,
    open val shortName: String,
    open val isFaceted: Boolean,
    open val type: AttributeType,
    open val createdAt: LocalDateTime?,
    open val isRequired: Boolean? = null
)

data class StringAttributeDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val type: AttributeType = AttributeType.STRING_ATTR,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val isRequired: Boolean? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val createdAt: LocalDateTime? = null,
    val availableValues: List<StringAttributeValueDto>,
) : AttributeDto(id, attributeName, shortName, isFaceted, type, createdAt, isRequired)

data class NumericAttributeDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val type: AttributeType = AttributeType.NUMERIC_ATTR,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val isRequired: Boolean? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val createdAt: LocalDateTime? = null,
    val availableValues: List<NumericAttributeValueDto>,
) : AttributeDto(id, attributeName, shortName, isFaceted, type, createdAt, isRequired)

data class ColorAttributeDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val type: AttributeType = AttributeType.COLOR_ATTR,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val isRequired: Boolean? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val createdAt: LocalDateTime? = null,
    val availableValues: List<ColorAttributeValueDto>,
) : AttributeDto(id, attributeName, shortName, isFaceted, type, createdAt, isRequired)

data class CompositeAttributeDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val type: AttributeType = AttributeType.COMPOSITE_ATTR,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val isRequired: Boolean? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val createdAt: LocalDateTime? = null,
    val subAttributes: List<AttributeDto>
): AttributeDto(id, attributeName, shortName, isFaceted, type, createdAt, isRequired)

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
@JsonSubTypes(
    *arrayOf(
        JsonSubTypes.Type(
            value = StringAttributeValueDto::class,
            name = "string_type"
        ),
        JsonSubTypes.Type(
            value = NumericAttributeValueDto::class,
            name = "numeric_type"
        ),
        JsonSubTypes.Type(
            value = ColorAttributeValueDto::class,
            name = "color_type"
        )
    )
)
sealed class AttributeValueDto(
    open val type: AttributeValueType
)

data class StringAttributeValueDto(
    val stringValue: String,
    val stringRuValue: String?,
    override val type: AttributeValueType = AttributeValueType.STRING_TYPE
) : AttributeValueDto(type)

data class NumericAttributeValueDto(
    val numValue: Double,
    val numRuValue: Double?,
    val numUnit: String,
    override val type: AttributeValueType = AttributeValueType.NUMERIC_TYPE
) : AttributeValueDto(type)

data class ColorAttributeValueDto(
    val colorValue: String,
    val colorHex: String,
    override val type: AttributeValueType = AttributeValueType.COLOR_TYPE
) : AttributeValueDto(type)


