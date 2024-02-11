package ru.aasmc.productservice.service.impl

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.AttributeDto
import ru.aasmc.productservice.dto.CategoryResponse
import ru.aasmc.productservice.dto.CreateCategoryRequest
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.mapper.AttributeMappers
import ru.aasmc.productservice.mapper.CategoryMapper
import ru.aasmc.productservice.service.CategoryService
import ru.aasmc.productservice.storage.model.Category
import ru.aasmc.productservice.storage.model.CategoryAttribute
import ru.aasmc.productservice.storage.repository.AttributeRepository
import ru.aasmc.productservice.storage.repository.CategoryRepository
import ru.aasmc.productservice.utils.CryptoTool

@Service
@Transactional
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val mapper: CategoryMapper,
    private val cryptoTool: CryptoTool,
    private val attributeMapper: AttributeMappers,
    private val attributeRepository: AttributeRepository
) : CategoryService {

    override fun createCategory(dto: CreateCategoryRequest): CategoryResponse {
        val category = categoryRepository.save(mapper.toDomain(dto))
        addNewAttributes(dto, category)
        addSelectedAttributes(dto, category)
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
        var attribute = attributeMapper.toDomain(dto)
        attribute = attributeRepository.save(attribute)
        category.categoryAttributes.add(
            CategoryAttribute(
                isRequired = dto.isRequired ?: false,
                category = category,
                attribute = attribute
            )
        )
        log.debug("Successfully added attribute to category")
        categoryRepository.save(category)
        return mapper.toCategoryResponse(category)
    }

    private fun addSelectedAttributes(
        dto: CreateCategoryRequest,
        category: Category
    ) {
        val attrToIsRequired = dto.selectedAttributeIds.map { attr ->
            attributeRepository.findById(cryptoTool.idOf(attr.id))
                .orElseThrow {
                    val msg = "Attribute with id=$attr not found"
                    ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
                } to attr.isRequired
        }

        val categoryAttributes = attrToIsRequired.map { (attr, isRequired) ->
            CategoryAttribute(isRequired, category, attr)
        }
        category.categoryAttributes.addAll(categoryAttributes)
    }

    private fun addNewAttributes(
        dto: CreateCategoryRequest,
        category: Category
    ) {
        val newAttributes = dto.attributesToCreate
            .map { attrDto ->
                var attr = attributeMapper.toDomain(attrDto)
                attr = attributeRepository.save(attr)
                CategoryAttribute(
                    isRequired = attrDto.isRequired ?: false,
                    category = category,
                    attribute = attr
                )
            }

        category.categoryAttributes.addAll(newAttributes)
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