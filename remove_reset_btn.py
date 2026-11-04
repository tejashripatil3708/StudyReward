import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                        Spacer(Modifier.height(12.dp))
                        Text(
                            "💡 Tip: If you ran test sessions earlier today, your used time might exceed your earned time. Tap below to reset today's used time to 0 and restore your balance!",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                onResetSpends()
                                showBalanceInfo = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🔄 Reset Today's Used Time (Test Mode)", fontWeight = FontWeight.Bold)
                        }"""

assert target in text, "target not found"
text = text.replace(target, "", 1)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("Reset button removed successfully!")
