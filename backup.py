import sys

with open(r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

# Make backup first!
with open(r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\java\com\example\studyreward\MainActivity.kt.bak", "w", encoding="utf-8") as f:
    f.write(text)

print("Backup created successfully.")
