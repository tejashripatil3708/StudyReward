package com.example.studyreward

import android.app.AppOpsManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.os.Process
import androidx.core.app.NotificationCompat

import android.media.AudioTrack
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import androidx.compose.foundation.interaction.MutableInteractionSource
import android.accessibilityservice.AccessibilityService
import android.app.DatePickerDialog
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.graphics.Rect
import android.provider.CalendarContract
import android.content.ContentUris
import android.graphics.Matrix
import android.graphics.Outline
import android.view.ViewOutlineProvider
import android.graphics.PixelFormat
import android.os.Handler
import android.os.PowerManager
import android.os.Looper
import android.provider.Settings as AndroidSettings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.viewinterop.AndroidView
import android.view.TextureView
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CameraCharacteristics
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.VideocamOff
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.CallEnd
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import kotlin.math.sin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TaskItem(val id: String, val title: String, val subject: String, val done: Boolean, val order: Long, val dur: Long = 15L, val worked: Long = 0L, val doneAt: Long = 0L, val reward: Long = 0L, val kind: String = "", val date: String = "")
data class Subject(val id: String, val name: String, val color: Long)
data class Group(val id: String, val name: String, val admin: String, val code: String, val pool: Int, val members: List<String>,
                 val intro: String = "", val rules: String = "", val capacity: Int = 10, val joinMethod: String = "code",
                 val nudgeAt: Long = 0L, val nudgeBy: String = "",
                 val isPrivate: Boolean = false, val password: String = "", val category: String = "General")
data class Req(val id: String, val fromUid: String, val fromName: String, val mins: Long, val at: Long, val yes: List<String>, val no: List<String>)
data class Member(val id: String, val name: String, val study: Long, val rewards: Long, val studying: Boolean, val asOf: Long, val total: Long)

// The most gifted screen time (minutes) anyone can get per day from ALL groups combined
const val MAX_GIFT_MINS = 90L
// Screen time (minutes) you give up to revive a streak after one missed day
const val REVIVE_COST_MIN = 30L

val palette = listOf(
    0xFFFF6B6B, 0xFFB57EDC, 0xFF6C3FC5, 0xFFD62D0F, 0xFFFF6A00, 0xFFFFE600,
    0xFF4ADE80, 0xFF38BDF8, 0xFFFF8FB1, 0xFF14B8A6, 0xFFA3E635, 0xFFFBBF24,
    0xFF94A3B8, 0xFF3B82F6, 0xFFE879F9, 0xFF8B5E3C, 0xFF22D3EE, 0xFFF97316)

class AppTheme(val name: String, val light: Boolean, val bg: Long, val card: Long, val text: Long, val accent: Long, val sel: Long,
               val scene: String = "")

val themes = listOf(
    // Cute aesthetic design themes with painted Canvas art
    AppTheme("Lavender Dream", true, 0xFFF8F5FE, 0xEEFFFFFF, 0xFF2C243B, 0xFF8E63E6, 0xFFE9E0FD, "lavender"),
    AppTheme("Mochi Pink", true, 0xFFFFF5F7, 0xEEFFFFFF, 0xFF3D2329, 0xFFFF5C8D, 0xFFFCE4EC, "mochi_pink"),
    AppTheme("Matcha Latte", true, 0xFFF5F9F4, 0xEEFFFFFF, 0xFF1E2D1E, 0xFF4E9B56, 0xFFE8F5E9, "matcha"),
    AppTheme("Peach Soda", true, 0xFFFFF8F3, 0xEEFFFFFF, 0xFF3B2518, 0xFFFF7A45, 0xFFFFEBE0, "peach"),
    AppTheme("Sky Pudding", true, 0xFFF3F8FD, 0xEEFFFFFF, 0xFF182A38, 0xFF3DA9FC, 0xFFE1F0FA, "sky_pudding"),
    AppTheme("Bubblegum", true, 0xFFFDF4FB, 0xEEFFFFFF, 0xFF3A1E35, 0xFFE040FB, 0xFFF8E3F4, "bubblegum"),
    AppTheme("Starry Plush", false, 0xFF12142B, 0xEE1B1E3F, 0xFFF0F2FF, 0xFF8C9EFF, 0xFF2A2E5E, "starry_plush"),
    AppTheme("Cocoa Bear", true, 0xFFFAF6F3, 0xEEFFFFFF, 0xFF362820, 0xFF8D6E63, 0xFFEFE8E1, "cocoa"),
    // Classic themes
    AppTheme("Dusk", false, 0xFF0F1715, 0xFF19241F, 0xFFE6EFEA, 0xFF7FD1B9, 0xFF2A423A),
    AppTheme("AMOLED black", false, 0xFF000000, 0xFF151515, 0xFFEDEDED, 0xFF38BDF8, 0xFF2A2A2A),
    AppTheme("Paper", true, 0xFFF7F5EF, 0xFFEDEAE0, 0xFF202B2A, 0xFF2F6F62, 0xFFD9E6DF),
    AppTheme("Forest", false, 0xFF0B1410, 0xFF15241C, 0xFFDDEEE3, 0xFF4ADE80, 0xFF24402F),
    AppTheme("Midnight", false, 0xFF0B1020, 0xFF151B33, 0xFFE1E6F5, 0xFF7C9CFF, 0xFF26305A),
    AppTheme("Purple", false, 0xFF130E1C, 0xFF1F1730, 0xFFEAE1F5, 0xFFB57EDC, 0xFF3A2A5A),
    AppTheme("Rose", false, 0xFF1A0F12, 0xFF2A1A1F, 0xFFF5E4E8, 0xFFFF6B8B, 0xFF4A2A33),
    // Scenic themes: a painted scene behind the app, with slightly see-through cards
    AppTheme("Misty Mountains", false, 0xFF0E1A24, 0xB8172A38, 0xFFE8F1F5, 0xFF8FD3E8, 0x99284A5E, "mountains"),
    AppTheme("Sunset Beach", false, 0xFF2A1420, 0xB83A2030, 0xFFFFEFE6, 0xFFFFB27A, 0x99623042, "sunset"),
    AppTheme("Starry Night", false, 0xFF070B1E, 0xB8121A38, 0xFFE9ECFF, 0xFFB7A6FF, 0x99283469, "night"),
    AppTheme("Calm Ocean", false, 0xFF06222B, 0xB80D3340, 0xFFE4F6F8, 0xFF6FE0D0, 0x99185568, "ocean"),
    AppTheme("Pine Forest", false, 0xFF0B1A12, 0xB8143022, 0xFFE6F2E9, 0xFF9BDB8A, 0x99265A3C, "forest"),
    AppTheme("Rainy Window", false, 0xFF14181F, 0xB81E2530, 0xFFE8EBF0, 0xFF9FB8D6, 0x99384A63, "rain"),
    // Calm, white and spacious with a fresh green accent
    AppTheme("Clean", true, 0xFFFFFFFF, 0xFFF5F5F3, 0xFF1F2328, 0xFF1F9D55, 0xFFE3F2E8))

// System fonts (more can be added later with downloadable fonts)
var fonts: List<Pair<String, FontFamily>> = listOf("Default" to FontFamily.Default)

fun sysFont(name: String): FontFamily =
    FontFamily(androidx.compose.ui.text.font.Typeface(android.graphics.Typeface.create(name, android.graphics.Typeface.NORMAL)))

// Built-in Android font families, plus every .ttf or .otf placed in app/src/main/assets/fonts
fun loadFonts(ctx: Context): List<Pair<String, FontFamily>> {
    val builtIn = listOf(
        "Default" to FontFamily.Default, "Light" to sysFont("sans-serif-light"), "Medium" to sysFont("sans-serif-medium"),
        "Condensed" to sysFont("sans-serif-condensed"), "Heavy" to sysFont("sans-serif-black"), "Serif" to FontFamily.Serif,
        "Serif mono" to sysFont("serif-monospace"), "Monospace" to FontFamily.Monospace, "Casual" to sysFont("casual"),
        "Cursive" to FontFamily.Cursive, "Small caps" to sysFont("sans-serif-smallcaps"))
    val own = try {
        (ctx.assets.list("fonts") ?: emptyArray()).filter { it.endsWith(".ttf", true) || it.endsWith(".otf", true) }.mapNotNull { f ->
            try {
                f.substringBeforeLast('.').replace('-', ' ').replace('_', ' ') to
                        FontFamily(androidx.compose.ui.text.font.Typeface(android.graphics.Typeface.createFromAsset(ctx.assets, "fonts/$f")))
            } catch (e: Exception) { null }
        }
    } catch (e: Exception) { emptyList() }
    return builtIn + own
}

fun typographyFor(ff: FontFamily): Typography {
    val d = Typography()
    return Typography(
        displayLarge = d.displayLarge.copy(fontFamily = ff), displayMedium = d.displayMedium.copy(fontFamily = ff),
        displaySmall = d.displaySmall.copy(fontFamily = ff), headlineLarge = d.headlineLarge.copy(fontFamily = ff),
        headlineMedium = d.headlineMedium.copy(fontFamily = ff), headlineSmall = d.headlineSmall.copy(fontFamily = ff),
        titleLarge = d.titleLarge.copy(fontFamily = ff), titleMedium = d.titleMedium.copy(fontFamily = ff),
        titleSmall = d.titleSmall.copy(fontFamily = ff), bodyLarge = d.bodyLarge.copy(fontFamily = ff),
        bodyMedium = d.bodyMedium.copy(fontFamily = ff), bodySmall = d.bodySmall.copy(fontFamily = ff),
        labelLarge = d.labelLarge.copy(fontFamily = ff), labelMedium = d.labelMedium.copy(fontFamily = ff),
        labelSmall = d.labelSmall.copy(fontFamily = ff))
}



// ---------- Interactive Talking Tom & AI Study Buddy ----------

enum class BuddyTone { ROAST, HYPE, TIP }

val ROASTS = listOf(
    "Stop poking me and poke your textbook! 😤",
    "I checked your screen time... do you want to talk about it? 💀",
    "Is scrolling social media on your syllabus? Didn't think so. 💅",
    "You've been on '5 more minutes of break' for 40 minutes... 🤨",
    "Your phone screen won't get you that dream college seat! 📚",
    "If procrastination burned calories, you'd be a fitness model! 🏃",
    "I'm judging you silently. Actually, not silently: GO STUDY! 📢",
    "Feed me study hours or I will starve! ⏳",
    "Instagram stars don't even know you exist. Go study! 🌟"
)

val HYPE_QUIPS = listOf(
    "You're a genius in the making! Let's lock in! 🔥",
    "Future topper detected! Every minute studied counts! ⭐",
    "A 1-hour session today saves 10 hours of panic tomorrow! 🧘",
    "Brain gym in session! Building those focus muscles! 💪",
    "Best student right here! Let's conquer this syllabus! 🚀",
    "I believe in you 100%! Let's start the timer and crush it! 💖",
    "Small steps every day lead to massive exam results! 🏆"
)

val STUDY_TIPS = listOf(
    "Active recall tip: Close your notes and write what you remember from memory! 📖",
    "Pomodoro rule: 25 min deep focus, then 5 min stretch. Keeps brain fresh! 🍅",
    "Hydration check! Drink a glass of water right now, brain needs it! 💧",
    "Teach the concept to me! If you can explain it to your mascot, you know it! 🧠",
    "Keep phone in another room while timer runs. Out of sight, out of mind! 📵",
    "Hardest subject first when your willpower is at 100%! ⚡"
)

data class MascotChatMessage(val isUser: Boolean, val text: String, val time: String)

fun generateMascotAIAnswer(query: String, mascotName: String): String {
    val q = query.lowercase().trim()
    return when {
        q.contains("mitochondria") || q.contains("powerhouse") ->
            "Mitochondria are known as the **powerhouse of the cell**! ⚡\n\n" +
            "• **What they do**: They produce **ATP** (adenosine triphosphate), which is the usable chemical energy currency for our cells via cellular respiration.\n" +
            "• **Key parts**: Outer membrane, highly folded inner membrane (**cristae** to increase surface area), and the central matrix containing its own mitochondrial DNA!\n\n" +
            "Keep fueling your brain with energy, just like mitochondria do! You're doing awesome! 🌸"

        q.contains("feynman") ->
            "The **Feynman Technique** is the #1 way to truly understand any hard topic! 🧠✨\n\n" +
            "1. **Pick a concept**: Write the topic name at the top of a blank page.\n" +
            "2. **Teach it to a 10-year-old**: Explain it in simple, jargon-free words as if teaching someone else.\n" +
            "3. **Identify your knowledge gaps**: Whenever you get stuck or use complicated terms, go back to your notes and re-read.\n" +
            "4. **Simplify & use analogies**: Connect it to everyday life!\n\n" +
            "If you can't explain it simply, you don't understand it well enough yet! Try teaching it to me, $mascotName! 🐾"

        q.contains("active recall") || q.contains("memoriz") || q.contains("remember") ->
            "Here is the secret to super-memory: **Active Recall & Spaced Repetition**! 📚💡\n\n" +
            "• **Don't just re-read**: Passive re-reading is a trap! Close your book and write down everything you remember on a blank sheet (the *Blurting Method*).\n" +
            "• **Test yourself**: Use flashcards, quiz questions, or chapter-end problems.\n" +
            "• **Spaced Repetition intervals**: Review after 1 day, then 3 days, then 7 days, then 14 days. This flattens the *Ebbinghaus Forgetting Curve*!\n\n" +
            "Give it a try right after this study session! I'm cheering for you! (｡♥‿♥｡)"

        q.contains("pomodoro") ->
            "The **Pomodoro Technique** keeps your brain fresh and prevents burnout! 🍅✨\n\n" +
            "• **Classic**: 25 min deep focus ➔ 5 min short break.\n" +
            "• **After 4 rounds**: Take a longer 15–30 min break to recharge.\n" +
            "• **Pro tip**: StudyReward has built-in Pomodoro timers in Settings > Focus & Breaks! You can customize round lengths to match your natural attention span! 🌸"

        q.contains("quadratic") || q.contains("formula") && (q.contains("math") || q.contains("root")) ->
            "Here is the standard **Quadratic Formula**! 📐✨\n\n" +
            "For any equation in the form: **ax² + bx + c = 0**\n\n" +
            "The roots are:\n" +
            "**x = (-b ± √(b² - 4ac)) / (2a)**\n\n" +
            "• **Discriminant (D = b² - 4ac)**:\n" +
            "  - If D > 0: Two distinct real roots.\n" +
            "  - If D = 0: One repeated real root.\n" +
            "  - If D < 0: Two complex conjugate roots!\n\n" +
            "You've got the math magic in you! Solve one problem right now to lock it in! 🚀"

        q.contains("photosynthesis") ->
            "Here is **Photosynthesis** broken down simply! 🌱☀️\n\n" +
            "• **Equation**: 6CO₂ + 6H₂O + Sunlight ➔ C₆H₁₂O₆ (Glucose) + 6O₂\n" +
            "• **Where it happens**: Inside **chloroplasts** (using the green pigment chlorophyll).\n" +
            "• **Two stages**:\n" +
            "  1. *Light-dependent reactions*: In thylakoid membranes; water is split, oxygen is released, producing ATP and NADPH.\n" +
            "  2. *Calvin Cycle (Light-independent)*: In the stroma; carbon dioxide is fixed into glucose sugar!\n\n" +
            "Plants work hard making food, just like you working hard studying! 🌿"

        q.contains("newton") || (q.contains("law") && q.contains("motion")) ->
            "Here are **Newton's 3 Laws of Motion**! 🍎🚀\n\n" +
            "1. **Inertia**: An object stays at rest or in uniform motion unless acted upon by a net external force.\n" +
            "2. **F = ma**: Acceleration is directly proportional to net force and inversely proportional to mass.\n" +
            "3. **Action & Reaction**: For every action, there is an equal and opposite reaction!\n\n" +
            "Your study effort right now = positive force pushing your future forward! 💫"

        q.contains("schedule") || q.contains("routine") || q.contains("plan") ->
            "Here is a balanced, high-focus study routine for today! ⏰🌸\n\n" +
            "• **Block 1 (Deep Work, 90m)**: Tackle your hardest subject first (e.g. Math or Physics) while your brain is fresh.\n" +
            "• **Rest (15m)**: Stretch, drink water, take a walk away from screens.\n" +
            "• **Block 2 (Practice & Problems, 60m)**: Solve questions, past papers, or flashcards.\n" +
            "• **Block 3 (Revision, 30m)**: Summarize key formulas and notes.\n\n" +
            "Remember to reward yourself with screen time after! You deserve it! 🎮🍬"

        q.contains("motivat") || q.contains("tired") || q.contains("burnout") || q.contains("exhaust") || q.contains("stress") || q.contains("give up") ->
            "Hey cutie, take a deep breath. Inhale... exhale. 🌸💖\n\n" +
            "You have survived 100% of your hardest days so far. You don't have to be perfect; you just have to show up for the next 15 minutes!\n\n" +
            "• Drink a glass of cold water.\n" +
            "• Do a quick 2-minute stretch.\n" +
            "• Remember *why* you started this journey.\n\n" +
            "I believe in you with all my little mascot heart! I'm sitting right here studying with you. Let's do this together! (｡♥‿♥｡) ✨"

        q.contains("procrastinat") || q.contains("distract") || q.contains("focus") ->
            "Here is the **2-Minute Rule to beat procrastination**: ⚡\n\n" +
            "Tell yourself: *'I am only going to study for 2 minutes. If I still want to stop, I can.'*\n" +
            "90% of the battle is just starting! Once your brain enters the flow state, momentum takes over.\n\n" +
            "Put your phone face down, take out one pen, and open page 1. I am watching you, let's go! 🐾👀"

        else ->
            "That is an interesting question about \"$query\"! 🌟\n\n" +
            "• **Key Concept**: When studying this, always start with the fundamental definition, write down the core formula/keywords, and work through 2 practice examples.\n" +
            "• **Study tip**: Try summarizing this concept in exactly 3 bullet points in your notebook.\n" +
            "• **Need more details?** You can also add your free Google Gemini API key in Settings > Study Buddy AI to get full real-time answers for any complex college topic!\n\n" +
            "$mascotName is always here cheering you on! Let's keep studying! 🌸✨"
    }
}

// Smart Arithmetic Solver for AI
fun solveMathExpression(raw: String): String? {
    val q = raw.lowercase().trim()
        .replace("what is", "").replace("what's", "")
        .replace("calculate", "").replace("solve", "")
        .replace("evaluate", "").replace("?", "").replace("=", "").trim()

    // Match percentage: e.g. "15% of 200" or "20 percent of 50"
    val pctRegex = Regex("""(\d+(?:\.\d+)?)\s*(?:%|percent)\s*of\s*(\d+(?:\.\d+)?)""")
    pctRegex.find(q)?.let {
        val p = it.groupValues[1].toDoubleOrNull()
        val total = it.groupValues[2].toDoubleOrNull()
        if (p != null && total != null) {
            val res = (p / 100.0) * total
            val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else "%.2f".format(res)
            return "$p% of $total = $formatted"
        }
    }

    // Match simple binary math operations: 2+2, 15*8, 100/4, 25-9, 2^8
    val mathRegex = Regex("""(-?\d+(?:\.\d+)?)\s*([\+\-\*/xX\^])\s*(-?\d+(?:\.\d+)?)""")
    mathRegex.find(q)?.let {
        val a = it.groupValues[1].toDoubleOrNull()
        val op = it.groupValues[2]
        val b = it.groupValues[3].toDoubleOrNull()
        if (a != null && b != null) {
            val res = when (op) {
                "+" -> a + b
                "-" -> a - b
                "*", "x", "X" -> a * b
                "/" -> if (b != 0.0) a / b else null
                "^" -> Math.pow(a, b)
                else -> null
            }
            if (res != null) {
                val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else "%.4f".format(res).trimEnd('0').trimEnd('.')
                return "$raw = $formatted"
            }
        }
    }

    // Match square root: sqrt(144) or square root of 64
    val sqrtRegex = Regex("""(?:sqrt|square root of)\s*\(?\s*(\d+(?:\.\d+)?)\s*\)?""")
    sqrtRegex.find(q)?.let {
        val n = it.groupValues[1].toDoubleOrNull()
        if (n != null && n >= 0) {
            val res = Math.sqrt(n)
            val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else "%.4f".format(res).trimEnd('0').trimEnd('.')
            return "Square root of $n = $formatted"
        }
    }

    return null
}

fun smartLocalAIAnswer(query: String, mascotName: String): String {
    val q = query.lowercase().trim()

    // 1. Check math expressions first (e.g. 2+2, 15*8, 15% of 200)
    solveMathExpression(query)?.let { return "$it\n\nNeed me to explain how to solve similar problems step-by-step?" }

    // 2. Natural Greetings & Chat
    if (q in listOf("hey", "hello", "hi", "heya", "yo", "sup", "greetings")) {
        return "Hey there! Ready to get some quality focus time in? Tell me what subject or concept you're studying right now, and let's tackle it together!"
    }
    if (q.contains("how are you") || q.contains("how r u")) {
        return "I'm feeling energized and ready to help you study! How is your day going? What can we work on?"
    }
    if (q.contains("who are you") || q.contains("what are you") || q.contains("your name")) {
        return "I'm $mascotName, your personal AI study companion in StudyReward! I can solve math, break down complex topics using the Feynman technique, explain formulas, and help you stay focused."
    }
    if (q.contains("what can you do") || q.contains("help me with")) {
        return "Here is what I can do for you:\n" +
                "• Solve arithmetic and formulas (try typing 2+2 or 15*8)\n" +
                "• Explain concepts in Physics, Biology, Chemistry, Math, and Coding\n" +
                "• Share study techniques like Feynman and Pomodoro\n" +
                "• Give you a motivational push when you're feeling sluggish!"
    }
    if (q.contains("joke") || q.contains("funny")) {
        return "Why did the student eat their homework?\n\nBecause their teacher told them it was a piece of cake! Now back to studying!"
    }

    // 3. Subject-specific explanations
    if (q.contains("photosynthesis")) {
        return "**Photosynthesis** is how plants convert sunlight into energy:\n\n" +
                "• **Formula**: 6CO₂ + 6H₂O + Sunlight ➔ C₆H₁₂O₆ (Glucose) + 6O₂ (Oxygen)\n" +
                "• **Light Reactions**: Occur in the thylakoid membranes, splitting water and generating ATP + NADPH.\n" +
                "• **Calvin Cycle (Dark Reactions)**: Occurs in the stroma, using that ATP to synthesize glucose from CO₂."
    }
    if (q.contains("newton") && (q.contains("law") || q.contains("first") || q.contains("second") || q.contains("third"))) {
        return "**Newton's Three Laws of Motion**:\n\n" +
                "1. **Inertia**: An object stays at rest or in uniform motion unless acted upon by a net external force.\n" +
                "2. **F = ma**: Net force equals mass times acceleration.\n" +
                "3. **Action & Reaction**: For every action, there is an equal and opposite reaction."
    }
    if (q.contains("quadratic") || q.contains("-b")) {
        return "**The Quadratic Formula** solves equations of the form ax² + bx + c = 0:\n\n" +
                "**x = (-b ± √(b² - 4ac)) / (2a)**\n\n" +
                "• If b² - 4ac > 0: Two distinct real roots.\n" +
                "• If b² - 4ac = 0: One repeated real root.\n" +
                "• If b² - 4ac < 0: Two complex / imaginary roots."
    }
    if (q.contains("ohm") || q.contains("v=ir") || q.contains("resistance")) {
        return "**Ohm's Law**: **V = I × R**\n\n" +
                "• **V** = Voltage (Volts)\n" +
                "• **I** = Current (Amperes)\n" +
                "• **R** = Resistance (Ohms Ω)\n\n" +
                "Voltage pushes the electrical charge, current is the flow rate, and resistance opposes that flow!"
    }
    if (q.contains("mitochondria") || q.contains("powerhouse")) {
        return "**Mitochondria** are the powerhouses of the cell! They generate ATP through cellular respiration to fuel all cellular activities."
    }
    if (q.contains("feynman")) {
        return "**The Feynman Technique**:\n1. Choose a concept.\n2. Explain it in plain words to a 10-year-old.\n3. Identify gaps where you relied on jargon.\n4. Review and simplify with analogies!"
    }
    if (q.contains("pomodoro")) {
        return "**Pomodoro Technique**: 25 minutes of deep distraction-free focus, followed by a 5-minute break. After 4 rounds, take a 15-20 minute long break."
    }
    if (q.contains("procrastinat") || q.contains("lazy") || q.contains("not feel like")) {
        return "Procrastination is an emotional barrier, not a time management problem!\n\n" +
                "**Use the 2-Minute Rule**: Commit to studying for just 2 minutes. Open the book, write one sentence. Starting is 80% of the battle!"
    }

    return "That's an interesting question about \"" + query.take(40) + "\"!\n\n" +
            "To break this down simply: identify the core principles first, relate it to something you already know, and practice applying it with an example problem. What specific part can I explain in more detail?"
}

suspend fun queryLiveMascotAI(
    history: List<MascotChatMessage>,
    newQuestion: String,
    mascotName: String
): String = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
    // 1. Immediate arithmetic evaluation
    solveMathExpression(newQuestion)?.let { return@withContext "$it\n\nNeed me to show the step-by-step breakdown?" }

    // 2. Try free live LLM endpoint if online
    try {
        val encodedPrompt = java.net.URLEncoder.encode(
            "You are $mascotName, an intelligent and friendly AI study assistant. Answer clearly, accurately, and concisely: $newQuestion",
            "UTF-8"
        )
        val url = java.net.URL("https://text.pollinations.ai/$encodedPrompt")
        val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 4000
            readTimeout = 6000
            setRequestProperty("User-Agent", "Mozilla/5.0")
        }
        if (conn.responseCode == 200) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }.trim()
            if (responseText.isNotBlank() && !responseText.contains("<!DOCTYPE") && responseText.length > 3) {
                return@withContext responseText
            }
        }
    } catch (_: Exception) {}

    // 3. Robust offline reasoning & knowledge engine
    return@withContext smartLocalAIAnswer(newQuestion, mascotName)
}

