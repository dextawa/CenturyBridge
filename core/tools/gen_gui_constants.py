#!/usr/bin/env python3
"""Generate the 1.20.2 layer's dead GUI constants (CbGuiConstants.java) and their fieldRedirects.

1.20.2 dropped a set of public/protected static final constants of the client GUI -- AbstractButton's
widgets.png button geometry, AbstractSliderButton's slider.png geometry, PlayerTabOverlay's icons.png
heart offsets, LanguageManager.DEFAULT_LANGUAGE_CODE, EntityRenderers.DEFAULT_PLAYER_MODEL. javac folds
such constants into the mod, but bytecode that reads them (another compiler, reflection-free ASM, the
API probe) does GETSTATIC and dies with NoSuchFieldError. The bridge gives each read the value the field
had in 1.20.1: the constant is read off the field's ConstantValue attribute in the 1.20.1 class file,
never typed by hand. (The textures those coordinates index into exist again: CbGuiLegacyTextures.)

For every damage-table row of the step that reads a static field (get-static / get-static-protected)
of an area owner, where the field is gone and 1.20.1 declared it with a ConstantValue, this writes a
same-name public static final field into CbGuiConstants and a fieldRedirects entry into the area's
wiring fragment (shims-1.20.2.json.d/s1client.json, json round-trip; other sections untouched).

  python3 core/tools/gen_gui_constants.py [dataDir] [damage.tsv]
"""
import json
import os
import re
import struct
import sys
import zipfile

AREA = r"^net/minecraft/client/(?!multiplayer/|telemetry|quickplay|gui/screens/advancements)"
OUT = "core/shims/1.20.2/src/top/dext/centurybridge/rt/v1_20_2/CbGuiConstants.java"
CLS = "top/dext/centurybridge/rt/v1_20_2/CbGuiConstants"
FRAGMENT = "core/segments/shims-1.20.2.json.d/s1client.json"


def class_names(data):
    """intermediary -> mojang, by the last release that still had the class (step_check's naming)"""
    names = {}
    for f in ("inter-to-mojang-classes.tsv", "inter-to-mojang-1.21.11.tsv"):
        p = os.path.join(data, "mappings", f)
        if not os.path.exists(p):
            continue
        for line in open(p, encoding="utf-8"):
            r = line.rstrip("\n").split("\t")
            if len(r) >= 3 and r[0] == "C" and r[1] not in names:
                names[r[1]] = r[2]
    return names


def moj(names, c):
    if c in names:
        return names[c]
    d = c.rfind("$")
    return moj(names, c[:d]) + c[d:] if d > 0 else c


def constants(raw):
    """field name+desc -> ConstantValue of one class file"""
    n = struct.unpack(">H", raw[8:10])[0]
    i, idx = 10, 1
    pool = {}
    while idx < n:
        tag = raw[i]
        if tag == 1:
            ln = struct.unpack(">H", raw[i + 1:i + 3])[0]
            pool[idx] = ("utf8", raw[i + 3:i + 3 + ln].decode("utf-8"))
            i += 3 + ln
        elif tag == 3:
            pool[idx] = ("int", struct.unpack(">i", raw[i + 1:i + 5])[0])
            i += 5
        elif tag == 4:
            pool[idx] = ("float", struct.unpack(">f", raw[i + 1:i + 5])[0])
            i += 5
        elif tag == 5:
            pool[idx] = ("long", struct.unpack(">q", raw[i + 1:i + 9])[0])
            i += 9
            idx += 1
        elif tag == 6:
            pool[idx] = ("double", struct.unpack(">d", raw[i + 1:i + 9])[0])
            i += 9
            idx += 1
        elif tag == 8:
            pool[idx] = ("string", struct.unpack(">H", raw[i + 1:i + 3])[0])
            i += 3
        elif tag in (7, 16, 19, 20):
            i += 3
        elif tag in (9, 10, 11, 12, 17, 18):
            i += 5
        elif tag == 15:
            i += 4
        else:
            raise ValueError(f"constant pool tag {tag}")
        idx += 1
    i += 6  # access, this, super
    i += 2 + 2 * struct.unpack(">H", raw[i:i + 2])[0]  # interfaces
    out = {}
    fields = struct.unpack(">H", raw[i:i + 2])[0]
    i += 2
    for _ in range(fields):
        name = pool[struct.unpack(">H", raw[i + 2:i + 4])[0]][1]
        desc = pool[struct.unpack(">H", raw[i + 4:i + 6])[0]][1]
        attrs = struct.unpack(">H", raw[i + 6:i + 8])[0]
        i += 8
        for _ in range(attrs):
            an = pool[struct.unpack(">H", raw[i:i + 2])[0]][1]
            ln = struct.unpack(">I", raw[i + 2:i + 6])[0]
            if an == "ConstantValue":
                kind, v = pool[struct.unpack(">H", raw[i + 6:i + 8])[0]]
                if kind == "string":
                    v = pool[v][1]
                out[name + ":" + desc] = v
            i += 6 + ln
    return out


