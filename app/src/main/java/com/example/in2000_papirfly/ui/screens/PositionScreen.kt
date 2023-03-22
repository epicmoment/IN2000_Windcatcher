package com.example.in2000_papirfly.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.in2000_papirfly.ui.viewModels.PositionScreenViewModel

@Composable
fun PositionScreen(
    viewModel: PositionScreenViewModel = viewModel(),

    val posScrUiState by viewModel.posScrUiState.collectAsStateWithLifecycle()
}