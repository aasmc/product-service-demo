package ru.aasmc.productservice.storage.model

import ru.aasmc.productservice.dto.ImageCollection
import java.math.BigDecimal

data class ProductEventData(
    val shopName: String,
    /**
     * Hierarchy of categories, delimited by dot(.).
     */
    val categoryString: String,
    val productName: String,
    val productDescription: String,
    val variants: List<ProductEventVariant>
)

data class ProductEventVariant(
    val variantId: Long,
    val price: BigDecimal,
    val stock: Int,
    val attributes: Map<String, Any>,
    val images: ImageCollection
)