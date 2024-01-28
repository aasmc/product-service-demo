package ru.aasmc.productservice.service.impl

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.CreateShopRequest
import ru.aasmc.productservice.dto.ShopResponse
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.mapper.ShopMapper
import ru.aasmc.productservice.service.ShopService
import ru.aasmc.productservice.storage.model.Shop
import ru.aasmc.productservice.storage.repository.ShopRepository
import ru.aasmc.productservice.utils.CryptoTool

@Service
class ShopServiceImpl(
    private val shopRepository: ShopRepository,
    private val mapper: ShopMapper,
    private val cryptoTool: CryptoTool
) : ShopService {

    override fun createShop(dto: CreateShopRequest): ShopResponse {
        val shop = shopRepository.save(mapper.toDomain(dto))
        log.debug("Successfully saved shop to repository. {}", shop)
        return mapper.toFullResponse(shop)
    }

    override fun getShopByIdWithProducts(hashedId: String): ShopResponse {
        val shop = getShopOrThrow(hashedId)
        return mapper.toFullResponse(shop)
    }

    override fun getShopsForSeller(sellerId: String): List<ShopResponse> {
        return shopRepository.findAllBySeller_Id(cryptoTool.idOf(sellerId))
            .map(mapper::toFullResponse)
    }

    private fun getShopOrThrow(hashedId: String): Shop {
        val shop = shopRepository.findShopByIdWithSeller(cryptoTool.idOf(hashedId))
            .orElseThrow {
                val msg = "Shop with ID=$hashedId not found"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        log.debug("Retrieved shop with ID=${shop.id}. Shop: {}", shop)
        return shop
    }

    companion object {
        private val log = LoggerFactory.getLogger(ShopServiceImpl::class.java)
    }
}