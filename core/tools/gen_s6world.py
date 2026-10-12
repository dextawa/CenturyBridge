"""gen_s6world.py: the s6world wiring fragment of climb step 1.20.6 -> 1.21 (owners under net/minecraft/world/
outside world/item, world/effect and world/food; server/ outside server/network; commands, gametest, util, nbt,
resources, sounds, SharedConstants, CrashReport).

  python gen_s6world.py <V stub: client-1.20.6-intermediary.jar> <W stub: client-1.21-intermediary.jar>

Writes core/segments/steps/1.20.6__1.21.json.d/s6world.json and the generated getter classes of the layer
(rt/CbWorldSounds.java, rt/CbWorldLinks.java). Each entry carries its basis, read off the 1.20.6 and 1.21 bytecode.
Where one decision applies to many keys (every SoundEvents field 1.21 turned into a Holder, every CommonLinks string
that became a URI, every vanilla owner a call can name), the keys are read off the stubs instead of listed by hand.
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

STEP = "top/dext/centurybridge/step/v1_21"
RT = STEP + "/rt"
FAC = STEP + "/facade"
MC = "net/minecraft/"
G = RT + "/Guard"
HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
FRAGMENT = os.path.join(ROOT, "core/segments/steps/1.20.6__1.21.json.d/s6world.json")
RT_SRC = os.path.join(ROOT, "core/shims/steps/1.21/src", RT)


def L(c):
    return "L" + (c if "/" in c else MC + c) + ";"


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith("net/minecraft/") and n.endswith(".class"):
                c = classfile.read(z.read(n))
                if c:
                    out[c.name] = c
    return out


def with_subclasses(classes, root):
    """root and every class of the stub that has it among its superclasses"""
    out = [root]
    for name in sorted(classes):
        x = classes[name].super
        while x:
            if x == root:
                out.append(name)
                break
            x = classes[x].super if x in classes else None
    return out


def field_types(classes, owner):
    c = classes.get(owner)
    return {f[0]: f[1] for f in c.fields} if c else {}


class Fragment:
    def __init__(self):
        self.w = {
            "methodRenames": {}, "callReshapes": {}, "superCtorReshapes": {}, "ctorRedirects": {},
            "staticRedirects": {}, "instanceRedirects": {}, "fieldRedirects": {}, "fieldGetters": {},
            "instanceFieldGetters": {}, "instanceFieldSetters": {}, "superRedirects": {}, "covers": [],
            "overrideHooks": [], "overrideRenames": [], "frameDefaults": {}, "convertHooks": [], "adapterHooks": [],
            "abstractFills": [], "classRenames": {}, "superRenames": {}, "genericWitnesses": {}, "ledger": {},
        }

    def reshape(self, key, desc, ops, basis, ret=None):
        e = {"desc": desc, "ops": ops, "basis": basis}
        if ret:
            e["ret"] = ret
        assert key not in self.w["callReshapes"], key
        self.w["callReshapes"][key] = e

    def ctor(self, key, desc, ops, basis, post=None, post_args=None):
        e = {"desc": desc, "ops": ops, "basis": basis}
        if post:
            e["post"], e["postArgs"] = post, post_args
        assert key not in self.w["superCtorReshapes"], key
        self.w["superCtorReshapes"][key] = e

    def hook(self, old, new, under, basis, **extra):
        e = {"old": old, "new": new, "onlyUnder": MC + under if "/" not in under else under, "basis": basis}
        e.update(extra)
        self.w["overrideHooks"].append(e)

    def put(self, section, key, value):
        assert key not in self.w[section], (section, key)
        self.w[section][key] = value

    def write(self):
        with open(FRAGMENT, "w", encoding="utf-8", newline="\n") as o:
            json.dump({k: v for k, v in self.w.items() if v}, o, indent=1)
            o.write("\n")
        print(FRAGMENT, {k: len(v) for k, v in self.w.items() if v})


def write_java(name, body):
    path = os.path.join(RT_SRC, name + ".java")
    with open(path, "w", encoding="utf-8", newline="\n") as o:
        o.write(body)
    print(path)


# ------------------------------------------------------------------------------------------------ attributes
def attributes(f):
    A = RT + "/CbEntityAttributes"
    UUID, RL, AM, AI = "Ljava/util/UUID;", L("class_2960"), L("class_1322"), L("class_1324")
    OP, HOLDER = L("class_1322$class_1323"), L("class_6880")
    id_of = "conv:%d " + A + "#idOf(" + UUID + ")" + RL
    # AttributeModifier: a record (UUID id, String name, double, Operation) in 1.20.6, (ResourceLocation id, double,
    # Operation) in 1.21; final in both, so nothing overrides its members
    f.put("ctorRedirects", MC + "class_1322.<init>(Ljava/lang/String;D" + OP + ")V", A + "#newModifier")
    f.put("ctorRedirects", MC + "class_1322.<init>(" + UUID + "Ljava/lang/String;D" + OP + ")V", A + "#newModifier")
    f.reshape(MC + "class_1322.comp_2447()" + UUID, "()" + RL, [],
              "id() answers the ResourceLocation 1.21 names the modifier by; its 1.20.6 uuid is the inverse of the "
              "data fixer's mapping (CbEntityAttributes.uuidOf)",
              ret="conv " + A + "#uuidOf(" + RL + ")" + UUID)
    f.put("instanceRedirects", MC + "class_1322.comp_2448()Ljava/lang/String;", A + "#name")
    f.put("fieldRedirects", MC + "class_1320.field_30097:I", A + "#MAX_NAME_LENGTH")
    # AttributeInstance: the by-id lookups take the 1.21 id of the 1.20.6 uuid
    f.reshape(MC + "class_1324.method_6199(" + UUID + ")" + AM, "(" + RL + ")" + AM, [id_of % 0],
              "getModifier by the id the data fixer gives the uuid; a mod's super call included")
    f.hook("method_6199(" + UUID + ")" + AM, "method_6199(" + RL + ")" + AM, "class_1324",
           "1.21 asks getModifier(id): a 1.20.6 subclass's getModifier(UUID) answers for the id's uuid",
           wrap=A + ".uuidOf(" + RL + ")" + UUID)
    f.reshape(MC + "class_1324.method_6196(" + AM + ")Z", "(" + RL + ")Z",
              ["conv:0 " + A + "#idOfModifier(" + AM + ")" + RL],
              "1.20.6's hasModifier(modifier) looked the modifier's id up; 1.21's takes the id")
    f.hook("method_6196(" + AM + ")Z", "method_6196(" + RL + ")Z", "class_1324",
           "1.21 asks hasModifier(id): a 1.20.6 subclass's hasModifier(AttributeModifier) is handed the instance's own "
           "modifier of that id, else one carrying the id (CbEntityAttributes.hasModifier)",
           bridge=A + ".hasModifier(L" + A + "$LegacyHasModifier;" + RL + ")Z", adapter=A + "$LegacyHasModifier")
    f.reshape(MC + "class_1324.method_6200(" + UUID + ")V", "(" + RL + ")Z", [id_of % 0],
              "removeModifier by the id of the uuid; 1.21 answers whether one was removed, 1.20.6 answered nothing",
              ret="pop")
    f.hook("method_6200(" + UUID + ")V", "method_6200(" + RL + ")Z", "class_1324",
           "1.21 asks removeModifier(id) and reads whether one went: the 1.20.6 subclass's removeModifier(UUID) runs "
           "and the answer is whether the id was there before and is gone after (CbEntityAttributes.removeModifier)",
           bridge=A + ".removeModifier(L" + A + "$LegacyRemoveModifier;" + RL + ")Z", adapter=A + "$LegacyRemoveModifier")
    f.w["covers"].append(MC + "class_1324.method_27304(" + UUID + ")Z")
    # AttributeMap
    for owner in (MC + "class_5131",):
        f.put("methodRenames", owner + ".method_26841()Ljava/util/Set;", "method_60497")
    f.w["overrideRenames"].append({
        "old": "method_26841()Ljava/util/Set;", "new": "method_60497", "onlyUnder": MC + "class_5131",
        "superCalls": "rename",
        "basis": "getDirtyAttributes is 1.21's getAttributesToSync: the same set (the client-syncable instances that "
                 "changed), filled and cleared where 1.20.6 filled and cleared it"})
    for name, ret, what in (("method_45332", "D", "getModifierValue"), ("method_45330", "Z", "hasModifier")):
        f.reshape(MC + "class_5131." + name + "(" + HOLDER + UUID + ")" + ret, "(" + HOLDER + RL + ")" + ret,
                  ["keep:0", id_of % 1], "AttributeMap." + what + " by the id of the uuid")
        f.hook(name + "(" + HOLDER + UUID + ")" + ret, name + "(" + HOLDER + RL + ")" + ret, "class_5131",
               "1.21 asks AttributeMap." + what + "(holder, id): a 1.20.6 subclass's override answers for the id's uuid",
               wrap=A + ".uuidOf(" + RL + ")" + UUID)
    for name, ret, what in (("method_27308", "D", "getModifierValue"), ("method_27309", "Z", "hasModifier")):
        f.reshape(MC + "class_5132." + name + "(" + HOLDER + UUID + ")" + ret, "(" + HOLDER + RL + ")" + ret,
                  ["keep:0", id_of % 1], "AttributeSupplier." + what + " by the id of the uuid")


# ------------------------------------------------------------------------------------------------ sounds, links
def sounds_and_links(f, v, w):
    snd = "Lnet/minecraft/class_3414;"
    lines, n = [], 0
    vt, wt = field_types(v, MC + "class_3417"), field_types(w, MC + "class_3417")
    for name in sorted(vt):
        if vt[name] == snd and wt.get(name) in (L("class_6880"), L("class_6880$class_6883")):
            f.put("fieldGetters", MC + "class_3417." + name + ":" + snd, RT + "/CbWorldSounds")
            lines.append("    public static class_3414 %s() {\n        return class_3417.%s.comp_349();\n    }\n" % (name, name))
            n += 1
    write_java("CbWorldSounds", (
        "package top.dext.centurybridge.step.v1_21.rt;\n\n"
        "import net.minecraft.class_3414;\n"
        "import net.minecraft.class_3417;\n\n"
        "/**\n"
        " * SoundEvents fields 1.21 turned into Holders (generated by core/tools/gen_s6world.py, do not edit): a\n"
        " * 1.20.6 read of the SoundEvent field reads the value of 1.21's holder -- the same registered SoundEvent,\n"
        " * the registration having bound the holder when SoundEvents initialised.\n"
        " */\n"
        "public final class CbWorldSounds {\n\n" + "\n".join(lines) + "\n    private CbWorldSounds() {\n    }\n}\n"))
    lines = []
    vt, wt = field_types(v, MC + "class_8216"), field_types(w, MC + "class_8216")
    for name in sorted(vt):
        if vt[name] == "Ljava/lang/String;" and wt.get(name) == "Ljava/net/URI;":
            f.put("fieldGetters", MC + "class_8216." + name + ":Ljava/lang/String;", RT + "/CbWorldLinks")
            lines.append("    public static String %s() {\n        return class_8216.%s.toString();\n    }\n" % (name, name))
    write_java("CbWorldLinks", (
        "package top.dext.centurybridge.step.v1_21.rt;\n\n"
        "import net.minecraft.class_8216;\n\n"
        "/**\n"
        " * CommonLinks strings 1.21 turned into URIs (generated by core/tools/gen_s6world.py, do not edit): 1.21\n"
        " * builds each with URI.create from the very string 1.20.6 held (checked field by field), and toString()\n"
        " * gives that string back.\n"
        " */\n"
        "public final class CbWorldLinks {\n\n" + "\n".join(lines) + "\n    private CbWorldLinks() {\n    }\n}\n"))


def main():
    v, w = load(sys.argv[1]), load(sys.argv[2])
    f = Fragment()
    attributes(f)
    sounds_and_links(f, v, w)
    f.write()


if __name__ == "__main__":
    main()
