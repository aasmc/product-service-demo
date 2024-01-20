package ru.aasmc.productservice.dto

import java.math.BigDecimal

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

data class CreateProductResponse(
    val productId: String,
    val variants: List<ProductVariantShortResponseDto>
)

data class ProductVariantShortResponseDto(
    val variantName: String,
    val id: String
)

data class ProductResponse(
    val productId: String,
    val shopId: String,
    val categoryName: String,
    val name: String,
    val description: String,
    val variants: List<ProductVariantFullResponseDto>
)

data class ProductVariantFullResponseDto(
    val productId: String,
    val variantId: String,
    val variantName: String,
    val price: BigDecimal,
    val stock: Int,
    val attributes: Map<String, Any>,
    val images: ImageCollection,
)