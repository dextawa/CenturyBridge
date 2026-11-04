"""The <Mob>Ai.init<X>Activity(.., brain) statics at the 1.21.11 -> 26.1 wall, given back for mods' calls
(an access widener opens them; staticRedirects onto rt/CbWallAiActivities).

1.21.11's init<X>Activity([mob,] brain) was void and added one activity to the brain (Brain.addActivity /
addActivityWithConditions / addActivityAndRemoveMemoryWhenStopped). 26.1 keeps the same-named private static but
returns the activity as an ActivityData record ([mob] or no argument) and the mob's BRAIN_PROVIDER adds it. Given
back: 26.1's init<X>Activity is called and its ActivityData added to the brain with Brain.addActivity(activity,
behaviors, conditions, memoriesToErase) -- once per call, as 1.21.11 added it once per call. A mob argument 1.21.11
took and 26.1's method no longer takes is dropped (the activity 26.1 builds is the native one). Only rows of the
damage-aw table whose 26.1 method (by the wall table, never by simple name) returns ActivityData and whose 26.1
arguments are 1.21.11's minus the trailing brain (or none) are carried; everything else is reported.

  python gen_wall_ai_activities.py <damage-aw.tsv> <W jar> <wall table> <fragment.json> <rt java> <aw lines out>

Regenerating (from the repo root):
  python3 core/tools/gen_wall_ai_activities.py core/segments/damage-aw/1.21.11__26.1.damage.tsv \\
      data/jars/client-26.1.jar data/mappings/inter-to-mojang-1.21.11-26.1.tsv \\
      core/segments/steps/1.21.11__26.1.json.d/swall-aiactivities-gen.json \\
      core/shims/steps/26.1/src/top/dext/centurybridge/step/v26_1/rt/CbWallAiActivities.java <tmp>/aiactivities-aw.txt
(the access-widener lines it prints are kept in the layer's cb-step-26.1.accesswidener by hand)
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

BRAIN = "net/minecraft/class_4095"
W_BRAIN = "net/minecraft/world/entity/ai/Brain"
W_DATA = "Lnet/minecraft/world/entity/ai/ActivityData;"
RT = "top/dext/centurybridge/step/v26_1/rt/CbWallAiActivities"


def jtype(desc_type):
    return desc_type[1:-1].replace("/", ".").replace("$", ".")


def main():
    dmg, wjar, table, frag, rtjava, awout = sys.argv[1:7]
    cmap, mmap = {}, {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3:
            if f[0] == "C":
                cmap[f[1]] = f[2]
            elif f[0] == "M":
                mmap[f[1]] = f[2]
    wz = zipfile.ZipFile(wjar)
    wcache = {}

    def wcls(name):
        if name not in wcache:
            e = name + ".class"
            wcache[name] = classfile.read(wz.read(e), code=False) if e in wz.namelist() else None
        return wcache[name]

    def wdesc(vdesc):
        out, i = [], 0
        while i < len(vdesc):
            c = vdesc[i]
            if c == "L":
                j = vdesc.index(";", i)
                out.append("L" + cmap.get(vdesc[i + 1:j], vdesc[i + 1:j]) + ";")
                i = j + 1
            else:
                out.append(c)
                i += 1
        return "".join(out)

    rows = set()
    for line in open(dmg, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 4 and f[0] == "aw-call-static" and f[3].endswith("L" + BRAIN + ";)V"):
            rows.add((f[1], f[2], f[3]))
    redirects, methods, aw, report = {}, [], set(), []
    for owner, name, desc in sorted(rows):
        moj = cmap.get(owner)
        wm = mmap.get(owner + "." + name)
        wc = wcls(moj) if moj else None
        if wc is None or wm is None:
            report.append("%s.%s%s: no 26.1 class or member in the wall table" % (owner, name, desc))
            continue
        wname = wm.rsplit(".", 1)[1]
        vargs = classfile.split_args(desc)[0]
        wargs_v = [wdesc(a) for a in vargs[:-1]]
        cands = [m for m in wc.methods if m.name == wname and m.desc.endswith(")" + W_DATA)
                 and m.access & classfile.ACC_STATIC]
        pick = None
        for m in cands:
            margs = classfile.split_args(m.desc)[0]
            if margs == wargs_v or margs == []:
                pick = m
        if pick is None:
            report.append("%s.%s%s: 26.1 %s.%s has no ActivityData shape matching %s" % (owner, name, desc, moj, wname, wargs_v))
            continue
        margs = classfile.split_args(pick.desc)[0]
        if not pick.access & classfile.ACC_PUBLIC:
            aw.add("accessible\tmethod\t%s\t%s\t%s" % (moj, wname, pick.desc))
        simple = moj.rsplit("/", 1)[1]
        jname = "%s_%s" % (simple[0].lower() + simple[1:], wname)
        params = ["%s a%d" % (jtype(t), k) for k, t in enumerate(wargs_v)] + ["Brain brain"]
        call = "%s.%s(%s)" % (moj.replace("/", ".").replace("$", "."), wname,
                              ", ".join("a%d" % k for k in range(len(margs))))
        dropped = "" if margs or not wargs_v else " (1.21.11's mob argument dropped: 26.1 builds the activity without it)"
        methods.append(
            "    /** 1.21.11 %s.%s (%s.%s%s)%s */\n"
            "    public static void %s(%s) {\n"
            "        add(brain, %s);\n"
            "    }\n" % (simple, wname, owner.rsplit("/", 1)[1], name, desc, dropped, jname, ", ".join(params), call))
        redirects[owner + "." + name + desc] = RT + "#" + jname
    body = ("package top.dext.centurybridge.step.v26_1.rt;\n\n"
            "import net.minecraft.world.entity.ai.ActivityData;\n"
            "import net.minecraft.world.entity.ai.Brain;\n"
            "\n/**\n * 1.21.11's <Mob>Ai.init<X>Activity(.., brain) statics, which 26.1 turned into ActivityData factories: see\n"
            " * core/tools/gen_wall_ai_activities.py, which generates this file -- do not edit.\n */\n"
            "@SuppressWarnings({\"rawtypes\", \"unchecked\"})\npublic final class CbWallAiActivities {\n\n"
            "    private CbWallAiActivities() {\n    }\n\n"
            "    /** 1.21.11 added the activity once per call; so does this */\n"
            "    private static void add(Brain brain, ActivityData a) {\n"
            "        brain.addActivity(a.activityType(), a.behaviorPriorityPairs(), a.conditions(), a.memoriesToEraseWhenStopped());\n"
            "    }\n\n"
            + "\n".join(methods) + "}\n")
    with open(rtjava, "w", encoding="utf-8", newline="\n") as fh:
        fh.write(body)
    with open(frag, "w", encoding="utf-8", newline="\n") as fh:
        json.dump({"staticRedirects": dict(sorted(redirects.items()))}, fh, indent=1)
        fh.write("\n")
    with open(awout, "w", encoding="utf-8", newline="\n") as fh:
        fh.write("\n".join(sorted(aw)) + "\n")
    for r in report:
        print("not given back: " + r)
    print("%d redirects -> %s; access-widener lines -> %s" % (len(redirects), frag, awout))


if __name__ == "__main__":
    main()
