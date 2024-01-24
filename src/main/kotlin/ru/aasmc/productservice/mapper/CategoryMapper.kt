package ru.aasmc.productservice.mapper

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.CategoryResponse
import ru.aasmc.productservice.dto.CreateCategoryRequest
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.storage.model.Category
import ru.aasmc.productservice.storage.repository.CategoryAttributeRepository
import ru.aasmc.productservice.storage.repository.CategoryRepository
import ru.aasmc.productservice.utils.CryptoTool

@Component
class CategoryMapper(
    private val cryptoTool: CryptoTool,
    private val categoryRepository: CategoryRepository,
    private val categoryAttributeRepository: CategoryAttributeRepository
) {

    fun toDomain(dto: CreateCategoryRequest): Category {
        val parent = dto.parentId
            ?.let { parentId ->
                categoryRepository.findById(cryptoTool.idOf(parentId))
            }?.orElseThrow {
                val msg = "Parent category with ID=${dto.parentId} not found."
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }

        val categoryAttributes = dto.attributeNames.map { attributeName ->
            categoryAttributeRepository.findByAttribute_Name(attributeName)
                .orElseThrow {
                    val msg = "CategoryAttribute with name=$attributeName not found"
                    ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
                }
        }.toHashSet()

        val newCategory = Category(
            name = dto.name,
            parent = parent,
            categoryAttributes = categoryAttributes
        )
        parent?.subCategories?.add(newCategory)
        return newCategory
    }

    fun toCreateCategoryResponse(domain: Category): CategoryResponse =
        CategoryResponse(cryptoTool.hashOf(domain.id!!))

    fun toCategoryResponse(domain: Category): CategoryResponse =
        CategoryResponse(
            categoryId = cryptoTool.hashOf(domain.id!!),
            parentCategoryId = domain.parent?.id?.let { cryptoTool.hashOf(it) },
            attributeNames = domain.categoryAttributes.map { it.attribute.name }
        )

    fun toCategoryResponseList(categories: List<Category>): List<CategoryResponse> =
        categories.map(::toCategoryResponse)

}