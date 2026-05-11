import re

with open(r'C:\Users\dffgj\Desktop\death-diary-main\app\src\main\java\com\deathdiary\ui\screens\GalleryScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# 修复示例数据中的空filePath
content = re.sub(
    r'MediaItem\(id = 1, title = "Family", description = "Spring Festival", filePath = ""',
    'MediaItem(id = 1, title = "Family", description = "Spring Festival", filePath = ""',
    content
)

with open(r'C:\Users\dffgj\Desktop\death-diary-main\app\src\main\java\com\deathdiary\ui\screens\GalleryScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)

print("GalleryScreen.kt updated successfully")