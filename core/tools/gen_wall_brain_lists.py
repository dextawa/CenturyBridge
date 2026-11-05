"""1.21.11's brained mobs' SENSOR_TYPES / MEMORY_TYPES at the 1.21.11 -> 26.1 wall: given back, read and write.

1.21.11 built each brained mob's brain from two static lists, SENSOR_TYPES and MEMORY_TYPES, in the static
initializer of the mob (Piglin, Villager, ...) or of its AI class (WardenAi, CamelAi, ...); brainProvider() handed
them to Brain.provider(memories, sensors). 26.1 keeps none of the 40 fields: a brain registers its provider's own
short memory list, each sensor's requires() and each behaviour's getRequiredMemories() (javap of 26.1 Brain.<init>
and addActivity), so 26.1's set is derived and per brain. This writes, from the 1.21.11 bytecode:
  - one class per list owner, rt/brainlists/CbWallBrainList<Owner>, whose build() repeats the owner's static
    initializer's list constructions -- the same element reads, in the same order, through the same factory
    (ImmutableList.of / List.of) -- so a converted mod's injector anchored on those reads moves there
    (mixinAnchorMoves "<owner>.<clinit>()V" -> build()V) and a mod's @ModifyArg / @Redirect on the factory call acts
    on what build() makes; a getter and a setter per field (fieldGetters / staticFieldSetters, also serving
    mixins' @Shadow / @Accessor on them, private fields included);
  - each class registers with rt/CbWallBrainExtras: 1.21.11's lists (base) and the mob class whose brainProvider()
    read them (from the 1.21.11 bytecode of every brainProvider()). What a mod's write or injector puts in the list
    beyond base is added to the brains 26.1 builds for that mob (CbWallBrainExtrasMixin on Brain.Provider.makeBrain:
    a union with 26.1's own set, never a replacement); what it takes out cannot take effect and is reported.
    build() carries @CbVanillaReads naming that mixin, so MixinJudge counts an injector moved there as reaching
    vanilla's brains.

  python3 gen_wall_brain_lists.py <V classes dir> <W jar> <wall table> <javap> <src root> <fragment.json> <report.tsv>

Regenerating (on beta2, from a work dir holding core/; vx = unpacked client-1.21.11-intermediary-code.jar):
  python3 core/tools/gen_wall_brain_lists.py vx ~/cbwork/data/jars/client-26.1.jar \\
      ~/cbwork/data/mappings/inter-to-mojang-1.21.11-26.1.tsv ~/cbtest/jdk-25.0.4.1+1/bin/javap core/shims/steps/26.1/src \\
      core/segments/steps/1.21.11__26.1.json.d/swall13-brainlists-gen.json brainlists-report.tsv
"""
import json
import os
import re
import subprocess
import sys
import zipfile

MEM = "net/minecraft/class_4140"
SENS = "net/minecraft/class_4149"
PKG = "top/dext/centurybridge/step/v26_1/rt/brainlists"
PROVIDER = "method_28306"


def javap(jp, cp, cls, private=True):
    args = [jp, "-c", "-cp", cp] + (["-p"] if private else []) + [cls.replace("/", ".")]
    return subprocess.run(args, capture_output=True, text=True).stdout


def methods(text):
    """{header line: [instruction lines]} of a javap -c listing"""
    out, cur = {}, None
    for line in text.splitlines():
        if re.match(r"^  \S", line) and line.rstrip().endswith(";") and "(" in line or line.strip() == "static {};":
            cur = line.strip()
            out[cur] = []
        elif cur is not None and re.match(r"^\s+\d+: ", line):
            out[cur].append(line)
    return out


