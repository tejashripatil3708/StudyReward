import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

start = text.find("fun RowScope.StatCard(")
end = text.find("val rewardTypes =", start)
assert start != -1 and end != -1, "Profile bounds not found"

new_profile = """fun RowScope.StatCard(label: String, value: String, icon: String = "✨") {
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
fun ProfileTab(name: String, email: String, studied: Long, subjectCount: Int, done: Int, total: Int, streak: Int, onOpen: (String) -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        Text("🌸 Student Lounge ✨", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 16.dp))
        
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
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text("🌟 Level 1 Focus Apprentice", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
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
        Text("Personal Controls ⚙️", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 8.dp))

        listOf(
            "🔒  App blocking & Screen Limits" to "blocking",
            "🎯  Target Events & Countdowns" to "events",
            "🏆  Study Challenges" to "challenges",
            "📊  Study Statistics & Insights" to "stats",
            "📝  Detailed Study Logs" to "logs",
            "⚙️  App & Focus Settings" to "settings"
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
"""

text = text[:start] + new_profile + text[end:]

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("ProfileTab redesigned successfully!")
