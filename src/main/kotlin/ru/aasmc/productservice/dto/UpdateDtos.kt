package ru.aasmc.productservice.dto

import java.math.BigDecimal

data class UpdateSkuStockRequest(
    val productVariantId: String,
    val sku: String,
    val newStock: Int
)

data class UpdateSkuStockResponse(
    val sku: String,
    val newStock: Int
)

data class UpdateSkuPriceRequest(
    val productVariantId: String,
    val sku: String,
    val newPrice: BigDecimal
)

data class UpdateSkuPriceResponse(
    val sku: String,
    val newPrice: BigDecimal
)

data class UpdateProductVariantPriceRequest(
    val productVariantId: String,
    val newPrice: BigDecimal
)

data class ProductUpdateDto(
    val newName: String?,
    val newDescription: String?
)