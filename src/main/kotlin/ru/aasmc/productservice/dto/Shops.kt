package ru.aasmc.productservice.dto

data class CreateShopRequest(
    val sellerId: String,
    val name: String,
    val description: String
)

data class ShopResponse(
    val id: String,
    val sellerId: String,
    val name: String,
    val description: String,
    val products: List<ProductResponse>
)
