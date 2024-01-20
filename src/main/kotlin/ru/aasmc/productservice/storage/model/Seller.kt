package ru.aasmc.productservice.storage.model

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize

@Entity
@Table(name = "sellers")
class Seller(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "first_name", nullable = false)
    var firstName: String,
    @Column(name = "last_name", nullable = false)
    var lastName: String,
    @OneToMany(mappedBy = "seller")
    @BatchSize(size = 10)
    val shops: MutableSet<Shop> = hashSetOf()
) {

    override fun toString(): String {
        return "Seller(id=$id, firstName='$firstName', lastName='$lastName')"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? Seller ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}