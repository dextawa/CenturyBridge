"""Compile-time constants a release took away or changed, answered with the previous release's value.

A damage row that reads a static field (get-static) whose declaration in V carries a ConstantValue
-- a primitive or String constant (BeehiveBlockEntity.TAG_FLOWER_POS = "FlowerPos" in 1.20.4,
private "flower_pos" in 1.20.5; LevelEvent.PARTICLES_PLANT_GROWTH = 2005, gone) -- is a value,
not a link to W's member: what V's field held is exactly what a mod reading it got. The read is
redirected (fieldRedirects) to a same-named constant on one runtime class of the area holding V's
value, whatever W did with its own field (removed it, narrowed it, renamed the NBT key it names).

The generator writes that class and replaces, in the area's wiring fragment, every fieldRedirect
pointing at it -- the rest of the fragment is left as it is.

  python gen_step_constants.py <damage.tsv> <V jar> <area fragment.json> <runtime class> <src root> [<area regex>]
      runtime class: internal name, e.g. top/dext/centurybridge/step/v1_20_5/rt/CbBlockConstants
"""
import json
import os
import re
import struct
import sys
import zipfile

DATA = os.environ.get("CB_DATA", "data")
NAMES = os.path.join(DATA, "mappings/inter-to-mojang-classes.tsv")
WALL = os.path.join(DATA, "mappings/inter-to-mojang-1.21.11.tsv")


def mojang_names():
    cm = {}
    for p in (NAMES, WALL):
        if os.path.exists(p):
            for line in open(p, encoding="utf-8"):
                f = line.rstrip("\n").split("\t")
                if len(f) >= 3 and f[0] == "C":
                    cm.setdefault(f[1], f[2])

    def moj(c):
        if c in cm:
            return cm[c]
        d = c.rfind("$")
        return moj(c[:d]) + c[d:] if d > 0 else c
    return moj


def constants(b):
    """{(field name, desc): value} of every static field with a ConstantValue attribute"""
    n = struct.unpack(">H", b[8:10])[0]
    i, idx = 10, 1
    pool = {}
    while idx < n:
        tag = b[i]
        if tag == 1:
            ln = struct.unpack(">H", b[i + 1:i + 3])[0]
            pool[idx] = ("utf8", b[i + 3:i + 3 + ln].decode("utf-8", "surrogatepass"))
            i += 3 + ln
        elif tag == 3:
            pool[idx] = ("int", struct.unpack(">i", b[i + 1:i + 5])[0])
            i += 5
        elif tag == 4:
            pool[idx] = ("float", struct.unpack(">f", b[i + 1:i + 5])[0])
            i += 5
        elif tag == 5:
            pool[idx] = ("long", struct.unpack(">q", b[i + 1:i + 9])[0])
            i += 9
            idx += 1
        elif tag == 6:
            pool[idx] = ("double", struct.unpack(">d", b[i + 1:i + 9])[0])
            i += 9
            idx += 1
        elif tag == 8:
            pool[idx] = ("string", struct.unpack(">H", b[i + 1:i + 3])[0])
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

    def u2(at):
        return struct.unpack(">H", b[at:at + 2])[0]
    i += 6
    i += 2 + 2 * u2(i)
    out = {}
    cnt = u2(i)
    i += 2
    for _ in range(cnt):
        acc, nm, ds = u2(i), pool[u2(i + 2)][1], pool[u2(i + 4)][1]
        i += 6
        na = u2(i)
        i += 2
        for _ in range(na):
            an = pool[u2(i)][1]
            ln = struct.unpack(">I", b[i + 2:i + 6])[0]
            if an == "ConstantValue" and acc & 0x8:
                kind, v = pool[u2(i + 6)]
                out[(nm, ds)] = pool[v][1] if kind == "string" else v
            i += 6 + ln
    return out


def java_literal(desc, v):
    if desc == "Ljava/lang/String;":
        return json.dumps(v, ensure_ascii=True)
    if desc == "Z":
        return "true" if v else "false"
    if desc == "C":
        return f"(char) {v}"
    if desc == "B":
        return f"(byte) {v}"
    if desc == "S":
        return f"(short) {v}"
    if desc == "J":
        return f"{v}L"
    if desc == "F":
        return "Float.NaN" if v != v else f"{v!r}F" if abs(v) != float("inf") else ("Float.POSITIVE_INFINITY" if v > 0 else "Float.NEGATIVE_INFINITY")
    if desc == "D":
        return "Double.NaN" if v != v else f"{v!r}" if abs(v) != float("inf") else ("Double.POSITIVE_INFINITY" if v > 0 else "Double.NEGATIVE_INFINITY")
    return str(v)


JTYPE = {"Ljava/lang/String;": "String", "Z": "boolean", "C": "char", "B": "byte", "S": "short", "I": "int",
         "J": "long", "F": "float", "D": "double"}


def main():
    damage, vjar, fragment, rtcls, src = sys.argv[1:6]
    area = re.compile(sys.argv[6]) if len(sys.argv) > 6 else None
    moj = mojang_names()
    rows = []
    for i, line in enumerate(open(damage, encoding="utf-8")):
        if i == 0:
            continue
        r = line.rstrip("\n").split("\t")
        if r[0] == "get-static" and (not area or area.search(moj(r[1]))):
            rows.append(r)
    consts = {}
    found = []
    with zipfile.ZipFile(vjar) as z:
        for r in rows:
            owner, name, desc = r[1], r[2], r[3]
            try:
                b = z.read(owner + ".class")
            except KeyError:
                continue
            c = constants(b)
            if (name, desc) in c:
                found.append((owner, name, desc, c[(name, desc)]))
    by_name = {}
    for owner, name, desc, v in found:
        if name in by_name and by_name[name][1:] != (desc, v):
            raise SystemExit(f"two constants named {name} with different values: {by_name[name]} vs {(owner, desc, v)}")
        by_name[name] = (owner, desc, v)
    pkg, simple = rtcls.rsplit("/", 1)
    lines = [f"package {pkg.replace('/', '.')};", "",
             "/**",
             " * Compile-time constants of the previous release a mod read, with the value it read then:",
             " * the release this layer runs on removed, narrowed or changed them. Generated by",
             " * core/tools/gen_step_constants.py from the step's damage table; every value is the",
             " * ConstantValue of the field in the previous release's class file.",
             " */",
             f"public final class {simple} {{", ""]
    for name in sorted(by_name, key=lambda n: (moj(by_name[n][0]), n)):
        owner, desc, v = by_name[name]
        lines.append(f"    /** {moj(owner)}.{name} */")
        lines.append(f"    public static final {JTYPE[desc]} {name} = {java_literal(desc, v)};")
        lines.append("")
    lines.append(f"    private {simple}() {{")
    lines.append("    }")
    lines.append("}")
    path = os.path.join(src, rtcls + ".java")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    open(path, "w", encoding="utf-8", newline="\n").write("\n".join(lines) + "\n")
    frag = json.load(open(fragment, encoding="utf-8")) if os.path.exists(fragment) else {}
    fr = {k: v for k, v in frag.get("fieldRedirects", {}).items() if v != rtcls}
    for owner, name, desc, v in found:
        fr[f"{owner}.{name}:{desc}"] = rtcls
    frag["fieldRedirects"] = dict(sorted(fr.items()))
    open(fragment, "w", encoding="utf-8", newline="\n").write(json.dumps(frag, indent=1) + "\n")
    print(f"{len(rows)} get-static rows, {len(found)} constants of V -> {path}, fieldRedirects in {fragment}")


if __name__ == "__main__":
    main()
