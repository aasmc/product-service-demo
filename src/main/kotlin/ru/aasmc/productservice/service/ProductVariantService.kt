package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.*

interface ProductVariantService {

    fun updateSkuStock(dto: UpdateSkuStockRequest): UpdateSkuStockResponse

    fun updateSkuPrice(dto: UpdateSkuPriceRequest): UpdateSkuPriceResponse

    fun updateVariantPrice(dto: UpdateProductVariantPriceRequest): ProductVariantResponse

    fun updateVariantName(variantId: String, newName: String): ProductVariantResponse

    fun addVariantPhoto(variantId: String, photo: AppImage): ProductVariantResponse

    fun removeVariantPhoto(variantId: String, photoUrl: String): ProductVariantResponse

    fun addVariantAttribute(variantId: String, attribute: AttributeDto): ProductVariantResponse

    fun removeVariantAttribute(variantId: String, attributeName: String): ProductVariantResponse

    fun addValueToCompositeAttribute(
        variantId: String,
        attributeName: String,
        subAttributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse

    fun addAttributeValue(
        variantId: String,
        attributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse

    fun removeAttributeValue(
        variantId: String,
        attributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse

    fun removeValueFromCompositeAttribute(
        variantId: String,
        attributeName: String,
        subAttributeName: String,
        value: AttributeValueDto
    ): ProductVariantResponse

}