package no.met.in2000.windcatcher

import android.app.Application
import androidx.room.Room
import no.met.in2000.windcatcher.data.AppContainer
import no.met.in2000.windcatcher.data.database.WindcatcherDatabase

class WindcatcherApplication : Application() {

    val database by lazy {
        Room.databaseBuilder(
            this,
            WindcatcherDatabase::class.java,
            "papirflydatabase"
        ).build()
    }
    val appContainer by lazy { AppContainer(database) }

}