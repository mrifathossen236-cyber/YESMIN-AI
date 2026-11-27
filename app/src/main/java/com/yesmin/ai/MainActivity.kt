package com.yesmin.ai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("bn", "BD"))
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    tts?.language = Locale.getDefault()
                }
            }
        }

        setContent {
            YesminApp(onSpeak = { speak(it) })
        }
    }

    private fun speak(text: String) {
        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "yesmin"
        )
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YesminApp(onSpeak: (String) -> Unit) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { SettingsStore(context) }

    var apiKey by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("gemini-2.5-flash") }
    var message by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var settingsOpen by remember { mutableStateOf(false) }

    var chat by remember {
        mutableStateOf(
            listOf(
                "YESMIN" to "হাই! আমি YESMIN। কীভাবে সাহায্য করতে পারি?"
            )
        )
    }

    var status by remember { mutableStateOf("Ready") }

    LaunchedEffect(Unit) {
        apiKey = store.getApiKey()
        model = store.getModel()
    }

    val speechLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val spoken = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()

            if (!spoken.isNullOrBlank()) {
                message = spoken
            }
        }

    val gradient = Brush.verticalGradient(
        listOf(
            Color(0xFF160D2B),
            Color(0xFF080B18)
        )
    )

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFE879F9),
            secondary = Color(0xFF8B5CF6),
            background = Color(0xFF080B18),
            surface = Color(0xFF17152A)
        )
    ) {

        Scaffold(
            containerColor = Color.Transparent,

            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "YESMIN AI",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Your personal AI companion",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                    },

                    actions = {
                        TextButton(
                            onClick = { settingsOpen = true }
                        ) {
                            Text("Settings")
                        }
                    },

                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF160D2B),
                        titleContentColor = Color.White
                    )
                )
            }

        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
                    .padding(padding)
                    .padding(16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFFE879F9),
                                            Color(0xFF6366F1)
                                        )
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                "Y",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "YESMIN",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            status,
                            color = Color(0xFFC4B5FD),
                            fontSize = 12.sp
                        )
                    }
                }

                // FIXED QUICK BUTTON ROW
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    QuickButton(
                        "YouTube",
                        Modifier.weight(1f)
                    ) {
                        openYouTube(context)
                    }

                    QuickButton(
                        "Apps",
                        Modifier.weight(1f)
                    ) {
                        context.startActivity(
                            Intent(Settings.ACTION_SETTINGS)
                        )
                    }

                    QuickButton(
                        "Accessibility",
                        Modifier.weight(1f)
                    ) {
                        context.startActivity(
                            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),

                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    items(chat) { (who, text) ->

                        Column(
                            modifier = Modifier.fillMaxWidth(),

                            horizontalAlignment =
                                if (who == "YESMIN")
                                    Alignment.Start
                                else
                                    Alignment.End
                        ) {

                            Surface(
                                color =
                                    if (who == "YESMIN")
                                        Color(0xFF292044)
                                    else
                                        Color(0xFF43306B),

                                shape = RoundedCornerShape(16.dp)
                            ) {

                                Column(
                                    Modifier.padding(12.dp)
                                ) {

                                    Text(
                                        who,
                                        color = Color(0xFFE9A8FF),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedTextField(
                        value = message,

                        onValueChange = {
                            message = it
                        },

                        modifier = Modifier.weight(1f),

                        placeholder = {
                            Text("YESMIN-কে কিছু বলো…")
                        },

                        maxLines = 3,
                        shape = RoundedCornerShape(18.dp)
                    )

                    IconButton(
                        onClick = {

                            val intent = Intent(
                                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                            ).apply {

                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )

                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE,
                                    "bn-BD"
                                )

                                putExtra(
                                    RecognizerIntent.EXTRA_PROMPT,
                                    "YESMIN-কে বলুন"
                                )
                            }

                            try {
                                speechLauncher.launch(intent)
                            } catch (_: Exception) {
                                status = "Speech recognition unavailable"
                            }
                        }
                    ) {
                        Text("🎙️")
                    }

                    Button(
                        enabled = !busy && message.isNotBlank(),

                        onClick = {

                            val prompt = message.trim()
                            message = ""

                            chat = chat + ("You" to prompt)

                            scope.launch {

                                busy = true
                                status = "Thinking…"

                                try {

                                    val result = handleCommand(
                                        context,
                                        prompt
                                    ) {
                                        apiKey to model
                                    }

                                    val answer =
                                        result
                                            ?: if (apiKey.isNotBlank()) {
                                                GeminiClient.generate(
                                                    apiKey,
                                                    model,
                                                    prompt
                                                )
                                            } else {
                                                "Gemini API Key সেট করতে Settings খুলুন।"
                                            }

                                    chat = chat + ("YESMIN" to answer)

                                    onSpeak(answer)

                                } catch (e: Exception) {

                                    val error =
                                        "সমস্যা হয়েছে: ${e.localizedMessage ?: "Unknown error"}"

                                    chat = chat + ("YESMIN" to error)
                                    onSpeak(error)

                                } finally {
                                    busy = false
                                    status = "Ready"
                                }
                            }
                        }

                    ) {
                        Text(if (busy) "…" else "Send")
                    }
                }
            }
        }

        if (settingsOpen) {

            AlertDialog(

                onDismissRequest = {
                    settingsOpen = false
                },

                title = {
                    Text("YESMIN Settings")
                },

                text = {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            "Gemini API configuration",
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = apiKey,
                            onValueChange = { apiKey = it },
                            label = { Text("Gemini API Key") },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Gemini model") },
                            singleLine = true
                        )

                        Text(
                            "API key এই ডিভাইসে DataStore-এ রাখা হয়। পাবলিক APK-তে client-side key সম্পূর্ণ নিরাপদ নয়।",
                            fontSize = 11.sp
                        )
                    }
                },

                confirmButton = {

                    TextButton(
                        onClick = {

                            scope.launch {

                                store.save(
                                    apiKey.trim(),
                                    model.trim().ifBlank {
                                        "gemini-2.5-flash"
                                    }
                                )
                            }

                            settingsOpen = false
                            status = "Settings saved"
                        }
                    ) {
                        Text("Save")
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {
                            settingsOpen = false
                        }
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

// FIXED QUICK BUTTON FUNCTION
@Composable
private fun QuickButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,

        contentPadding = PaddingValues(
            horizontal = 5.dp
        )
    ) {

        Text(
            label,
            fontSize = 11.sp
        )
    }
}

private fun openYouTube(
    context: android.content.Context
) {

    val intent =
        context.packageManager.getLaunchIntentForPackage(
            "com.google.android.youtube"
        ) ?: Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com")
        )

    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    context.startActivity(intent)
}

