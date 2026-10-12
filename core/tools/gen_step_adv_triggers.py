"""Generate the step-1.20.3 layer's bridges for vanilla's criterion triggers (area s2adv).

1.20.3 moved every vanilla trigger to codecs: the trigger's createInstance(json, player, context)
(createInstance(json, context) on ImpossibleTrigger) became codec(), its TriggerInstance (an
AbstractCriterionTriggerInstance subclass with serializeToJson()) became a record with a static
CODEC. For each trigger the damage table lists (core/segments/damage/1.20.2__1.20.3.damage.tsv, the
rows naming its createInstance), this writes, from both releases' bytecode:

  rt/CbAdvLegacyTriggers.java  one adapter interface per trigger: the 1.20.2 createInstance shape a
                               mod subclass overriding it still declares
  rt/CbAdvTriggers.java        per trigger:
                                 create_<T>   instanceRedirect of createInstance: a subclass's own
                                              override (through the adapter), else vanilla's
                                 vanilla_<T>  superRedirect of createInstance: what vanilla's 1.20.2
                                              createInstance returned -- the instance decoded from
                                              the JSON by 1.20.3's codec, with the player predicate
                                              the caller passed
                                 codec_<T>    codec() synthesized into a subclass that overrides
                                              createInstance (adapterHooks): decodes through that
                                              override
                               per instance:
                                 json_<I>     superRedirect of serializeToJson(): the instance in
                                              1.20.2's JSON form, written by its codec
                                 vanillaJson  the same for any vanilla instance (the dispatch
                                              CbAdvCriteria.serializeToJson uses)
  ...json.d/s2adv.json         the instanceRedirects / superRedirects / adapterHooks entries for
                               them (entries this generator owns are replaced, the rest kept)

It also checks the claim the vanilla_<T> bridges rest on -- that 1.20.3's codec reads the JSON keys
1.20.2's createInstance read -- by comparing the string constants of the two, and prints every
difference.

  python gen_step_adv_triggers.py <worktree root> <1.20.2 code jar> <1.20.3 code jar>
"""
import json
import os
import re
import subprocess
import sys

PKG = "top/dext/centurybridge/step/v1_20_3"
RT = PKG + "/rt"
FAC_CTX = PKG + "/facade/DeserializationContext"
GEN_TRIGGERS = RT + "/CbAdvTriggers"
GEN_LEGACY = RT + "/CbAdvLegacyTriggers"


def javap(jar, cls, code=False):
    args = ["javap", "-p"] + (["-c"] if code else []) + ["-cp", jar, cls.replace("/", ".")]
    return subprocess.run(args, capture_output=True, text=True, errors="replace").stdout


def strings(listing):
    return set(re.findall(r"// String (\S+)", listing))


def nested(jar, cls):
    """the classes nested in cls (cls$...), from the jar's entries"""
    import zipfile
    with zipfile.ZipFile(jar) as z:
        return sorted(n[:-6] for n in z.namelist() if n.startswith(cls + "$") and n.endswith(".class"))


def parse_class(listing):
    """fields [(access, type, name)], methods [(access, ret, name, params)] of a javap -p listing"""
    fields, methods = [], []
    for line in listing.splitlines():
        line = line.strip()
        m = re.match(r"((?:public|protected|private|static|final|abstract|synchronized|native|transient|volatile|default| )*)"
                     r"(?:<[^>]*>\s+)?([\w$.<>\[\], ?]+?)\s+([\w$]+)\((.*)\)(?: throws [\w$., ]+)?;$", line)
        if m:
            methods.append((m.group(1).split(), m.group(2), m.group(3), m.group(4)))
            continue
        m = re.match(r"((?:public|protected|private|static|final|transient|volatile| )*)([\w$.<>\[\], ?]+?)\s+([\w$]+);$", line)
        if m:
            fields.append((m.group(1).split(), m.group(2), m.group(3)))
        m = re.match(r".*(?:class|interface) ([\w$.]+)", line)
    return fields, methods


def jtype(t):
    """javap's dotted type -> Java source type (nested classes with '.')"""
    return t.replace("$", ".")


def simple(moj):
    return moj.rsplit("/", 1)[-1].replace("$", "_")


