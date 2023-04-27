package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.R
import com.example.in2000_papirfly.data.Attachment
import com.example.in2000_papirfly.data.Attachments
import com.example.in2000_papirfly.ui.viewmodels.CustomizationViewModel


val colOrange = Color(224, 128, 37)
val colBlue = Color(92, 121, 148)
val colOrangeHighlightTransparent = Color(224, 128, 37, 175)
val colOrangeHighlight = Color(114, 84, 52)
val colBlack = Color(30, 45, 60)
val colTextGray = Color(195, 195, 195)


@Composable
fun CustomizationScreen (
    onNextPage: () -> Unit,
    viewModel: CustomizationViewModel = viewModel(factory = (LocalContext.current.applicationContext as PapirflyApplication).appContainer.customizationViewModelFactory)
) {

    val appContainer = (LocalContext.current.applicationContext as PapirflyApplication)

    val customizeState = viewModel.customizeState.collectAsState()
    val loadoutState = viewModel.loadoutState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colBlue)
            .paint(
                painter = painterResource(id = R.drawable.blueprint_bg),
                contentScale = ContentScale.Crop

            )
    ) {

        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            // Plane
            Box (
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxHeight(0.43f)
            ) {
                
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.paperplane2),
                        contentDescription = "Paper plane",
                        modifier = Modifier.fillMaxSize(0.62f)
                    )

                    Column() {

                        AttachmentButton(isSelected = customizeState.value.selectedSlot == 1) { viewModel.setSlot(1) }
                        AttachmentButton(isSelected = customizeState.value.selectedSlot == 2) { viewModel.setSlot(2) }
                        AttachmentButton(isSelected = customizeState.value.selectedSlot == 3) { viewModel.setSlot(3) }
                        AttachmentButton(isSelected = customizeState.value.selectedSlot == 4) { viewModel.setSlot(4) }

                    }

                }

            }

            Spacer(modifier = Modifier.height(10.dp))

            // Valgliste
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.93f)
                    .fillMaxHeight()
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0, 0, 0, 100))
            ) {

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(colBlack)
                    ) {

                        Text(
                            text = when (customizeState.value.selectedSlot) {
                                1 -> "PAPIRTYPE"
                                2 -> "KATEGORI 2"
                                3 -> "KATEGORI 3"
                                else -> "KATEGORI 4"
                            },
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold

                        )

                    }

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 20.dp),
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.92f)
                    ) {

                        items(
                            count = Attachments.list[customizeState.value.selectedSlot - 1].size
                        ) {

                            Box(
                                modifier = Modifier.padding(0.dp)
                            ) {
                                AttachmentCard(
                                    attachment = Attachments.list[customizeState.value.selectedSlot - 1][it],
                                    isSelected = when (customizeState.value.selectedSlot) {
                                        1 -> it == loadoutState.value.slot1attachment
                                        2 -> it == loadoutState.value.slot2attachment
                                        3 -> it == loadoutState.value.slot3attachment
                                        else -> it == loadoutState.value.slot4attachment
                                    },
                                    onClickEquip = {
                                        viewModel.equipAttachment(customizeState.value.selectedSlot, it)
                                    }
                                )

                            }

                        }


                    }


                }

            }

            
            // Bruk-knapp
            Box (
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {

                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(0.7f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colOrange)
                        .clickable(onClick = onNextPage)
                ) {

                    Text(
                        text = "BRUK",
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )

                }

            }

        }

    }

}

@Composable
fun AttachmentButton(isSelected : Boolean, onClickSetSlot : () -> Unit) {

    Box(
        modifier = Modifier
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(65.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    color = if (isSelected) {
                        colOrangeHighlightTransparent
                    } else {
                        Color(0, 0, 0, 150)
                    }
                )
                .clickable(onClick = onClickSetSlot)
        )

    }
    
}

@Composable
fun AttachmentCard (attachment: Attachment, isSelected: Boolean, onClickEquip : () -> Unit) {

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = if (isSelected) {
                    colOrangeHighlight
                } else {
                    colBlack
                }
            )
            .clickable(onClick = onClickEquip)
    ) {

        Row {

            Box (
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.20f)
            ){
                Image(
                    painter = painterResource(id = R.drawable.paperplane2),
                    contentDescription = "Attachment Icon",
                    modifier = Modifier.fillMaxSize(0.85f)
                )
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = attachment.name.uppercase(),
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = attachment.description,
                    color = colTextGray,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp
                )
            }

        }


    }

}