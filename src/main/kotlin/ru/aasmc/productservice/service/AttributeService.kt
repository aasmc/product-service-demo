package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.AttributeResponse
import ru.aasmc.productservice.dto.CreateAttributeRequest

interface AttributeService {

    fun createAttribute(dto: CreateAttributeRequest): AttributeResponse

    fun getAllAttributesForCategory(categoryName: String): List<AttributeResponse>

    fun getAllAttributes(): List<AttributeResponse>

    fun getAttributeByName(name: String): AttributeResponse

}