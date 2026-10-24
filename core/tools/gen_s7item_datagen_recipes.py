"""1.21.1's static RecipeProvider helpers on 1.21.2's instance ones (s7item, step 1.21.1 -> 1.21.2; dev-time datagen).

1.21.1's RecipeProvider (class_2446) had protected static helpers, most taking the RecipeOutput first; 1.21.2 made them
instance methods of a provider bound to its registries and output, under the same names. This generator compares both
jars' declarations (javap -s) and writes:
  - core/shims/steps/1.21.2/src/top/dext/centurybridge/step/v1_21_2/rt/CbItemDatagenRecipes.java (generated): a 1.21.2
    RecipeProvider whose statics forward each 1.21.1 helper to the provider bound to that output (CbItemDatagenRecipes.of);
  - the staticRedirects of those helpers (the fragment given).
Helpers 1.21.2 kept static with the same descriptor need nothing; the few without a counterpart are written by hand in
CbItemDatagenRecipes outside the generated block (listed in MANUAL).

  python gen_s7item_datagen_recipes.py <V code jar> <W code jar> <java out> <fragment out>
"""
import json
import re
import subprocess
import sys

PKG = "top.dext.centurybridge.step.v1_21_2.rt"
RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemDatagenRecipes"
MANUAL_RT = RT
OWNER = "net/minecraft/class_2446"
OUT = "Lnet/minecraft/class_8790;"
# 1.21.1 helpers whose 1.21.2 form differs beyond the output: written by hand
MANUAL = {
    "method_48530": "(Lnet/minecraft/class_8790;Lnet/minecraft/class_1792;Lnet/minecraft/class_2960;)V",
    "method_48532": "(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_6862;)V",
    "method_36546": "(Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)Lnet/minecraft/class_5797;",
}


def members(jar):
    text = subprocess.run(["javap", "-p", "-s", "-cp", jar, "net.minecraft.class_2446"], capture_output=True, text=True).stdout
    out = []
    lines = text.splitlines()
    for i, line in enumerate(lines):
        m = re.search(r"\b(method_\d+)\(", line)
        if m and i + 1 < len(lines) and "descriptor:" in lines[i + 1]:
            desc = lines[i + 1].split("descriptor:")[1].strip()
            out.append((m.group(1), desc, " static " in " " + line, "private" in line, "lambda$" in line))
    return out


def params(desc):
    inner = desc[1:desc.index(")")]
    out, i = [], 0
    while i < len(inner):
        j = i
        while inner[j] == "[":
            j += 1
        if inner[j] == "L":
            j = inner.index(";", j)
        out.append(inner[i:j + 1])
        i = j + 1
    return out


def java_type(d):
    dims = 0
    while d.startswith("["):
        dims += 1
        d = d[1:]
    prim = {"I": "int", "Z": "boolean", "F": "float", "D": "double", "J": "long", "V": "void", "B": "byte", "S": "short",
            "C": "char"}
    t = prim.get(d) or d[1:-1].replace("/", ".").replace("$", ".")
    return t + "[]" * dims


def main():
    vjar, wjar, java_out, frag_out = sys.argv[1:5]
    v = [m for m in members(vjar) if m[2] and not m[3] and not m[4]]
    w = {}
    for m in members(wjar):
        if not m[3] and not m[4]:
            w.setdefault(m[0], []).append(m)
    methods, wiring, report = [], {}, []
    for name, desc, _, _, _ in v:
        ps = params(desc)
        ret = desc[desc.index(")") + 1:]
        if name in MANUAL:
            wiring[OWNER + "." + name + desc] = MANUAL_RT + "#" + name
            continue
        cands = w.get(name, [])
        same_static = [c for c in cands if c[2] and c[1] == desc]
        if same_static:
            continue
        with_out = ps and ps[0] == OUT
        want = "(" + "".join(ps[1:] if with_out else ps) + ")" + ret
        inst = [c for c in cands if not c[2] and c[1] == want]
        if not inst:
            report.append(name + desc + ": no 1.21.2 counterpart")
            continue
        args = ["arg%d" % i for i in range(len(ps))]
        decl = ", ".join(java_type(p) + " " + a for p, a in zip(ps, args))
        call_args = ", ".join(args[1:] if with_out else args)
        target = "of(arg0)" if with_out else "current()"
        # a static of the same signature would hide 1.21.2's inherited instance method: a helper without the output
        # forwards under a name of its own
        static_name = name if with_out else "legacy_" + name
        body = ("" if ret == "V" else "return ") + target + "." + name + "(" + call_args + ");"
        methods.append("    /** 1.21.1's RecipeProvider." + name + desc + " */\n"
                       "    public static " + java_type(ret) + " " + static_name + "(" + decl + ") {\n"
                       "        " + body + "\n    }\n")
        wiring[OWNER + "." + name + desc] = RT + "#" + static_name
    java = open(java_out, encoding="utf-8").read()
    begin, end = "    // ---- BEGIN GENERATED (gen_s7item_datagen_recipes.py) ----\n", "    // ---- END GENERATED ----\n"
    java = java[:java.index(begin) + len(begin)] + "\n".join(methods) + java[java.index(end):]
    open(java_out, "w", encoding="utf-8", newline="\n").write(java)
    json.dump({"staticRedirects": wiring}, open(frag_out, "w", encoding="utf-8", newline="\n"), indent=1)
    print("%d forwarded, %d manual, %d without counterpart" % (len(methods), len(MANUAL), len(report)))
    for r in report:
        print("  " + r)


if __name__ == "__main__":
    main()
