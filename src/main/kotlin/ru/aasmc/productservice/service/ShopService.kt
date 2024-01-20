package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.CreateShopRequest
import ru.aasmc.productservice.dto.CreateShopResponse
import ru.aasmc.productservice.dto.ShopFullResponse
import ru.aasmc.productservice.dto.ShopShortResponse

interface ShopService {

    fun createShop(dto: CreateShopRequest): CreateShopResponse

    fun getShopByIdWithoutProducts(hashedId: String): ShopShortResponse

    fun getShopByIdWithProducts(hashedId: String): ShopFullResponse

}