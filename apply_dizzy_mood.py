import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# 1. Update MascotMood enum to include DIZZY
old_enum = """enum class MascotMood {
    SLEEPING,
    FOCUSED,
    CHEERING,
    TIRED
}"""

new_enum = """enum class MascotMood {
    SLEEPING,
    FOCUSED,
    CHEERING,
    TIRED,
    DIZZY
}"""

assert old_enum in text, "old_enum not found"
text = text.replace(old_enum, new_enum, 1)

# 2. Add MascotMood.DIZZY rendering in MascotCanvas
old_tired = """                // Tear drop on right cheek
                drawCircle(Color(0xFF42A5F5), radius = 3.dp.toPx(), center = Offset(cx + w * 0.22f, cheekY + 8f))
            }
        }"""

new_dizzy = """                // Tear drop on right cheek
                drawCircle(Color(0xFF42A5F5), radius = 3.dp.toPx(), center = Offset(cx + w * 0.22f, cheekY + 8f))
            }
            MascotMood.DIZZY -> {
                // Swirly cartoon spiral eyes (@_@)
                val spiralRadius = 8.dp.toPx()
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = (bounce * 40f) % 360f, sweepAngle = 280f, useCenter = false,
                    topLeft = Offset(cx - eyeDist - spiralRadius, eyeY - spiralRadius),
                    size = Size(spiralRadius * 2, spiralRadius * 2),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = (bounce * 40f + 140f) % 360f, sweepAngle = 280f, useCenter = false,
                    topLeft = Offset(cx + eyeDist - spiralRadius, eyeY - spiralRadius),
                    size = Size(spiralRadius * 2, spiralRadius * 2),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                // Wavy surprised mouth ~
                drawArc(
                    color = Color(0xFF2D253B),
                    startAngle = 0f, sweepAngle = 180f, useCenter = false,
                    topLeft = Offset(cx - 6f, eyeY + 12f), size = Size(12f, 10f),
                    style = Stroke(width = 2.5f.dp.toPx(), cap = StrokeCap.Round)
                )
                // Floating stars orbiting around the head
                val starOrbit = (bounce * 8f)
                drawCircle(Color(0xFFFFD54F), radius = 3.dp.toPx(), center = Offset(cx - w * 0.28f + starOrbit, cy - h * 0.35f))
                drawCircle(Color(0xFFFFB300), radius = 2.5f.dp.toPx(), center = Offset(cx + w * 0.28f - starOrbit, cy - h * 0.38f))
            }
        }"""

assert old_tired in text, "old_tired not found"
text = text.replace(old_tired, new_dizzy, 1)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("MascotMood.DIZZY added successfully!")
