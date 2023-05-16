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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.R
import com.example.in2000_papirfly.data.Attachment
import com.example.in2000_papirfly.data.Attachments
import com.example.in2000_papirfly.ui.composables.PlaneRender
import com.example.in2000_papirfly.ui.viewmodels.CustomizationViewModel
import com.example.in2000_papirfly.ui.theme.colRed
import com.example.in2000_papirfly.ui.theme.colDarkBlue

val colOrangeHighlightTransparent = Color(127, 44, 36, 175)
val colOrangeHighlight = Color(102, 51, 46)
val colTextGray = Color(195, 195, 195)


@Composable
fun CustomizationScreen (
    onNextPage: () -> Unit,
    viewModel: CustomizationViewModel = viewModel(factory = (LocalContext.current.applicationContext as PapirflyApplication).appContainer.customizationViewModelFactory)
) {

    //val appContainer = (LocalContext.current.applicationContext as PapirflyApplication).appContainer

    val customizeState = viewModel.customizeState.collectAsState()
    val loadoutState = viewModel.loadoutState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            //.background(colBlue)
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

                    Box(
                        modifier = Modifier.fillMaxSize(0.62f)
                    ) {
                        PlaneRender(
                            paper = Attachments.list[0][loadoutState.value.slots[0]],
                            nose = Attachments.list[1][loadoutState.value.slots[1]],
                            wings = Attachments.list[2][loadoutState.value.slots[2]],
                            tail = Attachments.list[3][loadoutState.value.slots[3]]
                        )
                    }

                    Column() {

                        for (i in 0..3) {

                            val slotAttachment = loadoutState.value.slots[i]

                            AttachmentSlot(
                                isSelected = customizeState.value.selectedSlot == i,
                                attachment = Attachments.list[i][slotAttachment],
                                tint = if (i != 0) {
                                        Attachments.list[0][loadoutState.value.slots[0]].tint
                                    } else {
                                        Color(255, 255, 255)
                                    }
                            ) {
                                viewModel.setSlot(i)
                            }

                        }

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
                ) {

                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(colDarkBlue)
                    ) {

                        Text(
                            text = when (customizeState.value.selectedSlot) {
                                0 -> stringResource(R.string.papertype).uppercase()
                                1 -> stringResource(R.string.nose).uppercase()
                                2 -> stringResource(R.string.wings).uppercase()
                                else -> stringResource(R.string.tail).uppercase()
                            },
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold

                        )

                    }

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 5.dp),
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.92f)
                    ) {

                        items(
                            count = Attachments.list[customizeState.value.selectedSlot].size
                        ) {

                            Box(
                                modifier = Modifier.padding(0.dp)
                            ) {

                                AttachmentCard(
                                    attachment = Attachments.list[customizeState.value.selectedSlot][it],
                                    isSelected = loadoutState.value.slots[customizeState.value.selectedSlot] == it,
                                    tint = if (customizeState.value.selectedSlot != 0) {
                                            Attachments.list[0][loadoutState.value.slots[0]].tint
                                        } else {
                                            Color(255, 255, 255)
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
                        .background(colRed)
                        .clickable(onClick = onNextPage)
                ) {

                    Text(
                        text = stringResource(R.string.use).uppercase(),
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                    )

                }

            }

        }

    }

}

@Composable
fun AttachmentSlot(isSelected: Boolean, attachment: Attachment?, tint: Color, onClickSetSlot: () -> Unit) {

    Box(
        modifier = Modifier
            .padding(6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
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
        ) {

            if (attachment != null) {
                Image(
                    painter = painterResource(id = attachment.icon),
                    contentDescription = stringResource(attachment.description),
                    colorFilter = ColorFilter.tint(color = tint, blendMode = BlendMode.Modulate),
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }

        }

    }
    
}

@Composable
fun AttachmentCard (attachment: Attachment, tint: Color, isSelected: Boolean, onClickEquip: () -> Unit) {

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = if (isSelected) {
                    colOrangeHighlight
                } else {
                    colDarkBlue
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
                    painter = painterResource(id = attachment.icon),
                    contentDescription = stringResource(R.string.attachment_icon_description),
                    colorFilter = ColorFilter.tint(color = tint, blendMode = BlendMode.Modulate),
                    modifier = Modifier.fillMaxSize(0.85f)
                )
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(attachment.name).uppercase(),
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(attachment.description),
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