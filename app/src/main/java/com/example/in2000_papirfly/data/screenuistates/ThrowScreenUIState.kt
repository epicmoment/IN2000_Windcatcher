package com.example.in2000_papirfly.data.screenuistates

import com.example.in2000_papirfly.data.components.HighScore
import com.example.in2000_papirfly.data.components.Weather
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.defaultHighScoreShownMap
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.emptyHighScoreMap
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.emptyThrowPointWeatherList

data class ThrowScreenUIState(

    val uiState: ThrowScreenState = ThrowScreenState.Throwing,

    val throwPointWeatherList: List<Weather> = emptyThrowPointWeatherList(),

    val throwPointHighScoreMap: MutableMap<String, HighScore> = emptyHighScoreMap(),

    val highScoresShownOnMap: MutableMap<String, Boolean> = defaultHighScoreShownMap(),

    val logState: LogState = LogState(),
)

sealed interface ThrowScreenState {

    object Flying: ThrowScreenState

    object Throwing: ThrowScreenState

    object MovingMap: ThrowScreenState

    object ChoosingPosition: ThrowScreenState

    object ViewingLog: ThrowScreenState
}