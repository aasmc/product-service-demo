package ru.aasmc.productservice.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.aasmc.productservice.service.ProductOutboxService
import ru.aasmc.productservice.storage.model.*
import ru.aasmc.productservice.storage.repository.CategoryRepository
import ru.aasmc.productservice.storage.repository.ProductOutboxRepository

@Service
class ProductOutboxServiceImpl(
    private val productOutboxRepository: ProductOutboxRepository
) : ProductOutboxService {

    override fun addEvent(productId: Long, product: Product?, eventType: EventType) {
        val productOutbox = ProductOutbox(
            productId = productId,
            eventType = eventType,
            eventData = if (eventType == EventType.DELETE) null else buildProductEventData(product!!)
        )
        log.debug("Saving event to outbox table: {}", productOutbox)
        productOutboxRepository.save(productOutbox)
    }


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
                stock = variant.stock,
                attributes = variant.attributes,
                images = variant.images,
                createdAt = variant.createdAt!!,
                updatedAt = variant.updatedAt!!
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