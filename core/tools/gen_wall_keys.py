"""ResourceKey constants 26.1 dropped from the datagen key holders: given back as the keys 1.21.11 held.

A ResourceKey is a value -- a registry and an id -- and 1.21.11's VegetationFeatures.TREES_PLAINS (or
TradeRebalanceEnchantmentProviders.*) was ResourceKey.create(<registry>, minecraft:<id>). 26.1 removed some
of those constants (the features or providers were renamed or folded); a 1.21.11 mod reading one gets the
same key back, which names what it named in 1.21.11 -- whether 26.1's data registers that id is data, as it
was for any key in 1.21.11.

Only get-static rows of the step's damage table typed ResourceKey whose owner 26.1 lacks the field are
considered, and only where 1.21.11's static initializer sets the field from `ldc "<id>"; invokestatic
<helper>(String)ResourceKey` and the helper is exactly ResourceKey.create(<Registries field>,
Identifier.withDefaultNamespace(name)). Everything else is listed in the report.

  python gen_wall_keys.py <damage.tsv> <V code jar> <W jar> <wall table> <fragment.json> <runtime class> <src root> <report.tsv>

Regenerating (from the repo root):
  python3 core/tools/gen_wall_keys.py core/segments/damage/1.21.11__26.1.damage.tsv \
      data/jars/client-1.21.11-intermediary-code.jar data/jars/client-26.1.jar data/mappings/inter-to-mojang-1.21.11-26.1.tsv \
      core/segments/steps/1.21.11__26.1.json.d/swall-keys-gen.json top/dext/centurybridge/step/v26_1/rt/CbWallKeys \
      core/shims/steps/26.1/src <tmp>/keys-report.tsv
"""
import json
import os
import re
import subprocess
import sys

KEY = "Lnet/minecraft/class_5321;"
LDC = re.compile(r"ldc(?:_w)?\s+#\d+\s+// String (\S+)")
INV = re.compile(r"invokestatic\s+#\d+\s+// (?:Interface)?Method ([\w/$]+)\.(\w+):\(Ljava/lang/String;\)Lnet/minecraft/class_5321;")
PUT = re.compile(r"putstatic\s+#\d+\s+// Field (field_\d+):Lnet/minecraft/class_5321;")
REG = re.compile(r"getstatic\s+#\d+\s+// Field net/minecraft/class_7924\.(field_\d+):Lnet/minecraft/class_5321;")


def javap(jar, cls):
    return subprocess.run(["javap", "-c", "-p", "-cp", jar, cls.replace("/", ".")],
                          capture_output=True, text=True, errors="replace").stdout


def method_body(text, name):
    m = re.search(r"\n  [^\n]*\b" + re.escape(name) + r"\(java\.lang\.String\);\n(.*?)\n\n", text, re.S)
    return m.group(1) if m else ""


def main():
    dmg, vjar, wjar, table, frag, rtcls, src, report = sys.argv[1:9]
    fmap = {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "F":
            fmap[f[1]] = f[2].rsplit(".", 1)[1]
    cmap = {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "C":
            cmap[f[1]] = f[2]
    declared = {}

    def w_has(owner, field):
        wo = cmap.get(owner, owner)
        if wo not in declared:
            out = subprocess.run(["javap", "-p", "-cp", wjar, wo.replace("/", ".")],
                                 capture_output=True, text=True, errors="replace").stdout
            declared[wo] = set(re.findall(r" (\w+);$", out, re.M))
        return fmap.get(f"{owner}.{field}", field) in declared[wo]

    owners = {}
    for line in open(dmg, encoding="utf-8"):
        r = line.rstrip("\n").split("\t")
        if len(r) >= 4 and r[0] == "get-static" and r[3] == KEY and not w_has(r[1], r[2]):
            owners.setdefault(r[1], set()).add(r[2])
    rep, getters = [], {}
    helpers = {}
    for owner, fields in sorted(owners.items()):
        text = javap(vjar, owner)
        clinit = text[text.find("static {};"):]
        last, inv = None, None
        found = {}
        for line in clinit.splitlines():
            m = LDC.search(line)
            if m:
                last, inv = m.group(1), None
                continue
            m = INV.search(line)
            if m and last:
                inv = (m.group(1), m.group(2))
                continue
            m = PUT.search(line)
            if m:
                if last and inv:
                    found[m.group(1)] = (last, inv)
                last, inv = None, None
        for field in sorted(fields):
            if field not in found:
                rep.append((owner, field, "no ldc/helper/putstatic pattern in 1.21.11's static initializer"))
                continue
            ident, (hown, hname) = found[field]
            hk = (hown, hname)
            if hk not in helpers:
                body = method_body(javap(vjar, hown), hname)
                regs = REG.findall(body)
                ok = (len(regs) == 1 and "class_2960.method_60656:(Ljava/lang/String;)" in body
                      and "class_5321.method_29179:" in body and body.count("invoke") == 2)
                helpers[hk] = regs[0] if ok else None
            reg = helpers[hk]
            if not reg:
                rep.append((owner, field, f"helper {hown}.{hname} is not ResourceKey.create(Registries.X, withDefaultNamespace(name))"))
                continue
            wreg = fmap.get(f"net/minecraft/class_7924.{reg}")
            if not wreg:
                rep.append((owner, field, f"no 26.1 name for Registries.{reg}"))
                continue
            getters[field] = (owner, ident, wreg)
    pkg, simple = rtcls.rsplit("/", 1)
    lines = ["package " + pkg.replace("/", ".") + ";", "",
             "import net.minecraft.core.registries.Registries;",
             "import net.minecraft.resources.Identifier;",
             "import net.minecraft.resources.ResourceKey;", "",
             "/**",
             " * Generated by core/tools/gen_wall_keys.py -- do not edit; regenerate with the command in its header.",
             " *",
             " * ResourceKey constants 26.1 dropped, given back as the keys 1.21.11 held (registry and id).",
             " */",
             "@SuppressWarnings({\"rawtypes\", \"unused\"})",
             "public final class " + simple + " {", "",
             "    private " + simple + "() {", "    }"]
    for field in sorted(getters):
        owner, ident, wreg = getters[field]
        lines += ["", f"    /** {owner}.{field}: minecraft:{ident} */",
                  f"    public static ResourceKey {field}() {{",
                  f"        return ResourceKey.create(Registries.{wreg}, Identifier.withDefaultNamespace(\"{ident}\"));",
                  "    }"]
    lines += ["}", ""]
    path = os.path.join(src, rtcls + ".java")
    with open(path, "w", encoding="utf-8", newline="\n") as fh:
        fh.write("\n".join(lines))
    out = {"fieldGetters": {f"{o}.{f}:{KEY}": rtcls for f, (o, _, _) in sorted(getters.items())}}
    with open(frag, "w", encoding="utf-8", newline="\n") as fh:
        json.dump(out, fh, indent=1)
        fh.write("\n")
    with open(report, "w", encoding="utf-8", newline="\n") as fh:
        for row in rep:
            fh.write("\t".join(row) + "\n")
    print(f"{len(getters)} keys -> {frag}; {len(rep)} left (report {report})")


if __name__ == "__main__":
    main()
