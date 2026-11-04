import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = "@Composable\nclass CuteMascotSpeaker"
replacement = "class CuteMascotSpeaker"

assert target in text, "target not found"
text = text.replace(target, replacement, 1)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("@Composable removed from CuteMascotSpeaker class successfully!")
