package ru.aasmc.productservice.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.AttributeCollection
import ru.aasmc.productservice.dto.ImageCollection
import ru.aasmc.productservice.service.UpdateOutboxService
import ru.aasmc.productservice.storage.model.ProductSkuUpdateOutbox
import ru.aasmc.productservice.storage.model.ProductUpdateOutbox
import ru.aasmc.productservice.storage.model.jsonb_data.*
import ru.aasmc.productservice.storage.repository.ProductSkuUpdateOutboxRepository
import ru.aasmc.productservice.storage.repository.ProductUpdateOutboxRepository
import java.math.BigDecimal

@Service
class UpdateOutboxServiceImpl(
    private val productUpdateRepo: ProductUpdateOutboxRepository,
    private val skuUpdateRepo: ProductSkuUpdateOutboxRepository
) : UpdateOutboxService {
    override fun saveUpdatePVPhotosEvent(
        variantId: Long,
        newPhotos: ImageCollection
    ) {
        val data = UpdatePVPhotos(
            variantId = variantId,
            newPhotos = newPhotos
        )
        saveProductUpdate(data) {
            "Successfully saved UpdatePVPhotosEvent: {}"
        }
    }

    override fun saveUpdatePVPriceEvent(
        variantId: Long,
        newPrice: BigDecimal
    ) {
        val data = UpdatePVPrice(
            variantId = variantId,
            newPrice = newPrice
        )
        saveProductUpdate(data) {
            "Successfully saved UpdatePVPriceEvent: {}"
        }
    }

    override fun saveUpdatePVAttributesEvent(
        variantId: Long,
        newAttributes: AttributeCollection
    ) {
        val data = UpdatePVAttributes(
            variantId = variantId,
            newAttributes = newAttributes
        )
        saveProductUpdate(data) {
            "Successfully saved UpdatePVAttributeEvent: {}"
        }
    }

    override fun saveUpdatePVNameEvent(variantId: Long, newName: String) {
        val data = UpdatePVName(
            variantId = variantId,
            newName = newName
        )
        saveProductUpdate(data) {
            "Successfully saved UpdatePVNameEvent: {}"
        }
    }

    override fun saveUpdateProductNameEvent(
        productId: Long,
        newName: String
    ) {
        val data = UpdateProductName(
            productId = productId,
            newName = newName
        )
        saveProductUpdate(data) {
            "Successfully saved UpdateProductNameEvent: {}"
        }
    }

    override fun saveUpdateProductDescriptionEvent(
        productId: Long,
        newDescription: String
    ) {
        val data = UpdateProductDescription(
            productId = productId,
            newDescription = newDescription
        )
        saveProductUpdate(data) {
            "Successfully save UpdateProductDescriptionEvent: {}"
        }
    }

    override fun saveUpdateSkuStockEvent(
        variantId: Long,
        sku: String,
        newStock: Int
    ) {
        val data = UpdateSkuStock(
            variantId = variantId,
            sku = sku,
            newStock = newStock
        )
        saveSkuUpdate(data) {
            "Successfully saved UpdateSkuStockEvent: {}"
        }
    }

    override fun saveUpdateSkuPriceEvent(
        variantId: Long,
        sku: String,
        newPrice: BigDecimal
    ) {
        val data = UpdateSkuPrice(
            variantId = variantId,
            sku = sku,
            newPrice = newPrice
        )
        saveSkuUpdate(data) {
            "Successfully saved UpdateSkuPriceEvent: {}"
        }
    }

    private fun saveSkuUpdate(
        data: SkuUpdate,
        logMsg: () -> String
    ) {
        var event = ProductSkuUpdateOutbox(
            eventData = data
        )
        event = skuUpdateRepo.save(event)
        log.info(logMsg(), event)
    }

    private fun saveProductUpdate(
        data: ProductUpdate,
        logMsg: () -> String
    ) {
        var event = ProductUpdateOutbox(
            eventData = data
        )
        event = productUpdateRepo.save(event)
        log.info(logMsg(), event)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UpdateOutboxServiceImpl::class.java)
    }
}