import sys
import subprocess
import re

THRESHOLD = 10


# запускаем lizard
result = subprocess.run(
    ["lizard", ".", "-l", "java"],
    stdout=subprocess.PIPE,
    stderr=subprocess.PIPE,
    text=True
)

if result.returncode != 0:
    print("Failed to run lizard")
    print(result.stderr)
    sys.exit(1)

output = result.stdout

# пример строки lizard:
#  16  32  98 app_cli/src/main/java/... Main::main
cc_pattern = re.compile(r"^\s*(\d+)\s+\d+\s+\d+\s+(.+)", re.MULTILINE)

errors = []

for match in cc_pattern.finditer(output):
    cc = int(match.group(1))
    location = match.group(2)

    if cc > THRESHOLD:
        errors.append((location, cc))

if errors:
    print("Cyclomatic Complexity threshold exceeded!\n")
    for location, cc in errors:
        print(f"CC={cc} -> {location}")

    sys.exit(1)  # ❌ ошибка
else:
    print("Cyclomatic Complexity check passed ✅")
    sys.exit(0)
