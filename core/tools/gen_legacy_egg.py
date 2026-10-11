"""Draw CenturyBridge's own spawn egg template: a grayscale egg and a spots overlay, 16x16,
the two layers 1.20.1's item/template_spawn_egg model tinted with an egg's two colours
(1.21.5 removed the template; every vanilla egg has its own texture since). Original
procedural art -- an ellipse with a darker rim and a light top-left -- so CenturyBridge
ships no Mojang pixels.

  python gen_legacy_egg.py <resourcesDir>
writes <resourcesDir>/assets/centurybridge/textures/item/legacy_spawn_egg{,_overlay}.png
"""
import os
import sys

from PIL import Image


def inside(x, y):
    cx, cy = 7.5, 8.3
    px, py = x + 0.5, y + 0.5
    a = 4.7 if py < cy else 5.4  # narrower above the middle
    b = 6.9
    return ((px - cx) / a) ** 2 + ((py - cy) / b) ** 2 <= 1.0


def main():
    out = os.path.join(sys.argv[1], "assets", "centurybridge", "textures", "item")
    os.makedirs(out, exist_ok=True)
    base = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            if not inside(x, y):
                continue
            rim = any(not inside(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
            if rim:
                g = 140
            else:
                # light from the top left, shade toward the bottom right
                d = (x - 5.0) + (y - 5.0)
                g = max(185, min(255, int(250 - d * 6)))
            base.putpixel((x, y), (g, g, g, 255))
    spots = [(5, 4), (6, 4), (5, 5), (9, 6), (10, 6), (10, 7), (6, 9), (7, 9), (6, 10), (10, 11), (9, 12), (10, 12), (5, 12)]
    over = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for x, y in spots:
        if inside(x, y):
            over.putpixel((x, y), (235, 235, 235, 255))
    base.save(os.path.join(out, "legacy_spawn_egg.png"))
    over.save(os.path.join(out, "legacy_spawn_egg_overlay.png"))
    print("wrote", out)


if __name__ == "__main__":
    main()
