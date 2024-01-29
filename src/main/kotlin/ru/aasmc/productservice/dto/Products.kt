package ru.aasmc.productservice.dto

import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.properties.Delegates

/**
 * Запрос, приходящий с фронта на создание товара.
 * Содержит общие для товара характеристики:
 * - [shopId] идентификатор магазина, в котором создается товар
 * - [categoryName] название категории, которой принадлежит товар (самая нижнеуровневая категория)
 * - [name] название товара
 * - [description] общее описание товара
 * - [variants] список вариантов товара
 */
data class CreateProductRequest(
    val shopId: String,
    val categoryName: String,
    val name: String,
    val description: String,
    val variants: Set<ProductVariantRequestDto>
)

/**
 * Обозначает вариант товара.
 * - [variantName] - название варианта, например, Футболка синяя, или Футболка светло-желтая.
 * - [price] - цена данного варианта товара.
 * - [attributeCollection] - коллекция атрибутов, присущих данному варианту товара, например,
 *                          список цветов, размеров, габариты и т.п.
 * - [skuCollection] - коллекция уникальных идентификаторов единиц товара, которая используется
 *                     для отслеживания наличия конкретной единицы товара на складе.
 *                     Также для каждой конкретной единицы товара может быть переопределена ее
 *                     стоимость, в этом случае при переходе на карточку товара этой единицы,
 *                     стоимость будет указана отсюда, а не из поля [ProductVariantRequestDto.price]
 * - [images] - коллекция изображений варианта товара, в которой одно из изображений должно быть
 *              помечено как основное.
 */
data class ProductVariantRequestDto(
    val variantName: String,
    val price: BigDecimal,
    val attributeCollection: AttributeCollection,
    val skuCollection: SkuCollection,
    val images: ImageCollection
)

/**
 * Обозначает уникальный идентификатор единицы товара - sku (stock keeping unit).
 * [attrValue] - значение атрибута, который является определяющим для конкретной единицы товара,
 *              например, для одежды это будет размер: Футболка синяя со следующимиразмерами:
 *              XS, S, M, L, XL - значит на складе есть 5 единиц товара типа футболка синяя.
 *              Для других категорий, атрибут может отличаться. Например, для категории
 *              Украшения -> Цепочки, это может быть длина цепочки: 10см, 20см, 30см и т.п.
 *              Этот параметр будет обязательным для заполнения на фронте продавцом при создании карточки товара
 * [sku]      - сам уникальный идентификатор, который может быть сформирован по различным принципам, главное -
 *              он должен быть уникальным в системе и быть понятным продавцу
 * [price]    - цена данной единицы товара
 * [stock]    - количество данных единиц товара у продавца
 */
data class Sku(
    val attrValue: String,
    val sku: String,
    val price: BigDecimal,
    val stock: Int,
)

data class SkuCollection(
    // нащвание атрибута, который является определяющим для конкретной единицы товара
    val attrName: String,
    val skus: List<Sku>
)

data class ProductResponse(
    val productId: String,
    val shopId: String,
    val categoryName: String,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
    val variants: List<ProductVariantResponse>
)

data class ProductVariantResponse(
    val productId: String,
    val variantId: String,
    val variantName: String,
    val price: BigDecimal,
    val attributesCollection: AttributeCollection,
    val skuCollection: SkuCollection,
    val images: ImageCollection,
    val createdAt: LocalDateTime
)