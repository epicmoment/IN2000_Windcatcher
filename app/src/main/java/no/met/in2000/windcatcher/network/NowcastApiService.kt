package no.met.in2000.windcatcher.network

import android.util.Log
import no.met.in2000.windcatcher.data.apidata.NowcastData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*

object NowcastURL {
    private const val BASE_URL =
        "https://gw-uio.intark.uh-it.no/in2000/weatherapi/nowcast/2.0/complete?"

    fun urlBuilder(lat: Double, lon: Double): String {
        return "${BASE_URL}lat=${lat}&lon=${lon}"
    }
}

object NowcastApiService {

    /**
     * Fetches the weather at the given coordinates
     * asynchronously from the Nowcast API.
     * All coordinates will be rounded to four decimals.
     *
     * @param lat: Double representing the latitude of the position
     * @param lon: Double representing the longitude of the position
     *
     * @return Weather data for the given coordinates as a
     * NowcastData-object
     */
    suspend fun getNowcastData(lat: Double, lon: Double): NowcastData {
        val roundedLat = kotlin.math.round(lat * 10000.0) / 10000.0
        val roundedLon = kotlin.math.round(lon * 10000.0) / 10000.0
        val response: HttpResponse

        val nowcastClient = HttpClient(Android) {
            install(ContentNegotiation) {
                json()
            }
            install(UserAgent) {
                agent = "Team 03"
            }
        }

        try {
            response = nowcastClient.get(NowcastURL.urlBuilder(roundedLat, roundedLon)) {
                headers {
                    append("X-Gravitee-Api-Key", "c473e19e-965e-4c53-8408-5c4cb9622403")
                }
            }

        } catch (cause: Throwable) {
            /*
             * This is not a very good way to catch API errors, as we do not differentiate between
             * different types of errors
             */
            Log.e("API", "No internet connection!")
            return NowcastData()
        }

        Log.i("HTTP response", "Nowcast: ${response.status.value}")

        return if (response.status.value in 200..299) {
            response.body()
        } else {
            NowcastData()
        }
    }
}