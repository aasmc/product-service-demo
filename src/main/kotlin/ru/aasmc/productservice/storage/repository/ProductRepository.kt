package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.aasmc.productservice.storage.model.Product
import java.util.*

interface ProductRepository: JpaRepository<Product, Long> {

    @Query("select p from Product p join fetch p.category cat join fetch p.shop shop where p.id = :id")
    fun findProductByIdWithShopAndCategory(@Param("id") id: Long): Optional<Product>

}