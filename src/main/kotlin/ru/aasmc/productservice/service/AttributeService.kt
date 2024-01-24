package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.AttributeDto

interface AttributeService {

    fun createAttribute(dto: AttributeDto): AttributeDto

    fun getAllAttributesForCategory(categoryName: String): List<AttributeDto>

    fun getAllAttributes(): List<AttributeDto>

    fun getAttributeByName(name: String): AttributeDto

}