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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.in2000_papirfly.ui.screens.NavScreen
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.MapView
import com.example.in2000_papirfly.ui.theme.IN2000_PapirflyTheme
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint


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

        setContent {
            IN2000_PapirflyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavScreen()
                    /*
                    MapView(
                        location = GeoPoint(59.9441, 10.7191)
                    )
                    */
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IN2000_PapirflyTheme {

    }
}