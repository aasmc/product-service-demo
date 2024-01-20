package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.*

interface ProductService {

    fun createProduct(dto: CreateProductRequest): CreateProductResponse

    fun getProductById(id: String): ProductResponse

    fun getProductVariants(id: String): List<ProductVariantFullResponseDto>

    fun addProductVariant(productId: String, dto: ProductVariantRequestDto): ProductVariantFullResponseDto

    fun deleteProductVariant(productId: String, variantId: String)

    fun deleteProduct(productId: String)

}