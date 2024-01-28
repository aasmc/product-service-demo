package ru.aasmc.productservice.mapper

import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.storage.model.*
import ru.aasmc.productservice.storage.repository.ColorAttributeValueRepository
import ru.aasmc.productservice.storage.repository.CompositeAttributeValueRepository
import ru.aasmc.productservice.storage.repository.NumericAttributeValueRepository
import ru.aasmc.productservice.storage.repository.StringAttributeValueRepository
import ru.aasmc.productservice.utils.CryptoTool
import java.lang.RuntimeException

@Component
class AttributeValueMapper(
    private val cryptoTool: CryptoTool,
    private val stringValueRepository: StringAttributeValueRepository,
    private val colorValueRepository: ColorAttributeValueRepository,
    private val numericValueRepository: NumericAttributeValueRepository,
    private val compositeAttributeValueRepository: CompositeAttributeValueRepository,
) {

    fun toDomain(dto: AttributeValueDto, attribute: Attribute): AttributeValue {
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


    fun toDto(value: AttributeValue): AttributeValueDto {
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

            else -> {
                throw RuntimeException("Invalid attribute value")
            }
        }
    }

    fun toDtoList(
        values: Set<AttributeValue>
    ): List<AttributeValueDto> {
        return values.map(::toDto)
    }

    fun toDomainList(
        valueDtos: List<AttributeValueDto>,
        attribute: Attribute
    ): MutableSet<AttributeValue> {
        return valueDtos.map { dto ->
            toDomain(dto, attribute)
        }.toHashSet()
    }

    fun toCompositeDto(
        compositeValue: CompositeAttributeValue
    ): CompositeAttributeValueDto = CompositeAttributeValueDto(
        id = cryptoTool.hashOf(compositeValue.id!!),
        name = compositeValue.name,
        values = toDtoList(compositeValue.value)
    )

    fun toCompositeDomain(
        dto: CompositeAttributeValueDto,
        attribute: Attribute
    ): CompositeAttributeValue =
        getCompositeAttributeValueOrCreate(dto, attribute)

    fun compositeToDtoList(
        values: Set<CompositeAttributeValue>
    ): List<CompositeAttributeValueDto> {
        return values.map(::toCompositeDto)
    }

    fun compositeToDomainList(
        compositeDtos: List<CompositeAttributeValueDto>,
        attribute: Attribute
    ): MutableSet<CompositeAttributeValue> {
        return compositeDtos.map { dto ->
            toCompositeDomain(dto, attribute)
        }.toHashSet()
    }

    private fun getCompositeAttributeValueOrCreate(
        dto: CompositeAttributeValueDto,
        attribute: Attribute
    ): CompositeAttributeValue {
        return compositeAttributeValueRepository.findByName(dto.name)
            .orElseGet {
                val values = toDomainList(dto.values, attribute)
                val composite = CompositeAttributeValue(
                    name = dto.name,
                    attribute = attribute,
                    value = values
                )
                values.forEach { it.compositeAttributeValue = composite }
                composite
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
        val opt = if (dto.numRuValue == null) {
            numericValueRepository.findByNumValueAndNumUnit(dto.numValue, dto.numUnit)
        } else {
            numericValueRepository.findByNumValueAndNumRuValueAndNumUnit(dto.numValue, dto.numRuValue, dto.numUnit)
        }
        return opt.orElseGet {
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

}