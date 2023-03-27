package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.in2000_papirfly.data.Location
import com.example.in2000_papirfly.ui.viewmodels.PositionScreenViewModel

@Composable
fun PositionScreen(
    viewModel: PositionScreenViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onNextPage : (Location) -> Unit
) {

    val posScrUiState = viewModel.posScrUiState.collectAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = modifier
            .fillMaxSize()) {
            //items(posScrUiState.){
            //}
        }
    }
}