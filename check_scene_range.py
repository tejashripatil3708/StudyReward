import sys
sys.stdout.reconfigure(encoding="utf-8")

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

start = text.find("fun SceneBackground(")
end = text.find("drawRect(bg.copy(alpha =", start)
end = text.find("}", end + 50) + 1
print("SceneBackground range:", start, end)
print(text[start:start+400])
