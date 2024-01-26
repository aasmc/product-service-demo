package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.aasmc.productservice.storage.model.CategoryAttribute
import java.util.*

interface CategoryAttributeRepository: JpaRepository<CategoryAttribute, Long> {

    fun findByAttribute_Name(name: String): Optional<CategoryAttribute>

    fun findByCategory_Name(name: String): List<CategoryAttribute>

}