
package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class Categoria(
    val id: Int,
    val nombre: String,
    val descripcion: String?,
    @SerializedName("icono_url")
    val iconoUrl: String?
)

