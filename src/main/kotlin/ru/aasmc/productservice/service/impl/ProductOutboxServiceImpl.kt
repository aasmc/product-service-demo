package ru.aasmc.productservice.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.aasmc.productservice.service.ProductOutboxService
import ru.aasmc.productservice.storage.model.*
import ru.aasmc.productservice.storage.model.jsonb_data.EventType
import ru.aasmc.productservice.storage.model.jsonb_data.ProductEventData
import ru.aasmc.productservice.storage.model.jsonb_data.ProductEventVariant
import ru.aasmc.productservice.storage.model.jsonb_data.ProductVariantEventData
import ru.aasmc.productservice.storage.repository.ProductOutboxRepository
import ru.aasmc.productservice.storage.repository.ProductVariantOutboxRepository

@Service
class ProductOutboxServiceImpl(
    private val productOutboxRepo: ProductOutboxRepository,
    private val variantOutboxRepo: ProductVariantOutboxRepository
) : ProductOutboxService {

    override fun addProductEvent(productId: Long, product: Product?, eventType: EventType) {
        val productOutbox = ProductOutbox(
            productId = productId,
            eventType = eventType,
            eventData = if (eventType == EventType.DELETE) null else buildProductEventData(product!!)
        )
        log.debug("Saving product event to outbox table: {}", productOutbox)
        productOutboxRepo.save(productOutbox)
    }

    override fun addProductVariantEvent(
        productId: Long,
        productVariantId: Long,
        eventType: EventType,
        productVariant: ProductVariant?
    ) {
        val outbox = ProductVariantOutbox(
            variantId = productVariantId,
            productId = productId,
            eventType = eventType,
            eventData = if (eventType == EventType.DELETE) null
            else buildProductVariantEventData(productId, productVariant!!)
        )
        log.debug("Saving product variant event to outbox table: {}", outbox)
        variantOutboxRepo.save(outbox)
    }

    private fun buildProductVariantEventData(
        productId: Long,
        productVariant: ProductVariant
    ) = ProductVariantEventData(
        variantId = productVariant.id!!,
        productId = productId,
        variantName = productVariant.variantName,
        price = productVariant.price,
        attributes = productVariant.attributes,
        images = productVariant.images,
        skuCollection = productVariant.skuCollection
    )


    private fun buildProductEventData(product: Product): ProductEventData {
        return ProductEventData(
            shopName = product.shop.name,
            categoryString = buildCategoryString(product.category),
            productName = product.name,
            productDescription = product.description,
            variants = mapVariants(product.variants),
            createdAt = product.createdAt!!,
            updatedAt = product.updatedAt!!
        )
    }

    private fun mapVariants(variants: Set<ProductVariant>): List<ProductEventVariant> {
        return variants.map { variant ->
            ProductEventVariant(
                variantId = variant.id!!,
                price = variant.price,
                attributes = variant.attributes,
                images = variant.images,
                createdAt = variant.createdAt!!,
                updatedAt = variant.updatedAt!!,
                skuCollection = variant.skuCollection
            )
        }
    }

    private fun buildCategoryString(category: Category): String {
        var str = category.name
        var parent = category.parent
        while (parent != null) {
            str = "${parent.name}.$str"
            parent = parent.parent
        }
        return str
    }


    companion object {
        private val log = LoggerFactory.getLogger(ProductOutboxServiceImpl::class.java)
    }
}