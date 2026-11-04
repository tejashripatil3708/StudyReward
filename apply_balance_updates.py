import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# 1. Add resetTodaySpends in App
stop_using_target = """        prefs.edit().putLong("blk_use_start", 0L).putLong("blk_use_until", 0L).apply()
    }"""
assert stop_using_target in text, "stop_using_target not found"

reset_spends_code = """        prefs.edit().putLong("blk_use_start", 0L).putLong("blk_use_until", 0L).apply()
    }

    fun resetTodaySpends() {
        val startTs = startOfDay()
        me.collection("spends").whereGreaterThanOrEqualTo("at", startTs).get().addOnSuccessListener { qs ->
            val batch = com.google.firebase.firestore.FirebaseFirestore.getInstance().batch()
            for (doc in qs.documents) {
                batch.delete(doc.reference)
            }
            batch.commit()
        }
        spent = 0L
        usingSecs = 0L
        using = false
        prefs.edit().putLong("blk_use_start", 0L).putLong("blk_use_until", 0L).apply()
        android.widget.Toast.makeText(ctx, "Playtime used time reset! Full earned balance restored ✨", android.widget.Toast.LENGTH_SHORT).show()
    }"""

text = text.replace(stop_using_target, reset_spends_code, 1)

# 2. Update HomeTab call in App
hometab_call_target = """                        mType, mName, { t, n ->
                            mType = t; mName = n
                            prefs.edit().putString("mascot_type", t.id).putString("mascot_name", n).apply()
                            me.set(mapOf("mascotType" to t.id, "mascotName" to n), SetOptions.merge())
                        }) {"""

hometab_call_replacement = """                        mType, mName, { t, n ->
                            mType = t; mName = n
                            prefs.edit().putString("mascot_type", t.id).putString("mascot_name", n).apply()
                            me.set(mapOf("mascotType" to t.id, "mascotName" to n), SetOptions.merge())
                        },
                        base, rate, giftSecs, tasks.filter { it.kind == "reward" && it.doneAt >= startOfDay() }.sumOf { it.reward }, earned, spent,
                        ::resetTodaySpends) {"""

assert hometab_call_target in text, "hometab_call_target not found"
text = text.replace(hometab_call_target, hometab_call_replacement, 1)

# 3. Update HomeTab definition signature
hometab_def_target = """    mascotType: MascotType = MascotType.MOCHI, mascotName: String = "Mochi", onMascotChange: (MascotType, String) -> Unit = { _, _ -> },
    onUse: () -> Unit
) {"""

hometab_def_replacement = """    mascotType: MascotType = MascotType.MOCHI, mascotName: String = "Mochi", onMascotChange: (MascotType, String) -> Unit = { _, _ -> },
    baseMins: Int = 60, earnRate: Int = 15, giftSecs: Long = 0L, tasksEarned: Long = 0L, totalEarned: Long = 0L, totalSpent: Long = 0L,
    onResetSpends: () -> Unit = {},
    onUse: () -> Unit
) {"""

assert hometab_def_target in text, "hometab_def_target not found"
text = text.replace(hometab_def_target, hometab_def_replacement, 1)

# 4. Update Playtime Balance card in HomeTab
card_target = """        // Screen time left / Playtime Balance card
        Row(
            Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(if (dayOff) "Vacation Day! 🏝️✨" else "🎮 Playtime Balance 🍬", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (dayOff) "No limits today" else clock(balance), fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    color = if (using) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            }
            if (!dayOff) Button(
                onClick = onUse,
                enabled = using || balance > 0,
                shape = RoundedCornerShape(50)
            ) { Text(if (using) "Back to Study 🌸" else "Playtime! 🎮✨") }
        }"""

card_replacement = """        var showBalanceInfo by remember { mutableStateOf(false) }

        if (showBalanceInfo) {
            AlertDialog(
                onDismissRequest = { showBalanceInfo = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎮 Playtime Breakdown", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                text = {
                    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                        Text(
                            "Here is exactly how your screen time balance is calculated today:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text("☀️ Base Daily Allowance: ${baseMins} min", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.height(6.dp))
                                val studyEarnedMins = (studied * earnRate) / 3600
                                Text("📚 Studied: ${hm(studied)} ➔ +${studyEarnedMins}m earned", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("   (Earn rate: ${earnRate} min playtime per 60 min study)", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(6.dp))
                                Text("👥 Approved Group Gifts: +${giftSecs / 60} min", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.height(6.dp))
                                Text("✅ Tasks Completed: +${tasksEarned / 60} min", fontSize = 13.sp, fontWeight = FontWeight.Medium)

                                HorizontalDivider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("📈 Total Playtime Earned:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(clock(totalEarned), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(Modifier.height(4.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("⏳ Playtime Used Today:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(clock(totalSpent), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (totalSpent > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                                }

                                HorizontalDivider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("🎮 Screen Time Balance:", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                    Text(clock(balance), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))
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
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showBalanceInfo = false }) {
                        Text("Close ✨", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Screen time left / Playtime Balance card (Tap to view breakdown & reset spends)
        Row(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
                .clickable { showBalanceInfo = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (dayOff) "Vacation Day! 🏝️✨" else "🎮 Playtime Balance 🍬", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(6.dp))
                    Text("ℹ️", fontSize = 11.sp)
                }
                Text(if (dayOff) "No limits today" else clock(balance), fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    color = if (using) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                if (!dayOff) {
                    Text(
                        "Earned: ${hm(totalEarned)} • Used: ${hm(totalSpent)} 🔍",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            if (!dayOff) Button(
                onClick = onUse,
                enabled = using || balance > 0,
                shape = RoundedCornerShape(50)
            ) { Text(if (using) "Back to Study 🌸" else "Playtime! 🎮✨") }
        }"""

assert card_target in text, "card_target not found"
text = text.replace(card_target, card_replacement, 1)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("Balance card, breakdown dialog, and reset function updated successfully!")
