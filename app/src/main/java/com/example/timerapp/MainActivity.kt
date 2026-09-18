package com.example.timerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf("android.permission.POST_NOTIFICATIONS"),
                101
            )
        }
        setContent {
            TimeFlowApp(
                showNotification = {
                    showTimerNotification()
                }
            )
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "timer_channel",
                "Timer Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun showTimerNotification() {

        val notification = NotificationCompat.Builder(this, "timer_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Focus Session Complete! 🎉")
            .setContentText("Great job! Your focus session has finished.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1001, notification)
    }
}

@Composable
fun TimeFlowApp(
    showNotification: () -> Unit
)  {

    var selectedMinutes by remember { mutableIntStateOf(25) }
    var remainingSeconds by remember { mutableIntStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {

        while (isRunning && remainingSeconds > 0) {

            delay(1000)

            remainingSeconds--
        }

        if (remainingSeconds == 0) {
            isRunning = false
            showNotification()
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60

    val timeText = String.format(
        "%02d:%02d",
        minutes,
        seconds
    )

    val status = when {

        isRunning -> "●  RUNNING"

        remainingSeconds == 0 -> "●  COMPLETED"

        remainingSeconds < selectedMinutes * 60 ->
            "●  PAUSED"

        else -> "●  READY"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF090B16),
                        Color(0xFF15192B),
                        Color(0xFF090B16)
                    )
                )
            )
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            // APP TITLE

            Text(
                text = "TIMEFLOW",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "FOCUS • TRACK • ACHIEVE",
                fontSize = 11.sp,
                color = Color(0xFF8F96B8),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(35.dp))

            // TIMER

            Box(
                modifier = Modifier
                    .size(270.dp)
                    .border(
                        width = 4.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFF7C4DFF),
                                Color(0xFF00D4FF),
                                Color(0xFF7C4DFF)
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(14.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF303650),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "TIME REMAINING",
                        fontSize = 11.sp,
                        color = Color(0xFF8F96B8),
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = timeText,
                        fontSize = 55.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00D4FF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // PRESETS

            Text(
                text = "CHOOSE DURATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8F96B8),
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                DurationButton(
                    minutes = 5,
                    selected = selectedMinutes == 5,
                    onClick = {
                        selectedMinutes = 5
                        remainingSeconds = 5 * 60
                        isRunning = false
                    }
                )

                DurationButton(
                    minutes = 25,
                    selected = selectedMinutes == 25,
                    onClick = {
                        selectedMinutes = 25
                        remainingSeconds = 25 * 60
                        isRunning = false
                    }
                )

                DurationButton(
                    minutes = 45,
                    selected = selectedMinutes == 45,
                    onClick = {
                        selectedMinutes = 45
                        remainingSeconds = 45 * 60
                        isRunning = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // START / PAUSE

            Button(
                onClick = {

                    if (remainingSeconds > 0) {
                        isRunning = !isRunning
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C4DFF)
                )
            ) {

                Text(
                    text = if (isRunning)
                        "PAUSE TIMER"
                    else
                        "START TIMER",

                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // RESET

            OutlinedButton(
                onClick = {

                    isRunning = false

                    remainingSeconds =
                        selectedMinutes * 60

                },
                modifier = Modifier
                    .width(150.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(15.dp)
            ) {

                Text(
                    text = "↻  RESET",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // SESSION CARD

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF191D31))
                    .padding(18.dp),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "CURRENT SESSION",
                        fontSize = 10.sp,
                        color = Color(0xFF8F96B8),
                        letterSpacing = 1.sp
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "$selectedMinutes MIN FOCUS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = timeText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00D4FF)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "MAKE EVERY SECOND COUNT",
                fontSize = 10.sp,
                color = Color(0xFF626A8A),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}


@Composable
fun DurationButton(
    minutes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,

        modifier = Modifier
            .width(85.dp)
            .height(45.dp),

        shape = RoundedCornerShape(14.dp),

        colors = ButtonDefaults.buttonColors(

            containerColor =
                if (selected)
                    Color(0xFF7C4DFF)
                else
                    Color(0xFF191D31),

            contentColor = Color.White
        )
    ) {

        Text(
            text = "$minutes MIN",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}