package ru.aasmc.productservice.mapper

import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.storage.model.*
import ru.aasmc.productservice.storage.repository.*
import ru.aasmc.productservice.utils.CryptoTool

@Component
class AttributeMapper(
    private val stringValueRepository: StringAttributeValueRepository,
    private val colorValueRepository: ColorAttributeValueRepository,
    private val numericValueRepository: NumericAttributeValueRepository,
    private val compositeAttributeValueRepository: CompositeAttributeValueRepository,
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

    fun toDto(domain: Attribute): AttributeDto {
        return if (domain.isComposite) {
            CompositeAttributeDto(
                id = cryptoTool.hashOf(domain.id!!),
                attributeName = domain.name,
                shortName = domain.shortName,
                isFaceted = domain.isFaceted,
                createdAt = domain.createdAt,
                values = mapCompositeDomainValuesToDto(domain.compositeAttributeValues)
            )
        } else {
            PlainAttributeDto(
                id = cryptoTool.hashOf(domain.id!!),
                attributeName = domain.name,
                shortName = domain.shortName,
                isFaceted = domain.isFaceted,
                createdAt = domain.createdAt,
                values = mapDomainValuesToDto(domain.attributeValues)
            )
        }
    }

    private fun addValuesToAttribute(attribute: Attribute, dto: AttributeDto) {
        when (dto) {
            is PlainAttributeDto -> {
                attribute.attributeValues
                    .addAll(mapDtoValuesToDomain(dto.values, attribute))
            }

            is CompositeAttributeDto -> {
                attribute.compositeAttributeValues
                    .addAll(mapCompositeDtoValuesToDomain(dto.values, attribute))
            }
        }
    }

    private fun mapDtoValuesToDomain(
        valueDtos: List<AttributeValueDto>,
        attribute: Attribute
    ): MutableSet<AttributeValue> {
        return valueDtos.map { dto ->
            mapDtoValue(dto, attribute)
        }.toHashSet()
    }

    private fun mapDtoValue(dto: AttributeValueDto, attribute: Attribute): AttributeValue {
        return when (dto) {
            is StringAttributeValueDto -> {
                getStringAttributeValueOrCreate(dto, attribute)
            }

            is NumericAttributeValueDto -> {
                getNumericAttributeValueOrCreate(dto, attribute)
            }

            is ColorAttributeValueDto -> {
                getColorAttributeValueOrCreate(dto, attribute)
            }
        }
    }

    private fun getStringAttributeValueOrCreate(
        dto: StringAttributeValueDto,
        attribute: Attribute
    ): StringAttributeValue {
        return stringValueRepository.findByStringValue(dto.stringValue)
            .orElseGet {
                StringAttributeValue(
                    attribute = attribute,
                    compositeAttributeValue = null,
                    stringRuValue = dto.stringRuValue,
                    stringValue = dto.stringValue
                )
            }
    }

    private fun getNumericAttributeValueOrCreate(
        dto: NumericAttributeValueDto,
        attribute: Attribute
    ): NumericAttributeValue {
        return numericValueRepository
            .findByNumValueAndNumRuValueAndNumUnit(dto.numValue, dto.numRuValue, dto.numUnit)
            .orElseGet {
                NumericAttributeValue(
                    attribute = attribute,
                    compositeAttributeValue = null,
                    numValue = dto.numValue,
                    numRuValue = dto.numRuValue,
                    numUnit = dto.numUnit
                )
            }
    }

    private fun getColorAttributeValueOrCreate(
        dto: ColorAttributeValueDto,
        attribute: Attribute
    ): ColorAttributeValue {
        return colorValueRepository.findByColorHex(dto.colorHex)
            .orElseGet {
                ColorAttributeValue(
                    attribute = attribute,
                    compositeAttributeValue = null,
                    colorHex = dto.colorHex,
                    colorValue = dto.colorValue
                )
            }
    }

    private fun mapCompositeDtoValuesToDomain(
        compositeDtos: List<CompositeAttributeValueDto>,
        attribute: Attribute
    ): MutableSet<CompositeAttributeValue> {
        return compositeDtos.map { dto ->
            getCompositeAttributeValueOrCreate(dto, attribute)
        }.toHashSet()
    }

    private fun getCompositeAttributeValueOrCreate(
        dto: CompositeAttributeValueDto,
        attribute: Attribute
    ): CompositeAttributeValue {
        return compositeAttributeValueRepository.findByName(dto.name)
            .orElseGet {
                val value = mapDtoValue(dto.value, attribute)
                val composite = CompositeAttributeValue(
                    name = dto.name,
                    attribute = attribute,
                    value = value
                )
                value.compositeAttributeValue = composite
                composite
            }
    }

    private fun mapDomainValuesToDto(
        values: Set<AttributeValue>
    ): List<AttributeValueDto> {
        return values.map(::mapDomainValue)
    }

    private fun mapDomainValue(value: AttributeValue): AttributeValueDto {
        val id = cryptoTool.hashOf(value.id!!)
        return when (value) {
            is ColorAttributeValue -> ColorAttributeValueDto(
                id = id,
                colorValue = value.colorValue,
                colorHex = value.colorHex
            )

            is NumericAttributeValue -> NumericAttributeValueDto(
                id = id,
                numValue = value.numValue,
                numRuValue = value.numRuValue,
                numUnit = value.numUnit
            )

            is StringAttributeValue -> StringAttributeValueDto(
                id = id,
                stringValue = value.stringValue,
                stringRuValue = value.stringRuValue
            )
        }
    }

    private fun mapCompositeDomainValuesToDto(
        values: Set<CompositeAttributeValue>
    ): List<CompositeAttributeValueDto> {
        return values.map { compositeValue ->
            CompositeAttributeValueDto(
                id = cryptoTool.hashOf(compositeValue.id!!),
                name = compositeValue.name,
                value = mapDomainValue(compositeValue.value)
            )
        }
    }
}