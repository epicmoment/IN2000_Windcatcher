package com.example.in2000_papirfly.planelogictests

import com.example.in2000_papirfly.data.components.FlightModifier
import com.example.in2000_papirfly.data.components.Plane
import com.example.in2000_papirfly.data.repositories.PlaneRepository
import com.example.in2000_papirfly.data.components.Weather
import com.example.in2000_papirfly.helpers.WeatherConstants
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.PlaneLogic
import org.junit.Test


/**
 * These test cases are based on the documentation of the different modifiers in the Plane.kt file
 */
class PlaneLogicTests {

    //      calculateAirPressureDropRate tests     //
    @Test
    fun test_calculateAirPressureDropRate_with_0_effect_in_high_pressure(){
        val plane = Plane(
            flightModifier = FlightModifier(airPressureEffect = 0.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val airPressureDropRate = planeLogic.calculateAirPressureDropRate(1030.0, planeRepository.planeState.value.flightModifier)

        val expectedValue = 0.0
        assert(airPressureDropRate == expectedValue){ println("airPressureDropRate = $airPressureDropRate, Expected $expectedValue") }
    }

    @Test
    fun test_calculateAirPressureDropRate_with_1_effect_in_high_pressure(){
        val plane = Plane(
            flightModifier = FlightModifier(airPressureEffect = 1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val airPressureDropRate = planeLogic.calculateAirPressureDropRate(1030.0, planeRepository.planeState.value.flightModifier)

        assert(airPressureDropRate < 0.0 ){ println("airPressureDropRate = $airPressureDropRate, Expected negative value") }
    }

    @Test
    fun test_calculateAirPressureDropRate_with_negative_1_effect_in_high_pressure(){
        val plane = Plane(
            flightModifier = FlightModifier(airPressureEffect = -1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val airPressureDropRate = planeLogic.calculateAirPressureDropRate(1030.0, planeRepository.planeState.value.flightModifier)

        assert(airPressureDropRate > 0.0 ){ println("airPressureDropRate = $airPressureDropRate, Expected positive value") }
    }

    @Test
    fun test_calculateAirPressureDropRate_with_negative_1_effect_in_low_pressure(){
        val plane = Plane(
            flightModifier = FlightModifier(airPressureEffect = -1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val airPressureDropRate = planeLogic.calculateAirPressureDropRate(980.0, planeRepository.planeState.value.flightModifier)

        assert(airPressureDropRate < 0.0 ){ println("airPressureDropRate = $airPressureDropRate, Expected negative value") }
    }


    //      calculateRainDropRate tests      //
    @Test
    fun test_calculateRainDropRate_with_0_effect_in_rain(){
        val plane = Plane(
            flightModifier = FlightModifier(rainEffect = 0.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val rainDropRate = planeLogic.calculateRainDropRate(WeatherConstants.RAIN_MAX, planeRepository.planeState.value.flightModifier)

        assert(rainDropRate == 0.0 ){ println("rainDropRate = $rainDropRate, Expected 0.0") }
    }

    @Test
    fun test_calculateRainDropRate_with_1_effect_in_no_rain(){
        val plane = Plane(
            flightModifier = FlightModifier(rainEffect = 1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val rainDropRate = planeLogic.calculateRainDropRate(0.0, planeRepository.planeState.value.flightModifier)

        assert(rainDropRate == 0.0 ){ println("rainDropRate = $rainDropRate, Expected 0.0") }
    }

    @Test
    fun test_calculateRainDropRate_with_1_effect_in_rain(){
        val plane = Plane(
            flightModifier = FlightModifier(rainEffect = 1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val rainDropRate = planeLogic.calculateRainDropRate(WeatherConstants.RAIN_MAX, planeRepository.planeState.value.flightModifier)

        assert(rainDropRate > 0.0 ){ println("rainDropRate = $rainDropRate, Expected positive value") }
    }

    @Test
    fun test_calculateRainDropRate_with_negative_1_effect_in_rain(){
        val plane = Plane(
            flightModifier = FlightModifier(rainEffect = -1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val rainDropRate = planeLogic.calculateRainDropRate(WeatherConstants.RAIN_MAX, planeRepository.planeState.value.flightModifier)

        assert(rainDropRate < 0.0 ){ println("rainDropRate = $rainDropRate, Expected negative value") }
    }


    //      calculateTemperatureDropRate        //
    @Test
    fun test_calculateTemperatureDropRate_with_0_effect_in_positive_temperature(){
        val plane = Plane(
            flightModifier = FlightModifier(temperatureEffect = 0.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val temperatureDropRate = planeLogic.calculateRainDropRate(WeatherConstants.TEMPERATURE_MAX, planeRepository.planeState.value.flightModifier)

        assert(temperatureDropRate == 0.0 ){ println("temperatureDropRate = $temperatureDropRate, Expected 0.0") }
    }

    @Test
    fun test_calculateTemperatureDropRate_with_1_effect_in_positive_temperature(){
        val plane = Plane(
            flightModifier = FlightModifier(temperatureEffect = 1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val temperatureDropRate = planeLogic.calculateTemperatureDropRate(WeatherConstants.TEMPERATURE_MAX, planeRepository.planeState.value.flightModifier)

        assert(temperatureDropRate < 0.0 ){ println("temperatureDropRate = $temperatureDropRate, Expected negative value") }
    }

    @Test
    fun test_calculateTemperatureDropRate_with_negative_1_effect_in_positive_temperature(){
        val plane = Plane(
            flightModifier = FlightModifier(temperatureEffect = -1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val temperatureDropRate = planeLogic.calculateTemperatureDropRate(WeatherConstants.TEMPERATURE_MAX, planeRepository.planeState.value.flightModifier)

        assert(temperatureDropRate > 0.0 ){ println("temperatureDropRate = $temperatureDropRate, Expected positive value") }
    }

    @Test
    fun test_calculateTemperatureDropRate_with_negative_1_effect_in_negative_temperature(){
        val plane = Plane(
            flightModifier = FlightModifier(temperatureEffect = -1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val temperatureDropRate = planeLogic.calculateTemperatureDropRate(WeatherConstants.TEMPERATURE_MIN, planeRepository.planeState.value.flightModifier)

        assert(temperatureDropRate < 0.0 ){ println("temperatureDropRate = $temperatureDropRate, Expected negative value") }
    }

    @Test
    fun test_calculateTemperatureDropRate_with_1_effect_in_negative_temperature(){
        val plane = Plane(
            flightModifier = FlightModifier(temperatureEffect = 1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val temperatureDropRate = planeLogic.calculateTemperatureDropRate(WeatherConstants.TEMPERATURE_MIN, planeRepository.planeState.value.flightModifier)

        assert(temperatureDropRate > 0.0 ){ println("temperatureDropRate = $temperatureDropRate, Expected positive value") }
    }



    //      calculateDropRate tests     //
    @Test
    fun test_calculateDropRate_only_weight_1(){
        val plane = Plane(
            flightModifier = FlightModifier(weight = 1.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val weightDropRate = planeLogic.calculateDropRate(0.0, Weather())

        assert(weightDropRate > 0.0 ){ println("temperatureDropRate = $weightDropRate, Expected positive value") }
    }

    @Test
    fun test_calculateDropRate_only_weight_0(){
        val plane = Plane(
            flightModifier = FlightModifier(weight = 0.0)
        )
        val planeRepository: PlaneRepository = PlaneRepositoryDummy(plane)
        val planeLogic = PlaneLogic(planeRepository)

        val weightDropRate = planeLogic.calculateDropRate(0.0, Weather())

        assert(weightDropRate == 0.0 ){ println("temperatureDropRate = $weightDropRate, Expected 0.0") }
    }


    //      Help methods        //

}