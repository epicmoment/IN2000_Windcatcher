package com.example.in2000_papirfly

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.example.in2000_papirfly.data.AppContainer
import com.example.in2000_papirfly.data.database.PapirflyDatabase

class PapirflyApplication : Application() {

    val database by lazy {
        Room.databaseBuilder(
            this,
            PapirflyDatabase::class.java,
            "papirflydatabase"
        ).build()
    }
    val appContainer by lazy { AppContainer(database) }
}