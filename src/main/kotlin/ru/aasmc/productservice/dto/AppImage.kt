package ru.aasmc.productservice.dto

data class AppImage(
    val url: String,
    val isPrimary: Boolean = false
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppImage

        return url == other.url
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }
}
