package ru.aasmc.productservice.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateProductRequest(
    val shopId: String,
    val categoryName: String,
    val name: String,
    val description: String,
    val variants: Set<ProductVariantRequestDto>
)

data class ProductVariantRequestDto(
    val variantName: String,
    val price: BigDecimal,
    val stock: Int,
    val attributes: MutableMap<String, Any>,
    val images: ImageCollection
)

data class ProductResponse(
    val productId: String,
    val shopId: String,
    val categoryName: String,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
    val variants: List<ProductVariantResponse>
)

data class ProductVariantResponse(
    val productId: String,
    val variantId: String,
    val variantName: String,
    val price: BigDecimal,
    val stock: Int,
    val attributes: Map<String, Any>,
    val images: ImageCollection,
    val createdAt: LocalDateTime
)