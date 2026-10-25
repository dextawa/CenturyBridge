"""Wiring for 1.21.4's NBT accessors on 1.21.5 (s5, step 1.21.4 -> 1.21.5; semantics in rt/CbNbt).

  callReshapes        CompoundTag's typed getters getByte .. getLongArray, getCompound and getBoolean keep their
                      names and answer an Optional since 1.21.5; the call takes the Optional and its value or 1.21.4's
                      fallback (0, "", false, an empty array, a new CompoundTag -- javap V class_2487: each getter
                      tested the tag id and answered that fallback otherwise, which is what 1.21.5's Optional is
                      empty for). Converters CbNbt.opt*.
  fieldGetters        Tag.TAG_ANY_NUMERIC (99)
  instanceRedirects   the CompoundTag / ListTag members 1.21.5 dropped or whose 1.21.4 body no 1.21.5 member keeps:
                      getTagType, contains(key, type), getList(key, type), the UUID members, the List-taking array
                      puts; ListTag's getters, the collection tags' set / add (1.21.4's AbstractList members) and the
                      array tags' typed set / add, (1.21.4 answered only for the exact element type), getElementType of
                      the collection tags; Tag / StringTag.getAsString
  staticRedirects     NbtUtils' UUID and BlockPos helpers, TagParser.parseTag
  ctorRedirects       ByteArrayTag / IntArrayTag / LongArrayTag from a List (or LongSet)

  python core/tools/gen_s5_nbt.py core/segments/steps/1.21.4__1.21.5.json.d/s5-nbt-gen.json
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_5/rt/CbNbt"
C = "net/minecraft/class_2487"
L = "net/minecraft/class_2499"
S = "Ljava/lang/String;"
OPT = "Ljava/util/Optional;"

# CompoundTag getter: name, V return descriptor, converter name
GETTERS = [
    ("method_10571", "B", "optByte"),
    ("method_10568", "S", "optShort"),
    ("method_10550", "I", "optInt"),
    ("method_10537", "J", "optLong"),
    ("method_10583", "F", "optFloat"),
    ("method_10574", "D", "optDouble"),
    ("method_10558", S, "optString"),
    ("method_10577", "Z", "optBoolean"),
    ("method_10547", "[B", "byteArray"),
    ("method_10561", "[I", "intArray"),
    ("method_10565", "[J", "longArray"),
    ("method_10562", "L" + C + ";", "optCompound"),
]

COMPOUND_REDIRECTS = [
    "method_10540(" + S + ")B",
    "method_10573(" + S + "I)Z",
    "method_10554(" + S + "I)L" + L + ";",
    "method_25926(" + S + ")Ljava/util/UUID;",
    "method_25927(" + S + "Ljava/util/UUID;)V",
    "method_25928(" + S + ")Z",
    "method_36110(" + S + "Ljava/util/List;)V",
    "method_10572(" + S + "Ljava/util/List;)V",
    "method_10538(" + S + "Ljava/util/List;)V",
]

LIST_REDIRECTS = [
    "method_10602(I)L" + C + ";",
    "method_10603(I)L" + L + ";",
    "method_10609(I)S",
    "method_10600(I)I",
    "method_36111(I)[I",
    "method_36112(I)[J",
    "method_10611(I)D",
    "method_10604(I)F",
    "method_10608(I)" + S,
]

COLLECTIONS = ["net/minecraft/class_2499", "net/minecraft/class_2479", "net/minecraft/class_2495", "net/minecraft/class_2501"]

NBT_UTILS = "net/minecraft/class_2512"


def main():
    out = {"_generated": "core/tools/gen_s5_nbt.py -- regenerate, never hand-edit",
           "callReshapes": {}, "instanceRedirects": {}, "interfaceCallRedirects": {}, "staticRedirects": {},
           "ctorRedirects": {}}
    for name, ret, conv in GETTERS:
        out["callReshapes"][C + "." + name + "(" + S + ")" + ret] = {
            "desc": "(" + S + ")" + OPT,
            "ops": ["keep"],
            "ret": "conv " + RT + "#" + conv + "(" + OPT + ")" + ret,
            "basis": "CompoundTag." + name + " answers an Optional since 1.21.5, empty where 1.21.4's body answered "
                     "its fallback (rt/CbNbt." + conv + ")",
        }
    for m in COMPOUND_REDIRECTS:
        out["instanceRedirects"][C + "." + m] = RT
    for m in LIST_REDIRECTS:
        out["instanceRedirects"][L + "." + m] = RT
    for c in COLLECTIONS:
        out["instanceRedirects"][c + ".method_10601()B"] = RT
    # 1.21.4's CollectionTag was an abstract class (AbstractList): its calls are virtual, keyed as instance calls
    out["instanceRedirects"]["net/minecraft/class_2483.method_10601()B"] = RT
    # 1.21.4's AbstractList set / add of the collection tags, and the array tags' typed ones
    for m in ["method_10606(ILnet/minecraft/class_2520;)Lnet/minecraft/class_2520;", "method_10531(ILnet/minecraft/class_2520;)V"]:
        out["instanceRedirects"]["net/minecraft/class_2483." + m] = RT
        for c in COLLECTIONS:
            out["instanceRedirects"][c + "." + m] = RT
    for c, t, s_, a in [("class_2479", "class_2481", "method_17803", "method_17805"),
                        ("class_2495", "class_2497", "method_17806", "method_17808"),
                        ("class_2501", "class_2503", "method_17810", "method_17812")]:
        tt = "Lnet/minecraft/" + t + ";"
        out["instanceRedirects"]["net/minecraft/" + c + "." + s_ + "(I" + tt + ")" + tt] = RT
        out["instanceRedirects"]["net/minecraft/" + c + "." + a + "(I" + tt + ")V"] = RT
    out["fieldGetters"] = {"net/minecraft/class_2520.field_33263:B": RT}
    out["interfaceCallRedirects"]["net/minecraft/class_2520.method_10714()" + S] = RT
    out["instanceRedirects"]["net/minecraft/class_2519.method_10714()" + S] = RT
    for m in ["method_25929(Ljava/util/UUID;)Lnet/minecraft/class_2495;",
              "method_25930(Lnet/minecraft/class_2520;)Ljava/util/UUID;",
              "method_10691(L" + C + ";" + S + ")Ljava/util/Optional;",
              "method_10692(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2520;"]:
        out["staticRedirects"][NBT_UTILS + "." + m] = RT
    out["staticRedirects"]["net/minecraft/class_2522.method_10718(" + S + ")L" + C + ";"] = RT
    out["ctorRedirects"]["net/minecraft/class_2479.<init>(Ljava/util/List;)V"] = RT + "#newByteArray"
    out["ctorRedirects"]["net/minecraft/class_2495.<init>(Ljava/util/List;)V"] = RT + "#newIntArray"
    out["ctorRedirects"]["net/minecraft/class_2501.<init>(Ljava/util/List;)V"] = RT + "#newLongArray"
    out["ctorRedirects"]["net/minecraft/class_2501.<init>(Lit/unimi/dsi/fastutil/longs/LongSet;)V"] = RT + "#newLongArraySet"
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
