import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

start = text.find("fun TodoTab(")
end = text.find("\n@Composable\nfun GroupsTab(", start)
assert start != -1 and end != -1, "TodoTab bounds not found"

new_todo = """fun TodoTab(
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

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp)) {
        LazyColumn {
            item {
                Text("✨ My Daily Quests 📝", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 16.dp))
                Text("Earn +1.5x bonus screen time for physical & wellness tasks! Today: $doneToday / $cap 🍬",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
                TaskDayNav(back) { back = it }
                if (back != 0) Text("You can plan other days, but you can only tick or time tasks on today's list.",
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                
                // Reward Task Type Pills
                Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 6.dp)) {
                    rewardTypes.forEach { r ->
                        Text(r, Modifier.padding(end = 6.dp).clip(RoundedCornerShape(16.dp))
                            .background(if (type == r) chipOn else chipOff).clickable { type = r }
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
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.clickable { addingFor = if (addingFor == name) null else name }
                        ) {
                            Text("+ Add Task", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }
                if (addingFor == name) Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(title, { title = it }, label = { Text("New study task") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { if (title.isNotBlank()) { onAdd(title.trim(), name, 0L, "", dayKey); title = "" } }, shape = RoundedCornerShape(16.dp)) { Text("Add") }
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
}"""

text = text[:start] + new_todo + text[end:]

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("TodoTab redesigned surgically!")
