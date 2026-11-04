import sys
sys.stdout.reconfigure(encoding="utf-8")

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

idx = text.find("That's an interesting thought about")
eol = text.find("\n", idx)
print("Old line:", text[idx:eol])
text = text[:idx] + 'That\'s an interesting thought about \\"$query\\"! 💡\\n\\n" +' + text[eol:]

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("Line 405 fixed successfully!")
