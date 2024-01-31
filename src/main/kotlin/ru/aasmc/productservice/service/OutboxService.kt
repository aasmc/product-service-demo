package ru.aasmc.productservice.service

import ru.aasmc.productservice.storage.model.EventType
import ru.aasmc.productservice.storage.model.Product
import ru.aasmc.productservice.storage.model.ProductVariant

interface OutboxService {

    fun addProductEvent(productId: Long, product: Product?, eventType: EventType)

    fun addProductVariantEvent(
        productId: Long,
        productVariantId: Long,
        eventType: EventType,
        productVariant: ProductVariant? = null
    )

}