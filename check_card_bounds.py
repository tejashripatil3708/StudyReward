import sys
sys.stdout.reconfigure(encoding="utf-8")

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

start = text.find("fun InteractiveMascotCard(")
end = text.find("// ---------- Mascot Component (Canvas Drawn) ----------", start)
print("InteractiveMascotCard range:", start, end)
print(text[start:start+350])
