package ru.aasmc.productservice.storage.model.jsonb_data

import ru.aasmc.productservice.dto.AttributeCollection
import ru.aasmc.productservice.dto.ImageCollection
import ru.aasmc.productservice.dto.SkuCollection
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
    val attributes: AttributeCollection,
    val skuCollection: SkuCollection,
    val images: ImageCollection,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)