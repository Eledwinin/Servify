package com.example.servify.ui.componentes.feedback

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.servify.ui.theme.ServifyBluePrimary
import com.example.servify.ui.theme.ServifySurface

/**
 * Contenedor oficial y reutilizable de Pull-to-Refresh para Servify (Material 3).
 * Proporciona soporte nativo al gesto de arrastre vertical con indicador circular
 * estilizado acorde a la paleta corporativa de la aplicación.
 *
 * @param isRefreshing Estado booleano que indica si la acción de refresco está activa.
 * @param onRefresh Callback invocado cuando el usuario completa el gesto de arrastre.
 * @param modifier Modificador de diseño para el contenedor.
 * @param state Estado interno del gesto de pull-to-refresh.
 * @param content Contenido desplazable (LazyColumn o Column con verticalScroll).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServifyPullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    content: @Composable BoxScope.() -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = state,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = ServifySurface,
                color = ServifyBluePrimary
            )
        },
        content = content
    )
}
