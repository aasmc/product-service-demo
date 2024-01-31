package ru.aasmc.productservice.storage.model

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import ru.aasmc.productservice.storage.model.jsonb_data.ProductUpdate
import java.time.LocalDateTime

@Entity
@Table(name = "product_update_outbox")
@org.hibernate.annotations.Immutable
class ProductUpdateOutbox(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @CreationTimestamp
    @Column(name = "event_timestamp")
    var timestamp: LocalDateTime = LocalDateTime.now(),
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    var eventData: ProductUpdate
) {

    override fun toString(): String {
        return "ProductUpdateOutbox(id=$id, timestamp=$timestamp, eventData=$eventData)"
    }
}