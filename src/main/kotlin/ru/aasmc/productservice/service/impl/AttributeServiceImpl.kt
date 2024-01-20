package ru.aasmc.productservice.service.impl

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.AttributeResponse
import ru.aasmc.productservice.dto.CreateAttributeRequest
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.mapper.AttributeMapper
import ru.aasmc.productservice.service.AttributeService
import ru.aasmc.productservice.storage.repository.AttributeRepository

@Service
@Transactional
class AttributeServiceImpl(
    private val mapper: AttributeMapper,
    private val attributeRepository: AttributeRepository
) : AttributeService {

    override fun createAttribute(dto: CreateAttributeRequest): AttributeResponse {
        val attribute = attributeRepository.save(mapper.toDomain(dto))
        log.debug("Successfully created attribute: {}", attribute)
        return mapper.toDto(attribute)
    }

    override fun getAllAttributesForCategory(categoryName: String): List<AttributeResponse> {
        val attributes = attributeRepository.findByCategoryName(categoryName)
        log.debug(
            "Retrieved all attributes for category with name = {}. Attributes: {}",
            categoryName,
            attributes
        )
        return attributes.map(mapper::toDto)
    }

    override fun getAllAttributes(): List<AttributeResponse> {
        return attributeRepository.findAll().map(mapper::toDto)
    }

    override fun getAttributeByName(name: String): AttributeResponse {
        val attr = attributeRepository.findByName(name)
            .orElseThrow {
                val msg = "Attribute with name=$name not found"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        return mapper.toDto(attr)
    }

    companion object {
        private val log = LoggerFactory.getLogger(AttributeServiceImpl::class.java)
    }
}