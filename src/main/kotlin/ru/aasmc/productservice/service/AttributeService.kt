package ru.aasmc.productservice.service

import ru.aasmc.productservice.dto.AttributeDto
import ru.aasmc.productservice.dto.AttributeValueDto
import ru.aasmc.productservice.dto.CompositeAttributeValueDto

interface AttributeService {

    fun createAttribute(dto: AttributeDto): AttributeDto

    fun getAllAttributesForCategory(categoryName: String): List<AttributeDto>

    fun getAllAttributes(): List<AttributeDto>

    fun getAttributeByName(name: String): AttributeDto

    fun addAttributeValue(
        attributeId: String,
        dto: AttributeValueDto,
    ): AttributeValueDto

    fun addCompositeAttributeValue(
        attributeId: String,
        dto: CompositeAttributeValueDto,
    ): CompositeAttributeValueDto

}