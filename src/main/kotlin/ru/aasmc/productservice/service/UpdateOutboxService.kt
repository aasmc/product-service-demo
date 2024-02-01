package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.AttributeCollection
import ru.aasmc.productservice.dto.ImageCollection
import java.math.BigDecimal

interface UpdateOutboxService {

    fun saveUpdatePVPhotosEvent(
        variantId: Long,
        newPhotos: ImageCollection
    )

    fun saveUpdatePVPriceEvent(
        variantId: Long,
        newPrice: BigDecimal
    )

    fun saveUpdatePVAttributesEvent(
        variantId: Long,
        newAttributes: AttributeCollection
    )

    fun saveUpdatePVNameEvent(
        variantId: Long,
        newName: String
    )

    fun saveUpdateProductNameEvent(
        productId: Long,
        newName: String
    )

    fun saveUpdateProductDescriptionEvent(
        productId: Long,
        newDescription: String
    )

    fun saveUpdateSkuStockEvent(
        variantId: Long,
        sku: String,
        newStock: Int
    )

    fun saveUpdateSkuPriceEvent(
        variantId: Long,
        sku: String,
        newPrice: BigDecimal
    )

}