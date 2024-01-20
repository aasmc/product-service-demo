package ru.aasmc.productservice.service

import ru.aasmc.productservice.storage.model.EventType
import ru.aasmc.productservice.storage.model.Product

interface ProductOutboxService {

    fun addEvent(productId: Long, product: Product?, eventType: EventType)

}