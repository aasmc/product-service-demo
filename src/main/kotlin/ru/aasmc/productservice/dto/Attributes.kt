package ru.aasmc.productservice.dto


data class CreateAttributeRequest(
    val attributeName: String,
    val attributeValues: Set<AttributeValueDto>
)

data class AttributeValueDto(
    val isComposite: Boolean,
    val attributeValueName: String,
    val components: Set<AttributeValueComponentDto>
)

data class AttributeValueComponentDto(
    val componentName: String,
    val componentValue: String
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