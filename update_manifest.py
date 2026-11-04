import sys

path = r"C:\Users\Tejashri\AndroidStudioProjects\StudyReward\app\src\main\AndroidManifest.xml"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """        <intent>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent>"""

replacement = """        <intent>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent>
        <intent>
            <action android:name="android.intent.action.TTS_SERVICE" />
        </intent>"""

assert target in text, "target not found in manifest"
text = text.replace(target, replacement, 1)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("TTS query added to AndroidManifest.xml successfully!")
