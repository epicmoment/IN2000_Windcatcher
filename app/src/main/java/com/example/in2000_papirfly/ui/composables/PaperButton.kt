package com.example.in2000_papirfly.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.in2000_papirfly.R
import java.time.format.TextStyle

@Composable
fun PaperButton(
    onClick: () -> Unit,
    enabled: Boolean,
    text: String,
    size: Int = 200
){
    Box(
        modifier = Modifier
            .clickable(
                enabled = enabled
            ) {
                onClick()
            }
    ){
        Image(
            painter = painterResource(id = R.drawable.icon_button_back02),
            contentDescription = "TODO",
            modifier = Modifier.size(size.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.icon_button_front02),
            contentDescription = "TODO",
            modifier = Modifier
                .size((size*0.9).dp)
                .align(Alignment.Center)
        )
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
            fontSize = (size/5).sp,
            fontWeight = FontWeight.Bold
        )
    }

}

@Preview
@Composable
fun preview_PaperButton(){
    PaperButton({}, true, "Throw")
}