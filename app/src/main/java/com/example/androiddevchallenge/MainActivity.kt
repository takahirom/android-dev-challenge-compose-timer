/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        ConstraintLayout {
            val (clock, timeEdit, timeText, button) = createRefs()
            var text by remember { mutableStateOf("01:00") }
            var isStarted by remember { mutableStateOf<Boolean>(false) }
            val allTimeSeconds = remember { Animatable(100F) }
            val elapsedTimeSeconds = remember { Animatable(0F) }
            LaunchedEffect(key1 = isStarted) {
                if (isStarted) {
                    val (minutes, seconds) = text.split(":")
                    val targetValue = minutes.toInt().toFloat() * 60 + seconds.toInt()
                    allTimeSeconds.animateTo(targetValue)
                    elapsedTimeSeconds.animateTo(targetValue)
                    while (elapsedTimeSeconds.value >= 1) {
                        delay(1000)
                        elapsedTimeSeconds.animateTo(elapsedTimeSeconds.value - 1)
                    }
                    isStarted = false
                }
            }
            TextField(
                modifier = Modifier.constrainAs(timeEdit) {
                    top.linkTo(clock.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(button.start)
                },
                value = text, onValueChange = { text = it }
            )
            Button(
                modifier = Modifier
                    .constrainAs(button) {
                        top.linkTo(timeEdit.top)
                        start.linkTo(timeEdit.end, 16.dp)
                        end.linkTo(parent.end)
                        bottom.linkTo(timeEdit.bottom)
                    },
                onClick = {
                    isStarted = !isStarted
                }
            ) {
                Text(if (isStarted) "stop" else "start")
            }
            Text(
                modifier = Modifier.constrainAs(timeText) {
                    top.linkTo(timeEdit.bottom)
                    start.linkTo(timeEdit.start)
                },
                text = "${elapsedTimeSeconds.targetValue}"
            )
            BoxWithConstraints(
                modifier = Modifier
                    .constrainAs(clock) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end, 16.dp)
                    },
                contentAlignment = Alignment.Center
            ) {
                val background = MaterialTheme.colors.background
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1F)
                        .background(background)
                ) {
                    val width = constraints.maxWidth.toFloat()
                    fun topLeft(width: Float, size: Float): Float {
                        val center = width / 2
                        return center - size / 2
                    }

                    val sizeLarge = width * 8 / 10
                    val topLeft = topLeft(width = width, sizeLarge)
                    drawArc(
                        Color(0x1e, 0x88, 0xe5),
                        0F,
                        360f * (elapsedTimeSeconds.value / allTimeSeconds.value),
                        useCenter = true,
                        size = Size(sizeLarge, sizeLarge),
                        topLeft = Offset(topLeft, topLeft),
//                        blendMode = BlendMode.Xor
                    )

                    val sizeMedium = width * 7 / 10
                    val smallTopMedium = topLeft(width = width, sizeMedium)
                    drawArc(
                        Color(0x6a, 0xb7, 0xff),
                        0F,
                        ((elapsedTimeSeconds.value / allTimeSeconds.value) * 360F * 10) % 360,
                        useCenter = true,
                        size = Size(sizeMedium, sizeMedium),
                        topLeft = Offset(
                            smallTopMedium,
                            smallTopMedium
                        ),
//                        blendMode = BlendMode.Xor
                    )
                    val sizeSmall = width * 6 / 10
                    val smallTopLeft = topLeft(width = width, sizeSmall)
                    drawArc(
                        background,
                        0F,
                        360f,
                        useCenter = true,
                        size = Size(sizeSmall, sizeSmall),
                        topLeft = Offset(
                            smallTopLeft,
                            smallTopLeft
                        ),
                        blendMode = BlendMode.SrcIn
                    )
                }
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
