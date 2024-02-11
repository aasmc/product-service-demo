package ru.aasmc.productservice.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.mapper.ProductVariantMapper
import ru.aasmc.productservice.service.ProductVariantService
import ru.aasmc.productservice.service.UpdateOutboxService
import ru.aasmc.productservice.storage.model.ProductVariant
import ru.aasmc.productservice.storage.repository.ProductVariantRepository
import ru.aasmc.productservice.utils.CryptoTool

@Primary
@Transactional
@Service
class JsonbOpsProductVariantServiceImpl(
    private val updateOutboxService: UpdateOutboxService,
    private val productVariantRepository: ProductVariantRepository,
    private val cryptoTool: CryptoTool,
    private val productVariantMapper: ProductVariantMapper,
    private val om: ObjectMapper
) : ProductVariantService {
    override fun updateSkuStock(dto: UpdateSkuStockRequest): UpdateSkuStockResponse {
        val variantId = cryptoTool.idOf(dto.productVariantId)
        val count = productVariantRepository.updateSkuStock(
            sku = dto.sku,
            variantId = variantId,
            newStock = dto.newStock
        )
        checkUpdateCount(count, dto.productVariantId)
        updateOutboxService.saveUpdateSkuStockEvent(
            variantId, dto.sku, dto.newStock
        )
        log.info("Successfully updated Sku stock: {}", dto)
        return UpdateSkuStockResponse(
            sku = dto.sku,
            newStock = dto.newStock
        )
    }

    override fun updateSkuPrice(dto: UpdateSkuPriceDto): UpdateSkuPriceResponse {
        val variantId = cryptoTool.idOf(dto.productVariantId)
        val count = productVariantRepository.updateSkuPrice(
            sku = dto.sku,
            variantId = variantId,
            newPrice = dto.newPrice
        )
        checkUpdateCount(count, dto.productVariantId)
        updateOutboxService.saveUpdateSkuPriceEvent(variantId, dto.sku, dto.newPrice)
        log.info("Successfully updated Sku price: {}", dto)
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
        log.info("Successfully updated Product Variant price: {}", dto)
        return productVariantMapper.toProductVariantFullResponse(updated)
    }

    override fun updateVariantName(variantId: String, newName: String): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val updated = updateProductVariant(variantId, id) { variant ->
            variant.variantName = newName
            variant
        }
        updateOutboxService.saveUpdatePVNameEvent(id, newName)
        log.info("Successfully set new name: {} to Product Variant with ID={}", newName, id)
        return productVariantMapper.toProductVariantFullResponse(updated)
    }

    override fun addVariantPhoto(variantId: String, photo: AppImage): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val count = productVariantRepository.addImage(id, om.writeValueAsString(photo))
        checkUpdateCount(count, variantId)
        log.info("Successfully added new photo {} to product variant with ID={}", photo, id)
        val updated = getProductVariantOrThrow(variantId, id)
        updateOutboxService.saveUpdatePVPhotosEvent(
            id, updated.imageCollection
        )
        return productVariantMapper.toProductVariantFullResponse(updated)
    }

    override fun removeVariantPhoto(variantId: String, photo: AppImage): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val count = productVariantRepository.removeImage(id, photo.url)
        checkUpdateCount(count, variantId)
        val updated = getProductVariantOrThrow(variantId, id)
        updateOutboxService.saveUpdatePVPhotosEvent(id, updated.imageCollection)
        log.info("Successfully removed photo {} from product variant with ID={}", photo, id)
        return productVariantMapper.toProductVariantFullResponse(updated)
    }

    override fun addVariantAttribute(variantId: String, attribute: AttributeDto): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val count = productVariantRepository.addOrReplaceVariantAttribute(
            variantId = id,
            attrString = om.writeValueAsString(attribute),
            attrName = attribute.attributeName
        )
        checkUpdateCount(count, variantId)
        val updated = getProductVariantOrThrow(variantId, id)
        updateOutboxService.saveUpdatePVAttributesEvent(id, updated.attributeCollection)
        log.info(
            "Successfully added attribute: {} to product variant with ID={}",
            attribute, id
        )
        return productVariantMapper.toProductVariantFullResponse(updated)
    }

    override fun removeVariantAttribute(variantId: String, attributeName: String): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val count = productVariantRepository.removeVariantAttribute(
            variantId = id,
            attrName = attributeName
        )
        checkUpdateCount(count, variantId)
        val updated = getProductVariantOrThrow(variantId, id)
        updateOutboxService.saveUpdatePVAttributesEvent(id, updated.attributeCollection)
        log.info(
            "Successfully removed attribute with name={} from Product Variant with ID={}",
            attributeName, id
        )
        return productVariantMapper.toProductVariantFullResponse(updated)
    }

    override fun addValueToCompositeAttribute(
        variantId: String,
        attributeName: String,
        subAttributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val count = productVariantRepository.addCompositeAttributeValue(
            variantId = id,
            attrName = attributeName,
            subAttrName = subAttributeName,
            valueString = om.writeValueAsString(value)
        )
        checkUpdateCount(count, variantId)
        val updated = getProductVariantOrThrow(variantId, id)
        updateOutboxService.saveUpdatePVAttributesEvent(id, updated.attributeCollection)
        log.info(
            "Successfully added value: {} to composite attribute with name: {} of product variant with ID={}",
            value, attributeName, id
        )
        return productVariantMapper.toProductVariantFullResponse(updated)
    }

    override fun addAttributeValue(
        variantId: String,
        attributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val count = productVariantRepository.addAttributeValue(
            id,
            attributeName,
            om.writeValueAsString(value)
        )
        checkUpdateCount(count, variantId)
        val updated = getProductVariantOrThrow(variantId, id)
        updateOutboxService.saveUpdatePVAttributesEvent(id, updated.attributeCollection)
        log.info(
            "Successfully added value: {} to attribute with name: {} of product variant with ID={}",
            value, attributeName, id
        )
        return productVariantMapper.toProductVariantFullResponse(updated)
    }

    override fun removeAttributeValue(
        variantId: String,
        attributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val count = when (value) {
            is ColorAttributeValueDto -> {
                productVariantRepository.removeColorAttributeValue(
                    id,
                    attributeName,
                    value.colorValue,
                    value.colorHex
                )
            }

            is NumericAttributeValueDto -> {
                productVariantRepository.removeNumericAttributeValue(
                    id,
                    attributeName,
                    value.numValue
                )
            }

            is StringAttributeValueDto -> {
                productVariantRepository.removeStringAttributeValue(
                    id,
                    attributeName,
                    value.stringValue
                )
            }
        }
        checkUpdateCount(count, variantId)
        val updated = getProductVariantOrThrow(variantId, id)
        updateOutboxService.saveUpdatePVAttributesEvent(id, updated.attributeCollection)
        log.info(
            "Successfully removed value: {} from attribute with name: {} of product variant with ID={}",
            value, attributeName, id
        )
        return productVariantMapper.toProductVariantFullResponse(updated)
    }

    override fun removeValueFromCompositeAttribute(
        variantId: String,
        attributeName: String,
        subAttributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val count = when (value) {
            is ColorAttributeValueDto -> {
                productVariantRepository.removeCompositeAttributeColorValue(
                    id,
                    attributeName,
                    subAttributeName,
                    value.colorValue
                )
            }

            is NumericAttributeValueDto -> {
                productVariantRepository.removeCompositeAttributeNumericValue(
                    id,
                    attributeName,
                    subAttributeName,
                    value.numValue
                )
            }

            is StringAttributeValueDto -> {
                productVariantRepository.removeCompositeAttributeStringValue(
                    id,
                    attributeName,
                    subAttributeName,
                    value.stringValue
                )
            }
        }
        checkUpdateCount(count, variantId)
        val updated = getProductVariantOrThrow(variantId, id)
        updateOutboxService.saveUpdatePVAttributesEvent(id, updated.attributeCollection)
        log.info(
            "Successfully removed value: {} from composite attribute with name:{}, subattribut name: {} of product variant with ID={}",
            value, attributeName, subAttributeName, id
        )
        return productVariantMapper.toProductVariantFullResponse(updated)
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

    private fun getProductVariantOrThrow(idStr: String, id: Long): ProductVariant {
        return productVariantRepository.findById(id).orElseThrow {
            val msg = "Product Variant with ID=$idStr not found."
            ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
        }
    }

    private fun checkVariantExists(id: Long, idStr: String) {
        if (productVariantRepository.getIdIfPresent(id) == null) {
            val msg = "ProductVariant with ID=$idStr not found."
            throw ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
        }
    }

    private fun checkUpdateCount(count: Int, idStr: String) {
        var msg: String? = null
        var status: Int? = null
        if (count == 0) {
            msg = "Product Variant with ID=$idStr not found."
            status = HttpStatus.NOT_FOUND.value()

        } else if (count > 1) {
            msg = "Database error. Database contains more than one row in Table product_variants for Product Variant with ID=$idStr."
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        }
        if (msg != null) {
            throw ProductServiceException(msg, status!!)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(JsonbOpsProductVariantServiceImpl::class.java)
    }
}