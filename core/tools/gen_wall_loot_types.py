"""The loot "type" records 26.1 replaced with their MapCodecs: one facade per type, generated.

1.21.11 wrapped each loot codec in a record registered in its registry (LootItemFunctionType(MapCodec),
LootItemConditionType, LootPoolEntryType, LootNumberProviderType, LootNbtProviderType,
LootScoreProviderType); a function / condition / entry / provider answered its type from getType(), and
the constants classes (LootItemFunctions.SET_COUNT, ...) held the registered records. 26.1 registers the
MapCodec itself and asks codec() instead. Per type this writes:

  facade      step/v26_1/facade/<Type>: the record's surface (constructor, the codec accessor under its
              intermediary name, record equals / hashCode / toString), canonical per codec: the first
              instance made for a codec (a mod's own `new Type(CODEC)` at class init, or the one built
              for a 26.1 codec) is the one getType() and the constants answer, so `==` keeps working;
  getType()   calls answered with the facade of the object's codec(); a mod's override reconnected to
              codec() (overrideHooks, the facade's codec as the answer);
  constants   fieldGetters: the facade of the codec 26.1 registers under the same id (the id read from
              1.21.11's registration in the constants class, checked against 26.1's bootstrap);
  codecs      1.21.11's per-class codec constants 26.1 renamed (CopyBlockState.CODEC is MAP_CODEC): the
              codec 1.21.11 registered under an id and the one 26.1 registers under the same id are the
              same class's codec; a 1.21.11 field 26.1 does not declare reads 26.1's (fieldGetters);
  register    Registry.register(registry, id, facade) registers the codec (CbWallRegistry): a mod
              registering its type the 1.21.11 way puts the codec 26.1's registry holds.

  python gen_wall_loot_types.py <V code jar> <W jar> <fragment.json> <src root> <report.tsv> <wall table>

Regenerating (from the repo root):
  python3 core/tools/gen_wall_loot_types.py data/jars/client-1.21.11-intermediary-code.jar data/jars/client-26.1.jar \
      core/segments/steps/1.21.11__26.1.json.d/swall-loottypes-gen.json core/shims/steps/26.1/src <tmp>/loot-report.tsv       data/mappings/inter-to-mojang-1.21.11-26.1.tsv
"""
import json
import os
import re
import subprocess
import sys

PKG = "top/dext/centurybridge/step/v26_1"
GUARD = PKG + "/rt/Guard"
HELPER = PKG + "/rt/CbWallLootTypes"

# (V record, its codec accessor, V value interface, getType, V constants class, W registry field,
#  W value interface (mojang), W constants class (mojang), facade simple name, helper method stem)
TYPES = [
    ("net/minecraft/class_5339", "comp_1858", "net/minecraft/class_117", "method_29321", "net/minecraft/class_131",
     "LOOT_FUNCTION_TYPE", "net/minecraft/world/level/storage/loot/functions/LootItemFunction",
     "net/minecraft/world/level/storage/loot/functions/LootItemFunctions", "LootItemFunctionType", "function"),
    ("net/minecraft/class_5342", "comp_1878", "net/minecraft/class_5341", "method_29325", "net/minecraft/class_217",
     "LOOT_CONDITION_TYPE", "net/minecraft/world/level/storage/loot/predicates/LootItemCondition",
     "net/minecraft/world/level/storage/loot/predicates/LootItemConditions", "LootItemConditionType", "condition"),
    ("net/minecraft/class_5338", "comp_1847", "net/minecraft/class_79", "method_29318", "net/minecraft/class_75",
     "LOOT_POOL_ENTRY_TYPE", "net/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer",
     "net/minecraft/world/level/storage/loot/entries/LootPoolEntries", "LootPoolEntryType", "entry"),
    ("net/minecraft/class_5657", "comp_1896", "net/minecraft/class_5658", "method_365", "net/minecraft/class_5659",
     "LOOT_NUMBER_PROVIDER_TYPE", "net/minecraft/world/level/storage/loot/providers/number/NumberProvider",
     "net/minecraft/world/level/storage/loot/providers/number/NumberProviders", "LootNumberProviderType", "numberProvider"),
    ("net/minecraft/class_5650", "comp_1891", "net/minecraft/class_5651", "method_32439", "net/minecraft/class_5652",
     "LOOT_NBT_PROVIDER_TYPE", "net/minecraft/world/level/storage/loot/providers/nbt/NbtProvider",
     "net/minecraft/world/level/storage/loot/providers/nbt/NbtProviders", "LootNbtProviderType", "nbtProvider"),
    ("net/minecraft/class_5669", "comp_1904", "net/minecraft/class_5670", "method_32475", "net/minecraft/class_5671",
     "LOOT_SCORE_PROVIDER_TYPE", "net/minecraft/world/level/storage/loot/providers/score/ScoreboardNameProvider",
     "net/minecraft/world/level/storage/loot/providers/score/ScoreboardNameProviders", "LootScoreProviderType", "scoreProvider"),
]

