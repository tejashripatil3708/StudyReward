import sys
sys.stdout.reconfigure(encoding="utf-8")

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

start = text.find("fun SceneBackground(")
idx = text.find("drawRect(bg.copy(alpha =", start)
end = text.find("}", idx + 50) + 1
print(f"SceneBackground ends around: {end}")
print(text[idx-100:end+100])
