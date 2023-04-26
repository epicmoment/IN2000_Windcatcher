package com.example.in2000_papirfly.data

object Attachments {
    val list : List<List<Attachment>> = listOf(

        listOf(
            Attachment(name = "Skrivepapir", description = "Helt vanlig papir. Medium vekt, tåler regn dårlig. Lorem ipsum dolor sit amet."),
            Attachment(name = "Fotopapir", description = "Tung papirtype som tåler regn bra. Lorem ipsum dolor sit amet."),
            Attachment(name = "Annet papir", description = "En eller annen type idk. Lorem ipsum dolor sit amet.")
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
    val description : String
)