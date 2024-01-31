package ru.aasmc.productservice.storage.model

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "product_outbox")
@org.hibernate.annotations.Immutable
class ProductOutbox(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "product_id")
    var productId: Long,
    @Column(name = "event_type")
    @Enumerated(value = EnumType.STRING)
    var eventType: EventType,
    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "event_timestamp")
    var eventTimestamp: LocalDateTime = LocalDateTime.now(),
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb", name = "event_data")
    var eventData: ProductEventData?
) {

    override fun toString(): String {
        return "ProductOutbox(id=$id, " +
                "productId=$productId, " +
                "eventType=$eventType, " +
                "eventTimestamp=$eventTimestamp, " +
                "eventData=$eventData)"
    }
}