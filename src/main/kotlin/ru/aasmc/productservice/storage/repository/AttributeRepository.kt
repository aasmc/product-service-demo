package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.aasmc.productservice.storage.model.Attribute
import ru.aasmc.productservice.storage.model.ColorAttribute
import ru.aasmc.productservice.storage.model.NumericAttribute
import ru.aasmc.productservice.storage.model.StringAttribute
import java.util.*

interface AttributeRepository: JpaRepository<Attribute, Long> {

    fun findByName(name: String): Optional<Attribute>

    @Query("select a from Attribute a join CategoryAttribute ca on a.id = ca.attribute.id where ca.category.name = :name")
    fun findByCategoryName(@Param("name") name: String): List<Attribute>

    fun countByNameIn(names: Set<String>): Long

}

interface StringAttributeRepository: JpaRepository<StringAttribute, Long> {

    fun findByName(name: String): Optional<StringAttribute>

    @Query("select a from StringAttribute a join CategoryAttribute ca on a.id = ca.attribute.id where ca.category.name = :name")
    fun findByCategoryName(@Param("name") name: String): List<StringAttribute>

    fun countByNameIn(names: Set<String>): Long

}

interface NumericAttributeRepository: JpaRepository<NumericAttribute, Long> {

    fun findByName(name: String): Optional<NumericAttribute>

    @Query("select a from NumericAttribute a join CategoryAttribute ca on a.id = ca.attribute.id where ca.category.name = :name")
    fun findByCategoryName(@Param("name") name: String): List<NumericAttribute>

    fun countByNameIn(names: Set<String>): Long

}

interface ColorAttributeRepository: JpaRepository<ColorAttribute, Long> {

    fun findByName(name: String): Optional<ColorAttribute>

    @Query("select a from ColorAttribute a join CategoryAttribute ca on a.id = ca.attribute.id where ca.category.name = :name")
    fun findByCategoryName(@Param("name") name: String): List<ColorAttribute>

    fun countByNameIn(names: Set<String>): Long

}