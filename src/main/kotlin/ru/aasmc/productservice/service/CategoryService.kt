package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.CategoryResponse
import ru.aasmc.productservice.dto.CreateCategoryRequest
import ru.aasmc.productservice.dto.CreateCategoryResponse

interface CategoryService {

    fun createCategory(dto: CreateCategoryRequest): CreateCategoryResponse

    fun getCategoryById(id: String): CategoryResponse

    fun getAllCategories(): List<CategoryResponse>

}