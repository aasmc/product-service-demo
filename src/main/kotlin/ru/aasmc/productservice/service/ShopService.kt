package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.CreateShopRequest
import ru.aasmc.productservice.dto.ShopResponse

interface ShopService {

    fun createShop(dto: CreateShopRequest): ShopResponse

    fun getShopByIdWithProducts(hashedId: String): ShopResponse

    fun getShopsForSeller(sellerId: String): List<ShopResponse>

}