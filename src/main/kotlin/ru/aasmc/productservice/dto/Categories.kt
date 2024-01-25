package ru.aasmc.productservice.dto

import java.time.LocalDateTime

data class CreateCategoryRequest(
    val name: String,
    val selectedAttributeIds: Set<String>,
    val attributesToCreate: List<AttributeDto>,
    val parentId: String? = null,
)

data class CategoryResponse(
    val categoryId: String,
    val parentId: String?,
    val subcategoryNames: List<String>,
    val createdAt: LocalDateTime,
    val attributes: List<AttributeDto>
)
