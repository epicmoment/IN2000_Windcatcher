package com.example.in2000_papirfly

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.in2000_papirfly.ui.theme.IN2000_PapirflyTheme
import org.osmdroid.config.Configuration
import com.example.in2000_papirfly.ui.screens.NavScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The following code is necessary for getting Open Street Map to work
        // Code based on this comment: github.com/osmdroid/osmdroid/issues/1313#issuecomment-481238741
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        // END OSM config

        //val viewModel: ScreenStateViewModel by viewModels(factoryProducer = { ScreenStateViewModel.Factory } )
        //

        val appContainer = (application as PapirflyApplication).appContainer

        setContent {
            IN2000_PapirflyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavScreen()
                    /*FlightLog(
                        distance = 120,
                        logPoints = mutableListOf(
                            Pair(GeoPoint(0.0, 0.0,), Weather(windSpeed = 20.0))
                        )
                    ) {

                    }*/
                }
            }
        }
    }
}