package ru.aasmc.productservice.service.impl

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.mapper.ProductVariantMapper
import ru.aasmc.productservice.service.ProductService
import ru.aasmc.productservice.service.ProductVariantService
import ru.aasmc.productservice.service.UpdateOutboxService
import ru.aasmc.productservice.storage.model.ProductVariant
import ru.aasmc.productservice.storage.repository.ProductVariantRepository
import ru.aasmc.productservice.utils.CryptoTool

@Service
@Transactional
class ProductVariantServiceImpl(
    private val updateOutboxService: UpdateOutboxService,
    private val productVariantRepository: ProductVariantRepository,
    private val cryptoTool: CryptoTool,
    private val productVariantMapper: ProductVariantMapper
) : ProductVariantService {

    override fun updateSkuStock(dto: UpdateSkuStockRequest): UpdateSkuStockResponse {
        val id = cryptoTool.idOf(dto.productVariantId)
        updateProductVariantSku(dto.productVariantId, id) { sku ->
            if (sku.sku == dto.sku) sku.copy(stock = dto.newStock) else sku
        }
        updateOutboxService.saveUpdateSkuStockEvent(
            id, dto.sku, dto.newStock
        )
        return UpdateSkuStockResponse(
            sku = dto.sku,
            newStock = dto.newStock
        )
    }

    override fun updateSkuPrice(dto: UpdateSkuPriceDto): UpdateSkuPriceResponse {
        val id = cryptoTool.idOf(dto.productVariantId)
        updateProductVariantSku(dto.productVariantId, id) { sku ->
            if (sku.sku == dto.sku) sku.copy(price = dto.newPrice) else sku
        }
        updateOutboxService.saveUpdateSkuPriceEvent(id, dto.sku, dto.newPrice)
        return UpdateSkuPriceResponse(
            sku = dto.sku,
            newPrice = dto.newPrice
        )
    }

    override fun updateVariantPrice(dto: UpdateProductVariantPriceDto): ProductVariantResponse {
        val id = cryptoTool.idOf(dto.productVariantId)
        val updated = updateProductVariant(dto.productVariantId, id) { variant ->
            variant.price = dto.newPrice
            variant
        }

        updateOutboxService.saveUpdatePVPriceEvent(
            variantId = id,
            newPrice = dto.newPrice
        )
        return productVariantMapper.toProductVariantFullResponse(updated)
    }

    override fun updateVariantName(variantId: String, newName: String): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val updated = updateProductVariant(variantId, id) { variant ->
            variant.variantName = newName
            variant
        }
        updateOutboxService.saveUpdatePVNameEvent(id, newName)
        return productVariantMapper.toProductVariantFullResponse(updated)
    }

    override fun addVariantPhoto(variantId: String, photo: AppImage): ProductVariantResponse {
        TODO("Not yet implemented")
    }

    override fun removeVariantPhoto(variantId: String, photo: AppImage): ProductVariantResponse {
        TODO("Not yet implemented")
    }

    override fun addVariantAttribute(variantId: String, attribute: AttributeDto): ProductVariantResponse {
        TODO("Not yet implemented")
    }

    override fun removeVariantAttribute(variantId: String, attributeName: String): ProductVariantResponse {
        TODO("Not yet implemented")
    }

    override fun addAttributeValue(
        variantId: String,
        attributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse {
        TODO("Not yet implemented")
    }

    override fun removeAttributeValue(
        variantId: String,
        attributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse {
        TODO("Not yet implemented")
    }

    private fun getProductVariantOrThrow(idStr: String, id: Long): ProductVariant {
        return productVariantRepository.findById(id).orElseThrow {
            val msg = "Product Variant with ID=$idStr not found."
            ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
        }
    }

    private fun updateProductVariantSku(idStr: String, id: Long, mapper: (Sku) -> Sku) {
        val variant = getProductVariantOrThrow(idStr, id)
        variant.skuCollection.skus.map(mapper)
        productVariantRepository.save(variant)
    }

    private fun updateProductVariant(
        idStr: String,
        id: Long,
        mapper: (ProductVariant) -> ProductVariant
    ): ProductVariant {
        var variant = getProductVariantOrThrow(idStr, id)
        variant = mapper(variant)
        return productVariantRepository.save(variant)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProductVariantServiceImpl::class.java)
    }
}