LDC = re.compile(r"ldc(?:_w)?\s+#\d+\s+// String (\S+)")
PUT = re.compile(r"putstatic\s+#\d+\s+// Field (field_\d+):L([\w/$]+);")
LDC_ANY = re.compile(r"// String (\S+)")
GET = re.compile(r"getstatic\s+#\d+\s+// Field ([\w/$]+)\.(\w+):Lcom/mojang/serialization/MapCodec;")


def codecs_by_id(text):
    """id -> (owner, field) of the MapCodec registered under it (an id string followed by a codec read)"""
    out, last = {}, None
    for line in text.splitlines():
        m = LDC.search(line)
        if m:
            last = m.group(1)
            continue
        m = GET.search(line)
        if m and last:
            out.setdefault(last, (m.group(1), m.group(2)))
            last = None
    return out


def javap(jar, cls):
    return subprocess.run(["javap", "-c", "-p", "-cp", jar, cls.replace("/", ".")],
                          capture_output=True, text=True, errors="replace").stdout


def clinit(text):
    m = text.find("static {};")
    return text[m:] if m >= 0 else ""


def v_constants(vjar, consts, vtype):
    """field -> id of every constant of the type the 1.21.11 constants class registers"""
    out, last = {}, None
    for line in clinit(javap(vjar, consts)).splitlines():
        m = LDC.search(line)
        if m:
            last = m.group(1)
            continue
        m = PUT.search(line)
        if m:
            if m.group(2) == vtype and last:
                out[m.group(1)] = last
            last = None
    return out


def w_ids(wjar, wconsts):
    """every id string 26.1's constants class names in its code (its bootstrap registers them)"""
    return set(LDC_ANY.findall(javap(wjar, wconsts)))


WALL_F = {}
_DECL = {}


def wf_declared(wjar, owner, name):
    if owner not in _DECL:
        _DECL[owner] = set(re.findall(r" (\w+);$", subprocess.run(["javap", "-p", "-cp", wjar, owner.replace("/", ".")],
                           capture_output=True, text=True, errors="replace").stdout, re.M))
    return name in _DECL[owner]


def dot(c):
    return c.replace("/", ".").replace("$", ".")


