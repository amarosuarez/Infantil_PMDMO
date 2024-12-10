package com.example.infantil

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem.fromUri
import androidx.media3.exoplayer.ExoPlayer
import kotlin.math.sqrt

@Composable
fun VistaImagenes() {
    // Contexto
    val context = LocalContext.current

    // Sensor
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    // Lista de imágenes
    val imagenes = listOf(
        R.drawable.perro,
        R.drawable.gato,
        R.drawable.vaca,
        R.drawable.pajaro,
        R.drawable.leon,
    )

    // Lista de sonidos
    val sonidos = listOf(
        R.raw.perro,
        R.raw.gato,
        R.raw.vaca,
        R.raw.pajaro,
        R.raw.leon,
    )

    // Creamos el ExoPlayer
    val player = ExoPlayer.Builder(context).build()

    // Detectamos el movimiento
    LaunchedEffect(Unit) {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Umbral de sensibilidad de movimiento
        val shakeThreshold = 40f
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt(x * x + y * y + z * z)

                // Si la magnitud es mayor al umbral reproducimos el sonido
                if (magnitude > shakeThreshold) {
                    val mediaItem = fromUri(Uri.parse("android.resource://${context.packageName}/${R.raw.cristales}"))
                    player.setMediaItem(mediaItem)
                    player.prepare()

                    // Si no se está reproduciendo ya, reproducimos el sonido
                    if (!player.isPlaying) {
                        player.play()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }


    // LazyColumn para mostrar las imágenes
    LazyColumn {
        items(imagenes.size) { index ->
            Image(
                painter = painterResource(imagenes[index]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .aspectRatio(1f)
                    .clickable {
                        // Cuando se pulse en una imagen, se reproduce el sonido del animal
                        val mediaItem = fromUri(Uri.parse("android.resource://${context.packageName}/${sonidos[index]}"))
                        player.setMediaItem(mediaItem)
                        player.prepare()
                        player.play()
                    },
                contentScale = ContentScale.Crop
            )
        }
    }

}