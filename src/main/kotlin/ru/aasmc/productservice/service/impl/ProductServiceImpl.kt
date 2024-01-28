package ru.aasmc.productservice.service.impl

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.mapper.ProductMapper
import ru.aasmc.productservice.mapper.ProductVariantMapper
import ru.aasmc.productservice.service.ProductOutboxService
import ru.aasmc.productservice.service.ProductService
import ru.aasmc.productservice.storage.model.EventType
import ru.aasmc.productservice.storage.repository.ProductRepository
import ru.aasmc.productservice.storage.repository.ProductVariantRepository
import ru.aasmc.productservice.utils.CryptoTool

@Service
@Transactional
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val cryptoTool: CryptoTool,
    private val mapper: ProductMapper,
    private val productVariantMapper: ProductVariantMapper,
    private val productVariantRepository: ProductVariantRepository,
    private val productOutboxService: ProductOutboxService
): ProductService {

    override fun createProduct(dto: CreateProductRequest): ProductResponse {
        // TODO discuss whether we need to check if all required attributes of
        // category have been filled, or is a check-up on front enough?
        val product = productRepository.save(mapper.toDomain(dto))
        log.debug("Successfully saved product: {}", product)
        productOutboxService.addEvent(product.id!!, product, EventType.INSERT)
        return mapper.toProductResponseDto(product)
    }

    override fun getProductById(id: String): ProductResponse {
        val product = productRepository.findById(cryptoTool.idOf(id))
            .orElseThrow {
                val msg = "Product with ID=$id not found"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        log.debug("Found product: {}", product)
        return mapper.toProductResponseDto(product)
    }

    override fun getProductVariants(id: String): List<ProductVariantResponse> {
        val product = productRepository.findById(cryptoTool.idOf(id))
            .orElseThrow {
                val msg = "Product with ID=$id not found"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        log.debug("Found product in getProductVariants. {}", product)
        return product.variants.map(productVariantMapper::toProductVariantFullResponse)
    }

    override fun addProductVariant(productId: String, dto: ProductVariantRequestDto): ProductVariantResponse {
        val product = productRepository.findById(cryptoTool.idOf(productId))
            .orElseThrow {
                val msg = "Product with ID=$productId not found"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        val variant = productVariantMapper.toDomain(dto, product)
        product.variants.add(variant)
        productRepository.save(product)
        log.debug("Successfully added variant {} to product {}", variant, product)
        productOutboxService.addEvent(product.id!!, product, EventType.UPDATE)
        return productVariantMapper.toProductVariantFullResponse(variant)
    }

    override fun deleteProductVariant(productId: String, variantId: String) {
        val product = productRepository.findById(cryptoTool.idOf(productId))
            .orElseThrow {
                val msg = "Product with ID=$productId not found"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        // TODO find out if this remove is necessary
        product.variants.removeIf { variant -> variant.id == cryptoTool.idOf(variantId) }
        productVariantRepository.deleteById(cryptoTool.idOf(variantId))
        log.debug("Successfully deleted product variant with ID=$variantId")
        productRepository.save(product)
        productOutboxService.addEvent(product.id!!, product, EventType.UPDATE)
    }

    override fun deleteProduct(productId: String) {
        val id = cryptoTool.idOf(productId)
        val product = productRepository.findById(id)
            .orElseThrow {
                val msg = "Product with ID=$productId not found"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        product.shop.products.remove(product)
        productRepository.deleteById(id)
        log.debug("Successfully deleted product with ID=$productId")
        productOutboxService.addEvent(cryptoTool.idOf(productId), null, EventType.DELETE)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProductServiceImpl::class.java)
    }
}