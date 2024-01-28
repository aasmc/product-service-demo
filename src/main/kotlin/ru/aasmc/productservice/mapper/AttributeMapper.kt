package ru.aasmc.productservice.mapper

import ru.aasmc.productservice.dto.AttributeDto
import ru.aasmc.productservice.storage.model.Attribute

interface AttributeMapper<DTO: AttributeDto, DOMAIN: Attribute> {

    fun toDomain(dto: DTO): DOMAIN

    fun toDto(domain: DOMAIN, isRequired: Boolean? = null): DTO

}