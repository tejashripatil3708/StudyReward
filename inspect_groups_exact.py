import sys
sys.stdout.reconfigure(encoding="utf-8")

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

start = text.find("fun GroupsTab(")
end = text.find("\n@Composable\nfun rememberNow(): Long", start)
print(f"GroupsTab start: {start}, end: {end}")
print("Content length:", end - start)
