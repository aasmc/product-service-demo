package ru.aasmc.productservice.mapper

import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.storage.model.*
import ru.aasmc.productservice.storage.repository.*
import ru.aasmc.productservice.utils.CryptoTool

@Component
class AttributeMapper(
    private val attributeValueMapper: AttributeValueMapper,
    private val cryptoTool: CryptoTool
) {

    fun toDomain(dto: AttributeDto): Attribute {
        val attribute = Attribute(
            name = dto.attributeName,
            shortName = dto.shortName,
            isFaceted = dto.isFaceted,
            isComposite = dto.type == AttributeType.COMPOSITE
        )
        addValuesToAttribute(attribute, dto)
        return attribute
    }

    fun toDto(domain: Attribute, isRequired: Boolean? = null): AttributeDto {
        return if (domain.isComposite) {
            CompositeAttributeDto(
                id = cryptoTool.hashOf(domain.id!!),
                attributeName = domain.name,
                shortName = domain.shortName,
                isFaceted = domain.isFaceted,
                createdAt = domain.createdAt,
                availableValues = attributeValueMapper
                    .compositeToDtoList(domain.compositeAttributeValues),
                isRequired = isRequired
            )
        } else {
            PlainAttributeDto(
                id = cryptoTool.hashOf(domain.id!!),
                attributeName = domain.name,
                shortName = domain.shortName,
                isFaceted = domain.isFaceted,
                createdAt = domain.createdAt,
                availableValues = attributeValueMapper
                    .toDtoList(domain.attributeValues),
                isRequired = isRequired
            )
        }
    }



    private fun addValuesToAttribute(attribute: Attribute, dto: AttributeDto) {
        when (dto) {
            is PlainAttributeDto -> {
                attribute.attributeValues
                    .addAll(attributeValueMapper
                        .toDomainList(dto.availableValues, attribute))
            }

            is CompositeAttributeDto -> {
                attribute.compositeAttributeValues
                    .addAll(attributeValueMapper
                        .compositeToDomainList(dto.availableValues, attribute))
            }
        }
    }

}