package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.*

interface ProductService {

    fun createProduct(dto: CreateProductRequest): ProductResponse

    fun getProductById(id: String): ProductResponse

    fun getProductVariants(id: String): List<ProductVariantResponse>

    fun addProductVariant(productId: String, dto: ProductVariantRequestDto): ProductVariantResponse

    fun deleteProductVariant(productId: String, variantId: String)

    fun deleteProduct(productId: String)

    fun updateName(productId: String, newName: String): ProductResponse

    fun updateDescription(productId: String, newDescription: String): ProductResponse

}