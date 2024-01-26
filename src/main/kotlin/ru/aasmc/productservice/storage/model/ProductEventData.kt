package ru.aasmc.productservice.storage.model

import ru.aasmc.productservice.dto.AttributeDto
import ru.aasmc.productservice.dto.ImageCollection
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductEventData(
    val shopName: String,
    /**
     * Hierarchy of categories, delimited by dot(.).
     */
    val categoryString: String,
    val productName: String,
    val productDescription: String,
    val variants: List<ProductEventVariant>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class ProductEventVariant(
    val variantId: Long,
    val price: BigDecimal,
    val stock: Int,
    val attributes: List<AttributeDto>,
    val images: ImageCollection,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)