package ru.aasmc.productservice.mapper

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.CategoryResponse
import ru.aasmc.productservice.dto.CreateCategoryRequest
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.storage.model.Category
import ru.aasmc.productservice.storage.repository.CategoryRepository
import ru.aasmc.productservice.utils.CryptoTool

@Component
class CategoryMapper(
    private val cryptoTool: CryptoTool,
    private val categoryRepository: CategoryRepository,
    private val attributeMapper: AttributeMappers
) {

    fun toDomain(dto: CreateCategoryRequest): Category {
        val parent = dto.parentId
            ?.let { parentId ->
                categoryRepository.findById(cryptoTool.idOf(parentId))
            }?.orElseThrow {
                val msg = "Parent category with ID=${dto.parentId} not found."
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }

        val newCategory = Category(
            name = dto.name,
            parent = parent,
        )
        parent?.subCategories?.add(newCategory)
        return newCategory
    }

    fun toCategoryResponse(domain: Category): CategoryResponse =
        CategoryResponse(
            categoryId = cryptoTool.hashOf(domain.id!!),
            name = domain.name,
            parentId = domain.parent?.id?.let { cryptoTool.hashOf(it) },
            subcategoryNames = domain.subCategories.map { it.name },
            attributes = domain.categoryAttributes.map { categoryAttribute ->
                attributeMapper.toDto(categoryAttribute.attribute, categoryAttribute.isRequired)
            },
            createdAt = domain.createdAt!!
        )

    fun toCategoryResponseList(categories: List<Category>): List<CategoryResponse> =
        categories.map(::toCategoryResponse)

}