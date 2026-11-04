import sys
sys.stdout.reconfigure(encoding="utf-8")

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

start = text.find("fun ProfileTab(")
end = text.find("fun TodoTab(", start)
print(f"ProfileTab range: {start} to {end}")
print(text[start:end])
