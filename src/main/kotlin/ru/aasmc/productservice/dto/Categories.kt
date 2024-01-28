package ru.aasmc.productservice.dto

import java.time.LocalDateTime

data class CreateCategoryRequest(
    val name: String,
    val selectedAttributeIds: Set<SelectedAttribute> = hashSetOf(),
    val attributesToCreate: List<AttributeDto> = listOf(),
    val parentId: String? = null,
)

data class SelectedAttribute(
    val id: String,
    val isRequired: Boolean
)

data class CategoryResponse(
    val categoryId: String,
    val name: String,
    val parentId: String?,
    val subcategoryNames: List<String>,
    val createdAt: LocalDateTime,
    val attributes: List<AttributeDto>
)
