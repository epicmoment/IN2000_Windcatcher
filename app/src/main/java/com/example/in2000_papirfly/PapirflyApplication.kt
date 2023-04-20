package com.example.in2000_papirfly

import android.app.Application
import androidx.room.Room
import com.example.in2000_papirfly.data.AppContainer
import com.example.in2000_papirfly.data.database.PapirflyDatabase

class PapirflyApplication : Application() {

    var appContainer = AppContainer()
}