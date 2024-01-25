package ru.aasmc.productservice.controller

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import ru.aasmc.productservice.BaseIntegTest
import ru.aasmc.productservice.dto.CategoryResponse
import ru.aasmc.productservice.storage.repository.CategoryRepository
import ru.aasmc.productservice.testdata.*
import ru.aasmc.productservice.utils.CryptoTool
import java.time.LocalDateTime

class AttributeControllerTest @Autowired constructor (
    private val cryptoTool: CryptoTool,
    private val categoryRepository: CategoryRepository
): BaseIntegTest() {

    @Test
    fun testCreateSubCategory() {
        var topLevel = topLevelCategoryDomain()
        topLevel = categoryRepository.save(topLevel)
        val topLevelIdStr = cryptoTool.hashOf(topLevel.id!!)

        val request = subCategoryRequestWithAttributesToCreate(topLevelIdStr)
        val now = LocalDateTime.now()

        webTestClient.post()
            .uri(BASE_CATEGORIES_URL)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CategoryResponse::class.java)
            .value { response ->
                assertThat(response.categoryId).isNotNull()
                assertThat(response.name).isEqualTo(TEST_SUBCATEGORY_NAME)
                assertThat(response.parentId).isEqualTo(topLevelIdStr)
                assertThat(response.subcategoryNames).isEmpty()
                assertThat(response.createdAt).isNotNull()
                assertThat(response.createdAt).isAfter(now)
                assertThat(response.attributes).hasSize(2)
            }
    }

    @Test
    fun testCreateTopLevelCategory() {
        val topLevelRequest = topLevelCategoryRequest()
        val now = LocalDateTime.now()
        webTestClient.post()
            .uri(BASE_CATEGORIES_URL)
            .bodyValue(topLevelRequest)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CategoryResponse::class.java)
            .value { response ->
                assertThat(response.categoryId).isNotNull()
                assertThat(response.name).isEqualTo(TEST_TOP_LEVEL_CATEGORY_NAME)
                assertThat(response.subcategoryNames).isEmpty()
                assertThat(response.createdAt).isAfter(now)
            }
    }

    @Test
    fun contextLoads() {

    }



}