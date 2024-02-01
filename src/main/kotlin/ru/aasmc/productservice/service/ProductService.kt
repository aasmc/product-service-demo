package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.*

interface ProductService {

    fun createProduct(dto: CreateProductRequest): ProductResponse

    fun getProductById(productId: String): ProductResponse

    fun getProductVariants(productId: String): List<ProductVariantResponse>

    fun addProductVariant(productId: String, dto: ProductVariantRequestDto): ProductVariantResponse

    fun deleteProductVariant(productId: String, variantId: String)

    fun deleteProduct(productId: String)

    fun updateProduct(
        productId: String,
        update: ProductUpdateDto
    ): ProductResponse


}