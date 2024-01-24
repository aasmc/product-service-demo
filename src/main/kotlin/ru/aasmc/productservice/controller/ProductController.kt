package ru.aasmc.productservice.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.service.ProductService

@RestController
@RequestMapping("/v1/products")
class ProductController(
    private val productService: ProductService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@RequestBody dto: CreateProductRequest): ProductResponse {
        log.info("Received POST request to create product: {}", dto)
        return productService.createProduct(dto)
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable("id") id: String): ProductResponse {
        log.info("Received request to GET product by ID={}", id)
        return productService.getProductById(id)
    }

    @GetMapping("/variant/{id}")
    fun getProductVariants(@PathVariable("id") id: String): List<ProductVariantResponse> {
        log.info("Received request to GET variants of product with ID={}", id)
        return productService.getProductVariants(id)
    }

    @PutMapping("/{productId}/variant")
    fun addProductVariant(@PathVariable("productId") productId: String, dto: ProductVariantRequestDto): ProductVariantResponse {
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