package ru.aasmc.productservice.mapper

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.storage.model.*

@Component
class AttributeMappers(
    private val stringAttributeMapper: StringAttributeMapper,
    private val numericAttributeMapper: NumericAttributeMapper,
    private val colorAttributeMapper: ColorAttributeMapper,
    private val compositeAttributeMapper: CompositeAttributeMapper
) {

    fun toDomain(dto: AttributeDto): Attribute {

        return when(dto) {
            is ColorAttributeDto -> {
                colorAttributeMapper.toDomain(dto)
            }
            is NumericAttributeDto -> {
                numericAttributeMapper.toDomain(dto)
            }
            is StringAttributeDto -> {
                stringAttributeMapper.toDomain(dto)
            }

            is CompositeAttributeDto -> {
                compositeAttributeMapper.toDomain(dto)
            }
        }
    }

    fun toDto(domain: Attribute, isRequired: Boolean?): AttributeDto {
        return when(domain) {
            is StringAttribute -> {
                stringAttributeMapper.toDto(domain, isRequired)
            }
            is NumericAttribute -> {
                numericAttributeMapper.toDto(domain, isRequired)
            }
            is ColorAttribute -> {
                colorAttributeMapper.toDto(domain, isRequired)
            }
            is CompositeAttribute -> {
                compositeAttributeMapper.toDto(domain, isRequired)
            }
            else -> {
                val msg = "Unknown attribyte type while converting from domain to dto"
                throw ProductServiceException(msg, HttpStatus.INTERNAL_SERVER_ERROR.value())
            }
        }
    }
}