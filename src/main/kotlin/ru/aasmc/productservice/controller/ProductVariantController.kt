package ru.aasmc.productservice.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.service.ProductVariantService

@RestController
@RequestMapping("/v1/product-variants")
class ProductVariantController(
    private val productVariantService: ProductVariantService
) {

    @PatchMapping("/sku-stock")
    fun updateSkuStock(@RequestBody dto: UpdateSkuStockRequest): UpdateSkuStockResponse {
        log.info("Received PATCH request to update sku stock: {}", dto)
        return productVariantService.updateSkuStock(dto)
    }

    @PatchMapping("/sku-price")
    fun updateSkuPrice(@RequestBody dto: UpdateSkuPriceRequest): UpdateSkuPriceResponse {
        log.info("Received PATCH request to update sku price: {}", dto)
        return productVariantService.updateSkuPrice(dto)
    }

    @PatchMapping("/price")
    fun updateVariantPrice(@RequestBody dto: UpdateProductVariantPriceRequest): ProductVariantResponse {
        log.info("Received PATCH request to update product variant price: {}", dto)
        return productVariantService.updateVariantPrice(dto)
    }

    @PatchMapping("/{id}/name")
    fun updateVariantName(
        @PathVariable("id") variantId: String,
        @RequestParam("newName") newName: String
    ): ProductVariantResponse {
        log.info(
            "Received PATCH request to set new name: {} to product variant with ID={}",
            newName, variantId
        )
        return productVariantService.updateVariantName(variantId, newName)
    }

    @PatchMapping("/{id}/photo")
    fun addVariantPhoto(
        @PathVariable("id") variantId: String,
        @RequestBody photo: AppImage,
    ): ProductVariantResponse {
        log.info(
            "Received PATCH request to add photo: {} to product variant with ID={}",
            photo, variantId
        )
        return productVariantService.addVariantPhoto(variantId, photo)
    }

    @DeleteMapping("/{id}/photo")
    fun deleteVariantPhoto(
        @PathVariable("id") variantId: String,
        @RequestParam("photoUrl") photoUrl: String,
    ): ProductVariantResponse {
        log.info(
            "Received request to DELETE photo: {} from product variant with ID={}",
            photoUrl, variantId
        )
        return productVariantService.removeVariantPhoto(variantId, photoUrl)
    }

    @PatchMapping("/{id}/attribute")
    fun addVariantAttribute(
        @PathVariable("id") variantId: String,
        @RequestBody attribute: AttributeDto,
    ): ProductVariantResponse {
        log.info(
            "Received PATCH request to add attribute: {} to product variant with ID={}",
            attribute, variantId
        )
        return productVariantService.addVariantAttribute(variantId, attribute)
    }

    @DeleteMapping("/{id}/attribute")
    fun deleteVariantAttribute(
        @PathVariable("id") variantId: String,
        @RequestParam("attributeName") attributeName: String
    ): ProductVariantResponse {
        log.info(
            "Received request to DELETE attribute with name={} from product variant with ID={}",
            attributeName, variantId
        )
        return productVariantService.removeVariantAttribute(variantId, attributeName)
    }

    @PatchMapping("/{id}/attribute-value")
    fun addAttributeValue(
        @PathVariable("id") variantId: String,
        @RequestBody value: AttributeValueDto,
        @RequestParam("attributeName") attributeName: String
    ): ProductVariantResponse {
        log.info(
            "Received PATCH request to add value: {} to attribute with name={}, of product variant with ID={}",
            value, attributeName, variantId
        )
        return productVariantService.addAttributeValue(variantId, attributeName, value)
    }

    @PatchMapping("/{id}/composite-attribute-value")
    fun addCompositeAttributeValue(
        @PathVariable("id") variantId: String,
        @RequestBody value: AttributeValueDto,
        @RequestParam("attributeName") attributeName: String,
        @RequestParam("subAttributeName") subAttributeName: String,
    ): ProductVariantResponse {
        log.info(
            "Received PATCH request to add value: {} to composite attribute with name={}, of product variant with ID={}",
            value, attributeName, variantId
        )
        return productVariantService.addValueToCompositeAttribute(variantId, attributeName, subAttributeName, value)
    }

    @PatchMapping("/{id}/attribute-value-delete")
    fun deleteAttributeValue(
        @PathVariable("id") variantId: String,
        @RequestBody value: AttributeValueDto,
        @RequestParam("attributeName") attributeName: String
    ): ProductVariantResponse {
        log.info(
            "Received request to DELETE value: {} from attribute with name={} of product variant with ID={}",
            value, attributeName, variantId
        )
        return productVariantService.removeAttributeValue(variantId, attributeName, value)
    }

    @PatchMapping("/{id}/composite-attribute-value-delete")
    fun deleteCompositeAttributeValue(
        @PathVariable("id") variantId: String,
        @RequestBody value: AttributeValueDto,
        @RequestParam("attributeName") attributeName: String,
        @RequestParam("subAttributeName") subAttributeName: String,
    ): ProductVariantResponse {
        log.info(
            "Received request to DELETE value: {} from composite attribute with name={} of product variant with ID={}",
            value, attributeName, variantId
        )
        return productVariantService.removeValueFromCompositeAttribute(variantId, attributeName, subAttributeName, value)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProductVariantController::class.java)
    }

}