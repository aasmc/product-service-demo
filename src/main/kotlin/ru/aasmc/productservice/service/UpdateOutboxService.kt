package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.AttributeCollection
import ru.aasmc.productservice.dto.ImageCollection
import java.math.BigDecimal

interface UpdateOutboxService {

    fun sendUpdatePVPhotosEvent(
        variantId: Long,
        newPhotos: ImageCollection
    )

    fun sendUpdatePVPriceEvent(
        variantId: Long,
        prevPrice: BigDecimal,
        newPrice: BigDecimal
    )

    fun sendUpdatePVAttributesEvent(
        variantId: Long,
        newAttributes: AttributeCollection
    )

    fun sendUpdateProductNameEvent(
        productId: Long,
        prevName: String,
        newName: String
    )

    fun sendUpdateProductDescriptionEvent(
        productId: Long,
        newDescription: String
    )

    fun sendUpdateSkuStockEvent(
        variantId: Long,
        sku: String,
        prevStock: Int,
        newStock: Int
    )

    fun sendUpdateSkuPriceEvent(
        variantId: Long,
        sku: String,
        prevPrice: BigDecimal,
        newPrice: BigDecimal
    )

}