package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class TecnicoCategoria(
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("categoria_id") val categoriaId: Int
)