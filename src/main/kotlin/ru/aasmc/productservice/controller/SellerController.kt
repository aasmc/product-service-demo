package ru.aasmc.productservice.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.aasmc.productservice.dto.CreateSellerRequest
import ru.aasmc.productservice.dto.CreateSellerResponse
import ru.aasmc.productservice.dto.SellerResponse
import ru.aasmc.productservice.service.SellerService

@RestController
@RequestMapping("/v1/sellers")
class SellerController(
    private val sellerService: SellerService
) {

    @PostMapping
    fun createSeller(@RequestBody dto: CreateSellerRequest): CreateSellerResponse {
        log.info("Received POST request to create seller. {}", dto)
        return sellerService.createSeller(dto)
    }

    @GetMapping("/{sellerId}")
    fun getSeller(@PathVariable("sellerId") sellerId: String): SellerResponse {
        log.info("Received request to GET seller by ID={}", sellerId)
        return sellerService.getSellerById(sellerId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(SellerController::class.java)
    }
}