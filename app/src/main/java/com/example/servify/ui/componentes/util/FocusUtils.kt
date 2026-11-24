package com.example.servify.ui.componentes.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import kotlinx.coroutines.launch

/**
 * Modificador reutilizable que asegura que al enfocar un campo de texto (Input/TextField),
 * este se desplace automáticamente hacia el área visible de la pantalla por encima del teclado (IME).
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.bringIntoViewOnFocus(): Modifier = composed {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    this
        .bringIntoViewRequester(bringIntoViewRequester)
        .onFocusEvent { focusState ->
            if (focusState.isFocused) {
                coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            }
        }
}
