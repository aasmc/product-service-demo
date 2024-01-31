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
    private val productUpdateOutboxRepository: ProductUpdateOutboxRepository,
    private val productSkuUpdateOutboxRepository: ProductSkuUpdateOutboxRepository
) : UpdateOutboxService {
    override fun sendUpdatePVPhotosEvent(
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

    override fun sendUpdatePVPriceEvent(
        variantId: Long,
        prevPrice: BigDecimal,
        newPrice: BigDecimal
    ) {
        val data = UpdatePVPrice(
            variantId = variantId,
            prevPrice = prevPrice,
            newPrice = newPrice
        )
        saveProductUpdate(data) {
            "Successfully saved UpdatePVPriceEvent: {}"
        }
    }

    override fun sendUpdatePVAttributesEvent(
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

    override fun sendUpdateProductNameEvent(
        productId: Long,
        prevName: String,
        newName: String
    ) {
        val data = UpdateProductName(
            productId = productId,
            prevName = prevName,
            newName = newName
        )
        saveProductUpdate(data) {
            "Successfully saved UpdateProductNameEvent: {}"
        }
    }

    override fun sendUpdateProductDescriptionEvent(
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

    override fun sendUpdateSkuStockEvent(
        variantId: Long,
        sku: String,
        prevStock: Int,
        newStock: Int
    ) {
        val data = UpdateSkuStock(
            variantId = variantId,
            sku = sku,
            prevStock = prevStock,
            newStock = newStock
        )
        saveSkuUpdate(data) {
            "Successfully saved UpdateSkuStockEvent: {}"
        }
    }

    override fun sendUpdateSkuPriceEvent(
        variantId: Long,
        sku: String,
        prevPrice: BigDecimal,
        newPrice: BigDecimal
    ) {
        val data = UpdateSkuPrice(
            variantId = variantId,
            sku = sku,
            prevPrice = prevPrice,
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
        event = productSkuUpdateOutboxRepository.save(event)
        log.info(logMsg(), event)
    }

    private fun saveProductUpdate(
        data: ProductUpdate,
        logMsg: () -> String
    ) {
        var event = ProductUpdateOutbox(
            eventData = data
        )
        event = productUpdateOutboxRepository.save(event)
        log.info(logMsg(), event)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UpdateOutboxServiceImpl::class.java)
    }
}