private suspend fun handleCommand(
    context: android.content.Context,
    text: String,
    config: () -> Pair<String, String>
): String? {

    val q = text.lowercase(Locale.ROOT)

    return try {

        when {

            q.contains("youtube") ||
            q.contains("ইউটিউব") ||
            q.contains("গান") ||
            q.contains("song") -> {

                val query = Uri.encode(
                    text.replace(
                        Regex("(?i)yesmin|open|play|চালাও|খোলো|ইউটিউব|youtube"),
                        ""
                    ).trim()
                )

                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            "https://www.youtube.com/results?search_query=$query"
                        )
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )

                "YouTube-এ সার্চ খুলে দিয়েছি।"
            }

            q.contains("settings") ||
            q.contains("সেটিংস") -> {

                context.startActivity(
                    Intent(Settings.ACTION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )

                "ফোনের Settings খুলে দিয়েছি।"
            }

            q.contains("chrome") ||
            q.contains("browser") -> {

                val intent =
                    context.packageManager.getLaunchIntentForPackage(
                        "com.android.chrome"
                    )

                if (intent != null) {
                    context.startActivity(
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } else {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com")
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }

                "Browser খুলে দিয়েছি।"
            }

            q.contains("camera") ||
            q.contains("ক্যামেরা") -> {

                context.startActivity(
                    Intent("android.media.action.IMAGE_CAPTURE")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )

                "Camera খুলে দিয়েছি।"
            }

            else -> null
        }

    } catch (e: Exception) {

        "এই কাজটি করা যায়নি: ${
            e.localizedMessage ?: "Unknown error"
        }"
    }
}
