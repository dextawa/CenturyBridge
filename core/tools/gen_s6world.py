"""gen_s6world.py: the s6world wiring fragment of climb step 1.20.6 -> 1.21 (owners under net/minecraft/world/
outside world/item, world/effect and world/food; server/ outside server/network; commands, gametest, util, nbt,
resources, sounds, SharedConstants, CrashReport).

  python gen_s6world.py <V stub: client-1.20.6-intermediary.jar> <W stub: client-1.21-intermediary.jar>
                        <V code: client-1.20.6-intermediary-code.jar>

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


def load(jar, code=False):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith("net/minecraft/") and n.endswith(".class"):
                c = classfile.read(z.read(n), code)
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
            "staticFieldSetters": {}, "instanceFieldGetters": {}, "instanceFieldSetters": {}, "superRedirects": {}, "covers": [],
            "overrideHooks": [], "overrideRenames": [], "frameDefaults": {}, "convertHooks": [], "adapterHooks": [],
            "abstractFills": [], "classRenames": {}, "fieldRenames": {}, "superRenames": {}, "genericWitnesses": {}, "ledger": {},
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


# ------------------------------------------------------------------------------------------------ server, reports
def server(f, v):
    S = RT + "/CbWorldServer"
    MS, DS, FILE, PATH = "net/minecraft/server/MinecraftServer", MC + "class_3176", "Ljava/io/File;", "Ljava/nio/file/Path;"
    to_file, to_path = "conv " + S + "#file(" + PATH + ")" + FILE, S + ".path(" + FILE + ")" + PATH
    # getServerDirectory / getFile: File in 1.20.6, Path in 1.21 -- the call converts the answer back, an override's
    # File answers 1.21 as a Path; super calls take the reshape (no carrying hook), so they reach 1.21's body
    f.reshape(MS + ".method_3831()" + FILE, "()" + PATH, [],
              "1.21 answers the server directory as a Path: Path.of(\"\") is 1.20.6's new File(\".\") (CbWorldServer.file)",
              ret=to_file)
    f.hook("method_3831()" + FILE, "method_3831()" + PATH, MS,
           "1.21 asks getServerDirectory() for a Path: a 1.20.6 override's File, as a path", unwrap=to_path)
    f.reshape(MS + ".method_3758(Ljava/lang/String;)" + FILE, "(Ljava/lang/String;)" + PATH, ["keep:0"],
              "1.21 answers getFile(name) as a Path: dir.resolve(name) is 1.20.6's new File(dir, name) (CbWorldServer.file)",
              ret=to_file)
    f.hook("method_3758(Ljava/lang/String;)" + FILE, "method_3758(Ljava/lang/String;)" + PATH, MS,
           "1.21 asks getFile(name) for a Path: a 1.20.6 override's File, as a path", unwrap=to_path)
    # isNetherEnabled() -> isLevelEnabled(Level)
    f.put("instanceRedirects", MS + ".method_3839()Z", S + "#isNetherEnabled")
    f.put("superRedirects", MS + ".method_3839()Z", S + "#superIsNetherEnabled")
    f.put("superRedirects", DS + ".method_3839()Z", S + "#superIsNetherEnabled")
    f.hook("method_3839()Z", "method_60671(" + L("class_1937") + ")Z", MS,
           "1.21 asks isLevelEnabled(level) where 1.20.6 asked isNetherEnabled() for the nether portal: the nether is the "
           "1.20.6 override's answer, any other level the vanilla answer above it (CbWorldServer.isLevelEnabled)",
           bridge=S + ".isLevelEnabled(L" + S + "$LegacyNether;" + L("class_1937") + ")Z", adapter=S + "$LegacyNether", guard=G)
    # reportChunkLoadFailure / reportChunkSaveFailure(pos): 1.20.6's bodies were empty; 1.21's take the failure
    CP, RSI = L("class_1923"), L("class_9240")
    for name in ("method_57821", "method_57822"):
        f.reshape(MS + "." + name + "(" + CP + ")V", "(Ljava/lang/Throwable;" + RSI + CP + ")V",
                  ["push " + S + "#noFailure()Ljava/lang/Throwable;", "push " + S + "#noStorage()" + RSI, "keep:0"],
                  "a 1.20.6 caller reports no failure: the sentinel makes 1.21's body 1.20.6's empty one "
                  "(CbWorldServerMixin), a 1.20.6 subclass's override still runs through its hook")
        f.put("frameDefaults", name + "(" + CP + ")V", {
            "0": S + "#noFailure()Ljava/lang/Throwable;",
            "1": "zero: the storage of a sentinel report is never read, CbWorldServerMixin returns first"})
    # PlayerList.respawn(player, keepAll): 1.21 takes the removal reason 1.20.6 always used
    SP, RR = L("class_3222"), L("class_1297$class_5529")
    f.reshape(MC + "class_3324.method_14556(" + SP + "Z)" + SP, "(" + SP + "Z" + RR + ")" + SP,
              ["keep:0", "keep:1", "push " + S + "#respawnRemovalReason()" + RR],
              "1.20.6's respawn removed the old player with RemovalReason.DISCARDED; 1.21 takes the reason")
    f.put("frameDefaults", "method_14556(" + SP + "Z)" + SP, {"2": S + "#respawnRemovalReason()" + RR})
    # CrashReport: one kind of report in 1.20.6, the crash report (1.21's ReportType.CRASH)
    CR, RT_, LIST = MC + "class_128", L("class_9813"), "Ljava/util/List;"
    crash, lines = "push " + S + "#crashReportType()" + RT_, "push " + S + "#noExtraLines()" + LIST
    # getFriendlyReport() is 1.21's getFriendlyReport(type, lines) under another name: a call goes through a static
    # that makes the virtual 1.21 call (a reshape keeps the name); a subclass's super call is the hook's
    f.put("instanceRedirects", CR + ".method_568()Ljava/lang/String;", S + "#getFriendlyReport")
    f.hook("method_568()Ljava/lang/String;", "method_60921(" + RT_ + LIST + ")Ljava/lang/String;", "class_128",
           "1.21 builds every report text in getFriendlyReport(type, lines) (the one-argument form and saveToFile call "
           "it): a 1.20.6 subclass's getFriendlyReport() answers it; its super call gets the type and lines vanilla passed",
           args=[], frames=G, superCalls="rewrite")
    f.put("frameDefaults", "method_568()Ljava/lang/String;", {"0": S + "#crashReportType()" + RT_, "1": S + "#noExtraLines()" + LIST})
    f.reshape(CR + ".method_572()" + FILE, "()" + PATH, [], "the saved report's file, as 1.20.6's File (null before it is saved)",
              ret=to_file)
    f.hook("method_572()" + FILE, "method_572()" + PATH, "class_128",
           "1.21 asks getSaveFile() for a Path: a 1.20.6 override's File, as a path", unwrap=to_path)
    f.reshape(CR + ".method_569(" + FILE + ")Z", "(" + PATH + RT_ + LIST + ")Z",
              ["conv:0 " + S + "#path(" + FILE + ")" + PATH, crash, lines],
              "1.20.6's saveToFile(file) wrote the crash report: 1.21's, to the same file, as ReportType.CRASH")
    f.hook("method_569(" + FILE + ")Z", "method_569(" + PATH + RT_ + LIST + ")Z", "class_128",
           "1.21 saves through saveToFile(path, type, lines): a 1.20.6 subclass's saveToFile(File) saves the report; "
           "its super call is the reshape above (the crash report, as 1.20.6 wrote it)",
           args=[0], wrap=S + ".file(" + PATH + ")" + FILE)


# ------------------------------------------------------------------------------------------------ constants, util
def constants(f):
    K = RT + "/CbWorldConstants"
    for field, desc, target in (("field_29743", "Z", "USE_NEW_RENDERSYSTEM"), ("field_29744", "Z", "MULTITHREADED_RENDERING"),
                                ("field_29727", "Z", "DEBUG_STORE_CHUNK_STACKTRACES"),
                                ("field_43095", "Ljava/util/Set;", "DATA_FIX_TYPES_TO_OPTIMIZE")):
        f.put("fieldRedirects", MC + "class_155." + field + ":" + desc, K + "#" + target)
    # 1.21 removed DATA_FIX_TYPES_TO_OPTIMIZE: writes land in CB too, with their 1.20.6 effect (CbWorldDataFixersMixin)
    f.put("staticFieldSetters", MC + "class_155.field_43095:Ljava/util/Set;", K + "#setDataFixTypesToOptimize")
    f.put("staticRedirects", MC + "class_155.method_43250()V", K + "#enableDataFixerOptimizations")
    # Tadpole.HITBOX_WIDTH / HEIGHT: final in 1.21; reads follow what a 1.20.6 mod wrote (CbEntityFrogspawnMixin)
    A = RT + "/CbEntityAnimals"
    for field, what in (("field_39302", "Width"), ("field_39303", "Height")):
        f.put("fieldGetters", MC + "class_7110." + field + ":F", A + "#tadpoleHitbox" + what)
        f.put("staticFieldSetters", MC + "class_7110." + field + ":F", A + "#setTadpoleHitbox" + what)
    f.put("fieldGetters", MC + "class_7701.field_46779:" + L("class_7696"), K)
    f.put("fieldRedirects", MC + "class_6088.field_31161:I", K + "#SOUND_ENDER_EYE_LAUNCH")
    f.put("fieldRedirects", MC + "class_1463.field_30368:I", K + "#RABBIT_EVIL_ATTACK_POWER")
    f.put("fieldRedirects", MC + "class_1463.field_30369:I", K + "#RABBIT_EVIL_ARMOR_VALUE")
    f.put("fieldRedirects", MC + "class_1492.field_30412:I", K + "#INV_CHEST_COUNT")
    f.put("fieldRedirects", MC + "class_1350.field_30205:I", K + "#TELEPORT_WHEN_DISTANCE_IS")
    f.put("fieldRedirects", MC + "class_1308.field_30086:Ljava/lang/String;", K + "#LEASH_TAG")
    f.put("fieldRedirects", MC + "class_1668.field_51507:D", K + "#ATTACK_DEFLECTION_SCALE")
    f.put("fieldRedirects", MC + "class_1668.field_51508:D", K + "#BOUNCE_DEFLECTION_SCALE")
    f.put("instanceRedirects", MC + "class_156$class_158.method_669(Ljava/net/URL;)V", RT + "/CbWorldUtil#openUrl")


# ------------------------------------------------------------------------------------------------ resources, commands, gametest
def resources_and_commands(f):
    R, C = RT + "/CbWorldResources", RT + "/CbWorldCommands"
    RL, STR = MC + "class_2960", "Ljava/lang/String;"
    f.put("ctorRedirects", RL + ".<init>(" + STR + ")V", R + "#newResourceLocation")
    f.put("ctorRedirects", RL + ".<init>(" + STR + STR + ")V", R + "#newResourceLocation")
    f.ctor(RL + ".<init>(" + STR + ")V", "(" + STR + STR + ")V",
           ["conv:0 " + R + "#parsedNamespace(" + STR + ")" + STR, "conv:0 " + R + "#parsedPath(" + STR + ")" + STR],
           "a 1.20.6 subclass's super(location): the namespace and path 1.20.6 split and validated, onto the "
           "(namespace, path) constructor the layer's access widener opens (the class is extendable through it too)")
    f.ctor(RL + ".<init>(" + STR + STR + ")V", "(" + STR + STR + ")V",
           ["conv:0,1 " + R + "#checkedNamespace(" + STR + STR + ")" + STR, "conv:0,1 " + R + "#checkedPath(" + STR + STR + ")" + STR],
           "a 1.20.6 subclass's super(namespace, path): validated as 1.20.6's constructor validated, namespace first")
    f.ctor(RL + ".<init>(" + STR + STR + L("class_2960$class_7658") + ")V", "(" + STR + STR + ")V", ["keep:0", "keep:1", "drop:2"],
           "1.20.6's (namespace, path, Dummy) constructor took both unchecked: 1.21's private (namespace, path) constructor "
           "does the same (assertions only)")
    f.put("staticRedirects", RL + ".method_12830(" + STR + "C)[" + STR, R + "#decompose")
    f.put("staticRedirects", RL + ".method_20207(" + STR + ")Z", R + "#isValidResourceLocation")
    # commands
    HOLDER, DCM, DCP = L("class_6880"), L("class_9323"), L("class_9326")
    patch = "conv:1 " + C + "#patchOf(" + DCM + ")" + DCP
    f.ctor(MC + "class_2290.<init>(" + HOLDER + DCM + ")V", "(" + HOLDER + DCP + ")V", ["keep:0", patch],
           "ItemInput's components as 1.21's patch: setting each component of 1.20.6's map (CbWorldCommands.patchOf)")
    f.ctor(MC + "class_2291$class_7215.<init>(" + HOLDER + DCM + ")V", "(" + HOLDER + DCP + ")V", ["keep:0", patch],
           "ItemResult's components as 1.21's patch (CbWorldCommands.patchOf)")
    f.reshape(MC + "class_2291$class_7215.comp_2439()" + DCM, "()" + DCP, [],
              "ItemResult.components() as 1.20.6's map: the components the patch sets (CbWorldCommands.mapOf)",
              ret="conv " + C + "#mapOf(" + DCP + ")" + DCM)
    sel_v = "(IZZLjava/util/function/Predicate;Lnet/minecraft/class_2096$class_2099;Ljava/util/function/Function;Lnet/minecraft/class_238;Ljava/util/function/BiConsumer;ZLjava/lang/String;Ljava/util/UUID;Lnet/minecraft/class_1299;Z)V"
    f.ctor(MC + "class_2300.<init>" + sel_v, sel_v.replace("Ljava/util/function/Predicate;", "Ljava/util/List;"),
           ["keep:0", "keep:1", "keep:2", "conv:3 " + C + "#predicateList(Ljava/util/function/Predicate;)Ljava/util/List;"]
           + ["keep:%d" % i for i in range(4, 13)],
           "EntitySelector's predicate as 1.21's list, which the selector tests all of (CbWorldCommands.predicateList)")
    # gametest
    GT = RT + "/CbWorldGameTests"
    gtr = "(Lnet/minecraft/class_4520$class_9116;Ljava/util/Collection;Lnet/minecraft/class_3218;Lnet/minecraft/class_4521;Lnet/minecraft/class_4520$class_9117;Lnet/minecraft/class_4520$class_9117;"
    f.ctor(MC + "class_4520.<init>" + gtr + ")V", gtr + "Z)V", ["keep"] * 6 + ["push " + GT + "#noHaltOnError()Z"],
           "1.20.6's runner never stopped at a failed test (1.21's haltOnError false)")
    f.ctor(MC + "class_9120.<init>(Lnet/minecraft/class_2338;I)V", "(Lnet/minecraft/class_2338;IZ)V",
           ["keep", "keep", "push " + GT + "#noClearOnBatch()Z"],
           "1.20.6's StructureGridSpawner left earlier batches standing (1.21's clearOnBatch false)")


# ------------------------------------------------------------------------------------------------ portals, dimension changes
def portals(f, v):
    P, PK = RT + "/CbEntityPortals", RT + "/CbEntityPackets"
    E, SL, SP, BP, WB, V3 = MC + "class_1297", L("class_3218"), L("class_3222"), L("class_2338"), L("class_2784"), L("class_243")
    PI, OPT, PORTAL = L("class_5454"), "Ljava/util/Optional;", L("class_9797")
    # PortalInfo: 1.21 gave its intermediary name to DimensionTransition; a 1.20.6 PortalInfo is one without a level
    f.put("ctorRedirects", MC + "class_5454.<init>(" + V3 + V3 + "FF)V", P + "#portalInfo")
    f.ctor(MC + "class_5454.<init>(" + V3 + V3 + "FF)V", "(" + SL + V3 + V3 + "FF" + L("class_5454$class_9823") + ")V",
           ["push " + P + "#noLevel()" + SL, "keep:0", "keep:1", "keep:2", "keep:3",
            "push " + P + "#nothingAfter()" + L("class_5454$class_9823")],
           "a 1.20.6 PortalInfo subclass's super(pos, speed, yRot, xRot): the record's constructor with no level and nothing "
           "after the move (the layer's access widener makes the record extendable)")
    for field, desc, getter in (("field_25879", V3, "portalPos"), ("field_25880", V3, "portalSpeed"),
                                ("field_25881", "F", "portalYRot"), ("field_25882", "F", "portalXRot")):
        f.put("instanceFieldGetters", MC + "class_5454." + field + ":" + desc, P + "#" + getter)
    # the entity's portal state: 1.21's PortalProcessor for the nether portal, the entrance kept by the Entity mixin
    for field, desc, getter, setter in (("field_5963", "Z", "isInsidePortal", "setInsidePortal"),
                                        ("field_5972", "I", "portalTime", "setPortalTime"),
                                        ("field_5991", BP, "portalEntrancePos", "setPortalEntrancePos")):
        f.put("instanceFieldGetters", E + "." + field + ":" + desc, P + "#" + getter)
        f.put("instanceFieldSetters", E + "." + field + ":" + desc, P + "#" + setter)
    # changeDimension(ServerLevel)
    cd = "method_5731(" + SL + ")" + L("class_1297")
    f.put("instanceRedirects", E + "." + cd, P + "#changeDimension")
    for owner in (E, MC + "class_3222", MC + "class_1542", MC + "class_3988", MC + "class_1684"):
        f.put("superRedirects", owner + "." + cd, P + "#superChangeDimension")
    f.hook(cd, "method_5731(" + PI + ")" + L("class_1297"), E,
           "1.21 moves an entity with changeDimension(transition): a 1.20.6 class's changeDimension(dest) override runs for "
           "the transition's level, its super call moves by that transition (CbEntityPortals.changeDimension)",
           bridge=P + ".changeDimension(L" + P + "$LegacyChangeDimension;" + PI + ")" + L("class_1297"),
           adapter=P + "$LegacyChangeDimension", guard=G)
    # findDimensionEntryPoint / getExitPortal: asked by the layer's 1.20.6 rules and by the portal-block mixins
    fe = "method_30329(" + SL + ")" + PI
    f.put("instanceRedirects", E + "." + fe, P + "#findDimensionEntryPoint")
    for owner in (E, MC + "class_3222"):
        f.put("superRedirects", owner + "." + fe, P + "#superFindDimensionEntryPoint")
    ge = "method_30330(" + SL + BP + "Z" + WB + ")" + OPT
    f.put("instanceRedirects", E + "." + ge, P + "#getExitPortal")
    for owner in (E, MC + "class_3222"):
        f.put("superRedirects", owner + "." + ge, P + "#superGetExitPortal")
    for shape, adapter, why in ((fe, "LegacyEntryPoint", "findDimensionEntryPoint"), (ge, "LegacyExitPortal", "getExitPortal"),
                                ("method_5741()I", "LegacyPortalWaitTime", "getPortalWaitTime")):
        f.w["adapterHooks"].append({
            "under": E, "adapter": P + "$" + adapter, "requires": [shape], "synth": [],
            "basis": "1.20.6 asked the entity's own " + why + " where 1.21 asks the portal block: the layer asks it "
                     "again through this interface (CbEntityPortals, the portal-block mixins)"})
    # handleInsidePortal(pos) / handleNetherPortal(): 1.21's setAsInsidePortal(portal, pos) / handlePortal()
    hi = "method_5717(" + BP + ")V"
    f.put("instanceRedirects", E + "." + hi, P + "#handleInsidePortal")
    f.put("superRedirects", E + "." + hi, P + "#superHandleInsidePortal")
    f.hook(hi, "method_60697(" + PORTAL + BP + ")V", E,
           "1.21 marks an entity in any portal with setAsInsidePortal(portal, pos); 1.20.6's handleInsidePortal(pos) was the "
           "nether portal's: the override gets the nether portal, 1.21 the others (CbEntityPortals.setAsInsidePortal)",
           bridge=P + ".setAsInsidePortal(L" + P + "$LegacyInsidePortal;" + PORTAL + BP + ")V",
           adapter=P + "$LegacyInsidePortal", guard=G)
    hn = "method_18379()V"
    f.put("instanceRedirects", E + "." + hn, P + "#handleNetherPortal")
    f.put("superRedirects", E + "." + hn, P + "#superHandleNetherPortal")
    f.hook(hn, "method_60698()V", E,
           "1.21's baseTick calls handlePortal() where 1.20.6's called handleNetherPortal(): the override runs unless the "
           "entity is in an end portal or gateway, which 1.20.6 never ticked this way (CbEntityPortals.handlePortal)",
           bridge=P + ".handlePortal(L" + P + "$LegacyNetherPortalTick;)V", adapter=P + "$LegacyNetherPortalTick", guard=G)
    f.put("instanceRedirects", E + ".method_5741()I", P + "#getPortalWaitTime")
    f.put("superRedirects", E + ".method_5741()I", P + "#superGetPortalWaitTime")
    f.put("superRedirects", MC + "class_1657.method_5741()I", P + "#superGetPortalWaitTime")
    f.put("instanceRedirects", E + ".method_20620(DDD)V", P + "#teleportToWithTicket")
    f.w["covers"].append(E + ".method_59657(" + V3 + ")" + OPT)
    # setOnGroundWithKnownMovement is 1.21's setOnGroundWithMovement (same body)
    for owner in with_subclasses(v, E):
        f.put("methodRenames", owner + ".method_52208(Z" + V3 + ")V", "method_60607")
    f.w["overrideRenames"].append({
        "old": "method_52208(Z" + V3 + ")V", "new": "method_60607", "onlyUnder": E, "superCalls": "rename",
        "basis": "setOnGroundWithKnownMovement is 1.21's setOnGroundWithMovement: same arguments, same body"})
    # canChangeDimensions() is canUsePortal(allowPassengers) -- the same overrides in both releases; 1.20.6 refused a passenger
    f.reshape(E + ".method_5822()Z", "(Z)Z", ["push " + P + "#refusePassengers()Z"],
              "canChangeDimensions() is 1.21's canUsePortal(allowPassengers): 1.20.6 refused a passenger, so false")
    f.put("frameDefaults", "method_5822()Z", {
        "0": "zero: canChangeDimensions refused a passenger in 1.20.6, which is canUsePortal(false)"})
    # spawn packets
    f.put("instanceRedirects", E + ".method_18002()" + L("class_2596"), PK + "#getAddEntityPacket")
    f.put("frameDefaults", "method_18002()" + L("class_2596"), {
        "0": PK + "#serverEntityFor(" + L("class_1297") + ")" + L("class_3231")})
    # ServerLevel arrivals
    for name, adapter, static in (("method_18207", "LegacyCommandArrival", "addDuringCommandTeleport"),
                                  ("method_18211", "LegacyPortalArrival", "addDuringPortalTeleport")):
        f.put("instanceRedirects", MC + "class_3218." + name + "(" + SP + ")V", P + "#" + static)
        f.put("superRedirects", MC + "class_3218." + name + "(" + SP + ")V", P + "#superAddDuring")
        f.w["adapterHooks"].append({
            "under": MC + "class_3218", "adapter": P + "$" + adapter, "requires": [name + "(" + SP + ")V"], "synth": [],
            "basis": "marks a 1.20.6 ServerLevel subclass's own " + static + " for 1.21's addDuringTeleport (CbEntityPortals)"})
    f.w["adapterHooks"].append({
        "under": MC + "class_3218", "adapter": P + "$LegacyLevel", "requires": [], "guard": G,
        "synth": [{"new": "method_18769(" + L("class_1297") + ")V",
                   "call": P + ".addDuringTeleport(L" + P + "$LegacyLevel;" + L("class_1297") + ")V"}],
        "basis": "1.21 adds every arriving entity with addDuringTeleport; a 1.20.6 subclass's addDuringCommandTeleport / "
                 "addDuringPortalTeleport answers the players 1.20.6 sent there (CbEntityPortals.addDuringTeleport)"})
    f.put("staticRedirects", MC + "class_3218.method_29200(" + SL + ")V", P + "#makeObsidianPlatform")
    f.put("staticRedirects", MC + "class_2424.method_30484(" + SL + L("class_5459$class_5460") + L("class_2350$class_2351")
          + V3 + L("class_1297") + V3 + "FF)" + PI, P + "#createPortalInfo")
    # PortalForcer.findPortalAround: Optional<FoundRectangle> in 1.20.6, Optional<BlockPos> in 1.21 (generic drift)
    fpa = "method_30483(" + BP + "Z" + WB + ")" + OPT
    f.put("instanceRedirects", MC + "class_1946." + fpa, P + "#findPortalAround")
    f.put("superRedirects", MC + "class_1946." + fpa, P + "#superFindPortalAround")
    f.w["convertHooks"].append({
        "under": MC + "class_1946", "member": fpa, "ret": P + "#portalPositions(" + OPT + ")" + OPT,
        "basis": "1.21 asks findClosestPortalPosition for the portal block; a 1.20.6 override's rectangles answer with their corner"})
    # end gateway and respawn statics
    f.put("staticRedirects", MC + "class_2643.method_11409(" + L("class_1937") + BP + L("class_2680") + L("class_1297")
          + L("class_2643") + ")V", P + "#teleportEntity")
    f.put("staticRedirects", MC + "class_2643.method_30276(" + L("class_1297") + ")Z", P + "#canEntityTeleport")
    f.put("staticRedirects", MC + "class_1657.method_26091(" + SL + BP + "FZZ)" + OPT, P + "#findRespawnPositionAndUseSpawnBlock")


# ------------------------------------------------------------------------------------------------ living entities, mobs, players
def code_of(classes, owner, shape):
    """the body of a method the class declares (None: not declared there, or no body)"""
    c = classes.get(owner)
    for m in c.methods if c else ():
        if m.name + m.desc == shape:
            return m.code
    return None


def declarers(classes, shape):
    """every class of the jar that declares the shape (non-static), sorted"""
    return sorted(n for n, c in classes.items()
                  if any(m.name + m.desc == shape and not m.access & classfile.ACC_STATIC for m in c.methods))


def living(f, v, vc):
    E, A = RT + "/CbEntityLiving", RT + "/CbEntityAnimals"
    ENT, LIV, MOB, PLY = MC + "class_1297", MC + "class_1309", MC + "class_1308", MC + "class_1657"
    SLOT, STACK, LEVEL, SL, DS, BP = L("class_1304"), L("class_1799"), L("class_1937"), L("class_3218"), L("class_1282"), L("class_2338")
    server_level = E + "#serverLevel(" + L("class_1297") + ")" + SL
    f.put("instanceRedirects", ENT + ".method_5639(I)V", E + "#igniteForSeconds")
    f.put("staticRedirects", LIV + ".method_32326(" + STACK + ")" + SLOT, E + "#getEquipmentSlotForItem")
    # eat(level, stack): 1.21 passes the food it read off the stack
    f.put("instanceRedirects", LIV + ".method_18866(" + LEVEL + STACK + ")" + STACK, E + "#eat")
    f.put("frameDefaults", "method_18866(" + LEVEL + STACK + ")" + STACK, {
        "2": E + "#foodOf(" + L("class_1309") + LEVEL + STACK + ")" + L("class_4174")})
    # broadcastBreakEvent(slot) -> onEquippedItemBroken(item, slot). The hook is wired here, not left to
    # mechanical.json: the adapter below makes the generator count the old shape as served
    f.put("instanceRedirects", LIV + ".method_20235(" + SLOT + ")V", E + "#broadcastBreakEvent")
    f.hook("method_20235(" + SLOT + ")V", "method_20235(" + L("class_1792") + SLOT + ")V", "class_1309",
           "1.21 calls onEquippedItemBroken(item, slot) where 1.20.6 called broadcastBreakEvent(slot): the override gets "
           "the slot, its super call the item and slot vanilla passed",
           args=[1], frames=G, superCalls="rewrite")
    f.put("frameDefaults", "method_20235(" + SLOT + ")V", {
        "0": E + "#brokenItem(" + L("class_1309") + SLOT + ")" + L("class_1792")})
    f.w["adapterHooks"].append({
        "under": LIV, "adapter": E + "$LegacyBreakEvent", "requires": ["method_20235(" + SLOT + ")V"], "synth": [],
        "basis": "a 1.20.6 caller's broadcastBreakEvent(slot) answers through a 1.20.6 subclass's own override (CbEntityLiving)"})
    # server-side methods 1.21 hands the server level (or the attacker)
    f.put("instanceRedirects", LIV + ".method_16080(" + DS + ")V", E + "#dropAllDeathLoot")
    f.put("frameDefaults", "method_16080(" + DS + ")V", {"0": server_level})
    f.put("instanceRedirects", LIV + ".method_23883()V", E + "#dropExperience")
    f.put("frameDefaults", "method_23883()V", {
        "0": "zero: 1.20.6's dropExperience() had no attacker; with none 1.21 lets no enchantment change the experience, "
             "and 1.20.6 had no such enchantment"})
    f.put("instanceRedirects", LIV + ".method_6126(" + BP + ")V", E + "#onChangedBlock")
    f.put("frameDefaults", "method_6126(" + BP + ")V", {"0": server_level})
    f.put("instanceRedirects", MC + "class_3763.method_16484(IZ)V", E + "#applyRaidBuffs")
    f.put("frameDefaults", "method_16484(IZ)V", {"0": server_level})
    # equipSaddle(source) -> equipSaddle(stack, source): 1.20.6's horse took a new saddle stack
    SS = L("class_3419")
    f.reshape(MC + "class_5146.method_6576(" + SS + ")V", "(" + STACK + SS + ")V", ["push " + A + "#newSaddle()" + STACK, "keep:0"],
              "1.20.6's AbstractHorse.equipSaddle put new ItemStack(SADDLE) in the saddle slot, which 1.21 takes as the "
              "argument; Pig and Strider ignore it in both")
    f.put("frameDefaults", "method_6576(" + SS + ")V", {"0": A + "#newSaddle()" + STACK})
    # startAutoSpinAttack(ticks): 1.20.6's spin attacked with ATTACK_DAMAGE (CbEntitySpinAttackMixin)
    f.put("instanceRedirects", PLY + ".method_40126(I)V", E + "#startAutoSpinAttack")
    f.put("frameDefaults", "method_40126(I)V", {
        "1": E + "#legacySpinDamage()F",
        "2": "zero: no item: 1.21's getWeaponItem then answers the main hand, the stack 1.20.6's attack used"})
    # canWearBodyArmor(): 1.21 asks canUseSlot(BODY) where 1.20.6 asked it
    f.put("instanceRedirects", MOB + ".method_6735()Z", E + "#canWearBodyArmor")
    assert declarers(vc, "method_6735()Z") == [MC + c for c in ("class_1308", "class_1498", "class_1501")]
    for owner in ("class_1308", "class_1498", "class_1501"):
        f.put("superRedirects", MC + owner + ".method_6735()Z", E + "#superCanWearBodyArmor")
    f.hook("method_6735()Z", "method_56991(" + SLOT + ")Z", "class_1308",
           "1.21 asks canUseSlot(BODY) where 1.20.6 asked canWearBodyArmor() (the horse inventory's armor slot, the "
           "dispenser's horse armor, the horse screen): a 1.20.6 override answers the body slot, super the others",
           bridge=E + ".canUseSlot(L" + E + "$LegacyBodyArmor;" + SLOT + ")Z", adapter=E + "$LegacyBodyArmor", guard=G)
    # getRandom() moved from LivingEntity to Entity (same field)
    RS = L("class_5819")
    for owner in with_subclasses(v, LIV):
        f.put("methodRenames", owner + ".method_6051()" + RS, "method_59922")
    f.w["overrideRenames"].append({
        "old": "method_6051()" + RS, "new": "method_59922", "onlyUnder": LIV, "superCalls": "rename",
        "basis": "LivingEntity.getRandom() is 1.21's Entity.getRandom(): the same random field"})


# ------------------------------------------------------------------------------------------------ leashes
def leash(f, v, vc):
    LE = RT + "/CbEntityLeash"
    MOB, PFM, ENT, PLY = MC + "class_1308", MC + "class_1314", L("class_1297"), L("class_1657")
    # the members that only moved to Leashable: same arguments, same bodies over 1.21's LeashData
    moved = (("method_5933()" + ENT, "method_60952", "getLeashHolder"), ("method_5934()Z", "method_60953", "isLeashed"),
             ("method_5954(" + ENT + "Z)V", "method_60964", "setLeashedTo"),
             ("method_18810(I)V", "method_60968", "setDelayedLeashHolderId"), ("method_58151()Z", "method_60954", "mayBeLeashed"))
    subs = with_subclasses(v, MOB)
    for shape, new, what in moved:
        for owner in subs:
            f.put("methodRenames", owner + "." + shape, new)
        f.w["overrideRenames"].append({
            "old": shape, "new": new, "onlyUnder": MOB, "superCalls": "rename",
            "basis": "Mob." + what + " is 1.21's Leashable." + what + " (the leash moved from Mob's fields to its LeashData)"})
    # canBeLeashed(Player) -> canBeLeashed() / canHaveALeashAttachedToIt(): which 1.20.6 bodies tested the leash
    shape = "method_5931(" + PLY + ")Z"
    tests, constant = set(), {}
    for name in sorted(vc):
        code = code_of(vc, name, shape)
        if code is None:
            continue
        if len(code) == 2 and code[1] == 0xAC and code[0] in (0x03, 0x04):
            constant[name] = code[0] == 0x04
        else:
            tests.add(name)
    assert {c for c, val in constant.items() if val} == {MC + "class_1433", MC + "class_5762"}, constant
    assert {c for c, val in constant.items() if not val} == {
        MC + c for c in ("class_1421", "class_1440", "class_1481", "class_1480", "class_3988")}, constant
    leash_tests = {MC + c for c in ("class_1308", "class_1321", "class_1493", "class_1477", "class_5136", "class_4760")}
    assert tests == leash_tests, tests
    f.put("instanceRedirects", MOB + "." + shape, LE + "#canBeLeashed")
    # a super call lands on the 1.20.6 body of the class that declared it (CbEntityLeash, one per class)
    for owner in sorted(set(constant) | tests):
        f.put("superRedirects", owner + "." + shape, LE + "#superCanBeLeashed")
    f.hook(shape, "method_5931()Z", "class_1308",
           "1.21 asks canBeLeashed() (in canHaveALeashAttachedToIt, from Entity.interact) where 1.20.6 asked "
           "canBeLeashed(player) in Mob's interaction: the 1.20.6 override answers for the interacting player",
           bridge=LE + ".canBeLeashed(L" + LE + "$LegacyCanBeLeashed;)Z", adapter=LE + "$LegacyCanBeLeashed", guard=G)
    # tickLeash(): Entity.baseTick's Leashable.tickLeash (CbEntityLeashMixin) ticks a 1.20.6 override instead
    f.put("instanceRedirects", MOB + ".method_5995()V", LE + "#tickLeash")
    assert declarers(vc, "method_5995()V") == [MOB, PFM]
    f.put("superRedirects", MOB + ".method_5995()V", LE + "#superTickLeash")
    f.put("superRedirects", PFM + ".method_5995()V", LE + "#superTickLeash")
    f.w["adapterHooks"].append({
        "under": MOB, "adapter": LE + "$LegacyTickLeash", "requires": ["method_5995()V"], "synth": [],
        "basis": "1.20.6's Mob.tick called this.tickLeash(); 1.21's Entity.baseTick calls the static Leashable.tickLeash, "
                 "which CbEntityLeashMixin hands to a 1.20.6 override through this interface"})
    # onLeashDistance(distance) -> handleLeashAtDistance(holder, distance)
    od = "method_6142(F)V"
    f.put("instanceRedirects", PFM + "." + od, LE + "#onLeashDistance")
    assert declarers(vc, od) == [MC + c for c in ("class_1314", "class_1496", "class_7689")]
    for owner in ("class_1314", "class_1496", "class_7689"):
        f.put("superRedirects", MC + owner + "." + od, LE + "#superOnLeashDistance")
    f.hook(od, "method_60956(" + ENT + "F)Z", "class_1314",
           "1.21 asks handleLeashAtDistance(holder, distance) where 1.20.6's PathfinderMob.tickLeash restricted the mob to "
           "the holder, stopped a sitting tamable and called onLeashDistance(distance): the same, in that order",
           bridge=LE + ".handleLeashAtDistance(L" + LE + "$LegacyLeashDistance;" + ENT + "F)Z",
           adapter=LE + "$LegacyLeashDistance", guard=G)


# ------------------------------------------------------------------------------------------------ horses
def horses(f, vc):
    A, E = RT + "/CbEntityAnimals", RT + "/CbEntityLiving"
    # canWearBodyArmor(): the adapter for a class that also declares canUseSlot, where the bridge hook steps aside
    f.w["adapterHooks"].append({
        "under": MC + "class_1308", "adapter": E + "$LegacyBodyArmor", "requires": ["method_6735()Z"], "synth": [],
        "basis": "a 1.20.6 caller's canWearBodyArmor() answers through a 1.20.6 subclass's own override (CbEntityLiving)"})
    # getInventorySize(): final in 1.21 (1 + 3 x getInventoryColumns()); 1.20.6's sizes are the same for every vanilla horse
    assert declarers(vc, "method_6750()I") == [MC + c for c in ("class_1492", "class_1496", "class_1501")]
    f.w["overrideRenames"].append({
        "old": "method_6750()I", "new": "method_6750$cb1_21", "onlyUnder": MC + "class_1496", "adapter": A + "$LegacyInventorySize",
        "basis": "1.21 made AbstractHorse.getInventorySize() final: a 1.20.6 override is renamed and answers through "
                 "CbEntityHorseInventoryMixin (its super call reaches 1.21's body, the same number for vanilla horses)"})
    # HorseInventoryMenu(id, inventory, container, horse): 1.21 takes the chest columns
    f.ctor(MC + "class_1724.<init>(IL" + MC + "class_1661;L" + MC + "class_1263;L" + MC + "class_1496;)V",
           "(IL" + MC + "class_1661;L" + MC + "class_1263;L" + MC + "class_1496;I)V",
           ["keep", "keep", "keep", "keep", "conv:3 " + A + "#chestColumns(L" + MC + "class_1496;)I"],
           "1.20.6's menu laid out the horse's getInventoryColumns() columns of chest slots when it had a chest; 1.21 "
           "takes that count, 0 without a chest")


# ------------------------------------------------------------------------------------------------ enchantment effects on entities
def enchanting(f, vc):
    S, EN = RT + "/CbEntitySoulSpeed", RT + "/CbEntityEnchanting"
    LIV, MOB, PLY = MC + "class_1309", MC + "class_1308", MC + "class_1657"
    # soul speed: six 1.20.6 methods, data in 1.21
    soul = (("method_27302()Z", "canSpawnSoulSpeedParticle", "LegacyCanSpawnParticle", ["class_1309", "class_746"]),
            ("method_25937()V", "spawnSoulSpeedParticle", "LegacySpawnParticle", ["class_1309"]),
            ("method_27303()Z", "onSoulSpeedBlock", "LegacyOnSoulSpeedBlock", ["class_1309", "class_1657"]),
            ("method_29500(" + L("class_2680") + ")Z", "shouldRemoveSoulSpeed", "LegacyShouldRemove", ["class_1309", "class_1657"]),
            ("method_29501()V", "removeSoulSpeed", "LegacyRemove", ["class_1309"]),
            ("method_29502()V", "tryAddSoulSpeed", "LegacyTryAdd", ["class_1309"]))
    for shape, name, adapter, homes in soul:
        assert declarers(vc, shape) == sorted(MC + h for h in homes), (shape, declarers(vc, shape))
        f.put("instanceRedirects", LIV + "." + shape, S + "#" + name)
        for home in homes:
            owner = S + "Client" if home == "class_746" else S
            f.put("superRedirects", MC + home + "." + shape, owner + "#super" + name[0].upper() + name[1:])
        f.w["adapterHooks"].append({
            "under": LIV, "adapter": S + "$" + adapter, "requires": [shape], "synth": [],
            "basis": "1.20.6's " + name + " is overridable, 1.21's soul speed is data: a 1.20.6 caller and 1.20.6's drive "
                     "(CbEntitySoulSpeedMixin) answer through the subclass's own override"})
    # dropCustomDeathLoot(source, looting, recentlyHit) -> (level, source, recentlyHit)
    DS, SL = L("class_1282"), L("class_3218")
    dl = "method_6099(" + DS + "IZ)V"
    homes = ("class_1308", "class_1309", "class_1528", "class_1548", "class_1560", "class_1613", "class_1639", "class_1642", "class_4836")
    assert declarers(vc, dl) == sorted(MC + h for h in homes), declarers(vc, dl)
    f.put("instanceRedirects", LIV + "." + dl, EN + "#dropCustomDeathLoot")
    for home in homes:
        f.put("superRedirects", MC + home + "." + dl, EN + "#superDropCustomDeathLoot")
    f.hook(dl, "method_6099(" + SL + DS + "Z)V", "class_1309",
           "1.21 asks dropCustomDeathLoot(level, source, recentlyHit) where 1.20.6 asked it with the looting level of a "
           "killing player: the 1.20.6 override gets that level (CbEntityEnchanting.looting)",
           bridge=EN + ".dropCustomDeathLoot(L" + EN + "$LegacyDeathLoot;" + SL + DS + "Z)V", adapter=EN + "$LegacyDeathLoot", guard=G)
    # spawn equipment enchanting
    RS, DI, SLA, SLOT = L("class_5819"), L("class_1266"), L("class_5425"), L("class_1304")
    sw, sa = "method_30759(" + RS + "F)V", "method_30758(" + RS + "F" + SLOT + ")V"
    assert declarers(vc, sw) == [MOB, MC + "class_1604"] and declarers(vc, sa) == [MOB]
    f.put("instanceRedirects", MOB + "." + sw, EN + "#enchantSpawnedWeapon")
    f.put("superRedirects", MOB + "." + sw, EN + "#superEnchantSpawnedWeapon")
    f.put("superRedirects", MC + "class_1604." + sw, EN + "#superEnchantSpawnedWeapon")
    f.hook(sw, "method_30759(" + SLA + RS + DI + ")V", "class_1308",
           "1.21 asks enchantSpawnedWeapon(level, random, difficulty) where 1.20.6 passed the difficulty's special "
           "multiplier: the 1.20.6 override gets it; its super call enchants as 1.20.6 did",
           bridge=EN + ".enchantSpawnedWeapon(L" + EN + "$LegacySpawnWeapon;" + SLA + RS + DI + ")V", adapter=EN + "$LegacySpawnWeapon", guard=G)
    f.put("instanceRedirects", MOB + "." + sa, EN + "#enchantSpawnedArmor")
    f.put("superRedirects", MOB + "." + sa, EN + "#superEnchantSpawnedArmor")
    f.hook(sa, "method_30758(" + SLA + RS + SLOT + DI + ")V", "class_1308",
           "1.21 asks enchantSpawnedArmor(level, random, slot, difficulty) where 1.20.6 passed the special multiplier",
           bridge=EN + ".enchantSpawnedArmor(L" + EN + "$LegacySpawnArmor;" + SLA + RS + SLOT + DI + ")V", adapter=EN + "$LegacySpawnArmor", guard=G)
    pd = "method_5984(" + RS + DI + ")V"
    f.put("instanceRedirects", MOB + "." + pd, EN + "#populateDefaultEquipmentEnchantments")
    f.put("frameDefaults", pd, {"0": EN + "#serverLevelAccessor(" + L("class_1297") + ")" + SLA})
    # doEnchantDamageEffects(attacker, target): 1.21 calls EnchantmentHelper.doPostAttackEffects (CbEntityPostAttackMixin)
    de = "method_5723(" + L("class_1309") + L("class_1297") + ")V"
    assert declarers(vc, de) == [MC + "class_1297"]
    f.put("instanceRedirects", MC + "class_1297." + de, EN + "#doEnchantDamageEffects")
    f.put("superRedirects", MC + "class_1297." + de, EN + "#superDoEnchantDamageEffects")
    f.w["adapterHooks"].append({
        "under": MC + "class_1297", "adapter": EN + "$LegacyEnchantDamageEffects", "requires": [de], "synth": [],
        "basis": "1.21 calls EnchantmentHelper.doPostAttackEffects where 1.20.6 called the hitting entity's "
                 "doEnchantDamageEffects: CbEntityPostAttackMixin hands the call to a 1.20.6 override through this interface"})
    # ThrownTrident.isChanneling(): 1.21's channeling is the weapon's enchantment data (CbEntityTridentMixin)
    ch = "method_31551()Z"
    assert declarers(vc, ch) == [MC + "class_1685"]
    f.put("instanceRedirects", MC + "class_1685." + ch, EN + "#isChanneling")
    f.put("superRedirects", MC + "class_1685." + ch, EN + "#superIsChanneling")
    f.w["adapterHooks"].append({
        "under": MC + "class_1685", "adapter": EN + "$LegacyChanneling", "requires": [ch], "synth": [],
        "basis": "1.20.6 struck channeling's lightning when the trident's isChanneling() said so: CbEntityTridentMixin asks "
                 "a 1.20.6 override through this interface"})


# ------------------------------------------------------------------------------------------------ projectiles
def projectiles(f, vc):
    P, AR = RT + "/CbEntityProjectiles", RT + "/CbEntityArrows"
    ET, LV, LE, ST, V3 = L("class_1299"), L("class_1937"), L("class_1309"), L("class_1799"), L("class_243")
    # hurting projectiles: 1.20.6's (dx, dy, dz) is 1.21's direction, then 1.20.6's power vector and rest
    direction = "conv:%d,%d,%d " + P + "#direction(DDD)" + V3
    hurting = (("class_1668", ET + "DDDDDD" + LV, ET + "DDD" + V3 + LV, ["keep:0", "keep:1", "keep:2", "keep:3", direction % (4, 5, 6), "keep:7"], [4, 5, 6]),
               ("class_1668", ET + LE + "DDD" + LV, ET + LE + V3 + LV, ["keep:0", "keep:1", direction % (2, 3, 4), "keep:5"], [2, 3, 4]),
               ("class_3855", ET + "DDDDDD" + LV, ET + "DDD" + V3 + LV, ["keep:0", "keep:1", "keep:2", "keep:3", direction % (4, 5, 6), "keep:7"], [4, 5, 6]),
               ("class_3855", ET + LE + "DDD" + LV, ET + LE + V3 + LV, ["keep:0", "keep:1", direction % (2, 3, 4), "keep:5"], [2, 3, 4]),
               ("class_1670", LV + LE + "DDD", LV + LE + V3, ["keep:0", "keep:1", direction % (2, 3, 4)], [2, 3, 4]),
               ("class_1674", LV + LE + "DDDI", LV + LE + V3 + "I", ["keep:0", "keep:1", direction % (2, 3, 4), "keep:5"], [2, 3, 4]),
               ("class_1677", LV + LE + "DDD", LV + LE + V3, ["keep:0", "keep:1", direction % (2, 3, 4)], [2, 3, 4]),
               ("class_1677", LV + "DDDDDD", LV + "DDD" + V3, ["keep:0", "keep:1", "keep:2", "keep:3", direction % (4, 5, 6)], [4, 5, 6]),
               ("class_1687", LV + LE + "DDD", LV + LE + V3, ["keep:0", "keep:1", direction % (2, 3, 4)], [2, 3, 4]),
               ("class_8956", LV + "DDDDDD", LV + "DDD" + V3, ["keep:0", "keep:1", "keep:2", "keep:3", direction % (4, 5, 6)], [4, 5, 6]))
    for owner, old, new, ops, dirs in hurting:
        f.ctor(MC + owner + ".<init>(" + old + ")V", "(" + new + ")V", ops,
               "1.20.6's direction (dx, dy, dz) is 1.21's Vec3; afterwards the projectile takes 1.20.6's power vector and "
               "rest (CbEntityProjectiles.legacyPower)", post=P + "#legacyPower(" + L(owner) + "DDD)V", post_args=dirs)
    for axis, field in (("X", "field_7601"), ("Y", "field_7600"), ("Z", "field_7599")):
        f.put("instanceFieldGetters", MC + "class_1668." + field + ":D", P + "#" + axis.lower() + "Power")
        f.put("instanceFieldSetters", MC + "class_1668." + field + ":D", P + "#set" + axis + "Power")
    # wind charges: 1.20.6's damage calculators as facades; explode() at the charge's position
    f.put("classRenames", MC + "class_9236$class_9237", FAC + "/class_9236$class_9237")
    f.put("classRenames", MC + "class_8956$class_9036", FAC + "/class_8956$class_9036")
    f.put("fieldGetters", MC + "class_9236.field_50137:" + L("class_9236$class_9237"), P)
    f.put("instanceRedirects", MC + "class_9236.method_55055()V", P + "#explode")
    f.put("frameDefaults", "method_55055()V", {"0": P + "#position(" + L("class_1297") + ")" + V3})
    # arrows: 1.20.6's constructors took no weapon
    no_weapon = "push " + AR + "#noWeapon()" + ST
    origin = "push " + AR + "#origin()D"
    arrows = (("class_1665", ET + "DDD" + LV + ST, ET + "DDD" + LV + ST + ST, ["keep", "keep", "keep", "keep", "keep", "keep", no_weapon]),
              ("class_1665", ET + LE + LV + ST, ET + LE + LV + ST + ST, ["keep", "keep", "keep", "keep", no_weapon]),
              ("class_1665", ET + LV + ST, ET + "DDD" + LV + ST + ST, ["keep:0", origin, origin, origin, "keep:1", "keep:2", no_weapon]),
              ("class_1667", LV + "DDD" + ST, LV + "DDD" + ST + ST, ["keep", "keep", "keep", "keep", "keep", no_weapon]),
              ("class_1667", LV + LE + ST, LV + LE + ST + ST, ["keep", "keep", "keep", no_weapon]),
              ("class_1679", LV + "DDD" + ST, LV + "DDD" + ST + ST, ["keep", "keep", "keep", "keep", "keep", no_weapon]),
              ("class_1679", LV + LE + ST, LV + LE + ST + ST, ["keep", "keep", "keep", no_weapon]))
    for owner, old, new, ops in arrows:
        f.ctor(MC + owner + ".<init>(" + old + ")V", "(" + new + ")V", ops,
               "1.20.6's arrow constructors took no weapon: 1.21's with none, which is the same arrow (the shooter set the "
               "rest afterwards)" + ("; 1.20.6's (type, level, stack) left the arrow at the origin" if origin in ops else ""))
    A = MC + "class_1665"
    for shape, name, adapter in (("method_35207()I", "getKnockback", "LegacyGetKnockback"),
                                 ("method_7449(I)V", "setKnockback", "LegacySetKnockback"),
                                 ("method_7442(Z)V", "setShotFromCrossbow", "LegacyCrossbow"),
                                 ("method_7435(" + LE + "F)V", "setEnchantmentEffectsFromEntity", "LegacyEnchantmentEffects")):
        assert declarers(vc, shape) == [A], (shape, declarers(vc, shape))
        f.put("instanceRedirects", A + "." + shape, AR + "#" + name)
        f.put("superRedirects", A + "." + shape, AR + "#super" + name[0].upper() + name[1:])
        f.w["adapterHooks"].append({
            "under": A, "adapter": AR + "$" + adapter, "requires": [shape], "synth": [],
            "basis": "a 1.20.6 caller's " + name + " answers through a 1.20.6 arrow class's own override (CbEntityArrows)"})
    f.put("staticRedirects", MC + "class_1675.method_18813(" + LE + ST + "F)" + L("class_1665"), AR + "#getMobArrow")
    # AbstractSkeleton.getArrow(ammo, power) -> getArrow(ammo, power, weapon)
    ga = "method_6996(" + ST + "F)" + L("class_1665")
    f.put("instanceRedirects", MC + "class_1547." + ga, AR + "#getArrow")
    f.hook(ga, "method_6996(" + ST + "F" + ST + ")" + L("class_1665"), "class_1547",
           "1.21 asks getArrow(ammo, power, weapon): the 1.20.6 override gets the ammo and power, its super call the weapon "
           "vanilla passed", args=[0, 1], frames=G, superCalls="rewrite")
    f.put("frameDefaults", ga, {"2": AR + "#weaponOf(" + L("class_1297") + ")" + ST})


# ------------------------------------------------------------------------------------------------ hanging entities, paintings
def decorations(f, v, vc):
    D, PA = RT + "/CbEntityDecorations", RT + "/CbEntityPaintings"
    HE, KNOT, FRAME, PAINT, PV = MC + "class_1530", MC + "class_1532", MC + "class_1533", MC + "class_1534", MC + "class_1535"
    BP, DIR, AABB = L("class_2338"), L("class_2350"), L("class_238")
    # the box: 1.21 asks calculateBoundingBox(pos, direction) of a hanging entity, 1.20.6 computed it from the pixel sizes
    f.w["abstractFills"].append({
        "under": HE, "method": "method_59943(" + BP + DIR + ")" + AABB,
        "call": D + ".calculateBoundingBox(L" + HE + ";" + BP + DIR + ")" + AABB,
        "basis": "a 1.20.6 hanging entity sized its box from getWidth()/getHeight() in recalculateBoundingBox; 1.21 asks "
                 "calculateBoundingBox(pos, direction): 1.20.6's computation (CbEntityDecorations)"})
    assert declarers(vc, "method_6895()V") == [HE, KNOT, FRAME], declarers(vc, "method_6895()V")
    f.w["overrideRenames"].append({
        "old": "method_6895()V", "new": "method_6895$cb1_21", "onlyUnder": HE, "adapter": D + "$LegacyRecalculate",
        "basis": "1.21 made HangingEntity.recalculateBoundingBox() final: a 1.20.6 override is renamed and answers through "
                 "CbEntityHangingMixin (CbEntityKnotMixin for a knot, no hanging entity in 1.21); its super call reaches 1.21's body"})
    # getWidth()/getHeight(): pixels, gone in 1.21
    for shape, name, adapter in (("method_6897()I", "getWidth", "LegacyWidth"), ("method_6891()I", "getHeight", "LegacyHeight")):
        assert declarers(vc, shape) == [HE, KNOT, FRAME, PAINT], (shape, declarers(vc, shape))
        f.put("instanceRedirects", HE + "." + shape, D + "#" + name)
        f.put("instanceRedirects", KNOT + "." + shape, D + "#" + name)
        for home in (KNOT, FRAME, PAINT):
            f.put("superRedirects", home + "." + shape, D + "#super" + name[0].upper() + name[1:])
        f.w["adapterHooks"].append({
            "under": HE, "adapter": D + "$" + adapter, "requires": [shape], "synth": [],
            "basis": "1.20.6's " + name + "() of a hanging entity, in pixels, answers through a 1.20.6 class's own (CbEntityDecorations)"})
    # getPos() and the pos field moved up to BlockAttachedEntity
    for owner in with_subclasses(v, HE):
        f.put("methodRenames", owner + ".method_6896()" + BP, "method_59940")
        f.put("fieldRenames", owner + ".field_7100:" + BP, "field_51589")
    f.w["overrideRenames"].append({
        "old": "method_6896()" + BP, "new": "method_59940", "onlyUnder": HE, "superCalls": "rename",
        "basis": "HangingEntity.getPos() is 1.21's BlockAttachedEntity.getPos(): the same position field"})
    # the knot: no hanging entity in 1.21
    for shape, name, adapter in (("method_6892(" + DIR + ")V", "setDirection", "LegacyKnotDirection"),
                                 ("method_6894()V", "playPlacementSound", "LegacyKnotPlacementSound")):
        f.put("instanceRedirects", KNOT + "." + shape, D + "#" + name)
        f.put("superRedirects", KNOT + "." + shape, D + "#super" + name[0].upper() + name[1:])
        f.w["adapterHooks"].append({
            "under": KNOT, "adapter": D + "$" + adapter, "requires": [shape], "synth": [],
            "basis": "1.20.6's LeashFenceKnotEntity." + name + " (a hanging entity's then) answers through a 1.20.6 subclass's own"})
    # item frames
    f.put("instanceRedirects", FRAME + ".method_43272()" + L("class_9209"), D + "#getFramedMapId")
    f.put("frameDefaults", "method_43272()" + L("class_9209"), {"0": D + "#framedItem(" + L("class_1297") + ")" + L("class_1799")})
    # paintings and their variants
    HOLDER, TAG = L("class_6880"), L("class_2487")
    f.put("staticRedirects", PAINT + ".method_48928(" + TAG + HOLDER + ")V", PA + "#storeVariant")
    f.put("ctorRedirects", PV + ".<init>(II)V", PA + "#newVariant")
    f.put("instanceRedirects", PV + ".method_6945()I", PA + "#getWidth")
    f.put("instanceRedirects", PV + ".method_6943()I", PA + "#getHeight")
    f.put("staticRedirects", MC + "class_7408.method_43406(" + L("class_2378") + ")" + L("class_1535"), PA + "#bootstrap")
    f.put("fieldGetters", MC + "class_7923.field_41182:" + L("class_7922"), PA)
    f.put("ledger", PV, "not bridgeable: a 1.20.6 subclass of PaintingVariant. 1.21's PaintingVariant is a final record "
          "(width, height, assetId) that no class can extend, and a stand-in superclass would split the type: the mod's own "
          "code hands its instance on where a PaintingVariant is expected, which the verifier rejects, and 1.21's registry "
          "and painting take only the record. 1.20.6's variant objects themselves, new PaintingVariant(width, height), are "
          "bridged as records (CbEntityPaintings)")


# ------------------------------------------------------------------------------------------------ blocks and block entities
def blocks(f, vc):
    B = RT + "/CbWorldBlocks"
    ST, LV, BP, ENT, LE, PLY, BS = L("class_2680"), L("class_1937"), L("class_2338"), L("class_1297"), L("class_1309"), L("class_1657"), L("class_1799")
    f.put("fieldRedirects", MC + "class_1937$class_7867.field_47332:" + L("class_1937$class_7867"), MC + "class_1937$class_7867#field_51779")
    # button press / lever pull: 1.21 also clicks and emits the event 1.20.6's use did after them
    for owner, shape, new, name, adapter, ret in (
            ("class_2269", "method_21845(" + ST + LV + BP + ")V", "method_21845(" + ST + LV + BP + PLY + ")V", "press", "LegacyPress", "V"),
            ("class_2401", "method_21846(" + ST + LV + BP + ")" + ST, "method_21846(" + ST + LV + BP + PLY + ")V", "pull", "LegacyPull", "V")):
        assert declarers(vc, shape) == [MC + owner], (shape, declarers(vc, shape))
        f.put("instanceRedirects", MC + owner + "." + shape, B + "#" + name)
        f.put("superRedirects", MC + owner + "." + shape, B + "#super" + name[0].upper() + name[1:])
        f.hook(shape, new, owner,
               "1.21 asks " + name + "(state, level, pos, player) where a player used the block: the 1.20.6 override "
               + name + "es; its super call is 1.21's with that player (click and event, which 1.20.6's use added "
               "afterwards), 1.20.6's body outside such a call",
               bridge=B + "." + name + "(L" + B + "$" + adapter + ";" + ST + LV + BP + PLY + ")" + ret, adapter=B + "$" + adapter, guard=G)
    f.put("staticRedirects", MC + "class_3715.method_17472(" + ENT + LV + BP + ST + BS + ")Z", B + "#tryPlaceBook")
    f.put("staticRedirects", MC + "class_8886.method_54475(" + LV + L("class_8566") + ")Ljava/util/Optional;", B + "#getPotentialResults")
    f.put("instanceRedirects", MC + "class_3924.method_17503(" + ENT + BS + "I)Z", B + "#placeFood")
    # beacon beam colour: float[] rgb in 1.20.6, an ARGB int in 1.21
    SEC = MC + "class_2580$class_2581"
    f.put("ctorRedirects", SEC + ".<init>([F)V", B + "#newBeamSection")
    f.ctor(SEC + ".<init>([F)V", "(I)V", ["conv:0 " + B + "#beamColor([F)I"],
           "1.20.6's beam colour, float red, green, blue from 0 to 1, as 1.21's opaque ARGB int")
    f.reshape(SEC + ".method_10944()[F", "()I", [], "the beam colour as 1.20.6's float[] {r, g, b}", ret="conv " + B + "#beamColor(I)[F")
    f.hook("method_10944()[F", "method_10944()I", "class_2580$class_2581",
           "1.21 asks getColor() for an ARGB int: a 1.20.6 override's float[] as one", unwrap=B + ".beamColor([F)I")
    # dispenser: addItem(stack) took the whole stack into the first empty slot; 1.21's insertItem merges
    ai = "method_11075(" + BS + ")I"
    assert declarers(vc, ai) == [MC + "class_2601"]
    f.put("instanceRedirects", MC + "class_2601." + ai, B + "#addItem")
    f.put("superRedirects", MC + "class_2601." + ai, B + "#superAddItem")
    f.hook(ai, "method_11075(" + BS + ")" + BS, "class_2601",
           "1.21 asks insertItem(stack) for what is left: a 1.20.6 addItem override's slot means the whole stack went in",
           bridge=B + ".insertItem(L" + B + "$LegacyAddItem;" + BS + ")" + BS, adapter=B + "$LegacyAddItem", guard=G)
    # jukebox: 1.21 plays through its song player
    JB = MC + "class_2619"
    f.put("instanceRedirects", JB + ".method_44373()Z", B + "#isRecordPlaying")
    f.put("superRedirects", JB + ".method_44373()Z", B + "#superIsRecordPlaying")
    f.w["adapterHooks"].append({
        "under": JB, "adapter": B + "$LegacyRecordPlaying", "requires": ["method_44373()Z"], "synth": [],
        "basis": "a 1.20.6 caller's isRecordPlaying() answers through a 1.20.6 jukebox subclass's own override"})
    f.put("methodRenames", JB + ".method_49212()V", "method_60786")
    f.w["overrideRenames"].append({
        "old": "method_49212()V", "new": "method_60786", "onlyUnder": JB, "superCalls": "rename",
        "basis": "JukeboxBlockEntity.startPlaying() is 1.21's tryForcePlaySong(): play the disc in the jukebox (1.21's song player)"})
    # decorated pot patterns: keys of a registry of names in 1.20.6, of pattern records in 1.21
    DP = MC + "class_8173"
    f.put("fieldGetters", DP + ".field_42787:" + L("class_5321"), B)
    f.put("staticRedirects", DP + ".method_49205(" + L("class_5321") + ")" + L("class_2960"), B + "#location")
    f.put("staticRedirects", DP + ".method_49207(" + L("class_2378") + ")Ljava/lang/String;", B + "#bootstrap")
    f.put("genericWitnesses", DP + ".method_49206(" + L("class_1792") + ")" + L("class_5321"), {
        "by": [DP + ".method_49205(" + L("class_5321") + ")" + L("class_2960"),
               MC + "class_4722.method_49341(" + L("class_5321") + ")" + L("class_4730")],
        "basis": "phantom key: the key passes unchanged; 1.20.6 code takes the pattern out only through "
                 "DecoratedPotPatterns.location(key), which CbWorldBlocks answers for 1.21's keys by their pattern's asset, "
                 "and Sheets.getDecoratedPotMaterial(key) (s6client)"})
    # 1.20.6's UpdateOneTwentyOneBannerPatterns: flow and guster, vanilla in 1.21
    f.put("classRenames", MC + "class_9367", FAC + "/class_9367")


def main():
    v, w = load(sys.argv[1]), load(sys.argv[2])
    vc = load(sys.argv[3], code=True)
    f = Fragment()
    attributes(f)
    sounds_and_links(f, v, w)
    server(f, v)
    constants(f)
    resources_and_commands(f)
    portals(f, v)
    living(f, v, vc)
    leash(f, v, vc)
    horses(f, vc)
    enchanting(f, vc)
    projectiles(f, vc)
    decorations(f, v, vc)
    blocks(f, vc)
    f.write()


if __name__ == "__main__":
    main()
