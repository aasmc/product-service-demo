package ru.aasmc.productservice.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.service.ProductVariantService
import ru.aasmc.productservice.service.UpdateOutboxService

@Service
class ProductVariantServiceImpl(
    private val updateOutboxService: UpdateOutboxService
) : ProductVariantService {

    override fun updateSkuStock(dto: UpdateSkuStockRequest): UpdateSkuStockResponse {
        TODO("Not yet implemented")
    }

    override fun updateSkuPrice(dto: UpdateSkuPriceDto): UpdateSkuPriceResponse {
        TODO("Not yet implemented")
    }

    override fun updateVariantPrice(dto: UpdateProductVariantPriceDto): ProductVariantResponse {
        TODO("Not yet implemented")
    }

    override fun updateVariantName(variantId: String, newName: String): ProductVariantResponse {
        TODO("Not yet implemented")
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

    companion object {
        private val log = LoggerFactory.getLogger(ProductVariantServiceImpl::class.java)
    }
}