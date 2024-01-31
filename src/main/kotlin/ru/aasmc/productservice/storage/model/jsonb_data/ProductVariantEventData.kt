package ru.aasmc.productservice.storage.model.jsonb_data

import ru.aasmc.productservice.dto.AttributeCollection
import ru.aasmc.productservice.dto.ImageCollection
import ru.aasmc.productservice.dto.SkuCollection
import java.math.BigDecimal

data class ProductVariantEventData(
    val variantId: Long,
    val productId: Long,
    val variantName: String,
    val price: BigDecimal,
    val attributes: AttributeCollection,
    val images: ImageCollection,
    val skuCollection: SkuCollection
)
