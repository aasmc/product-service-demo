package ru.aasmc.productservice.docs.service

import ru.aasmc.productservice.docs.dto.*

interface ShopService {

    fun initShopCreation(dto: CreateShopInitialRequest): Shop

    fun addShopLogo(dto: CreateShopLogoRequest): Shop

    fun addShopDescription(dto: CreateShopDescriptionRequest): Shop

    fun addShopLocation(dto: CreateShopLocationRequest): Shop

}