def load_classes(path):
    cm = {}
    for line in open(path, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "C":
            cm[f[1]] = f[2]
    return cm


def main():
    root, jar_v, jar_w = sys.argv[1], sys.argv[2], sys.argv[3]
    data = os.environ.get("CB_DATA", os.path.join(root, "data"))
    cm = load_classes(os.path.join(data, "mappings", "inter-to-mojang-classes.tsv"))

    def moj(c):
        if c in cm:
            return cm[c]
        d = c.rfind("$")
        return moj(c[:d]) + c[d:] if d > 0 else c

    dmg = os.path.join(root, "core/segments/damage/1.20.2__1.20.3.damage.tsv")
    triggers = {}   # T -> (name, desc, instance)
    instances = set()
    for i, line in enumerate(open(dmg, encoding="utf-8")):
        if i == 0:
            continue
        usage, owner, name, desc, kinds, w = line.rstrip("\n").split("\t")
        m = re.match(r"\(Lcom/google/gson/JsonObject;(Ljava/util/Optional;)?Lnet/minecraft/class_5257;\)L(net/minecraft/class_\d+\$class_\d+);$", desc)
        if m and name.startswith("method_") and m.group(2).startswith(owner + "$") \
                and moj(m.group(2)).endswith("$TriggerInstance"):
            triggers[owner] = (name, desc, m.group(2), m.group(1) is not None)
            instances.add(m.group(2))
        if name == "method_807" and desc == "()Lcom/google/gson/JsonObject;" and "$" in owner \
                and moj(owner).endswith("$TriggerInstance"):
            instances.add(owner)

    report = []
    legacy_src, trig_src, json_cases = [], [], []
    wiring = {"instanceRedirects": {}, "superRedirects": {}, "adapterHooks": []}
    for t in sorted(triggers, key=lambda c: moj(c)):
        name, desc, inst, simple_shape = triggers[t]
        tn = simple(moj(t))
        t_v = javap(jar_v, t, True)
        i_w = javap(jar_w, inst, True)
        t_w = javap(jar_w, t, True)
        fields, methods = parse_class(i_w)
        comps = [f for f in fields if "private" in f[0] and "final" in f[0] and "static" not in f[0]]
        codec = [f for f in fields if "static" in f[0] and "public" in f[0] and "Codec" in f[1]]
        if len(codec) != 1:
            report.append(f"{moj(inst)}: no single public static codec on 1.20.3 -- skipped")
            continue
        codec = codec[0][2]
        java_t = jtype(t.replace("/", "."))
        java_i = jtype(inst.replace("/", "."))
        # 1.20.2's createInstance read these JSON keys; 1.20.3's codec must read the same ones
        keys_v = strings(t_v)
        keys_w = strings(i_w) | strings(t_w)
        for n in nested(jar_w, inst):
            keys_w |= strings(javap(jar_w, n, True))
        missing = sorted(k for k in keys_v if re.fullmatch(r"[a-z_]+", k) and k not in keys_w)
        if missing:
            report.append(f"{moj(t)}: 1.20.2 strings not in 1.20.3's codec: {missing}")
        legacy_src.append(f"""    /** {moj(t)}: its 1.20.2 createInstance */
    public interface {tn} {{
        {java_i} {name}(JsonObject json, {"Optional<net.minecraft.class_5258> player, " if simple_shape else ""}DeserializationContext context);
    }}
""")
        params = "JsonObject json, " + ("Optional<net.minecraft.class_5258> player, " if simple_shape else "") + "DeserializationContext context"
        args = "json, " + ("player, " if simple_shape else "") + "context"
        if simple_shape:
            # rebuild the record with the caller's player predicate (component 0 of every SimpleInstance record)
            if not comps or comps[0][2] != "comp_2029":
                report.append(f"{moj(inst)}: first component is not player (comp_2029) -- skipped")
                continue
            rebuilt = "new " + java_i + "(" + ", ".join(["player"] + [f"decoded.{c[2]}()" for c in comps[1:]]) + ")"
            vanilla_body = f"""        JsonObject conditions = json.deepCopy();
        conditions.remove("player");
        {java_i} decoded = CbAdvPredicates.parse({java_i}.{codec}, conditions);
        return {rebuilt};"""
        else:
            vanilla_body = f"""        return CbAdvPredicates.parse({java_i}.{codec}, json);"""
        trig_src.append(f"""    // ---------------------------------------------------------------- {moj(t)}

    /** {moj(t)}.createInstance: a subclass's own 1.20.2 override, else vanilla's (validated as 1.20.2 did) */
    public static {java_i} create_{tn}({java_t} self, {params}) {{
        {java_i} instance = self instanceof CbAdvLegacyTriggers.{tn} legacy ? legacy.{name}({args}) : vanilla_{tn}(self, {args});
        CbAdvCriteria.validateExplicit(instance, context);
        return instance;
    }}

    /** {moj(t)}.createInstance as vanilla 1.20.2 ran it (a subclass's super call) */
    public static {java_i} vanilla_{tn}({java_t} self, {params}) {{
{vanilla_body}
    }}

    /** codec() of a 1.20.2 subclass that overrides createInstance: decodes through that override */
    public static Codec<{java_i}> codec_{tn}(CbAdvLegacyTriggers.{tn} trigger) {{
        return CbAdvCriteria.legacyCodec(json -> {{
            DeserializationContext context = CbAdvContext.current();
            return trigger.{name}(json, {"CbAdvPredicates.method_51705(json, \"player\", context), " if simple_shape else ""}context);
        }});
    }}
""")
        key = f"{t}.{name}{desc}"
        wiring["instanceRedirects"][key] = f"{GEN_TRIGGERS}#create_{tn}"
        wiring["superRedirects"][key] = f"{GEN_TRIGGERS}#vanilla_{tn}"
        wiring["adapterHooks"].append({
            "under": t,
            "adapter": f"{GEN_LEGACY}${tn}",
            "requires": [name + desc],
            "synth": [{"new": "method_54937()Lcom/mojang/serialization/Codec;",
                       "call": f"{GEN_TRIGGERS}.codec_{tn}(L{GEN_LEGACY}${tn};)Lcom/mojang/serialization/Codec;"}],
        })
    for inst in sorted(instances, key=lambda c: moj(c)):
        i_w = javap(jar_w, inst)
        fields, _ = parse_class(i_w)
        codec = [f for f in fields if "static" in f[0] and "public" in f[0] and "Codec" in f[1]]
        if len(codec) != 1:
            report.append(f"{moj(inst)}: no single public static codec on 1.20.3 -- no JSON bridge")
            continue
        java_i = jtype(inst.replace("/", "."))
        iname = simple(moj(inst))
        json_cases.append((java_i, codec[0][2], iname))
        trig_src.append(f"""    /** {moj(inst)}.serializeToJson() as vanilla 1.20.2 wrote it (a subclass's super call) */
    public static JsonObject json_{iname}({java_i} self) {{
        return CbAdvPredicates.encode({java_i}.{codec[0][2]}, self).getAsJsonObject();
    }}
""")
        wiring["superRedirects"][f"{inst}.method_807()Lcom/google/gson/JsonObject;"] = f"{GEN_TRIGGERS}#json_{iname}"

    header = "// generated by core/tools/gen_step_adv_triggers.py -- do not edit\n"
    legacy = header + f"""package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.gson.JsonObject;
import java.util.Optional;
import top.dext.centurybridge.step.v1_20_3.facade.DeserializationContext;

/**
 * The 1.20.2 createInstance of each vanilla trigger, as adapter interfaces: a mod subclass that
 * overrides it gets the interface (adapterHooks), and codec() -- synthesized into it -- decodes
 * through that override.
 */
public final class CbAdvLegacyTriggers {{

{chr(10).join(legacy_src)}
    private CbAdvLegacyTriggers() {{
    }}
}}
"""
    cases = "\n".join(f"        if (instance instanceof {c[0]} i) {{\n            return CbAdvPredicates.encode({c[0]}.{c[1]}, i);\n        }}"
                      for c in json_cases)
    triggers_java = header + f"""package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import java.util.Optional;
import top.dext.centurybridge.step.v1_20_3.facade.DeserializationContext;

/**
 * Vanilla's criterion triggers with their 1.20.2 createInstance / serializeToJson, on 1.20.3's
 * codecs (see core/tools/gen_step_adv_triggers.py for what each bridge is wired to).
 *
 * vanilla_* stands for vanilla's 1.20.2 createInstance(json, player, context): 1.20.3's codec
 * reads the conditions JSON under the same keys (the generator checks each trigger's), the player
 * predicate is the one the caller passed (1.20.2 never read "player" there), and the context's
 * validation is left to whoever asked: the advancement loader validates every criterion it
 * decoded, an explicit 1.20.2 entry point (create_*, CbAdvCriteria) validates what it returns.
 */
public final class CbAdvTriggers {{

{chr(10).join(trig_src)}
    /** a vanilla 1.20.3 trigger instance in its JSON form (its codec's), or null for one that is not vanilla's */
    static JsonElement vanillaJson(net.minecraft.class_184 instance) {{
{cases}
        return null;
    }}

    private CbAdvTriggers() {{
    }}
}}
"""
    src = os.path.join(root, "core/shims/steps/1.20.3/src", RT.replace("/", os.sep))
    open(os.path.join(src, "CbAdvLegacyTriggers.java"), "w", encoding="utf-8", newline="\n").write(legacy)
    open(os.path.join(src, "CbAdvTriggers.java"), "w", encoding="utf-8", newline="\n").write(triggers_java)

    frag = os.path.join(root, "core/segments/steps/1.20.2__1.20.3.json.d/s2adv.json")
    table = json.load(open(frag, encoding="utf-8")) if os.path.exists(frag) else {}
    owned = (GEN_TRIGGERS, GEN_LEGACY)
    for sec in ("instanceRedirects", "superRedirects"):
        cur = {k: v for k, v in table.get(sec, {}).items() if not v.startswith(owned)}
        cur.update(wiring[sec])
        table[sec] = dict(sorted(cur.items()))
    hooks = [h for h in table.get("adapterHooks", []) if not h["adapter"].startswith(owned)]
    table["adapterHooks"] = hooks + wiring["adapterHooks"]
    with open(frag, "w", encoding="utf-8", newline="\n") as o:
        json.dump(table, o, indent=1)
        o.write("\n")
    print(f"{len(triggers)} triggers, {len(json_cases)} instances")
    for r in report:
        print("  " + r)


if __name__ == "__main__":
    main()
