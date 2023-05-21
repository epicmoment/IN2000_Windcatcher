package no.met.in2000.windcatcher.data.screenuistates

import no.met.in2000.windcatcher.data.components.HighScore
import no.met.in2000.windcatcher.data.components.Weather
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.defaultHighScoreShownMap
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.emptyHighScoreMap
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.emptyThrowPointWeatherList

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