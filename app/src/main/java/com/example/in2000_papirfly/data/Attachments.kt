package com.example.in2000_papirfly.data

object Attachments {
    val list : List<List<Attachment>> = listOf(

        listOf(
            Attachment(
                name = "Skrivepapir",
                description = "Helt vanlig papir. Medium vekt, tåler regn dårlig. Lorem ipsum dolor sit amet.",
                flightModifier = FlightModifier(windEffect = 0.75, rainEffect = 1.0),
            ),
            Attachment(
                name = "Fotopapir",
                description = "Tung papirtype som tåler regn bra. Lorem ipsum dolor sit amet.",
                flightModifier = FlightModifier(windEffect = 0.25, rainEffect = 0.2, temperatureEffect = 1.0)
            ),
            Attachment(
                name = "Annet papir",
                description = "En eller annen type idk. Lorem ipsum dolor sit amet.",
                flightModifier = FlightModifier()
            ),
        ),

        listOf(

        ),

        listOf(

        ),

        listOf(

        )

    )
}

data class Attachment (
    val name : String,
    val description : String,
    val flightModifier: FlightModifier = FlightModifier(),
)