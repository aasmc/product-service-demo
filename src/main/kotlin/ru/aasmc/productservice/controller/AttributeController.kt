package ru.aasmc.productservice.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.aasmc.productservice.dto.AttributeDto
import ru.aasmc.productservice.dto.AttributeValueDto
import ru.aasmc.productservice.dto.AttributesCollection
import ru.aasmc.productservice.service.AttributeService

@RestController
@RequestMapping("/v1/attributes")
class AttributeController(
    private val attributeService: AttributeService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAttribute(@RequestBody dto: AttributeDto): AttributeDto {
        log.info("Received POST request to create attribute: {}", dto)
        return attributeService.createAttribute(dto)
    }

    @GetMapping("/category/{categoryName}")
    fun getAttributesForCategory(
        @PathVariable("categoryName") categoryName: String
    ): AttributesCollection {
        log.info("Received request to GET all attributes for category with name={}", categoryName)
        val attrs = attributeService.getAllAttributesForCategory(categoryName)
        return AttributesCollection(attrs)
    }

    @GetMapping("/all")
    fun getAllAttributes(): AttributesCollection {
        log.info("Received request to GET all attributes")
        return AttributesCollection(attributeService.getAllAttributes())
    }

    @GetMapping("/{name}")
    fun getAttributeByName(@PathVariable("name") name: String): AttributeDto {
        log.info("Received request to GET attribute by name: {}", name)
        return attributeService.getAttributeByName(name)
    }

    @PutMapping("/{id}/value")
    fun addAttributeValue(
        @PathVariable("id") attrId: String,
        @RequestBody dto: AttributeValueDto,
    ): AttributeValueDto {
        log.info("Received PUT request to add value: {}, to attribute with ID={}", dto, attrId)
        return attributeService.addAttributeValue(attrId, dto)
    }

    companion object {
        private val log = LoggerFactory.getLogger(AttributeController::class.java)
    }
}