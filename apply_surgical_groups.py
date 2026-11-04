import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

start = text.find("fun GroupsTab(")
end = text.find("\n@Composable\nfun rememberNow(): Long", start)
assert start != -1 and end != -1, "GroupsTab bounds not found"

new_groups = """fun GroupsTab(uid: String, groups: List<Group>, reqs: Map<String, List<Req>>, unread: Set<String>, openId: String?, setOpenId: (String?) -> Unit,
              onChat: (String) -> Unit, onRankings: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val ctx = LocalContext.current
    var dialog by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    val g = groups.find { it.id == openId }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        if (g == null) {
            Text("👥 Study Squads 🐾", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 16.dp))
            Text("Stay accountable, learn together, and share screen time! 🌸", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { dialog = "create"; text = "" },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) { Text("✨ + Create Squad") }

                OutlinedButton(
                    onClick = { dialog = "join"; text = "" },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) { Text("🔗 Join with code") }
            }

            OutlinedButton(
                onClick = onRankings,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("🏆 Study Rankings & Badges")
            }

            if (msg.isNotBlank()) Text(msg, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp))

            if (groups.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.40f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)),
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                ) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌱 (｡♥‿♥｡) 🌱", fontSize = 32.sp)
                        Spacer(Modifier.height(10.dp))
                        Text("No study squads yet!", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(4.dp))
                        Text("Create a squad above or enter your friend's invite code to study together and earn bonus screen time!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }

                Text("Leaderboard (everyone) 🌟", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
                Leaderboard(uid, 50)
            } else {
                Text("My Squads (${groups.size}) 💖", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 20.dp, bottom = 6.dp))
            }

            groups.forEach { x ->
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp).clickable { setOpenId(x.id) }
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(46.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Text("👥", fontSize = 22.sp)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(x.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text("${x.members.size} / ${x.capacity} members", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                if (x.id in unread) {
                                    Spacer(Modifier.width(6.dp))
                                    Text("💬 New chat", color = Color(0xFFE5484D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
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
        } else GroupDetail(g, uid, db, ctx, reqs[g.id] ?: emptyList(), g.id in unread, { onChat(g.id) }) { setOpenId(null) }
    }

    if (dialog.isNotEmpty()) {
        AlertDialog(onDismissRequest = { dialog = "" },
            title = { Text(if (dialog == "create") "Create Study Squad ✨" else "Join with Code 🔗", fontWeight = FontWeight.Bold) },
            text = { OutlinedTextField(text, { text = it }, label = { Text(if (dialog == "create") "Squad name" else "6-character Code") }, singleLine = true, shape = RoundedCornerShape(16.dp)) },
            dismissButton = { TextButton(onClick = { dialog = "" }) { Text("Cancel") } },
            confirmButton = {
                TextButton(onClick = {
                    val input = text.trim()
                    if (input.isNotBlank()) {
                        if (dialog == "create") {
                            val code = (1..6).map { "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".random() }.joinToString("")
                            db.collection("groups").add(mapOf<String, Any>("name" to input, "admin" to uid, "code" to code,
                                "pool" to 120L, "capacity" to 10L, "joinMethod" to "code", "intro" to "", "rules" to "", "members" to listOf(uid)))
                            msg = "Squad created! Open it to share the code with friends."
                        } else {
                            db.collection("groups").whereEqualTo("code", input.uppercase()).limit(1).get().addOnSuccessListener { snap ->
                                val d = snap.documents.firstOrNull()
                                val n = (d?.get("members") as? List<*>)?.size ?: 0
                                msg = when {
                                    d == null -> "Code not found"
                                    n >= (d.getLong("capacity") ?: 10L) -> "That group is full"
                                    (d.get("members") as? List<*>)?.contains(uid) == true -> "You are already in this group"
                                    d.getString("joinMethod") == "approval" -> {
                                        d.reference.collection("joinRequests").document(uid).set(mapOf("name" to
                                                myName(), "at" to System.currentTimeMillis()))
                                        "Request sent. The squad leader has to approve you."
                                    }
                                    else -> { d.reference.update("members", FieldValue.arrayUnion(uid)); "Joined!" }
                                }
                            }
                        }
                    }
                    dialog = ""
                }) { Text("Confirm ✨", fontWeight = FontWeight.Bold) }
            })
    }
}"""

text = text[:start] + new_groups + text[end:]

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("GroupsTab replaced cleanly!")