def main():
    vdir, wjar, table, jp, src, frag, report = sys.argv[1:8]
    cls, fld = {}, {}
    for line in open(table, encoding="utf-8"):
        p = line.rstrip("\n").split("\t")
        if len(p) >= 3 and p[0] == "C":
            cls[p[1]] = p[2]
        elif len(p) >= 3 and p[0] == "F":
            fld[p[1]] = p[2].rsplit(".", 1)[1]
    with zipfile.ZipFile(wjar) as z:
        wnames = {n[:-6] for n in z.namelist() if n.endswith(".class")}

    # the list fields: static fields of a 1.21.11 class typed List / ImmutableList of MemoryModuleType / SensorType
    owners = {}
    for root, _, files in os.walk(os.path.join(vdir, "net")):
        for f in files:
            if not f.endswith(".class"):
                continue
            path = os.path.join(root, f)
            data = open(path, "rb").read()
            if MEM.encode() not in data and SENS.encode() not in data:
                continue
            c = os.path.relpath(path, vdir)[:-6].replace(os.sep, "/")
            sig = subprocess.run([jp, "-p", "-s", "-cp", vdir, c.replace("/", ".")], capture_output=True, text=True).stdout
            lines = sig.splitlines()
            for i, l in enumerate(lines):
                m = re.match(r"^\s+(private|protected|public)? ?static final (com\.google\.common\.collect\.ImmutableList|java\.util\.List)<.*(class_4140|class_4149).*> (field_\d+);", l)
                if m:
                    owners.setdefault(c, []).append((m.group(4), m.group(2).replace(".", "/"), m.group(3) == "class_4140", m.group(1) or "package"))

    # which mob class's brainProvider() reads each list field
    readers = {}
    for root, _, files in os.walk(os.path.join(vdir, "net")):
        for f in files:
            if not f.endswith(".class"):
                continue
            path = os.path.join(root, f)
            data = open(path, "rb").read()
            if PROVIDER.encode() not in data or not any(o.encode() in data for o in owners):
                continue
            c = os.path.relpath(path, vdir)[:-6].replace(os.sep, "/")
            for head, body in methods(javap(jp, vdir, c)).items():
                if " " + PROVIDER + "(" not in head:
                    continue
                for ins in body:
                    m = re.search(r"getstatic .*// Field (net/minecraft/[\w$]+)\.(field_\d+):", ins)
                    if m and m.group(1) in owners:
                        readers.setdefault((m.group(1), m.group(2)), set()).add(c)
                    m = re.search(r"getstatic .*// Field (field_\d+):", ins)
                    if m and c in owners:
                        readers.setdefault((c, m.group(1)), set()).add(c)

    getters, setters, moves, rep = {}, {}, {}, []
    for owner in sorted(owners):
        fields = {f[0]: f for f in owners[owner]}
        clinit = methods(javap(jp, vdir, owner)).get("static {};", [])
        lists, pending = [], []
        for ins in clinit:
            m = re.search(r"getstatic .*// Field (net/minecraft/class_41(40|49))\.(field_\d+):", ins)
            if m:
                pending.append((m.group(1), m.group(3)))
                continue
            m = re.search(r"invokestatic .*// (?:Interface)?Method (com/google/common/collect/ImmutableList|java/util/List)\.of:", ins)
            if m:
                factory = m.group(1)
                continue
            m = re.search(r"putstatic .*// Field (field_\d+):", ins)
            if m:
                if m.group(1) in fields:
                    lists.append((m.group(1), factory, list(pending)))
                pending = []
        # an AI class's lists are read by its own makeBrain(<mob>, Dynamic): the brain of its first parameter's class
        for head, body in methods(javap(jp, vdir, owner)).items():
            if head == "static {};" or not any(re.search(r"getstatic .*// Field (%s)\:" % "|".join(fields), i) for i in body):
                continue
            pm = re.search(r"\((net\.minecraft\.class_\d+(?:\$class_\d+)?)[,)]", head) \
                or re.search(r"class_4095\$class_5303<(net\.minecraft\.class_\d+(?:\$class_\d+)?)>", head)
            if pm and " static " in " " + head:
                for fid in fields:
                    if any(re.search(r"getstatic .*// Field %s:" % fid, i) for i in body):
                        readers.setdefault((owner, fid), set()).add(pm.group(1).replace(".", "/"))
        if len(lists) != len(fields):
            rep.append((owner, "SKIPPED", "lists built %d, fields %d" % (len(lists), len(fields))))
            continue
        mobs = set()
        for fid in fields:
            mobs |= readers.get((owner, fid), set())
        mob_w = sorted(cls.get(m, m) for m in mobs)
        mojang = cls.get(owner, owner)
        simple = mojang.rsplit("/", 1)[1].replace("$", "_")
        name = "CbWallBrainList" + simple
        jcls = PKG + "/" + name
        missing = [e for _, _, els in lists for e in els if (e[0] + "." + e[1]) not in fld]
        if missing or not mob_w or any(m not in wnames for m in mob_w):
            rep.append((owner, "SKIPPED", "unmapped elements %s / mobs %s" % (missing, mob_w)))
            continue
        out = []
        out.append("package %s;\n" % PKG.replace("/", "."))
        out.append("import com.google.common.collect.ImmutableList;")
        out.append("import java.util.List;")
        out.append("import net.minecraft.world.entity.ai.memory.MemoryModuleType;")
        out.append("import net.minecraft.world.entity.ai.sensing.SensorType;")
        out.append("import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;")
        out.append("import top.dext.centurybridge.step.v26_1.rt.CbWallBrainExtras;\n")
        out.append("/**")
        out.append(" * Generated by core/tools/gen_wall_brain_lists.py -- do not edit; regenerate with the command in its header.")
        out.append(" * 1.21.11 %s's static brain lists (%s), built as its static initializer built them." % (
            mojang.rsplit("/", 1)[1], ", ".join(f + " " + ("MEMORY_TYPES" if fields[f][2] else "SENSOR_TYPES") for f, _, _ in lists)))
        out.append(" */")
        out.append("@SuppressWarnings({\"rawtypes\", \"unchecked\"})")
        out.append("public final class %s {\n" % name)
        for fid, factory, els in lists:
            out.append("    private static %s %s;" % ("ImmutableList" if fields[fid][1].endswith("ImmutableList") else "List", fid))
        out.append("")
        out.append("    static {")
        out.append("        build();")
        mem = [f for f, _, _ in lists if fields[f][2]]
        sen = [f for f, _, _ in lists if not fields[f][2]]
        out.append("        CbWallBrainExtras.register(%s.class, %s, %s, %s.class, () -> %s, () -> %s);" % (
            name, base_expr(lists, fields, True, fld), base_expr(lists, fields, False, fld),
            mob_w[0].replace("/", ".").replace("$", "."), mem[0] if mem else "List.of()", sen[0] if sen else "List.of()"))
        for extra in mob_w[1:]:
            out.append("        CbWallBrainExtras.alsoFor(%s.class, %s.class);" % (name, extra.replace("/", ".").replace("$", ".")))
        out.append("    }\n")
        out.append("    private %s() {\n    }\n" % name)
        out.append("    /** %s.<clinit>'s list constructions, in its order: a mod's injectors on them run here */" % mojang.rsplit("/", 1)[1])
        out.append("    @CbVanillaReads(\"CbWallBrainExtrasMixin: what build() leaves in the lists beyond 1.21.11's own is added to the "
                   "memories and sensors Brain.Provider.makeBrain passes to Brain.<init> for the mob (removals: not carried, "
                   "release_notes_known)\")")
        out.append("    private static void build() {")
        for fid, factory, els in lists:
            fac = "ImmutableList.of" if factory.endswith("ImmutableList") else "List.of"
            elems = ", ".join(("MemoryModuleType." if o == MEM else "SensorType.") + fld[o + "." + e] for o, e in els)
            out.append("        %s = %s(%s);" % (fid, fac, elems))
        out.append("    }\n")
        for fid, factory, els in lists:
            t = "ImmutableList" if fields[fid][1].endswith("ImmutableList") else "List"  # the declared type, not the factory's
            kind = "MEMORY_TYPES" if fields[fid][2] else "SENSOR_TYPES"
            out.append("    /** %s.%s (%s.%s), as a mod's writes left it */" % (mojang.rsplit("/", 1)[1], kind, owner, fid))
            out.append("    public static %s %s() {\n        return %s;\n    }\n" % (t, fid, fid))
            out.append("    /** a write of %s.%s (%s.%s): the brains 26.1 builds for the mob take what it adds */" % (
                mojang.rsplit("/", 1)[1], kind, owner, fid))
            out.append("    public static void %s(%s value) {\n        %s = value;\n    }\n" % (fid, t, fid))
        if out[-1] == "":
            out.pop()
        out.append("}")
        path = os.path.join(src, jcls + ".java")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        open(path, "w", encoding="utf-8").write("\n".join(out) + "\n")
        for fid, factory, _ in lists:
            key = "%s.%s:L%s;" % (owner, fid, fields[fid][1])  # the field's declared type (a List may be built by ImmutableList.of)
            getters[key] = jcls
            setters[key] = jcls
        moves[owner + ".<clinit>()V"] = {
            "to": jcls + ".build()V",
            "basis": "gen_wall_brain_lists: 1.21.11 %s.<clinit> builds %s from %d element reads (javap); 26.1 keeps none of "
                     "these fields and derives a brain's memories (provider list, sensors' requires(), behaviours' "
                     "getRequiredMemories()); build() repeats the same reads through the same factory in the same order"
                     % (mojang.rsplit("/", 1)[1], "/".join(f for f, _, _ in lists), sum(len(e) for _, _, e in lists))}
        rep.append((owner, name, "mobs " + " ".join(mob_w) + "; " + "; ".join(
            "%s %s %d" % (f, fields[f][3], len(e)) for f, _, e in lists)))

    index = os.path.join(src, PKG, "CbWallBrainListIndex.java")
    holders = ",\n".join('        "%s"' % v["to"].split(".build")[0].replace("/", ".")
                         for v in sorted(moves.values(), key=lambda v: v["to"]))
    with open(index, "w", encoding="utf-8") as f:
        f.write("package %s;\n\n" % PKG.replace("/", "."))
        f.write("/** Generated by core/tools/gen_wall_brain_lists.py -- do not edit. The list holders, for rt/CbWallBrainExtras. */\n")
        f.write("public final class CbWallBrainListIndex {\n    private CbWallBrainListIndex() {\n    }\n\n")
        f.write("    public static final String[] HOLDERS = {\n%s\n    };\n}\n" % holders)
    json.dump({"_swall13_brainlists": "gen_wall_brain_lists.py (V bytecode); regenerate, never hand-edit",
               "fieldGetters": dict(sorted(getters.items())),
               "staticFieldSetters": dict(sorted(setters.items())),
               "mixinAnchorMoves": dict(sorted(moves.items()))},
              open(frag, "w", encoding="utf-8"), indent=1)
    with open(report, "w", encoding="utf-8") as f:
        for r in rep:
            f.write("\t".join(r) + "\n")
    print(len(moves), "owners,", len(getters), "fields;", sum(1 for r in rep if r[1] == "SKIPPED"), "skipped ->", report)


def base_expr(lists, fields, memories, fld):
    for fid, factory, els in lists:
        if fields[fid][2] == memories:
            return "List.of(%s)" % ", ".join(("MemoryModuleType." if o == MEM else "SensorType.") + fld[o + "." + e] for o, e in els)
    return "List.of()"


if __name__ == "__main__":
    main()
