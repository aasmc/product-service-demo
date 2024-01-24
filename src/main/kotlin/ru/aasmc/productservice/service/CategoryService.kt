package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.CategoryResponse
import ru.aasmc.productservice.dto.CreateCategoryRequest

interface CategoryService {

    fun createCategory(dto: CreateCategoryRequest): CategoryResponse

    fun getCategoryById(id: String): CategoryResponse

    fun getAllCategories(): List<CategoryResponse>

}