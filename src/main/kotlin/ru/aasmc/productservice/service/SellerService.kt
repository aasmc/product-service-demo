package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.CreateSellerRequest
import ru.aasmc.productservice.dto.CreateSellerResponse
import ru.aasmc.productservice.dto.SellerResponse

interface SellerService {

    fun createSeller(dto: CreateSellerRequest): CreateSellerResponse

    fun getSellerById(hashedId: String): SellerResponse

}