def facade_source(t):
    vrec, comp, viface, gettype, consts, reg, wiface, wconsts, simple, stem = t
    return f"""package {dot(PKG)}.facade;

import com.mojang.serialization.MapCodec;
import java.util.IdentityHashMap;
import java.util.Map;
import {dot(PKG)}.rt.CbWallRegistry;

/**
 * Generated by core/tools/gen_wall_loot_types.py -- do not edit; regenerate with the command in its header.
 *
 * 1.21.11's {simple} ({vrec}): a record around the MapCodec 26.1 registers directly. Canonical per codec:
 * the first instance made for a codec is the one {{@link #cb$of}} answers (getType() and the constants), so
 * identity comparisons between types keep holding. Member names are 1.21.11's intermediary names.
 */
public class {simple}<T extends {dot(wiface)}> implements CbWallRegistry.Wrapper {{

    private static final Map<MapCodec<?>, {simple}<?>> CB$CANON = new IdentityHashMap<>();

    private final MapCodec<T> cb$codec;

    /** 1.21.11 {simple}(MapCodec) */
    public {simple}(MapCodec<T> codec) {{
        this.cb$codec = codec;
        synchronized (CB$CANON) {{
            CB$CANON.putIfAbsent(codec, this);
        }}
    }}

    /** the type of a codec: the first instance made for it */
    @SuppressWarnings({{"unchecked", "rawtypes"}})
    public static {simple}<?> cb$of(MapCodec<?> codec) {{
        if (codec == null) {{
            return null;
        }}
        synchronized (CB$CANON) {{
            {simple}<?> t = CB$CANON.get(codec);
            return t != null ? t : new {simple}(codec);
        }}
    }}

    /** codec() */
    public MapCodec<T> {comp}() {{
        return this.cb$codec;
    }}

    @Override
    public Object cb$unwrap() {{
        return this.cb$codec;
    }}

    @Override
    public boolean equals(Object o) {{
        return this == o || o instanceof {simple}<?> t && this.cb$codec.equals(t.cb$codec);
    }}

    @Override
    public int hashCode() {{
        return this.cb$codec.hashCode();
    }}

    @Override
    public String toString() {{
        return "{simple}[codec=" + this.cb$codec + "]";
    }}
}}
"""


