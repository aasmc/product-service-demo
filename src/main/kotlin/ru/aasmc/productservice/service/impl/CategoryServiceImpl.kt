package ru.aasmc.productservice.service.impl

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.CategoryResponse
import ru.aasmc.productservice.dto.CreateCategoryRequest
import ru.aasmc.productservice.dto.CreateCategoryResponse
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.mapper.CategoryMapper
import ru.aasmc.productservice.service.CategoryService
import ru.aasmc.productservice.storage.repository.CategoryRepository
import ru.aasmc.productservice.utils.CryptoTool

@Service
@Transactional
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val mapper: CategoryMapper,
    private val cryptoTool: CryptoTool
): CategoryService {

    override fun createCategory(dto: CreateCategoryRequest): CreateCategoryResponse {
        val category = categoryRepository.save(mapper.toDomain(dto))
        log.debug("Successfully saved category to DB. {}", category)
        return mapper.toCreateCategoryResponse(category)
    }

    override fun getCategoryById(id: String): CategoryResponse {
        val category = categoryRepository.findById(cryptoTool.idOf(id))
            .orElseThrow {
                val msg = "Category with ID=$id not found"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        log.debug("Found category. {}", category)
        return mapper.toCategoryResponse(category)
    }

    override fun getAllCategories(): List<CategoryResponse> {
        return mapper.toCategoryResponseList(categoryRepository.findAll())
    }

    companion object {
        private val log = LoggerFactory.getLogger(CategoryServiceImpl::class.java)
    }
}