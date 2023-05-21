package no.met.in2000.windcatcher.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.met.in2000.windcatcher.R
import no.met.in2000.windcatcher.ui.composables.PlaneProvider
import no.met.in2000.windcatcher.ui.theme.colRed

@Composable
fun MainScreen(onNextPage : () -> Unit, onCustomizePage : () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bakgrunn_kart),
                contentScale = ContentScale.Crop,
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .size(size = 250.dp)
            ) {
                PlaneProvider()
            }

            Button (
                modifier = Modifier.shadow(
                    elevation = 10.dp,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                ),
                onClick = onNextPage,
                colors = ButtonDefaults.buttonColors(colRed),
                shape = RoundedCornerShape(20),
            ) {
                Text(
                    text = stringResource(R.string.start).uppercase(),
                    fontSize = 40.sp,
                    color = Color.White
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Button (
                    onClick = onCustomizePage,
                    colors = ButtonDefaults.buttonColors(colRed),
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 0.dp,
                            ambientColor = Color.Black,
                            spotColor = Color.Black
                        ),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.construction),
                        contentDescription = stringResource(R.string.customize_page_description),
                        modifier = Modifier.size(size = 30.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}