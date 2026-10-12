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
    f.put("staticRedirects", MC + "class_155.method_43250()V", K + "#enableDataFixerOptimizations")
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


def main():
    v, w = load(sys.argv[1]), load(sys.argv[2])
    f = Fragment()
    attributes(f)
    sounds_and_links(f, v, w)
    server(f, v)
    constants(f)
    resources_and_commands(f)
    portals(f, v)
    f.write()


if __name__ == "__main__":
    main()