@Composable
fun MascotAIAssistantDialog(
    mascotName: String,
    mascotType: MascotType,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(
                MascotChatMessage(
                    isUser = false,
                    text = "Hi cutie! I'm $mascotName, your personal AI study assistant! 🌸✨ Ask me anything about your subjects, difficult concepts, formulas, memory tricks, or study routines! (｡♥‿♥｡)",
                    time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                )
            )
        )
    }
    var isThinking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val quickPills = listOf(
        "🧠 Feynman Technique",
        "🍅 Pomodoro Rules",
        "⚡ Beat Procrastination",
        "📐 Quadratic Formula",
        "🔬 Mitochondria",
        "🌱 Photosynthesis",
        "💖 Give me motivation"
    )

    fun sendQuestion(userText: String) {
        val q = userText.trim()
        if (q.isBlank()) return
        val nowTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val currentHistory = messages
        messages = messages + MascotChatMessage(isUser = true, text = q, time = nowTime)
        query = ""
        isThinking = true

        scope.launch {
            try { listState.animateScrollToItem(messages.size - 1) } catch (e: Exception) { }
            val answer = queryLiveMascotAI(currentHistory, q, mascotName)
            messages = messages + MascotChatMessage(isUser = false, text = answer, time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()))
            isThinking = false
            try { listState.animateScrollToItem(messages.size - 1) } catch (e: Exception) { }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                        MascotCanvas(type = mascotType, mood = MascotMood.CHEERING, modifier = Modifier.size(38.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("$mascotName AI Study Assistant ✨", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Online & ready to help you learn! 🌸", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss) {
                        Text("✕", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Quick Suggestion Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    quickPills.forEach { pill ->
                        Text(
                            pill,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .clickable { sendQuestion(pill) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Chat Messages
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    items(messages) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                        ) {
                            if (!msg.isUser) {
                                Text(mascotType.emoji, fontSize = 16.sp, modifier = Modifier.padding(end = 6.dp, top = 4.dp))
                            }
                            Column(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .clip(RoundedCornerShape(
                                        topStart = 16.dp, topEnd = 16.dp,
                                        bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                        bottomEnd = if (msg.isUser) 4.dp else 16.dp
                                    ))
                                    .background(
                                        if (msg.isUser) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    msg.text,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    msg.time,
                                    fontSize = 10.sp,
                                    color = (if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.7f),
                                    modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
                                )
                            }
                        }
                    }
                    if (isThinking) {
                        item {
                            Text(
                                "$mascotName is thinking... ✨",
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                // Input Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Ask anything about your studies... 🌸", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { sendQuestion(query) },
                        shape = CircleShape,
                        enabled = query.isNotBlank() && !isThinking,
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        Text("🚀", fontSize = 16.sp)
                    }
                }
            }
        }
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
    var tone by remember { mutableStateOf(BuddyTone.ROAST) }
    var currentSpeech by remember { mutableStateOf(ROASTS.random()) }
    var pokeAnim by remember { mutableStateOf(false) }
    var isDizzy by remember { mutableStateOf(false) }
    val pokeTimestamps = remember { mutableStateListOf<Long>() }
    var showAIDialog by remember { mutableStateOf(false) }
    val view = androidx.compose.ui.platform.LocalView.current
    val ctx = androidx.compose.ui.platform.LocalContext.current

    // TextToSpeech Talking Tom voice (pitch 1.65f, brisk 1.15f)
    var tts: android.speech.tts.TextToSpeech? by remember { mutableStateOf(null) }
    var isMuted by remember { mutableStateOf(false) }

    DisposableEffect(ctx) {
        var t: android.speech.tts.TextToSpeech? = null
        t = android.speech.tts.TextToSpeech(ctx) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                t?.language = java.util.Locale.US
                t?.setPitch(1.65f)
                t?.setSpeechRate(1.15f)
            }
        }
        tts = t
        onDispose {
            t?.stop()
            t?.shutdown()
        }
    }

    fun speak(text: String) {
        if (!isMuted) {
            val clean = text.replace(Regex("[^a-zA-Z0-9 '.,!?]"), " ").trim()
            if (clean.isNotBlank()) {
                tts?.speak(clean, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "mascot_speech")
            }
        }
    }

    val DIZZY_QUIPS = remember {
        listOf(
            "Whooooaaa... my head is spinning! 💫😵",
            "Stop poking meee! I see stars! ⭐💫",
            "W-w-woah! Earth to buddy! Everything is wobbly! 🌀",
            "Too... many... pokes! Dizzy mode activated! 😵‍💫",
            "I'm seeing double! Or maybe triple?! 🌟🥴"
        )
    }

    // Infinite live breathing & idle hop
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
    val dizzyShake by inf.animateFloat(
        initialValue = -22f, targetValue = 22f,
        animationSpec = infiniteRepeatable(tween(110, easing = androidx.compose.animation.core.LinearEasing), RepeatMode.Reverse),
        label = "dizzyShake"
    )

    // Spring bouncy jump & wiggle on tap / poke!
    val jumpOffsetY by animateFloatAsState(
        targetValue = if (pokeAnim) -35f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "jump"
    )
    val wiggleAngle by animateFloatAsState(
        targetValue = if (isDizzy) dizzyShake else if (pokeAnim) 20f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        label = "wiggle"
    )
    val squishScale by animateFloatAsState(
        targetValue = if (pokeAnim) 1.20f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "squish"
    )

    val baseMood = when {
        goalMins > 0 && studiedSecs >= goalMins * 60 -> MascotMood.CHEERING
        studiedSecs > 0 -> MascotMood.CHEERING
        else -> MascotMood.SLEEPING
    }

    fun onPoke() {
        val now = System.currentTimeMillis()
        pokeTimestamps.removeAll { now - it > 2000L }
        pokeTimestamps.add(now)

        try { view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP) } catch (e: Exception) { }
        pokeAnim = true

        if (pokeTimestamps.size >= 3) {
            isDizzy = true
            currentSpeech = DIZZY_QUIPS.random()
            speak(currentSpeech)
        } else {
            currentSpeech = when (tone) {
                BuddyTone.ROAST -> ROASTS.random()
                BuddyTone.HYPE -> HYPE_QUIPS.random()
                BuddyTone.TIP -> STUDY_TIPS.random()
            }
            speak(currentSpeech)
        }
    }

    LaunchedEffect(pokeAnim) {
        if (pokeAnim) {
            delay(350)
            pokeAnim = false
        }
    }

    LaunchedEffect(isDizzy) {
        if (isDizzy) {
            delay(3000)
            isDizzy = false
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
            // Interactive Talking Tom Mascot Column with Change button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .graphicsLayer {
                            translationY = jumpOffsetY + (if (isDizzy) 0f else idleHop)
                            rotationZ = wiggleAngle + (if (isDizzy) dizzyShake else idleWiggle)
                            scaleX = squishScale
                            scaleY = 2f - squishScale
                        }
                        .clip(CircleShape)
                        .clickable { onPoke() },
                    contentAlignment = Alignment.Center
                ) {
                    MascotCanvas(
                        type = type,
                        mood = if (isDizzy) MascotMood.DIZZY else if (pokeAnim) MascotMood.CHEERING else baseMood,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenCompanionPicker() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "Change 🐾",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // Speech bubble & action buttons
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(type.emoji, fontSize = 15.sp)
                    Spacer(Modifier.weight(1f))

                    // Explicit Swap Mascot Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clickable { onOpenCompanionPicker() }
                    ) {
                        Text(
                            "🐾 Swap Mascot",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Voice Mute Toggle
                    Text(
                        if (isMuted) "🔇 Muted" else "🔊 Voice",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isMuted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isMuted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .clickable {
                                isMuted = !isMuted
                                if (!isMuted) speak("Talking Tom voice is ready!")
                            }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    // Ask AI button
                    Text(
                        "Ask AI ✨",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { showAIDialog = true }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(6.dp))

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
                        text = currentSpeech,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Tone selector buttons (Roast / Hype / Tips) + Choose mascot
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    BuddyTone.ROAST to "Roast 🔥",
                    BuddyTone.HYPE to "Hype ⚡",
                    BuddyTone.TIP to "Tip 💡"
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
                                speak(currentSpeech)
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.clickable { onOpenCompanionPicker() }
            ) {
                Text(
                    "🐾 Mascot Picker",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }
        }
    }
}

// ---------- Mascot Component (Canvas Drawn) ----------

enum class MascotType(val id: String, val displayName: String, val defaultName: String, val emoji: String) {
    MOCHI("mochi", "Mochi Sprout", "Mochi", "🌱"),
    BUNNY("bunny", "Cloud Bunny", "Boba", "🐰"),
    KITTY("kitty", "Peach Kitty", "Mimi", "🐱"),
    BEAR("bear", "Honey Bear", "Kuma", "🐻"),
    PENGUIN("penguin", "Pudding Penguin", "Pipi", "🐧");

    companion object {
        fun fromId(id: String) = entries.find { it.id == id } ?: MOCHI
    }
}

enum class MascotMood {
    SLEEPING,
    FOCUSED,
    CHEERING,
    TIRED,
    DIZZY
}

@Composable
fun MascotCanvas(type: MascotType, mood: MascotMood, modifier: Modifier = Modifier) {
    val infTransition = rememberInfiniteTransition(label = "mascot")
    val bounce by infTransition.animateFloat(
        initialValue = 0f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bounce"
    )
    val earWiggle by infTransition.animateFloat(
        initialValue = -2f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ear"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f + bounce

        // Shadow under mascot
        drawOval(
            color = Color.Black.copy(alpha = 0.08f),
            topLeft = Offset(cx - w * 0.32f, h * 0.88f),
            size = Size(w * 0.64f, h * 0.12f)
        )

        when (type) {
            MascotType.MOCHI -> {
                // Main dumpling body (soft warm cream)
                drawOval(
                    color = Color(0xFFFFF9E6),
                    topLeft = Offset(cx - w * 0.38f, cy - h * 0.32f),
                    size = Size(w * 0.76f, h * 0.68f)
                )
                // Outline
                drawOval(
                    color = Color(0xFF5A4A3A).copy(alpha = 0.25f),
                    topLeft = Offset(cx - w * 0.38f, cy - h * 0.32f),
                    size = Size(w * 0.76f, h * 0.68f),
                    style = Stroke(width = 3.dp.toPx())
                )
                // Tiny green double leaf sprout
                val sproutStemY = cy - h * 0.32f
                val stemPath = Path().apply {
                    moveTo(cx, sproutStemY + 4f)
                    quadraticTo(cx - 2f, sproutStemY - 16f, cx + earWiggle, sproutStemY - 26f)
                }
                drawPath(stemPath, Color(0xFF589B5C), style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                // Left leaf
                drawOval(
                    color = Color(0xFF66BB6A),
                    topLeft = Offset(cx - 16f + earWiggle, sproutStemY - 32f),
                    size = Size(14f, 9f)
                )
                // Right leaf
                drawOval(
                    color = Color(0xFF81C784),
                    topLeft = Offset(cx + 2f + earWiggle, sproutStemY - 34f),
                    size = Size(15f, 10f)
                )
            }
            MascotType.BUNNY -> {
                val earH = h * 0.42f
                val earW = w * 0.16f
                // Left Ear
                drawOval(
                    color = Color(0xFFFFF7FA),
                    topLeft = Offset(cx - w * 0.28f + earWiggle, cy - h * 0.55f),
                    size = Size(earW, earH)
                )
                drawOval(
                    color = Color(0xFFFFB6C1),
                    topLeft = Offset(cx - w * 0.25f + earWiggle, cy - h * 0.50f),
                    size = Size(earW * 0.65f, earH * 0.70f)
                )
                // Right Ear
                drawOval(
                    color = Color(0xFFFFF7FA),
                    topLeft = Offset(cx + w * 0.12f - earWiggle, cy - h * 0.55f),
                    size = Size(earW, earH)
                )
                drawOval(
                    color = Color(0xFFFFB6C1),
                    topLeft = Offset(cx + w * 0.15f - earWiggle, cy - h * 0.50f),
                    size = Size(earW * 0.65f, earH * 0.70f)
                )
                // Bunny Body
                drawOval(
                    color = Color(0xFFFFF7FA),
                    topLeft = Offset(cx - w * 0.36f, cy - h * 0.30f),
                    size = Size(w * 0.72f, h * 0.65f)
                )
                drawOval(
                    color = Color(0xFF5A4A3A).copy(alpha = 0.20f),
                    topLeft = Offset(cx - w * 0.36f, cy - h * 0.30f),
                    size = Size(w * 0.72f, h * 0.65f),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            MascotType.KITTY -> {
                // Left Cat Ear
                val leftEar = Path().apply {
                    moveTo(cx - w * 0.32f, cy - h * 0.15f)
                    lineTo(cx - w * 0.35f, cy - h * 0.45f)
                    lineTo(cx - w * 0.10f, cy - h * 0.28f)
                    close()
                }
                drawPath(leftEar, Color(0xFFFFF0E6))
                val leftInner = Path().apply {
                    moveTo(cx - w * 0.30f, cy - h * 0.18f)
                    lineTo(cx - w * 0.32f, cy - h * 0.40f)
                    lineTo(cx - w * 0.14f, cy - h * 0.27f)
                    close()
                }
                drawPath(leftInner, Color(0xFFFFB4A2))

                // Right Cat Ear
                val rightEar = Path().apply {
                    moveTo(cx + w * 0.32f, cy - h * 0.15f)
                    lineTo(cx + w * 0.35f, cy - h * 0.45f)
                    lineTo(cx + w * 0.10f, cy - h * 0.28f)
                    close()
                }
                drawPath(rightEar, Color(0xFFFFF0E6))
                val rightInner = Path().apply {
                    moveTo(cx + w * 0.30f, cy - h * 0.18f)
                    lineTo(cx + w * 0.32f, cy - h * 0.40f)
                    lineTo(cx + w * 0.14f, cy - h * 0.27f)
                    close()
                }
                drawPath(rightInner, Color(0xFFFFB4A2))

                // Kitty Body
                drawOval(
                    color = Color(0xFFFFF0E6),
                    topLeft = Offset(cx - w * 0.36f, cy - h * 0.30f),
                    size = Size(w * 0.72f, h * 0.65f)
                )
                drawOval(
                    color = Color(0xFF5A4A3A).copy(alpha = 0.20f),
                    topLeft = Offset(cx - w * 0.36f, cy - h * 0.30f),
                    size = Size(w * 0.72f, h * 0.65f),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            MascotType.BEAR -> {
                // Bear round ears
                drawCircle(Color(0xFFE4C9B0), radius = w * 0.15f, center = Offset(cx - w * 0.26f, cy - h * 0.28f))
                drawCircle(Color(0xFFC7A283), radius = w * 0.08f, center = Offset(cx - w * 0.26f, cy - h * 0.28f))
                drawCircle(Color(0xFFE4C9B0), radius = w * 0.15f, center = Offset(cx + w * 0.26f, cy - h * 0.28f))
                drawCircle(Color(0xFFC7A283), radius = w * 0.08f, center = Offset(cx + w * 0.26f, cy - h * 0.28f))

                // Bear body
                drawOval(
                    color = Color(0xFFE4C9B0),
                    topLeft = Offset(cx - w * 0.36f, cy - h * 0.30f),
                    size = Size(w * 0.72f, h * 0.65f)
                )
                drawOval(
                    color = Color(0xFF5A4A3A).copy(alpha = 0.20f),
                    topLeft = Offset(cx - w * 0.36f, cy - h * 0.30f),
                    size = Size(w * 0.72f, h * 0.65f),
                    style = Stroke(width = 3.dp.toPx())
                )
                // Bear Muzzle
                drawOval(
                    color = Color(0xFFFFF3E6),
                    topLeft = Offset(cx - w * 0.16f, cy + h * 0.02f),
                    size = Size(w * 0.32f, h * 0.22f)
                )
                drawCircle(Color(0xFF4A3525), radius = 5.dp.toPx(), center = Offset(cx, cy + h * 0.08f))
            }
            MascotType.PENGUIN -> {
                // Dark outer body
                drawOval(
                    color = Color(0xFF2B3A4A),
                    topLeft = Offset(cx - w * 0.36f, cy - h * 0.32f),
                    size = Size(w * 0.72f, h * 0.68f)
                )
                // White belly
                drawOval(
                    color = Color(0xFFFFFFFF),
                    topLeft = Offset(cx - w * 0.24f, cy - h * 0.18f),
                    size = Size(w * 0.48f, h * 0.52f)
                )
                // Little yellow feet
                drawOval(Color(0xFFFFB300), Offset(cx - w * 0.22f, cy + h * 0.32f), Size(w * 0.18f, h * 0.08f))
                drawOval(Color(0xFFFFB300), Offset(cx + w * 0.04f, cy + h * 0.32f), Size(w * 0.18f, h * 0.08f))
                // Cute beak
                val beak = Path().apply {
                    moveTo(cx - 7f, cy + 4f)
                    lineTo(cx + 7f, cy + 4f)
                    lineTo(cx, cy + 16f)
                    close()
                }
                drawPath(beak, Color(0xFFFF9800))
            }
        }

        // Rosy Cheeks (proportional so it looks cute on both tiny 40dp and large 180dp canvases)
        val cheekRadius = (w * 0.055f).coerceAtLeast(1.5f)
        val cheekY = cy + h * 0.08f
        drawCircle(Color(0xFFFF8FA3).copy(alpha = 0.35f), radius = cheekRadius, center = Offset(cx - w * 0.20f, cheekY))
        drawCircle(Color(0xFFFF8FA3).copy(alpha = 0.35f), radius = cheekRadius, center = Offset(cx + w * 0.20f, cheekY))

        // Eyes & Mouth according to Mood
        val eyeY = cy - h * 0.02f
        val eyeDist = w * 0.15f
        val strokeW = 3.dp.toPx()

        when (mood) {
            MascotMood.SLEEPING -> {
                // Peaceful closed curved eyes ( ‿ ‿ )
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = 10f, sweepAngle = 160f, useCenter = false,
                    topLeft = Offset(cx - eyeDist - 10f, eyeY - 6f), size = Size(20f, 14f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = 10f, sweepAngle = 160f, useCenter = false,
                    topLeft = Offset(cx + eyeDist - 10f, eyeY - 6f), size = Size(20f, 14f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                // Little z z Z floating
                val zOff = (bounce * 1.5f).toInt()
                drawCircle(Color(0xFF8E63E6).copy(alpha = 0.5f), radius = 2.dp.toPx(), center = Offset(cx + w * 0.32f, cy - h * 0.28f - zOff))
                drawCircle(Color(0xFF8E63E6).copy(alpha = 0.7f), radius = 3.dp.toPx(), center = Offset(cx + w * 0.38f, cy - h * 0.38f - zOff))
            }
            MascotMood.FOCUSED -> {
                // Round determined eyes with catchlights
                drawCircle(Color(0xFF2D253B), radius = 6.dp.toPx(), center = Offset(cx - eyeDist, eyeY))
                drawCircle(Color.White, radius = 2.5f.dp.toPx(), center = Offset(cx - eyeDist - 2f, eyeY - 2f))
                drawCircle(Color(0xFF2D253B), radius = 6.dp.toPx(), center = Offset(cx + eyeDist, eyeY))
                drawCircle(Color.White, radius = 2.5f.dp.toPx(), center = Offset(cx + eyeDist - 2f, eyeY - 2f))

                // Tiny study headband (hachimaki) across forehead
                val bandY = cy - h * 0.16f
                drawLine(
                    color = Color(0xFFFF5252),
                    start = Offset(cx - w * 0.32f, bandY),
                    end = Offset(cx + w * 0.32f, bandY),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Center sun on headband
                drawCircle(Color.White, radius = 4.dp.toPx(), center = Offset(cx, bandY))
            }
            MascotMood.CHEERING -> {
                // Happy curved eyes (^ ^)
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = 190f, sweepAngle = 160f, useCenter = false,
                    topLeft = Offset(cx - eyeDist - 10f, eyeY - 8f), size = Size(20f, 16f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = 190f, sweepAngle = 160f, useCenter = false,
                    topLeft = Offset(cx + eyeDist - 10f, eyeY - 8f), size = Size(20f, 16f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                // Big happy smile
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = 0f, sweepAngle = 180f, useCenter = false,
                    topLeft = Offset(cx - 9f, eyeY + 4f), size = Size(18f, 14f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                // Tiny sparkle stars
                drawCircle(Color(0xFFFFD54F), radius = 3.dp.toPx(), center = Offset(cx - w * 0.38f, cy - h * 0.20f))
                drawCircle(Color(0xFFFFD54F), radius = 3.dp.toPx(), center = Offset(cx + w * 0.38f, cy - h * 0.20f))
            }
            MascotMood.TIRED -> {
                // Sad droopy eyes ( •́ ̯•̀ )
                drawCircle(Color(0xFF2D253B), radius = 5.dp.toPx(), center = Offset(cx - eyeDist, eyeY + 2f))
                drawCircle(Color(0xFF2D253B), radius = 5.dp.toPx(), center = Offset(cx + eyeDist, eyeY + 2f))
                // Inverted tiny sad mouth
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = 200f, sweepAngle = 140f, useCenter = false,
                    topLeft = Offset(cx - 7f, eyeY + 12f), size = Size(14f, 10f),
                    style = Stroke(width = 2.5f.dp.toPx(), cap = StrokeCap.Round)
                )
                // Tear drop on right cheek
                drawCircle(Color(0xFF42A5F5), radius = 3.dp.toPx(), center = Offset(cx + w * 0.22f, cheekY + 8f))
            }
            MascotMood.DIZZY -> {
                // Cartoon dizzy spirals for eyes (@@)
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = (bounce * 25f), sweepAngle = 280f, useCenter = false,
                    topLeft = Offset(cx - eyeDist - 8f, eyeY - 8f), size = Size(16f, 16f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = (bounce * 25f) + 140f, sweepAngle = 240f, useCenter = false,
                    topLeft = Offset(cx - eyeDist - 4f, eyeY - 4f), size = Size(8f, 8f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = (-bounce * 25f), sweepAngle = 280f, useCenter = false,
                    topLeft = Offset(cx + eyeDist - 8f, eyeY - 8f), size = Size(16f, 16f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = (-bounce * 25f) + 140f, sweepAngle = 240f, useCenter = false,
                    topLeft = Offset(cx + eyeDist - 4f, eyeY - 4f), size = Size(8f, 8f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                // Wavy dizzy mouth (~)
                val mouthPath = Path().apply {
                    moveTo(cx - 10f, eyeY + 10f)
                    quadraticTo(cx - 5f, eyeY + 6f, cx, eyeY + 10f)
                    quadraticTo(cx + 5f, eyeY + 14f, cx + 10f, eyeY + 10f)
                }
                drawPath(mouthPath, Color(0xFF2D253B), style = Stroke(width = strokeW, cap = StrokeCap.Round))
                // Orbiting stars / dizzy sparkles
                drawCircle(Color(0xFFFFD54F), radius = 4.dp.toPx(), center = Offset(cx - w * 0.28f, cy - h * 0.38f - bounce))
                drawCircle(Color(0xFFFFB300), radius = 3.dp.toPx(), center = Offset(cx, cy - h * 0.44f + bounce))
                drawCircle(Color(0xFFFFD54F), radius = 4.dp.toPx(), center = Offset(cx + w * 0.28f, cy - h * 0.38f - bounce))
            }
        }
    }
}


@Composable
fun MascotDialog(
    currentType: MascotType,
    currentName: String,
    onSave: (MascotType, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selType by remember { mutableStateOf(currentType) }
    var nameText by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Your Study Companion ✨", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    MascotCanvas(type = selType, mood = MascotMood.CHEERING, modifier = Modifier.size(96.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("Choose character:", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    MascotType.entries.forEach { t ->
                        val sel = t == selType
                        Row(
                            Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    selType = t
                                    if (nameText.isBlank() || nameText == currentType.defaultName) {
                                        nameText = t.defaultName
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(t.emoji, fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                t.displayName,
                                fontSize = 13.sp,
                                color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Companion's Name:", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it.take(20) },
                    singleLine = true,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = nameText.trim().ifBlank { selType.defaultName }
                    onSave(selType, finalName)
                },
                shape = RoundedCornerShape(50)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MascotCard(
    type: MascotType,
    name: String,
    mood: MascotMood,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MascotCanvas(type = type, mood = mood, modifier = Modifier.size(72.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.width(6.dp))
                Text(type.emoji, fontSize = 16.sp)
            }
            Text(
                subtitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

fun scheme(t: AppTheme) = (if (t.light) lightColorScheme() else darkColorScheme()).copy(
    background = Color(t.bg), surface = Color(t.bg), surfaceVariant = Color(t.card),
    secondaryContainer = Color(t.sel), onSurface = Color(t.text), onBackground = Color(t.text),
    primary = Color(t.accent), onPrimary = if (t.light) Color.White else Color.Black,
    surfaceContainerHigh = Color(t.card).copy(alpha = 1f), onSurfaceVariant = Color(t.text).copy(alpha = 0.68f),
    outlineVariant = Color(t.text).copy(alpha = 0.14f))


class MainActivity : ComponentActivity() {
    companion object {
        var pendingInviteCode: String? = null
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIncomingLink(intent)
    }

    private fun handleIncomingLink(intent: Intent?) {
        val uri = intent?.data ?: return
        val code = when {
            uri.getQueryParameter("code") != null -> uri.getQueryParameter("code")
            uri.getQueryParameter("join") != null -> uri.getQueryParameter("join")
            uri.pathSegments.isNotEmpty() -> uri.pathSegments.last()
            !uri.host.isNullOrBlank() && uri.scheme == "studyreward" -> uri.host
            else -> null
        }
        if (!code.isNullOrBlank()) {
            pendingInviteCode = code.trim().uppercase()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            if (cm != null && cm.hasPrimaryClip()) {
                val clipText = cm.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                val clean = clipText.trim()
                val regex = Regex("""(?:(?:join/|code=|join=|))([A-Z0-9]{6})""", RegexOption.IGNORE_CASE)
                val match = regex.find(clean)
                if (match != null && pendingInviteCode == null) {
                    val foundCode = match.groupValues[1].uppercase()
                    val lastPrompted = getSharedPreferences("prefs", MODE_PRIVATE).getString("last_clipboard_code", "")
                    if (foundCode != lastPrompted) {
                        getSharedPreferences("prefs", MODE_PRIVATE).edit().putString("last_clipboard_code", foundCode).apply()
                        pendingInviteCode = foundCode
                    }
                }
            }
        } catch (_: Exception) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingLink(intent)
        fonts = loadFonts(this)
        val sp = getSharedPreferences("prefs", MODE_PRIVATE)
        if (sp.getBoolean("blk_on", false) && !isFull(this)) {
            UsageBlockService.start(this)
        }
        setContent {
            val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
            if (!prefs.contains("themeV3")) prefs.edit().putInt("theme", 0).putBoolean("themeV3", true).apply()
            var themeIdx by remember { mutableStateOf(prefs.getInt("theme", 0).coerceIn(0, themes.size - 1)) }
            var fontIdx by remember { mutableStateOf(fonts.indexOfFirst { it.first == prefs.getString("fontName", "Default") }.coerceAtLeast(0)) }
            MaterialTheme(colorScheme = scheme(themes[themeIdx]), typography = typographyFor(fonts[fontIdx].second)) {
                Box(Modifier.fillMaxSize()) {
                    val t = themes[themeIdx]
                    if (t.scene.isNotEmpty()) SceneBackground(t.scene, Color(t.bg))
                    App(themeIdx, { themeIdx = it; prefs.edit().putInt("theme", it).apply() },
                        fontIdx, { fontIdx = it; prefs.edit().putString("fontName", fonts[it].first).apply() })
                }
            }
        }
    }
}

suspend fun signIn(ctx: Context) {
    val option = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(ctx.getString(R.string.default_web_client_id)).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
    val cred = CredentialManager.create(ctx).getCredential(ctx, request).credential
    if (cred is CustomCredential && cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
        FirebaseAuth.getInstance()
            .signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
    }
}

// Reward tasks pay 1.5x the study rate, so they are always worth more than just studying
fun rewardSecs(dur: Long, rate: Int) = dur * rate * 3 / 2
fun dateKey(c: Calendar = Calendar.getInstance()) = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.time)
fun pickDate(ctx: Context, onDate: (Calendar) -> Unit) {
    val c = Calendar.getInstance()
    DatePickerDialog(ctx, { _, y, m, d ->
        onDate(Calendar.getInstance().apply { set(y, m, d, 12, 0, 0) })
    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
}
var nickname: String = ""
fun myName(): String = nickname.ifBlank { FirebaseAuth.getInstance().currentUser?.displayName ?: "Friend" }
fun hm(s: Long) = "${s / 3600}h ${(s % 3600) / 60}m"
// A request is approved when at least half of the OTHER members vote yes
fun needed(g: Group) = Math.ceil((g.members.size - 1) / 2.0).toInt()
fun yesVotes(r: Req, g: Group) = r.yes.count { it in g.members && it != r.fromUid }
fun noVotes(r: Req, g: Group) = r.no.count { it in g.members && it != r.fromUid }
fun isApproved(r: Req, g: Group) = yesVotes(r, g) >= maxOf(1, needed(g))
fun isRejected(r: Req, g: Group) = noVotes(r, g) > (maxOf(1, g.members.size - 1) - needed(g))
// Approved requests are honoured in order until the group's shared pool for the day runs out
fun acceptedReqs(g: Group, list: List<Req>): List<Req> {
    var used = 0L
    val poolLimit = if (g.pool > 0) g.pool.toLong() else 120L
    return list.filter { isApproved(it, g) }.sortedBy { it.at }.filter {
        if (used + it.mins <= poolLimit) { used += it.mins; true } else false
    }
}
// Someone counts as studying if their last update is recent (a heartbeat is sent every minute)
fun isLive(m: Member, now: Long) = m.studying && now - m.asOf < 150_000
fun liveStudy(m: Member, now: Long) = if (isLive(m, now)) m.study + (now - m.asOf) / 1000 else m.study
fun liveTotal(m: Member, now: Long) = if (isLive(m, now)) m.total + (now - m.asOf) / 1000 else m.total
fun notifyUser(ctx: Context, title: String, text: String) {
    try {
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= 26 && nm.getNotificationChannel("study") == null) {
            val channel = android.app.NotificationChannel(
                "study",
                "Study & Timetable Reminders",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for upcoming study blocks, lectures, and earned playtime"
                enableVibration(true)
            }
            nm.createNotificationChannel(channel)
        }
        val pi = android.app.PendingIntent.getActivity(
            ctx, 0,
            ctx.packageManager.getLaunchIntentForPackage(ctx.packageName) ?: Intent(),
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notif = androidx.core.app.NotificationCompat.Builder(ctx, "study")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        val notifId = 1000 + (System.currentTimeMillis() % 9000).toInt()
        nm.notify(notifId, notif)
    } catch (e: Exception) { }
}
fun mmss(s: Long) = "%d:%02d".format(s / 60, s % 60)
fun startOfDay() = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis
fun clock(s: Long) = "%d:%02d:%02d".format(s / 3600, (s % 3600) / 60, s % 60)

// ==================== PROCEDURAL AMBIENT STUDY SOUND ENGINE ====================
object AmbientStudySoundEngine {
    private var track: AudioTrack? = null
    @Volatile private var isRunning = false
    var currentSound = "lofi"
    var volume = 0.70f
    private var worker: Thread? = null

    val isPlaying: Boolean get() = isRunning

    fun play(type: String) {
        currentSound = type
        if (isRunning) return
        isRunning = true
        startEngine()
    }

    fun stop() {
        isRunning = false
        try {
            track?.stop()
            track?.release()
        } catch (_: Exception) {}
        track = null
        worker?.interrupt()
        worker = null
    }

    fun toggle(type: String = currentSound): Boolean {
        return if (isRunning && currentSound == type) {
            stop()
            false
        } else {
            stop()
            play(type)
            true
        }
    }

    private fun startEngine() {
        worker = Thread {
            val sampleRate = 44100
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            try {
                val t = AudioTrack(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                    bufferSize * 2,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
                )
                t.setVolume(volume)
                t.play()
                track = t

                val buf = ShortArray(bufferSize)
                var brown1 = 0f
                var brown2 = 0f
                var phaseLofi1 = 0.0
                var phaseLofi2 = 0.0
                var phaseLofi3 = 0.0
                var phaseZen1 = 0.0
                var phaseZen2 = 0.0
                var phaseBin1 = 0.0
                var phaseBin2 = 0.0
                var sampleIndex = 0L

                // Lo-Fi chord frequencies: Cmaj9, Am7, Fmaj7, Gsus4
                val chordFreqs = listOf(
                    listOf(261.63, 329.63, 392.00, 493.88), // Cmaj7
                    listOf(220.00, 261.63, 329.63, 392.00), // Am7
                    listOf(174.61, 220.00, 261.63, 329.63), // Fmaj7
                    listOf(196.00, 261.63, 293.66, 392.00)  // Gsus4
                )
                val chordDuration = 44100 * 4 // 4 seconds per chord

                while (isRunning) {
                    val sType = currentSound
                    for (i in 0 until bufferSize) {
                        val sample: Float = when (sType) {
                            "lofi" -> {
                                // Warm, soothing electric piano lo-fi chords in pentatonic major
                                val chordIdx = ((sampleIndex / chordDuration) % chordFreqs.size).toInt()
                                val chord = chordFreqs[chordIdx]
                                val chordPos = (sampleIndex % chordDuration).toDouble() / chordDuration
                                // Gentle attack and slow exponential warm decay
                                val env = if (chordPos < 0.08) (chordPos / 0.08) else Math.exp(-2.5 * (chordPos - 0.08))

                                phaseLofi1 += 2.0 * Math.PI * chord[0] / sampleRate
                                phaseLofi2 += 2.0 * Math.PI * chord[1] / sampleRate
                                phaseLofi3 += 2.0 * Math.PI * chord[2] / sampleRate
                                if (phaseLofi1 > 2.0 * Math.PI) phaseLofi1 -= 2.0 * Math.PI
                                if (phaseLofi2 > 2.0 * Math.PI) phaseLofi2 -= 2.0 * Math.PI
                                if (phaseLofi3 > 2.0 * Math.PI) phaseLofi3 -= 2.0 * Math.PI

                                val s1 = Math.sin(phaseLofi1) + 0.25 * Math.sin(2.0 * phaseLofi1)
                                val s2 = Math.sin(phaseLofi2) + 0.20 * Math.sin(2.0 * phaseLofi2)
                                val s3 = Math.sin(phaseLofi3) + 0.15 * Math.sin(2.0 * phaseLofi3)
                                ((s1 + s2 + s3) * 0.18 * env).toFloat().coerceIn(-1f, 1f)
                            }
                            "rain" -> {
                                // Deep, cozy low-pass filtered rain (zero harsh treble hiss)
                                val white = (Math.random() * 2.0 - 1.0).toFloat()
                                brown1 = (brown1 * 0.985f) + (white * 0.015f)
                                brown2 = (brown2 * 0.985f) + (brown1 * 0.015f)
                                val drop = if (Math.random() < 0.00015) (Math.random() * 0.25).toFloat() else 0f
                                ((brown2 * 5.0f) + drop).coerceIn(-1f, 1f)
                            }
                            "waves" -> {
                                // Deep, slow, soothing 12-second ocean swell
                                val white = (Math.random() * 2.0 - 1.0).toFloat()
                                brown1 = (brown1 * 0.988f) + (white * 0.012f)
                                val swell = (Math.sin(2.0 * Math.PI * 0.075 * (sampleIndex / 44100.0)) * 0.5 + 0.5).toFloat()
                                (brown1 * 6.0f * (0.2f + swell * 0.8f)).coerceIn(-1f, 1f)
                            }
                            "zen" -> {
                                // Pure 432Hz + 216Hz crystalline meditative drone
                                phaseZen1 += 2.0 * Math.PI * 432.0 / sampleRate
                                phaseZen2 += 2.0 * Math.PI * 216.0 / sampleRate
                                if (phaseZen1 > 2.0 * Math.PI) phaseZen1 -= 2.0 * Math.PI
                                if (phaseZen2 > 2.0 * Math.PI) phaseZen2 -= 2.0 * Math.PI
                                val vibrato = 1.0 + 0.02 * Math.sin(2.0 * Math.PI * 0.15 * (sampleIndex / 44100.0))
                                val s1 = Math.sin(phaseZen1 * vibrato) * 0.22
                                val s2 = Math.sin(phaseZen2) * 0.16
                                (s1 + s2).toFloat().coerceIn(-1f, 1f)
                            }
                            "binaural" -> {
                                // 6Hz Theta binaural study wave (200Hz + 206Hz) with warm ambient pad
                                phaseBin1 += 2.0 * Math.PI * 200.0 / sampleRate
                                phaseBin2 += 2.0 * Math.PI * 206.0 / sampleRate
                                if (phaseBin1 > 2.0 * Math.PI) phaseBin1 -= 2.0 * Math.PI
                                if (phaseBin2 > 2.0 * Math.PI) phaseBin2 -= 2.0 * Math.PI
                                val s = (Math.sin(phaseBin1) + Math.sin(phaseBin2)) * 0.18
                                s.toFloat().coerceIn(-1f, 1f)
                            }
                            else -> 0f
                        }
                        sampleIndex++
                        buf[i] = (sample * 32767).toInt().coerceIn(-32768, 32767).toShort()
                    }
                    t.write(buf, 0, bufferSize)
                }
            } catch (_: Exception) {}
        }
        worker?.start()
    }
}

fun soundName(type: String): String = when (type) {
    "lofi" -> "Lo-Fi Piano Chords"
    "rain" -> "Cozy Soft Rain"
    "waves" -> "Deep Ocean Waves"
    "zen" -> "432Hz Zen Meditation"
    "binaural" -> "Theta Focus (Binaural)"
    else -> "Ambient Sound"
}

@Composable
fun StudySoundscapeCard(modifier: Modifier = Modifier) {
    var playing by remember { mutableStateOf(AmbientStudySoundEngine.isPlaying) }
    var soundType by remember { mutableStateOf(AmbientStudySoundEngine.currentSound) }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.VolumeUp,
                        contentDescription = "Music",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Study Soundscape", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.width(6.dp))
                        if (playing) {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF4ADE80).copy(alpha = 0.25f)) {
                                Text("PLAYING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                    Text(
                        if (playing) soundName(soundType) else "Calm, whisper-soft audio for deep focus",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = {
                        val active = AmbientStudySoundEngine.toggle(soundType)
                        playing = active
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (playing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(if (playing) "Pause" else "Play", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Sound pills
            val sounds = listOf(
                "lofi" to "Lo-Fi Piano",
                "rain" to "Cozy Rain",
                "waves" to "Ocean Waves",
                "zen" to "432Hz Zen",
                "binaural" to "Theta Focus"
            )
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sounds.forEach { (type, label) ->
                    val selected = soundType == type
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable {
                            soundType = type
                            if (playing) {
                                AmbientStudySoundEngine.play(type)
                            }
                        }
                    ) {
                        Text(
                            label,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==================== HANGING MASCOT OVERLAY (DRAGGABLE & HIDEABLE) ====================
@Composable
fun DraggableHangingMascotOverlay(
    mascotType: MascotType,
    mascotName: String,
    modifier: Modifier = Modifier
) {
    var bubbleText by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(bubbleText) {
        if (bubbleText != null) {
            kotlinx.coroutines.delay(3500)
            bubbleText = null
        }
    }

    val transition = rememberInfiniteTransition(label = "hang")
    val swayAngle by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    val quips = remember {
        listOf(
            "Stay focused, you're doing great!",
            "Remember to drink some water!",
            "Check off another task to earn playtime!",
            "One step at a time!",
            "You got this!"
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.TopEnd
    ) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val maxSlidePx = with(density) { (maxWidth - 80.dp).toPx().coerceAtLeast(0f) }

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(end = 16.dp, top = 4.dp)
                .offset { IntOffset(dragOffset.x.toInt(), 0) } // Y strictly pinned to top boundary ceiling
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newX = (dragOffset.x + dragAmount.x).coerceIn(-maxSlidePx, 0f)
                        dragOffset = androidx.compose.ui.geometry.Offset(newX, 0f)
                    }
                }
        ) {
            if (bubbleText != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    shadowElevation = 6.dp,
                    modifier = Modifier.padding(end = 8.dp, top = 16.dp).widthIn(max = 190.dp).clickable { bubbleText = null }
                ) {
                    Text(
                        bubbleText!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .graphicsLayer(rotationZ = swayAngle)
                    .clickable {
                        bubbleText = quips.random()
                    }
            ) {
                Box(
                    Modifier.width(2.5.dp).height(20.dp).background(
                        Color(0xFF86EFAC),
                        shape = RoundedCornerShape(2.dp)
                    )
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                    shadowElevation = 5.dp,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        MascotCanvas(
                            type = mascotType,
                            mood = MascotMood.CHEERING,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }
        }
    }
}

// Vector Lock Icon (zero dependency, clean Material design)
@Composable
fun LockIcon(tint: Color = MaterialTheme.colorScheme.primary, modifier: Modifier = Modifier.size(20.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val shackleRadius = w * 0.26f
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.24f, h * 0.12f),
            size = Size(shackleRadius * 2, shackleRadius * 2),
            style = Stroke(width = w * 0.12f, cap = StrokeCap.Round)
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.15f, h * 0.38f),
            size = Size(w * 0.70f, h * 0.54f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.14f, w * 0.14f)
        )
        drawCircle(
            color = Color.White,
            radius = w * 0.08f,
            center = Offset(w * 0.50f, h * 0.60f)
        )
    }
}

data class TourStepData(val title: String, val highlightLabel: String, val description: String, val isCardTop: Boolean)

// ==================== APP INTERACTIVE TOUR GUIDE OVERLAY ====================
@Composable
fun AppTourGuide(
    step: Int,
    onStepChange: (Int) -> Unit,
    onClose: () -> Unit
) {
    val totalSteps = 8
    val stepData = when (step) {
        1 -> TourStepData(
            "Study Mascot & Motivation",
            "Mascot Companion",
            "Meet your study buddy! Poke it to hear cute audio reactions, tap Roast for tough love, Hype for energy, Tip for study methods, or Ask AI to answer any homework question.",
            false
        )
        2 -> TourStepData(
            "Playtime Bank & Screen Time Economy",
            "1:1 Earn Rate",
            "Every 1 minute of focused studying earns you exactly 1 minute of phone screen time! When you want to use social media or play games, tap 'Playtime!' to unlock your phone. Tap 'Ledger' to view full history.",
            false
        )
        3 -> TourStepData(
            "Strict App Blocking & Lock Screen",
            "Focus Lock Protection",
            "When your study timer starts, distracting apps (Instagram, YouTube, TikTok, Games) are instantly locked with a cute lock screen! If you urgently need your phone, use Emergency Unlock (costs 5 earned mins).",
            true
        )
        4 -> TourStepData(
            "Special Edition: Shorts & Reels Auto-Block",
            "Accessibility Shield",
            "Your Special Edition uses Android Accessibility to automatically detect and block YouTube Shorts and Instagram Reels, while keeping educational study channels (Khan Academy, CrashCourse, etc.) accessible!",
            true
        )
        5 -> TourStepData(
            "Melodic Lo-Fi & Study Soundscapes",
            "Ambient Audio Engine",
            "Listen to calming Lo-Fi Piano Chords, Cozy Soft Rain, Deep Ocean Waves, Zen 432Hz Meditation, or Theta Binaural Focus Beats to silence noisy backgrounds.",
            false
        )
        6 -> TourStepData(
            "Learning Quests & Study Timers",
            "Focus Sessions",
            "Tap Play on any subject to start studying. Supports Pomodoro rounds, break timers, and front-camera live preview for personal study accountability.",
            true
        )
        7 -> TourStepData(
            "To-Do Tasks & Google Calendar Sync",
            "Organization & Alerts",
            "Add homework quests with estimated times—finishing tasks gives bonus playtime! The Plan tab syncs with your Google and phone calendars with 5-minute pre-study reminders.",
            true
        )
        8 -> TourStepData(
            "YPT Study Squads & Challenges",
            "Study Together",
            "Discover Public squads or join Private password-protected squads! Share invite links, study in live rooms, take on 21-day study challenges, and share the group screen time pool.",
            true
        )
        else -> TourStepData("", "", "", false)
    }
    val title = stepData.title
    val highlightLabel = stepData.highlightLabel
    val description = stepData.description
    val isCardTop = stepData.isCardTop

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { },
        contentAlignment = if (isCardTop) Alignment.TopCenter else Alignment.BottomCenter
    ) {
        Column(
            Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                shadowElevation = 14.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "Step $step of $totalSteps",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                highlightLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = onClose) {
                            Text("Skip Tour", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(6.dp))
                    Text(description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp)

                    Spacer(Modifier.height(16.dp))

                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (i in 1..totalSteps) {
                                Box(
                                    Modifier
                                        .size(if (i == step) 10.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(if (i == step) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                                )
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        if (step > 1) {
                            OutlinedButton(
                                onClick = { onStepChange(step - 1) },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Back", fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = {
                                if (step < totalSteps) onStepChange(step + 1)
                                else onClose()
                            },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(if (step < totalSteps) "Next" else "Done", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun App(themeIdx: Int, onTheme: (Int) -> Unit, fontIdx: Int, onFont: (Int) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var uid by remember { mutableStateOf(auth.currentUser?.uid) }
    var error by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    Surface(Modifier.fillMaxSize(), color = if (themes[themeIdx].scene.isNotEmpty()) Color.Transparent else MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface) {
        if (uid == null) {
            Column(Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally) {
                Text("StudyReward", fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    scope.launch {
                        try { signIn(ctx); uid = auth.currentUser?.uid }
                        catch (e: Exception) { error = e.message ?: "Sign-in failed" }
                    }
                }) { Text("Sign in with Google") }
                Text(error)
            }
        } else Main(uid!!, themeIdx, onTheme, fontIdx, onFont) { auth.signOut(); uid = null }
    }
}

@Composable
fun Main(uid: String, themeIdx: Int, onTheme: (Int) -> Unit, fontIdx: Int, onFont: (Int) -> Unit, onSignOut: () -> Unit) {
    val me = FirebaseFirestore.getInstance().collection("users").document(uid)
    var subjects by remember { mutableStateOf(listOf<Subject>()) }
    var tasks by remember { mutableStateOf(listOf<TaskItem>()) }
    var today by remember { mutableStateOf(mapOf<String, Long>()) }
    var tab by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf<String?>(null) }
    var secs by remember { mutableStateOf(0) }
    var daysOff by remember { mutableStateOf(listOf<String>()) }
    var groups by remember { mutableStateOf(listOf<Group>()) }
    var reqs by remember { mutableStateOf(mapOf<String, List<Req>>()) } // groupId -> today's requests
    var paused by remember { mutableStateOf(false) }
    var screen by remember { mutableStateOf("") } // "", stats, logs, settings
    var pendingFrom by remember { mutableStateOf<String?>(null) }
    var secsByDay by remember { mutableStateOf(mapOf<String, Long>()) }   // dateKey -> seconds studied
    var goals by remember { mutableStateOf(mapOf<String, Long>()) }       // dateKey -> goal in minutes
    var revived by remember { mutableStateOf(setOf<String>()) }
    var goalsLoaded by remember { mutableStateOf(false) }
    var rankByDay by remember { mutableStateOf(mapOf<String, Long>()) }  // dateKey -> ranked seconds
    var raiseGoal by remember { mutableStateOf(false) }
    var events by remember { mutableStateOf(listOf<EventItem>()) }
    var openEventId by remember { mutableStateOf<String?>(null) }
    var slots by remember { mutableStateOf(listOf<Slot>()) }
    var nick by remember { mutableStateOf("") }
    var optIn by remember { mutableStateOf(false) }   // appear in public rankings
    var openGroupId by remember { mutableStateOf<String?>(null) }
    var chatGroup by remember { mutableStateOf<String?>(null) }
    var lastMsg by remember { mutableStateOf(mapOf<String, Pair<Long, String>>()) }   // groupId -> (time, sender) of latest message
    var chatRead by remember { mutableStateOf(mapOf<String, Long>()) }
    val dayKey by produceState(initialValue = dateKey()) { while (true) { delay(30_000); value = dateKey() } }
    var autoSlot by remember { mutableStateOf<Slot?>(null) }       // timetable block that started the timer
    var provisional by remember { mutableStateOf(false) }          // auto-started time counts only after you tick it
    var autoDone by remember { mutableStateOf(setOf<String>()) }   // blocks already auto-started today
    var focusSecs by remember { mutableStateOf(0) }      // seconds studied since the last break
    var breakLeft by remember { mutableStateOf(0) }
    var awaiting by remember { mutableStateOf(false) }   // break is over, waiting for you to continue
    var round by remember { mutableStateOf(0) }
    var breakPrompt by remember { mutableStateOf(false) }
    var allStudy by remember { mutableStateOf(0L) }
    var spent by remember { mutableStateOf(0L) }
    var todaySpends by remember { mutableStateOf(listOf<SpendEntry>()) }
    var showLedger by remember { mutableStateOf(false) }
    var rate by remember { mutableStateOf(30) } // minutes of screen time per hour studied
    var using by remember { mutableStateOf(false) }
    var usingSecs by remember { mutableStateOf(0L) }
    var runningTask by remember { mutableStateOf<String?>(null) }
    var taskSecs by remember { mutableStateOf(0) }
    var cap by remember { mutableStateOf(3) } // max tasks per day that can earn screen time
    var base by remember { mutableStateOf(60) } // starting screen time in minutes

    DisposableEffect(uid, dayKey) {   // listeners restart when the date changes
        val r1 = me.collection("subjects").addSnapshotListener { snap, _ ->
            subjects = (snap?.documents?.map {
                Subject(it.id, it.getString("name") ?: "", it.getLong("color") ?: palette[0]) to (it.getLong("order") ?: 0L)
            } ?: emptyList()).sortedBy { it.second }.map { it.first }
        }
        val r2 = me.collection("tasks").addSnapshotListener { snap, _ ->
            tasks = (snap?.documents?.map {
                TaskItem(it.id, it.getString("title") ?: "", it.getString("subject") ?: "",
                    it.getBoolean("done") ?: false, it.getLong("order") ?: 0L,
                    maxOf(15L, it.getLong("dur") ?: 15L), it.getLong("worked") ?: 0L, it.getLong("doneAt") ?: 0L, it.getLong("reward") ?: 0L, it.getString("kind") ?: "", it.getString("date") ?: "2000-01-01")
            } ?: emptyList()).sortedBy { it.order }
        }
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val r3 = me.collection("sessions").whereGreaterThanOrEqualTo("at", start).addSnapshotListener { snap, _ ->
            val m = mutableMapOf<String, Long>()
            snap?.documents?.forEach {
                val s = it.getString("subject") ?: ""
                m[s] = (m[s] ?: 0L) + (it.getLong("seconds") ?: 0L)
            }
            today = m
        }
        val r5 = me.collection("spends").whereGreaterThanOrEqualTo("at", start).addSnapshotListener { snap, _ ->
            spent = snap?.documents?.sumOf { it.getLong("seconds") ?: 0L } ?: 0L
            todaySpends = snap?.documents?.mapNotNull { doc ->
                val s = doc.getLong("seconds") ?: return@mapNotNull null
                val a = doc.getLong("at") ?: return@mapNotNull null
                SpendEntry(doc.id, s, a, doc.getString("reason"))
            }?.sortedByDescending { it.at } ?: emptyList()
        }
        val r6 = me.addSnapshotListener { snap, _ ->
            val tk = dateKey()
            val nxFrom = snap?.getString("nxFrom")
            val nx = snap?.get("nx") as? Map<*, *>
            val useNx = nxFrom != null && nxFrom <= tk && nx != null
            fun pick(k: String, d: Int): Int = ((if (useNx) nx?.get(k) else snap?.get(k)) as? Number)?.toInt() ?: d
            rate = pick("rate", 30).coerceIn(0, 120)
            cap = pick("taskCap", 3)
            base = pick("baseMins", 60)
            pendingFrom = if (nxFrom != null && nxFrom > tk) nxFrom else null
            nick = snap?.getString("nickname") ?: ""
            nickname = nick
            optIn = snap?.getBoolean("rankOptIn") ?: false
            daysOff = (snap?.get("daysOff") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        }
        val r7 = FirebaseFirestore.getInstance().collection("groups").whereArrayContains("members", uid).addSnapshotListener { snap, _ ->
            groups = snap?.documents?.map {
                Group(it.id, it.getString("name") ?: "", it.getString("admin") ?: "", it.getString("code") ?: "",
                    (it.getLong("pool") ?: 120L).toInt(),
                    (it.get("members") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    it.getString("intro") ?: "", it.getString("rules") ?: "", (it.getLong("capacity") ?: 10L).toInt(),
                    it.getString("joinMethod") ?: "code", it.getLong("nudgeAt") ?: 0L, it.getString("nudgeBy") ?: "",
                    it.getBoolean("isPrivate") ?: ((it.getString("password") ?: "").isNotEmpty()),
                    it.getString("password") ?: "",
                    it.getString("category") ?: "General")
            } ?: emptyList()
        }
        val r4 = me.collection("sessions").addSnapshotListener { snap, _ ->
            allStudy = snap?.documents?.sumOf { it.getLong("seconds") ?: 0L } ?: 0L
        }
        val r9 = me.collection("sessions").whereGreaterThanOrEqualTo("at", dayStart(59)).addSnapshotListener { snap, _ ->
            val m = mutableMapOf<String, Long>()
            val cap = mutableMapOf<String, Long>()
            snap?.documents?.forEach {
                val k = dateKey(Calendar.getInstance().apply { timeInMillis = it.getLong("at") ?: 0L })
                m[k] = (m[k] ?: 0L) + (it.getLong("seconds") ?: 0L)
                if ((it.getLong("seconds") ?: 0L) <= 9 * 3600) cap[k] = (cap[k] ?: 0L) + (it.getLong("seconds") ?: 0L)
            }
            secsByDay = m
            rankByDay = cap.filterValues { it <= 20 * 3600 }
        }
        val r10 = me.collection("goals").addSnapshotListener { snap, _ ->
            goals = snap?.documents?.associate { it.id to (it.getLong("target") ?: 0L) } ?: emptyMap()
            revived = snap?.documents?.filter { it.getBoolean("revived") == true }?.map { it.id }?.toSet() ?: emptySet()
            goalsLoaded = true
        }
        val r11 = me.collection("events").addSnapshotListener { snap, _ ->
            events = (snap?.documents?.map {
                EventItem(it.id, it.getString("name") ?: "", it.getString("date") ?: "", it.getString("start") ?: dateKey(),
                    it.getLong("color") ?: palette[0], (it.get("subjects") as? List<*>)?.filterIsInstance<String>() ?: emptyList())
            } ?: emptyList()).sortedBy { it.date }
        }
        val r12 = me.collection("timetable").addSnapshotListener { snap, _ ->
            slots = snap?.documents?.map {
                Slot(it.id, (it.getLong("day") ?: 0L).toInt(), (it.getLong("start") ?: 0L).toInt(), (it.getLong("end") ?: 0L).toInt(),
                    it.getString("subject") ?: "", it.getString("note") ?: "")
            } ?: emptyList()
        }
        onDispose { r1.remove(); r2.remove(); r3.remove(); r5.remove(); r6.remove(); r7.remove(); r4.remove(); r9.remove(); r10.remove(); r11.remove(); r12.remove() }
    }
    DisposableEffect(groups.map { it.id }) {
        val regs = groups.map { gr ->
            FirebaseFirestore.getInstance().collection("groups").document(gr.id).collection("messages")
                .orderBy("at", Query.Direction.DESCENDING).limit(1).addSnapshotListener { snap, _ ->
                    val d = snap?.documents?.firstOrNull()
                    if (d != null) lastMsg = lastMsg + (gr.id to ((d.getLong("at") ?: 0L) to (d.getString("uid") ?: "")))
                }
        }
        onDispose { regs.forEach { it.remove() } }
    }
    DisposableEffect(groups.map { it.id }.joinToString(","), dayKey) {
        val startTs = startOfDay()
        val regs = groups.map { gr ->
            FirebaseFirestore.getInstance().collection("groups").document(gr.id).collection("requests")
                .addSnapshotListener { snap, _ ->
                    val docs = snap?.documents ?: emptyList()
                    val groupReqs = docs.mapNotNull { d ->
                        val at = d.getLong("at") ?: 0L
                        // Requests from today (with 4 hour grace window for timezone/drift)
                        if (at >= startTs - 14400_000L) {
                            Req(d.id, d.getString("fromUid") ?: "", d.getString("fromName") ?: "Friend",
                                d.getLong("mins") ?: 0L, at,
                                (d.get("yes") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                                (d.get("no") as? List<*>)?.filterIsInstance<String>() ?: emptyList())
                        } else null
                    }
                    reqs = reqs + (gr.id to groupReqs)
                }
        }
        onDispose { regs.forEach { it.remove() } }
    }
    val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE) }
    var fp by remember {
        mutableStateOf(FocusPrefs(prefs.getBoolean("brOn", true), prefs.getInt("brEvery", 50), prefs.getInt("brLen", 10),
            prefs.getBoolean("pomo", false), prefs.getInt("pFocus", 25), prefs.getInt("pShort", 5), prefs.getInt("pLong", 15)))
    }
    fun setFp(n: FocusPrefs) {
        fp = n
        prefs.edit().putBoolean("brOn", n.on).putInt("brEvery", n.every).putInt("brLen", n.len).putBoolean("pomo", n.pomo)
            .putInt("pFocus", n.focus).putInt("pShort", n.short).putInt("pLong", n.long).apply()
    }
    // The running study timer is saved every 10 seconds so it can be recovered if the app is closed
    var recover by remember {
        mutableStateOf(prefs.getString("active", null)?.split("|", limit = 2)?.let { if (it.size == 2) it[1] to (it[0].toIntOrNull() ?: 0) else null })
    }
    LaunchedEffect(running, paused, provisional) {
        if (running == null || provisional) prefs.edit().remove("active").apply()
        else while (true) { prefs.edit().putString("active", "$secs|$running").apply(); delay(10_000) }
    }
    fun resetFocus() { focusSecs = 0; breakLeft = 0; awaiting = false; round = 0; breakPrompt = false }
    // Study time does not count during a break: the study timer is paused and a separate countdown runs
    fun startBreak(mins: Int) { paused = true; breakLeft = mins * 60; awaiting = false; focusSecs = 0 }
    fun resumeAfterBreak() { breakLeft = 0; awaiting = false; paused = false }
    LaunchedEffect(running, paused) {
        while (running != null && !paused) {
            delay(1000); secs++; focusSecs++
            if (fp.pomo) {
                if (focusSecs >= fp.focus * 60) {
                    round++
                    val len = if (round % 4 == 0) fp.long else fp.short
                    startBreak(len)
                    notifyUser(ctx, "Pomodoro done 🍅", "Take a $len minute break.")
                }
            } else if (fp.on && focusSecs >= fp.every * 60 && !breakPrompt) {
                breakPrompt = true
                notifyUser(ctx, "Time for a break ☕", "You have been studying for ${fp.every} minutes.")
            }
        }
    }
    val onBreak = breakLeft > 0
    LaunchedEffect(onBreak) {
        while (breakLeft > 0 && running != null) {
            delay(1000); breakLeft--
            if (breakLeft == 0) {
                awaiting = true
                notifyUser(ctx, "Break over ?", "Open the app when you are ready to continue.")
            }
        }
    }

    // Wallet resets every day: earned today = free daily time + study x rate + reward tasks; balance = earned - spent today
    val dayOff = dateKey() in daysOff
    // Study and reward tasks are never capped. Time from approved group requests has an overall daily maximum.
    val myReqMins = groups.sumOf { gr -> acceptedReqs(gr, reqs[gr.id] ?: emptyList()).filter { it.fromUid == uid }.sumOf { it.mins } }
    val giftSecs = minOf(myReqMins * 60L, MAX_GIFT_MINS * 60L)
    val earned = base * 60L + (today.values.sum() + (if (provisional) 0 else secs)) * rate / 60 +
            tasks.filter { it.kind == "reward" && it.doneAt >= startOfDay() }.sumOf { it.reward } + giftSecs
    val balance = maxOf(0L, earned - spent - usingSecs)

    fun stopUsing() {
        val now = System.currentTimeMillis()
        val st = prefs.getLong("blk_use_start", 0L)
        val realUsed = if (st > 0L) maxOf(usingSecs, minOf((now - st) / 1000, balance + usingSecs)) else usingSecs
        if (realUsed > 0L) {
            spent += realUsed // Instantly deduct from local spent
            me.collection("spends").add(mapOf<String, Any>("seconds" to realUsed, "at" to now))
        }
        usingSecs = 0L
        using = false
        prefs.edit().putLong("blk_use_start", 0L).putLong("blk_use_until", 0L).apply()
    }

    // Keep usingSecs synchronized to real wall-clock time so backgrounding/minimizing never freezes the balance
    LaunchedEffect(using) {
        if (using) {
            while (using) {
                val now = System.currentTimeMillis()
                val st = prefs.getLong("blk_use_start", 0L)
                val until = prefs.getLong("blk_use_until", 0L)
                if (st > 0L) {
                    val elapsed = (now - st) / 1000
                    usingSecs = elapsed
                    if (now >= until || earned - spent - usingSecs <= 0L) {
                        stopUsing()
                        break
                    }
                }
                delay(1000)
            }
        }
    }

    // Reconcile Use timer on start or whenever screen resumes
    LaunchedEffect(Unit) {
        val st = prefs.getLong("blk_use_start", 0L)
        val until = prefs.getLong("blk_use_until", 0L)
        val now = System.currentTimeMillis()
        if (st > 0L) {
            if (now < until) {
                // Timer is still active
                using = true
                usingSecs = (now - st) / 1000
            } else {
                // Timer expired while in background
                val used = (until - st) / 1000
                if (used > 0L) {
                    me.collection("spends").add(mapOf<String, Any>("seconds" to used, "at" to now))
                }
                prefs.edit().putLong("blk_use_start", 0L).putLong("blk_use_until", 0L).apply()
                using = false
                usingSecs = 0L
            }
        }
    }
    LaunchedEffect(dayOff, dayKey) { prefs.edit().putString("blk_off_key", if (dayOff) dateKey() else "").apply() }
    LaunchedEffect(balance, using) { if (using && balance <= 0L) stopUsing() }

    fun saveRunning() {
        val r = running
        if (provisional) { secs = 0; provisional = false; autoSlot = null }  // unconfirmed timetable time is discarded
        if (r != null && secs > 0) {
            val sSecs = secs.toLong()
            // Instantly update local today map so screen time left updates on this exact frame!
            today = today.toMutableMap().apply { put(r, (get(r) ?: 0L) + sSecs) }
            me.collection("sessions").add(
                mapOf("subject" to r, "seconds" to sSecs, "at" to System.currentTimeMillis()))
        }
        secs = 0
    }
    fun saveTask() {
        val id = runningTask
        if (id != null && taskSecs > 0) me.collection("tasks").document(id).update("worked", FieldValue.increment(taskSecs.toLong()))
        taskSecs = 0; runningTask = null
    }
    fun toggle(name: String) { if (using) stopUsing(); saveTask(); saveRunning(); resetFocus(); running = name; paused = false }
    fun endStudy() { saveRunning(); running = null; paused = false; resetFocus(); autoSlot = null }
    fun startAuto(sl: Slot) {
        toggle(sl.subject)
        autoSlot = sl
        provisional = true
        notifyUser(ctx, "Timetable: ${sl.subject}", "Your study block started. Open the app and tick ? to confirm you are studying.")
    }
    fun rejectAuto() { secs = 0; provisional = false; autoSlot = null; running = null; paused = false; resetFocus() }
    LaunchedEffect(slots) {
        while (true) {
            val c = Calendar.getInstance()
            val nowMin = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)
            val wd = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7
            val day = dateKey()
            if (running == null && !using && runningTask == null) {
                // 1. Pre-alert 5 minutes before scheduled study block starts
                val upcoming = slots.filter {
                    it.day == wd && (it.start - nowMin) in 1..5 && "${it.id}|$day|pre" !in autoDone
                }
                for (u in upcoming) {
                    autoDone = autoDone + "${u.id}|$day|pre"
                    notifyUser(ctx, "⏰ Upcoming: ${u.subject}", "Starts in ${u.start - nowMin} minutes! Get ready to focus & earn playtime.")
                }

                // 2. Auto-start prompt when study block time arrives
                val sl = slots.filter { it.day == wd && nowMin >= it.start && nowMin < it.end && "${it.id}|$day" !in autoDone }
                    .minByOrNull { it.start }
                if (sl != null) { autoDone = autoDone + "${sl.id}|$day"; startAuto(sl) }
            }
            val a = autoSlot
            if (a != null && nowMin >= a.end) { if (running == a.subject) endStudy(); autoSlot = null }
            delay(15_000)
        }
    }
    fun toggleTask(id: String) {
        if (using) stopUsing(); saveRunning(); running = null; paused = false
        val same = runningTask == id
        saveTask()
        if (!same) runningTask = id
    }
    val doneToday = tasks.count { it.kind == "reward" && it.done && it.reward > 0 && it.doneAt >= startOfDay() }
    // Publish today's summary so group members can see it
    val isCurrentlyStudying = (running != null && !paused) || (runningTask != null)
    LaunchedEffect(today, doneToday, running, paused, allStudy, runningTask) {
        while (true) {
            val studying = (running != null && !paused) || (runningTask != null)
            val currentSecs = today.values.sum() + secs + taskSecs
            FirebaseFirestore.getInstance().collection("profiles").document(uid).set(mapOf<String, Any>(
                "name" to myName(),
                "studyToday" to currentSecs,
                "studyTotal" to allStudy + secs + taskSecs,
                "studying" to studying,
                "asOf" to System.currentTimeMillis(),
                "day" to dateKey(),
                "rewardsDone" to doneToday.toLong()
            ), SetOptions.merge())
            if (!studying) break
            delay(10_000)
        }
    }
    fun completeTask(t: TaskItem) {
        val earns = doneToday < cap
        val rew = if (earns) rewardSecs(t.dur, rate) else 0L
        // Instantly update local task state so reward playtime updates on this exact frame!
        tasks = tasks.map { if (it.id == t.id) it.copy(done = true, doneAt = System.currentTimeMillis(), reward = rew) else it }
        me.collection("tasks").document(t.id).update(mapOf<String, Any>(
            "done" to true, "doneAt" to System.currentTimeMillis(), "reward" to rew))
    }
    LaunchedEffect(runningTask) { while (runningTask != null) { delay(1000); taskSecs++ } }
    LaunchedEffect(taskSecs) {
        val t = tasks.find { it.id == runningTask }
        if (t != null && t.worked + taskSecs >= t.dur * 60) saveTask()
    }

    val todayKey = dateKey()
    val nextEvent = events.filter { it.date >= todayKey }.minByOrNull { it.date }
    val nowCal = Calendar.getInstance()
    val upNext = slots.filter { it.day == (nowCal.get(Calendar.DAY_OF_WEEK) + 5) % 7 && it.end > nowCal.get(Calendar.HOUR_OF_DAY) * 60 + nowCal.get(Calendar.MINUTE) }
        .minByOrNull { it.start }
    val goalToday = goals[todayKey] ?: 0L
    val studiedNow = today.values.sum() + (if (provisional) 0 else secs)
    val todayMet = goalToday > 0 && studiedNow >= goalToday * 60
    val (streak, revivable) = computeStreak(goals, revived, secsByDay, daysOff, todayMet)
    val lastTarget = goals.filterKeys { it < todayKey }.maxByOrNull { it.key }?.value ?: 120L
    fun setGoal(m: Long) { me.collection("goals").document(todayKey).set(mapOf("target" to m), SetOptions.merge()) }
    fun revive() {
        me.collection("goals").document(keyBack(1)).set(mapOf("revived" to true), SetOptions.merge())
        me.collection("spends").add(mapOf<String, Any>("seconds" to REVIVE_COST_MIN * 60, "at" to System.currentTimeMillis(), "reason" to "revive"))
    }
    var ack by remember { mutableStateOf(prefs.getBoolean("ack1", false)) }
    if (!ack) OnboardingScreen { n ->
        prefs.edit().putBoolean("ack1", true).apply()
        ack = true
        if (n.isNotBlank()) me.set(mapOf("nickname" to n), SetOptions.merge())
    }
    if (ack && goalsLoaded && goalToday == 0L && todayKey !in daysOff) GoalDialog(0L, lastTarget, { setGoal(it) }, null)
    else if (raiseGoal) GoalDialog(goalToday, goalToday, { setGoal(it); raiseGoal = false }, { raiseGoal = false })

    if (showLedger) {
        RewardLedgerDialog(
            baseMins = base,
            rate = rate,
            todayStudy = today,
            tasks = tasks,
            giftSecs = giftSecs,
            spends = todaySpends,
            usingSecs = usingSecs,
            balance = balance,
            onDismiss = { showLedger = false }
        )
    }

    recover?.let { (n, sc) ->
        AlertDialog(onDismissRequest = {}, title = { Text("Your timer was interrupted") },
            text = { Text("The $n timer was running (${mmss(sc.toLong())} recorded) when the app closed. Time while the app was closed is not counted.") },
            confirmButton = { TextButton(onClick = { toggle(n); secs = sc; recover = null }) { Text("Resume") } },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        if (sc > 0) me.collection("sessions").add(mapOf("subject" to n, "seconds" to sc, "at" to System.currentTimeMillis(), "recovered" to true))
                        recover = null
                    }) { Text("End and save") }
                    TextButton(onClick = { recover = null }) { Text("Discard") }
                }
            })
    }
    var nudgeMsg by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(groups) {
        groups.forEach { gr ->
            if (gr.nudgeAt > prefs.getLong("nudge_" + gr.id, 0L) && gr.nudgeBy != uid && System.currentTimeMillis() - gr.nudgeAt < 30 * 60_000) {
                prefs.edit().putLong("nudge_" + gr.id, gr.nudgeAt).apply()
                nudgeMsg = "The leader of \"${gr.name}\" nudged everyone: time to study! 📚"
                notifyUser(ctx, "📣 ${gr.name}", "Your group leader nudged everyone to study.")
            }
        }
    }
    nudgeMsg?.let { m ->
        AlertDialog(onDismissRequest = { nudgeMsg = null }, title = { Text("📣 Group nudge") }, text = { Text(m) },
            confirmButton = { TextButton(onClick = { nudgeMsg = null }) { Text("OK") } })
    }
    if (provisional) AlertDialog(onDismissRequest = {}, title = { Text("⏰ ${autoSlot?.subject ?: "Study"} block started") },
        text = { Text("Your timetable started the timer. Are you studying right now? Time only counts after you confirm, and unconfirmed time is thrown away.") },
        confirmButton = { TextButton(onClick = { provisional = false }) { Text("✓ Yes, studying") } },
        dismissButton = { TextButton(onClick = { rejectAuto() }) { Text("✕ No") } })
    if (breakPrompt) AlertDialog(onDismissRequest = {}, title = { Text("Time for a break ☕") },
        text = { Text("You have studied ${fp.every} minutes in a row. Rest your eyes and stretch for ${fp.len} minutes.") },
        confirmButton = { TextButton(onClick = { breakPrompt = false; startBreak(fp.len) }) { Text("Take a ${fp.len}-min break") } },
        dismissButton = { TextButton(onClick = { breakPrompt = false; focusSecs = maxOf(0, fp.every * 60 - 600) }) { Text("10 more minutes") } })

    LaunchedEffect(rankByDay, groups, optIn, allStudy) {
        val name = myName()
        val d = dateKey()
        val wk = weekKey(d)
        val mo = d.take(7)
        val daySecs = rankByDay[d] ?: 0L
        val weekSecs = rankByDay.filterKeys { weekKey(it) == wk }.values.sum()
        val monthSecs = rankByDay.filterKeys { it.startsWith(mo) }.values.sum()
        val db = FirebaseFirestore.getInstance()
        fun put(doc: String, entry: String, data: Map<String, Any>) {
            db.collection("rank").document(doc).collection("entries").document(entry).set(data)
        }
        if (!optIn) {   // not in public rankings: remove any entries and publish nothing
            listOf("day_$d", "week_$wk", "month_$mo", "all").forEach { db.collection("rank").document(it).collection("entries").document(uid).delete() }
            groups.forEach { gr ->
                listOf("gday_$d", "gweek_$wk", "gmonth_$mo").forEach { doc ->
                    db.collection("rank").document(doc).collection("entries").document(gr.id + "_" + uid).delete()
                }
            }
            return@LaunchedEffect
        }
        put("all", uid, mapOf("name" to name, "secs" to allStudy))
        listOf("day_$d" to daySecs, "week_$wk" to weekSecs, "month_$mo" to monthSecs).forEach { (doc, v) ->
            put(doc, uid, mapOf("name" to name, "secs" to v))
        }
        groups.forEach { gr ->
            listOf("gday_$d" to daySecs, "gweek_$wk" to weekSecs, "gmonth_$mo" to monthSecs).forEach { (doc, v) ->
                put(doc, gr.id + "_" + uid, mapOf("gid" to gr.id, "gname" to gr.name, "uid" to uid, "secs" to v))
            }
        }
    }

    fun isUnread(gid: String) = lastMsg[gid]?.let { (at, from) ->
        from != uid && at > (chatRead[gid] ?: prefs.getLong("chat_read_$gid", 0L))
    } ?: false
    val unreadSet = groups.filter { isUnread(it.id) }.map { it.id }.toSet()
    fun markRead(gid: String) {
        val t = System.currentTimeMillis()
        prefs.edit().putLong("chat_read_$gid", t).apply()
        chatRead = chatRead + (gid to t)
    }

    fun saveAdvanced(r: Int, c: Int, b: Int) {
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
        me.set(mapOf<String, Any>("rate" to rate.toLong(), "taskCap" to cap.toLong(), "baseMins" to base.toLong(),
            "nx" to mapOf("rate" to r.coerceIn(0, 120).toLong(), "taskCap" to c.toLong(), "baseMins" to b.toLong()),
            "nxFrom" to dateKey(tomorrow)), SetOptions.merge())
    }
    var mType by remember { mutableStateOf(MascotType.fromId(prefs.getString("mascot_type", "mochi") ?: "mochi")) }
    var mName by remember { mutableStateOf(prefs.getString("mascot_name", "Mochi") ?: "Mochi") }
    var showAppTour by remember { mutableStateOf(false) }
    var tourStep by remember { mutableStateOf(1) }

    LaunchedEffect(MainActivity.pendingInviteCode) {
        val pCode = MainActivity.pendingInviteCode
        if (!pCode.isNullOrBlank()) {
            tab = 3
            screen = ""
            // Handled in GroupsTab
        }
    }

    val navHistory = remember { mutableStateListOf(0) }

    BackHandler(enabled = screen.isNotEmpty() || openGroupId != null || openEventId != null || navHistory.size > 1 || tab != 0) {
        when {
            screen.isNotEmpty() -> screen = ""
            openGroupId != null -> openGroupId = null
            openEventId != null -> openEventId = null
            navHistory.size > 1 -> {
                navHistory.removeAt(navHistory.size - 1)
                tab = navHistory.last()
            }
            tab != 0 -> tab = 0
        }
    }

    Scaffold(containerColor = if (themes[themeIdx].scene.isNotEmpty()) Color.Transparent else MaterialTheme.colorScheme.background, bottomBar = {
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(32.dp)).padding(6.dp)
        ) {
            listOf(Icons.Rounded.Home to "Home", Icons.Rounded.TaskAlt to "To-Do", Icons.Rounded.CalendarMonth to "Plan",
                Icons.Rounded.Groups to "Groups", Icons.Rounded.Person to "Profile").forEachIndexed { i, (icon, label) ->
                val on = tab == i
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(26.dp))
                        .background(if (on) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                        .clickable {
                        if (i == tab && i == 3) openGroupId = null
                        if (i != tab) {
                            navHistory.add(i)
                            tab = i
                        }
                        screen = ""
                    }.padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp),
                            tint = if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        if (i == 3 && unreadSet.isNotEmpty())
                            Box(Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFE5484D)).align(Alignment.TopEnd))
                    }
                    Text(label, fontSize = 11.sp, color = if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            if (screen == "stats") StatsScreen(uid, subjects) { screen = "" }
            else if (screen == "badges") BadgesScreen(uid, streak, tasks) { screen = "" }
            else if (screen == "logs") LogsScreen(uid, subjects) { screen = "" }
            else if (screen == "events") EventsScreen(uid, subjects, events, openEventId) { screen = ""; openEventId = null }
            else if (screen == "challenges") ChallengesScreen(uid, groups) { screen = "" }
            else if (screen == "rankings") RankingsScreen(uid, groups) { screen = "" }
            else if (screen == "blocking") BlockingScreen(balance >= 300, {
                if (balance >= 300) {
                    me.collection("spends").add(mapOf<String, Any>("seconds" to 300L, "at" to System.currentTimeMillis(), "reason" to "emergency"))
                    prefs.edit().putLong("blk_override", System.currentTimeMillis() + 300_000L).apply()
                    true
                } else false
            }) { screen = "" }
            else if (screen == "safety") SafetyScreen(uid) { screen = "" }
            else if (screen == "chat") ChatScreen(uid, groups.find { it.id == chatGroup }, { chatGroup?.let { markRead(it) } }) { screen = "" }
            else if (screen == "settings") {
                val user = FirebaseAuth.getInstance().currentUser
                val studiedTodayTotal = today.values.sum() + (if (provisional) 0 else secs)
                SettingsScreen(user?.displayName ?: "", user?.email ?: "", themeIdx, onTheme, fontIdx, onFont, daysOff,
                    { me.set(mapOf("daysOff" to it), SetOptions.merge()) }, rate, cap, base, pendingFrom, studiedTodayTotal,
                    ::saveAdvanced, onSignOut, fp, { setFp(it) },
                    nick, { me.set(mapOf("nickname" to it.trim()), SetOptions.merge()) },
                    optIn, { me.set(mapOf("rankOptIn" to it), SetOptions.merge()) },
                    { prefs.edit().putBoolean("ack1", false).apply(); ack = false; screen = "" }) { screen = "" }
            } else when (tab) {
                0 -> {
                    HomeTab(subjects, today, running, secs, ::toggle,
                        { n, c -> me.collection("subjects").add(mapOf("name" to n, "color" to c, "order" to System.currentTimeMillis())) },
                        { id -> me.collection("subjects").document(id).delete() },
                        { id, c -> me.collection("subjects").document(id).update("color", c) }, balance, using, dayOff, paused, { if (breakLeft == 0 && !awaiting) paused = !paused }, ::endStudy, groups, uid,
                        streak, goalToday, studiedNow, revivable, balance >= REVIVE_COST_MIN * 60, { raiseGoal = true }, { revive() },
                        nextEvent, { openEventId = nextEvent?.id; screen = "events" },
                        breakLeft, awaiting, if (fp.pomo) maxOf(0, fp.focus * 60 - focusSecs) else -1, ::resumeAfterBreak,
                        fp.pomo, { setFp(fp.copy(pomo = it)) }, upNext,
                        mType, mName, { t, n ->
                            mType = t; mName = n
                            prefs.edit().putString("mascot_type", t.id).putString("mascot_name", n).apply()
                            me.set(mapOf("mascotType" to t.id, "mascotName" to n), SetOptions.merge())
                        }, { showLedger = true }) {
                        if (using) stopUsing() else { saveRunning(); running = null; paused = false; saveTask(); using = true
                            val t0 = System.currentTimeMillis()
                            prefs.edit().putLong("blk_use_start", t0).putLong("blk_use_until", t0 + balance * 1000).apply() }
                    }
                }
                1 -> TodoTab(subjects, tasks,
                    { t, s, m, k, d -> me.collection("tasks").add(mapOf("title" to t, "subject" to s, "done" to false, "dur" to m, "kind" to k, "date" to d, "order" to System.currentTimeMillis())) },
                    { t -> completeTask(t) },
                    { t -> me.collection("tasks").document(t.id).delete() },
                    { list, i, d ->
                        val l = list.toMutableList(); val j = i + d
                        if (j in l.indices) {
                            val x = l.removeAt(i); l.add(j, x)
                            l.forEachIndexed { k, y -> me.collection("tasks").document(y.id).update("order", k.toLong()) }
                        }
                    }, runningTask, taskSecs, ::toggleTask, doneToday, cap, rate,
                    { t, d -> me.collection("tasks").document(t.id).update("done", d) },
                    { t -> me.collection("tasks").document(t.id).update("date", todayKey) })
                4 -> {
                    val user = FirebaseAuth.getInstance().currentUser
                    val userSessions = rememberSessions(uid)
                    var showMascotProfileDlg by remember { mutableStateOf(false) }
                    ProfileTab(user?.displayName ?: "", user?.email ?: "", today.values.sum(), subjects.size,
                        tasks.count { it.done }, tasks.size, streak) {
                        if (it == "ledger") showLedger = true
                        else if (it == "tour") { tourStep = 1; tab = 0; showAppTour = true }
                        else if (it == "mascot") { showMascotProfileDlg = true }
                        else if (it == "export") {
                            exportStudyData(ctx, user?.displayName ?: "Student", userSessions, tasks, subjects)
                        } else screen = it
                    }
                    if (showMascotProfileDlg) {
                        MascotDialog(
                            currentType = mType,
                            currentName = mName,
                            onSave = { t, n ->
                                mType = t
                                mName = n
                                prefs.edit().putString("mascot_type", t.id).putString("mascot_name", n).apply()
                                showMascotProfileDlg = false
                            },
                            onDismiss = { showMascotProfileDlg = false }
                        )
                    }
                }
                2 -> TimetableTab(uid, subjects, slots, onStartStudy = { subj ->
                    toggle(subj)
                    tab = 0
                })
                3 -> {
                    val myLiveStudying = (running != null && !paused) || (runningTask != null)
                    val myLiveSecs = today.values.sum() + secs + taskSecs
                    GroupsTab(uid, groups, reqs, unreadSet, openGroupId, { openGroupId = it },
                        { chatGroup = it; screen = "chat" }, { screen = "rankings" },
                        myLiveStudying, myLiveSecs, { tab = 0 })
                }
                else -> Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                    Text("Coming soon", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Only show hanging companion when big mascot card is NOT visible (hide on HomeTab)
            val showHangingMascot = (tab != 0) || (running != null) || (screen.isNotEmpty())
            if (showHangingMascot && !showAppTour) {
                DraggableHangingMascotOverlay(
                    mascotType = mType,
                    mascotName = mName
                )
            }

            // 💡 App Interactive Guided Tour
            if (showAppTour) {
                AppTourGuide(
                    step = tourStep,
                    onStepChange = { s ->
                        tourStep = s
                        tab = when (s) {
                            1, 2, 3, 4 -> 0
                            5 -> 1
                            6 -> 2
                            7 -> 3
                            8 -> 4
                            else -> 0
                        }
                    },
                    onClose = {
                        showAppTour = false
                        prefs.edit().putBoolean("has_completed_tour", true).apply()
                        tab = 0
                    }
                )
            }
        }
    }
}

@Composable
fun ColorRow(selected: Long, onPick: (Long) -> Unit) {
    Column {
        palette.chunked(6).forEach { row ->
            Row(Modifier.padding(vertical = 4.dp)) {
                row.forEach { c ->
                    Box(Modifier.padding(3.dp).size(34.dp).clip(CircleShape).background(Color(c)).clickable { onPick(c) },
                        contentAlignment = Alignment.Center) { if (c == selected) Text("✓", color = Color.Black) }
                }
            }
        }
    }
}


data class SpendEntry(val id: String, val seconds: Long, val at: Long, val reason: String?)

data class LedgerItem(
    val title: String,
    val subtitle: String,
    val deltaMins: Int,
    val icon: String,
    val isEarned: Boolean,
    val timeStr: String
)

@Composable
fun RewardLedgerDialog(
    baseMins: Int,
    rate: Int,
    todayStudy: Map<String, Long>,
    tasks: List<TaskItem>,
    giftSecs: Long,
    spends: List<SpendEntry>,
    usingSecs: Long,
    balance: Long,
    onDismiss: () -> Unit
) {
    val dayStart = startOfDay()
    val timeFmt = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())

    val items = mutableListOf<Pair<Long, LedgerItem>>()

    // 1. Daily Free Allowance
    if (baseMins > 0) {
        items.add(dayStart to LedgerItem("Daily Free Allowance", "Base daily screen time", baseMins, "🎁", true, "12:00 AM"))
    }

    // 2. Study Sessions
    todayStudy.forEach { (subject, secs) ->
        if (secs > 0) {
            val earnedMins = ((secs * rate) / 3600).toInt()
            val timeLabel = timeFmt.format(java.util.Date(dayStart + secs * 1000L))
            items.add((dayStart + secs * 1000L) to LedgerItem(
                "$subject Study",
                "${secs / 60}m studied (${rate}m rate)",
                earnedMins,
                "📚",
                true,
                timeLabel
            ))
        }
    }

    // 3. Completed Wellness & Physical Quests (+1.5x bonus)
    tasks.filter { it.kind == "reward" && it.doneAt >= dayStart }.forEach { t ->
        val earnedMins = (t.reward / 60).toInt()
        val timeLabel = timeFmt.format(java.util.Date(t.doneAt))
        items.add(t.doneAt to LedgerItem(t.title, "Quest completed (+1.5x bonus)", earnedMins, "🍬", true, timeLabel))
    }

    // 4. Squad Gifts
    if (giftSecs > 0) {
        val giftMins = (giftSecs / 60).toInt()
        items.add((dayStart + 2) to LedgerItem("Squad Screen Time Gift", "Approved by study squad", giftMins, "💖", true, "Today"))
    }

    // 5. Spends
    spends.forEach { s ->
        val spentMins = maxOf(1, (s.seconds / 60).toInt())
        val (reasonText, icon) = when (s.reason) {
            "emergency" -> "Emergency Unlock Bypass" to "🚨"
            "revive" -> "Streak Revive Cost" to "❤️‍🩹"
            else -> "Unlocked App Usage" to "📱"
        }
        val timeLabel = timeFmt.format(java.util.Date(s.at))
        items.add(s.at to LedgerItem(reasonText, "Deducted from balance", -spentMins, icon, false, timeLabel))
    }

    // 6. Currently Active Playtime
    if (usingSecs > 0) {
        val activeMins = maxOf(1, (usingSecs / 60).toInt())
        items.add(System.currentTimeMillis() to LedgerItem("Active Playtime", "Current session in progress", -activeMins, "⏳", false, "Now"))
    }

    val sortedItems = items.sortedByDescending { it.first }.map { it.second }
    val totalEarned = (baseMins + todayStudy.values.sum() * rate / 3600 + tasks.filter { it.kind == "reward" && it.doneAt >= dayStart }.sumOf { it.reward } / 60 + giftSecs / 60).toInt()
    val totalSpent = (spends.sumOf { it.seconds } / 60 + usingSecs / 60).toInt()

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp)) {
                // Header
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(42.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📜", fontSize = 22.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Screen Time Ledger", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Complete audit of earned & spent time", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDismiss) {
                        Text("✕", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(Modifier.height(14.dp))

                // 3 Distinct Pastel Summary Cards
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Card 1: Earned
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(Modifier.padding(vertical = 12.dp, horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Earned", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32))
                            Spacer(Modifier.height(4.dp))
                            Text("+$totalEarned m", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        }
                    }

                    // Card 2: Spent
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFFFEBEE),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE57373).copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(Modifier.padding(vertical = 12.dp, horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Spent", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFC62828))
                            Spacer(Modifier.height(4.dp))
                            Text("-$totalSpent m", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB71C1C))
                        }
                    }

                    // Card 3: Available
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFF3E5F5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBA68C8).copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(Modifier.padding(vertical = 12.dp, horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Available", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6A1B9A))
                            Spacer(Modifier.height(4.dp))
                            Text(clock(balance), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A148C),
                                style = androidx.compose.ui.text.TextStyle(fontFeatureSettings = "tnum"))
                        }
                    }
                }

                // Rate Info Pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(Modifier.padding(horizontal = 12.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡", fontSize = 13.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Current Rate: ${rate}m screen time per 1 hr study (${if (rate >= 60) "1:1 Parity" else "${60 / maxOf(1, rate)}:1 Focus"})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Transactions List
                if (sortedItems.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌱 (•‿•) ☀️", fontSize = 32.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("No balance changes recorded yet today!", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Study or complete wellness quests to earn screen time.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sortedItems) { item ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        Modifier.size(40.dp).clip(CircleShape).background(
                                            if (item.isEarned) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                        ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(item.icon, fontSize = 18.sp)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text(item.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (item.isEarned) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                        ) {
                                            Text(
                                                (if (item.deltaMins >= 0) "+${item.deltaMins}m" else "${item.deltaMins}m"),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.isEarned) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Spacer(Modifier.height(2.dp))
                                        Text(item.timeStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Close Ledger ✓", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HomeTab(
    subjects: List<Subject>, today: Map<String, Long>, running: String?, secs: Int,
    onToggle: (String) -> Unit, onAdd: (String, Long) -> Unit, onDelete: (String) -> Unit,
    onRecolor: (String, Long) -> Unit, balance: Long, using: Boolean, dayOff: Boolean,
    paused: Boolean, onPause: () -> Unit, onEnd: () -> Unit, groups: List<Group>, uid: String,
    streak: Int, goalMins: Long, studied: Long, revivable: Int, canRevive: Boolean, onGoal: () -> Unit, onRevive: () -> Unit,
    nextEvent: EventItem?, onEvent: () -> Unit,
    breakLeft: Int, awaiting: Boolean, roundLeft: Int, onResumeBreak: () -> Unit, pomo: Boolean, onPomo: (Boolean) -> Unit, upNext: Slot?,
    mascotType: MascotType = MascotType.MOCHI, mascotName: String = "Mochi", onMascotChange: (MascotType, String) -> Unit = { _, _ -> },
    onLedger: () -> Unit = {},
    onUse: () -> Unit
) {
    if (running != null) {   // while a subject runs, show the focus screen with the group
        FocusView(running, subjects.find { it.name == running }?.color ?: palette[0], secs, paused,
            today.values.sum(), uid, groups, onPause, onEnd, breakLeft, awaiting, roundLeft, onResumeBreak, mascotType, mascotName)
        return
    }
    var edit by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var newColor by remember { mutableStateOf(palette[0]) }
    var recolor by remember { mutableStateOf<Subject?>(null) }
    var showStreak by remember { mutableStateOf(false) }
    val total = today.values.sum() + secs

    // Entire Home tab is scrollable so all content and subjects are accessible on every phone screen!
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 100.dp)
    ) {
        val chip = MaterialTheme.colorScheme.surfaceVariant
        Row(Modifier.fillMaxWidth().padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(SimpleDateFormat("EEE, MM/dd", Locale.getDefault()).format(Date()), Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (nextEvent != null) {
                val dl = daysBetween(dateKey(), nextEvent.date)
                Text("🎯 " + (if (dl <= 0) "today" else "${dl}d"),
                    Modifier.padding(start = 6.dp).clip(RoundedCornerShape(14.dp)).background(chip)
                        .clickable { onEvent() }.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 13.sp)
            }
            if (!dayOff) Text("🔥 $streak" + if (revivable > 0) " ⚠️" else "",
                Modifier.padding(start = 6.dp).clip(RoundedCornerShape(14.dp)).background(chip)
                    .clickable { showStreak = true }.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 13.sp)
        }
        if (upNext != null) {
            val nm = Calendar.getInstance().let { it.get(Calendar.HOUR_OF_DAY) * 60 + it.get(Calendar.MINUTE) }
            Text("⏰ " + (if (upNext.start <= nm) "Now" else "Up next") + ": ${upNext.subject} • ${minLabel(upNext.start)}",
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
        }

        var showMascotDlg by remember { mutableStateOf(false) }
        InteractiveMascotCard(
            type = mascotType,
            name = mascotName,
            studiedSecs = total,
            goalMins = goalMins,
            streak = streak,
            onOpenCompanionPicker = { showMascotDlg = true },
            modifier = Modifier.padding(top = 10.dp)
        )
        if (showMascotDlg) {
            MascotDialog(
                currentType = mascotType,
                currentName = mascotName,
                onSave = { t, n ->
                    onMascotChange(t, n)
                    showMascotDlg = false
                },
                onDismiss = { showMascotDlg = false }
            )
        }

        // Timer display (comfortable sizing so subjects are never buried)
        Spacer(Modifier.height(16.dp))
        Text(
            clock(total),
            style = androidx.compose.ui.text.TextStyle(fontFeatureSettings = "tnum"),
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            "Studied today",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
        )

        // Screen time left / Playtime Balance card (ultra-polished pastel wallet card)
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.60f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (dayOff) "🏝️" else "🎮", fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(if (dayOff) "Vacation Day!" else "Playtime Bank", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(if (dayOff) "Unlimited screen time today ✨" else "Screen time earned from study", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Polished Ledger Button Pill
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier.clickable { onLedger() }
                    ) {
                        Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📜", fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text("Ledger ›", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Available Balance", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            if (dayOff) "∞ Unlimited" else clock(balance),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (using) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            style = androidx.compose.ui.text.TextStyle(fontFeatureSettings = "tnum")
                        )
                    }

                    if (!dayOff) {
                        Button(
                            onClick = onUse,
                            enabled = using || balance > 0,
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (using) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
                        ) {
                            Text(if (using) "Stop Playtime 🛑" else "Playtime", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // 🎧 Study Soundscape Card (Ambient Lo-Fi & Calming Noise)
        StudySoundscapeCard()

        Spacer(Modifier.height(16.dp))

        // Subjects Section Header + Prominent Add Button
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Learning Quests", Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Button(
                onClick = { edit = !edit },
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(if (edit) "Done ✓" else "+ Add / Edit")
            }
        }

        // Add Subject Form when edit is open
        if (edit) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("Add a new subject:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    ColorRow(newColor) { newColor = it }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it.take(25) },
                            label = { Text("Subject name (e.g. Physics, Math)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    onAdd(name.trim(), newColor)
                                    name = ""
                                    newColor = palette[(subjects.size + 1) % palette.size]
                                }
                            },
                            enabled = name.isNotBlank()
                        ) { Text("Add") }
                    }
                }
            }
        }

        // Subject Items
        if (subjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .clickable { edit = true }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📚", fontSize = 32.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("No subjects yet!", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Tap here or '+ Add / Edit' above to create your first subject! 🌸",
                        fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            subjects.forEach { s ->
                val on = running == s.name
                val t = (today[s.name] ?: 0L) + if (on) secs else 0
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(48.dp).clip(CircleShape).background(Color(s.color))
                            .clickable { if (edit) recolor = s else onToggle(s.name) },
                        contentAlignment = Alignment.Center
                    ) { Text(if (edit) "🎨" else if (on) "❚❚" else "▶", color = Color.Black) }
                    Spacer(Modifier.width(16.dp))
                    Text(s.name, Modifier.weight(1f), fontSize = 17.sp, fontWeight = FontWeight.Medium)
                    Text(clock(t), color = if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, fontWeight = if (on) FontWeight.Bold else FontWeight.Normal)
                    if (edit) TextButton(onClick = { onDelete(s.id) }) { Text("✕", color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
    if (showStreak) {
        AlertDialog(onDismissRequest = { showStreak = false },
            title = { Text(if (streak > 0) "🔥 $streak-day streak" else "🌱 No streak yet") },
            text = {
                Column {
                    if (goalMins > 0) {
                        Text("Goal ${goalLabel(goalMins)} • studied ${hm(studied)}" + if (studied >= goalMins * 60) " ✅" else "")
                        Box(Modifier.padding(top = 8.dp).fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)) {
                            Box(Modifier.fillMaxWidth((studied.toFloat() / (goalMins * 60)).coerceIn(0.02f, 1f))
                                .fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                        }
                    }
                    if (revivable > 0) {
                        Text("You missed yesterday's goal", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                        Text("Revive your $revivable-day streak by giving up $REVIVE_COST_MIN min of today's screen time. Only possible today.",
                            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 6.dp))
                        Button(onClick = { onRevive(); showStreak = false }, enabled = canRevive) {
                            Text(if (canRevive) "Revive streak" else "Not enough screen time left")
                        }
                    }
                    TextButton(onClick = { showStreak = false; onGoal() }) { Text("Raise today's goal") }
                }
            },
            confirmButton = { TextButton(onClick = { showStreak = false }) { Text("Close") } })
    }
    recolor?.let { s ->
        AlertDialog(onDismissRequest = { recolor = null },
            confirmButton = { TextButton(onClick = { recolor = null }) { Text("Close") } },
            title = { Text("Colour for ${s.name}") },
            text = { ColorRow(s.color) { onRecolor(s.id, it); recolor = null } })
    }
}

@Composable
fun RowScope.StatCard(label: String, value: String, icon: String = "✨") {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)),
        modifier = Modifier.weight(1f).padding(4.dp)
    ) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 20.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(2.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
fun SafetyScreen(uid: String, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE) }
    var blockedUsers by remember { mutableStateOf(prefs.getStringSet("blocked_users", emptySet()) ?: emptySet()) }
    var newBlockName by remember { mutableStateOf("") }
    var reportUser by remember { mutableStateOf("") }
    var reportReason by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 60.dp)
    ) {
        Row(Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Text("‹", fontSize = 28.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(8.dp))
            Text("🛡️ Safety & Privacy", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        // Section 1: Block Users
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f),
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("🚫 Blocked Users", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Blocked users cannot message you or see your study status in squads.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newBlockName,
                        onValueChange = { newBlockName = it },
                        label = { Text("Username to block") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newBlockName.isNotBlank()) {
                                val updated = blockedUsers + newBlockName.trim()
                                blockedUsers = updated
                                prefs.edit().putStringSet("blocked_users", updated).apply()
                                newBlockName = ""
                                Toast.makeText(ctx, "User blocked", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Block") }
                }
                if (blockedUsers.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text("Currently Blocked:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    blockedUsers.forEach { bUser ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• $bUser", fontSize = 13.sp)
                            Text(
                                "Unblock",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    val updated = blockedUsers - bUser
                                    blockedUsers = updated
                                    prefs.edit().putStringSet("blocked_users", updated).apply()
                                }
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Report Inappropriate Content
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f),
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("🚩 Report User or Content", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Help keep StudyReward a supportive, safe study community.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = reportUser,
                    onValueChange = { reportUser = it },
                    label = { Text("User or squad name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reportReason,
                    onValueChange = { reportReason = it },
                    label = { Text("Reason (Harassment, inappropriate name, spam)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (reportUser.isNotBlank() && reportReason.isNotBlank()) {
                            try {
                                FirebaseFirestore.getInstance().collection("reports").add(
                                    mapOf(
                                        "reportedUser" to reportUser.trim(),
                                        "reason" to reportReason.trim(),
                                        "byUid" to uid,
                                        "at" to System.currentTimeMillis()
                                    )
                                )
                            } catch (e: Exception) { }
                            reportUser = ""
                            reportReason = ""
                            Toast.makeText(ctx, "Report submitted. Thank you for keeping our community safe! ✨", Toast.LENGTH_LONG).show()
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Submit Report") }
            }
        }

        // Section 3: Delete Account & Data (GDPR Compliance)
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFFFEBEE),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("🗑️ Delete Account & Data", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFC62828))
                Text("Permanently wipes your study history, streaks, and balances from this device.", fontSize = 11.sp, color = Color(0xFFB71C1C))
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Wipe My Data & Sign Out", color = Color.White) }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Permanent Account Wipe") },
            text = { Text("Are you sure you want to permanently delete your data? All focus history, earned playtime, and tasks will be erased.") },
            confirmButton = {
                Button(
                    onClick = {
                        prefs.edit().clear().apply()
                        try { FirebaseAuth.getInstance().signOut() } catch (e: Exception) { }
                        showDeleteConfirm = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text("Yes, Delete Everything") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ProfileTab(name: String, email: String, studied: Long, subjectCount: Int, done: Int, total: Int, streak: Int, onOpen: (String) -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        Text("Student Lounge", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 16.dp))

        // Profile Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.50f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(60.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                    Text(name.take(1).uppercase().ifBlank { "🐾" }, fontSize = 26.sp, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(name.ifBlank { "Focus Hero" }, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(email.ifBlank { "Self-Directed Student" }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { onOpen("badges") }
                    ) {
                        Text("🌟 Badges & Achievements Shelf ›", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }
        }

        // Stats Row
        Row(Modifier.fillMaxWidth()) {
            StatCard("Studied today", clock(studied), "⏱️")
            StatCard("Day Streak", "🔥 $streak", "🏆")
            StatCard("Tasks done", "$done/$total", "✅")
        }

        Spacer(Modifier.height(18.dp))
        Text("Personal Controls", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 8.dp))

        listOf(
            "🐾  Change Study Mascot & Personality" to "mascot",
            "💡  App Guided Tour (Feature Walkthrough)" to "tour",
            "Screen Time & Reward Ledger" to "ledger",
            "🏆  Badges & Achievements Shelf" to "badges",
            "📊  Study Statistics & Insights" to "stats",
            "🎯  Target Events & Countdowns" to "events",
            "🔥  Study Challenges" to "challenges",
            "📝  Detailed Study Logs" to "logs",
            "🔒  App blocking & Screen Limits" to "blocking",
            "📦  Backup & Export Data" to "export",
            "⚙️  App & Focus Settings" to "settings",
            "🛡️  Safety, Privacy & Block Users" to "safety"
        ).forEach { (label, key) ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { onOpen(key) }
            ) {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(label, Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text("›", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

val rewardTypes = listOf("🍽️ Washing dishes", "💪 Workout", "🏃 Running", "🚴 Cycling", "🧹 Cleaning",
    "🚶 Walking", "🧺 Laundry", "🍳 Cooking", "🧘 Yoga", "Other")

@Composable
fun TodoTab(
    subjects: List<Subject>, tasks: List<TaskItem>, onAdd: (String, String, Long, String, String) -> Unit,
    onCheck: (TaskItem) -> Unit, onDelete: (TaskItem) -> Unit, onMove: (List<TaskItem>, Int, Int) -> Unit,
    runningTask: String?, taskSecs: Int, onTaskToggle: (String) -> Unit, doneToday: Int, cap: Int, rate: Int,
    onStudyCheck: (TaskItem, Boolean) -> Unit, onToday: (TaskItem) -> Unit
) {
    var addingFor by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf<String?>(null) }
    var custom by remember { mutableStateOf("") }
    var mins by remember { mutableStateOf("") }
    var back by remember { mutableStateOf(0) }            // 0 = today, 1 = yesterday, -1 = tomorrow
    var showEarlier by remember { mutableStateOf(false) }
    val todayKey = dateKey()
    val dayKey = keyBack(back)
    val dayTasks = tasks.filter { it.date == dayKey }
    val rewards = dayTasks.filter { it.kind == "reward" }
    val study = dayTasks.filter { it.kind != "reward" }
    val earlier = tasks.filter { it.date < todayKey && !it.done }
    val names = subjects.map { it.name }
    val groups = subjects.map { Triple(it.name, Color(it.color), study.filter { t -> t.subject == it.name }) } +
            listOfNotNull(study.filter { it.subject !in names }.takeIf { it.isNotEmpty() }?.let { Triple("Other", MaterialTheme.colorScheme.onSurfaceVariant, it) })
    val chipOn = MaterialTheme.colorScheme.primary
    val chipOff = MaterialTheme.colorScheme.surfaceVariant

    var showGeneralAddDialog by remember { mutableStateOf(false) }
    var generalTitle by remember { mutableStateOf("") }
    var generalSubject by remember { mutableStateOf("General") }
    var generalMins by remember { mutableStateOf("25") }

    if (showGeneralAddDialog) {
        AlertDialog(
            onDismissRequest = { showGeneralAddDialog = false },
            title = { Text("Add Task", fontWeight = FontWeight.Bold) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = generalTitle,
                        onValueChange = { generalTitle = it.take(50) },
                        label = { Text("Task title") },
                        placeholder = { Text("e.g. Read chapter, Review notes, Clean desk") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("Subject / Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    val allSubjectOptions = listOf("General") + subjects.map { it.name }
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allSubjectOptions.forEach { sName ->
                            FilterChip(
                                selected = generalSubject == sName,
                                onClick = { generalSubject = sName },
                                label = { Text(sName, fontSize = 11.sp) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Estimated minutes:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("15", "25", "45", "60").forEach { mVal ->
                            FilterChip(
                                selected = generalMins == mVal,
                                onClick = { generalMins = mVal },
                                label = { Text("${mVal}m", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val t = generalTitle.trim()
                        if (t.isNotBlank()) {
                            val dur = generalMins.toLongOrNull() ?: 25L
                            onAdd(t, generalSubject, dur, "study", dayKey)
                            generalTitle = ""
                            showGeneralAddDialog = false
                        }
                    }
                ) { Text("Add Task", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showGeneralAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Daily Tasks", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Complete tasks to earn bonus screen time", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = { showGeneralAddDialog = true },
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("+ Add Task", fontWeight = FontWeight.Bold)
            }
        }
        LazyColumn {
            item {
                Text("Task Quests", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 16.dp))
                Text("Earn +1.5x bonus screen time for physical & wellness tasks! Today: $doneToday / $cap completed",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
                TaskDayNav(back) { back = it }
                if (back != 0) Text("You can plan other days, but you can only tick or time tasks on today's list.",
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                
                // Reward Task Type Pills
                Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 6.dp)) {
                    rewardTypes.forEach { r ->
                        Text(r, Modifier.padding(end = 6.dp).clip(RoundedCornerShape(16.dp))
                            .background(if (type == r) chipOn else chipOff).clickable { type = if (type == r) null else r }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                            color = if (type == r) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (type == r) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp)
                    }
                }
                if (type != null) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.50f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (type == "Other") OutlinedTextField(custom, { custom = it }, label = { Text("Task name") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))
                            else Text(type!!, Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(mins, { mins = it.filter { c -> c.isDigit() }.take(3) }, label = { Text("Mins 15+") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.width(96.dp), shape = RoundedCornerShape(14.dp))
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val n = if (type == "Other") custom.trim() else type!!
                                    if (n.isNotBlank()) {
                                        onAdd(n, "", maxOf(15L, mins.toLongOrNull() ?: 15L), "reward", dayKey)
                                        type = null; custom = ""; mins = ""
                                    }
                                },
                                shape = RoundedCornerShape(16.dp)
                            ) { Text("Add ✨") }
                            Spacer(Modifier.width(6.dp))
                            OutlinedButton(
                                onClick = { type = null; custom = ""; mins = "" },
                                shape = RoundedCornerShape(16.dp)
                            ) { Text("Done ✓") }
                        }
                    }
                }

                if (rewards.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🍬 (｡♥‿♥｡) 🍬", fontSize = 24.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("No reward tasks for this day!", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Tap an activity chip above to schedule one and earn bonus screen time!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            itemsIndexed(rewards) { i, t ->
                val live = t.worked + if (runningTask == t.id) taskSecs else 0
                val ready = live >= t.dur * 60
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = if (t.done) 0.25f else 0.45f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (t.done) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(t.done, { if (it) onCheck(t) }, enabled = !t.done && ready && back == 0)
                        Column(Modifier.weight(1f).padding(horizontal = 6.dp)) {
                            Text(t.title, color = if (t.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = if (t.done) TextDecoration.LineThrough else null)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        if (t.done) (if (t.reward > 0) "🍬 +${t.reward / 60}m earned" else "limit reached")
                                        else "${mmss(minOf(live, t.dur * 60))} / ${t.dur}:00 • +${rewardSecs(t.dur, rate) / 60}m 🍬",
                                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        if (!t.done && !ready && back == 0) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { onTaskToggle(t.id) }.padding(horizontal = 4.dp)
                            ) {
                                Text(if (runningTask == t.id) "❚❚" else "▶", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                            }
                        }
                        Text("▲", Modifier.clickable(enabled = i > 0) { onMove(rewards, i, -1) }.padding(6.dp),
                            color = if (i > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Text("▼", Modifier.clickable(enabled = i < rewards.size - 1) { onMove(rewards, i, 1) }.padding(6.dp),
                            color = if (i < rewards.size - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Text("✕", Modifier.clickable { onDelete(t) }.padding(6.dp), color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            item {
                HorizontalDivider(Modifier.padding(top = 16.dp, bottom = 8.dp), color = chipOff)
                Text("📚 Subject Study To-Dos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 8.dp))
                if (groups.isEmpty()) Text("Add subjects on the Home tab first.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            items(groups) { (name, color, list) ->
                Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.width(6.dp).height(22.dp).background(color, RoundedCornerShape(3.dp)))
                    Spacer(Modifier.width(10.dp))
                    Text(name, Modifier.weight(1f), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    if (name != "Other") {
                        Surface(
                            shape = CircleShape,
                            color = if (addingFor == name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.clickable { addingFor = if (addingFor == name) null else name }
                        ) {
                            Text(
                                if (addingFor == name) "Done ✓" else "+ Add Task",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (addingFor == name) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                if (addingFor == name) Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(title, { title = it }, label = { Text("New study task") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { if (title.isNotBlank()) { onAdd(title.trim(), name, 0L, "", dayKey); title = "" } }, shape = RoundedCornerShape(16.dp)) { Text("Add") }
                    Spacer(Modifier.width(6.dp))
                    OutlinedButton(onClick = { addingFor = null; title = "" }, shape = RoundedCornerShape(16.dp)) { Text("Done ✓") }
                }
                list.forEachIndexed { i, t ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (t.done) 0.30f else 0.50f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                    ) {
                        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(t.done, { onStudyCheck(t, it) }, enabled = back == 0)
                            Text(t.title, Modifier.weight(1f).padding(horizontal = 6.dp),
                                color = if (t.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                textDecoration = if (t.done) TextDecoration.LineThrough else null)
                            Text("▲", Modifier.clickable(enabled = i > 0) { onMove(list, i, -1) }.padding(6.dp),
                                color = if (i > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            Text("▼", Modifier.clickable(enabled = i < list.size - 1) { onMove(list, i, 1) }.padding(6.dp),
                                color = if (i < list.size - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            Text("✕", Modifier.clickable { onDelete(t) }.padding(6.dp), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            item {
                if (back == 0 && earlier.isNotEmpty()) {
                    Text((if (showEarlier) "▼ " else "▶ ") + "Earlier, not done (${earlier.size})",
                        Modifier.clickable { showEarlier = !showEarlier }.padding(vertical = 14.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                    if (showEarlier) earlier.sortedBy { it.date }.forEach { t ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(t.title, fontWeight = FontWeight.Medium)
                                    Text((if (t.kind == "reward") "Reward task • " else t.subject + " • ") +
                                            (if (t.date.startsWith("2000")) "older" else niceDate(t.date)), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                TextButton(onClick = { onToday(t) }) { Text("📅 Move to Today") }
                                Text("✕", Modifier.clickable { onDelete(t) }.padding(6.dp), color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}
@Composable
fun LiveCameraPreview(modifier: Modifier = Modifier) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    AndroidView(
        factory = { context ->
            TextureView(context).apply {
                clipToOutline = true
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: android.view.View, outline: Outline) {
                        val radius = 24f * context.resources.displayMetrics.density
                        outline.setRoundRect(0, 0, view.width, view.height, radius)
                    }
                }
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    var cameraDevice: CameraDevice? = null
                    var captureSession: CameraCaptureSession? = null
                    var previewSize: android.util.Size = android.util.Size(1280, 720)

                    fun applyTransform(viewW: Int, viewH: Int) {
                        if (viewW <= 0 || viewH <= 0) return
                        val matrix = Matrix()
                        val centerX = viewW / 2f
                        val centerY = viewH / 2f

                        // Front camera sensor orientation outputs landscape frames (e.g. 1280x720).
                        // In portrait device orientation, sensor height acts as width and sensor width acts as height:
                        val camW = previewSize.height.toFloat() // e.g. 720
                        val camH = previewSize.width.toFloat()  // e.g. 1280
                        val previewAspect = camW / camH         // 0.5625 (9:16)
                        val viewAspect = viewW.toFloat() / viewH.toFloat()

                        val scaleX: Float
                        val scaleY: Float
                        if (viewAspect > previewAspect) {
                            // View is wider than camera portrait aspect ratio (e.g. 4:3 or 1:1)
                            // Fit width, scale height up so camera aspect ratio is preserved (center-crop, no stretch)
                            scaleX = 1f
                            scaleY = (viewW / previewAspect) / viewH
                        } else {
                            // View is taller than camera portrait aspect ratio
                            // Fit height, scale width up (center-crop, no stretch)
                            scaleX = (viewH * previewAspect) / viewW
                            scaleY = 1f
                        }

                        // Apply center-crop scale (un-stretched, non-mirrored)
                        matrix.setScale(scaleX, scaleY, centerX, centerY)

                        setTransform(matrix)
                    }

                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        try {
                            val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                            val frontCameraId = manager.cameraIdList.firstOrNull { id ->
                                val chars = manager.getCameraCharacteristics(id)
                                chars.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
                            } ?: manager.cameraIdList.firstOrNull() ?: return

                            val chars = manager.getCameraCharacteristics(frontCameraId)
                            val map = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                            val sizes = map?.getOutputSizes(SurfaceTexture::class.java) ?: emptyArray()

                            // Select standard 16:9 preview size (prefer 1280x720 or 1920x1080)
                            val optimalSize = sizes.filter { s ->
                                val aspect = s.width.toFloat() / s.height.toFloat()
                                Math.abs(aspect - (16f / 9f)) < 0.05f && s.width <= 1920 && s.height <= 1080
                            }.maxByOrNull { it.width * it.height } ?: sizes.firstOrNull() ?: android.util.Size(1280, 720)

                            previewSize = optimalSize
                            surface.setDefaultBufferSize(previewSize.width, previewSize.height)
                            applyTransform(width, height)

                            if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                manager.openCamera(frontCameraId, object : CameraDevice.StateCallback() {
                                    override fun onOpened(camera: CameraDevice) {
                                        cameraDevice = camera
                                        try {
                                            val surfaceObj = android.view.Surface(surface)
                                            val builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                                                addTarget(surfaceObj)
                                            }
                                            camera.createCaptureSession(listOf(surfaceObj), object : CameraCaptureSession.StateCallback() {
                                                override fun onConfigured(session: CameraCaptureSession) {
                                                    captureSession = session
                                                    try {
                                                        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                                                        session.setRepeatingRequest(builder.build(), null, null)
                                                    } catch (e: Exception) { }
                                                }
                                                override fun onConfigureFailed(session: CameraCaptureSession) { }
                                            }, null)
                                        } catch (e: Exception) { }
                                    }
                                    override fun onDisconnected(camera: CameraDevice) {
                                        camera.close()
                                        cameraDevice = null
                                    }
                                    override fun onError(camera: CameraDevice, error: Int) {
                                        camera.close()
                                        cameraDevice = null
                                    }
                                }, null)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                        applyTransform(width, height)
                    }

                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        try {
                            captureSession?.close()
                            cameraDevice?.close()
                        } catch (e: Exception) { }
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) { }
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun LiveStudyRoomDialog(
    group: Group,
    members: List<Member>,
    uid: String,
    now: Long,
    myLiveStudying: Boolean,
    myLiveSecs: Long,
    onStartStudy: () -> Unit,
    onCheer: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var isCamOn by remember { mutableStateOf(false) }
    var isMicOn by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var callSecs by remember { mutableStateOf(0) }
    var showReactions by remember { mutableStateOf(false) }
    var reactionBanner by remember { mutableStateOf<String?>(null) }

    val camLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) isCamOn = true
        else Toast.makeText(ctx, "Camera permission needed for video study", Toast.LENGTH_SHORT).show()
    }

    val micLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) isMicOn = true
        else Toast.makeText(ctx, "Microphone permission needed to speak", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSecs++
        }
    }

    LaunchedEffect(reactionBanner) {
        if (reactionBanner != null) {
            delay(2500)
            reactionBanner = null
        }
    }

    val otherMembers = members.filter { it.id != uid }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101216))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Top Zoom Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE53935),
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "LIVE STUDY CALL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.12f)
                        ) {
                            Text(
                                group.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFFB4A2),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.4f)
                        ) {
                            Text(
                                "⏱️ ${clock(callSecs.toLong())}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = androidx.compose.ui.text.TextStyle(fontFeatureSettings = "tnum")
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // In-Call Study Timer Pill (Start/Study Live indicator)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (myLiveStudying) Color(0xFF1B5E20).copy(alpha = 0.40f) else Color(0xFF37474F).copy(alpha = 0.50f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (myLiveStudying) Color(0xFF81C784).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (myLiveStudying) "🟢" else "📚", fontSize = 14.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (myLiveStudying) "You are live focusing! • ${clock(myLiveSecs)} studied"
                                else "Study timer paused • Ready to focus?",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (!myLiveStudying) {
                            Text(
                                "Start Study ▶",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB4A2),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .clickable {
                                        onDismiss()
                                        onStartStudy()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Video Participant Tiles (Zoom Grid Layout)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // TILE 1: YOUR VIDEO / AVATAR TILE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1E222B))
                            .border(1.5.dp, if (isCamOn) Color(0xFF81C784).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.10f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCamOn) {
                            LiveCameraPreview(modifier = Modifier.fillMaxSize())
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2C323F))
                                        .border(2.dp, if (isMicOn) Color(0xFF81C784) else Color.Transparent, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🌱", fontSize = 36.sp)
                                }
                                Spacer(Modifier.height(10.dp))
                                Text("Camera is Off", fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f))
                            }
                        }

                        // Bottom info bar overlay on your tile
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.55f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("You", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                if (myLiveStudying) {
                                    Spacer(Modifier.width(6.dp))
                                    Text("• 📚 Focusing", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isMicOn) Color(0xFF2E7D32) else Color(0xFFE53935).copy(alpha = 0.8f),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(
                                            if (isMicOn) Icons.Rounded.Mic else Icons.Rounded.MicOff,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // TILE 2: SQUAD PEERS (Zoom Member Tile)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1E222B))
                            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (otherMembers.isEmpty()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                                Text("🛋️", fontSize = 36.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Waiting for squad peers to join...", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("Invite your friends using squad code: ${group.code}", color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
                            }
                        } else {
                            val peer = otherMembers.first()
                            val peerLive = isLive(peer, now)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(if (peerLive) Color(0xFF1B5E20) else Color(0xFF2C323F))
                                        .border(2.5.dp, if (peerLive) Color(0xFF81C784) else Color.Transparent, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(if (peerLive) "🎧" else "🐰", fontSize = 36.sp)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    if (peerLive) "🟢 Live Focusing • Silent Study" else "Silent Focus Study",
                                    fontSize = 12.sp,
                                    color = if (peerLive) Color(0xFF81C784) else Color.White.copy(alpha = 0.5f)
                                )
                            }

                            // Bottom overlay for peer tile
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.55f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(peer.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(clock(liveStudy(peer, now)), color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Surface(shape = CircleShape, color = Color(0xFF37474F), modifier = Modifier.size(24.dp)) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Rounded.MicOff, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Reaction Banner popup
                if (reactionBanner != null) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            reactionBanner!!,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                // Reaction Picker Tray (when tapped)
                if (showReactions) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("👏", "🔥", "☕", "💡", "💖", "🎉").forEach { emoji ->
                            Text(
                                emoji,
                                fontSize = 26.sp,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable {
                                        reactionBanner = "You reacted with $emoji!"
                                        onCheer("$emoji (Live call reaction)")
                                        showReactions = false
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // FLOATING ZOOM CALL CONTROL BAR
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color(0xFF1E222B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. MIC TOGGLE
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    if (!isMicOn) {
                                        if (androidx.core.content.ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                            isMicOn = true
                                        } else {
                                            micLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                        }
                                    } else {
                                        isMicOn = false
                                    }
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isMicOn) Color(0xFF2E7D32) else Color(0xFF2C323F))
                            ) {
                                Icon(
                                    if (isMicOn) Icons.Rounded.Mic else Icons.Rounded.MicOff,
                                    contentDescription = "Toggle Mic",
                                    tint = if (isMicOn) Color.White else Color(0xFFE57373),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(if (isMicOn) "Mute" else "Unmute", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                        }

                        // 2. CAMERA TOGGLE
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    if (!isCamOn) {
                                        if (androidx.core.content.ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                            isCamOn = true
                                        } else {
                                            camLauncher.launch(android.Manifest.permission.CAMERA)
                                        }
                                    } else {
                                        isCamOn = false
                                    }
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isCamOn) Color(0xFF2E7D32) else Color(0xFF2C323F))
                            ) {
                                Icon(
                                    if (isCamOn) Icons.Rounded.Videocam else Icons.Rounded.VideocamOff,
                                    contentDescription = "Toggle Camera",
                                    tint = if (isCamOn) Color.White else Color(0xFFE57373),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(if (isCamOn) "Cam Off" else "Cam On", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                        }

                        // 3. SPEAKER / AUDIO TOGGLE
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { isSpeakerOn = !isSpeakerOn },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isSpeakerOn) Color(0xFF2C323F) else Color(0xFF37474F))
                            ) {
                                Icon(
                                    if (isSpeakerOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                                    contentDescription = "Toggle Speaker",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text("Speaker", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                        }

                        // 4. REACTIONS BUTTON
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { showReactions = !showReactions },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2C323F))
                            ) {
                                Text("✨", fontSize = 20.sp)
                            }
                            Spacer(Modifier.height(2.dp))
                            Text("React", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                        }

                        // 5. LEAVE CALL BUTTON (Red Round Zoom Button)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD32F2F))
                            ) {
                                Icon(
                                    Icons.Rounded.CallEnd,
                                    contentDescription = "End Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text("Leave", color = Color(0xFFEF9A9A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupsTab(
    uid: String, groups: List<Group>, reqs: Map<String, List<Req>>, unread: Set<String>,
    openId: String?, setOpenId: (String?) -> Unit,
    onChat: (String) -> Unit, onRankings: () -> Unit,
    myLiveStudying: Boolean = false, myLiveSecs: Long = 0L, onStartStudy: () -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    val ctx = LocalContext.current
    var dialog by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var passcodeInput by remember { mutableStateOf("") }
    var targetJoinSquad by remember { mutableStateOf<Group?>(null) }
    var msg by remember { mutableStateOf("") }
    var squadTab by remember { mutableStateOf(0) } // 0: My Squads, 1: Discover Squads
    var discoverQuery by remember { mutableStateOf("") }
    var discoverSquads by remember { mutableStateOf(listOf<Group>()) }
    var createIsPrivate by remember { mutableStateOf(false) }
    var createPassword by remember { mutableStateOf("") }
    var createCategory by remember { mutableStateOf("General") }

    // Preload ALL discoverable squads immediately on open so they appear directly with 0 search delay!
    DisposableEffect(Unit) {
        val reg = db.collection("groups").limit(60).addSnapshotListener { snap, _ ->
            discoverSquads = snap?.documents?.map {
                Group(
                    it.id,
                    it.getString("name") ?: "",
                    it.getString("admin") ?: "",
                    it.getString("code") ?: "",
                    (it.getLong("pool") ?: 120L).toInt(),
                    (it.get("members") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    it.getString("intro") ?: "",
                    it.getString("rules") ?: "",
                    (it.getLong("capacity") ?: 10L).toInt(),
                    it.getString("joinMethod") ?: "code",
                    it.getLong("nudgeAt") ?: 0L,
                    it.getString("nudgeBy") ?: "",
                    it.getBoolean("isPrivate") ?: ((it.getString("password") ?: "").isNotEmpty()),
                    it.getString("password") ?: "",
                    it.getString("category") ?: "General"
                )
            } ?: emptyList()
        }
        onDispose { reg.remove() }
    }

    // Auto-join deep link code if present
    LaunchedEffect(MainActivity.pendingInviteCode) {
        val c = MainActivity.pendingInviteCode
        if (!c.isNullOrBlank()) {
            MainActivity.pendingInviteCode = null
            text = c
            dialog = "join"
        }
    }

    val g = groups.find { it.id == openId }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        if (g == null) {
            Text("Study Squads", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 16.dp))
            Text("Stay accountable, learn together, and share screen time", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 14.dp))

            // Clean Segmented Tabs (No emoji soup)
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)).padding(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (squadTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier.weight(1f).clickable { squadTab = 0 }
                ) {
                    Box(Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Text("My Squads (${groups.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (squadTab == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (squadTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier.weight(1f).clickable { squadTab = 1 }
                ) {
                    Box(Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Text("Discover Squads", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (squadTab == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { dialog = "create"; text = ""; createIsPrivate = false; createPassword = ""; createCategory = "General" },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) { Text("+ Create Squad", fontSize = 13.sp) }

                OutlinedButton(
                    onClick = {
                        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                        val clip = cm?.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                        val autoCode = when {
                            clip.contains("join/") -> clip.substringAfter("join/").take(6).uppercase()
                            clip.contains("code=") -> clip.substringAfter("code=").take(6).uppercase()
                            clip.contains("group/") -> clip.substringAfter("group/").take(6).uppercase()
                            clip.trim().length == 6 && clip.trim().all { it.isLetterOrDigit() } -> clip.trim().uppercase()
                            else -> ""
                        }
                        text = autoCode
                        dialog = "join"
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) { Text("Join via Link/Code", fontSize = 13.sp) }
            }

            OutlinedButton(
                onClick = onRankings,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Study Leaderboard & Rankings")
            }

            if (msg.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    Text(msg, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                }
            }

            if (squadTab == 0) {
                // My Squads
                if (groups.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.40f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.Groups, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(10.dp))
                            Text("No study squads yet", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(4.dp))
                            Text("Create a squad above, enter an invite link, or browse Discover Squads to study together!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }

                    Text("Global Leaderboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
                    Leaderboard(uid, 50)
                } else {
                    Spacer(Modifier.height(8.dp))
                    groups.forEach { x ->
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp).clickable { setOpenId(x.id) }
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(46.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                    if (x.isPrivate) LockIcon(tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp)) else Icon(Icons.Rounded.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(x.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (x.isPrivate) Color(0xFFF59E0B).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f)
                                        ) {
                                            Text(if (x.isPrivate) "Private" else "Public", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (x.isPrivate) Color(0xFFD97706) else Color(0xFF059669), modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(Modifier.height(3.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${x.members.size} / ${x.capacity} members", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(8.dp))
                                        Text(x.category, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        if (x.id in unread) {
                                            Spacer(Modifier.width(8.dp))
                                            Text("New chat", color = Color(0xFFE5484D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            } else {
                // Discover Squads - ALL visible directly without searching!
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = discoverQuery,
                    onValueChange = { discoverQuery = it },
                    placeholder = { Text("Filter squads by name or tag...") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                val filtered = discoverSquads.filter {
                    discoverQuery.isBlank() || it.name.contains(discoverQuery, ignoreCase = true) || it.category.contains(discoverQuery, ignoreCase = true)
                }

                if (discoverSquads.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("Loading discoverable squads...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                } else if (filtered.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No squads found matching '$discoverQuery'", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                } else {
                    filtered.forEach { sq ->
                        val isMember = sq.members.contains(uid)
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(46.dp).clip(CircleShape).background(
                                        if (sq.isPrivate) Color(0xFFF59E0B).copy(alpha = 0.18f) else Color(0xFF10B981).copy(alpha = 0.18f)
                                    ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (sq.isPrivate) LockIcon(tint = Color(0xFFD97706), modifier = Modifier.size(22.dp)) else Icon(Icons.Rounded.Groups, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(22.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(sq.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (sq.isPrivate) Color(0xFFF59E0B).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f)
                                        ) {
                                            Text(if (sq.isPrivate) "Private" else "Public", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (sq.isPrivate) Color(0xFFD97706) else Color(0xFF059669), modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(Modifier.height(3.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${sq.members.size}/${sq.capacity} members", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(8.dp))
                                        Text(sq.category, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                if (isMember) {
                                    Button(
                                        onClick = { setOpenId(sq.id) },
                                        shape = RoundedCornerShape(16.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) { Text("Open", fontSize = 12.sp) }
                                } else {
                                    Button(
                                        onClick = {
                                            if (sq.members.size >= sq.capacity) {
                                                msg = "That squad is currently full!"
                                            } else if (sq.isPrivate) {
                                                targetJoinSquad = sq
                                                passcodeInput = ""
                                                dialog = "passcode"
                                            } else {
                                                db.collection("groups").document(sq.id).update("members", FieldValue.arrayUnion(uid))
                                                msg = "Joined squad '${sq.name}'!"
                                            }
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (sq.isPrivate) Color(0xFFD97706) else MaterialTheme.colorScheme.primary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) { Text(if (sq.isPrivate) "Unlock" else "Join", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        } else GroupDetail(g, uid, db, ctx, reqs[g.id] ?: emptyList(), g.id in unread, { onChat(g.id) }, { setOpenId(null) }, myLiveStudying, myLiveSecs, onStartStudy)
    }

    if (dialog == "create") {
        AlertDialog(
            onDismissRequest = { dialog = "" },
            title = { Text("Create Study Squad", fontWeight = FontWeight.Bold) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        text, { text = it.take(30) },
                        label = { Text("Squad name") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("Category / Focus:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("General", "Exam", "College", "Coding", "Languages", "High School").forEach { cat ->
                            val sel = createCategory == cat
                            FilterChip(
                                selected = sel,
                                onClick = { createCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("Squad Privacy (YPT style):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !createIsPrivate,
                            onClick = { createIsPrivate = false },
                            label = { Text("Public (Anyone can join)", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = createIsPrivate,
                            onClick = { createIsPrivate = true },
                            label = { Text("Private (Passcode)", fontSize = 11.sp) }
                        )
                    }
                    if (createIsPrivate) {
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            createPassword, { createPassword = it.take(20) },
                            label = { Text("Set Passcode / Password") },
                            placeholder = { Text("e.g. 1234") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            dismissButton = { TextButton(onClick = { dialog = "" }) { Text("Cancel") } },
            confirmButton = {
                TextButton(
                    onClick = {
                        val input = text.trim()
                        if (input.isNotBlank()) {
                            if (createIsPrivate && createPassword.trim().isBlank()) {
                                msg = "Please enter a passcode for your private squad!"
                            } else {
                                val code = (1..6).map { "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".random() }.joinToString("")
                                db.collection("groups").add(
                                    mapOf(
                                        "name" to input,
                                        "admin" to uid,
                                        "code" to code,
                                        "pool" to 120L,
                                        "capacity" to 10L,
                                        "joinMethod" to if (createIsPrivate) "password" else "code",
                                        "isPrivate" to createIsPrivate,
                                        "password" to createPassword.trim(),
                                        "category" to createCategory,
                                        "intro" to "",
                                        "rules" to "",
                                        "members" to listOf(uid)
                                    )
                                )
                                msg = "Squad '$input' created! Code: $code"
                                dialog = ""
                            }
                        }
                    }
                ) { Text("Create", fontWeight = FontWeight.Bold) }
            }
        )
    } else if (dialog == "join") {
        AlertDialog(
            onDismissRequest = { dialog = "" },
            title = { Text("Join via Link or Code", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        text, { text = it },
                        label = { Text("Paste invite link or 6-character code") },
                        placeholder = { Text("e.g. ABC123 or https://study-reward-ee2c2.web.app/join/...") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            dismissButton = { TextButton(onClick = { dialog = "" }) { Text("Cancel") } },
            confirmButton = {
                TextButton(
                    onClick = {
                        val raw = text.trim()
                        val code = when {
                            raw.contains("join/") -> raw.substringAfter("join/").take(6).uppercase()
                            raw.contains("code=") -> raw.substringAfter("code=").take(6).uppercase()
                            raw.contains("group/") -> raw.substringAfter("group/").take(6).uppercase()
                            else -> raw.take(6).uppercase()
                        }
                        if (code.isNotBlank()) {
                            db.collection("groups").whereEqualTo("code", code).limit(1).get().addOnSuccessListener { snap ->
                                val d = snap.documents.firstOrNull()
                                val n = (d?.get("members") as? List<*>)?.size ?: 0
                                if (d == null) {
                                    msg = "Squad with code '$code' not found"
                                } else if (n >= (d.getLong("capacity") ?: 10L)) {
                                    msg = "That squad is full"
                                } else if ((d.get("members") as? List<*>)?.contains(uid) == true) {
                                    msg = "You are already a member of this squad!"
                                    setOpenId(d.id)
                                    dialog = ""
                                } else {
                                    val isPriv = d.getBoolean("isPrivate") ?: ((d.getString("password") ?: "").isNotEmpty())
                                    val pwd = d.getString("password") ?: ""
                                    if (isPriv && pwd.isNotBlank()) {
                                        targetJoinSquad = Group(
                                            d.id, d.getString("name") ?: "", d.getString("admin") ?: "", code,
                                            (d.getLong("pool") ?: 120L).toInt(),
                                            (d.get("members") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                                            isPrivate = true, password = pwd
                                        )
                                        passcodeInput = ""
                                        dialog = "passcode"
                                    } else {
                                        d.reference.update("members", FieldValue.arrayUnion(uid))
                                        msg = "Joined squad '${d.getString("name")}'!"
                                        setOpenId(d.id)
                                        dialog = ""
                                    }
                                }
                            }
                        }
                    }
                ) { Text("Join", fontWeight = FontWeight.Bold) }
            }
        )
    } else if (dialog == "passcode") {
        AlertDialog(
            onDismissRequest = { dialog = ""; targetJoinSquad = null },
            title = { Text("Private Squad Passcode", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("This squad is private. Enter the passcode to join.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        passcodeInput, { passcodeInput = it },
                        label = { Text("Passcode / Password") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            dismissButton = { TextButton(onClick = { dialog = ""; targetJoinSquad = null }) { Text("Cancel") } },
            confirmButton = {
                TextButton(
                    onClick = {
                        val sq = targetJoinSquad
                        if (sq != null) {
                            if (passcodeInput.trim() == sq.password.trim()) {
                                db.collection("groups").document(sq.id).update("members", FieldValue.arrayUnion(uid))
                                msg = "Unlocked & joined squad '${sq.name}'!"
                                setOpenId(sq.id)
                                dialog = ""
                                targetJoinSquad = null
                            } else {
                                msg = "Incorrect passcode! Check with the squad leader."
                            }
                        }
                    }
                ) { Text("Unlock", fontWeight = FontWeight.Bold) }
            }
        )
    }
}

@Composable
fun rememberNow(): Long {
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { delay(1000); now = System.currentTimeMillis() } }
    return now
}

@Composable
fun rememberMembers(ids: List<String>): List<Member> {
    var list by remember { mutableStateOf(listOf<Member>()) }
    DisposableEffect(ids) {
        val reg = if (ids.isEmpty()) null else
            FirebaseFirestore.getInstance().collection("profiles").whereIn(FieldPath.documentId(), ids.take(10))
                .addSnapshotListener { snap, _ ->
                    val d = dateKey()
                    list = snap?.documents?.map {
                        val fresh = it.getString("day") == d
                        Member(it.id, it.getString("name") ?: "Friend",
                            if (fresh) it.getLong("studyToday") ?: 0L else 0L,
                            if (fresh) it.getLong("rewardsDone") ?: 0L else 0L,
                            fresh && (it.getBoolean("studying") ?: false), it.getLong("asOf") ?: 0L, it.getLong("studyTotal") ?: 0L)
                    } ?: emptyList()
                }
        onDispose { reg?.remove() }
    }
    return list
}

@Composable
fun MemberTile(name: String, secs: Long, studying: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val on = MaterialTheme.colorScheme.primary
    val col = if (studying) on else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier.padding(4.dp).clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, if (studying) on else Color.Transparent, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (studying) "🟢" else "⚪", fontSize = 30.sp)
        Text(name, color = col, maxLines = 1, fontSize = 14.sp)
        Text(clock(secs), color = col, fontSize = 13.sp)
    }
}

// Member tasks dialog to inspect what squad peers are working on today
@Composable
fun MemberTasksDialog(member: Member, onDismiss: () -> Unit, onCheer: () -> Unit) {
    var tasks by remember { mutableStateOf(listOf<TaskItem>()) }
    var loading by remember { mutableStateOf(true) }
    DisposableEffect(member.id) {
        val reg = FirebaseFirestore.getInstance().collection("users").document(member.id)
            .collection("tasks").whereEqualTo("date", dateKey()).addSnapshotListener { snap, _ ->
                tasks = snap?.documents?.mapNotNull { d ->
                    TaskItem(d.id, d.getString("title") ?: "", d.getString("subject") ?: "",
                        d.getBoolean("done") ?: false, d.getLong("order") ?: 0L,
                        d.getLong("dur") ?: 15L, d.getLong("worked") ?: 0L, d.getLong("doneAt") ?: 0L,
                        d.getLong("reward") ?: 0L, d.getString("kind") ?: "", d.getString("date") ?: "")
                } ?: emptyList()
                loading = false
            }
        onDispose { reg.remove() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                    Text(if (member.studying) "🟢" else "🐾", fontSize = 18.sp)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(member.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(if (member.studying) "Studying right now • ${clock(member.study)} today" else "${clock(member.study)} studied today",
                        fontSize = 12.sp, color = if (member.studying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 280.dp).verticalScroll(rememberScrollState())) {
                Text("Today's Tasks & Quests:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                if (loading) {
                    Text("Loading tasks...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else if (tasks.isEmpty()) {
                    Text("No public study tasks listed for today yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    tasks.forEach { t ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(if (t.done) "✅" else "⚪", fontSize = 14.sp)
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(t.title, fontSize = 13.sp, fontWeight = if (t.done) FontWeight.Normal else FontWeight.Medium,
                                    textDecoration = if (t.done) TextDecoration.LineThrough else null)
                                if (t.subject.isNotBlank()) Text(t.subject, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onCheer, shape = RoundedCornerShape(14.dp)) {
                Text("Cheer 🎉")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

// Studying members come first and glow in the accent colour; the others are greyed out
@Composable
fun MemberGrid(members: List<Member>, uid: String, now: Long, selfSecs: Long?, selfStudying: Boolean, onMemberClick: (Member) -> Unit = {}) {
    val sorted = members.sortedWith(compareByDescending<Member> { if (it.id == uid && selfSecs != null) selfStudying else isLive(it, now) }
        .thenByDescending { if (it.id == uid && selfSecs != null) (selfSecs ?: 0L) else liveStudy(it, now) })
    sorted.chunked(3).forEach { row ->
        Row(Modifier.fillMaxWidth()) {
            row.forEach { m ->
                val isSelf = m.id == uid && selfSecs != null
                val displayName = m.name + if (m.id == uid) " (you)" else ""
                val secs = if (isSelf) (selfSecs ?: 0L) else liveStudy(m, now)
                val studying = if (isSelf) selfStudying else isLive(m, now)
                MemberTile(displayName, secs, studying, Modifier.weight(1f)) { onMemberClick(m) }
            }
            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

@Composable
fun Leaderboard(uid: String, limit: Int) {
    var rows by remember { mutableStateOf(listOf<Member>()) }
    val now = rememberNow()
    DisposableEffect(Unit) {
        val reg = FirebaseFirestore.getInstance().collection("rank").document("all").collection("entries")
            .orderBy("secs", Query.Direction.DESCENDING).limit(limit.toLong())
            .addSnapshotListener { snap, _ ->
                val d = dateKey()
                rows = snap?.documents?.map {
                    val fresh = it.getString("day") == d
                    Member(it.id, it.getString("name") ?: "Friend", 0L, 0L, false, 0L, it.getLong("secs") ?: 0L)
                } ?: emptyList()
            }
        onDispose { reg.remove() }
    }
    if (rows.isEmpty()) Text("No one on the leaderboard yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    rows.forEachIndexed { i, m ->
        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${i + 1}", Modifier.width(28.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(m.name + if (m.id == uid) " (you)" else "", Modifier.weight(1f),
                fontWeight = if (m.id == uid) FontWeight.Bold else FontWeight.Normal)
            if (isLive(m, now)) Text("🟢 ", color = MaterialTheme.colorScheme.primary)
            Text(hm(liveTotal(m, now)))
        }
    }
}

@Composable
fun FocusView(
    name: String, color: Long, secs: Int, paused: Boolean, todayTotal: Long, uid: String, groups: List<Group>,
    onPause: () -> Unit, onEnd: () -> Unit, breakLeft: Int, awaiting: Boolean, roundLeft: Int, onResumeBreak: () -> Unit,
    mascotType: MascotType = MascotType.MOCHI, mascotName: String = "Mochi"
) {
    var gi by remember { mutableStateOf(0) }
    val g = groups.getOrNull(gi)
    val members = rememberMembers(g?.members ?: emptyList())
    val now = rememberNow()
    val card = MaterialTheme.colorScheme.surfaceVariant
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(14.dp).clip(CircleShape).background(Color(color)))
            Spacer(Modifier.width(10.dp))
            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }
        if (breakLeft > 0 || awaiting) {
            Column(Modifier.fillMaxWidth().padding(bottom = 8.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), RoundedCornerShape(20.dp)).padding(20.dp)) {
                Text(if (awaiting) "Break's over ⏰" else "Break time ☕", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (!awaiting) Text(mmss(breakLeft.toLong()), fontSize = 44.sp, fontWeight = FontWeight.Bold)
                Text(if (awaiting) "Ready to go again? Your study timer is still paused." else "Stretch, drink water, rest your eyes. Study time is not counting.",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 6.dp))
                Button(onClick = onResumeBreak) { Text(if (awaiting) "Continue studying" else "Skip break") }
            }
        }
        if (roundLeft >= 0 && breakLeft == 0 && !awaiting)
            Text("🍅 Pomodoro • ${mmss(roundLeft.toLong())} left in this round", color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp))
        Column(Modifier.fillMaxWidth().padding(vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            MascotCanvas(
                type = mascotType,
                mood = if (paused) MascotMood.SLEEPING else MascotMood.FOCUSED,
                modifier = Modifier.size(88.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (paused) "$mascotName is taking a sweet pause with you 💤🌸" else "$mascotName is cheering for you! You're doing amazing! (｡♥‿♥｡) ✨",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(clock(secs.toLong()), style = androidx.compose.ui.text.TextStyle(fontFeatureSettings = "tnum"), fontSize = 64.sp, fontWeight = FontWeight.Bold)
            Text("Self-reported study time", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(68.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary).clickable { onPause() },
                    contentAlignment = Alignment.Center) {
                    Text(if (paused) "▶" else "❚❚", fontSize = 22.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(Modifier.width(20.dp))
                OutlinedButton(onClick = onEnd, shape = RoundedCornerShape(50)) { Text("End & save") }
            }
        }
        Spacer(Modifier.height(8.dp))

        // Ambient noise during focus
        StudySoundscapeCard(Modifier.padding(vertical = 4.dp))

        Spacer(Modifier.height(12.dp))
        Column(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)).padding(16.dp)) {
            if (g != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(g.name, Modifier.weight(1f), fontSize = 20.sp, fontWeight = FontWeight.Medium)
                    if (groups.size > 1) Text("⇄", Modifier.clickable { gi = (gi + 1) % groups.size }.padding(8.dp), fontSize = 20.sp)
                }
                val n = members.count { if (it.id == uid) !paused else isLive(it, now) }
                Text("$n studying", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                MemberGrid(members, uid, now, todayTotal + secs, !paused)
            } else {
                Text("Leaderboard (everyone)", fontSize = 20.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                Leaderboard(uid, 10)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun GroupDetail(g: Group, uid: String, db: FirebaseFirestore, ctx: Context, reqs: List<Req>, hasNew: Boolean,
                onChat: () -> Unit, onBack: () -> Unit,
                myLiveStudying: Boolean = false, myLiveSecs: Long = 0L, onStartStudy: () -> Unit = {}) {
    val members = rememberMembers(g.members)
    val now = rememberNow()
    val ref = db.collection("groups").document(g.id)
    val soft = MaterialTheme.colorScheme.surfaceVariant
    val accent = MaterialTheme.colorScheme.primary
    val isLeader = g.admin == uid
    var msg by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }
    var aboutOpen by remember { mutableStateOf(false) }
    var dlg by remember { mutableStateOf("") }
    var t1 by remember { mutableStateOf("") }
    var t2 by remember { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf(false) }
    var joinReqs by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    val accepted = acceptedReqs(g, reqs)
    val left = maxOf(0L, g.pool - accepted.sumOf { it.mins })
    val myPending = reqs.any { it.fromUid == uid && !isApproved(it, g) && !isRejected(it, g) }

    DisposableEffect(g.id, isLeader) {
        val reg = if (isLeader) ref.collection("joinRequests").addSnapshotListener { snap, _ ->
            joinReqs = snap?.documents?.map { it.id to (it.getString("name") ?: "Friend") } ?: emptyList()
        } else null
        onDispose { reg?.remove() }
    }
    fun ask(mins: Long) {
        when {
            g.members.size < 2 -> msg = "Invite a friend first. Others have to approve your request."
            myPending -> msg = "You already have a request waiting for votes."
            mins > left -> msg = "Only $left min left in the shared pool today."
            else -> {
                ref.collection("requests").add(mapOf<String, Any>("fromUid" to uid, "fromName" to myName(),
                    "mins" to mins, "at" to System.currentTimeMillis(),
                    "yes" to emptyList<String>(), "no" to emptyList<String>()))
                msg = "Request sent. It needs ${needed(g)} yes vote(s) from the others."
            }
        }
    }
    fun vote(r: Req, yes: Boolean) {
        ref.collection("requests").document(r.id).update(if (yes) "yes" else "no", FieldValue.arrayUnion(uid))
    }

    if (showSettings) {
        // ---------- settings (opened from the gear in the corner) ----------
        TextButton(onClick = { showSettings = false }) {
            Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(16.dp))
            Text("  ${g.name}")
        }
        Text("Group settings", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        if (isLeader) {
            Text("You are the leader of this group.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Stepper("Shared pool per day", g.pool, "min", 30, 480, 30) { ref.update("pool", it.toLong()) }
            Stepper("Capacity", g.capacity, "people", maxOf(2, g.members.size), 10, 1) { ref.update("capacity", it.toLong()) }
            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Leader approves new members")
                    Text("Off: anyone with the code joins straight away", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(g.joinMethod == "approval", { ref.update("joinMethod", if (it) "approval" else "code") })
            }
            Row(Modifier.padding(top = 8.dp)) {
                OutlinedButton(onClick = { t1 = g.name; dlg = "name" }) { Text("Rename") }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = { t1 = g.intro; t2 = g.rules; dlg = "about" }) { Text("Intro and rules") }
            }
            val canNudge = System.currentTimeMillis() - g.nudgeAt > 3_600_000
            Button(onClick = {
                ref.update(mapOf<String, Any>("nudgeAt" to System.currentTimeMillis(), "nudgeBy" to uid))
                msg = "Nudge sent to everyone 📣"
            }, enabled = canNudge, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text(if (canNudge) "📣 Nudge everyone to study" else "Nudged recently (once an hour)")
            }
            if (msg.isNotBlank()) Text(msg, color = accent, modifier = Modifier.padding(top = 6.dp))
            if (joinReqs.isNotEmpty()) {
                Text("Join requests (${joinReqs.size})", fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 16.dp))
                joinReqs.forEach { (id, name) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(name, Modifier.weight(1f))
                        TextButton(onClick = {
                            if (g.members.size >= g.capacity) msg = "The group is full. Raise the capacity first."
                            else {
                                ref.update("members", FieldValue.arrayUnion(id))
                                ref.collection("joinRequests").document(id).delete()
                            }
                        }) { Text("Approve") }
                        TextButton(onClick = { ref.collection("joinRequests").document(id).delete() }) { Text("Decline") }
                    }
                }
            }
            Text("Members", fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 16.dp))
            members.filter { it.id != uid }.forEach { m ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(m.name, Modifier.weight(1f))
                    Text("Remove", Modifier.clickable { ref.update("members", FieldValue.arrayRemove(m.id)) }.padding(8.dp),
                        color = Color.Red, fontSize = 13.sp)
                }
            }
            TextButton(onClick = { confirmDelete = true }, modifier = Modifier.padding(top = 8.dp)) { Text("Delete this group", color = Color.Red) }
        } else {
            Text("Shared pool per day: ${g.pool} min (set by the leader)", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            Text("Capacity: ${g.capacity} people", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = { ref.update("members", FieldValue.arrayRemove(uid)); onBack() }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Leave group", color = Color.Red)
            }
        }
        Text("You can receive at most $MAX_GIFT_MINS min per day from all groups. Study and reward tasks are never capped.",
            color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp))
        Spacer(Modifier.height(24.dp))
    } else {
        // ---------- main group page ----------
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("  Groups")
            }
            Spacer(Modifier.weight(1f))
            Box(Modifier.size(40.dp).clip(CircleShape).background(soft).clickable { showSettings = true }, contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Settings, contentDescription = "Group settings", modifier = Modifier.size(22.dp))
                if (isLeader && joinReqs.isNotEmpty())
                    Box(Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFE5484D)).align(Alignment.TopEnd))
            }
        }
        Text(g.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("${g.members.size} members • ${members.count { isLive(it, now) }} studying now", fontSize = 13.sp, color = accent)

        // Squad invite code & quick-share card with live working clickable link
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            Column(Modifier.padding(14.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("SQUAD CODE: " + g.code, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 2.sp)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "https://tinyurl.com/studyreward-invite?code=" + g.code,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                val link = "https://tinyurl.com/studyreward-invite?code=" + g.code
                                val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Squad Link", link)
                                cm?.setPrimaryClip(clip)
                                Toast.makeText(ctx, "Clickable link copied!", Toast.LENGTH_SHORT).show()
                                try {
                                    val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                    ctx.startActivity(viewIntent)
                                } catch (_: Exception) {}
                            }
                        )
                    }
                    FilledTonalButton(
                        onClick = {
                            val link = "https://tinyurl.com/studyreward-invite?code=" + g.code
                            val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Squad Link", link)
                            cm?.setPrimaryClip(clip)
                            Toast.makeText(ctx, "Clickable link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Copy Link") }
                    Spacer(Modifier.width(6.dp))
                    Button(
                        onClick = {
                            val link = "https://tinyurl.com/studyreward-invite?code=" + g.code
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Join my study squad '" + g.name + "' on StudyReward!\n\nTap this link to join directly:\n" + link + "\n\n(Squad Code: " + g.code + ")"
                                )
                                type = "text/plain"
                            }
                            ctx.startActivity(Intent.createChooser(sendIntent, "Share Study Squad"))
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Share") }
                }
                if (isLeader) {
                    TextButton(
                        onClick = {
                            val newCode = (1..6).map { "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".random() }.joinToString("")
                            ref.update("code", newCode)
                            Toast.makeText(ctx, "Generated new squad code: $newCode", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Regenerate Squad Code", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        if (g.intro.isNotBlank() || g.rules.isNotBlank()) {
            Column(Modifier.fillMaxWidth().padding(top = 12.dp).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                .clickable { aboutOpen = !aboutOpen }.padding(14.dp)) {
                Text("About this group " + if (aboutOpen) "▲" else "▼", fontWeight = FontWeight.Medium)
                if (aboutOpen) {
                    if (g.intro.isNotBlank()) Text(g.intro, modifier = Modifier.padding(top = 8.dp))
                    if (g.rules.isNotBlank()) {
                        Text("Rules", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
                        Text(g.rules)
                    }
                } else Text(g.intro.ifBlank { g.rules }.take(80), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }
        }

        Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp)).padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text("Code  ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(g.code, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(soft).clickable {
                val i = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Join my study squad '" + g.name + "' on StudyReward!\n\nTap this link to join directly:\nhttps://tinyurl.com/studyreward-invite?code=" + g.code + "\n\n(Squad Code: " + g.code + ")"
                    )
                }
                ctx.startActivity(Intent.createChooser(i, "Share squad invite link"))
            }, contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Share, contentDescription = "Share code", modifier = Modifier.size(22.dp)) }
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            .clickable { onChat() }.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Chat, contentDescription = null, modifier = Modifier.size(22.dp), tint = accent)
            Text("  Group chat", Modifier.weight(1f), fontWeight = FontWeight.Medium)
            if (hasNew) Text("New", color = Color.White, fontSize = 11.sp,
                modifier = Modifier.background(Color(0xFFE5484D), RoundedCornerShape(10.dp)).padding(horizontal = 8.dp, vertical = 2.dp))
        }

        // 🎧 Study-With-Me Live Room & Squad Target
        var showLiveRoom by remember { mutableStateOf(false) }
        val effectiveStudyingCount = members.count { isLive(it, now) || (it.id == uid && myLiveStudying) }

        if (showLiveRoom) {
            LiveStudyRoomDialog(
                group = g,
                members = members,
                uid = uid,
                now = now,
                myLiveStudying = myLiveStudying,
                myLiveSecs = myLiveSecs,
                onStartStudy = onStartStudy,
                onCheer = { msgText ->
                    db.collection("groups").document(g.id).collection("messages").add(mapOf<String, Any>(
                        "uid" to uid,
                        "name" to myName(),
                        "text" to msgText,
                        "at" to System.currentTimeMillis()
                    ))
                    Toast.makeText(ctx, "Message sent to squad chat! 💬", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showLiveRoom = false }
            )
        }

        Surface(
            shape = RoundedCornerShape(22.dp),
            color = if (effectiveStudyingCount > 0) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (effectiveStudyingCount > 0) Color(0xFF81C784) else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp).clickable { showLiveRoom = true }
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(if (effectiveStudyingCount > 0) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (effectiveStudyingCount > 0) "🎧" else "🛋️", fontSize = 22.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Study Together Live Call 📹✨", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        if (effectiveStudyingCount > 0) {
                            Spacer(Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF2E7D32)) {
                                Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        if (effectiveStudyingCount > 0) "🟢 $effectiveStudyingCount squad member(s) live focusing now!"
                        else "Tap to enter Zoom-style video & mic study call with squad!",
                        fontSize = 12.sp,
                        color = if (effectiveStudyingCount > 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text("Join Call 📹", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp))
                }
            }
        }

        // 🎯 Squad Daily Goal Card
        val squadTotalSecs = members.sumOf { liveStudy(it, now) }
        val squadTargetSecs = maxOf(18000L, g.pool * 60L) // 5 hours or pool
        val squadDone = squadTotalSecs >= squadTargetSecs
        val squadPct = (squadTotalSecs.toFloat() / squadTargetSecs).coerceIn(0f, 1f)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (squadDone) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (squadDone) Color(0xFF2E7D32).copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("🎯 Squad Daily Target", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            if (squadDone) "🎉 Squad goal accomplished! Great teamwork!"
                            else "${squadTotalSecs / 3600}h ${(squadTotalSecs % 3600) / 60}m / ${squadTargetSecs / 3600}h studied together today",
                            fontSize = 12.sp,
                            color = if (squadDone) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("${(squadPct * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                        color = if (squadDone) Color(0xFF2E7D32) else accent)
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { squadPct },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (squadDone) Color(0xFF2E7D32) else accent,
                    trackColor = soft
                )
            }
        }

        var inspectingMember by remember { mutableStateOf<Member?>(null) }
        if (inspectingMember != null) {
            MemberTasksDialog(
                member = inspectingMember!!,
                onDismiss = { inspectingMember = null },
                onCheer = {
                    db.collection("groups").document(g.id).collection("messages").add(mapOf<String, Any>(
                        "uid" to uid,
                        "name" to myName(),
                        "text" to "🎉 Sent a cheer to ${inspectingMember!!.name}! Keep up the great studying!",
                        "at" to System.currentTimeMillis()
                    ))
                    Toast.makeText(ctx, "Cheer sent to squad chat! 🎉", Toast.LENGTH_SHORT).show()
                    inspectingMember = null
                }
            )
        }

        Text("Squad Members (tap to view tasks)", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp, bottom = 4.dp))
        MemberGrid(members, uid, now, myLiveSecs, myLiveStudying) { inspectingMember = it }

        Text("Shared screen time pool", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 20.dp))
        Text("$left of ${g.pool} min left today. A request needs yes votes from at least half of the other members (${needed(g)} now).",
            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Ask for", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 10.dp))
            listOf(5L, 10L, 15L, 30L).forEach { n ->
                Text("${n}m", Modifier.padding(end = 8.dp).clip(RoundedCornerShape(16.dp)).background(soft)
                    .clickable { ask(n) }.padding(horizontal = 14.dp, vertical = 8.dp))
            }
        }
        if (msg.isNotBlank()) Text(msg, color = accent, modifier = Modifier.padding(top = 8.dp))
        if (reqs.isNotEmpty()) Text("Requests today", fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 16.dp))
        reqs.sortedByDescending { it.at }.forEach { r ->
            val ok = isApproved(r, g)
            val no = isRejected(r, g)
            val inPool = accepted.any { it.id == r.id }
            val status = when {
                ok && inPool -> "Approved ?"
                ok -> "Approved, but the pool ran out"
                no -> "Rejected"
                else -> "Waiting: ${yesVotes(r, g)} of ${needed(g)} yes votes"
            }
            Column(Modifier.fillMaxWidth().padding(top = 8.dp).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp)).padding(12.dp)) {
                Text("${if (r.fromUid == uid) "You" else r.fromName} asked for ${r.mins} min")
                Text(status, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val voted = uid in r.yes || uid in r.no
                if (r.fromUid != uid && !ok && !no && !voted && uid in g.members) {
                    Row(Modifier.padding(top = 8.dp)) {
                        Button(onClick = { vote(r, true) }) { Text("Yes") }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(onClick = { vote(r, false) }) { Text("No") }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    if (dlg == "name") AlertDialog(onDismissRequest = { dlg = "" }, title = { Text("Group name") },
        text = { OutlinedTextField(t1, { t1 = it.take(30) }, singleLine = true) },
        confirmButton = { TextButton(onClick = { if (t1.isNotBlank()) ref.update("name", t1.trim()); dlg = "" }) { Text("Save") } },
        dismissButton = { TextButton(onClick = { dlg = "" }) { Text("Cancel") } })
    if (dlg == "about") AlertDialog(onDismissRequest = { dlg = "" }, title = { Text("Intro and rules") },
        text = {
            Column {
                OutlinedTextField(t1, { t1 = it.take(300) }, label = { Text("Intro") })
                OutlinedTextField(t2, { t2 = it.take(500) }, label = { Text("Rules (one per line)") }, modifier = Modifier.padding(top = 8.dp))
            }
        },
        confirmButton = { TextButton(onClick = { ref.update(mapOf<String, Any>("intro" to t1.trim(), "rules" to t2.trim())); dlg = "" }) { Text("Save") } },
        dismissButton = { TextButton(onClick = { dlg = "" }) { Text("Cancel") } })
    if (confirmDelete) AlertDialog(onDismissRequest = { confirmDelete = false }, title = { Text("Delete ${g.name}?") },
        text = { Text("Everyone will be removed from the group. This cannot be undone.") },
        confirmButton = { TextButton(onClick = { confirmDelete = false; ref.delete(); onBack() }) { Text("Delete") } },
        dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } })
}

// ---------- Statistics, study logs, settings ----------

data class FocusPrefs(val on: Boolean, val every: Int, val len: Int, val pomo: Boolean, val focus: Int, val short: Int, val long: Int)
data class EventItem(val id: String, val name: String, val date: String, val start: String, val color: Long, val subjects: List<String>)
data class Sess(val id: String, val subject: String, val secs: Long, val at: Long)

fun dayStart(back: Int): Long = Calendar.getInstance().apply { timeInMillis = startOfDay(); add(Calendar.DAY_OF_MONTH, -back) }.timeInMillis
fun dayEnd(back: Int): Long = dayStart(back - 1)
fun tfmt(ms: Long): String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(ms))
fun dfmt(back: Int): String = when (back) {
    0 -> "Today"
    1 -> "Yesterday"
    else -> SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(Date(dayStart(back)))
}

// Splits a study session across the hours of the day it covers
fun spread(start: Long, end: Long, hours: LongArray) {
    var t = start
    while (t < end) {
        val c = Calendar.getInstance().apply { timeInMillis = t }
        val h = c.get(Calendar.HOUR_OF_DAY)
        c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
        c.add(Calendar.HOUR_OF_DAY, 1)
        val e = minOf(end, c.timeInMillis)
        hours[h] += (e - t) / 1000
        t = e
    }
}

@Composable
fun rememberSessions(uid: String): List<Sess> {
    var list by remember { mutableStateOf(listOf<Sess>()) }
    DisposableEffect(uid) {
        val reg = FirebaseFirestore.getInstance().collection("users").document(uid).collection("sessions")
            .whereGreaterThanOrEqualTo("at", dayStart(29)).addSnapshotListener { snap, _ ->
                list = (snap?.documents?.map {
                    Sess(it.id, it.getString("subject") ?: "", it.getLong("seconds") ?: 0L, it.getLong("at") ?: 0L)
                } ?: emptyList()).sortedBy { it.at }
            }
        onDispose { reg.remove() }
    }
    return list
}

@Composable
fun DayNav(back: Int, onChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        TextButton(onClick = { onChange(minOf(29, back + 1)) }, enabled = back < 29) { Text("›", fontSize = 22.sp) }
        Text(dfmt(back), fontSize = 18.sp, fontWeight = FontWeight.Medium)
        TextButton(onClick = { onChange(maxOf(0, back - 1)) }, enabled = back > 0) { Text("›", fontSize = 22.sp) }
    }
}


fun exportStudyData(ctx: Context, name: String, sessions: List<Sess>, tasks: List<TaskItem>, subjects: List<Subject>) {
    try {
        val totalSecs = sessions.sumOf { it.secs }
        val sb = StringBuilder()
        sb.append("=== StudyReward Data Backup ===\n")
        sb.append("Student: ${name.ifBlank { "Student" }}\n")
        sb.append("Export Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}\n")
        sb.append("Total Study Recorded: ${totalSecs / 3600}h ${(totalSecs % 3600) / 60}m\n\n")
        sb.append("--- Subjects (${subjects.size}) ---\n")
        subjects.forEach { sb.append("• ${it.name}\n") }
        sb.append("\n--- Study Sessions (${sessions.size}) ---\n")
        sessions.sortedByDescending { it.at }.take(100).forEach { s ->
            sb.append("${SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault()).format(Date(s.at))} | ${s.subject} | ${s.secs / 60} mins\n")
        }
        sb.append("\n--- Tasks Completed (${tasks.count { it.done }}) ---\n")
        tasks.filter { it.done }.forEach { t ->
            sb.append("✓ [${if (t.kind == "reward") "Quest" else t.subject}] ${t.title}\n")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "StudyReward Data Backup - $name")
            putExtra(Intent.EXTRA_TEXT, sb.toString())
        }
        ctx.startActivity(Intent.createChooser(intent, "Export Study Data"))
    } catch (e: Exception) {
        Toast.makeText(ctx, "Could not export: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

data class AchievementBadge(
    val id: String,
    val title: String,
    val desc: String,
    val icon: String,
    val unlocked: Boolean,
    val progress: Float,
    val progressText: String
)

@Composable
fun BadgesScreen(uid: String, streak: Int, tasks: List<TaskItem>, onBack: () -> Unit) {
    val all = rememberSessions(uid)
    var filter by remember { mutableStateOf("all") } // all, unlocked, locked

    // 1. Early Bird (study started before 8 AM)
    val earlyBird = all.any {
        val cal = Calendar.getInstance().apply { timeInMillis = it.at - it.secs * 1000 }
        cal.get(Calendar.HOUR_OF_DAY) < 8
    }

    // 2. Night Owl (study finished after 10 PM)
    val nightOwl = all.any {
        val cal = Calendar.getInstance().apply { timeInMillis = it.at }
        cal.get(Calendar.HOUR_OF_DAY) >= 22
    }

    // 3. Streak Starter (3-day streak)
    val streak3 = streak >= 3

    // 4. Fire Focus (7-day streak)
    val streak7 = streak >= 7

    // 5. Unstoppable Legend (14-day streak)
    val streak14 = streak >= 14

    // 6. Deep Work Zen (90m+ single session)
    val maxSecs = all.maxOfOrNull { it.secs } ?: 0L
    val deepWork = maxSecs >= 5400L

    // 7. Quest Champion (5 completed daily quests)
    val questDone = tasks.count { it.kind == "reward" && it.done }
    val questChamp = questDone >= 5

    // 8. Polymath (studied 3+ distinct subjects)
    val distinctSubj = all.map { it.subject }.filter { it.isNotBlank() }.distinct().size
    val polymath = distinctSubj >= 3

    // 9. Study Marathon (4+ hours in a single day)
    val maxDaySecs = (0..29).maxOfOrNull { b -> all.filter { it.at >= dayStart(b) && it.at < dayEnd(b) }.sumOf { it.secs } } ?: 0L
    val marathon = maxDaySecs >= 14400L

    // 10. Century Club (100+ lifetime focus hours)
    val totalLifetimeSecs = all.sumOf { it.secs }
    val century = totalLifetimeSecs >= 360000L

    val badges = listOf(
        AchievementBadge("early", "Early Bird", "Completed a focus session before 8:00 AM", "🌅", earlyBird, if (earlyBird) 1f else 0f, if (earlyBird) "Unlocked ✨" else "Study before 8 AM"),
        AchievementBadge("night", "Night Owl", "Completed a focus session after 10:00 PM", "🦉", nightOwl, if (nightOwl) 1f else 0f, if (nightOwl) "Unlocked ✨" else "Study after 10 PM"),
        AchievementBadge("streak3", "Streak Starter", "Maintained a 3-day study streak", "🔥", streak3, (streak / 3f).coerceIn(0f, 1f), "$streak / 3 days"),
        AchievementBadge("streak7", "Fire Focus", "Reached an unbroken 7-day streak", "⚡", streak7, (streak / 7f).coerceIn(0f, 1f), "$streak / 7 days"),
        AchievementBadge("streak14", "Unstoppable Legend", "Reached an extraordinary 14-day streak", "👑", streak14, (streak / 14f).coerceIn(0f, 1f), "$streak / 14 days"),
        AchievementBadge("deep", "Deep Work Zen", "Completed a continuous 90+ minute focus session", "🧘", deepWork, (maxSecs / 5400f).coerceIn(0f, 1f), "${maxSecs / 60} / 90 mins"),
        AchievementBadge("quest", "Quest Champion", "Finished 5 daily physical or wellness quests", "🍬", questChamp, (questDone / 5f).coerceIn(0f, 1f), "$questDone / 5 quests"),
        AchievementBadge("poly", "Polymath Mind", "Studied 3 or more different subjects", "📚", polymath, (distinctSubj / 3f).coerceIn(0f, 1f), "$distinctSubj / 3 subjects"),
        AchievementBadge("mara", "Study Marathon", "Recorded 4+ hours of focus in a single day", "🏃", marathon, (maxDaySecs / 14400f).coerceIn(0f, 1f), "${maxDaySecs / 3600}h / 4h"),
        AchievementBadge("cent", "Century Club", "Surpassed 100 total lifetime study hours", "💎", century, (totalLifetimeSecs / 360000f).coerceIn(0f, 1f), "${totalLifetimeSecs / 3600}h / 100h")
    )

    val unlockedCount = badges.count { it.unlocked }
    val levelTitle = when {
        unlockedCount >= 9 -> "Level 4 Focus Legend 👑"
        unlockedCount >= 6 -> "Level 3 Deep Work Master ⚡"
        unlockedCount >= 3 -> "Level 2 Dedicated Scholar 📖"
        else -> "Level 1 Focus Apprentice 🌟"
    }

    val filteredBadges = when (filter) {
        "unlocked" -> badges.filter { it.unlocked }
        "locked" -> badges.filter { !it.unlocked }
        else -> badges
    }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("🏆 Badges & Achievements", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text("Unlock prestigious honors as your study discipline grows!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 12.dp))

        // Level Showcase Card
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(levelTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                    Text("$unlockedCount / ${badges.size} Unlocked", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { unlockedCount.toFloat() / badges.size },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        // Filter Pills
        Row(Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("all" to "All (${badges.size})", "unlocked" to "Unlocked 🌟 ($unlockedCount)", "locked" to "In Progress ⏳ (${badges.size - unlockedCount})").forEach { (k, lbl) ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (filter == k) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f).clickable { filter = k }
                ) {
                    Text(lbl, textAlign = TextAlign.Center, fontSize = 11.sp, fontWeight = if (filter == k) FontWeight.Bold else FontWeight.Normal,
                        color = if (filter == k) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp))
                }
            }
        }

        // Badges Grid / Cards
        filteredBadges.forEach { b ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (b.unlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (b.unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(48.dp).clip(CircleShape).background(
                            if (b.unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (b.unlocked) b.icon else "🔒", fontSize = 22.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(b.title, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                                color = if (b.unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
                            if (b.unlocked) {
                                Spacer(Modifier.width(6.dp))
                                Text("✨", fontSize = 12.sp)
                            }
                        }
                        Text(b.desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { b.progress },
                                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = if (b.unlocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(b.progressText, fontSize = 10.sp, fontWeight = FontWeight.Medium,
                                color = if (b.unlocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun StatsScreen(uid: String, subjects: List<Subject>, onBack: () -> Unit) {
    val all = rememberSessions(uid)
    var statsTab by remember { mutableStateOf(0) } // 0=Day, 1=Week, 2=Month, 3=Report Card
    var back by remember { mutableStateOf(0) }
    val ctx = LocalContext.current

    val day = all.filter { it.at >= dayStart(back) && it.at < dayEnd(back) }
    val total = day.sumOf { it.secs }
    val first = day.minOfOrNull { it.at - it.secs * 1000 }
    val last = day.maxOfOrNull { it.at }
    val hours = LongArray(24)
    day.forEach { spread(it.at - it.secs * 1000, it.at, hours) }
    val maxH = maxOf(1L, hours.maxOrNull() ?: 1L)
    val perSubject = day.groupBy { it.subject.ifBlank { "No subject" } }
        .mapValues { e -> e.value.sumOf { it.secs } }.entries.sortedByDescending { it.value }

    // 7-day stats
    val per7 = (0..6).map { b -> all.filter { it.at >= dayStart(b) && it.at < dayEnd(b) }.sumOf { it.secs } }
    val weekTotal = per7.sum()
    val maxW = maxOf(1L, per7.maxOrNull() ?: 1L)
    val weekSubjects = all.filter { it.at >= dayStart(6) }.groupBy { it.subject.ifBlank { "No subject" } }
        .mapValues { e -> e.value.sumOf { it.secs } }.entries.sortedByDescending { it.value }

    // 30-day stats
    val per30 = (0..29).map { b -> all.filter { it.at >= dayStart(b) && it.at < dayEnd(b) }.sumOf { it.secs } }
    val monthTotal = per30.sum()
    val maxD = maxOf(1L, per30.take(14).maxOrNull() ?: 1L)
    val accent = MaterialTheme.colorScheme.primary
    val soft = MaterialTheme.colorScheme.surfaceVariant

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("📊 Statistics & Insights", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Track your focus progress, consistency, and weekly report card.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 12.dp))

        // Period Switcher (Day, Week, Month, Report Card)
        Row(Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("📅 Day", "📊 Week", "🗓️ Month", "📝 Report Card").forEachIndexed { idx, title ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (statsTab == idx) accent else soft,
                    modifier = Modifier.weight(1f).clickable { statsTab = idx }
                ) {
                    Text(
                        title,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        fontWeight = if (statsTab == idx) FontWeight.Bold else FontWeight.Medium,
                        color = if (statsTab == idx) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                    )
                }
            }
        }

        when (statsTab) {
            0 -> { // DAY VIEW
                DayNav(back) { back = it }
                Row {
                    StatCard("Total", clock(total))
                    StatCard("Longest session", clock(day.maxOfOrNull { it.secs } ?: 0L))
                }
                Row {
                    StatCard("Started", first?.let { tfmt(it) } ?: "—")
                    StatCard("Finished", last?.let { tfmt(it) } ?: "—")
                    StatCard("Sessions", "${day.size}")
                }

                Text("Subjects", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 20.dp, bottom = 6.dp))
                if (perSubject.isEmpty()) Text("Nothing studied on this day.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                perSubject.forEach { (name, secs) ->
                    val color = Color(subjects.find { it.name == name }?.color ?: 0xFF888888)
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text(name, Modifier.weight(1f))
                        Text("${clock(secs)} • ${if (total > 0) secs * 100 / total else 0}%", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                    Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(soft)) {
                        Box(Modifier.fillMaxWidth(if (total > 0) (secs.toFloat() / total).coerceIn(0.02f, 1f) else 0.02f)
                            .fillMaxHeight().background(color))
                    }
                }

                Text("Hours of the day", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
                Row(Modifier.fillMaxWidth().height(90.dp), verticalAlignment = Alignment.Bottom) {
                    for (h in 0..23) {
                        Box(Modifier.weight(1f).padding(horizontal = 1.dp)
                            .fillMaxHeight(if (hours[h] > 0) (hours[h].toFloat() / maxH).coerceIn(0.05f, 1f) else 0.03f)
                            .background(if (hours[h] > 0) accent else soft, RoundedCornerShape(3.dp)))
                    }
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    listOf("0", "6", "12", "18", "23").forEach { Text(it, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
            1 -> { // WEEK VIEW
                Text("Last 7 Days Focus", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                Row {
                    StatCard("Week Total", "${weekTotal / 3600}h ${(weekTotal % 3600) / 60}m")
                    StatCard("Daily Avg", "${weekTotal / (7 * 3600)}h ${(weekTotal / 7 % 3600) / 60}m")
                    StatCard("Active Days", "${per7.count { it > 0 }}/7")
                }

                Spacer(Modifier.height(16.dp))
                Text("Daily Trend (Last 7 Days)", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    for (b in 6 downTo 0) {
                        val v = per7[b]
                        Column(Modifier.weight(1f).clickable { back = b; statsTab = 0 }, horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.height(100.dp).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                                Box(
                                    Modifier.fillMaxWidth(0.6f).fillMaxHeight(if (v > 0) (v.toFloat() / maxW).coerceIn(0.06f, 1f) else 0.03f)
                                        .background(if (v > 0) accent else soft, RoundedCornerShape(4.dp))
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(SimpleDateFormat("EEE", Locale.getDefault()).format(Date(dayStart(b))), fontSize = 11.sp, fontWeight = FontWeight.Medium,
                                color = if (b == 0) accent else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Weekly Subject Split", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
                if (weekSubjects.isEmpty()) Text("No study logs recorded this week.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                weekSubjects.forEach { (name, secs) ->
                    val color = Color(subjects.find { it.name == name }?.color ?: 0xFF888888)
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text(name, Modifier.weight(1f))
                        Text("${clock(secs)} • ${if (weekTotal > 0) secs * 100 / weekTotal else 0}%", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                    Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(soft)) {
                        Box(Modifier.fillMaxWidth(if (weekTotal > 0) (secs.toFloat() / weekTotal).coerceIn(0.02f, 1f) else 0.02f).fillMaxHeight().background(color))
                    }
                }
            }
            2 -> { // MONTH VIEW
                Text("Last 30 Days Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                Row {
                    StatCard("Month Total", "${monthTotal / 3600}h ${(monthTotal % 3600) / 60}m")
                    StatCard("Daily Avg", hm(monthTotal / 30))
                    StatCard("Days Studied", "${per30.count { it > 0 }}/30")
                }

                Spacer(Modifier.height(18.dp))
                Text("Daily Activity (Tap to view day)", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                Row(Modifier.fillMaxWidth()) {
                    for (b in 13 downTo 0) {
                        val v = per30[b]
                        Column(Modifier.weight(1f).clickable { back = b; statsTab = 0 }, horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.height(80.dp).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                                Box(Modifier.fillMaxWidth(0.6f).fillMaxHeight(if (v > 0) (v.toFloat() / maxD).coerceIn(0.05f, 1f) else 0.03f)
                                    .background(if (b == back) accent else accent.copy(alpha = if (v > 0) 0.50f else 0.12f), RoundedCornerShape(3.dp)))
                            }
                            Text(SimpleDateFormat("d", Locale.getDefault()).format(Date(dayStart(b))), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Consistency Score 🎯", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        val consistencyPct = (per30.count { it >= 1800 } * 100) / 30
                        Text("$consistencyPct% of days reached deep work (30m+ focus).", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
                        LinearProgressIndicator(
                            progress = { consistencyPct / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = accent,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
            3 -> { // WEEKLY REPORT CARD
                val hoursCount = weekTotal / 3600
                val (grade, gradeTitle, color) = when {
                    hoursCount >= 20 -> Triple("A+", "Outstanding Focus Champion 🏆", Color(0xFF2E7D32))
                    hoursCount >= 14 -> Triple("A", "Excellent Study Scholar 🌟", Color(0xFF388E3C))
                    hoursCount >= 8 -> Triple("B+", "Consistent Steady Learner ⚡", Color(0xFF1976D2))
                    hoursCount >= 4 -> Triple("B", "Good Foundational Effort 🌱", Color(0xFFF57C00))
                    else -> Triple("C", "Building Momentum 🚀", Color(0xFFD32F2F))
                }

                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Weekly Report Card", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier.size(80.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(grade, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, color = color)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(gradeTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
                        Spacer(Modifier.height(14.dp))

                        // Mascot evaluation
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text("💬 Mascot Review:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    when (grade) {
                                        "A+" -> "Astounding performance! You completely dominated your study schedule. Keep this legendary discipline alive!"
                                        "A" -> "Fantastic week! You stayed disciplined and dedicated to your subjects. Keep pushing towards your goals!"
                                        "B+" -> "Solid work this week! Your consistency is building strong neural pathways. Try adding 15 more minutes next week!"
                                        "B" -> "A good start! Remember, small daily study sessions add up to massive results over a semester."
                                        else -> "Every expert was once a beginner. Start with just 25 minutes tomorrow and let's get that streak glowing!"
                                    },
                                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Scorecard details
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Weekly Focus", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${hoursCount}h ${(weekTotal % 3600) / 60}m", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Active Days", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${per7.count { it > 0 }}/7", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Top Subject", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(weekSubjects.firstOrNull()?.key ?: "—", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val shareTxt = "📚 My StudyReward Weekly Report Card:\nGrade: $grade ($gradeTitle)\n⏱️ Total Focus: ${hoursCount}h ${(weekTotal % 3600) / 60}m\n🔥 Active Days: ${per7.count { it > 0 }}/7\nLet's study hard together!"
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareTxt)
                                }
                                ctx.startActivity(Intent.createChooser(sendIntent, "Share Report Card"))
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Share Report Card 📢")
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun LogsScreen(uid: String, subjects: List<Subject>, onBack: () -> Unit) {
    val all = rememberSessions(uid)
    var back by remember { mutableStateOf(0) }
    var editing by remember { mutableStateOf<Sess?>(null) }
    var mins by remember { mutableStateOf("") }
    var subj by remember { mutableStateOf("") }
    val col = FirebaseFirestore.getInstance().collection("users").document(uid).collection("sessions")
    val day = all.filter { it.at >= dayStart(back) && it.at < dayEnd(back) }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("Study logs", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Tap a log to fix a mistake. You can shorten it, delete it, or change its subject, but never add time.",
            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
        DayNav(back) { back = it }
        if (day.isEmpty()) Text("No logs on this day.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 16.dp))
        day.forEach { s ->
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                .clickable { editing = s; mins = maxOf(1L, s.secs / 60).toString(); subj = s.subject }.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(12.dp).clip(CircleShape).background(Color(subjects.find { it.name == s.subject }?.color ?: 0xFF888888)))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(s.subject.ifBlank { "No subject" })
                    Text("${tfmt(s.at - s.secs * 1000)} • ${tfmt(s.at)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(clock(s.secs))
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    editing?.let { s ->
        AlertDialog(onDismissRequest = { editing = null },
            title = { Text("Edit log") },
            text = {
                Column {
                    OutlinedTextField(mins, { mins = it.filter { c -> c.isDigit() }.take(4) },
                        label = { Text("Length in minutes (at most ${maxOf(1L, s.secs / 60)})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Text("Subject", modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        subjects.forEach { sub ->
                            Text(sub.name, Modifier.padding(end = 6.dp).clip(RoundedCornerShape(16.dp))
                                .background(if (subj == sub.name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { subj = sub.name }.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = if (subj == sub.name) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val newSecs = minOf(s.secs, (mins.toLongOrNull() ?: (s.secs / 60)) * 60)
                    if (newSecs <= 0) col.document(s.id).delete()
                    else col.document(s.id).update(mapOf<String, Any>("seconds" to newSecs, "subject" to subj, "edited" to true))
                    editing = null
                }) { Text("Save") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { col.document(s.id).delete(); editing = null }) { Text("Delete") }
                    TextButton(onClick = { editing = null }) { Text("Cancel") }
                }
            })
    }
}

@Composable
fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 22.dp, bottom = 4.dp))
}

@Composable
fun SettingsScreen(
    name: String, email: String, themeIdx: Int, onTheme: (Int) -> Unit, fontIdx: Int, onFont: (Int) -> Unit,
    daysOff: List<String>, onDaysOff: (List<String>) -> Unit,
    rate: Int, cap: Int, base: Int, pendingFrom: String?, studiedTodaySecs: Long, onSaveAdvanced: (Int, Int, Int) -> Unit,
    onSignOut: () -> Unit, fp: FocusPrefs, onFp: (FocusPrefs) -> Unit,
    nick: String, onNick: (String) -> Unit, optIn: Boolean, onOptIn: (Boolean) -> Unit, onTour: () -> Unit, onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val todayKey = dateKey()
    val notifLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) { }
    fun askNotifications() { if (android.os.Build.VERSION.SDK_INT >= 33) notifLauncher.launch("android.permission.POST_NOTIFICATIONS") }
    var taps by remember { mutableStateOf(0) }
    var page by remember { mutableStateOf("") }
    var showRules by remember { mutableStateOf(false) }
    var r by remember(rate) { mutableStateOf(rate.coerceIn(0, 60)) }
    var c by remember(cap) { mutableStateOf(cap) }
    var b by remember(base) { mutableStateOf(base) }
    BackHandler(page != "") { page = "" }
    val accent = MaterialTheme.colorScheme.primary
    val soft = MaterialTheme.colorScheme.surfaceVariant

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
        if (page == "") {
            TextButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("  Back")
            }
            Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            SettingsRow(Icons.Rounded.Person, "Profile and privacy", "Nickname and public rankings") { page = "privacy" }
            SettingsRow(Icons.Rounded.Palette, "Appearance", "Theme and font") { page = "look" }
            SettingsRow(Icons.Rounded.Timer, "Focus and breaks", "Pomodoro and break reminders") { page = "focus" }
            SettingsRow(Icons.Rounded.BeachAccess, "Days off", "Holidays and vacations") { page = "off" }
            SettingsRow(Icons.Rounded.Tune, "Rewards & Earn Rate", "Screen time ratio (1:1 parity) and daily limits") { page = "adv" }
            SettingsRow(Icons.Rounded.Info, "Help and about", "How time is counted, welcome tour, sign out") { page = "about" }
            Text("StudyReward 1.0", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 24.dp).clickable { taps++ })
        } else {
            TextButton(onClick = { page = "" }) {
                Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("  Settings")
            }
            when (page) {
                "privacy" -> {
                    Text("Profile and privacy", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    SectionLabel("Account")
                    Text(name.ifBlank { "Student" })
                    Text(email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    SectionLabel("Nickname")
                    var nickText by remember(nick) { mutableStateOf(nick) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(nickText, { nickText = it.take(20) }, label = { Text("Shown to others") },
                            singleLine = true, modifier = Modifier.weight(1f))
                        TextButton(onClick = { onNick(nickText) }) { Text("Save") }
                    }
                    SectionLabel("Rankings")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Show me in public rankings")
                            Text("Off by default. Members of your groups always see your status inside those groups.",
                                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(optIn, { onOptIn(it) })
                    }
                }
                "look" -> {
                    Text("Appearance", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    SectionLabel("Theme")
                    themes.forEachIndexed { i, t ->
                        Row(Modifier.fillMaxWidth().clickable { onTheme(i) }.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(width = 68.dp, height = 40.dp).clip(RoundedCornerShape(10.dp)).background(Color(t.bg))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)).padding(6.dp)) {
                                if (t.scene.isNotEmpty()) SceneBackground(t.scene, Color(t.bg))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(width = 30.dp, height = 24.dp).clip(RoundedCornerShape(6.dp)).background(Color(t.card)))
                                    Spacer(Modifier.width(6.dp))
                                    Box(Modifier.size(16.dp).clip(CircleShape).background(Color(t.accent)))
                                }
                            }
                            Spacer(Modifier.width(14.dp))
                            Text(t.name, Modifier.weight(1f))
                            if (i == themeIdx) Text("✓", color = accent, fontWeight = FontWeight.Bold)
                        }
                    }
                    SectionLabel("Font")
                    fonts.forEachIndexed { i, (fname, family) ->
                        Row(Modifier.fillMaxWidth().clickable { onFont(i) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(fname, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Nothing is impossible for those who dream.", fontFamily = family)
                            }
                            if (i == fontIdx) Text("✓", color = accent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "focus" -> {
                    Text("Focus and breaks", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    SectionLabel("Study mode")
                    Row(Modifier.padding(vertical = 6.dp)) {
                        listOf(false to "Normal", true to "🍅 Pomodoro").forEach { (mode, label) ->
                            Text(label, Modifier.padding(end = 8.dp).clip(RoundedCornerShape(16.dp))
                                .background(if (fp.pomo == mode) accent else soft)
                                .clickable { onFp(fp.copy(pomo = mode)) }.padding(horizontal = 14.dp, vertical = 8.dp),
                                color = if (fp.pomo == mode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    SectionLabel("Break reminders")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Remind me to take breaks", Modifier.weight(1f))
                        Switch(fp.on, { v -> onFp(fp.copy(on = v)); if (v) askNotifications() })
                    }
                    if (fp.on) {
                        Stepper("Remind me every", fp.every, "min", 20, 120, 5) { onFp(fp.copy(every = it)) }
                        Stepper("Break length", fp.len, "min", 5, 30, 5) { onFp(fp.copy(len = it)) }
                    }
                    SectionLabel("Pomodoro")
                    Stepper("Focus", fp.focus, "min", 15, 60, 5) { onFp(fp.copy(focus = it)) }
                    Stepper("Short break", fp.short, "min", 3, 15, 1) { onFp(fp.copy(short = it)) }
                    Stepper("Long break (every 4th round)", fp.long, "min", 10, 30, 5) { onFp(fp.copy(long = it)) }
                }
                "off" -> {
                    Text("Days off", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("No screen time limits on these days, and your streak skips them.", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                    Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Today is a day off", Modifier.weight(1f))
                        Switch(todayKey in daysOff, { on -> onDaysOff(if (on) (daysOff + todayKey).distinct() else daysOff - todayKey) })
                    }
                    Row(Modifier.padding(top = 8.dp)) {
                        OutlinedButton(onClick = { pickDate(ctx) { d -> onDaysOff((daysOff + dateKey(d)).distinct()) } }) { Text("+ Day off") }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(onClick = {
                            pickDate(ctx) { from ->
                                pickDate(ctx) { to ->
                                    val days = mutableListOf<String>()
                                    val cal = from.clone() as Calendar
                                    while (!cal.after(to) && days.size < 31) { days.add(dateKey(cal)); cal.add(Calendar.DAY_OF_MONTH, 1) }
                                    onDaysOff((daysOff + days).distinct())
                                }
                            }
                        }) { Text("+ Vacation") }
                    }
                    daysOff.filter { it >= todayKey }.sorted().forEach { d ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(d, Modifier.weight(1f))
                            Text("✕", Modifier.clickable { onDaysOff(daysOff - d) }.padding(8.dp))
                        }
                    }
                }
                "adv" -> {
                    Text("Rewards & Earn Rate", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("Customize how study time converts to play screen time (Max 1:1 Parity).", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))

                    val hasStudied = studiedTodaySecs > 0L
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (hasStudied) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (hasStudied) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(if (hasStudied) "🔒" else "⚡", fontSize = 20.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    if (hasStudied) "Already Studied Today" else "No Study Logged Yet Today",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (hasStudied) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    if (hasStudied) "You already logged ${hm(studiedTodaySecs)} today, so changes will activate tomorrow to protect today's session."
                                    else "You haven't started studying today yet, so your new rate will activate immediately today!",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Text("Quick Ratio Presets (Max 1:1 Parity):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 8.dp)) {
                        listOf(
                            Triple("⚡ 1:1 Equal (Max)", 60, "1 hr study = 60m play"),
                            Triple("⚖️ 2:1 Classic", 30, "1 hr study = 30m play"),
                            Triple("🎯 3:1 Focus", 20, "1 hr study = 20m play"),
                            Triple("🛡️ 4:1 Strict", 15, "1 hr study = 15m play")
                        ).forEach { (label, value, desc) ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (r == value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.padding(end = 8.dp).clickable { r = value }
                            ) {
                                Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                    Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                                        color = if (r == value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                                    Text(desc, fontSize = 10.sp,
                                        color = if (r == value) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    Stepper("Screen time per hour studied (max 60m / 1:1)", r, "min", 0, 60, 5) { r = it.coerceIn(0, 60) }
                    Text(
                        if (r == 60) "⚡ Perfect 1:1 parity active! (Maximum allowed: 1 min study = 1 min screen time)"
                        else "⚖️ Reward rate: ${r}m screen time per 60m studied",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Stepper("Reward tasks per day", c, "tasks", 1, 10, 1) { c = it }
                    Stepper("Free screen time every day", b, "min", 0, 240, 15) { b = it }
                    
                    Button(
                        onClick = { onSaveAdvanced(r.coerceIn(0, 60), c, b) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(48.dp)
                    ) {
                        Text(if (hasStudied) "Save (Activates Tomorrow 🔒)" else "Save & Apply Today ⚡", fontWeight = FontWeight.Bold)
                    }
                    if (pendingFrom != null) Text("A change is waiting and starts on $pendingFrom.", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                }
                "about" -> {
                    Text("Help and about", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showRules = true }, modifier = Modifier.padding(top = 8.dp)) { Text("How time is counted") }
                    TextButton(onClick = onTour) { Text("Replay the welcome tour") }
                    OutlinedButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) { Text("Sign out") }

                    var confirmDeleteAccount by remember { mutableStateOf(false) }
                    TextButton(
                        onClick = { confirmDeleteAccount = true },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text("⚠️ Delete My Account & Cloud Data", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    }

                    if (confirmDeleteAccount) {
                        AlertDialog(
                            onDismissRequest = { confirmDeleteAccount = false },
                            title = { Text("Delete Account Permanently?", fontWeight = FontWeight.Bold) },
                            text = { Text("This will permanently erase your study records, stats, and squad memberships from the cloud. This action cannot be undone.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    val u = FirebaseAuth.getInstance().currentUser
                                    if (u != null) {
                                        FirebaseFirestore.getInstance().collection("users").document(u.uid).delete()
                                        FirebaseFirestore.getInstance().collection("profiles").document(u.uid).delete()
                                        u.delete().addOnCompleteListener { onSignOut() }
                                    }
                                    confirmDeleteAccount = false
                                }) { Text("Permanently Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
                            },
                            dismissButton = { TextButton(onClick = { confirmDeleteAccount = false }) { Text("Cancel") } }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    if (showRules) AlertDialog(onDismissRequest = { showRules = false }, title = { Text("How time is counted") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text("• Study time counts while a subject timer runs. It is self-reported: the app cannot check that you are studying.\n\n" +
                        "• Each day you get free screen time, plus study time at your earn rate (maximum 30 min per hour), plus reward tasks (1.5x the rate), plus approved group requests (maximum $MAX_GIFT_MINS min a day).\n\n" +
                        "• Distracting apps stay locked unless your Use timer is running, it is a day off, or you use an emergency unlock.\n\n" +
                        "• Everything resets at midnight. On a day off there are no limits and your streak skips that day.\n\n" +
                        "• Breaks never count. A timetable block that starts by itself counts only after you tick it.\n\n" +
                        "• Challenges use the in-app Use timer. A streak revive costs $REVIVE_COST_MIN min and is not counted as use.", fontSize = 14.sp)
            }
        },
        confirmButton = { TextButton(onClick = { showRules = false }) { Text("Close") } })
}

// ---------- Daily goal and streak ----------

fun keyBack(b: Int): String = dateKey(Calendar.getInstance().apply { timeInMillis = dayStart(b) })
fun goalLabel(m: Long): String = if (m < 60) "${m}m" else if (m % 60 == 0L) "${m / 60}h" else "${m / 60}h ${m % 60}m"

// Returns (streak including today, streak that can still be revived).
// A day counts when its goal was met or the day was revived. Days off are skipped. A revive is only
// offered for yesterday, so missing two days in a row resets the streak to 0.
fun computeStreak(goals: Map<String, Long>, revived: Set<String>, secsByDay: Map<String, Long>,
                  daysOff: List<String>, todayMet: Boolean): Pair<Int, Int> {
    fun met(k: String): Boolean = k in revived || ((goals[k] ?: 0L) > 0 && (secsByDay[k] ?: 0L) >= (goals[k] ?: 0L) * 60)
    fun runFrom(start: Int): Int {
        var n = 0
        var b = start
        while (b < 60) {
            val k = keyBack(b)
            if (k in daysOff) { b++; continue }
            if (met(k)) { n++; b++ } else break
        }
        return n
    }
    val yesterday = keyBack(1)
    val missed = yesterday !in daysOff && !met(yesterday)
    return (runFrom(1) + if (todayMet) 1 else 0) to (if (missed) runFrom(2) else 0)
}

@Composable
fun GoalDialog(current: Long, suggested: Long, onPick: (Long) -> Unit, onDismiss: (() -> Unit)?) {
    val options = listOf(30L, 60L, 90L, 120L, 180L, 240L, 300L, 360L, 480L, 600L).filter { it >= current }
    var sel by remember { mutableStateOf(options.firstOrNull { it >= suggested } ?: options.lastOrNull() ?: 60L) }
    var isCustom by remember { mutableStateOf(false) }
    var customText by remember { mutableStateOf("") }
    val customVal = customText.toLongOrNull() ?: 0L
    val canConfirm = if (isCustom) customVal > current else sel > current || (current == 0L && sel > 0)

    AlertDialog(
        onDismissRequest = { onDismiss?.invoke() },
        title = { Text(if (current == 0L) "✨ Today's Study Goal 🌸" else "🚀 Level Up Today's Goal ✨") },
        text = {
            Column {
                Text(
                    if (current > 0) "Goals can only go up today cutie! Keep that streak shining 🔥"
                    else "Study at least this long to protect your streak! You've got this! 💖",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                options.chunked(3).forEach { row ->
                    Row(Modifier.padding(top = 6.dp)) {
                        row.forEach { m ->
                            val isSel = !isCustom && sel == m
                            Text(goalLabel(m), Modifier.padding(end = 6.dp).clip(RoundedCornerShape(16.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { isCustom = false; sel = m }.padding(horizontal = 14.dp, vertical = 8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCustom) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { isCustom = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Custom Goal ✏️✨", color = if (isCustom) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isCustom) FontWeight.Bold else FontWeight.Normal)
                }
                if (isCustom) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customText,
                        onValueChange = { customText = it.filter { c -> c.isDigit() }.take(4) },
                        label = { Text("Enter minutes (e.g. 45, 75, 150)") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (customVal > 0) {
                        Text("= ${goalLabel(customVal)}" + if (customVal <= current) " (must be > ${goalLabel(current)})" else "",
                            fontSize = 12.sp, color = if (customVal > current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalGoal = if (isCustom) customVal else sel
                    if (finalGoal > current || (current == 0L && finalGoal > 0)) {
                        onPick(finalGoal)
                    }
                },
                enabled = canConfirm
            ) { Text("Set Goal 🌸", fontWeight = FontWeight.Bold) }
        },
        dismissButton = { if (onDismiss != null) TextButton(onClick = onDismiss) { Text("Later") } }
    )
}

// ---------- Events and countdowns ----------

fun parseKey(k: String): Long = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(k)?.time ?: 0L
fun daysBetween(a: String, b: String): Int = Math.round((parseKey(b) - parseKey(a)) / 86400000.0).toInt()
fun keyPlus(k: String, n: Int): String = dateKey(Calendar.getInstance().apply { timeInMillis = parseKey(k); add(Calendar.DAY_OF_MONTH, n) })
fun keyOfMs(ms: Long): String = dateKey(Calendar.getInstance().apply { timeInMillis = ms })
fun wdIndex(k: String): Int = (Calendar.getInstance().apply { timeInMillis = parseKey(k) }.get(Calendar.DAY_OF_WEEK) + 5) % 7
fun niceDate(k: String): String = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(parseKey(k)))

@Composable
fun rememberSessionsSince(uid: String, since: Long): List<Sess> {
    var list by remember { mutableStateOf(listOf<Sess>()) }
    DisposableEffect(uid, since) {
        val reg = FirebaseFirestore.getInstance().collection("users").document(uid).collection("sessions")
            .whereGreaterThanOrEqualTo("at", since).addSnapshotListener { snap, _ ->
                list = snap?.documents?.map {
                    Sess(it.id, it.getString("subject") ?: "", it.getLong("seconds") ?: 0L, it.getLong("at") ?: 0L)
                } ?: emptyList()
            }
        onDispose { reg.remove() }
    }
    return list
}

@Composable
fun EventsScreen(uid: String, subjects: List<Subject>, events: List<EventItem>, startId: String?, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val col = FirebaseFirestore.getInstance().collection("users").document(uid).collection("events")
    var openId by remember { mutableStateOf(startId) }
    var adding by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var picked by remember { mutableStateOf<String?>(null) }
    var color by remember { mutableStateOf(palette[0]) }
    var chosen by remember { mutableStateOf(setOf<String>()) }
    var err by remember { mutableStateOf("") }
    val ev = events.find { it.id == openId }
    val today = dateKey()

    if (ev != null) {
        EventDetail(uid, ev, subjects, { col.document(ev.id).delete(); openId = null }) { openId = null }
        return
    }
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("Events", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Add exams or deadlines to count down and see how much you studied for them.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp))
        Button(onClick = { adding = true }) { Text("+ New event") }
        if (events.isEmpty()) Text("No events yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 16.dp))
        events.forEach { e ->
            val dl = daysBetween(today, e.date)
            Row(Modifier.fillMaxWidth().padding(top = 10.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                .clickable { openId = e.id }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(5.dp).height(34.dp).background(Color(e.color), RoundedCornerShape(3.dp)))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(e.name, fontWeight = FontWeight.Medium)
                    Text(niceDate(e.date), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(if (dl < 0) "Past" else if (dl == 0) "Today" else "$dl d", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(e.color))
            }
        }
        Spacer(Modifier.height(24.dp))
    }
    if (adding) {
        AlertDialog(onDismissRequest = { adding = false },
            title = { Text("New event") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(name, { name = it }, label = { Text("Event name") }, singleLine = true)
                    OutlinedButton(onClick = { pickDate(ctx) { picked = dateKey(it) } }, modifier = Modifier.padding(top = 8.dp)) {
                        Text(if (picked == null) "Pick the date" else "Date: ${niceDate(picked!!)}")
                    }
                    Text("Colour", modifier = Modifier.padding(top = 12.dp))
                    ColorRow(color) { color = it }
                    Text("Count study time for (none selected = all subjects)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        subjects.forEach { sub ->
                            val on = sub.name in chosen
                            Text(sub.name, Modifier.padding(end = 6.dp).clip(RoundedCornerShape(16.dp))
                                .background(if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { chosen = if (on) chosen - sub.name else chosen + sub.name }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                color = if (on) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    if (err.isNotBlank()) Text(err, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val d = picked
                    if (name.isBlank()) err = "Give it a name"
                    else if (d == null || d < dateKey()) err = "Pick a date in the future"
                    else {
                        col.add(mapOf<String, Any>("name" to name.trim(), "date" to d, "start" to dateKey(),
                            "color" to color, "subjects" to chosen.toList()))
                        adding = false; name = ""; picked = null; chosen = emptySet(); err = ""
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { adding = false }) { Text("Cancel") } })
    }
}

@Composable
fun RowScope.HeatCell(k: String?, secs: Long, today: String, color: Color) {
    if (k == null) { Spacer(Modifier.weight(1f).aspectRatio(1f)); return }
    val a = when {
        k > today -> 0.06f
        secs <= 0 -> 0.14f
        secs < 3600 -> 0.35f
        secs < 3 * 3600 -> 0.55f
        secs < 5 * 3600 -> 0.78f
        else -> 1f
    }
    val ring = MaterialTheme.colorScheme.onSurface
    Box(Modifier.weight(1f).aspectRatio(1f).padding(2.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = a))
        .then(if (k == today) Modifier.border(1.5.dp, ring, RoundedCornerShape(8.dp)) else Modifier),
        contentAlignment = Alignment.Center) {
        Text(k.takeLast(2).trimStart('0'), fontSize = 10.sp, color = if (a > 0.5f) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EventDetail(uid: String, ev: EventItem, subjects: List<Subject>, onDelete: () -> Unit, onBack: () -> Unit) {
    val all = rememberSessionsSince(uid, parseKey(ev.start))
    var confirmDel by remember { mutableStateOf(false) }
    val today = dateKey()
    val color = Color(ev.color)
    val soft = MaterialTheme.colorScheme.surfaceVariant

    val filtered = all.filter { ev.subjects.isEmpty() || it.subject in ev.subjects }
    val perDay = filtered.groupBy { keyOfMs(it.at) }.mapValues { e -> e.value.sumOf { it.secs } }
    val totalDays = (daysBetween(ev.start, ev.date) + 1).coerceIn(1, 800)
    val elapsed = (daysBetween(ev.start, today) + 1).coerceIn(1, totalDays)
    val daysLeft = maxOf(0, daysBetween(today, ev.date))
    val keys = (0 until totalDays).map { keyPlus(ev.start, it) }
    val past = keys.take(elapsed)

    val total = past.sumOf { perDay[it] ?: 0L }
    val avg = total / elapsed
    val studiedDays = past.count { (perDay[it] ?: 0L) > 0 }
    val bestKey = past.maxByOrNull { perDay[it] ?: 0L }
    var run = 0
    var longest = 0
    past.forEach { if ((perDay[it] ?: 0L) > 0) { run++; longest = maxOf(longest, run) } else run = 0 }
    val upto = if ((perDay[today] ?: 0L) > 0) past else past.dropLast(1)
    var cur = 0
    upto.forEach { if ((perDay[it] ?: 0L) > 0) cur++ else cur = 0 }
    val last7 = (0..6).sumOf { perDay[keyBack(it)] ?: 0L }
    val prev7 = (7..13).sumOf { perDay[keyBack(it)] ?: 0L }
    val projected = total + avg * daysLeft
    val wdSum = LongArray(7)
    val wdCnt = IntArray(7)
    past.forEach { wdSum[wdIndex(it)] += perDay[it] ?: 0L; wdCnt[wdIndex(it)]++ }
    val wdAvg = (0..6).map { if (wdCnt[it] > 0) wdSum[it] / wdCnt[it] else 0L }
    val maxWd = maxOf(1L, wdAvg.maxOrNull() ?: 1L)
    val perSubject = filtered.groupBy { it.subject.ifBlank { "No subject" } }
        .mapValues { e -> e.value.sumOf { it.secs } }.entries.sortedByDescending { it.value }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text(ev.name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(niceDate(ev.date) + if (ev.subjects.isEmpty()) " • all subjects" else " • " + ev.subjects.joinToString(", "),
            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Column(Modifier.fillMaxWidth().padding(top = 12.dp).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)).padding(20.dp)) {
            Text(if (ev.date < today) "Done" else if (daysLeft == 0) "Today!" else "$daysLeft", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = color)
            Text(if (daysLeft == 1) "day left" else "days left", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Box(Modifier.padding(top = 12.dp).fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.background)) {
                Box(Modifier.fillMaxWidth((elapsed.toFloat() / totalDays).coerceIn(0.02f, 1f)).fillMaxHeight().background(color))
            }
            Text("Day $elapsed of $totalDays", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
        }

        Row(Modifier.padding(top = 8.dp)) {
            StatCard("Total time", hm(total))
            StatCard("Daily average", hm(avg))
            StatCard("Days studied", "$studiedDays/$elapsed")
        }
        Row {
            StatCard("Best day", hm(perDay[bestKey] ?: 0L))
            StatCard("Longest run", "$longest d")
            StatCard("Current run", "$cur d")
        }

        Text("Pace", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 20.dp, bottom = 6.dp))
        Text("At your average pace you will have studied about ${hm(projected)} by the event.")
        val diff = last7 - prev7
        Text("Last 7 days: ${hm(last7)} (${if (diff >= 0) "▲" else "▼"} ${hm(kotlin.math.abs(diff))} vs the 7 days before)",
            color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))

        Text("Study log", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
        Row(Modifier.fillMaxWidth()) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { Text(it, Modifier.weight(1f), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center) }
        }
        val cells: List<String?> = List(wdIndex(ev.start)) { null } + keys
        cells.chunked(7).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { k -> HeatCell(k, if (k != null) perDay[k] ?: 0L else 0L, today, color) }
                repeat(7 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Less ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            listOf(0.14f, 0.35f, 0.55f, 0.78f, 1f).forEach {
                Box(Modifier.padding(horizontal = 2.dp).size(14.dp).clip(RoundedCornerShape(4.dp)).background(color.copy(alpha = it)))
            }
            Text(" More (0, 1h, 3h, 5h+)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text("Average by weekday", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
        Row(Modifier.fillMaxWidth().height(80.dp), verticalAlignment = Alignment.Bottom) {
            for (d in 0..6) {
                Box(Modifier.weight(1f).padding(horizontal = 5.dp)
                    .fillMaxHeight((wdAvg[d].toFloat() / maxWd).coerceIn(0.04f, 1f)).background(color, RoundedCornerShape(5.dp)))
            }
        }
        Row(Modifier.fillMaxWidth()) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                Text(it, Modifier.weight(1f), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }

        Text("Subjects", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 24.dp, bottom = 6.dp))
        if (perSubject.isEmpty()) Text("Nothing studied yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        perSubject.forEach { (n, secs) ->
            val c = Color(subjects.find { it.name == n }?.color ?: 0xFF888888)
            val all2 = maxOf(1L, perSubject.sumOf { it.value })
            Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text(n, Modifier.weight(1f))
                Text("${hm(secs)} • ${secs * 100 / all2}%", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
            Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(soft)) {
                Box(Modifier.fillMaxWidth((secs.toFloat() / all2).coerceIn(0.02f, 1f)).fillMaxHeight().background(c))
            }
        }

        TextButton(onClick = { confirmDel = true }, modifier = Modifier.padding(top = 24.dp)) { Text("Delete this event", color = Color.Red) }
        Spacer(Modifier.height(24.dp))
    }
    if (confirmDel) {
        AlertDialog(onDismissRequest = { confirmDel = false },
            title = { Text("Delete ${ev.name}?") },
            confirmButton = { TextButton(onClick = { confirmDel = false; onDelete() }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { confirmDel = false }) { Text("Cancel") } })
    }
}

@Composable
fun Stepper(label: String, value: Int, unit: String, min: Int, max: Int, step: Int, onChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f))
        OutlinedButton(onClick = { onChange(maxOf(min, value - step)) }) { Text("-") }
        Text("  $value $unit  ")
        OutlinedButton(onClick = { onChange(minOf(max, value + step)) }) { Text("+") }
    }
}

// ---------- Timetable ----------

data class Slot(val id: String, val day: Int, val start: Int, val end: Int, val subject: String, val note: String)

val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

fun minLabel(m: Int): String {
    val h = m / 60
    val h12 = if (h % 12 == 0) 12 else h % 12
    return "%d:%02d %s".format(h12, m % 60, if ((h / 12) % 2 == 0) "AM" else "PM")
}

fun pickTime(ctx: Context, initial: Int, onPick: (Int) -> Unit) {
    android.app.TimePickerDialog(ctx, { _, h, m -> onPick(h * 60 + m) }, initial / 60 % 24, initial % 60, false).show()
}

data class CalendarEventItem(
    val id: Long,
    val title: String,
    val startMin: Int,
    val endMin: Int,
    val dayOfWeek: Int,
    val calendarName: String,
    val location: String?,
    val allDay: Boolean,
    val color: Int?
)

fun queryCalendarEvents(context: Context, startMillis: Long, endMillis: Long): List<CalendarEventItem> {
    val events = mutableListOf<CalendarEventItem>()
    try {
        if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALENDAR) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return emptyList()
        }
        val projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.CALENDAR_DISPLAY_NAME,
            CalendarContract.Instances.EVENT_LOCATION,
            CalendarContract.Instances.DISPLAY_COLOR
        )
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)

        context.contentResolver.query(
            builder.build(),
            projection,
            null,
            null,
            CalendarContract.Instances.BEGIN + " ASC"
        )?.use { cursor ->
            val idIdx = cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID)
            val titleIdx = cursor.getColumnIndex(CalendarContract.Instances.TITLE)
            val beginIdx = cursor.getColumnIndex(CalendarContract.Instances.BEGIN)
            val endIdx = cursor.getColumnIndex(CalendarContract.Instances.END)
            val allDayIdx = cursor.getColumnIndex(CalendarContract.Instances.ALL_DAY)
            val calIdx = cursor.getColumnIndex(CalendarContract.Instances.CALENDAR_DISPLAY_NAME)
            val locIdx = cursor.getColumnIndex(CalendarContract.Instances.EVENT_LOCATION)
            val colIdx = cursor.getColumnIndex(CalendarContract.Instances.DISPLAY_COLOR)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIdx)
                val title = cursor.getString(titleIdx) ?: "Calendar Event"
                val begin = cursor.getLong(beginIdx)
                val end = cursor.getLong(endIdx)
                val allDay = cursor.getInt(allDayIdx) == 1
                val calName = cursor.getString(calIdx) ?: "Google Calendar"
                val loc = if (locIdx >= 0 && !cursor.isNull(locIdx)) cursor.getString(locIdx) else null
                val color = if (colIdx >= 0 && !cursor.isNull(colIdx)) cursor.getInt(colIdx) else null

                val cal = Calendar.getInstance().apply { timeInMillis = begin }
                val dayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
                val startMin = if (allDay) 0 else (cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE))
                val endCal = Calendar.getInstance().apply { timeInMillis = end }
                val endMin = if (allDay) 24 * 60 else (endCal.get(Calendar.HOUR_OF_DAY) * 60 + endCal.get(Calendar.MINUTE)).coerceAtLeast(startMin + 15)

                events.add(
                    CalendarEventItem(
                        id = id,
                        title = title,
                        startMin = startMin,
                        endMin = endMin,
                        dayOfWeek = dayOfWeek,
                        calendarName = calName,
                        location = loc,
                        allDay = allDay,
                        color = color
                    )
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return events
}

@Composable
fun TimetableTab(
    uid: String,
    subjects: List<Subject>,
    slots: List<Slot>,
    onStartStudy: ((String) -> Unit)? = null
) {
    val ctx = LocalContext.current
    val col = FirebaseFirestore.getInstance().collection("users").document(uid).collection("timetable")
    val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
    var day by remember { mutableStateOf(todayIdx) }
    var weekView by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var dSubject by remember { mutableStateOf("") }
    var dCustom by remember { mutableStateOf("") }
    var dDays by remember { mutableStateOf(setOf<Int>()) }
    var dStart by remember { mutableStateOf(16 * 60) }
    var dEnd by remember { mutableStateOf(17 * 60) }
    var dNote by remember { mutableStateOf("") }
    var err by remember { mutableStateOf("") }

    val fullDayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    var reloadKey by remember { mutableStateOf(0) }
    var hasCalPerm by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                ctx,
                android.Manifest.permission.READ_CALENDAR
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    val calPermLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCalPerm = granted
        if (granted) reloadKey++
    }

    val calendarEvents by produceState<List<CalendarEventItem>>(initialValue = emptyList(), hasCalPerm, reloadKey) {
        if (!hasCalPerm) {
            value = emptyList()
        } else {
            val calWeekStart = Calendar.getInstance().apply {
                val dow = (get(Calendar.DAY_OF_WEEK) + 5) % 7
                add(Calendar.DAY_OF_MONTH, -dow)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startMillis = calWeekStart.timeInMillis
            val endMillis = startMillis + 7L * 24 * 60 * 60 * 1000
            value = queryCalendarEvents(ctx, startMillis, endMillis)
        }
    }

    val nowCalendar = Calendar.getInstance()
    val nowMin = nowCalendar.get(Calendar.HOUR_OF_DAY) * 60 + nowCalendar.get(Calendar.MINUTE)
    val ongoingSlot = if (day == todayIdx) slots.find { it.day == todayIdx && nowMin >= it.start && nowMin < it.end } else null
    val ongoingCal = if (day == todayIdx) calendarEvents.find { it.dayOfWeek == todayIdx && !it.allDay && nowMin >= it.startMin && nowMin < it.endMin } else null

    fun colorOf(name: String) = Color(subjects.find { it.name == name }?.color ?: 0xFF888888)

    fun openAdd() {
        editId = null
        dSubject = subjects.firstOrNull()?.name ?: "Other"
        dCustom = ""
        dDays = setOf(day)
        dStart = 16 * 60
        dEnd = 17 * 60
        dNote = ""
        err = ""
        dialog = true
    }

    fun openEdit(s: Slot) {
        editId = s.id
        if (subjects.any { it.name == s.subject }) {
            dSubject = s.subject
            dCustom = ""
        } else {
            dSubject = "Other"
            dCustom = s.subject
        }
        dDays = setOf(s.day)
        dStart = s.start
        dEnd = s.end
        dNote = s.note
        err = ""
        dialog = true
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 100.dp)
    ) {
        // 1. Warm Bubbly Header
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Study Planner ✨",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Schedule lectures & focus blocks",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFFFB4A2),
                modifier = Modifier.clickable { openAdd() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("＋", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A1E14))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Block", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A1E14))
                }
            }
        }

        // 2. Happening Now Pill Banner (Smart Focus Prompt)
        if (day == todayIdx && (ongoingSlot != null || ongoingCal != null)) {
            val activeName = ongoingSlot?.subject ?: ongoingCal?.title ?: "Focus Block"
            val activeEnd = ongoingSlot?.end ?: ongoingCal?.endMin ?: 0
            val isFromCal = ongoingSlot == null && ongoingCal != null

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = if (isFromCal) Color(0xFFF3E5F5) else Color(0xFFE8F5E9),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isFromCal) Color(0xFFCE93D8) else Color(0xFFA5D6A7)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isFromCal) Color(0xFFE1BEE7) else Color(0xFFC8E6C9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (isFromCal) "📅" else "🟢", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "HAPPENING NOW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFromCal) Color(0xFF7B1FA2) else Color(0xFF2E7D32),
                                letterSpacing = 1.sp
                            )
                            Text(
                                activeName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Until ${minLabel(activeEnd)} • Tap to start focus!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isFromCal) Color(0xFF8E24AA) else Color(0xFF43A047),
                        modifier = Modifier.clickable { onStartStudy?.invoke(activeName) }
                    ) {
                        Text(
                            "▶ Focus",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                        )
                    }
                }
            }
        }

        // 3. Cute Pastel Google Calendar Card
        val dayCalEvents = calendarEvents.filter { it.dayOfWeek == day }.sortedBy { it.startMin }
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFFFF7ED),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD8BE)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFE8D6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🗓️", fontSize = 22.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        if (hasCalPerm) "Google Calendar Linked ✨" else "Sync Phone Calendar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF5D4037)
                    )
                    Text(
                        if (hasCalPerm) "${dayCalEvents.size} event${if (dayCalEvents.size == 1) "" else "s"} on ${fullDayNames[day]}"
                        else "Show your college classes & exam dates",
                        fontSize = 11.sp,
                        color = Color(0xFF8D6E63)
                    )
                }
                if (!hasCalPerm) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFF8A65),
                        modifier = Modifier.clickable { calPermLauncher.launch(android.Manifest.permission.READ_CALENDAR) }
                    ) {
                        Text(
                            "Connect",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFE5D9),
                        modifier = Modifier.clickable { reloadKey++ }
                    ) {
                        Text(
                            "↻ Refresh",
                            color = Color(0xFFD84315),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 4. Pastel Segmented Toggle (Day View / Week View)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Row(Modifier.padding(4.dp)) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (!weekView) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { weekView = false }
                ) {
                    Text(
                        "☀️ Day Schedule",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (!weekView) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (weekView) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { weekView = true }
                ) {
                    Text(
                        "🗓️ Full Week",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (weekView) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (!weekView) {
            // 5. Spacious Rounded Day Capsules
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (d in 0..6) {
                    val isSel = day == d
                    val isToday = d == todayIdx
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = if (isToday && !isSel) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { day = d }
                    ) {
                        Column(
                            Modifier.padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                dayNames[d],
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isToday) (if (isSel) Color.White else MaterialTheme.colorScheme.primary)
                                        else Color.Transparent
                                    )
                            )
                        }
                    }
                }
            }

            val list = slots.filter { it.day == day }.sortedBy { it.start }

            // 6. Google Calendar Events Section (if any on this day)
            if (dayCalEvents.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        "📅 Google Calendar Events",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A1B9A)
                    )
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF3E5F5)
                    ) {
                        Text(
                            "${dayCalEvents.size}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A1B9A),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                dayCalEvents.forEach { ev ->
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xFFFAF5FC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1BEE7)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (ev.color != null) Color(ev.color) else Color(0xFFAB47BC))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    ev.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    if (ev.allDay) "All Day" else "${minLabel(ev.startMin)} – ${minLabel(ev.endMin)}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF8E24AA),
                                    fontWeight = FontWeight.Medium
                                )
                                if (!ev.location.isNullOrBlank()) {
                                    Text(
                                        "📍 ${ev.location}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF3E5F5),
                                    modifier = Modifier.clickable {
                                        editId = null
                                        dSubject = "Other"
                                        dCustom = ev.title
                                        dDays = setOf(day)
                                        dStart = ev.startMin
                                        dEnd = ev.endMin
                                        dNote = ev.location ?: ev.calendarName
                                        err = ""
                                        dialog = true
                                    }
                                ) {
                                    Text(
                                        "＋ Block",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6A1B9A),
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFE8F5E9),
                                    modifier = Modifier.clickable { onStartStudy?.invoke(ev.title) }
                                ) {
                                    Text(
                                        "▶ Focus",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 7. Scheduled Study Blocks Section
            Spacer(Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    "📚 Study Schedule (${fullDayNames[day]})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (list.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🛋️", fontSize = 32.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Free day on ${fullDayNames[day]}!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "No study blocks scheduled. Tap '+ Add Block' above to schedule a focus session and earn playtime.",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            list.forEach { sl ->
                val sColor = colorOf(sl.subject)
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = sColor.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, sColor.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { openEdit(sl) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .width(4.dp)
                                .height(44.dp)
                                .background(sColor, RoundedCornerShape(2.dp))
                        )
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    sl.subject,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = sColor.copy(alpha = 0.20f)
                                ) {
                                    Text(
                                        hm((sl.end - sl.start) * 60L),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = sColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "${minLabel(sl.start)} → ${minLabel(sl.end)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (sl.note.isNotBlank()) {
                                Text(
                                    "📝 ${sl.note}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.clickable { onStartStudy?.invoke(sl.subject) }
                        ) {
                            Text(
                                "▶ Focus",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // 8. Bubbly Week Planner Grid
            val first = minOf(6, (slots.minOfOrNull { it.start } ?: 360) / 60)
            val lastH = maxOf(22, ((slots.maxOfOrNull { it.end } ?: 1320) + 59) / 60)
            val perMin = 0.50f
            val height = ((lastH - first) * 60 * perMin).dp

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(Modifier.width(36.dp))
                        for (d in 0..6) {
                            Text(
                                dayNames[d],
                                Modifier.weight(1f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = if (d == todayIdx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (d == todayIdx) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Box(Modifier.width(36.dp).height(height)) {
                            for (h in first..lastH step 2) {
                                Text(
                                    "$h:00",
                                    Modifier.offset(y = ((h - first) * 60 * perMin).dp),
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        for (d in 0..6) {
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(height)
                                    .padding(horizontal = 1.5.dp)
                                    .background(Color.White.copy(alpha = 0.60f), RoundedCornerShape(8.dp))
                            ) {
                                // Timetable blocks
                                slots.filter { it.day == d }.forEach { sl ->
                                    val sc = colorOf(sl.subject)
                                    Box(
                                        Modifier
                                            .offset(y = ((sl.start - first * 60) * perMin).dp)
                                            .fillMaxWidth()
                                            .height(((sl.end - sl.start) * perMin).dp.coerceAtLeast(16.dp))
                                            .padding(1.dp)
                                            .background(sc.copy(alpha = 0.90f), RoundedCornerShape(6.dp))
                                            .clickable { openEdit(sl) }
                                    ) {
                                        Text(
                                            sl.subject.take(5),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                                // Google calendar events in lavender
                                calendarEvents.filter { it.dayOfWeek == d && !it.allDay }.forEach { ce ->
                                    Box(
                                        Modifier
                                            .offset(y = (((ce.startMin - first * 60) * perMin).coerceAtLeast(0f)).dp)
                                            .fillMaxWidth()
                                            .height(((ce.endMin - ce.startMin) * perMin).dp.coerceAtLeast(16.dp))
                                            .padding(1.dp)
                                            .background(Color(0xFFCE93D8), RoundedCornerShape(6.dp))
                                            .clickable { onStartStudy?.invoke(ce.title) }
                                    ) {
                                        Text(
                                            "📅 " + ce.title.take(4),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4A148C),
                                            modifier = Modifier.padding(1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Text(
                "💡 Lavender blocks (📅) are imported from your phone calendar. Tap any block to focus.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }

    // 9. Bubbly Pastel Add/Edit Block Dialog
    if (dialog) {
        AlertDialog(
            onDismissRequest = { dialog = false },
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    if (editId == null) "New Study Block ✨" else "Edit Study Block",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Text("Subject", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        (subjects.map { it.name } + "Other").forEach { n ->
                            val isSel = dSubject == n
                            val sCol = if (n == "Other") Color(0xFFFFB4A2) else colorOf(n)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSel) sCol else sCol.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .clickable { dSubject = n }
                            ) {
                                Text(
                                    n,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color.Black,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                    if (dSubject == "Other") {
                        OutlinedTextField(
                            dCustom,
                            { dCustom = it },
                            label = { Text("Custom subject name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Days of the Week", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth()) {
                        for (d in 0..6) {
                            val sel = d in dDays
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp)
                                    .clickable {
                                        dDays = if (sel) dDays - d else dDays + d
                                    }
                            ) {
                                Text(
                                    dayNames[d],
                                    textAlign = TextAlign.Center,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { pickTime(ctx, dStart) { dStart = it } }
                        ) {
                            Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Starts At", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(2.dp))
                                Text(minLabel(dStart), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { pickTime(ctx, dEnd) { dEnd = it } }
                        ) {
                            Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Ends At", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(2.dp))
                                Text(minLabel(dEnd), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        dNote,
                        { dNote = it },
                        label = { Text("Note / Classroom (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    if (err.isNotBlank()) {
                        Text(
                            err,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        val sName = if (dSubject == "Other") dCustom.trim() else dSubject
                        if (sName.isBlank()) { err = "Please pick a subject or enter a name"; return@clickable }
                        if (dDays.isEmpty()) { err = "Pick at least one day"; return@clickable }
                        if (dStart >= dEnd) { err = "End time must be after start time"; return@clickable }

                        val id = editId
                        if (id == null) {
                            dDays.forEach { d ->
                                col.add(mapOf("day" to d, "start" to dStart, "end" to dEnd, "subject" to sName, "note" to dNote))
                            }
                            dialog = false
                        } else {
                            val d = dDays.first()
                            col.document(id).set(mapOf("day" to d, "start" to dStart, "end" to dEnd, "subject" to sName, "note" to dNote))
                            dialog = false
                        }
                    }
                ) {
                    Text("Save Block", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp))
                }
            },
            dismissButton = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val id = editId
                    if (id != null) {
                        TextButton(onClick = { col.document(id).delete(); dialog = false }) {
                            Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    TextButton(onClick = { dialog = false }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        )
    }
}
@Composable
fun TaskDayNav(back: Int, onChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        TextButton(onClick = { onChange(minOf(29, back + 1)) }, enabled = back < 29) { Text("‹", fontSize = 26.sp, fontWeight = FontWeight.Bold) }
        Text(if (back == -1) "Tomorrow" else if (back < -1) SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(Date(dayStart(back))) else dfmt(back),
            fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        TextButton(onClick = { onChange(maxOf(-7, back - 1)) }, enabled = back > -7) { Text("›", fontSize = 26.sp, fontWeight = FontWeight.Bold) }
    }
}

// ---------- Challenges ----------

data class Chal(val id: String, val type: String, val param: Int, val start: String, val days: Int)

val chalTypes = listOf(
    Triple("nouse", "📵 No screen-time day", "Don't use your screen-time timer all day."),
    Triple("late", "🌙 Screen-free evenings", "No screen time after the hour you choose."),
    Triple("early", "🌅 Early bird", "Start studying (10+ min) before the hour you choose."),
    Triple("week", "🎯 Study target", "Reach a total number of study hours before the challenge ends."))

fun minuteOfDay(ms: Long): Int = Calendar.getInstance().apply { timeInMillis = ms }.let { it.get(Calendar.HOUR_OF_DAY) * 60 + it.get(Calendar.MINUTE) }

fun chalDayOk(c: Chal, key: String, sess: List<Sess>, spends: List<Sess>): Boolean = when (c.type) {
    "nouse" -> spends.none { keyOfMs(it.at) == key && it.secs > 0 }
    "late" -> spends.none { keyOfMs(it.at) == key && minuteOfDay(it.at) >= c.param * 60 }
    "early" -> sess.any { keyOfMs(it.at) == key && it.secs >= 600 && minuteOfDay(it.at - it.secs * 1000) < c.param * 60 }
    else -> false
}

fun chalDetail(c: Chal): String = when (c.type) {
    "late" -> "No screen time after ${c.param}:00 • ${c.days} days"
    "early" -> "Start studying before ${c.param}:00 • ${c.days} days"
    "week" -> "Study ${c.param} hours in ${c.days} days"
    else -> "No screen time • ${c.days} days"
}

@Composable
fun rememberSpends(uid: String, since: Long): List<Sess> {
    var list by remember { mutableStateOf(listOf<Sess>()) }
    DisposableEffect(uid, since) {
        val reg = FirebaseFirestore.getInstance().collection("users").document(uid).collection("spends")
            .whereGreaterThanOrEqualTo("at", since).addSnapshotListener { snap, _ ->
                list = snap?.documents?.filter { it.getString("reason") != "revive" }
                    ?.map { Sess(it.id, "", it.getLong("seconds") ?: 0L, it.getLong("at") ?: 0L) } ?: emptyList()
            }
        onDispose { reg.remove() }
    }
    return list
}

@Composable
fun ChallengesScreen(uid: String, groups: List<Group> = emptyList(), onBack: () -> Unit) {
    val col = FirebaseFirestore.getInstance().collection("users").document(uid).collection("challenges")
    val db = FirebaseFirestore.getInstance()
    var list by remember { mutableStateOf(listOf<Chal>()) }
    DisposableEffect(uid) {
        val reg = col.addSnapshotListener { snap, _ ->
            list = (snap?.documents?.map {
                Chal(it.id, it.getString("type") ?: "", (it.getLong("param") ?: 0L).toInt(), it.getString("start") ?: dateKey(), (it.getLong("days") ?: 7L).toInt())
            } ?: emptyList()).sortedByDescending { it.start }
        }
        onDispose { reg.remove() }
    }
    val sess = rememberSessionsSince(uid, dayStart(60))
    val spends = rememberSpends(uid, dayStart(60))
    val today = dateKey()
    val accent = MaterialTheme.colorScheme.primary
    val soft = MaterialTheme.colorScheme.surfaceVariant
    var joining by remember { mutableStateOf<String?>(null) }
    var param by remember { mutableStateOf(20) } // target study hours or hour limit
    var days by remember { mutableStateOf(21) } // 7, 14, 21, 30
    var shareToGroup by remember { mutableStateOf(false) }
    var selectedGroupId by remember { mutableStateOf(groups.firstOrNull()?.id ?: "") }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", modifier = Modifier.size(18.dp))
            }
            Text("Study Challenges", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Text("Active Challenges", fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
        if (list.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No active challenges yet. Choose a challenge below to boost your focus!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        list.forEach { c ->
            val keys = (0 until c.days).map { keyPlus(c.start, it) }
            val title = chalTypes.find { it.first == c.type }?.second ?: c.type
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = { col.document(c.id).delete() }) {
                            Text("✕", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(chalDetail(c), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (c.type == "week") {
                        val total = sess.filter { keyOfMs(it.at) in keys }.sumOf { it.secs }
                        val target = c.param * 3600L
                        val ended = keys.last() < today
                        val pct = ((total.toFloat() / maxOf(1L, target)) * 100).toInt()
                        Spacer(Modifier.height(8.dp))
                        Box(Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(MaterialTheme.colorScheme.background)) {
                            Box(Modifier.fillMaxWidth((total.toFloat() / maxOf(1L, target)).coerceIn(0.02f, 1f)).fillMaxHeight().background(accent))
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${hm(total)} of ${c.param}h ($pct%)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = accent)
                            val remainingDays = keys.count { it >= today }
                            Text(if (total >= target) "Completed!" else "$remainingDays days left", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        // Clean day tracker (clean compact wrap)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            keys.forEach { k ->
                                val ok = chalDayOk(c, k, sess, spends)
                                val mark = when {
                                    k > today -> "—"
                                    k == today -> if (ok) "✓" else if (c.type == "early") "—" else "⋯"
                                    ok -> "✓"
                                    else -> "✕"
                                }
                                Box(
                                    Modifier.padding(end = 5.dp).size(26.dp).clip(CircleShape)
                                        .background(if (mark == "✓") accent else MaterialTheme.colorScheme.background),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(mark, fontSize = 11.sp, color = if (mark == "✓") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        val past = keys.filter { it < today }
                        val good = past.count { chalDayOk(c, it, sess, spends) }
                        Text(
                            if (keys.last() < today) (if (good == c.days) "Completed!" else "Ended • $good of ${c.days} days")
                            else "Day ${minOf(c.days, maxOf(1, past.size + 1))} of ${c.days}",
                            fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Text("Available Challenges", fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 22.dp, bottom = 4.dp))
        chalTypes.forEach { (type, title, desc) ->
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable {
                    joining = type
                    param = when (type) { "late" -> 22; "early" -> 7; "week" -> 20; else -> 0 }
                    days = 21
                    shareToGroup = false
                }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
        Spacer(Modifier.height(30.dp))
    }

    joining?.let { type ->
        AlertDialog(
            onDismissRequest = { joining = null },
            title = { Text(chalTypes.find { it.first == type }?.second ?: "Join Challenge", fontWeight = FontWeight.Bold) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    when (type) {
                        "late" -> Stepper("No screen time after", param, "h (24h)", 18, 23, 1) { param = it }
                        "early" -> Stepper("Start studying before", param, "h (24h)", 5, 10, 1) { param = it }
                        "week" -> {
                            Text("Total Study Hours Target", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(onClick = { param = maxOf(1, param - 5) }) { Text("-5h") }
                                Spacer(Modifier.width(6.dp))
                                OutlinedButton(onClick = { param = maxOf(1, param - 1) }) { Text("-1h") }
                                Text("  ${param}h  ", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 6.dp))
                                OutlinedButton(onClick = { param = minOf(120, param + 1) }) { Text("+1h") }
                                Spacer(Modifier.width(6.dp))
                                OutlinedButton(onClick = { param = minOf(120, param + 5) }) { Text("+5h") }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text("Duration:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(Modifier.padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(7, 14, 21, 30).forEach { d ->
                            FilterChip(
                                selected = days == d,
                                onClick = { days = d },
                                label = { Text("$d days") }
                            )
                        }
                    }

                    if (groups.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = shareToGroup, onCheckedChange = { shareToGroup = it })
                            Text("Take on challenge with a Squad", fontSize = 13.sp)
                        }
                        if (shareToGroup) {
                            Text("Select Squad:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(Modifier.horizontalScroll(rememberScrollState()).padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                groups.forEach { grp ->
                                    FilterChip(
                                        selected = selectedGroupId == grp.id,
                                        onClick = { selectedGroupId = grp.id },
                                        label = { Text(grp.name) }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val chalData = mapOf<String, Any>(
                        "type" to type,
                        "param" to param.toLong(),
                        "start" to dateKey(),
                        "days" to days.toLong()
                    )
                    col.add(chalData)
                    if (shareToGroup && selectedGroupId.isNotBlank()) {
                        db.collection("groups").document(selectedGroupId).collection("challenges").add(chalData)
                    }
                    joining = null
                }) { Text("Start Challenge", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { joining = null }) { Text("Cancel") } }
        )
    }
}

// ---------- Rankings ----------

data class RankRow(val id: String, val name: String, val secs: Long, val members: Int = 0)

fun weekKey(k: String): String {
    val c = Calendar.getInstance().apply { firstDayOfWeek = Calendar.MONDAY; minimalDaysInFirstWeek = 4; timeInMillis = parseKey(k) }
    return "%d-W%02d".format(c.weekYear, c.get(Calendar.WEEK_OF_YEAR))
}
fun monthKeyBack(b: Int): String =
    SimpleDateFormat("yyyy-MM", Locale.US).format(Calendar.getInstance().apply { add(Calendar.MONTH, -b) }.time)

@Composable
fun RankingsScreen(uid: String, myGroups: List<Group>, onBack: () -> Unit) {
    var period by remember { mutableStateOf("day") }   // day, week, month, all
    var groupMode by remember { mutableStateOf(false) }
    var back by remember { mutableStateOf(0) }
    var rows by remember { mutableStateOf(listOf<RankRow>()) }
    val accent = MaterialTheme.colorScheme.primary
    val soft = MaterialTheme.colorScheme.surfaceVariant
    val key = when (period) {
        "day" -> keyBack(back)
        "week" -> weekKey(keyBack(back * 7))
        else -> monthKeyBack(back)
    }
    val label = when (period) {
        "day" -> dfmt(back)
        "week" -> if (back == 0) "This week" else if (back == 1) "Last week" else key
        "month" -> if (back == 0) "This month" else key
        else -> "All time"
    }
    val byGroups = groupMode && period != "all"

    DisposableEffect(period, byGroups, key) {
        val db = FirebaseFirestore.getInstance()
        val reg = if (period == "all") {
            db.collection("rank").document("all").collection("entries").orderBy("secs", Query.Direction.DESCENDING).limit(50).addSnapshotListener { snap, _ ->
                rows = snap?.documents?.map { RankRow(it.id, it.getString("name") ?: "Friend", it.getLong("secs") ?: 0L) } ?: emptyList()
            }
        } else if (!byGroups) {
            db.collection("rank").document("${period}_$key").collection("entries")
                .orderBy("secs", Query.Direction.DESCENDING).limit(50).addSnapshotListener { snap, _ ->
                    rows = snap?.documents?.map { RankRow(it.id, it.getString("name") ?: "Friend", it.getLong("secs") ?: 0L) }
                        ?.filter { it.secs > 0 } ?: emptyList()
                }
        } else {
            db.collection("rank").document("g${period}_$key").collection("entries").limit(500).addSnapshotListener { snap, _ ->
                rows = snap?.documents?.groupBy { it.getString("gid") ?: "" }?.map { (gid, docs) ->
                    RankRow(gid, docs.firstOrNull()?.getString("gname") ?: "Group", docs.sumOf { it.getLong("secs") ?: 0L },
                        docs.count { (it.getLong("secs") ?: 0L) > 0 })
                }?.filter { it.secs > 0 }?.sortedByDescending { it.secs }?.take(50) ?: emptyList()
            }
        }
        onDispose { reg.remove() }
    }

    val mine = setOf(uid) + myGroups.map { it.id }
    val myRank = rows.indexOfFirst { it.id in mine }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("Rankings", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Sessions over 9 hours and days over 20 hours are not counted.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Row(Modifier.padding(top = 12.dp).horizontalScroll(rememberScrollState())) {
            listOf("day" to "Daily", "week" to "Weekly", "month" to "Monthly", "all" to "All time").forEach { (p, name) ->
                Text(name, Modifier.padding(end = 8.dp).clip(RoundedCornerShape(16.dp))
                    .background(if (period == p) accent else soft).clickable { period = p; back = 0 }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                    color = if (period == p) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
            }
        }
        if (period != "all") Row(Modifier.padding(top = 8.dp)) {
            listOf(false to "👤 People", true to "👥 Groups").forEach { (g, name) ->
                Text(name, Modifier.padding(end = 8.dp).clip(RoundedCornerShape(16.dp))
                    .background(if (groupMode == g) accent else soft).clickable { groupMode = g }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                    color = if (groupMode == g) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
            }
        }
        if (period != "all") Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            TextButton(onClick = { back = minOf(30, back + 1) }) { Text("›", fontSize = 22.sp) }
            Text(label, fontSize = 17.sp, fontWeight = FontWeight.Medium)
            TextButton(onClick = { back = maxOf(0, back - 1) }, enabled = back > 0) { Text("›", fontSize = 22.sp) }
        }

        Text(if (myRank >= 0) "Your ${if (byGroups) "best group" else "rank"}: #${myRank + 1}" else "You're not ranked here yet.",
            color = accent, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))

        if (rows.isEmpty()) Text("No one has studied in this period yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        else {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                listOf(1, 0, 2).forEach { i ->
                    val r = rows.getOrNull(i)
                    Column(Modifier.weight(1f).padding(4.dp).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                        .padding(vertical = if (i == 0) 22.dp else 12.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(listOf("🥇", "🥈", "🥉")[i], fontSize = if (i == 0) 34.sp else 26.sp)
                        Text(r?.name ?: "—", maxLines = 1, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text(if (r != null) hm(r.secs) else "", color = accent, fontSize = 12.sp)
                        if (r != null && byGroups) Text("${r.members} active", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            rows.forEachIndexed { i, r ->
                if (i >= 3) Row(Modifier.fillMaxWidth().padding(top = 6.dp)
                    .background(if (r.id in mine) accent.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${i + 1}", Modifier.width(32.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(r.name + if (r.id == uid) " (you)" else "", Modifier.weight(1f), maxLines = 1)
                    Text(hm(r.secs))
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

// ---------- Group chat ----------

data class Msg(val id: String, val uid: String, val name: String, val text: String, val at: Long)

@Composable
fun ChatScreen(uid: String, group: Group?, onRead: () -> Unit, onBack: () -> Unit) {
    if (group == null) { LaunchedEffect(Unit) { onBack() }; return }
    val col = FirebaseFirestore.getInstance().collection("groups").document(group.id).collection("messages")
    var msgs by remember { mutableStateOf(listOf<Msg>()) }
    var text by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<Msg?>(null) }
    val listState = rememberLazyListState()
    val ctx = LocalContext.current
    val accent = MaterialTheme.colorScheme.primary
    val soft = MaterialTheme.colorScheme.surfaceVariant
    val onAccent = MaterialTheme.colorScheme.onPrimary
    val leader = group.admin == uid

    DisposableEffect(group.id) {
        val reg = col.orderBy("at", Query.Direction.ASCENDING).limitToLast(100).addSnapshotListener { snap, _ ->
            msgs = snap?.documents?.map {
                Msg(it.id, it.getString("uid") ?: "", it.getString("name") ?: "Friend", it.getString("text") ?: "", it.getLong("at") ?: 0L)
            } ?: emptyList()
        }
        onDispose { reg.remove() }
    }
    LaunchedEffect(msgs.size) { onRead(); if (msgs.isNotEmpty()) listState.animateScrollToItem(msgs.size - 1) }

    Column(Modifier.fillMaxSize().statusBarsPadding().imePadding().padding(horizontal = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Back") }
            Column {
                Text(group.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${group.members.size} members" + if (leader) " • tap a message to delete it" else "", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (msgs.isEmpty()) item { Text("No messages yet. Say hi 👋", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp)) }
            items(msgs, key = { it.id }) { m ->
                val mine = m.uid == uid
                Column(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalAlignment = if (mine) Alignment.End else Alignment.Start) {
                    if (!mine) Text(m.name, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 6.dp))
                    Column(Modifier.widthIn(max = 280.dp).clip(RoundedCornerShape(16.dp))
                        .background(if (mine) accent else soft)
                        .clickable { selected = m }
                        .padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(m.text, color = if (mine) onAccent else MaterialTheme.colorScheme.onSurface)
                        Text(tfmt(m.at), fontSize = 10.sp, color = if (mine) onAccent.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.End))
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(text, { text = it.take(500) }, placeholder = { Text("Message") }, maxLines = 3, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val t = text.trim()
                if (t.isNotEmpty()) {
                    col.add(mapOf<String, Any>("uid" to uid, "name" to myName(), "text" to t, "at" to System.currentTimeMillis()))
                    text = ""
                }
            }, enabled = text.isNotBlank()) { Text("Send") }
        }
    }
    selected?.let { m ->
        val mine = m.uid == uid
        val canDelete = mine || leader
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(if (canDelete) "Message Options" else "Report Message") },
            text = { Text("\"${m.text}\"\n— ${m.name}") },
            confirmButton = {
                if (canDelete) {
                    TextButton(onClick = { col.document(m.id).delete(); selected = null }) {
                        Text("Delete Message", color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    TextButton(onClick = {
                        FirebaseFirestore.getInstance().collection("reports").add(mapOf(
                            "reportedUid" to m.uid,
                            "reportedName" to m.name,
                            "message" to m.text,
                            "byUid" to uid,
                            "groupId" to group.id,
                            "at" to System.currentTimeMillis()
                        ))
                        Toast.makeText(ctx, "Report submitted. Thank you for keeping StudyReward safe!", Toast.LENGTH_LONG).show()
                        selected = null
                    }) {
                        Text("🚨 Report User / Content", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = { TextButton(onClick = { selected = null }) { Text("Cancel") } }
        )
    }
}

// ---------- Welcome tour ----------

@Composable
fun OnboardingScreen(onFinish: (String) -> Unit) {
    var page by remember { mutableStateOf(0) }
    var nick by remember { mutableStateOf("") }
    val accent = MaterialTheme.colorScheme.primary
    val pages = listOf(
        Triple(Icons.Rounded.Home, "Study. Earn. Spend wisely.", "StudyReward turns the time you study into screen time you have actually earned."),
        Triple(Icons.Rounded.TaskAlt, "How you earn", "Run a subject timer, do physical tasks like workouts or chores, and reach your daily goal to build a streak. Everything resets each day."),
        Triple(Icons.Rounded.Groups, "Better with friends", "Join a group to see who is studying, chat, and ask the group's shared pool for extra screen time. Your friends vote on it."),
        Triple(Icons.Rounded.Person, "Before you start", "Study and task time is self-reported: the app cannot check that you are really studying, and it does not block apps yet. " +
                "Group members see your nickname and whether you are studying. You only appear in public rankings if you switch that on in Settings.")
    )
    Dialog(onDismissRequest = {}, properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = false, dismissOnClickOutside = false)) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth(), Arrangement.End) {
                    if (page < 3) TextButton(onClick = { page = 3 }) { Text("Skip") } else Spacer(Modifier.height(48.dp))
                }
                Spacer(Modifier.weight(1f))
                val (icon, title, body) = pages[page]
                Box(Modifier.size(96.dp).clip(CircleShape).background(accent.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = accent)
                }
                Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 28.dp))
                Text(body, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
                if (page == 3) OutlinedTextField(nick, { nick = it.take(20) }, label = { Text("Nickname (optional)") },
                    singleLine = true, modifier = Modifier.padding(top = 20.dp).fillMaxWidth())
                Spacer(Modifier.weight(1f))
                Row {
                    repeat(4) { i ->
                        Box(Modifier.padding(4.dp).size(if (i == page) 10.dp else 8.dp).clip(CircleShape)
                            .background(if (i == page) accent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)))
                    }
                }
                Button(onClick = { if (page < 3) page++ else onFinish(nick.trim()) }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text(if (page < 3) "Next" else "Get started")
                }
            }
        }
    }
}

// ---------- Scenic backgrounds (painted with code, no image files) ----------

@Composable
fun SceneBackground(scene: String, bg: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        fun ridge(baseY: Float, amp: Float, step: Float, color: Color, seed: Float) {
            val path = Path()
            path.moveTo(0f, h)
            path.lineTo(0f, baseY)
            var x = 0f
            while (x <= w + step) {
                path.lineTo(x, baseY - amp * (0.5f + 0.5f * sin(x / step * 1.7f + seed)))
                x += step / 3
            }
            path.lineTo(w, h)
            path.close()
            drawPath(path, color)
        }
        fun sky(top: Color, mid: Color, bottom: Color) {
            drawRect(Brush.verticalGradient(listOf(top, mid, bottom), 0f, h * 0.7f))
        }
        when (scene) {
            "lavender" -> {
                sky(Color(0xFFEDE7F6), Color(0xFFD1C4E9), Color(0xFFB39DDB))
                // Soft glowing crescent moon
                drawCircle(Color(0xFFFFF9C4), h * 0.05f, Offset(w * 0.82f, h * 0.14f))
                drawCircle(Color(0xFFEDE7F6), h * 0.042f, Offset(w * 0.80f, h * 0.13f))
                // Fluffy pastel clouds
                drawOval(Color(0x77FFFFFF), Offset(w * 0.05f, h * 0.45f), Size(w * 0.55f, h * 0.12f))
                drawOval(Color(0x99FFFFFF), Offset(w * 0.15f, h * 0.42f), Size(w * 0.42f, h * 0.15f))
                drawOval(Color(0x77FFFFFF), Offset(w * 0.50f, h * 0.70f), Size(w * 0.60f, h * 0.14f))
                drawOval(Color(0x99FFFFFF), Offset(w * 0.60f, h * 0.66f), Size(w * 0.45f, h * 0.17f))
                // Twinkling stars
                val rand = kotlin.random.Random(19)
                repeat(40) {
                    drawCircle(Color(0xAAFFFFFF), 1.5f + rand.nextFloat() * 2f, Offset(rand.nextFloat() * w, rand.nextFloat() * h * 0.8f))
                }
            }
            "mochi_pink" -> {
                sky(Color(0xFFFFF0F5), Color(0xFFFCE4EC), Color(0xFFF8BBD0))
                // Soft ambient sakura petals
                val rand = kotlin.random.Random(33)
                repeat(35) {
                    val px = rand.nextFloat() * w
                    val py = rand.nextFloat() * h
                    drawOval(Color(0x99FF80AB), Offset(px, py), Size(12f, 7f))
                }
                // Dreamy soft glowing sun
                drawCircle(Color(0x44FF4081), h * 0.10f, Offset(w * 0.20f, h * 0.18f))
                drawCircle(Color(0x66FFE4EC), h * 0.06f, Offset(w * 0.20f, h * 0.18f))
            }
            "matcha" -> {
                sky(Color(0xFFF1F8E9), Color(0xFFDCEDC8), Color(0xFFC5E1A5))
                // Zen bamboo vertical stalk silhouettes
                listOf(w * 0.12f, w * 0.24f, w * 0.82f, w * 0.92f).forEachIndexed { i, bx ->
                    drawRect(Color(0x334E9B56), Offset(bx, 0f), Size(14f, h))
                    for (node in 1..8) {
                        drawLine(Color(0x552E7D32), Offset(bx - 3f, node * h / 9f), Offset(bx + 17f, node * h / 9f), strokeWidth = 3f)
                    }
                }
                // Floating tea leaves
                val rand = kotlin.random.Random(55)
                repeat(25) {
                    drawOval(Color(0x66689F38), Offset(rand.nextFloat() * w, rand.nextFloat() * h), Size(14f, 6f))
                }
            }
            "peach" -> {
                sky(Color(0xFFFFF3E0), Color(0xFFFFE0B2), Color(0xFFFFCC80))
                // Warm glowing citrus sun in corner
                drawCircle(Color(0x55FF9800), h * 0.14f, Offset(w * 0.88f, h * 0.12f))
                drawCircle(Color(0x77FFE0B2), h * 0.08f, Offset(w * 0.88f, h * 0.12f))
                // Effervescent soda bubbles rising
                val rand = kotlin.random.Random(77)
                repeat(45) {
                    val bx = rand.nextFloat() * w
                    val by = rand.nextFloat() * h
                    val bSize = 4f + rand.nextFloat() * 10f
                    drawCircle(Color(0x88FFFFFF), bSize, Offset(bx, by), style = Stroke(width = 2f))
                    drawCircle(Color(0xAAFFFFFF), bSize * 0.3f, Offset(bx - bSize * 0.3f, by - bSize * 0.3f))
                }
            }
            "sky_pudding" -> {
                sky(Color(0xFFE1F5FE), Color(0xFFB3E5FC), Color(0xFF81D4FA))
                // Gentle pastel rainbow arc across top
                val rainbowColors = listOf(Color(0x44FF8A80), Color(0x44FFD180), Color(0x44FFFF8D), Color(0x44B9F6CA), Color(0x4480D8FF), Color(0x44B388FF))
                rainbowColors.forEachIndexed { i, c ->
                    drawCircle(c, w * 0.85f + i * 14f, Offset(w * 0.5f, h * 0.12f), style = Stroke(width = 12f))
                }
                // Giant fluffy cumulus anime clouds
                drawOval(Color(0xAAFFFFFF), Offset(-w * 0.1f, h * 0.65f), Size(w * 0.65f, h * 0.22f))
                drawOval(Color(0xCCFFFFFF), Offset(0f, h * 0.60f), Size(w * 0.50f, h * 0.25f))
                drawOval(Color(0xAAFFFFFF), Offset(w * 0.45f, h * 0.70f), Size(w * 0.65f, h * 0.22f))
                drawOval(Color(0xCCFFFFFF), Offset(w * 0.55f, h * 0.64f), Size(w * 0.50f, h * 0.25f))
            }
            "bubblegum" -> {
                sky(Color(0xFFFCE4EC), Color(0xFFF8BBD0), Color(0xFFE1BEE7))
                // 90s Memphis aesthetic confetti
                val rand = kotlin.random.Random(88)
                repeat(30) {
                    val px = rand.nextFloat() * w
                    val py = rand.nextFloat() * h
                    if (it % 2 == 0) drawCircle(Color(0x66E040FB), 5f + rand.nextFloat() * 6f, Offset(px, py))
                    else drawRect(Color(0x66FF4081), Offset(px, py), Size(12f, 12f))
                }
            }
            "starry_plush" -> {
                sky(Color(0xFF0A0E27), Color(0xFF161B46), Color(0xFF2C3175))
                // Glowing constellation stars and lines
                val rand = kotlin.random.Random(99)
                val stars = (0..50).map { Offset(rand.nextFloat() * w, rand.nextFloat() * h * 0.75f) }
                stars.forEach {
                    drawCircle(Color.White.copy(alpha = 0.8f), 2f + rand.nextFloat() * 2f, it)
                }
                for (i in 0 until stars.size - 1 step 3) {
                    drawLine(Color(0x338C9EFF), stars[i], stars[i + 1], strokeWidth = 1.5f)
                }
                // Bright soft moon
                drawCircle(Color(0xFFFFEE58), h * 0.05f, Offset(w * 0.82f, h * 0.14f))
            }
            "cocoa" -> {
                sky(Color(0xFFEFEBE9), Color(0xFFD7CCC8), Color(0xFFBCAAA4))
                // Latte art heart and warm café wave
                ridge(h * 0.72f, h * 0.08f, w / 2.5f, Color(0x448D6E63), 1f)
                ridge(h * 0.82f, h * 0.06f, w / 3.0f, Color(0x666D4C41), 3f)
                drawOval(Color(0x446D4C41), Offset(w * 0.35f, h * 0.25f), Size(w * 0.30f, h * 0.10f))
            }
            "mountains" -> {
                sky(Color(0xFF16323F), Color(0xFF3D6B7D), Color(0xFF7FA9B3))
                drawCircle(Color(0x44FFFFFF), h * 0.06f, Offset(w * 0.75f, h * 0.17f))
                ridge(h * 0.52f, h * 0.16f, w / 2.5f, Color(0xFF2E5568), 0f)
                ridge(h * 0.64f, h * 0.14f, w / 3.2f, Color(0xFF1F4052), 2f)
                ridge(h * 0.78f, h * 0.10f, w / 4f, Color(0xFF12303D), 4f)
            }
            "sunset" -> {
                sky(Color(0xFF2B1B3D), Color(0xFFB84A6E), Color(0xFFD98A5F))
                drawCircle(Color(0xFFFFD9A0), h * 0.08f, Offset(w * 0.5f, h * 0.5f))
                drawRect(Color(0xFF1B2646), Offset(0f, h * 0.56f), Size(w, h * 0.44f))
                for (i in 0..6) drawRect(Color(0x66FFD9A0), Offset(w * (0.5f - 0.2f / (i + 1)), h * (0.58f + i * 0.035f)),
                    Size(w * 0.4f / (i + 1), h * 0.006f))
            }
            "night" -> {
                sky(Color(0xFF050816), Color(0xFF1B2250), Color(0xFF3B3F86))
                val r = kotlin.random.Random(7)
                repeat(80) {
                    drawCircle(Color.White.copy(alpha = 0.3f + r.nextFloat() * 0.6f), 1f + r.nextFloat() * 2.5f,
                        Offset(r.nextFloat() * w, r.nextFloat() * h * 0.65f))
                }
                drawCircle(Color(0xFFEFEAD6), h * 0.045f, Offset(w * 0.78f, h * 0.14f))
                ridge(h * 0.8f, h * 0.08f, w / 3f, Color(0xFF0B0E26), 1f)
            }
            "ocean" -> {
                sky(Color(0xFF0B3A4A), Color(0xFF2F8FA0), Color(0xFF58A9B3))
                drawCircle(Color(0x55FFFFFF), h * 0.07f, Offset(w * 0.3f, h * 0.35f))
                listOf(Color(0xFF1B7C8C), Color(0xFF136577), Color(0xFF0D4E5F), Color(0xFF083A49)).forEachIndexed { i, c ->
                    ridge(h * (0.52f + i * 0.11f), h * 0.025f, w / 2.2f, c, i * 1.3f)
                }
            }
            "forest" -> {
                sky(Color(0xFF0C2A1E), Color(0xFF2E6B47), Color(0xFF5E9770))
                val greens = listOf(Color(0xFF2A5A3B), Color(0xFF1C4129), Color(0xFF0F2A1B))
                for (layer in 0..2) {
                    val base = h * (0.62f + layer * 0.14f)
                    var x = -20f
                    var k = 0
                    while (x < w + 40f) {
                        val th = h * (0.14f + 0.05f * sin(k * 1.7f + layer))
                        val tw = w / (7f - layer)
                        val tree = Path()
                        tree.moveTo(x, base)
                        tree.lineTo(x + tw / 2, base - th)
                        tree.lineTo(x + tw, base)
                        tree.close()
                        drawPath(tree, greens[layer])
                        drawRect(greens[layer], Offset(x, base - 2f), Size(tw, h - base + 2f))
                        x += tw * 0.7f
                        k++
                    }
                }
            }
            "rain" -> {
                sky(Color(0xFF10141B), Color(0xFF232C3A), Color(0xFF3B4960))
                val r = kotlin.random.Random(11)
                repeat(14) {
                    drawCircle(Color(0xFFFFC27A).copy(alpha = 0.10f + r.nextFloat() * 0.12f), h * (0.02f + r.nextFloat() * 0.04f),
                        Offset(r.nextFloat() * w, h * (0.55f + r.nextFloat() * 0.3f)))
                }
                repeat(90) {
                    val x = r.nextFloat() * w
                    val y = r.nextFloat() * h
                    drawLine(Color.White.copy(alpha = 0.18f), Offset(x, y), Offset(x - 6f, y + 28f), strokeWidth = 2f)
                }
            }
        }
        drawRect(bg.copy(alpha = if (scene in listOf("lavender", "mochi_pink", "matcha", "peach", "sky_pudding", "bubblegum", "cocoa")) 0.18f else 0.60f))
    }
}

// ---------- App blocking ----------

const val YT = "com.google.android.youtube"

// In Full edition, YouTube is handled separately with watch-page checking.
// In Friends edition, YouTube is added to the blocked list by default.
val DEFAULT_DISTRACTING = setOf(
    "com.instagram.android", "com.facebook.katana", "com.snapchat.android",
    "com.zhiliaoapp.musically", "com.ss.android.ugc.trill", "com.twitter.android", "com.reddit.frontpage",
    "com.netflix.mediaclient", "tv.twitch.android.app", "com.pinterest", "in.mohalla.sharechat",
    "in.startv.hotstar", "com.amazon.avod.thirdpartyclient")

val DEFAULT_STUDY_CHANNELS = setOf(
    "Khan Academy", "Physics Wallah", "Unacademy", "Vedantu", "3Blue1Brown", "CrashCourse", "NPTEL", "MIT OpenCourseWare",
    "TED-Ed", "Organic Chemistry Tutor", "Neso Academy", "Gate Smashers", "freeCodeCamp", "Apna College", "CodeWithHarry")

val DEFAULT_STUDY_WORDS = setOf(
    "lecture", "tutorial", "course", "chapter", "syllabus", "revision", "exam", "jee", "neet", "upsc", "gate", "physics",
    "chemistry", "maths", "mathematics", "biology", "calculus", "algebra", "geometry", "mechanics", "thermodynamics",
    "engineering", "programming", "coding", "python", "data structures", "algorithm", "machine learning", "economics",
    "accounting", "ncert", "board exam", "class 11", "class 12", "numerical", "pyq", "previous year", "study with me")

data class AppInfo(val pkg: String, val label: String, val icon: ImageBitmap?)

fun isFull(ctx: Context): Boolean = try {
    ctx.packageManager.getServiceInfo(
        ComponentName(ctx, BlockerService::class.java), 0
    ) != null
} catch (e: Exception) {
    false
}

fun usageAccessOk(ctx: Context): Boolean {
    val appOps = ctx.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
    val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), ctx.packageName)
    } else {
        @Suppress("DEPRECATION")
        appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), ctx.packageName)
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

fun overlayOk(ctx: Context): Boolean =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        AndroidSettings.canDrawOverlays(ctx)
    } else true

fun loadApps(ctx: Context): List<AppInfo> {
    val pm = ctx.packageManager
    val i = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    val hideYt = isFull(ctx)
    return pm.queryIntentActivities(i, 0).map { ri ->
        AppInfo(ri.activityInfo.packageName, ri.loadLabel(pm).toString(),
            try { ri.loadIcon(pm).toBitmap(72, 72).asImageBitmap() } catch (e: Exception) { null })
    }.filter { it.pkg != ctx.packageName && it.pkg != "com.android.settings" && (!hideYt || it.pkg != YT) }
        .distinctBy { it.pkg }.sortedBy { it.label.lowercase() }
}

fun accessibilityOn(ctx: Context): Boolean {
    val v = AndroidSettings.Secure.getString(ctx.contentResolver, AndroidSettings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
    return v.contains(ctx.packageName + "/")
}

fun batteryOk(ctx: Context): Boolean =
    (ctx.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(ctx.packageName)

fun openBatteryDialog(ctx: Context) {
    try {
        ctx.startActivity(Intent(AndroidSettings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + ctx.packageName)))
    } catch (e: Exception) {
        try { ctx.startActivity(Intent(AndroidSettings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) } catch (e2: Exception) { }
    }
}

object Blocker {
    // Looks through the screen's elements (limited, so it stays fast). Return true from [test] to stop early.
    fun anyNode(root: AccessibilityNodeInfo?, limit: Int = 500, test: (AccessibilityNodeInfo) -> Boolean): Boolean {
        if (root == null) return false
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        var seen = 0
        while (queue.isNotEmpty() && seen < limit) {
            val n = queue.removeFirst()
            seen++
            if (test(n)) return true
            for (i in 0 until n.childCount) n.getChild(i)?.let { queue.add(it) }
        }
        return false
    }

    // Best effort: YouTube Shorts detection
    fun onShorts(root: AccessibilityNodeInfo?): Boolean = anyNode(root) { n ->
        val id = n.viewIdResourceName?.lowercase() ?: ""
        id.contains("reel_player") || id.contains("reel_recycler") || id.contains("shorts_player") || id.contains("reel_watch")
    }

    // Best effort: Instagram Reels detection
    fun onReels(root: AccessibilityNodeInfo?): Boolean = anyNode(root) { n ->
        val id = n.viewIdResourceName?.lowercase() ?: ""
        val cd = n.contentDescription?.toString()?.lowercase() ?: ""
        val text = n.text?.toString()?.lowercase() ?: ""
        id.contains("clips_viewer") || id.contains("clips_video_container") ||
        id.contains("reel_viewer") || id.contains("reels_viewer") || id.contains("reels_tab") ||
        id.contains("clips_action_bar") ||
        cd.startsWith("reels video") || cd.contains("reel by") || cd.startsWith("reel, ") ||
        text.contains("reels audio")
    }

    // A video page: the player, or the Like / Dislike / Subscribe row under it
    fun onWatchPage(root: AccessibilityNodeInfo?): Boolean = anyNode(root) { n ->
        val id = n.viewIdResourceName?.lowercase() ?: ""
        val t = ((n.text?.toString() ?: "") + " " + (n.contentDescription?.toString() ?: "")).lowercase()
        ((id.contains("watch_player") || id.contains("player_view") || id.contains("watch_while") || id.contains("watch_panel")) && !id.contains("mini")) ||
                t.contains("enter fullscreen") || t.contains("exit fullscreen")
    }

    // The words in the title and channel area (top part of the screen), or null if the page cannot be read yet
    fun videoText(ctx: Context, root: AccessibilityNodeInfo?): String? {
        if (root == null || !onWatchPage(root)) return null
        val limitY = ctx.resources.displayMetrics.heightPixels * 0.62
        val parts = mutableListOf<String>()
        val rect = Rect()
        anyNode(root, 600) { n ->
            n.getBoundsInScreen(rect)
            if (rect.top < limitY) {
                n.text?.toString()?.let { if (it.length > 2) parts.add(it) }
                n.contentDescription?.toString()?.let { if (it.length > 2) parts.add(it) }
            }
            false
        }
        return if (parts.size < 3) null else parts.joinToString(" ").lowercase()
    }

    fun isStudy(p: android.content.SharedPreferences, text: String): Boolean {
        val channels = p.getStringSet("blk_channels", null) ?: DEFAULT_STUDY_CHANNELS
        val words = p.getStringSet("blk_words", null) ?: DEFAULT_STUDY_WORDS
        return channels.any { text.contains(it.lowercase()) } || words.any { text.contains(it.lowercase()) }
    }

    fun isGame(ctx: Context, pkg: String): Boolean =
        try { ctx.packageManager.getApplicationInfo(pkg, 0).category == ApplicationInfo.CATEGORY_GAME } catch (e: Exception) { false }

    // null = allowed. "locked" = app is locked. "shorts" = Shorts. "yt" = YouTube: check the video that is playing.
    fun check(ctx: Context, pkg: String, root: AccessibilityNodeInfo?): String? {
        val p = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        if (!p.getBoolean("blk_on", false) || pkg == ctx.packageName || pkg == "com.android.settings") return null
        val now = System.currentTimeMillis()
        val examUntil = p.getLong("blk_exam_until", 0L)
        if (now < examUntil) {
            val set = p.getStringSet("blk_apps", null) ?: (if (isFull(ctx)) DEFAULT_DISTRACTING else (DEFAULT_DISTRACTING + YT))
            val game = p.getBoolean("blk_games", true) && isGame(ctx, pkg)
            if (pkg in set || game) return "exam"
        }
        if (pkg == "com.instagram.android" && p.getBoolean("blk_reels", true) && onReels(root)) {
            return "reels"
        }
        if (p.getString("blk_off_key", "") == dateKey()) return null           // day off: no limits
        if (now < p.getLong("blk_override", 0L)) return null                   // emergency unlock
        if (now < p.getLong("blk_use_until", 0L)) {
            if (isFull(ctx) && pkg == YT && onShorts(root)) return "shorts"
            return null                  // Use timer is running
        }
        if (isFull(ctx)) {
            if (pkg == YT) {
                if (onShorts(root)) return "shorts"
                return "yt"
            }
        } else {
            if (pkg == YT) return "locked"
        }
        val set = p.getStringSet("blk_apps", null) ?: (if (isFull(ctx)) DEFAULT_DISTRACTING else (DEFAULT_DISTRACTING + YT))
        val game = p.getBoolean("blk_games", true) && isGame(ctx, pkg)
        return if (pkg in set || game) "locked" else null
    }

    // For troubleshooting: remembers which element names and texts YouTube is showing
    fun debug(ctx: Context, root: AccessibilityNodeInfo?) {
        if (root == null) return
        val ids = linkedSetOf<String>()
        val texts = linkedSetOf<String>()
        anyNode(root, 600) { n ->
            n.viewIdResourceName?.let { ids.add(it.substringAfter(":id/")) }
            n.text?.toString()?.let { if (it.length in 3..60) texts.add(it) }
            false
        }
        val out = "shorts=${onShorts(root)} watchPage=${onWatchPage(root)}\nIDS: " + ids.take(60).joinToString(", ") +
                "\nTEXT: " + texts.take(30).joinToString(" | ")
        ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putString("blk_dbg", out.take(3500)).apply()
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            if (prefs.getBoolean("blk_on", false) && !isFull(context)) {
                UsageBlockService.start(context)
            }
        }
    }
}

class UsageBlockService : Service() {
    private val scope = kotlinx.coroutines.CoroutineScope(Dispatchers.Default + kotlinx.coroutines.Job())
    private var job: kotlinx.coroutines.Job? = null
    private var lastBlockedPkg = ""
    private var lastBlockedTime = 0L

    companion object {
        const val CHANNEL_ID = "study_blocker_fgs"
        const val NOTIF_ID = 2001

        fun start(ctx: Context) {
            val i = Intent(ctx, UsageBlockService::class.java)
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    ctx.startForegroundService(i)
                } else {
                    ctx.startService(i)
                }
            } catch (e: Exception) { }
        }

        fun stop(ctx: Context) {
            try { ctx.stopService(Intent(ctx, UsageBlockService::class.java)) } catch (e: Exception) { }
        }
    }

    override fun onBind(intent: Intent?): android.os.IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notif = createNotification()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIF_ID, notif, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIF_ID, notif)
        }
        startPolling()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(CHANNEL_ID, "StudyReward Blocker", NotificationManager.IMPORTANCE_LOW).apply {
                    description = "Locks distracting apps while you study"
                    setShowBadge(false)
                }
                nm.createNotificationChannel(channel)
            }
        }
    }

    private fun createNotification(): Notification {
        val pi = PendingIntent.getActivity(this, 0,
            packageManager.getLaunchIntentForPackage(packageName) ?: Intent(),
            PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("StudyReward Protection Active")
            .setContentText("Distracting apps stay locked while you focus.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pi)
            .build()
    }

    private fun startPolling() {
        job?.cancel()
        job = scope.launch {
            while (true) {
                val p = getSharedPreferences("prefs", MODE_PRIVATE)
                if (!p.getBoolean("blk_on", false)) {
                    stopSelf()
                    break
                }
                val fg = getForegroundPackage()
                if (fg != null && fg != packageName && fg != "com.android.settings") {
                    val status = Blocker.check(this@UsageBlockService, fg, null)
                    if (status == "locked") {
                        val now = System.currentTimeMillis()
                        if (fg != lastBlockedPkg || now - lastBlockedTime > 1500) {
                            lastBlockedPkg = fg
                            lastBlockedTime = now
                            val lockIntent = Intent(this@UsageBlockService, BlockActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                putExtra("pkg", fg)
                                putExtra("why", "locked")
                            }
                            try {
                                startActivity(lockIntent)
                            } catch (e: Exception) { }
                        }
                    }
                }
                delay(700)
            }
        }
    }

    private fun getForegroundPackage(): String? {
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return null
        val now = System.currentTimeMillis()
        // Query last 15 seconds to ensure we catch batched events on Vivo / aggressive OEM OS
        val events = usm.queryEvents(now - 15_000, now)
        val event = UsageEvents.Event()
        var lastPkg: String? = null
        var lastTs = 0L
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED && event.timeStamp >= lastTs) {
                lastPkg = event.packageName
                lastTs = event.timeStamp
            }
        }
        if (lastPkg == null) {
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 30_000, now)
            lastPkg = stats?.maxByOrNull { it.lastTimeUsed }?.packageName
        }
        return lastPkg
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val p = getSharedPreferences("prefs", MODE_PRIVATE)
        if (!p.getBoolean("blk_on", false)) {
            stopSelf()
            return START_NOT_STICKY
        }
        if (job == null || job?.isActive != true) {
            startPolling()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }
}

class BlockerService : AccessibilityService() {
    private var lastBlock = 0L
    private var lastCheck = 0L
    private var otherHits = 0
    private var nullHits = 0
    private val handler = Handler(Looper.getMainLooper())
    private var warning = false
    private var warnLeft = 0
    private var warnPkg = ""
    private var banner: TextView? = null

    private val tick = object : Runnable {
        override fun run() {
            if (warnLeft <= 0) {
                warning = false
                hideBanner()
                block(warnPkg, "notstudy", System.currentTimeMillis())
                return
            }
            showBanner("Not a study video. Closing in $warnLeft s")
            warnLeft--
            handler.postDelayed(this, 1000)
        }
    }

    private fun startWarn(pkg: String) {
        if (warning) return
        warning = true
        warnPkg = pkg
        warnLeft = 10
        handler.post(tick)
    }

    private fun cancelWarn() {
        if (!warning) return
        warning = false
        handler.removeCallbacks(tick)
        hideBanner()
    }

    // A thin red banner at the top; it never takes touches, so the video stays usable
    private fun showBanner(text: String) {
        val tv = banner ?: TextView(this).also { t ->
            t.setTextColor(android.graphics.Color.WHITE)
            t.setBackgroundColor(0xEE9C2B2B.toInt())
            t.textSize = 15f
            t.gravity = Gravity.CENTER
            t.setPadding(32, 28, 32, 28)
            val lp = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT)
            lp.gravity = Gravity.TOP
            lp.y = 120
            (getSystemService(WINDOW_SERVICE) as WindowManager).addView(t, lp)
            banner = t
        }
        tv.text = text
    }

    private fun hideBanner() {
        banner?.let { try { (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(it) } catch (e: Exception) { } }
        banner = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val e = event ?: return
        val pkg = e.packageName?.toString() ?: return
        if (pkg == packageName) return   // our own windows (lock screen, warning banner)
        val type = e.eventType
        if (type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && type != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return
        val now = System.currentTimeMillis()
        if (type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (pkg != YT || now - lastCheck < 600) return
            lastCheck = now
        }
        if (now - lastBlock < 800) return
        val root = rootInActiveWindow
        val p = getSharedPreferences("prefs", MODE_PRIVATE)
        if (pkg == YT && p.getBoolean("blk_debug", false)) Blocker.debug(this, root)
        when (Blocker.check(this, pkg, root)) {
            "locked" -> { cancelWarn(); block(pkg, "locked", now) }
            "shorts" -> { cancelWarn(); block(pkg, "shorts", now) }
            "reels" -> { cancelWarn(); block(pkg, "reels", now) }
            "exam" -> { cancelWarn(); block(pkg, "exam", now) }
            "yt" -> {
                val text = Blocker.videoText(this, root)
                if (text == null) {
                    otherHits = 0
                    nullHits++
                    if (nullHits >= 3) cancelWarn()          // left the video page
                } else {
                    nullHits = 0
                    if (Blocker.isStudy(p, text)) { otherHits = 0; cancelWarn() }
                    else { otherHits++; if (otherHits >= 2) startWarn(pkg) }   // two readings in a row, so a loading page is not flagged
                }
            }
            else -> {
                otherHits = 0
                if (pkg != "com.android.systemui") cancelWarn()
            }
        }
    }

    private fun block(pkg: String, why: String, now: Long) {
        lastBlock = now
        cancelWarn()
        performGlobalAction(GLOBAL_ACTION_HOME)
        handler.postDelayed({
            try {
                startActivity(Intent(this, BlockActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("pkg", pkg).putExtra("why", why))
            } catch (ex: Exception) {
                Toast.makeText(this, "Blocked by StudyReward", Toast.LENGTH_SHORT).show()
            }
        }, 250)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        cancelWarn()
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {}
}

class BlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pkg = intent.getStringExtra("pkg") ?: ""
        val why = intent.getStringExtra("why") ?: "locked"
        val label = try { packageManager.getApplicationLabel(packageManager.getApplicationInfo(pkg, 0)).toString() } catch (e: Exception) { pkg }
        val title = when (why) {
            "shorts" -> "Shorts are blocked"
            "reels" -> "Instagram Reels are blocked"
            "exam" -> "Exam Mode Active 🎯"
            "notstudy" -> "That doesn't look like a study video"
            else -> "$label is locked"
        }
        val body = when (why) {
            "shorts" -> "Scrolling Shorts is blocked in StudyReward. Normal videos still work."
            "reels" -> "Instagram Reels doomscrolling is blocked to protect your study focus. DMs and chats still work!"
            "exam" -> "Exam Mode is active. Distracting apps stay locked until your exam period concludes!"
            "notstudy" -> "Videos from study channels on your list, or with study words in the title, keep playing. " +
                    "If this one is for studying, add its channel in StudyReward > App blocking."
            else -> "Study to earn screen time, then press Use in StudyReward when you want to spend it."
        }
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxSize().padding(32.dp), Arrangement.Center, Alignment.CenterHorizontally) {
                        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
                        val mType = MascotType.fromId(prefs.getString("mascot_type", "mochi") ?: "mochi")
                        val mName = prefs.getString("mascot_name", "Mochi") ?: "Mochi"
                        MascotCanvas(type = mType, mood = MascotMood.TIRED, modifier = Modifier.size(96.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text("$mName says this app is locked! Earn screen time by studying.", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
                        Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 16.dp))
                        Button(onClick = {
                            startActivity(Intent(this@BlockActivity, MainActivity::class.java))
                            finish()
                        }) { Text("Open StudyReward") }
                        TextButton(onClick = { finish() }) { Text("Close") }
                    }
                }
            }
        }
    }
}

@Composable
fun BlockingScreen(canAfford: Boolean, onEmergency: () -> Boolean, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val full = isFull(ctx)
    val prefs = remember { ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE) }
    var on by remember { mutableStateOf(prefs.getBoolean("blk_on", false)) }
    var games by remember { mutableStateOf(prefs.getBoolean("blk_games", true)) }
    var debugOn by remember { mutableStateOf(prefs.getBoolean("blk_debug", false)) }
    var blocked by remember { mutableStateOf<Set<String>>(prefs.getStringSet("blk_apps", null)?.toSet() ?: (if (full) DEFAULT_DISTRACTING else (DEFAULT_DISTRACTING + YT))) }
    var reelsBlock by remember { mutableStateOf(prefs.getBoolean("blk_reels", true)) }
    var notifShield by remember { mutableStateOf(prefs.getBoolean("blk_notif_shield", false)) }
    var examUntil by remember { mutableStateOf(prefs.getLong("blk_exam_until", 0L)) }
    var channels by remember { mutableStateOf<Set<String>>(prefs.getStringSet("blk_channels", null)?.toSet() ?: DEFAULT_STUDY_CHANNELS) }
    var words by remember { mutableStateOf<Set<String>>(prefs.getStringSet("blk_words", null)?.toSet() ?: DEFAULT_STUDY_WORDS) }
    var wordsDialog by remember { mutableStateOf(false) }
    var debugTaps by remember { mutableStateOf(0) }
    var wordsText by remember { mutableStateOf("") }
    var newChannel by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    var dbg by remember { mutableStateOf(prefs.getString("blk_dbg", "") ?: "") }

    var accEnabled by remember { mutableStateOf(accessibilityOn(ctx)) }
    var usageOk by remember { mutableStateOf(usageAccessOk(ctx)) }
    var overOk by remember { mutableStateOf(overlayOk(ctx)) }
    var battery by remember { mutableStateOf(batteryOk(ctx)) }
    val ready = if (full) (accEnabled && battery && on) else (usageOk && overOk && battery && on)
    var step by remember { mutableStateOf(0) }   // guided setup: 0 idle, 1 step 1, 2 step 2, 3 battery

    LaunchedEffect(Unit) {
        while (true) {
            accEnabled = accessibilityOn(ctx)
            usageOk = usageAccessOk(ctx)
            overOk = overlayOk(ctx)
            battery = batteryOk(ctx)
            dbg = prefs.getString("blk_dbg", "") ?: ""
            delay(700)
        }
    }

    // Guided setup moves on by itself when returning from settings
    LaunchedEffect(accEnabled, usageOk, overOk, battery, step) {
        if (full) {
            if (step == 1 && accEnabled) {
                if (battery) { step = 0; on = true; prefs.edit().putBoolean("blk_on", true).apply(); msg = "All set. Blocking is on." }
                else { step = 2; openBatteryDialog(ctx) }
            } else if (step == 2 && battery) {
                step = 0; on = true; prefs.edit().putBoolean("blk_on", true).apply(); msg = "All set. Blocking is on."
            }
        } else {
            if (step == 1 && usageOk) {
                if (!overOk) {
                    step = 2
                    try { ctx.startActivity(Intent(AndroidSettings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + ctx.packageName))) } catch (e: Exception) { }
                } else if (!battery) {
                    step = 3; openBatteryDialog(ctx)
                } else {
                    step = 0; on = true; prefs.edit().putBoolean("blk_on", true).apply(); UsageBlockService.start(ctx); msg = "All set. Blocking is on."
                }
            } else if (step == 2 && overOk) {
                if (!battery) {
                    step = 3; openBatteryDialog(ctx)
                } else {
                    step = 0; on = true; prefs.edit().putBoolean("blk_on", true).apply(); UsageBlockService.start(ctx); msg = "All set. Blocking is on."
                }
            } else if (step == 3 && battery) {
                step = 0; on = true; prefs.edit().putBoolean("blk_on", true).apply(); UsageBlockService.start(ctx); msg = "All set. Blocking is on."
            }
        }
    }

    val apps by produceState(listOf<AppInfo>()) { value = withContext(Dispatchers.Default) { loadApps(ctx) } }
    val accent = MaterialTheme.colorScheme.primary
    val soft = MaterialTheme.colorScheme.surfaceVariant
    val grey = MaterialTheme.colorScheme.onSurfaceVariant

    LazyColumn(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp)) {
        item {
            TextButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("  Back")
            }
            Text("App blocking", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Apps on the list stay locked unless your Use timer is running, it is a day off, or you use an emergency unlock. " +
                    "Apps that are not on the list are never blocked.", fontSize = 13.sp, color = grey, modifier = Modifier.padding(vertical = 8.dp))

            Column(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)).padding(16.dp)) {
                Text(if (ready) "✓ Blocking is set up and on" else "Set up blocking", fontWeight = FontWeight.Bold,
                    color = if (ready) accent else MaterialTheme.colorScheme.onSurface)

                if (full) {
                    Text((if (accEnabled) "✓" else "✗") + "  1. Blocking service (Accessibility)", modifier = Modifier.padding(top = 8.dp))
                    Text((if (battery) "✓" else "✗") + "  2. Allow background use")
                    Text((if (on) "✓" else "✗") + "  3. Blocking switched on")

                    if (!ready) {
                        Button(onClick = {
                            when {
                                !accEnabled -> { step = 1; ctx.startActivity(Intent(AndroidSettings.ACTION_ACCESSIBILITY_SETTINGS)) }
                                !battery -> { step = 2; openBatteryDialog(ctx) }
                                else -> { on = true; prefs.edit().putBoolean("blk_on", true).apply() }
                            }
                        }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                            Text(if (!accEnabled) "Start setup" else if (!battery) "Allow background use" else "Turn blocking on")
                        }
                        if (!accEnabled) Text("In the list, tap StudyReward and switch it on. Then come back here: the next step opens by itself.",
                            fontSize = 12.sp, color = grey, modifier = Modifier.padding(top = 8.dp))
                        if (!accEnabled) TextButton(onClick = {
                            ctx.startActivity(Intent(AndroidSettings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + ctx.packageName)))
                        }) { Text("Switch is greyed out? Open App info, then ⋮ > Allow restricted settings", fontSize = 12.sp) }
                    }
                } else {
                    Text((if (usageOk) "✓" else "✗") + "  1. Usage access (detects active apps)", modifier = Modifier.padding(top = 8.dp))
                    Text((if (overOk) "✓" else "✗") + "  2. Display over other apps (lock screen)")
                    Text((if (battery) "✓" else "✗") + "  3. Allow background use")
                    Text((if (on) "✓" else "✗") + "  4. Blocking switched on")

                    if (!ready) {
                        Button(onClick = {
                            when {
                                !usageOk -> { step = 1; ctx.startActivity(Intent(AndroidSettings.ACTION_USAGE_ACCESS_SETTINGS)) }
                                !overOk -> {
                                    step = 2
                                    try { ctx.startActivity(Intent(AndroidSettings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + ctx.packageName))) } catch (e: Exception) { }
                                }
                                !battery -> { step = 3; openBatteryDialog(ctx) }
                                else -> {
                                    on = true
                                    prefs.edit().putBoolean("blk_on", true).apply()
                                    UsageBlockService.start(ctx)
                                }
                            }
                        }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                            Text(if (!usageOk) "Step 1: Grant Usage Access" else if (!overOk) "Step 2: Grant Overlay Permission" else if (!battery) "Step 3: Allow background use" else "Turn blocking on")
                        }
                    }
                }

                Text("If blocking ever stops working, allow auto-start for StudyReward in your phone settings and lock the app in Recents.",
                    fontSize = 12.sp, color = grey, modifier = Modifier.padding(top = 8.dp))
                if (msg.isNotBlank()) Text(msg, color = accent, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
            }

            Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Block distracting apps", fontWeight = FontWeight.Medium)
                    Text("The master switch. Turn it off any time.", fontSize = 12.sp, color = grey)
                }
                Switch(on, { v ->
                    on = v
                    prefs.edit().putBoolean("blk_on", v).apply()
                    if (!full) {
                        if (v) UsageBlockService.start(ctx) else UsageBlockService.stop(ctx)
                    }
                })
            }
            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Also block all games", Modifier.weight(1f))
                Switch(games, { games = it; prefs.edit().putBoolean("blk_games", it).apply() })
            }

            // Instagram Reels Dedicated Block
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Block Instagram Reels", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (full) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF59E0B).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    if (full) "Special Edition" else "Full-App in Friends Edition",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (full) Color(0xFF059669) else Color(0xFFD97706),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text("Blocks Reels doom-scrolling while keeping DMs & search accessible (uses Accessibility)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(reelsBlock, { reelsBlock = it; prefs.edit().putBoolean("blk_reels", it).apply() })
                }
            }

            // Distracting Website Blocking in Browsers
            var webBlock by remember { mutableStateOf(prefs.getBoolean("blk_websites", true)) }
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Block Distracting Websites in Browsers", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (full) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF59E0B).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    if (full) "Special Edition" else "Full-App in Friends Edition",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (full) Color(0xFF059669) else Color(0xFFD97706),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text("Blocks Instagram, TikTok, Reddit, and Twitter inside Chrome & mobile browsers during focus", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(webBlock, { webBlock = it; prefs.edit().putBoolean("blk_websites", it).apply() })
                }
            }

            // Multi-Day Exam Mode
            val nowMs = System.currentTimeMillis()
            val isExamOn = nowMs < examUntil
            val remainingExamHours = if (isExamOn) (examUntil - nowMs) / 3600000L else 0L
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (isExamOn) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isExamOn) MaterialTheme.colorScheme.error.copy(alpha = 0.4f) else Color.Transparent),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("🎯 Multi-Day Exam Mode", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                if (isExamOn) "ACTIVE: Distracting apps locked for next ${remainingExamHours + 1} hours!"
                                else "Lock all distracting apps strictly during exam week",
                                fontSize = 12.sp,
                                color = if (isExamOn) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isExamOn) {
                            OutlinedButton(
                                onClick = {
                                    examUntil = 0L
                                    prefs.edit().putLong("blk_exam_until", 0L).apply()
                                },
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("End Exam", fontSize = 12.sp) }
                        }
                    }
                    if (!isExamOn) {
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            listOf(1 to "1 Day", 2 to "2 Days", 3 to "3 Days", 7 to "1 Week").forEach { (days, lbl) ->
                                Button(
                                    onClick = {
                                        val until = System.currentTimeMillis() + days * 86400000L
                                        examUntil = until
                                        prefs.edit().putLong("blk_exam_until", until).apply()
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.padding(end = 6.dp)
                                ) { Text(lbl, fontSize = 12.sp) }
                            }
                        }
                    }
                }
            }

            // Notification Focus Shield
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("🔕 Distraction Notification Shield", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Auto-silence alerts during active focus sessions", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(notifShield, { notifShield = it; prefs.edit().putBoolean("blk_notif_shield", it).apply() })
                }
            }

            if (full) {
                Text("YouTube", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { debugTaps++ }.padding(top = 20.dp))
                Text("YouTube opens normally so you can search. Shorts are always blocked. If a video is not from a study channel or about a study topic, " +
                        "you get a 10-second warning and then it closes. While your Use timer runs, normal videos play freely (Shorts stay blocked).",
                    fontSize = 13.sp, color = grey, modifier = Modifier.padding(top = 4.dp))
                Text("Study channels", fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 12.dp))
                Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(newChannel, { newChannel = it.take(40) }, label = { Text("Add a channel name") }, singleLine = true,
                        modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        val c = newChannel.trim()
                        if (c.isNotEmpty()) {
                            channels = channels + c
                            prefs.edit().putStringSet("blk_channels", HashSet(channels)).apply()
                            newChannel = ""
                        }
                    }) { Text("Add") }
                }
                channels.sorted().forEach { c ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(c, Modifier.weight(1f))
                        Text("✕", Modifier.clickable {
                            channels = channels - c
                            prefs.edit().putStringSet("blk_channels", HashSet(channels)).apply()
                        }.padding(8.dp))
                    }
                }
                Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Study words", fontWeight = FontWeight.Medium)
                        Text("${words.size} words, like lecture, physics, jee, coding. A video title with one of them is treated as study.",
                            fontSize = 12.sp, color = grey)
                    }
                    TextButton(onClick = { wordsText = words.sorted().joinToString(", "); wordsDialog = true }) { Text("Edit") }
                }

                if (debugTaps >= 5) {
                    Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Troubleshooting helper")
                            Text("Records what YouTube shows so detection can be tuned. Turn on, open YouTube and play a video, come back, and copy the text.",
                                fontSize = 12.sp, color = grey)
                        }
                        Switch(debugOn, { debugOn = it; prefs.edit().putBoolean("blk_debug", it).apply() })
                    }
                    if (debugOn && dbg.isNotBlank()) {
                        Text(dbg, fontSize = 10.sp, maxLines = 12, color = grey, modifier = Modifier.padding(top = 6.dp))
                        OutlinedButton(onClick = {
                            val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            cm.setPrimaryClip(android.content.ClipData.newPlainText("StudyReward YouTube debug", dbg))
                            msg = "Copied."
                        }) { Text("Copy") }
                    }
                }
            } else {
                Text("YouTube", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 20.dp))
                Text("In the Friends edition, YouTube is locked as a whole app so it installs cleanly without Play Protect warnings. " +
                     "To watch study videos with automatic detection, use the Full edition.",
                    fontSize = 13.sp, color = grey, modifier = Modifier.padding(top = 4.dp))
            }

            Text("Emergency", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 20.dp))
            OutlinedButton(onClick = {
                msg = if (onEmergency()) "Unlocked for 5 minutes. 5 minutes of screen time were used." else "You do not have 5 minutes of screen time left."
            }, enabled = canAfford) { Text("Unlock everything for 5 min (costs 5 min)") }

            Text("Other apps (switch on = locked)", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 20.dp))
            OutlinedTextField(query, { query = it }, label = { Text("Search apps") }, singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp))
            if (apps.isEmpty()) Text("Loading apps...", color = grey)
        }
        items(apps.filter { query.isBlank() || it.label.contains(query, ignoreCase = true) }, key = { it.pkg }) { a ->
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                if (a.icon != null) Image(a.icon, contentDescription = null, modifier = Modifier.size(36.dp))
                else Spacer(Modifier.size(36.dp))
                Text(a.label, Modifier.weight(1f).padding(horizontal = 12.dp), maxLines = 1)
                Switch(a.pkg in blocked, { v ->
                    blocked = if (v) blocked + a.pkg else blocked - a.pkg
                    prefs.edit().putStringSet("blk_apps", HashSet(blocked)).apply()
                })
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }

    if (wordsDialog) {
        AlertDialog(onDismissRequest = { wordsDialog = false }, title = { Text("Study words") },
            text = {
                OutlinedTextField(wordsText, { wordsText = it }, label = { Text("Separate words with commas") }, minLines = 5)
            },
            confirmButton = {
                TextButton(onClick = {
                    words = wordsText.split(",", "\n").map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toSet()
                    prefs.edit().putStringSet("blk_words", HashSet(words)).apply()
                    wordsDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { wordsText = DEFAULT_STUDY_WORDS.sorted().joinToString(", ") }) { Text("Reset") }
                    TextButton(onClick = { wordsDialog = false }) { Text("Cancel") }
                }
            })
    }
}
