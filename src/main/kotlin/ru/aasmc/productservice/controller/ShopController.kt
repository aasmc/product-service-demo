package ru.aasmc.productservice.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.aasmc.productservice.dto.CreateShopRequest
import ru.aasmc.productservice.dto.ShopResponse
import ru.aasmc.productservice.service.ShopService

@RestController
@RequestMapping("/v1/shops")
class ShopController(
    private val shopService: ShopService
) {

    @PostMapping
    fun createShop(@RequestBody dto: CreateShopRequest): ShopResponse {
        log.info("Received POST request to create shop. {}", dto)
        return shopService.createShop(dto)
    }


    @GetMapping("/{shopId}")
    fun getShopWithProducts(@PathVariable("shopId") shopId: String): ShopResponse {
        log.info("Received request to GET shop with products. Shop ID={}", shopId)
        return shopService.getShopByIdWithProducts(shopId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ShopController::class.java)
    }
}