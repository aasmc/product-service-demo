package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.aasmc.productservice.storage.model.Shop
import java.util.*

interface ShopRepository: JpaRepository<Shop, Long> {

    @Query("select s from Shop s join fetch s.seller seller where s.id = :id")
    fun findShopByIdWithSeller(@Param("id") id: Long): Optional<Shop>

}