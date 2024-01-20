package ru.aasmc.productservice.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.aasmc.productservice.dto.CreateShopRequest
import ru.aasmc.productservice.dto.CreateShopResponse
import ru.aasmc.productservice.dto.ShopFullResponse
import ru.aasmc.productservice.dto.ShopShortResponse
import ru.aasmc.productservice.service.ShopService

@RestController
@RequestMapping("/v1/shops")
class ShopController(
    private val shopService: ShopService
) {

    @PostMapping
    fun createShop(@RequestBody dto: CreateShopRequest): CreateShopResponse {
        log.info("Received POST request to create shop. {}", dto)
        return shopService.createShop(dto)
    }

    @GetMapping("/{shopId}")
    fun getShopWithoutProducts(@PathVariable("shopId") shopId: String): ShopShortResponse {
        log.info("Received received to GET shop without products. Shop ID={}", shopId)
        return shopService.getShopByIdWithoutProducts(shopId)
    }

    @GetMapping("/full/{shopId}")
    fun getShopWithProducts(@PathVariable("shopId") shopId: String): ShopFullResponse {
        log.info("Received request to GET shop with products. Shop ID={}", shopId)
        return shopService.getShopByIdWithProducts(shopId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ShopController::class.java)
    }
}