package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.aasmc.productservice.storage.model.Attribute
import java.util.*

interface AttributeRepository: JpaRepository<Attribute, Long> {

    fun findByName(name: String): Optional<Attribute>

    @Query("select a from Attribute a join CategoryAttribute ca on a.id = ca.attribute.id where ca.category.name = :name")
    fun findByCategoryName(@Param("name") name: String): List<Attribute>

    fun countByNameIn(names: Set<String>): Long

}