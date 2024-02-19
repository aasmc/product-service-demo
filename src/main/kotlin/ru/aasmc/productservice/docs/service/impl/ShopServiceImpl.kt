package ru.aasmc.productservice.docs.service.impl

import org.springframework.stereotype.Service
import ru.aasmc.productservice.docs.dto.*
import ru.aasmc.productservice.docs.service.ShopService

@Service
class ShopServiceImpl : ShopService {
    override fun initShopCreation(dto: CreateShopInitialRequest): Shop {
        TODO("Not yet implemented")
    }

    override fun addShopLogo(dto: CreateShopLogoRequest): Shop {
        TODO("Not yet implemented")
    }

    override fun addShopDescription(dto: CreateShopDescriptionRequest): Shop {
        TODO("Not yet implemented")
    }

    override fun addShopLocation(dto: CreateShopLocationRequest): Shop {
        TODO("Not yet implemented")
    }
}