import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# 1. Update yesVotes
old_yes_votes = "fun yesVotes(r: Req, g: Group) = r.yes.count { it in g.members && it != r.fromUid }"
new_yes_votes = "fun yesVotes(r: Req, g: Group) = r.yes.count { it != r.fromUid && (g.members.isEmpty() || it in g.members) }"
assert old_yes_votes in text, "yesVotes not found"
text = text.replace(old_yes_votes, new_yes_votes, 1)

# 2. Update generateMascotAIAnswer and MascotAIAssistantDialog and InteractiveMascotCard
# Find range from "fun generateMascotAIAnswer(" to "// ---------- Mascot Component (Canvas Drawn) ----------"
start_mascot_section = text.find("fun generateMascotAIAnswer(")
end_mascot_section = text.find("// ---------- Mascot Component (Canvas Drawn) ----------", start_mascot_section)
assert start_mascot_section != -1 and end_mascot_section != -1, "Mascot section not found"

new_mascot_section = """fun generateMascotAIAnswer(query: String, mascotName: String): String {
    val q = query.lowercase().trim()
    return when {
        // Greetings
        q == "hi" || q == "hello" || q == "hey" || q == "yo" || q == "sup" || q.startsWith("hi ") || q.startsWith("hello ") || q.startsWith("hey ") || q.contains("good morning") || q.contains("good evening") || q.contains("good afternoon") || q.contains("namaste") -> {
            listOf(
                "Hey bestie! 💕 I'm so happy to see you! Ready to crush some study goals today, or just stopping by for a cute pep talk? 🌸✨",
                "Hello, my favorite human! 🐾✨ I've been hopping around waiting for you to study! What are we conquering today? 📚💖",
                "Heyyy! 🌟 Grab your water, get cozy, and let's make today count! How are you feeling right now? 🍵💫"
            ).random()
        }

        // How are you / small talk
        q.contains("how are you") || q.contains("how are u") || q.contains("how r u") || q.contains("how's it going") || q.contains("how do you do") || q.contains("wassup") || q.contains("what's up") -> {
            "I'm feeling super bouncy and full of energy! ⚡ Just keeping your study streak safe and protecting you from doom-scrolling distractions. How are you feeling today? Need study tips or some motivation? 🌸💪"
        }

        // Who are you / Identity
        q.contains("who are you") || q.contains("what is your name") || q.contains("your name") || q.contains("what are you") -> {
            "I'm **$mascotName**! 🐾 Your personal AI study buddy in StudyReward! I live on your home screen, jump when you poke me, help explain confusing topics, and celebrate every minute you study! 💖✨"
        }

        // Capabilities
        q.contains("what can you do") || q.contains("help me") || q.contains("features") || q.contains("what do you do") -> {
            "Here is everything I can do for you, bestie! 🌟\\n\\n" +
            "• **Explain complex subjects**: Ask me about BME, Calculus, Physics, Biology, Data Structures, or formulas! 📚\\n" +
            "• **Study techniques**: Ask about the Feynman technique, Pomodoro intervals, active recall, or blurting! 🧠\\n" +
            "• **Beat procrastination**: I'll give you instant 2-minute hacks to get started! ⏰\\n" +
            "• **Study schedules**: Ask me to plan your study blocks for today! 🗓️\\n" +
            "• **Jokes & encouragement**: Ask for a joke or motivation whenever you feel tired! 😄💕\\n\\n" +
            "Go ahead and test me on any question!"
        }

        // Jokes & Fun
        q.contains("joke") || q.contains("laugh") || q.contains("funny") || q.contains("bored") -> {
            listOf(
                "Why was the math book always depressed? 📖\\nBecause it had too many problems! 😂 But don't worry, you and I can solve anything together! ✏️✨",
                "Why did the student eat their homework? 🍰\\nBecause the teacher said it was a piece of cake! 🤣 Now go finish your real tasks! 🌸",
                "Why do programmers prefer dark mode? 💻\\nBecause light attracts bugs! 🐛 Keep your code and your study session bug-free today! ⚡",
                "Why did the cell go to therapy? 🧬\\nBecause it had too much mitotic tension! 🤣 Take a deep breath and keep smiling! 💕"
            ).random()
        }

        // Compliments & Love
        q.contains("cute") || q.contains("love you") || q.contains("sweet") || q.contains("thank you") || q.contains("thanks") || q.contains("thx") || q.contains("good job") || q.contains("best") -> {
            listOf(
                "Awww you're making me blush! (⁄ ⁄•⁄ω⁄•⁄ ⁄) 💕 Thank you! Having you as my study partner is the absolute best! Let's keep working hard together! 🌸✨",
                "You're the sweetest! 🥰 Hearing that made my little mascot heart do a happy backflip! Now let's turn that positive energy into solid study focus! ⚡📚",
                "Always happy to help you! You're working so hard and I notice every single effort you make. Super proud of you! 💖🐾"
            ).random()
        }

        // BME / Biomedical Engineering (the user's active subject!)
        q.contains("bme") || q.contains("biomedical") || q.contains("biomechanic") || q.contains("biomaterial") -> {
            "**Biomedical Engineering (BME)** is such an incredible field! 🧬🩺 You're engineering real solutions to save human lives! Here is a core overview:\\n\\n" +
            "• **Core Pillars**:\\n" +
            "  1. **Bioinstrumentation**: ECG, EMG, EEG, bio-potential amplifiers, and noise filtering.\\n" +
            "  2. **Biomaterials**: Biocompatibility, titanium/ceramic implants, and polymer scaffolds without immune rejection.\\n" +
            "  3. **Biomechanics**: Statics & dynamics of musculoskeletal systems, stress-strain curves of bone/tissue, and prosthetics.\\n" +
            "  4. **Medical Imaging**: X-Ray, CT (Hounsfield units), MRI (nuclear magnetic resonance), and Ultrasound (acoustic impedance).\\n\\n" +
            "• **Top Study Tip for BME**: Always connect the math/engineering equations directly to the physiological system! For example, think of blood flow through arteries as fluid dynamics (Poiseuille's law)!\\n\\n" +
            "What specific BME topic or formula are you reviewing right now? Tell me and I'll break it down step-by-step! 💡✨"
        }

        // Math & Calculus
        q.contains("calculus") || q.contains("derivative") || q.contains("integral") || q.contains("limits") -> {
            "Here is a quick refresher on **Calculus fundamentals**! 📐✨\\n\\n" +
            "• **Derivatives (Rate of Change)**: Finding the slope of a curve at a single instant! Power rule: d/dx(x^n) = n * x^(n-1). Chain rule: d/dx(f(g(x))) = f'(g(x)) * g'(x).\\n" +
            "• **Integrals (Accumulation)**: Finding the total area under a curve! Opposite of differentiation: ∫ x^n dx = (x^(n+1))/(n+1) + C.\\n" +
            "• **Study tip**: Practice 3 problems of each rule without looking at the solution book. You've got this! ✏️💪"
        }

        // Quadratic formula
        q.contains("quadratic") || (q.contains("formula") && (q.contains("math") || q.contains("root"))) -> {
            "Here is the standard **Quadratic Formula**! 📐✨\\n\\n" +
            "For any equation in the form: **ax² + bx + c = 0**\\n\\n" +
            "The roots are:\\n" +
            "**x = (-b ± √(b² - 4ac)) / (2a)**\\n\\n" +
            "• **Discriminant (D = b² - 4ac)**:\\n" +
            "  - D > 0: Two distinct real roots.\\n" +
            "  - D = 0: Exactly one repeated real root.\\n" +
            "  - D < 0: Two complex conjugate roots!\\n\\n" +
            "Solve one problem right now to lock it into your muscle memory! 🌸"
        }

        // Mitochondria / Bio
        q.contains("mitochondria") || q.contains("powerhouse") -> {
            "Mitochondria are known as the **powerhouse of the cell**! ⚡🔬\\n\\n" +
            "• **Function**: They produce **ATP** (adenosine triphosphate), the energy currency of our body, through cellular respiration (Krebs cycle & oxidative phosphorylation).\\n" +
            "• **Structure**: Double membrane! The inner membrane has folds called **cristae** to maximize surface area for electron transport chains.\\n\\n" +
            "Keep your brain cells energized with lots of ATP today! 🧠✨"
        }

        // Photosynthesis
        q.contains("photosynthesis") -> {
            "Here is **Photosynthesis** broken down simply! 🌱☀️\\n\\n" +
            "• **Chemical Equation**: 6CO₂ + 6H₂O + Sunlight ➔ C₆H₁₂O₆ (Glucose) + 6O₂\\n" +
            "• **Location**: Inside **chloroplasts** using the green pigment chlorophyll.\\n" +
            "• **Two stages**:\\n" +
            "  1. *Light Reactions* (in thylakoid membranes): Splits water to produce ATP, NADPH, and releases O₂.\\n" +
            "  2. *Calvin Cycle* (in stroma): Fixes CO₂ into glucose sugar without direct light!\\n\\n" +
            "Plants synthesize food; you synthesize knowledge! Let's go! 🌿✨"
        }

        // Newton's laws
        q.contains("newton") || (q.contains("law") && q.contains("motion")) -> {
            "Here are **Newton's 3 Laws of Motion**! 🍎🚀\\n\\n" +
            "1. **Law of Inertia**: An object stays at rest or in uniform straight motion unless acted on by an external net force.\\n" +
            "2. **F = m · a**: Net force equals mass multiplied by acceleration.\\n" +
            "3. **Action & Reaction**: For every action force, there is an equal and opposite reaction force!\\n\\n" +
            "Your focused study effort is the positive force launching your future! 🚀✨"
        }

        // Feynman technique
        q.contains("feynman") -> {
            "The **Feynman Technique** is the ultimate method to truly master any hard topic! 🧠📖\\n\\n" +
            "1. **Choose a concept**: Write its title at the top of a clean sheet.\\n" +
            "2. **Teach it simply**: Explain it out loud or in writing as if teaching a 10-year-old child (no jargon!).\\n" +
            "3. **Spot your gaps**: Whenever you hesitate or use buzzwords, consult your notes and fix the gap.\\n" +
            "4. **Use analogies**: Relate it to simple real-world things (e.g. water pipes for electrical circuits)!\\n\\n" +
            "Try explaining a topic to me right now in the chat! I'm listening! 🐾"
        }

        // Pomodoro
        q.contains("pomodoro") -> {
            "The **Pomodoro Technique** keeps your mind razor-sharp and prevents fatigue! 🍅⏱️\\n\\n" +
            "• **Standard rhythm**: 25 min deep work ➔ 5 min restful break.\\n" +
            "• **After 4 rounds**: Take a longer 15–30 min break to recharge completely.\\n" +
            "• **StudyReward feature**: Toggle the Pomodoro mode right here on the home page or in Focus settings! 🌸"
        }

        // Procrastination & Focus
        q.contains("procrastinat") || q.contains("distract") || q.contains("focus") || q.contains("lazy") -> {
            "Here is the secret **2-Minute Rule** to crush procrastination right now! ⏱️🔥\\n\\n" +
            "Tell yourself: *'I will just open the notebook and read for exactly 2 minutes. If I still want to stop after 2 minutes, I can.'*\\n\\n" +
            "Most procrastination is just friction before starting. Once your brain enters the flow state, momentum takes over! Put your phone down, pick up your pen, and count: 3, 2, 1... GO! 🚀"
        }

        // Motivation / Tired / Stress
        q.contains("motivat") || q.contains("tired") || q.contains("burnout") || q.contains("exhaust") || q.contains("stress") || q.contains("give up") || q.contains("sad") -> {
            "Take a slow, deep breath in... and exhale gently. 🍵🌸\\n\\n" +
            "You don't have to conquer the whole syllabus in one hour. You just have to sit down for the next 15 minutes.\\n" +
            "• Drink a glass of cold water 💧\\n" +
            "• Do a gentle shoulder roll and neck stretch 🧘\\n" +
            "• Remember how good it will feel when your exam is finished and you crushed it!\\n\\n" +
            "I'm right here sitting by your side cheering for you. Every small step counts! You can do this! 💖🐾"
        }

        // Smart conversational fallback for general questions
        else -> {
            "That's an interesting thought about \"$query\"! 💡\\n\\n" +
            "When tackling this, here is my recommended strategy:\\n" +
            "1. **Break it down**: Split it into 2 or 3 smaller questions in your notebook.\\n" +
            "2. **Core Idea**: What is the most important term or principle behind it?\\n" +
            "3. **Practice**: Explain it out loud in 3 bullet points.\\n\\n" +
            "Is there a specific part you're stuck on? Tell me more so I can help you understand it! 🌸✨"
        }
    }
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
                    text = "Hi bestie! I'm $mascotName, your personal AI study buddy! 🐾✨ Ask me anything about your subjects, confusing concepts, formulas, memory tricks, or just chat with me when you need a study break! (⁄ ⁄•⁄ω⁄•⁄ ⁄)",
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
        "🧬 BME Core Concepts",
        "🔬 Mitochondria",
        "🌱 Photosynthesis",
        "😂 Tell me a joke",
        "💪 Give me motivation"
    )

    fun sendQuestion(userText: String) {
        val q = userText.trim()
        if (q.isBlank()) return
        val nowTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        messages = messages + MascotChatMessage(isUser = true, text = q, time = nowTime)
        query = ""
        isThinking = true

        scope.launch {
            try { listState.animateScrollToItem(messages.size - 1) } catch (e: Exception) { }
            delay(500)
            val answer = generateMascotAIAnswer(q, mascotName)
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
                                    .widthIn(min = 80.dp, max = 280.dp)
                                    .clip(RoundedCornerShape(
                                        topStart = 16.dp, topEnd = 16.dp,
                                        bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                        bottomEnd = if (msg.isUser) 4.dp else 16.dp
                                    ))
                                    .background(
                                        if (msg.isUser) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
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
                                    color = (if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.75f),
                                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    if (isThinking) {
                        item {
                            Text(
                                "$mascotName is thinking... 💭✨",
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
    var tapCount by remember { mutableStateOf(0) }
    var showAIDialog by remember { mutableStateOf(false) }
    val view = androidx.compose.ui.platform.LocalView.current

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
        targetValue = if (pokeAnim) 20f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        label = "wiggle"
    )
    val squishScale by animateFloatAsState(
        targetValue = if (pokeAnim) 1.18f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "squish"
    )

    val mascotMood = when {
        goalMins > 0 && studiedSecs >= goalMins * 60 -> MascotMood.CHEERING
        studiedSecs > 0 -> MascotMood.CHEERING
        else -> MascotMood.SLEEPING
    }

    fun onPoke() {
        tapCount++
        try { view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP) } catch (e: Exception) { }
        pokeAnim = true
        currentSpeech = when (tone) {
            BuddyTone.ROAST -> ROASTS.random()
            BuddyTone.HYPE -> HYPE_QUIPS.random()
            BuddyTone.TIP -> STUDY_TIPS.random()
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
                        rotationZ = wiggleAngle + idleWiggle
                        scaleX = squishScale
                        scaleY = 2f - squishScale
                    }
                    .clip(CircleShape)
                    .clickable { onPoke() },
                contentAlignment = Alignment.Center
            ) {
                MascotCanvas(
                    type = type,
                    mood = if (pokeAnim || tapCount % 3 == 1) MascotMood.CHEERING else mascotMood,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.width(12.dp))

            // Speech bubble & action buttons
            Column(Modifier.weight(1f)) {
                // Top row: Mascot Name & Change button (completely unconstrained, never wraps vertically!)
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

                // Speech Bubble Box: rendered with actual speech string!
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

        // Tone selector buttons (Roast / Hype / Tips) on the left, Ask AI button on the right!
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
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Ask AI button prominently placed on bottom row!
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

text = text[:start_mascot_section] + new_mascot_section + text[end_mascot_section:]

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("Mascot AI and InteractiveMascotCard updated successfully!")
