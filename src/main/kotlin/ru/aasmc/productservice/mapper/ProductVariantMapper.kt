package ru.aasmc.productservice.mapper

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.ProductVariantResponse
import ru.aasmc.productservice.dto.ProductVariantRequestDto
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.storage.model.Product
import ru.aasmc.productservice.storage.model.ProductVariant
import ru.aasmc.productservice.storage.repository.AttributeRepository
import ru.aasmc.productservice.utils.CryptoTool

@Component
class ProductVariantMapper(
    private val attributeRepository: AttributeRepository,
    private val cryptoTool: CryptoTool
) {

    fun toDomain(dto: ProductVariantRequestDto, product: Product): ProductVariant {
        val names = dto.attributes.map { it.attributeName }.toSet()
        val attrCount = attributeRepository.countByNameIn(names)
        if (attrCount != dto.attributes.size.toLong()) {
            val msg = "Cannot create product variant, because not all attributes exist."
            throw ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
        }
        return ProductVariant(
            variantName = dto.variantName,
            price = dto.price,
            stock = dto.stock,
            attributes = dto.attributes.toMutableList(),
            images = dto.images,
            product = product
        )
    }

    fun toProductVariantFullResponse(domain: ProductVariant): ProductVariantResponse =
        ProductVariantResponse(
            productId = cryptoTool.hashOf(domain.product.id!!),
            variantId = cryptoTool.hashOf(domain.id!!),
            variantName = domain.variantName,
            price = domain.price,
            stock = domain.stock,
            attributes = domain.attributes,
            images = domain.images,
            createdAt = domain.createdAt!!
        )


}