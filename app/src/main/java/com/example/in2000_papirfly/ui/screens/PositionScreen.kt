package com.example.in2000_papirfly.ui.screens

import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.in2000_papirfly.ui.viewModels.PositionScreenViewModel

@Composable
fun PositionScreen(onNextPage: (Location) -> Unit,
    viewModel: PositionScreenViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {

    val posScrUiState by viewModel.posScrUiState.collectAsStateWithLifecycle()

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = modifier
            .fillMaxSize()) {
            items(posScrUiState.){
            }
        }
    }
}