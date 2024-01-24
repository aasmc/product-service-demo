package ru.aasmc.productservice.mapper

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.CreateShopRequest
import ru.aasmc.productservice.dto.CreateShopResponse
import ru.aasmc.productservice.dto.ShopResponse
import ru.aasmc.productservice.dto.ShopShortResponse
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.storage.model.Seller
import ru.aasmc.productservice.storage.model.Shop
import ru.aasmc.productservice.storage.repository.SellerRepository
import ru.aasmc.productservice.utils.CryptoTool

@Component
class ShopMapper(
    private val cryptoTool: CryptoTool,
    private val sellerRepository: SellerRepository,
    private val productMapper: ProductMapper
) {

    fun toDomain(dto: CreateShopRequest): Shop {
        val seller = getSeller(dto.sellerId)
        return Shop(
            seller = seller,
            name = dto.name,
            description = dto.description
        )
    }

    fun toCreateDto(domain: Shop): CreateShopResponse =
        CreateShopResponse(cryptoTool.hashOf(domain.id!!))

    fun toShortResponse(domain: Shop): ShopShortResponse =
        ShopShortResponse(
            id = cryptoTool.hashOf(domain.id!!),
            sellerId = cryptoTool.hashOf(domain.seller.id!!),
            name = domain.name,
            description = domain.name
        )

    fun toFullResponse(domain: Shop): ShopResponse {
        val products = domain.products
            .map(productMapper::toProductResponseDto)
        return ShopResponse(
            id = cryptoTool.hashOf(domain.id!!),
            sellerId = cryptoTool.hashOf(domain.seller.id!!),
            name = domain.name,
            description = domain.description,
            products = products
        )
    }

    private fun getSeller(hashedId: String): Seller {
        return sellerRepository.findById(cryptoTool.idOf(hashedId))
            .orElseThrow {
                val msg = "Cannot create shop because seller with ID=$hashedId doesn't exist"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
    }

}