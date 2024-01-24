package ru.aasmc.productservice.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.aasmc.productservice.dto.CategoryResponse
import ru.aasmc.productservice.dto.CreateCategoryRequest
import ru.aasmc.productservice.service.CategoryService

@RestController
@RequestMapping("/v1/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCategory(@RequestBody dto: CreateCategoryRequest): CategoryResponse {
        log.info("Received POST request to create category: {}", dto)
        return categoryService.createCategory(dto)
    }

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable("id") id: String): CategoryResponse {
        log.info("Received request to GET category by ID={}", id)
        return categoryService.getCategoryById(id)
    }

    @GetMapping
    fun getAllCategories(): List<CategoryResponse> {
        log.info("Received request to GET all categories")
        return categoryService.getAllCategories()
    }

    companion object {
        private val log = LoggerFactory.getLogger(CategoryController::class.java)
    }
}