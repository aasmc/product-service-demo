package ru.aasmc.productservice.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.service.ProductService

@RestController
@RequestMapping("/v1/products")
class ProductController(
    private val productService: ProductService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@RequestBody dto: CreateProductRequest): CreateProductResponse {
        log.info("Received POST request to create product: {}", dto)
        return productService.createProduct(dto)
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable("id") id: String): ProductResponse {
        log.info("Received request to GET product by ID={}", id)
        return productService.getProductById(id)
    }

    @GetMapping("/variant/{id}")
    fun getProductVariants(@PathVariable("id") id: String): List<ProductVariantFullResponseDto> {
        log.info("Received request to GET variants of product with ID={}", id)
        return productService.getProductVariants(id)
    }

    @PutMapping("/{productId}/variant")
    fun addProductVariant(@PathVariable("productId") productId: String, dto: ProductVariantRequestDto): ProductVariantFullResponseDto {
        log.info("Received PUT request to add product variant: {}", dto)
        return productService.addProductVariant(productId, dto)
    }

    @DeleteMapping("/{productId}/variant/{variantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProductVariant(
        @PathVariable("productId") productId: String,
        @PathVariable("variantId") variantId: String
    ) {
        log.info("Received request to DELETE variant with ID={} of product with ID={}", variantId, productId)
        productService.deleteProductVariant(productId, variantId)
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProduct(@PathVariable("productId") productId: String) {
        log.info("Received request to DELETE product with ID={}", productId)
        productService.deleteProduct(productId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProductController::class.java)
    }
}