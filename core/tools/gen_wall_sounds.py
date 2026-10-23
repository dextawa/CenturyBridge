"""SoundEvents constants 26.1 dropped: the sound events 26.1 still registers under 1.21.11's ids.

26.1 moved the cat, chicken, cow, pig and wolf sounds into per-variant sound sets
(SoundEvents.CAT_SOUNDS ...): the classic set registers "entity.<animal>.<sound>" -- 1.21.11's ids -- but
no longer as SoundEvents constants. A 1.21.11 mod reading SoundEvents.CAT_AMBIENT gets the sound event
26.1 registers under "minecraft:entity.cat.ambient" (fieldGetters, read from the sound event registry).

Only get-static rows of SoundEvents typed SoundEvent whose field 26.1 lacks; the id is read from 1.21.11's
static initializer (`ldc "<id>"` ... putstatic) and must be one 26.1 registers: a literal id in 26.1's
SoundEvents, or "entity.<x>.<sound>" for an <x> of 26.1's classic variant sound sets and a <sound> of the
templates 26.1 builds those ids from. Everything else is listed in the report.

A field 26.1 kept under the wall table's name as the registered Holder.Reference<SoundEvent> answers that
holder's sound event.

  python gen_wall_sounds.py <damage.tsv> <V code jar> <W jar> <wall table> <fragment.json> <runtime class> <src root> <report.tsv>

Regenerating (from the repo root):
  python3 core/tools/gen_wall_sounds.py core/segments/damage/1.21.11__26.1.damage.tsv \
      data/jars/client-1.21.11-intermediary-code.jar data/jars/client-26.1.jar data/mappings/inter-to-mojang-1.21.11-26.1.tsv \
      core/segments/steps/1.21.11__26.1.json.d/swall-sounds-gen.json top/dext/centurybridge/step/v26_1/rt/CbWallSounds \
      core/shims/steps/26.1/src <tmp>/sounds-report.tsv
"""
import json
import os
import re
import subprocess
import sys
import zipfile

OWNER = "net/minecraft/class_3417"
SOUND = "Lnet/minecraft/class_3414;"
LDC = re.compile(r"ldc(?:_w)?\s+#\d+\s+// String (\S+)")
PUT = re.compile(r"putstatic\s+#\d+\s+// Field (field_\d+):Lnet/minecraft/class_3414;")
VARIANT_SETS = ["feline/CatSoundVariants", "chicken/ChickenSoundVariants", "cow/CowSoundVariants",
                "pig/PigSoundVariants", "wolf/WolfSoundVariants"]


def javap(jar, cls, *flags):
    return subprocess.run(["javap", *flags, "-p", "-cp", jar, cls.replace("/", ".")],
                          capture_output=True, text=True, errors="replace").stdout


