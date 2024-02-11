package ru.aasmc.productservice.storage.model

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.*
import ru.aasmc.productservice.storage.model.jsonb_data.SkuUpdate
import java.time.LocalDateTime

@Entity
@Table(name = "product_sku_update_outbox")
@org.hibernate.annotations.Immutable
class ProductSkuUpdateOutbox(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "event_timestamp")
    var eventTimestamp: LocalDateTime = LocalDateTime.now(),
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    var eventData: SkuUpdate
) {

    override fun toString(): String {
        return "ProductSkuUpdateOutbox(id=$id, eventTimestamp=$eventTimestamp, eventData=$eventData)"
    }
}