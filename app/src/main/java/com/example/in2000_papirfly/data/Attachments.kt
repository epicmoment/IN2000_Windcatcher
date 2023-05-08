package com.example.in2000_papirfly.data

import com.example.in2000_papirfly.R

object Attachments {
    val list : List<List<Attachment>> = listOf(

        // Papirtype
        listOf(
            Attachment(
                name = "Skrivepapir",
                description = "Helt vanlig papir. Passer fint til mange typer vær.",
                icon = R.drawable.paperplane2,
                flightModifier = FlightModifier(windEffect = 0.75, rainEffect = 1.0, slowRateEffect = 1.0),
            ),
            Attachment(
                name = "Fotopapir",
                description = "Tung papirtype som tåler regn bra, men flyr dårlig i sol.",
                icon = R.drawable.paperplane2,
                flightModifier = FlightModifier(windEffect = 0.25, weight = 1.0)
            ),
            Attachment(
                name = "Bakepapir",
                description = "Lett papirtype som flyr bra i sol, men tåler regn dårlig.",
                icon = R.drawable.paperplane2,
                flightModifier = FlightModifier(rainEffect = 1.0, temperatureEffect = 1.0)
            )
        ),

        // Nese
        listOf(

            Attachment(
                name = "Medium Nese",
                description = "Helt vanlig nese.",
                flightModifier = FlightModifier(slowRateEffect = 1.0)
            ),

            Attachment(
                name = "Spiss Nese",
                description = "Kan fly raskere, men mister lettere høyde",
                flightModifier = FlightModifier(weight = 0.5, slowRateEffect = 0.2)
            ),

            Attachment(
                name = "Butt Nese",
                description = "Mister mindre høyde, men flyr tregere",
                flightModifier = FlightModifier(weight = 0.1, slowRateEffect = 1.0)

            )

        ),

        // Vinger
        listOf(

            Attachment(
                name = "Medium Vinger",
                description = "Helt vanlige vinger."
            ),

            Attachment(
                name = "Smale Vinger",
                description = "Luftrykkgreier? Idk",
                flightModifier = FlightModifier(airPressureEffect = 1.0, temperatureEffect = -0.5)
            ),

            Attachment(
                name = "Brede vinger",
                description = "Lufttrykkgreir? Idk",
                flightModifier = FlightModifier(airPressureEffect = -1.0, temperatureEffect = 0.5)
            )

        ),

        // Halefinne
        listOf(

            Attachment(
                name = "Ingen Halefinne",
                description = "All motvind påvirker flyet."
            ),

            Attachment(
                name = "Liten Halefinne",
                description = "Flyet påvirkes mindre av motvind, men er litt tyngre."
            ),

            Attachment(
                name = "Stor Halefinne",
                description = "Flyet påvirkes veldig lite av motvind, men er vesentlig tyngre."
            )

        )
    )
}

data class Attachment (
    val name : String,
    val description : String,
    val icon : Int = R.drawable.paperplane2,
    val flightModifier: FlightModifier = FlightModifier(),
)