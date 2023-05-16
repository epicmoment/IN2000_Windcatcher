package com.example.in2000_papirfly.data

import androidx.compose.ui.graphics.Color
import com.example.in2000_papirfly.R

object Attachments {

    val list : List<List<Attachment>> = listOf(

        // Papirtype
        listOf(
            Attachment(
                name = R.string.papertype_01,
                description = R.string.papertype_01_desctiption,
                icon = R.drawable.attachmentpaperoffice,
                flightModifier = FlightModifier(windEffect = 0.75, rainEffect = 1.0, slowRateEffect = 1.0),
            ),
            Attachment(
                name = R.string.papertype_02,
                description = R.string.papertype_02_description,
                icon = R.drawable.attachmentpaperphoto,
                flightModifier = FlightModifier(windEffect = 0.57, weight = 0.5, rainEffect = -1.0)
            ),
            Attachment(
                name = R.string.papertype_03,
                description = R.string.papertype_03_description,
                icon = R.drawable.attachmentpaperbake,
                flightModifier = FlightModifier(windEffect = 0.18, rainEffect = 1.19, temperatureEffect = 1.0),
                tint = Color(211, 176, 139)
            )
        ),

        // Nese
        listOf(

            Attachment(
                name = R.string.nose_01,
                description = R.string.nose_01_description,
                icon = R.drawable.attachmentnosenormal,
                flightModifier = FlightModifier(slowRateEffect = 1.0)
            ),

            Attachment(
                name = R.string.nose_02,
                description = R.string.nose_02_description,
                icon = R.drawable.attachmentnosenarrow,
                flightModifier = FlightModifier(weight = 0.5, slowRateEffect = 0.1)
            ),

            Attachment(
                name = R.string.nose_03,
                description = R.string.nose_03_description,
                icon = R.drawable.attachmentempty,
                flightModifier = FlightModifier(weight = 0.1, slowRateEffect = 1.0)

            )

        ),

        // Vinger
        listOf(

            Attachment(
                name = R.string.wing_01,
                description = R.string.wing_01_description,
                icon = R.drawable.attachmentwingsnormal
            ),

            Attachment(
                name = R.string.wing_02,
                description = R.string.wing_02_description,
                icon = R.drawable.attachmentwingsnarrow,
                flightModifier = FlightModifier(airPressureEffect = -1.0)
            ),

            Attachment(
                name = R.string.wing_03,
                description = R.string.wing_03_description,
                icon = R.drawable.attachmentwingswide,
                flightModifier = FlightModifier(airPressureEffect = 1.0)
            )

        ),

        // Halefinne
        listOf(

            Attachment(
                name = R.string.tail_01,
                description = R.string.tail_01_description,
                icon = R.drawable.attachmentempty,
                flightModifier = FlightModifier(windEffect = 0.5)
            ),

            Attachment(
                name = R.string.tail_02,
                description = R.string.tail_02_description,
                icon = R.drawable.attachmentfinsmall,
                flightModifier = FlightModifier(windEffect = 0.25, slowRateEffect = -0.1)
            ),

            Attachment(
                name = R.string.tail_03,
                description = R.string.tail_03_description,
                icon = R.drawable.attachmentfinlarge,
                flightModifier = FlightModifier(windEffect = 0.1, slowRateEffect = -0.2)
            )

        )
    )
}

data class Attachment (
    val name : Int,
    val description : Int,
    val icon : Int = R.drawable.paperplane2,
    val flightModifier: FlightModifier = FlightModifier(),
    val tint : Color = Color(255, 255, 255)
)