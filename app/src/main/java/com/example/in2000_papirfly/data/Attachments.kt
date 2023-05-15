package com.example.in2000_papirfly.data

import com.example.in2000_papirfly.R

object Attachments {
    val list : List<List<Attachment>> = listOf(

        // Papirtype
        listOf(
            Attachment(
                name = "Skrivepapir",
                description = "Påvirkes av vind og regn.",
                icon = R.drawable.paperplane2,
                flightModifier = FlightModifier(windEffect = 0.75, rainEffect = 1.0, slowRateEffect = 1.0),
            ),
            Attachment(
                name = "Fotopapir",
                description = "Tung papirtype som flyr bedre i regn.",
                icon = R.drawable.paperplane2,
                flightModifier = FlightModifier(windEffect = 0.25, weight = 1.0, rainEffect = -1.0)
            ),
            Attachment(
                name = "Bakepapir",
                description = "Lett papirtype som flyr bedre i høye temperaturer, men tåler regn dårlig.",
                icon = R.drawable.paperplane2,
                flightModifier = FlightModifier(rainEffect = 1.0, temperatureEffect = 1.0)
            )
        ),

        // Nese
        listOf(

            Attachment(
                name = "Medium Nese",
                description = "Helt vanlig nese.",
                icon = R.drawable.attachmentnosenormal,
                flightModifier = FlightModifier(slowRateEffect = 1.0)
            ),

            Attachment(
                name = "Spiss Nese",
                description = "Kan fly raskere, men mister lettere høyde",
                icon = R.drawable.attachmentnosenarrow,
                flightModifier = FlightModifier(weight = 0.5, slowRateEffect = 0.1)
            ),

            Attachment(
                name = "Butt Nese",
                description = "Mister mindre høyde, men flyr tregere",
                icon = R.drawable.attachmentempty,
                flightModifier = FlightModifier(weight = 0.1, slowRateEffect = 1.0)

            )

        ),

        // Vinger
        listOf(

            Attachment(
                name = "Medium Vinger",
                description = "Helt vanlige vinger.",
                icon = R.drawable.attachmentwingsnormal
            ),

            Attachment(
                name = "Smale Vinger",
                description = "Flyr bedre i lavt lufttrykk",
                icon = R.drawable.attachmentwingsnarrow,
                flightModifier = FlightModifier(airPressureEffect = 1.0)
            ),

            Attachment(
                name = "Brede vinger",
                description = "Flyr bedre i høyt lufttrykk",
                icon = R.drawable.attachmentwingswide,
                flightModifier = FlightModifier(airPressureEffect = -1.0)
            )

        ),

        // Halefinne
        listOf(

            Attachment(
                name = "Ingen Halefinne",
                description = "Påvirkes mye av vind.",
                icon = R.drawable.attachmentempty,
                flightModifier = FlightModifier(windEffect = 0.5)
            ),

            Attachment(
                name = "Liten Halefinne",
                description = "Påvirkes mindre av vind.",
                icon = R.drawable.attachmentfinsmall,
                flightModifier = FlightModifier(windEffect = 0.25, slowRateEffect = -0.1)
            ),

            Attachment(
                name = "Stor Halefinne",
                description = "Påvirkes lite av vind.",
                icon = R.drawable.attachmentfinlarge,
                flightModifier = FlightModifier(windEffect = 0.1, slowRateEffect = -0.2)
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