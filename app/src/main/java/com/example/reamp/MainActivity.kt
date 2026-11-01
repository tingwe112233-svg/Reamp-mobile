package com.example.reamp

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ReampApp() }
    }
}

@Composable
fun ReampApp() {
    var isYouTubeMode by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val retroGreen = Color(0xFF00FF00)
    val retroDark = Color(0xFF0A0A0A)
    val retroBorder = Color(0xFF333333)

    Column(
        modifier = Modifier.fillMaxSize().background(retroDark).padding(16.dp)
    ) {
        Text(
            text = "REAMP // SEARCH",
            color = retroGreen,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Pemisah YouTube & Spotify
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ChannelBtn("YOUTUBE", isYouTubeMode, retroGreen) { isYouTubeMode = true }
            ChannelBtn("SPOTIFY", !isYouTubeMode, retroGreen) { isYouTubeMode = false }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Kotak Pencarian
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (isYouTubeMode) "Search YouTube..." else "Search Spotify...", color = Color.DarkGray) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = retroGreen, unfocusedTextColor = retroGreen,
                focusedContainerColor = Color.Black, unfocusedContainerColor = Color.Black,
                focusedIndicatorColor = retroGreen, unfocusedIndicatorColor = retroBorder
            ),
            modifier = Modifier.fillMaxWidth().border(1.dp, retroBorder),
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Contoh Hasil Pencarian Sementara
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(5) { index ->
                val platformName = if (isYouTubeMode) "YT" else "SPOTIFY"
                val songTitle = "Lagu Contoh 0${index + 1} - $platformName"
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).border(1.dp, Color(0xFF222222)).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = songTitle, color = retroGreen, fontFamily = FontFamily.Monospace, fontSize = 16.sp)
                        Text(text = "Format: MP3 | Source: $platformName", color = Color.Gray, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Button(
                        onClick = {
                            val dummyUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                            downloadAudio(context, songTitle, dummyUrl)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004400)),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text("DL", color = retroGreen, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelBtn(text: String, isSelected: Boolean, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier.clickable(onClick = onClick).border(1.dp, if (isSelected) color else Color.DarkGray)
            .background(if (isSelected) Color(0xFF002200) else Color.Transparent).padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = if (isSelected) color else Color.Gray, fontFamily = FontFamily.Monospace)
    }
}

fun downloadAudio(context: Context, fileName: String, url: String) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading ke folder Download...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$fileName.mp3")

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
        Toast.makeText(context, "Mengunduh: $fileName", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
