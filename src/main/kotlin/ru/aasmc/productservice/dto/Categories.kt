package ru.aasmc.productservice.dto

data class CreateCategoryRequest(
    val name: String,
    val attributeNames: Set<String>,
    val parentId: String? = null,
)

data class CreateCategoryResponse(
    val categoryId: String
)

data class CategoryResponse(
    val categoryId: String,
    val parentCategoryId: String?,
    val attributeNames: List<String>
)