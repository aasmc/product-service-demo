package ru.aasmc.productservice.storage.model

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "product_variant_outbox")
@org.hibernate.annotations.Immutable
class ProductVariantOutbox(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "variant_id", nullable = false)
    var variantId: Long,
    @Column(name = "product_id", nullable = false)
    var productId: Long,
    @Column(name = "event_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    var eventType: EventType,
    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "event_timestamp")
    var eventTimeStamp: LocalDateTime = LocalDateTime.now(),
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(name = "event_data", columnDefinition = "jsonb")
    var eventData: ProductVariantEventData? = null
) {

    override fun toString(): String {
        return "ProductVariantOutbox(id=$id, " +
                "variantId=$variantId, " +
                "productId=$productId, " +
                "eventTimeStamp=$eventTimeStamp," +
                " eventData=$eventData)"
    }
}