def main():
    dmg, vjar, wjar, table, frag, rtcls, src, report = sys.argv[1:9]
    fmap = {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "F":
            fmap[f[1]] = f[2].rsplit(".", 1)[1]
    wdecl = javap(wjar, "net/minecraft/sounds/SoundEvents")
    wfields = set(re.findall(r" (\w+);$", wdecl, re.M))
    # fields 26.1 kept but as the registered holder (Holder.Reference<SoundEvent>): answered with its value
    holders = set(re.findall(r"net\.minecraft\.core\.Holder\$Reference<net\.minecraft\.sounds\.SoundEvent> (\w+);$", wdecl, re.M))
    # ids 26.1 registers: its literal ids, and the classic variant sets' templated ones
    with zipfile.ZipFile(wjar) as z:
        se = z.read("net/minecraft/sounds/SoundEvents.class")
    literal = set(LDC.findall(javap(wjar, "net/minecraft/sounds/SoundEvents", "-c")))
    templates = {m.decode() for m in re.findall(rb"entity\.\x01\.([a-z_]+)", se)}
    animals = set()
    for vs in VARIANT_SETS:
        text = javap(wjar, f"net/minecraft/world/entity/animal/{vs}$SoundSet", "-c")
        strings = LDC.findall(text[text.find("static {};"):])
        # ("CLASSIC", "classic", "<sound id>") per constant: the classic set's sound id
        if len(strings) >= 3 and strings[0] == "CLASSIC":
            animals.add(strings[2])

    def registered(ident):
        if ident in literal:
            return True
        m = re.fullmatch(r"entity\.([a-z_]+)\.([a-z_]+)", ident)
        return bool(m) and m.group(1) in animals and m.group(2) in templates

    wanted, held = set(), {}
    for line in open(dmg, encoding="utf-8"):
        r = line.rstrip("\n").split("\t")
        if len(r) >= 4 and r[0] == "get-static" and r[1] == OWNER and r[3] == SOUND \
                and fmap.get(f"{OWNER}.{r[2]}", r[2]) not in wfields:
            wanted.add(r[2])
        elif len(r) >= 4 and r[0] == "get-static" and r[1] == OWNER and r[3] == SOUND                 and fmap.get(f"{OWNER}.{r[2]}", r[2]) in holders:
            held[r[2]] = fmap.get(f"{OWNER}.{r[2]}", r[2])
    ids, last = {}, None
    text = javap(vjar, OWNER, "-c")
    for line in text[text.find("static {};"):].splitlines():
        m = LDC.search(line)
        if m:
            last = m.group(1)
            continue
        m = PUT.search(line)
        if m:
            if last:
                ids[m.group(1)] = last
            last = None
    getters, rep = {}, []
    for field in sorted(wanted):
        ident = ids.get(field)
        if not ident:
            rep.append((OWNER, field, "no id in 1.21.11's static initializer"))
        elif not registered(ident):
            rep.append((OWNER, field, f"26.1 registers no '{ident}'"))
        else:
            getters[field] = ident
    pkg, simple = rtcls.rsplit("/", 1)
    lines = ["package " + pkg.replace("/", ".") + ";", "",
             "import net.minecraft.core.registries.BuiltInRegistries;",
             "import net.minecraft.resources.Identifier;",
             "import net.minecraft.sounds.SoundEvent;", "",
             "/**",
             " * Generated by core/tools/gen_wall_sounds.py -- do not edit; regenerate with the command in its header.",
             " *",
             " * 1.21.11 SoundEvents constants 26.1 dropped: the sound event 26.1 registers under the same id.",
             " */",
             "public final class " + simple + " {", "",
             "    private " + simple + "() {", "    }"]
    for field in sorted(getters):
        lines += ["", f"    /** SoundEvents.{field}: minecraft:{getters[field]} */",
                  f"    public static SoundEvent {field}() {{",
                  f"        return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.withDefaultNamespace(\"{getters[field]}\"));",
                  "    }"]
    for field in sorted(held):
        lines += ["", f"    /** SoundEvents.{field}: 26.1's SoundEvents.{held[field]} holder's sound event */",
                  f"    public static SoundEvent {field}() {{",
                  f"        return net.minecraft.sounds.SoundEvents.{held[field]}.value();",
                  "    }"]
    getters.update({f: None for f in held})
    lines += ["}", ""]
    with open(os.path.join(src, rtcls + ".java"), "w", encoding="utf-8", newline="\n") as fh:
        fh.write("\n".join(lines))
    with open(frag, "w", encoding="utf-8", newline="\n") as fh:
        json.dump({"fieldGetters": {f"{OWNER}.{f}:{SOUND}": rtcls for f in sorted(getters)}}, fh, indent=1)
        fh.write("\n")
    with open(report, "w", encoding="utf-8", newline="\n") as fh:
        for row in rep:
            fh.write("\t".join(row) + "\n")
    print(f"{len(getters)} sounds -> {frag}; {len(rep)} left (report {report})")


if __name__ == "__main__":
    main()
