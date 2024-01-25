package ru.aasmc.productservice.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.aasmc.productservice.BaseIntegTest
import ru.aasmc.productservice.utils.CryptoTool

class AttributeControllerTest @Autowired constructor (
    private val cryptoTool: CryptoTool
): BaseIntegTest() {

    @Test
    fun testCreateProduct() {

    }

    @Test
    fun contextLoads() {

    }


}