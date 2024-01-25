package ru.aasmc.productservice.mapper

import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.CreateSellerRequest
import ru.aasmc.productservice.dto.CreateSellerResponse
import ru.aasmc.productservice.dto.SellerResponse
import ru.aasmc.productservice.service.ShopService
import ru.aasmc.productservice.storage.model.Seller
import ru.aasmc.productservice.utils.CryptoTool

@Component
class SellerMapper(
    private val cryptoTool: CryptoTool,
    private val shopService: ShopService
) {

    fun toDomain(dto: CreateSellerRequest): Seller =
        Seller(firstName = dto.firstName, lastName = dto.lastName)

    fun toCreateDto(domain: Seller): CreateSellerResponse {
        val hashedId = cryptoTool.hashOf(domain.id!!)
        return CreateSellerResponse(hashedId, domain.firstName, domain.lastName)
    }

    fun toFindDto(seller: Seller): SellerResponse {
        val hashedId = cryptoTool.hashOf(seller.id!!)
        return SellerResponse(
            id = hashedId,
            firstName = seller.firstName,
            lastName = seller.lastName,
            shops = shopService.getShopsForSeller(cryptoTool.hashOf(seller.id!!))
        )
    }

}

