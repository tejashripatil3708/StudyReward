import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# 1. Update reward chip toggle
old_chip = ".background(if (type == r) chipOn else chipOff).clickable { type = r }"
new_chip = ".background(if (type == r) chipOn else chipOff).clickable { type = if (type == r) null else r }"
assert old_chip in text, "old_chip not found"
text = text.replace(old_chip, new_chip, 1)

# 2. Add Done button to reward task input form
old_reward_form = """                            Button(
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
                    }"""

new_reward_form = """                            Button(
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
                    }"""

assert old_reward_form in text, "old_reward_form not found"
text = text.replace(old_reward_form, new_reward_form, 1)

# 3. Update Study task header button to show "Done ✓" when open
old_study_btn = """                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.clickable { addingFor = if (addingFor == name) null else name }
                        ) {
                            Text("+ Add Task", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }"""

new_study_btn = """                        Surface(
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
                        }"""

assert old_study_btn in text, "old_study_btn not found"
text = text.replace(old_study_btn, new_study_btn, 1)

# 4. Add Done button beside Add in Study task form
old_study_form = """                if (addingFor == name) Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(title, { title = it }, label = { Text("New study task") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { if (title.isNotBlank()) { onAdd(title.trim(), name, 0L, "", dayKey); title = "" } }, shape = RoundedCornerShape(16.dp)) { Text("Add") }
                }"""

new_study_form = """                if (addingFor == name) Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(title, { title = it }, label = { Text("New study task") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { if (title.isNotBlank()) { onAdd(title.trim(), name, 0L, "", dayKey); title = "" } }, shape = RoundedCornerShape(16.dp)) { Text("Add") }
                    Spacer(Modifier.width(6.dp))
                    OutlinedButton(onClick = { addingFor = null; title = "" }, shape = RoundedCornerShape(16.dp)) { Text("Done ✓") }
                }"""

assert old_study_form in text, "old_study_form not found"
text = text.replace(old_study_form, new_study_form, 1)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("Done buttons added to task forms successfully!")
