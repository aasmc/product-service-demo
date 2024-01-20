package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.aasmc.productservice.storage.model.Category
import java.util.*

interface CategoryRepository: JpaRepository<Category, Long> {

    fun findByName(name: String): Optional<Category>

    fun existsByName(name: String): Boolean

}