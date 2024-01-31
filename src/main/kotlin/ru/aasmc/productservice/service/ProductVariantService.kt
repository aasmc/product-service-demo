package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.UpdateSkuPriceDto
import ru.aasmc.productservice.dto.UpdateSkuPriceResponse
import ru.aasmc.productservice.dto.UpdateSkuStockRequest
import ru.aasmc.productservice.dto.UpdateSkuStockResponse

interface ProductVariantService {

    fun updateSkuStock(dto: UpdateSkuStockRequest): UpdateSkuStockResponse

    fun updateSkuPrice(dto: UpdateSkuPriceDto): UpdateSkuPriceResponse

    

}