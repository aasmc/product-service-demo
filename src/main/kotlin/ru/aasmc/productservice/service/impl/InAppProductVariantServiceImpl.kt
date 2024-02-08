package ru.aasmc.productservice.service.impl

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
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

@Service
@Transactional
class InAppProductVariantServiceImpl(
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
        log.info("Successfully updated Sku stock: {}", dto)
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
        val variant = getProductVariantOrThrow(variantId, id)
        variant.imageCollection.images.add(photo)
        log.info("Successfully added new photo {} to product variant with ID={}", photo, id)
        productVariantRepository.save(variant)
        return productVariantMapper.toProductVariantFullResponse(variant)
    }

    override fun removeVariantPhoto(variantId: String, photo: AppImage): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val variant = getProductVariantOrThrow(variantId, id)
        val removed = variant.imageCollection.images.remove(photo)
        if (removed) {
            log.info("Successfully removed photo {} from product variant with ID={}", photo, variant.id)
        } else {
            log.info(
                "Failed to remove photo {} from product variant with ID={} because such photo doesn't exist.",
                photo, variant.id
            )
        }
        productVariantRepository.save(variant)
        return productVariantMapper.toProductVariantFullResponse(variant)
    }

    override fun addVariantAttribute(variantId: String, attribute: AttributeDto): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val variant = getProductVariantOrThrow(variantId, id)
        val existing = variant.attributeCollection.attributes.firstOrNull { it.id == attribute.id }
        if (existing != null) {
            val msg = "Cannot add attribute with ID=${attribute.id} to product variant" +
                    " with ID=$variantId because it already has that attribute."
            throw ProductServiceException(msg, HttpStatus.BAD_REQUEST.value())
        }
        variant.attributeCollection.attributes.add(attribute)
        productVariantRepository.save(variant)
        return productVariantMapper.toProductVariantFullResponse(variant)
    }

    override fun removeVariantAttribute(variantId: String, attributeName: String): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val variant = getProductVariantOrThrow(variantId, id)
        val removed = variant.attributeCollection.attributes
            .removeIf { it.attributeName == attributeName }
        if (removed) {
            log.info(
                "Successfully removed attribute with name={} from Product Variant with ID={}",
                attributeName, id
            )
        } else {
            log.info(
                "Failed to remove attribute with name={} from Product Variant with ID={} because" +
                        " the variant has no attribute with that name.",
                attributeName, id
            )
        }
        return productVariantMapper.toProductVariantFullResponse(variant)
    }

    override fun addValueToCompositeAttribute(
        variantId: String,
        attributeName: String,
        subAttributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val variant = getProductVariantOrThrow(variantId, id)
        val added = updateCompositeAttributeValue(variant, attributeName, subAttributeName, value)
        if (added == null || !added) {
            log.info("Failed to add value: {} to composite attribute with name: {}", value, attributeName)
        } else {
            log.info("Successfully added value: {} to composite attribute with name: {}", value, attributeName)
        }
        return productVariantMapper.toProductVariantFullResponse(variant)
    }

    override fun addAttributeValue(
        variantId: String,
        attributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val variant = getProductVariantOrThrow(variantId, id)
        val added = updateAttributeValue(variant, attributeName, value)
        if (added == null || !added) {
            log.info("Failed to add value: {} to attribute with name: {}", value, attributeName)
        } else {
            log.info("Successfully added value: {} to attribute with name: {}", value, attributeName)
        }
        return productVariantMapper.toProductVariantFullResponse(variant)
    }

    override fun removeAttributeValue(
        variantId: String,
        attributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val variant = getProductVariantOrThrow(variantId, id)
        val removed = updateAttributeValue(variant, attributeName, value, true)
        if (removed == null || !removed) {
            log.info("Failed to remove value: {} from attribute with name: {}", value, attributeName)
        } else {
            log.info("Successfully removed value: {} from attribute with name: {}", value, attributeName)
        }
        return productVariantMapper.toProductVariantFullResponse(variant)
    }

    override fun removeValueFromCompositeAttribute(
        variantId: String,
        attributeName: String,
        subAttributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse {
        val id = cryptoTool.idOf(variantId)
        val variant = getProductVariantOrThrow(variantId, id)
        val removed = updateCompositeAttributeValue(variant, attributeName, subAttributeName, value, true)
        if (removed == null || !removed) {
            log.info("Failed to remove value: {} from composite attribute with name: {}", value, attributeName)
        } else {
            log.info("Successfully removed value: {} from composite attribute with name: {}", value, attributeName)
        }
        return productVariantMapper.toProductVariantFullResponse(variant)
    }

    private fun updateCompositeAttributeValue(
        variant: ProductVariant,
        attributeName: String,
        subAttributeName: String,
        value: AttributeValueDto,
        remove: Boolean = false
    ): Boolean? {
        return variant.attributeCollection.attributes
            .firstOrNull { it.attributeName == attributeName }
            ?.let { attrDto ->
                val composite = attrDto as? CompositeAttributeDto ?: throw ProductServiceException(
                    "Cannot add/remove value to/from attribute, because the attribute $attributeName is not composite",
                    HttpStatus.BAD_REQUEST.value()
                )
                composite.subAttributes
                    .firstOrNull { it.attributeName == subAttributeName }
                    ?.let { subAttr ->
                        addOrRemoveValueToAttribute(subAttr, value, subAttributeName, remove)
                    }
            }
    }

    private fun updateAttributeValue(
        variant: ProductVariant,
        attributeName: String,
        value: AttributeValueDto,
        remove: Boolean = false
    ): Boolean? {
        return variant.attributeCollection.attributes
            .firstOrNull { it.attributeName == attributeName }
            ?.let { attribute ->
                addOrRemoveValueToAttribute(attribute, value, attributeName, remove)
            }
    }

    private fun addOrRemoveValueToAttribute(
        attribute: AttributeDto,
        value: AttributeValueDto,
        attributeName: String,
        remove: Boolean = false
    ) = when (attribute) {
        is ColorAttributeDto -> {
            addOrRemoveColorAttributeValue(value, attributeName, attribute, remove)
        }

        is CompositeAttributeDto -> {
            // we don't add values directly to composite attributes
            false
        }

        is NumericAttributeDto -> {
            addOrRemoveNumericAttributeValue(value, attributeName, attribute, remove)
        }

        is StringAttributeDto -> {
            addOrRemoveStringAttributeValue(value, attributeName, attribute, remove)
        }
    }

    private fun addOrRemoveStringAttributeValue(
        value: AttributeValueDto,
        attributeName: String,
        attribute: StringAttributeDto,
        remove: Boolean = false
    ): Boolean {
        if (value !is StringAttributeValueDto) {
            val msg = "Cannot add value $value to attribute with name" +
                    " $attributeName because value type ${value::class} is not " +
                    "compatible with String value type."
            throw ProductServiceException(msg, HttpStatus.BAD_REQUEST.value())
        }
        return if (remove) {
            attribute.availableValues
                .removeIf {
                    it.stringRuValue == value.stringRuValue &&
                            it.stringValue == value.stringValue
                }
        } else {
            attribute.availableValues.add(value)
        }
    }

    private fun addOrRemoveNumericAttributeValue(
        value: AttributeValueDto,
        attributeName: String,
        attribute: NumericAttributeDto,
        remove: Boolean = false
    ): Boolean {
        if (value !is NumericAttributeValueDto) {
            val msg = "Cannot add value $value to attribute with name" +
                    " $attributeName because value type ${value::class} is not " +
                    "compatible with Numeric value type."
            throw ProductServiceException(msg, HttpStatus.BAD_REQUEST.value())
        }
        return if (remove) {
            attribute.availableValues.removeIf {
                it.numValue == value.numValue &&
                        it.numRuValue == value.numRuValue &&
                        it.numUnit == value.numUnit
            }
        } else {
            attribute.availableValues.add(value)
        }
    }

    private fun addOrRemoveColorAttributeValue(
        value: AttributeValueDto,
        attributeName: String,
        attribute: ColorAttributeDto,
        remove: Boolean = false
    ): Boolean {
        if (value !is ColorAttributeValueDto) {
            val msg = "Cannot add value $value to attribute with name" +
                    " $attributeName because value type ${value::class} is not " +
                    "compatible with Color value type."
            throw ProductServiceException(msg, HttpStatus.BAD_REQUEST.value())
        }
        return if (remove) {
            attribute.availableValues.removeIf {
                it.colorValue == value.colorValue &&
                        it.colorHex == value.colorHex
            }
        } else {
            attribute.availableValues.add(value)
        }
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
        private val log = LoggerFactory.getLogger(InAppProductVariantServiceImpl::class.java)
    }
}