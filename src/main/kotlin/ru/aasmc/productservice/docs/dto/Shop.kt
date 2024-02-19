package ru.aasmc.productservice.docs.dto

import java.util.UUID

data class CreateShopInitialRequest(
    val sellerId: UUID,
    val shopName: String
)

data class CreateShopLogoRequest(
    val shopId: UUID,
    val logo: String
)

data class CreateShopDescriptionRequest(
    val shopId: UUID,
    val shopDescription: String
)

data class CreateShopLocationRequest(
    val shopId: UUID,
    val location: Location
)

data class Shop(
    val id: UUID,
    val sellerId: UUID,
    var name: ShopName? = null,
    var logo: ShopLogo? = null,
    var description: ShopDescription? = null,
    var location: Location? = null,
    var status: ShopStatus = ShopStatus.INACTIVE
)

enum class ShopStatus {
    ACTIVE,
    INACTIVE,
    REMOVED
}
