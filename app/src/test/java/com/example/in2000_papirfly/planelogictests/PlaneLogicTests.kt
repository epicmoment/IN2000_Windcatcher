package com.example.in2000_papirfly.planelogictests

import com.example.in2000_papirfly.data.FlightModifier
import com.example.in2000_papirfly.data.LoadoutRepository
import com.example.in2000_papirfly.data.Plane
import com.example.in2000_papirfly.data.PlaneRepository
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.PlaneLogic
import junit.framework.TestCase.assertEquals
import org.junit.Test

class PlaneLogicTests {

    //      Calculate air pressure tests     //
    @Test
    fun test_calculateAirPressureDropRate_with_0_effect_in_high_pressure(){
        val loadoutRepository: LoadoutRepository = LoadoutRepositoryDummy()
        val plane: Plane = Plane(
            flightModifier = FlightModifier(airPressureEffect = 0.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic: PlaneLogic = PlaneLogic(planeRepository, loadoutRepository)

        val airPressureDropRate = planeLogic.calculateAirPressureDropRate(1030.0, planeRepository.planeState.value.flightModifier)

        val expectedValue = 0.0
        assert(airPressureDropRate == expectedValue){ println("airPressureDropRate = $airPressureDropRate, Expected $expectedValue") }
    }

    @Test
    fun test_calculateAirPressureDropRate_with_1_effect_in_high_pressure(){
        val loadoutRepository: LoadoutRepository = LoadoutRepositoryDummy()
        val plane: Plane = Plane(
            flightModifier = FlightModifier(airPressureEffect = 1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic: PlaneLogic = PlaneLogic(planeRepository, loadoutRepository)

        val airPressureDropRate = planeLogic.calculateAirPressureDropRate(1030.0, planeRepository.planeState.value.flightModifier)

        assert(airPressureDropRate < 0.0 ){ println("airPressureDropRate = $airPressureDropRate, Expected negative value") }
    }

    @Test
    fun test_calculateAirPressureDropRate_with_negative_1_effect_in_high_pressure(){
        val loadoutRepository: LoadoutRepository = LoadoutRepositoryDummy()
        val plane: Plane = Plane(
            flightModifier = FlightModifier(airPressureEffect = -1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic: PlaneLogic = PlaneLogic(planeRepository, loadoutRepository)

        val airPressureDropRate = planeLogic.calculateAirPressureDropRate(1030.0, planeRepository.planeState.value.flightModifier)

        assert(airPressureDropRate > 0.0 ){ println("airPressureDropRate = $airPressureDropRate, Expected positive value") }
    }

    @Test
    fun test_calculateAirPressureDropRate_with_negative_1_effect_in_low_pressure(){
        val loadoutRepository: LoadoutRepository = LoadoutRepositoryDummy()
        val plane: Plane = Plane(
            flightModifier = FlightModifier(airPressureEffect = -1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic: PlaneLogic = PlaneLogic(planeRepository, loadoutRepository)

        val airPressureDropRate = planeLogic.calculateAirPressureDropRate(980.0, planeRepository.planeState.value.flightModifier)

        assert(airPressureDropRate < 0.0 ){ println("airPressureDropRate = $airPressureDropRate, Expected negative value") }
    }

    //      Calculate rain drop rate tests      //
    @Test
    fun test_calculateRainDropRate_with_0_effect_in_rain(){

    }
}