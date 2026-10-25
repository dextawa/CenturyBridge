"""The <Mob>Ai brain statics at the 1.21.11 -> 26.1 wall: makeBrain(..brain) and brainProvider(), given back for
mods' calls (staticRedirects onto rt/CbWallAiBrains).

1.21.11's XAi.makeBrain(brain) (some take the mob first) filled an empty brain: one initXActivity(brain) per activity,
then setCoreActivities(of(CORE)), setDefaultActivity(D), useDefaultActivity(), and returned it; XAi.brainProvider()
was Brain.provider(MEMORY_TYPES, SENSOR_TYPES), and the mob built its brain as makeBrain(brainProvider().makeBrain(..)).
26.1 moved the activities into the provider: XAi.getActivities([mob]) lists them, the mob's static BRAIN_PROVIDER is
Brain.provider(sensors, mob -> getActivities(..)) and its brain arrives with them, core CORE and default IDLE (a mob
that defaults elsewhere sets it in its makeBrain(Packed)). Given back:
  - makeBrain: every activity of getActivities([mob]) the brain does not have yet is added (a brain from 26.1's
    provider has them all; one from a provider with no activities gets them), then 1.21.11's core and default
    activities, useDefaultActivity, the brain returned. Facts read from 1.21.11's body (the core and default activity
    fields, the set factory) and checked against 26.1: the default must be the one 26.1's mob gives its brain;
  - brainProvider(): the mob's BRAIN_PROVIDER (its activities included, which makeBrain then finds present);
  - WardenAi.makeBrain(warden, dynamic), which made the brain itself: Warden's BRAIN_PROVIDER fed the Dynamic as a
    Brain.Packed (rt/CbWallBrain.toPacked), then as above.
These are static methods 26.1's classes no longer have; Mixin cannot add a non-private static to a game class, so a
mod's injector into them stays open (a case for the shared mixin list), and only calls are carried.

  python gen_wall_ai_brains.py <damage.tsv> <V code jar> <W jar> <wall table> <fragment.json> <rt java> <aw lines out>

Regenerating (from the repo root):
  python3 core/tools/gen_wall_ai_brains.py core/segments/damage/1.21.11__26.1.damage.tsv \\
      data/jars/client-1.21.11-intermediary-code.jar data/jars/client-26.1.jar data/mappings/inter-to-mojang-1.21.11-26.1.tsv \\
      core/segments/steps/1.21.11__26.1.json.d/swall-aibrains-gen.json \\
      core/shims/steps/26.1/src/top/dext/centurybridge/step/v26_1/rt/CbWallAiBrains.java <tmp>/aibrains-aw.txt
(the access-widener lines it prints are kept in the layer's cb-step-26.1.accesswidener by hand)
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import bytecode_uses  # noqa: E402
import classfile  # noqa: E402

BRAIN = "net/minecraft/class_4095"
PROVIDER = "net/minecraft/class_4095$class_5303"
ACTIVITY = "net/minecraft/class_4168"
DYNAMIC = "com/mojang/serialization/Dynamic"
RT = "top/dext/centurybridge/step/v26_1/rt/CbWallAiBrains"


def main():
    dmg, vjar, wjar, table, frag, rtjava, awout = sys.argv[1:8]
    cmap, mmap, fmap = {}, {}, {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3:
            (cmap if f[0] == "C" else mmap if f[0] == "M" else fmap if f[0] == "F" else {})[f[1]] = f[2]
    rows = set()
    for line in open(dmg, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 4 and cmap.get(f[1], "").endswith("Ai"):
            rows.add((f[1], f[2], f[3]))
    vz, wz = zipfile.ZipFile(vjar), zipfile.ZipFile(wjar)

    def wcls(name):
        e = name + ".class"
        return classfile.read(wz.read(e), code=True) if e in wz.namelist() else None

    redirects, methods, aw, report = {}, [], set(), []
    for owner, name, desc in sorted(rows):
        moj = cmap[owner]
        simple = moj.rsplit("/", 1)[1]
        mob = moj[:-2]  # FrogAi -> Frog
        wmob = wcls(mob)
        wai = wcls(moj)
        if wmob is None or wai is None:
            report.append("%s.%s%s: 26.1 has no %s" % (moj, name, desc, mob))
            continue
        if not any(f[0] == "BRAIN_PROVIDER" for f in wmob.fields):
            report.append("%s.%s%s: 26.1's %s has no BRAIN_PROVIDER" % (moj, name, desc, mob))
            continue
        mobsimple = mob.rsplit("/", 1)[1]
        aw.add("accessible\tfield\t%s\tBRAIN_PROVIDER\tLnet/minecraft/world/entity/ai/Brain$Provider;" % mob)
        jname = "%s_%s" % (simple[0].lower() + simple[1:], mmap.get(owner + "." + name, name).rsplit(".", 1)[-1])
        if desc == "()L" + PROVIDER + ";":
            methods.append(
                "    /** 1.21.11 %s.brainProvider() (%s.%s): %s's BRAIN_PROVIDER */\n"
                "    public static Brain.Provider<%s> %s() {\n"
                "        return %s.BRAIN_PROVIDER;\n"
                "    }\n" % (simple, owner.rsplit("/", 1)[1], name, mobsimple, mobsimple, jname, mobsimple))
            redirects[owner + "." + name + desc] = RT + "#" + jname
            continue
        if not desc.endswith(")L" + BRAIN + ";"):
            continue
        vc = classfile.read(vz.read(owner + ".class"), code=True)
        vm = [m for m in vc.methods if m.name == name and m.desc == desc]
        if not vm:
            report.append("%s.%s%s: not in 1.21.11's code jar" % (moj, name, desc))
            continue
        calls = []
        bytecode_uses.run(vc, vm[0], on_call=lambda o, n, d, a: calls.append((o, n, d, [x.prod for x in a])))
        setf = [c for c in calls if c[1] == "of" and c[0] in ("java/util/Set", "com/google/common/collect/ImmutableSet")]
        core = [c for c in calls if c[0] == BRAIN and c[1] == "method_18890"]
        dflt = [c for c in calls if c[0] == BRAIN and c[1] == "method_18897"]
        use = [c for c in calls if c[0] == BRAIN and c[1] == "method_24536"]
        if len(setf) != 1 or len(core) != 1 or len(dflt) != 1 or len(use) != 1 or not str(setf[0][3][0]).startswith("get:") \
                or not str(dflt[0][3][0]).startswith("get:"):
            report.append("%s.%s%s: 1.21.11's body is not the init/core/default/use shape" % (moj, name, desc))
            continue
        core_act = fmap[ACTIVITY + "." + setf[0][3][0][4:]].rsplit(".", 1)[1]
        def_act = fmap[ACTIVITY + "." + dflt[0][3][0][4:]].rsplit(".", 1)[1]
        # 26.1's default for this mob: IDLE (Brain's constructor) unless its makeBrain(Packed) sets one
        wdef = "IDLE"
        for m in wmob.methods:
            if m.name == "makeBrain" and m.desc.startswith("(Lnet/minecraft/world/entity/ai/Brain$Packed;)"):
                wc = []
                bytecode_uses.run(wmob, m, on_call=lambda o, n, d, a: wc.append((o, n, [x.prod for x in a])))
                for o, n, a in wc:
                    if n == "setDefaultActivity":
                        wdef = str(a[0])[4:]
        if core_act != "CORE" or def_act != wdef:
            report.append("%s.%s%s: 1.21.11 core %s default %s, 26.1 core CORE default %s" % (moj, name, desc, core_act, def_act, wdef))
            continue
        ga = [m for m in wai.methods if m.name == "getActivities" and m.desc.endswith(")Ljava/util/List;")]
        if len(ga) != 1:
            report.append("%s.%s%s: 26.1's %s has %d getActivities" % (moj, name, desc, simple, len(ga)))
            continue
        if not ga[0].access & classfile.ACC_PUBLIC:
            aw.add("accessible\tmethod\t%s\tgetActivities\t%s" % (moj, ga[0].desc))
        gaargs = ga[0].desc[1:ga[0].desc.index(")")]
        vargs = desc[1:desc.index(")")]
        setcls = "ImmutableSet" if setf[0][0].startswith("com/google") else "Set"
        tail = ("        brain.setCoreActivities(%s.of(Activity.%s));\n"
                "        brain.setDefaultActivity(Activity.%s);\n"
                "        brain.useDefaultActivity();\n"
                "        return brain;\n" % (setcls, core_act, def_act))
        if vargs == "L" + BRAIN + ";":
            sig = "Brain<%s> brain" % mobsimple
            get = "%s.getActivities(%s)" % (simple, "" if gaargs == "" else "null")
            if gaargs != "":
                report.append("%s.%s%s: getActivities takes the mob, makeBrain has none" % (moj, name, desc))
                continue
            head = ""
        elif vargs == "L%s;L%s;" % (vc_mob_inter(cmap, mob), BRAIN):
            sig = "%s mob, Brain<%s> brain" % (mobsimple, mobsimple)
            get = "%s.getActivities(%s)" % (simple, "mob" if gaargs else "")
            head = ""
        elif vargs == "L%s;L%s;" % (vc_mob_inter(cmap, mob), DYNAMIC):
            sig = "%s mob, Dynamic<?> dynamic" % mobsimple
            get = "%s.getActivities(%s)" % (simple, "mob" if gaargs else "")
            head = ("        Brain<%s> brain = %s.BRAIN_PROVIDER.makeBrain(mob, CbWallBrain.toPacked(dynamic));\n" % (mobsimple, mobsimple))
        else:
            report.append("%s.%s%s: argument shape not handled" % (moj, name, desc))
            continue
        methods.append(
            "    /** 1.21.11 %s.makeBrain (%s.%s%s) */\n"
            "    public static Brain<?> %s(%s) {\n%s"
            "        addMissing(brain, %s);\n%s"
            "    }\n" % (simple, owner.rsplit("/", 1)[1], name, desc, jname, sig, head, get, tail))
        redirects[owner + "." + name + desc] = RT + "#" + jname
    imports = set()
    for k in redirects:
        moj = cmap[k.split(".")[0]]
        imports.add(moj.replace("/", "."))
        imports.add(moj[:-2].replace("/", "."))
    body = ("package top.dext.centurybridge.step.v26_1.rt;\n\n"
            + "".join("import %s;\n" % i for i in sorted(imports | {
                "com.google.common.collect.ImmutableSet", "com.mojang.serialization.Dynamic", "java.util.List", "java.util.Map",
                "java.util.Set", "net.minecraft.world.entity.LivingEntity", "net.minecraft.world.entity.ai.ActivityData",
                "net.minecraft.world.entity.ai.Brain", "net.minecraft.world.entity.schedule.Activity"}))
            + "\n/**\n * 1.21.11's <Mob>Ai.makeBrain(..) and brainProvider() statics, which 26.1 replaced by getActivities and each mob's\n"
              " * BRAIN_PROVIDER: see core/tools/gen_wall_ai_brains.py, which generates this file -- do not edit.\n */\n"
              "@SuppressWarnings({\"rawtypes\", \"unchecked\"})\npublic final class CbWallAiBrains {\n\n"
              "    private CbWallAiBrains() {\n    }\n\n"
              "    /** each activity the brain does not have yet (Brain.activityRequirements is keyed by activity) */\n"
              "    private static <E extends LivingEntity> void addMissing(Brain<E> brain, List<ActivityData<E>> activities) {\n"
              "        Map<Activity, ?> have = brain.activityRequirements;\n"
              "        for (ActivityData<E> a : activities) {\n"
              "            if (!have.containsKey(a.activityType())) {\n"
              "                brain.addActivity(a.activityType(), a.behaviorPriorityPairs(), a.conditions(), a.memoriesToEraseWhenStopped());\n"
              "            }\n        }\n    }\n\n"
            + "\n".join(methods) + "}\n")
    with open(rtjava, "w", encoding="utf-8", newline="\n") as fh:
        fh.write(body)
    with open(frag, "w", encoding="utf-8", newline="\n") as fh:
        json.dump({"staticRedirects": dict(sorted(redirects.items()))}, fh, indent=1)
        fh.write("\n")
    aw.add("accessible\tfield\tnet/minecraft/world/entity/ai/Brain\tactivityRequirements\tLjava/util/Map;")
    with open(awout, "w", encoding="utf-8", newline="\n") as fh:
        fh.write("\n".join(sorted(aw)) + "\n")
    for r in report:
        print("not given back: " + r)
    print("%d redirects -> %s; access-widener lines -> %s" % (len(redirects), frag, awout))


def vc_mob_inter(cmap, mob):
    for k, v in cmap.items():
        if v == mob:
            return k
    return mob


if __name__ == "__main__":
    main()
