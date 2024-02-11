package ru.aasmc.productservice.dto

data class ImageCollection(
    val images: MutableSet<AppImage> = hashSetOf()
)
