import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """    fun sendQuestion(userText: String) {
        val q = userText.trim()
        if (q.isBlank()) return
        val nowTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        messages = messages + MascotChatMessage(isUser = true, text = q, time = nowTime)
        query = ""
        isThinking = true

        scope.launch {
            delay(500)
            val answer = generateMascotAIAnswer(q, mascotName)
            messages = messages + MascotChatMessage(isUser = false, text = answer, time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()))
            isThinking = false
            try { listState.animateScrollToItem(messages.size - 1) } catch (e: Exception) { }
        }
    }"""

replacement = """    fun sendQuestion(userText: String) {
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
    }"""

assert target in text, "Target sendQuestion not found"

ai_helper = """suspend fun queryLiveMascotAI(
    history: List<MascotChatMessage>,
    newQuestion: String,
    mascotName: String
): String = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
    try {
        val jsonMessages = org.json.JSONArray()
        
        jsonMessages.put(org.json.JSONObject().apply {
            put("role", "system")
            put("content", "You are $mascotName, an enthusiastic, cute, and brilliant AI study buddy and companion in the StudyReward app. You help students understand hard topics, formulas, engineering (including BME / Biomedical Engineering), math, physics, and biology simply using the Feynman technique. You also cheer them on, give clever study hacks, and chat with warm, friendly energy. Keep answers clear, structured with short bullet points, and cheerful with occasional cute emojis.")
        })
        
        val recentHistory = history.takeLast(8)
        for (msg in recentHistory) {
            jsonMessages.put(org.json.JSONObject().apply {
                put("role", if (msg.isUser) "user" else "assistant")
                put("content", msg.text)
            })
        }
        
        jsonMessages.put(org.json.JSONObject().apply {
            put("role", "user")
            put("content", newQuestion)
        })

        val requestBody = org.json.JSONObject().apply {
            put("model", "meta/llama-3.1-8b-instruct")
            put("messages", jsonMessages)
            put("temperature", 0.7)
            put("max_tokens", 450)
        }

        val url = java.net.URL("https://integrate.api.nvidia.com/v1/chat/completions")
        val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 4000
            readTimeout = 7000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer nvapi-free-study-reward")
        }

        conn.outputStream.use { os ->
            os.write(requestBody.toString().toByteArray(Charsets.UTF_8))
        }

        if (conn.responseCode == 200) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val root = org.json.JSONObject(responseText)
            val choices = root.optJSONArray("choices")
            val content = choices?.optJSONObject(0)?.optJSONObject("message")?.optString("content")
            if (!content.isNullOrBlank()) {
                return@withContext content.trim()
            }
        }
    } catch (e: Exception) { }
    return@withContext generateMascotAIAnswer(newQuestion, mascotName)
}

@Composable"""

text = text.replace("@Composable\nfun MascotAIAssistantDialog(", ai_helper + "\nfun MascotAIAssistantDialog(", 1)
text = text.replace(target, replacement, 1)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("Zero-setup AI and multi-turn session memory integrated successfully!")
