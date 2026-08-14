from PIL import Image, ImageDraw
import os

# Amber theme colors
AMBER_PRIMARY = "#FFB300"  # Amber 600
AMBER_DARK = "#FF8F00"     # Amber 800
WHITE = "#FFFFFF"

# Mipmap sizes: (density, size)
SIZES = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192,
}

base_dir = r"C:\Users\Administrator\.qwenpaw\workspaces\default\memo-amber\app\src\main\res"

for dpi, size in SIZES.items():
    dir_path = os.path.join(base_dir, f"mipmap-{dpi}")
    os.makedirs(dir_path, exist_ok=True)
    
    # Regular icon: amber background with white center
    img = Image.new("RGBA", (size, size), AMBER_PRIMARY)
    draw = ImageDraw.Draw(img)
    
    # Draw rounded rectangle background
    radius = size // 6
    draw.rounded_rectangle([0, 0, size-1, size-1], radius=radius, fill=AMBER_PRIMARY)
    
    # Center circle/amber accent
    center_size = size // 2
    cx, cy = size // 2, size // 2
    draw.ellipse([cx - center_size//2, cy - center_size//2, 
                  cx + center_size//2, cy + center_size//2], fill=WHITE)
    
    # Inner amber dot
    inner_size = center_size // 3
    draw.ellipse([cx - inner_size//2, cy - inner_size//2,
                  cx + inner_size//2, cy + inner_size//2], fill=AMBER_PRIMARY)
    
    img.save(os.path.join(dir_path, "ic_launcher.png"), "PNG")
    
    # Round icon: full circle
    img_round = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw_round = ImageDraw.Draw(img_round)
    draw_round.ellipse([0, 0, size-1, size-1], fill=AMBER_PRIMARY)
    
    # White center
    center_size = size // 2
    cx, cy = size // 2, size // 2
    draw_round.ellipse([cx - center_size//2, cy - center_size//2,
                        cx + center_size//2, cy + center_size//2], fill=WHITE)
    
    # Inner amber dot
    inner_size = center_size // 3
    draw_round.ellipse([cx - inner_size//2, cy - inner_size//2,
                        cx + inner_size//2, cy + inner_size//2], fill=AMBER_PRIMARY)
    
    img_round.save(os.path.join(dir_path, "ic_launcher_round.png"), "PNG")
    
    print(f"Generated {dpi}: {size}x{size}")

print("Done!")
