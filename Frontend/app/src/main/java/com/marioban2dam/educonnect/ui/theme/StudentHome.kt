package com.marioban2dam.educonnect.ui.theme


import android.media.Image
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marioban2dam.educonnect.Course
import com.marioban2dam.educonnect.R
import java.time.format.TextStyle


// The Screen that the student can see when he login to the app
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun StudentHomeScreen() {

    val courses = listOf(
        Course("Programming", R.drawable.code_slash),
        Course("Databases", R.drawable.group),
        Course("Mobile Development", R.drawable.android),
        Course("Web Development", R.drawable.globe),
        Course("Services", R.drawable.vector__4_),
        Course("FOL", R.drawable.vector__3_)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyBlue, DarkBlue),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        // EDUCONNECT Title
        Text(
            text = "EDUCONNECT",
            modifier = Modifier
                .offset(x= 12.dp,y=15.dp)
                .padding(top = 52.dp),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                fontWeight = FontWeight(700),
                color = Color(0xFFF1E5FC),
                textAlign = TextAlign.Center,
            )
        )

        // Profile icon
        Image(
            painter = painterResource(id = R.drawable.person_square),
            contentDescription = "Photo profile of the student",
            contentScale = ContentScale.None,
            modifier = Modifier
                .offset(x=270.dp,y=15.dp)
                .padding(top = 52.dp, end = 16.dp)
                .size(45.dp)
        )

        // Student name
        Text(
            text = "Name",
            modifier = Modifier
                .offset(x= 275.dp,y=20.dp)
                .padding(top = 97.dp, end = 16.dp),
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
            fontWeight = FontWeight.Bold,
            color = Color(0xFFA6E1FA),
            textAlign = TextAlign.Center
        )

        // Courses title
        Text(
            text = "Courses",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 167.dp),
            fontSize = 30.sp,
            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF1E5FC),
            textAlign = TextAlign.Center
        )

        // Courses grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 230.dp, bottom = 16.dp)
        ) {
            items(courses.size) { index ->
                ReusableBoxForCourses(
                    name = courses[index].name,
                    iconResId = courses[index].iconResId,
                    onClick = {
                        println("Clicked on ${courses[index].name}")
                    }
                )
            }
        }

        // Bottom Navigation Bar
        BottomNavigationBar(selectedIndex = 0, onItemClick = { index ->
            println("Selected item: $index")
        })
    }
}

@Composable
fun ReusableBoxForCourses(name: String, iconResId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.45f) // Ajusta el ancho relativo al tamaño de la pantalla
            .aspectRatio(1.45f) // Mantén una proporción para el alto
            .border(width = 1.dp, color = Color(0xFF0E6BA8), shape = RoundedCornerShape(10.dp))
            .background(Color(0xFF001C55), shape = RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = name,
                modifier = Modifier
                    .size(32.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BottomNavigationBar(selectedIndex: Int, onItemClick: (Int) -> Unit) {
    val items = listOf(
        Option("Home", R.drawable.vector__2_),
        Option("Messages", R.drawable.vector__1_),
        Option("Grades", R.drawable.file_bar_graph_fill),
        Option("Profile", R.drawable.vector)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .offset(x=0.dp,y = 750.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFF001C55), shape = RoundedCornerShape(20.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, (label, icon) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onItemClick(index) }
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = label,
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF1E5FC),
                        fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