def java_literal(desc, v):
    if desc == "Ljava/lang/String;":
        return '"' + v.replace("\\", "\\\\").replace('"', '\\"') + '"'
    if desc in ("I", "S", "B", "C"):
        return str(v)
    if desc == "Z":
        return "true" if v else "false"
    if desc == "J":
        return f"{v}L"
    if desc == "F":
        return repr(float(v)) + "F"
    if desc == "D":
        return repr(float(v))
    raise ValueError(desc)


JTYPE = {"I": "int", "S": "short", "B": "byte", "C": "char", "Z": "boolean", "J": "long", "F": "float", "D": "double",
         "Ljava/lang/String;": "String"}


def main():
    data = sys.argv[1] if len(sys.argv) > 1 else os.environ.get("CB_DATA", "data")
    damage = sys.argv[2] if len(sys.argv) > 2 else "core/segments/damage/1.20.1__1.20.2.damage.tsv"
    names = class_names(data)
    rows = []
    for i, line in enumerate(open(damage, encoding="utf-8")):
        r = line.rstrip("\n").split("\t")
        if i == 0 or r[0] not in ("get-static", "get-static-protected") or "FIELD" not in r[4].split(","):
            continue
        if re.search(AREA, moj(names, r[1])):
            rows.append((r[1], r[2], r[3]))
    entries = []
    with zipfile.ZipFile(os.path.join(data, "jars", "client-1.20.1-intermediary-code.jar")) as z:
        for owner, name, desc in sorted(set(rows)):
            consts = constants(z.read(owner + ".class"))
            v = consts.get(name + ":" + desc)
            if v is None or desc not in JTYPE:
                continue
            entries.append((owner, name, desc, v))
    seen = {}
    for owner, name, desc, v in entries:
        if name in seen and seen[name] != (desc, v):
            raise SystemExit(f"{name}: two owners with different constants -- one holder class cannot serve both")
        seen[name] = (desc, v)
    with open(OUT, "w", encoding="utf-8", newline="\n") as f:
        f.write("package top.dext.centurybridge.rt.v1_20_2;\n\n")
        f.write("/**\n * Generated by core/tools/gen_gui_constants.py -- do not edit. The 1.20.1 values of client GUI\n")
        f.write(" * constants 1.20.2 removed, read off their ConstantValue attributes in the 1.20.1 class files;\n")
        f.write(" * fieldRedirects send a mod's GETSTATIC of the dead field here.\n */\npublic final class CbGuiConstants {\n\n")
        for owner, name, desc, v in entries:
            f.write(f"    /** was {owner}.{name} ({moj(names, owner).rsplit('/', 1)[-1]}) */\n")
            f.write(f"    public static final {JTYPE[desc]} {name} = {java_literal(desc, v)};\n\n")
        f.write("    private CbGuiConstants() {\n    }\n}\n")
    frag = json.load(open(FRAGMENT, encoding="utf-8")) if os.path.exists(FRAGMENT) else {}
    fr = frag.setdefault("fieldRedirects", {})
    for k in [k for k, v in fr.items() if v == CLS]:
        del fr[k]
    for owner, name, desc, v in entries:
        fr[f"{owner}.{name}:{desc}"] = CLS
    with open(FRAGMENT, "w", encoding="utf-8", newline="\n") as f:
        json.dump(frag, f, indent=1, ensure_ascii=False)
        f.write("\n")
    for owner, name, desc, v in entries:
        print(f"{moj(names, owner).rsplit('/', 1)[-1]}.{name}:{desc} = {v!r}")
    print(f"{len(entries)} constants -> {OUT}, fieldRedirects -> {FRAGMENT}")


if __name__ == "__main__":
    main()
