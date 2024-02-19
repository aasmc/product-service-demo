package ru.aasmc.productservice.docs.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.aasmc.productservice.docs.dto.*
import ru.aasmc.productservice.docs.service.ShopService

@RestController
@RequestMapping("/v2/shops")
class ShopController(
    private val shopService: ShopService
) {

    @PostMapping("/initial")
    fun initShopCreation(@RequestBody dto: CreateShopInitialRequest): Shop {
        log.info("Received POST request to init shop creation: {}", dto)
        return shopService.initShopCreation(dto)
    }

    @PostMapping("/logo")
    fun addShopLogo(@RequestBody dto: CreateShopLogoRequest): Shop {
        log.info("Received POST request to create shop logo: {}", dto)
        return shopService.addShopLogo(dto)
    }

    @PostMapping("/description")
    fun addShopDescription(@RequestBody dto: CreateShopDescriptionRequest): Shop {
        log.info("Received POST request to create shop description: {}", dto)
        return shopService.addShopDescription(dto)
    }

    @PostMapping("/location")
    fun addShopLocation(@RequestBody dto: CreateShopLocationRequest): Shop {
        log.info("Received POST request to create shop location: {}", dto)
        return shopService.addShopLocation(dto)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ShopController::class.java)
    }

}