import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

start = text.find("fun InteractiveMascotCard(")
end = text.find("// ---------- Mascot Component (Canvas Drawn) ----------", start)
assert start != -1 and end != -1, "Target bounds for InteractiveMascotCard not found"

new_code = """class CuteMascotSpeaker(context: android.content.Context) {
    private var tts: android.speech.tts.TextToSpeech? = null
    var isReady by mutableStateOf(false)

    init {
        try {
            tts = android.speech.tts.TextToSpeech(context.applicationContext) { status ->
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    try {
                        tts?.language = java.util.Locale.US
                        tts?.setPitch(1.75f) // High-pitch cute Talking Tom squeak!
                        tts?.setSpeechRate(1.15f)
                        isReady = true
                    } catch (e: Exception) { }
                }
            }
        } catch (e: Exception) { }
    }

    fun speak(text: String) {
        if (!isReady || tts == null) return
        try {
            val clean = text.replace(Regex("[^a-zA-Z0-9 ,.!?']"), " ").trim()
            if (clean.isNotBlank()) {
                tts?.speak(clean, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "mascot_speech")
            }
        } catch (e: Exception) { }
    }

    fun stop() {
        try { tts?.stop() } catch (e: Exception) { }
    }

    fun shutdown() {
        try { tts?.shutdown() } catch (e: Exception) { }
    }
}

@Composable
fun InteractiveMascotCard(
    type: MascotType,
    name: String,
    studiedSecs: Long,
    goalMins: Long,
    streak: Int,
    onOpenCompanionPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val speaker = remember { CuteMascotSpeaker(context) }
    DisposableEffect(Unit) {
        onDispose { speaker.shutdown() }
    }
    val prefs = remember { context.getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE) }
    var voiceEnabled by remember { mutableStateOf(prefs.getBoolean("mascot_voice_on", true)) }

    var tone by remember { mutableStateOf(BuddyTone.ROAST) }
    var currentSpeech by remember { mutableStateOf(ROASTS.random()) }
    var pokeAnim by remember { mutableStateOf(false) }
    var tapCount by remember { mutableStateOf(0) }
    var showAIDialog by remember { mutableStateOf(false) }
    val view = androidx.compose.ui.platform.LocalView.current

    var lastPokeTime by remember { mutableStateOf(0L) }
    var rapidPokeCount by remember { mutableStateOf(0) }
    var isDizzy by remember { mutableStateOf(false) }

    // Infinite live breathing & cute idle hop
    val inf = rememberInfiniteTransition(label = "mascot_idle")
    val idleHop by inf.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "idleHop"
    )
    val idleWiggle by inf.animateFloat(
        initialValue = -4f, targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "idleWiggle"
    )

    // Spring bouncy jump & wiggle on tap / poke!
    val jumpOffsetY by animateFloatAsState(
        targetValue = if (pokeAnim) -35f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "jump"
    )
    val wiggleAngle by animateFloatAsState(
        targetValue = if (pokeAnim) 22f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        label = "wiggle"
    )
    val squishScale by animateFloatAsState(
        targetValue = if (pokeAnim) 1.20f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "squish"
    )

    val mascotMood = when {
        isDizzy -> MascotMood.DIZZY
        goalMins > 0 && studiedSecs >= goalMins * 60 -> MascotMood.CHEERING
        studiedSecs > 0 -> MascotMood.CHEERING
        else -> MascotMood.SLEEPING
    }

    fun onPoke() {
        tapCount++
        val now = System.currentTimeMillis()
        if (now - lastPokeTime < 750) {
            rapidPokeCount++
        } else {
            rapidPokeCount = 1
        }
        lastPokeTime = now

        // Audio pop feedback on poke
        try {
            val tg = android.media.ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 50)
            tg.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 60)
        } catch (e: Exception) { }

        try { view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP) } catch (e: Exception) { }

        if (rapidPokeCount >= 3) {
            isDizzy = true
            currentSpeech = listOf(
                "Woah woah! I'm getting dizzy! (x_x) 💫",
                "Slow down bestie!! My head is spinning! 😵💫",
                "Too many pokes! Save that energy for studying! 😂💖",
                "Eeeek! You poked me too fast! (⁄ ⁄•⁄ω⁄•⁄ ⁄) 💫"
            ).random()
            if (voiceEnabled) speaker.speak(currentSpeech)
        } else {
            isDizzy = false
            pokeAnim = true
            currentSpeech = when (tone) {
                BuddyTone.ROAST -> ROASTS.random()
                BuddyTone.HYPE -> HYPE_QUIPS.random()
                BuddyTone.TIP -> STUDY_TIPS.random()
            }
            if (voiceEnabled) speaker.speak(currentSpeech)
        }
    }

    LaunchedEffect(isDizzy) {
        if (isDizzy) {
            delay(2800)
            isDizzy = false
            rapidPokeCount = 0
        }
    }

    LaunchedEffect(pokeAnim) {
        if (pokeAnim) {
            delay(350)
            pokeAnim = false
        }
    }

    if (showAIDialog) {
        MascotAIAssistantDialog(
            mascotName = name,
            mascotType = type,
            onDismiss = { showAIDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.50f))
            .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(28.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interactive Talking Tom Mascot: jumps, wiggles, and hops!
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .graphicsLayer {
                        translationY = jumpOffsetY + idleHop
                        rotationZ = wiggleAngle + idleWiggle + (if (isDizzy) (kotlin.math.sin(tapCount * 2f) * 25f) else 0f)
                        scaleX = if (isDizzy) squishScale * 1.15f else squishScale
                        scaleY = if (isDizzy) (2f - squishScale) * 0.9f else (2f - squishScale)
                    }
                    .clip(CircleShape)
                    .clickable { onPoke() },
                contentAlignment = Alignment.Center
            ) {
                MascotCanvas(
                    type = type,
                    mood = if (isDizzy) MascotMood.DIZZY else if (pokeAnim || tapCount % 3 == 1) MascotMood.CHEERING else mascotMood,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.width(12.dp))

            // Speech bubble & action buttons
            Column(Modifier.weight(1f)) {
                // Top row: Mascot Name, Speaker toggle & Change button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(type.emoji, fontSize = 15.sp)
                    Spacer(Modifier.width(6.dp))

                    // Cute Speaker Toggle (Voice on/off)
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = if (voiceEnabled) 0.15f else 0.06f),
                        modifier = Modifier.clickable {
                            voiceEnabled = !voiceEnabled
                            prefs.edit().putBoolean("mascot_voice_on", voiceEnabled).apply()
                            if (!voiceEnabled) speaker.stop()
                        }
                    ) {
                        Text(
                            if (voiceEnabled) "🔊" else "🔇",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // Change mascot button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.clickable { onOpenCompanionPicker() }
                    ) {
                        Text(
                            "Change 🐾",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Speech Bubble Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                        .clickable { onPoke() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        "\\\"$currentSpeech\\\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Tone selector buttons (Roast / Hype / Tips) on left, Ask AI on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    BuddyTone.ROAST to "🔥 Roast",
                    BuddyTone.HYPE to "⚡ Hype",
                    BuddyTone.TIP to "💡 Tip"
                ).forEach { (t, label) ->
                    val selected = tone == t
                    Text(
                        label,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                            .clickable {
                                tone = t
                                currentSpeech = when (t) {
                                    BuddyTone.ROAST -> ROASTS.random()
                                    BuddyTone.HYPE -> HYPE_QUIPS.random()
                                    BuddyTone.TIP -> STUDY_TIPS.random()
                                }
                                if (voiceEnabled) speaker.speak(currentSpeech)
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Ask AI button
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 2.dp,
                modifier = Modifier.clickable { showAIDialog = true }
            ) {
                Text(
                    "Ask AI 💬✨",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}
"""

text = text[:start] + new_code + text[end:]

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("InteractiveMascotCard updated with Talking Tom voice & dizzy poke physics!")
