package ru.aasmc.productservice.service.impl

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.AttributeDto
import ru.aasmc.productservice.dto.CategoryResponse
import ru.aasmc.productservice.dto.CreateCategoryRequest
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.mapper.AttributeMapper
import ru.aasmc.productservice.mapper.CategoryMapper
import ru.aasmc.productservice.service.CategoryService
import ru.aasmc.productservice.storage.model.Category
import ru.aasmc.productservice.storage.model.CategoryAttribute
import ru.aasmc.productservice.storage.repository.CategoryRepository
import ru.aasmc.productservice.utils.CryptoTool

@Service
@Transactional
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val mapper: CategoryMapper,
    private val cryptoTool: CryptoTool,
    private val attributeMapper: AttributeMapper
) : CategoryService {

    override fun createCategory(dto: CreateCategoryRequest): CategoryResponse {
        val category = categoryRepository.save(mapper.toDomain(dto))
        val newAttributes = dto.attributesToCreate
            .map { attrDto ->
                val attr = attributeMapper.toDomain(attrDto)
                CategoryAttribute(
                    isRequired = attrDto.isRequired,
                    category = category,
                    attribute = attr
                )
            }

        category.categoryAttributes.addAll(newAttributes)
        log.debug("Successfully saved category to DB. {}", category)
        return mapper.toCategoryResponse(category)
    }

    override fun getCategoryById(id: String): CategoryResponse {
        val category = getCategoryOrThrow(id)
        log.debug("Found category. {}", category)
        return mapper.toCategoryResponse(category)
    }

    override fun getAllCategories(): List<CategoryResponse> {
        return mapper.toCategoryResponseList(categoryRepository.findAll())
    }

    override fun addAttributeToCategory(dto: AttributeDto, categoryId: String): CategoryResponse {
        val category = getCategoryOrThrow(categoryId)
        val attribute = attributeMapper.toDomain(dto)
        category.categoryAttributes.add(
            CategoryAttribute(
                isRequired = dto.isRequired,
                category = category,
                attribute = attribute
            )
        )
        log.debug("Successfully added attribute to category")
        categoryRepository.save(category)
        return mapper.toCategoryResponse(category)
    }

    private fun getCategoryOrThrow(id: String): Category = categoryRepository.findById(cryptoTool.idOf(id))
        .orElseThrow {
            val msg = "Category with ID=$id not found"
            ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
        }

    companion object {
        private val log = LoggerFactory.getLogger(CategoryServiceImpl::class.java)
    }
}