def main():
    vjar, wjar, frag, src, report, table = sys.argv[1:7]
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "F":
            o = f[2].rsplit(".", 1)
            WALL_F[f[1]] = (o[0], o[1])
    fragment = {"classRenames": {}, "instanceRedirects": {}, "overrideHooks": [], "fieldGetters": {}, "facadeGaps": {}}
    helper = [f"package {dot(PKG)}.rt;", "",
              "import com.mojang.serialization.MapCodec;",
              "import net.minecraft.core.registries.BuiltInRegistries;",
              "import net.minecraft.resources.Identifier;", "",
              "/**",
              " * Generated by core/tools/gen_wall_loot_types.py -- do not edit; regenerate with the command in its header.",
              " *",
              " * getType() of 1.21.11's loot functions, conditions, entries and providers on 26.1's codec(), the",
              " * facade's codec back for a mod's getType() override, and 1.21.11's registered type constants.",
              " */",
              "@SuppressWarnings({\"rawtypes\", \"unused\"})",
              "public final class CbWallLootTypes {", "",
              "    private CbWallLootTypes() {", "    }"]
    rep = []
    for t in TYPES:
        vrec, comp, viface, gettype, consts, reg, wiface, wconsts, simple, stem = t
        fac = f"{PKG}/facade/{simple}"
        path = os.path.join(src, fac + ".java")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, "w", encoding="utf-8", newline="\n") as fh:
            fh.write(facade_source(t))
        fragment["classRenames"][vrec] = fac
        older = ("a member of the class before it became the 1.21.11 record (1.20.x: a Serializer / Codec wrapper, a"
                 " non-final class): damage of the step that changed it, not part of the 1.21.11 API this layer gives back")
        for k in (f"{vrec}.<init>(Lcom/mojang/serialization/Codec;)V", f"{vrec}.{comp}()Lcom/mojang/serialization/Codec;",
                  f"{vrec}.<init>(Lnet/minecraft/class_5335;)V", f"{vrec}.{comp}:Lcom/mojang/serialization/Codec;"):
            fragment["facadeGaps"][k] = older
        fragment["facadeGaps"][f"{vrec}.{comp}:Lcom/mojang/serialization/MapCodec;"] = (
            "the 1.21.11 record's private component field: the facade keeps the codec as cb$codec, read through the"
            " accessor; only an access widener on the dead class ever reached the field")
        fragment["instanceRedirects"][f"{viface}.{gettype}()L{vrec};"] = f"{HELPER}#{stem}Type"
        fragment["overrideHooks"].append({
            "old": f"{gettype}()L{vrec};",
            "new": "codec()Lcom/mojang/serialization/MapCodec;",
            "unwrap": f"{HELPER}.{stem}Codec(L{vrec};)Lcom/mojang/serialization/MapCodec;",
            "guard": GUARD,
            "onlyUnder": viface,
            "basis": f"1.21.11 getType() answered the {simple} 26.1 replaced with its codec: codec() answers the codec of the facade the mod's getType() returns"})
        helper += ["",
                   f"    /** 1.21.11 {dot(wiface).rsplit('.', 1)[1]}.getType() */",
                   f"    public static {dot(fac)} {stem}Type({dot(wiface)} value) {{",
                   f"        return ({dot(fac)}) {dot(fac)}.cb$of(value.codec());",
                   "    }", "",
                   f"    /** codec() of a 1.21.11 override of getType(): its type's codec */",
                   f"    public static MapCodec {stem}Codec({dot(fac)} type) {{",
                   f"        return type == null ? null : type.{comp}();",
                   "    }"]
        have = w_ids(wjar, wconsts)
        wcodecs = codecs_by_id(javap(wjar, wconsts))
        for ident, (vo, vf) in sorted(codecs_by_id(clinit(javap(vjar, consts))).items()):
            if ident not in wcodecs:
                continue
            wo, wf = wcodecs[ident]
            wtable = WALL_F.get(f"{vo}.{vf}")
            if wtable and wtable[0] == wo and wtable[1] == wf:
                continue  # the wall table already renames it to 26.1's field
            if f"{vo}.{vf}" in WALL_F and wf_declared(wjar, wo, WALL_F[f"{vo}.{vf}"][1]):
                continue  # 26.1 declares the table's name: not damage
            fragment["fieldGetters"][f"{vo}.{vf}:Lcom/mojang/serialization/MapCodec;"] = HELPER
            helper += ["",
                       f"    /** {vo}.{vf}: the codec registered as \"{ident}\", 26.1's {dot(wo)}.{wf} */",
                       f"    public static MapCodec {vf}() {{",
                       # through the registry: the same object, and readable where 26.1 made the field package-private
                       f"        return BuiltInRegistries.{reg}.getValue(Identifier.withDefaultNamespace(\"{ident}\"));",
                       "    }"]
        for field, ident in sorted(v_constants(vjar, consts, vrec).items()):
            if ident not in have:
                rep.append((consts, field, ident, f"26.1 {wconsts} registers no '{ident}'"))
                continue
            fragment["fieldGetters"][f"{consts}.{field}:L{vrec};"] = HELPER
            helper += ["",
                       f"    /** {consts}.{field}: the {simple} registered as \"{ident}\" */",
                       f"    public static {dot(fac)} {field}() {{",
                       f"        return ({dot(fac)}) {dot(fac)}.cb$of(BuiltInRegistries.{reg}.getValue(Identifier.withDefaultNamespace(\"{ident}\")));",
                       "    }"]
    helper += ["}", ""]
    path = os.path.join(src, HELPER + ".java")
    with open(path, "w", encoding="utf-8", newline="\n") as fh:
        fh.write("\n".join(helper))
    with open(frag, "w", encoding="utf-8", newline="\n") as fh:
        json.dump(fragment, fh, indent=1)
        fh.write("\n")
    with open(report, "w", encoding="utf-8", newline="\n") as fh:
        for row in rep:
            fh.write("\t".join(row) + "\n")
    print(f"{len(TYPES)} facades, {len(fragment['fieldGetters'])} constants -> {frag}; {len(rep)} left (report {report})")


if __name__ == "__main__":
    main()
