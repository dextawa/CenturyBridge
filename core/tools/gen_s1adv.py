"""Generated wiring for climb step 1.20.1 -> 1.20.2, area s1adv (advancements, criteria, triggers).

Rows of the step's damage table that share one change get one rule here instead of a hand-written
entry each. Every rule is read off both releases' class files:

  triggers   every vanilla criterion trigger (a class implementing CriterionTrigger, directly or as
             a SimpleCriterionTrigger)
     getId        1.20.1's getId() answered the class's ID constant (the classes registered once) or
                  its constructor argument (the five registered under several ids). A subclass's
                  super.getId() (superRedirects) answers the same: the constant 1.20.1 returned --
                  its literal read off the class's 1.20.1 static initializer -- or the id the
                  instance was built with (CbAdvTriggers.storedId). rt/CbAdvTriggerIds.java holds one
                  receiver-typed static per class.
     createInstance  (json, ContextAwarePredicate player, context) became (json, Optional player,
                  context): calls convert the player (ANY is absent: callReshapes), and a 1.20.1
                  subclass's override is reached from the new shape with absent as ANY (overrideHooks).
     <init>(id)   the five multi-registered triggers lost their id argument: the reshaped call drops it
                  into CbAdvTriggers.stash, SimpleCriterionTrigger's constructor claims it.
  instances  every vanilla trigger instance (an AbstractCriterionTriggerInstance subclass)
     <init>       positional, each argument converted to what 1.20.2 takes: a predicate that may be
                  ANY to an Optional (ANY is absent: CbAdvAny), an array to a List, a leading trigger
                  id dropped into CbAdvInstances.stash (the instance claims it). The 1.20.2 constructor
                  signature's type arguments say which Optional each one is.
     factories    1.20.1's static factories answered the instance; 1.20.2's answer a Criterion and take
                  other arguments. Each is ported as written in 1.20.1 (rt/CbAdvFactories.java, by
                  hand); this emits the staticRedirects for every one the damage table lists and checks
                  CbAdvFactories declares it.

  records    the predicates 1.20.2 turned into records (records()): 1.20.1 constructors reshaped onto the
             record's, part by part (PART_CONVERSIONS / PART_DEFAULTS).
  holders    members whose only change is Advancement -> AdvancementNode / AdvancementHolder (holders()):
             calls converted both ways, overrides hooked, constructors reshaped (CbAdvNodes).
  builders   the predicate builders' 1.20.1 setters, put back as covers (builders(): generated CbAdvBuilder*
             mixins; "covers" entries of those builders are this generator's).

A row no rule covers is left to the hand-written entries of the fragment. Generated entries carry
"basis": "gen_s1adv: ..." (objects) or point at CbAdvTriggerIds / CbAdvFactories (string maps); the
generator replaces exactly those and keeps the rest of the fragment.

  python core/tools/gen_s1adv.py <worktree root> <1.20.1 code jar> <1.20.2 code jar> <inter-to-mojang-classes.tsv>
"""
import glob
import json
import os
import re
import subprocess
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

RT = "top/dext/centurybridge/rt/v1_20_2"
TRIGGER_IDS = RT + "/CbAdvTriggerIds"
FACTORIES = RT + "/CbAdvFactories"
ANY = RT + "/CbAdvAny"
BASIS = "gen_s1adv"

CRITERION_TRIGGER = "net/minecraft/class_179"
SIMPLE_TRIGGER = "net/minecraft/class_4558"
ABSTRACT_INSTANCE = "net/minecraft/class_195"
RESOURCE_LOCATION = "net/minecraft/class_2960"
CONTEXT = "net/minecraft/class_5258"
JSON = "com/google/gson/JsonObject"
DESER = "net/minecraft/class_5257"
OPTIONAL = "java/util/Optional"
LIST = "java/util/List"

# 1.20.1 argument type -> (the 1.20.2 parameter it becomes, converter)
CONVERSIONS = {
    ("Lnet/minecraft/class_5258;", "Optional<net/minecraft/class_5258>"): "context",
    ("Lnet/minecraft/class_2048;", "Optional<net/minecraft/class_2048>"): "entity",
    ("Lnet/minecraft/class_2073;", "Optional<net/minecraft/class_2073>"): "item",
    ("Lnet/minecraft/class_2090;", "Optional<net/minecraft/class_2090>"): "location",
    ("Lnet/minecraft/class_2019;", "Optional<net/minecraft/class_2019>"): "damage",
    ("Lnet/minecraft/class_2022;", "Optional<net/minecraft/class_2022>"): "damageSource",
    ("Lnet/minecraft/class_2025;", "Optional<net/minecraft/class_2025>"): "distance",
    ("Lnet/minecraft/class_2102;", "Optional<net/minecraft/class_2102>"): "effects",
    ("Lnet/minecraft/class_4559;", "Optional<net/minecraft/class_4559>"): "state",
    ("[Lnet/minecraft/class_5258;", "List<net/minecraft/class_5258>"): "contexts",
    ("[Lnet/minecraft/class_2073;", "List<net/minecraft/class_2073>"): "items",
}


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith("net/minecraft/"):
                c = classfile.read(z.read(n))
                if c:
                    out[c.name] = c
    return out


_dis = {}


def disassemble(jar, cls):
    if (jar, cls) not in _dis:
        _dis[(jar, cls)] = subprocess.run(["javap", "-c", "-p", "-cp", jar, cls.replace("/", ".")],
                                          capture_output=True, text=True, encoding="utf-8", errors="replace").stdout
    return _dis[(jar, cls)]


def method_body(listing, name, desc_args):
    """the code lines of one method in a javap -c listing (matched by name and javap's argument text;
    name "static" is the static initializer)"""
    lines = listing.splitlines()
    for i, ln in enumerate(lines):
        head = ln.strip()
        hit = head == "static {};" if name == "static" else \
            re.search(r"\b" + re.escape(name) + r"\(" + re.escape(desc_args) + r"\);$", head)
        if hit:
            out = []
            for l2 in lines[i + 2:]:
                if not l2.strip():
                    break
                out.append(l2)
            return out
    return None


def is_under(classes, c, target):
    seen = set()
    todo = [c]
    while todo:
        x = todo.pop()
        if x in seen or x not in classes:
            continue
        seen.add(x)
        k = classes[x]
        if target == k.super or target in k.interfaces:
            return True
        todo += ([k.super] if k.super else []) + list(k.interfaces)
    return False


def split_desc(desc):
    out, i = [], 1
    while desc[i] != ")":
        j = i
        while desc[j] == "[":
            j += 1
        if desc[j] == "L":
            j = desc.index(";", j)
        out.append(desc[i:j + 1])
        i = j + 1
    return out, desc[desc.index(")") + 1:]


def sig_params(sig):
    """the parameter type signatures of a method signature, type arguments reduced to one level"""
    if not sig:
        return None
    sp = classfile.split_signature(sig)
    if not sp:
        return None
    out = []
    for t in sp[0]:
        m = re.match(r"L(java/util/(?:Optional|List));?<L([^<;]+)(?:<.*>)?;>;", t)
        out.append(f"{m.group(1).split('/')[-1]}<{m.group(2)}>" if m else t)
    return out


def ref(conv, old_t):
    new_t = "Ljava/util/List;" if old_t.startswith("[") else "Ljava/util/Optional;"
    return f"conv {ANY}#{conv}({old_t}){new_t}"


MIXIN_PKG = "top/dext/centurybridge/shims/v1_20_2"
MIXIN_CONFIG = "core/shims/1.20.2/resources/cb-1.20.2.mixins.json"


def mojang_simple(cls, table):
    c = cls
    suffix = ""
    while c not in table and "$" in c:
        suffix = c[c.rindex("$"):] + suffix
        c = c[:c.rindex("$")]
    return (table.get(c, c).split("/")[-1] + suffix).replace("$", "")


def sentinels(root, vjar, wjar, V, W, table, report):
    """
    1.20.1's ANY predicates whose constant 1.20.2 removed: the fieldRedirect of the constant to
    CbAdvAny (which must declare it), and, for every 1.20.1 method that answered for ANY before
    looking at anything else (aload_0; getstatic ANY; if_acmpne -- falling through to "return true"
    or "return JsonNull") and that 1.20.2 still declares under the same name and descriptor, a HEAD
    injector answering the same for the sentinel. Returns the mixin class names and the
    fieldRedirects.
    """
    any_src = open(os.path.join(root, "core/shims/1.20.2/src", ANY + ".java"), encoding="utf-8").read()
    names = []
    redirects = {}
    for p, vc in sorted(V.items()):
        if p not in W or vc.access & classfile.ACC_INTERFACE:
            continue
        if not table.get(p.split("$")[0], "").startswith("net/minecraft/advancements/"):
            continue  # the area: the advancement predicates
        consts = [f for f in vc.fields if f[1] == f"L{p};" and f[2] & classfile.ACC_STATIC and f[2] & classfile.ACC_PUBLIC]
        wf = {f[0] for f in W[p].fields}
        for fname, fdesc, _, _ in consts:
            if fname in wf:
                continue  # the constant survives: not a sentinel of ours
            listing = disassemble(vjar, p)
            if not re.search(r"\b" + fname + r":", listing):
                continue
            if f"public static final net.minecraft.{p.split('/')[-1].replace('$', '.')} {fname} " not in any_src:
                report.append(f"design: {p}.{fname} is gone and CbAdvAny declares no sentinel for it")
                continue
            redirects[f"{p}.{fname}:{fdesc}"] = ANY
            injectors = []
            wm = {(m.name, m.desc) for m in W[p].methods}
            for m in vc.methods:
                if m.access & classfile.ACC_STATIC or m.name.startswith("<"):
                    continue
                body = method_body(listing, m.name, javap_params(m.desc))
                if not body:
                    continue
                code = [re.sub(r"^\s*\d+: ", "", l).strip() for l in body]
                answer = None
                for i in range(len(code) - 4):
                    if code[i] == "aload_0" and re.search(r"getstatic .*\b" + fname + r":", code[i + 1]) \
                            and code[i + 2].startswith("if_acmpne"):
                        if code[i + 3] == "iconst_1" and code[i + 4] == "ireturn":
                            answer = "true"
                        elif "JsonNull.INSTANCE" in code[i + 3] and code[i + 4] == "areturn":
                            answer = "json"
                        break
                if answer is None:
                    continue
                if (m.name, m.desc) not in wm:
                    continue  # gone in 1.20.2: a damage row, bridged by hand
                injectors.append((m.name, m.desc, answer))
            if not injectors:
                continue
            simple = mojang_simple(p, table)
            cls_name = "CbAdvAny" + simple
            names.append(cls_name)
            jt = p.replace("/", ".").replace("$", ".")
            lines = [
                "package top.dext.centurybridge.shims.v1_20_2;\n",
                "import org.spongepowered.asm.mixin.Mixin;",
                "import org.spongepowered.asm.mixin.injection.At;",
                "import org.spongepowered.asm.mixin.injection.Inject;",
                "import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;\n",
                "/**",
                " * GENERATED by core/tools/gen_s1adv.py -- do not edit.",
                " *",
                f" * {simple}.ANY of 1.20.1 is the sentinel CbAdvAny.{fname}; these are the 1.20.1 methods that",
                " * answered for ANY before looking at anything else, giving the sentinel 1.20.1's answer where",
                " * 1.20.2 code meets it (1.20.2 has no ANY: an absent Optional says the same).",
                " */",
                f"@Mixin({jt}.class)",
                f"public abstract class {cls_name} {{\n",
            ]
            for name, desc, answer in injectors:
                params, ret = split_desc(desc)
                ptypes = [java_type(t) for t in params]
                plist = ", ".join(f"{t} a{i}" for i, t in enumerate(ptypes))
                if answer == "true":
                    cir = "CallbackInfoReturnable<Boolean>"
                    val = "true"
                else:
                    cir = "CallbackInfoReturnable<com.google.gson.JsonElement>"
                    val = "com.google.gson.JsonNull.INSTANCE"
                lines.append(f"    /** {name}{desc}: 1.20.1 answered {val} for ANY */")
                lines.append(f"    @Inject(method = \"{name}{desc}\", at = @At(\"HEAD\"), cancellable = true)")
                lines.append(f"    private void cb$any{name.split('_')[-1]}({plist + ', ' if plist else ''}{cir} cir) {{")
                lines.append(f"        if ((Object) this == top.dext.centurybridge.rt.v1_20_2.CbAdvAny.{fname}) {{")
                lines.append(f"            cir.setReturnValue({val});")
                lines.append("        }")
                lines.append("    }\n")
            lines.append("}")
            with open(os.path.join(root, "core/shims/1.20.2/src", MIXIN_PKG, cls_name + ".java"), "w",
                      encoding="utf-8", newline="\n") as f:
                f.write("\n".join(lines) + "\n")
    return names, redirects


RECORDS = RT + "/CbAdvRecords"

# 1.20.1 constructor argument (its generic signature) -> 1.20.2 parameter (its signature): the converter
# that turns the part a 1.20.1 constructor got into what the record takes for the same meaning
PART_CONVERSIONS = {
    ("Ljava/lang/Boolean;", "Ljava/util/Optional<Ljava/lang/Boolean;>;"): f"{RECORDS}#bool(Ljava/lang/Boolean;)Ljava/util/Optional;",
    ("Lnet/minecraft/class_2048;", "Ljava/util/Optional<Lnet/minecraft/class_2048;>;"): f"{ANY}#entity(Lnet/minecraft/class_2048;)Ljava/util/Optional;",
    ("Lnet/minecraft/class_2073;", "Ljava/util/Optional<Lnet/minecraft/class_2073;>;"): f"{ANY}#item(Lnet/minecraft/class_2073;)Ljava/util/Optional;",
    ("Lnet/minecraft/class_2022;", "Ljava/util/Optional<Lnet/minecraft/class_2022;>;"): f"{ANY}#damageSource(Lnet/minecraft/class_2022;)Ljava/util/Optional;",
    ("Lnet/minecraft/class_2105;", "Ljava/util/Optional<Lnet/minecraft/class_2105;>;"): f"{ANY}#nbt(Lnet/minecraft/class_2105;)Ljava/util/Optional;",
    ("Lnet/minecraft/class_4559;", "Ljava/util/Optional<Lnet/minecraft/class_4559;>;"): f"{ANY}#state(Lnet/minecraft/class_4559;)Ljava/util/Optional;",
    ("Lnet/minecraft/class_4552;", "Ljava/util/Optional<Lnet/minecraft/class_4552;>;"): f"{ANY}#light(Lnet/minecraft/class_4552;)Ljava/util/Optional;",
    ("Lnet/minecraft/class_4550;", "Ljava/util/Optional<Lnet/minecraft/class_4550;>;"): f"{ANY}#block(Lnet/minecraft/class_4550;)Ljava/util/Optional;",
    ("Lnet/minecraft/class_4551;", "Ljava/util/Optional<Lnet/minecraft/class_4551;>;"): f"{ANY}#fluid(Lnet/minecraft/class_4551;)Ljava/util/Optional;",
    ("Ljava/util/Set<Lnet/minecraft/class_2248;>;", "Ljava/util/Optional<Lnet/minecraft/class_6885<Lnet/minecraft/class_2248;>;>;"):
        f"{RECORDS}#blocks(Ljava/util/Set;)Ljava/util/Optional;",
    ("Ljava/util/Set<Lnet/minecraft/class_1792;>;", "Ljava/util/Optional<Lnet/minecraft/class_6885<Lnet/minecraft/class_1792;>;>;"):
        f"{RECORDS}#items(Ljava/util/Set;)Ljava/util/Optional;",
    ("Lnet/minecraft/class_3611;", "Ljava/util/Optional<Lnet/minecraft/class_6880<Lnet/minecraft/class_3611;>;>;"):
        f"{RECORDS}#fluid(Lnet/minecraft/class_3611;)Ljava/util/Optional;",
    ("Lnet/minecraft/class_1842;", "Ljava/util/Optional<Lnet/minecraft/class_6880<Lnet/minecraft/class_1842;>;>;"):
        f"{RECORDS}#potion(Lnet/minecraft/class_1842;)Ljava/util/Optional;",
    ("[Lnet/minecraft/class_2035;", "Ljava/util/List<Lnet/minecraft/class_2035;>;"):
        f"{RECORDS}#enchantments([Lnet/minecraft/class_2035;)Ljava/util/List;",
}
# a tag or a resource key, any type argument: nullable in 1.20.1, an Optional of it in 1.20.2
PART_GENERIC = {
    "Lnet/minecraft/class_6862": f"{RECORDS}#tag(Lnet/minecraft/class_6862;)Ljava/util/Optional;",
    "Lnet/minecraft/class_5321": f"{RECORDS}#key(Lnet/minecraft/class_5321;)Ljava/util/Optional;",
}
# what a 1.20.1 no-argument constructor's record holds for each part: the value 1.20.1's ()V stored
# (null / ANY / NONE -> absent or empty; MinMaxBounds.*.ANY)
PART_DEFAULTS = {
    "Ljava/util/Optional;": f"{RECORDS}#absent()Ljava/util/Optional;",
    "Ljava/util/List;": f"{RECORDS}#none()Ljava/util/List;",
    "Lnet/minecraft/class_2096$class_2100;": f"{RECORDS}#anyInts()Lnet/minecraft/class_2096$class_2100;",
    "Lnet/minecraft/class_2096$class_2099;": f"{RECORDS}#anyDoubles()Lnet/minecraft/class_2096$class_2099;",
    "Lnet/minecraft/class_6885;": f"{RECORDS}#noEntityTypes()Lnet/minecraft/class_6885;",
}
# a no-argument constructor whose record's canonical constructor is not the one 1.20.1's fields match
# by position: EnchantmentPredicate() stored a null enchantment and MinMaxBounds.Ints.ANY levels, the
# record's (Optional<Holder<Enchantment>>, Ints) constructor takes the same as absent and ANY
DEFAULT_TARGET = {
    "net/minecraft/class_2035": "(Ljava/util/Optional;Lnet/minecraft/class_2096$class_2100;)V",
}


def part_conversion(a, b):
    """the converter for a 1.20.1 constructor part of signature a where the record takes b, or None"""
    c = PART_CONVERSIONS.get((a, b))
    if c:
        return c
    for raw, conv in PART_GENERIC.items():
        if a.startswith(raw + "<") and b.startswith("Ljava/util/Optional<" + raw + "<"):
            return conv
    return None


def records(V, W, section, report, classes_table):
    """
    The advancement predicates 1.20.2 turned into records (final; the layer's access widener makes them
    extendable). A 1.20.1 constructor the record does not have is reshaped -- at a mod's new X(..) and at
    a mod subclass's super(..) -- onto the record's constructor of the same arity, positional, each part
    converted (PART_CONVERSIONS: a nullable or ANY part to an absent Optional, a set to a HolderSet, an
    array to a List); a no-argument constructor onto the canonical one with the values 1.20.1's stored
    (PART_DEFAULTS). LocationPredicate's x/y/z bounds fold into its position predicate. A constructor no
    rule maps is reported for hand wiring.
    """
    table = {}
    for line in open(classes_table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "C":
            table[f[1]] = f[2]
    for p in sorted(V):
        if p not in W or not table.get(p.split("$")[0], "").startswith("net/minecraft/advancements/criterion"):
            continue
        if W[p].super != "java/lang/Record" or V[p].super == "java/lang/Record":
            continue
        if not V[p].access & classfile.ACC_PUBLIC:
            continue  # package-private in 1.20.1: no mod could name its constructors
        news = [m for m in W[p].methods if m.name == "<init>" and m.access & classfile.ACC_PUBLIC]
        for om in V[p].methods:
            if om.name != "<init>" or not om.access & classfile.ACC_PUBLIC or any(n.desc == om.desc for n in news):
                continue
            key = f"{p}.<init>{om.desc}"
            oa = classfile.split_args(om.desc)[0]
            osig = (classfile.split_signature(om.signature) or [oa])[0] if om.signature else oa
            if not oa:
                # 1.20.1's ()V: the canonical constructor (the one with most parameters) with 1.20.1's values
                nm = next((n for n in news if n.desc == DEFAULT_TARGET.get(p)), None) or                     max(news, key=lambda n: len(classfile.split_args(n.desc)[0]))
                ops = []
                for t in classfile.split_args(nm.desc)[0]:
                    d = PART_DEFAULTS.get(t)
                    if not d:
                        report.append(f"design: {key}: no default for {t}")
                        ops = None
                        break
                    ops.append("push " + d)
                if ops is not None:
                    section("superCtorReshapes")[key] = {"desc": nm.desc, "ops": ops,
                        "basis": f"{BASIS}: 1.20.1's no-argument predicate (ANY's parts) is the record with every part absent / any"}
                continue
            if p == "net/minecraft/class_2090" and len(oa) == 10:
                # LocationPredicate(x, y, z, biome, structure, dimension, smokey, light, block, fluid)
                nm = news[0]
                ops = [f"conv:0,1,2 {RECORDS}#position(Lnet/minecraft/class_2096$class_2099;Lnet/minecraft/class_2096$class_2099;"
                       f"Lnet/minecraft/class_2096$class_2099;)Ljava/util/Optional;"]
                convs = {3: PART_GENERIC["Lnet/minecraft/class_5321"], 4: PART_GENERIC["Lnet/minecraft/class_5321"],
                         5: PART_GENERIC["Lnet/minecraft/class_5321"]}
                for i in range(3, 10):
                    c = convs.get(i) or part_conversion(osig[i], (classfile.split_signature(nm.signature) or [[]])[0][i - 2])
                    if not c:
                        report.append(f"design: {key}: part {i}")
                        ops = None
                        break
                    ops.append(f"conv:{i} {c}")
                if ops is not None:
                    section("superCtorReshapes")[key] = {"desc": nm.desc, "ops": ops,
                        "basis": f"{BASIS}: x/y/z bounds as the position predicate (absent when all are any), the rest positional as Optionals"}
                continue
            cands = [n for n in news if len(classfile.split_args(n.desc)[0]) == len(oa)]
            if len(cands) != 1:
                report.append(f"design: {key}: {len(cands)} record constructors of that arity")
                continue
            nm = cands[0]
            na = classfile.split_args(nm.desc)[0]
            nsig = (classfile.split_signature(nm.signature) or [na])[0] if nm.signature else na
            ops = []
            for i, (a, b, sa, sb) in enumerate(zip(oa, na, osig, nsig)):
                if a == b:
                    ops.append("keep")
                    continue
                c = part_conversion(sa, sb)
                if not c:
                    report.append(f"design: {key}: part {i} {sa} -> {sb}")
                    ops = None
                    break
                ops.append("conv " + c)
            if ops is not None:
                section("superCtorReshapes")[key] = {"desc": nm.desc, "ops": ops,
                    "basis": f"{BASIS}: positional, nullable or ANY parts as absent Optionals, sets as HolderSets, arrays as Lists"}


# a 1.20.1 builder setter's value -> the expression 1.20.2's Optional field holds for it
SETTER_CONV = {
    "Lnet/minecraft/class_2048;": "entity", "Lnet/minecraft/class_2090;": "location", "Lnet/minecraft/class_2102;": "effects",
    "Lnet/minecraft/class_2040;": "flags", "Lnet/minecraft/class_4552;": "light", "Lnet/minecraft/class_4550;": "block",
    "Lnet/minecraft/class_4551;": "fluid", "Lnet/minecraft/class_2073;": "item", "Lnet/minecraft/class_4559;": "state",
    "Lnet/minecraft/class_2105;": "nbt", "Lnet/minecraft/class_2022;": "damageSource", "Lnet/minecraft/class_2025;": "distance",
}
BUILDER_PREFIX = "CbAdvBuilder"


def builders(root, vjar, V, W, table, report):
    """
    1.20.1's predicate builder setters -- `this.f = value; return this` -- that 1.20.2 replaced by setters
    taking the nested builder (or dropped), where 1.20.2 keeps the field under the same name as an Optional.
    Each comes back as a cover: a mixin method of the exact 1.20.1 shape on the builder, storing what 1.20.1
    stored with ANY / null as absent (CbAdvAny) -- so a call, a mod's override and its super call behave as in
    1.20.1. Returns the mixin names and the covered keys.
    """
    names, covers = [], []
    for p in sorted(V):
        if p not in W or "$" not in p or not table.get(p.split("$")[0], "").startswith("net/minecraft/advancements/criterion"):
            continue
        wm = {(m.name, m.desc) for m in W[p].methods}
        wf = {f[0]: f for f in W[p].fields}
        listing = disassemble(vjar, p)
        found = []
        for m in V[p].methods:
            if m.access & classfile.ACC_STATIC or not m.access & classfile.ACC_PUBLIC or m.name.startswith("<") \
                    or (m.name, m.desc) in wm:
                continue
            code = [re.sub(r"^\s*\d+: ", "", l).strip() for l in (method_body(listing, m.name, javap_params(m.desc)) or [])]
            if [c.split()[0] for c in code] != ["aload_0", "aload_1", "putfield", "aload_0", "areturn"]:
                continue
            fld = re.search(r"Field (field_\d+):(\S+)", code[2])
            fname, vtype = fld.group(1), fld.group(2)
            w = wf.get(fname)
            if not w or w[1] != "Ljava/util/Optional;" or w[2] & classfile.ACC_FINAL:
                report.append(f"design: builder {p}.{m.name}{m.desc}: 1.20.2 field {fname} is not a settable Optional")
                continue
            if vtype == "Ljava/lang/Boolean;":
                expr = "java.util.Optional.ofNullable(value)"
            elif vtype in SETTER_CONV:
                expr = f"top.dext.centurybridge.rt.v1_20_2.CbAdvAny.{SETTER_CONV[vtype]}(value)"
            else:
                report.append(f"design: builder {p}.{m.name}{m.desc}: no conversion for {vtype}")
                continue
            if f"<{vtype[1:-1]}>" not in (w[3] or "") and f"<{vtype}>" not in (w[3] or ""):
                report.append(f"design: builder {p}.{m.name}{m.desc}: 1.20.2 {fname} holds {w[3]}")
                continue
            found.append((m, fname, vtype, expr))
        if not found:
            continue
        simple = mojang_simple(p, table)
        cls_name = BUILDER_PREFIX + simple
        names.append(cls_name)
        jt = p.replace("/", ".").replace("$", ".")
        lines = [
            "package top.dext.centurybridge.shims.v1_20_2;\n",
            "import org.spongepowered.asm.mixin.Mixin;",
            "import org.spongepowered.asm.mixin.Shadow;\n",
            "/**",
            " * GENERATED by core/tools/gen_s1adv.py -- do not edit.",
            " *",
            f" * {simple}'s 1.20.1 setters: `this.field = value; return this`. 1.20.2 keeps each field under the",
            " * same name as an Optional and sets it from a nested builder; the 1.20.1 shape is put back (a cover),",
            " * storing what 1.20.1 stored -- ANY and null as absent -- so calls, overrides and super calls of a",
            " * 1.20.1 mod behave as they did.",
            " */",
            f"@Mixin({jt}.class)",
            f"public abstract class {cls_name} {{\n",
        ]
        for fname in sorted({f for _, f, _, _ in found}):
            lines.append("    @Shadow")
            lines.append(f"    @SuppressWarnings(\"rawtypes\")")
            lines.append(f"    private java.util.Optional {fname};\n")
        for m, fname, vtype, expr in found:
            lines.append(f"    /** {m.name}{m.desc} (1.20.1: {fname} = value) */")
            lines.append(f"    public {java_type(m.desc[m.desc.index(')') + 1:])} {m.name}({java_type(vtype)} value) {{")
            lines.append(f"        this.{fname} = {expr};")
            lines.append(f"        return ({jt}) (Object) this;")
            lines.append("    }\n")
            covers.append(f"{p}.{m.name}{m.desc}")
        lines.append("}")
        with open(os.path.join(root, "core/shims/1.20.2/src", MIXIN_PKG, cls_name + ".java"), "w",
                  encoding="utf-8", newline="\n") as f:
            f.write("\n".join(lines) + "\n")
    return names, covers


NODES = RT + "/CbAdvNodes"
ADV, NODE, HOLDER = "Lnet/minecraft/class_161;", "Lnet/minecraft/class_8781;", "Lnet/minecraft/class_8779;"
# 1.20.1 Advancement -> what 1.20.2 takes in its place, and back
TO_NEW = {NODE: f"{NODES}#nodeOf({ADV})" + NODE, HOLDER: f"{NODES}#holderOf({ADV})" + HOLDER}
TO_OLD = {NODE: f"{NODES}#valueOf({NODE})" + ADV, HOLDER: f"{NODES}#valueOf({HOLDER})" + ADV}


def holders(root, V, W, section, report, frag):
    """
    Members whose 1.20.2 shape differs from 1.20.1's only in Advancement standing where 1.20.2 has an
    AdvancementNode (tree code: listeners, tabs, positions) or an AdvancementHolder (id-keyed code:
    progress, selection). The 1.20.1 Advancement is the record (CbAdvNodes): a call converts each such
    argument to the node it is placed at / its holder and the answer back to the record (callReshapes);
    a 1.20.1 override is reached from the new shape with the record (overrideHooks wrap / unwrap, one hook
    per topmost declaring type, so a class under two declaring types gets one); a constructor's super(..)
    and new are reshaped the same way (superCtorReshapes). Members with other type-argument drift are left
    for hand wiring, as are members another fragment or the hand-written part already wires.
    """
    wired = set()
    for f in [os.path.join(root, "core/segments/shims-1.20.2.json")] + sorted(
            glob.glob(os.path.join(root, "core/segments/shims-1.20.2.json.d/*.json"))):
        if f.endswith("s1adv.json"):
            continue
        d = json.load(open(f, encoding="utf-8"))
        for s, v in d.items():
            if isinstance(v, dict):
                wired |= set(v)
            elif s == "covers":
                wired |= set(v)
    for s, v in frag.items():  # the hand-written part of this fragment (generated entries are dropped by now)
        if isinstance(v, dict):
            wired |= set(v)
        elif s == "covers":
            wired |= set(v)
    found = []
    for p in sorted(V):
        if p not in W:
            continue
        wm = {}
        for m in W[p].methods:
            wm.setdefault(m.name, []).append(m)
        for m in V[p].methods:
            if ADV not in m.desc or not m.access & (classfile.ACC_PUBLIC | classfile.ACC_PROTECTED) \
                    or m.name.startswith("lambda") or m.access & classfile.ACC_SYNTHETIC:
                continue
            if any(w.desc == m.desc for w in wm.get(m.name, [])):
                continue
            oa, orr = classfile.split_args(m.desc)
            match = None
            for w in wm.get(m.name, []):
                na, nr = classfile.split_args(w.desc)
                if len(na) == len(oa) and all(x == y or (x == ADV and y in (NODE, HOLDER)) for x, y in zip(oa + [orr], na + [nr])):
                    match = w
            if not match:
                continue
            na, nr = classfile.split_args(match.desc)
            # other drift: a parameter that is not the advancement whose type arguments changed
            os_ = (classfile.split_signature(m.signature) or [oa])[0] if m.signature else oa
            ns_ = (classfile.split_signature(match.signature) or [na])[0] if match.signature else na
            drift = [i for i, (a, b, x, y) in enumerate(zip(oa, na, os_, ns_)) if a == b and x != y and a != ADV]
            key = f"{p}.{m.name}{m.desc}"
            if drift:
                report.append(f"design: {key}: parameters {drift} drift as well")
                continue
            found.append((p, m, match))
    by_shape = {}
    for p, m, w in found:
        key = f"{p}.{m.name}{m.desc}"
        if key in wired:
            report.append(f"wired elsewhere: {key}")
            continue
        oa, orr = classfile.split_args(m.desc)
        na, nr = classfile.split_args(w.desc)
        if m.name == "<init>":
            section("superCtorReshapes")[key] = {
                "desc": w.desc, "ops": ["keep" if a == b else "conv " + TO_NEW[b] for a, b in zip(oa, na)],
                "basis": f"{BASIS}: the advancement as the node / holder 1.20.2 takes (CbAdvNodes)"}
            continue
        entry = {"desc": w.desc, "ops": ["keep" if a == b else "conv " + TO_NEW[b] for a, b in zip(oa, na)],
                 "basis": f"{BASIS}: the advancement as the node / holder 1.20.2 takes, the answer back as the advancement"}
        if orr != nr:
            entry["ret"] = "conv " + TO_OLD[nr]
        section("callReshapes")[key] = entry
        if not m.access & (classfile.ACC_STATIC | classfile.ACC_FINAL | classfile.ACC_PRIVATE) and not V[p].access & classfile.ACC_FINAL:
            by_shape.setdefault((m.name, m.desc, w.desc), []).append(p)
    for (name, od, nd), owners in sorted(by_shape.items()):
        oa, orr = classfile.split_args(od)
        na, nr = classfile.split_args(nd)
        changed = {b for a, b in zip(oa, na) if a != b}
        if len(changed) > 1:
            report.append(f"design: {owners}.{name}{od}: node and holder arguments together")
            continue
        top = [o for o in owners if not any(o != q and is_under(V, o, q) for q in owners)]
        for o in top:
            h = {"old": name + od, "new": name + nd, "onlyUnder": o,
                 "basis": f"{BASIS}: a 1.20.1 override is reached from the new shape with the advancement (CbAdvNodes)"}
            if changed:
                b = changed.pop()
                h["wrap"] = TO_OLD[b].replace("#", ".")
            if orr != nr:
                h["unwrap"] = TO_NEW[nr].replace("#", ".")
            section("overrideHooks", list).append(h)


def java_type(t):
    dim = len(t) - len(t.lstrip("["))
    t = t.lstrip("[")
    base = {"Z": "boolean", "B": "byte", "C": "char", "S": "short", "I": "int", "J": "long", "F": "float", "D": "double"}
    s = base[t] if t in base else t[1:-1].replace("/", ".").replace("$", ".")
    return s + "[]" * dim


def javap_params(desc):
    """javap's spelling of a descriptor's parameter list"""
    a, _ = split_desc(desc)
    out = []
    for t in a:
        dim = len(t) - len(t.lstrip("["))
        t = t.lstrip("[")
        base = {"Z": "boolean", "B": "byte", "C": "char", "S": "short", "I": "int", "J": "long", "F": "float", "D": "double"}
        s = base[t] if t in base else t[1:-1].replace("/", ".")
        out.append(s + "[]" * dim)
    return ", ".join(out)


def main():
    root, vjar, wjar, classes_table = sys.argv[1:5]
    V, W = load(vjar), load(wjar)
    damage = [l.rstrip("\n").split("\t") for l in open(os.path.join(root, "core/segments/damage/1.20.1__1.20.2.damage.tsv"),
                                                     encoding="utf-8")][1:]
    frag_path = os.path.join(root, "core/segments/shims-1.20.2.json.d/s1adv.json")
    frag = json.load(open(frag_path, encoding="utf-8")) if os.path.exists(frag_path) else {}

    # drop what this generator owns
    for sec in list(frag):
        v = frag[sec]
        if isinstance(v, dict):
            for k in list(v):
                val = v[k]
                owned = (isinstance(val, dict) and str(val.get("basis", "")).startswith(BASIS)) or \
                        (isinstance(val, str) and (val.startswith(TRIGGER_IDS) or val.startswith(FACTORIES)
                                                   or (sec == "fieldRedirects" and val == ANY)))
                if owned:
                    del v[k]
        elif isinstance(v, list):
            frag[sec] = [h for h in v if not (isinstance(h, dict) and str(h.get("basis", "")).startswith(BASIS))]

    def section(name, kind=dict):
        return frag.setdefault(name, kind())

    # ---------------------------------------------------------------- triggers
    triggers = sorted(n for n, c in V.items() if n in W and not c.access & classfile.ACC_INTERFACE
                      and (is_under(V, n, CRITERION_TRIGGER)))
    java = []
    report = []
    for t in triggers:
        vc, wc = V[t], W[t]
        listing = disassemble(vjar, t)
        # getId: the constant or the constructor argument
        body = method_body(listing, "method_794", "")
        if body is not None:
            getstatic = [l for l in body if "getstatic" in l]
            getfield = [l for l in body if "getfield" in l]
            if getfield:
                ret = "CbAdvTriggers.storedId(self)"
                why = "its constructor argument"
            elif getstatic:
                fld = re.search(r"Field (?:([\w/$]+)\.)?(field_\d+):", getstatic[0])
                owner = fld.group(1) or t
                fname = fld.group(2)
                init = method_body(disassemble(vjar, owner), "static", "")
                lit = None
                if init:
                    for i, l in enumerate(init):
                        if re.search(r"putstatic .*\b" + fname + r":", l):
                            for back in reversed(init[:i]):
                                m = re.search(r"// String (\S+)", back)
                                if m:
                                    lit = m.group(1)
                                    break
                            break
                if lit is None:
                    report.append(f"design: {t}.method_794 constant {fname} without a literal")
                    continue
                ret = f"new net.minecraft.class_2960({json.dumps(lit)})"
                why = f"the constant {lit}"
            else:
                report.append(f"design: {t}.method_794 neither constant nor field")
                continue
            jt = t.replace("/", ".").replace("$", ".")
            java.append(f"    /** super.getId() in a subclass of {t.split('/')[-1]}: {why} */\n"
                        f"    public static net.minecraft.class_2960 method_794({jt} self) {{\n"
                        f"        return {ret};\n    }}\n")
            section("superRedirects")[f"{t}.method_794()L{RESOURCE_LOCATION};"] = TRIGGER_IDS
        # createInstance (json, player, context)
        for m in vc.methods:
            if m.access & (classfile.ACC_STATIC | classfile.ACC_BRIDGE | classfile.ACC_SYNTHETIC):
                continue
            if not m.desc.startswith(f"(L{JSON};L{CONTEXT};L{DESER};)"):
                continue
            new_desc = f"(L{JSON};L{OPTIONAL};L{DESER};)" + m.desc[m.desc.index(")") + 1:]
            if m.desc.endswith(f")L{ABSTRACT_INSTANCE};"):
                continue  # the base shape (SimpleCriterionTrigger's): hand-written
            if not any(w.name == m.name and w.desc == new_desc for w in wc.methods):
                report.append(f"design: {t}.{m.name}{m.desc}: no {new_desc}")
                continue
            key = f"{t}.{m.name}{m.desc}"
            section("callReshapes")[key] = {
                "desc": new_desc, "ops": ["keep", ref("context", f"L{CONTEXT};"), "keep"],
                "basis": f"{BASIS}: createInstance takes the player predicate as an Optional (ANY is absent)"}
            section("overrideHooks", list).append({
                "old": m.name + m.desc, "new": m.name + new_desc, "onlyUnder": t,
                "wrap": f"{RT}/CbAdvTriggers.player(L{OPTIONAL};)L{CONTEXT};",
                "basis": f"{BASIS}: a 1.20.1 override of createInstance is handed absent as ContextAwarePredicate.ANY"})
        # <init>(id) -> <init>()
        vct = [m.desc for m in vc.methods if m.name == "<init>" and m.access & classfile.ACC_PUBLIC]
        wct = [m.desc for m in wc.methods if m.name == "<init>" and m.access & classfile.ACC_PUBLIC]
        for d in vct:
            if d == f"(L{RESOURCE_LOCATION};)V" and d not in wct and "()V" in wct:
                section("superCtorReshapes")[f"{t}.<init>{d}"] = {
                    "desc": "()V", "ops": [f"drop {RT}/CbAdvTriggers#stash(L{RESOURCE_LOCATION};)V"],
                    "basis": f"{BASIS}: the trigger keeps the id it was built with (claimed by SimpleCriterionTrigger)"}

    # ---------------------------------------------------------------- instances
    instances = sorted(n for n, c in V.items() if n in W and c.super == ABSTRACT_INSTANCE)
    for i in instances:
        vc, wc = V[i], W[i]
        olds = [m for m in vc.methods if m.name == "<init>" and m.access & classfile.ACC_PUBLIC]
        news = [m for m in wc.methods if m.name == "<init>" and m.access & classfile.ACC_PUBLIC]
        for om in olds:
            if any(nm.desc == om.desc for nm in news):
                continue
            if len(news) != 1:
                report.append(f"design: {i}.<init>{om.desc}: {len(news)} 1.20.2 constructors")
                continue
            nm = news[0]
            oa, _ = split_desc(om.desc)
            na, _ = split_desc(nm.desc)
            ns = sig_params(nm.signature) or na
            ops = []
            if len(oa) == len(na) + 1 and oa[0] == f"L{RESOURCE_LOCATION};":
                ops.append(f"drop {RT}/CbAdvInstances#stash(L{RESOURCE_LOCATION};)V")
                oa = oa[1:]
            if len(oa) != len(na):
                report.append(f"design: {i}.<init>{om.desc} -> {nm.desc}: arity")
                continue
            ok = True
            for a, b, s in zip(oa, na, ns):
                if a == b:
                    ops.append("keep")
                    continue
                conv = CONVERSIONS.get((a, s))
                if not conv:
                    report.append(f"design: {i}.<init>{om.desc}: {a} -> {s}")
                    ok = False
                    break
                ops.append(ref(conv, a))
            if ok:
                section("superCtorReshapes")[f"{i}.<init>{om.desc}"] = {
                    "desc": nm.desc, "ops": ops,
                    "basis": f"{BASIS}: positional, predicates that may be ANY as Optionals, arrays as Lists"
                             + ("; the trigger id kept with the instance" if ops[0].startswith("drop") else "")}

    # factories: every call-static row of an instance class -> CbAdvFactories (hand-ported)
    src = open(os.path.join(root, "core/shims/1.20.2/src", FACTORIES + ".java"), encoding="utf-8").read() \
        if os.path.exists(os.path.join(root, "core/shims/1.20.2/src", FACTORIES + ".java")) else ""
    for r in damage:
        usage, owner, name, desc = r[0], r[1], r[2], r[3]
        if usage != "call-static" or (owner not in instances and owner not in triggers):
            continue
        if not re.search(r"public static \S+ " + re.escape(name) + r"\(", src):
            report.append(f"missing port: {owner}.{name}{desc}")
            continue
        section("staticRedirects")[f"{owner}.{name}{desc}"] = FACTORIES

    # ---------------------------------------------------------------- predicate records
    records(V, W, section, report, classes_table)

    # ---------------------------------------------------------------- Advancement -> node / holder
    holders(root, V, W, section, report, frag)

    # ---------------------------------------------------------------- ANY sentinels
    table = {}
    for line in open(classes_table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "C":
            table[f[1]] = f[2]
    sentinel_mixins, sentinel_redirects = sentinels(root, vjar, wjar, V, W, table, report)
    for k, v in sentinel_redirects.items():
        section("fieldRedirects")[k] = v
    builder_mixins, builder_covers = builders(root, vjar, V, W, table, report)
    owners = {k.split(".")[0] for k in builder_covers}
    frag["covers"] = sorted({c for c in frag.get("covers", []) if c.split(".")[0] not in owners} | set(builder_covers))
    cfg_path = os.path.join(root, MIXIN_CONFIG)
    cfg = json.load(open(cfg_path, encoding="utf-8"))
    mixins = set(cfg.get("mixins", []))
    old_generated = {m for m in mixins if m.startswith("CbAdvAny") or m.startswith(BUILDER_PREFIX)}
    mixins = (mixins - old_generated) | set(sentinel_mixins) | set(builder_mixins)
    cfg["mixins"] = sorted(mixins)
    with open(cfg_path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(cfg, f, indent=2)
        f.write("\n")

    # ---------------------------------------------------------------- write
    out_java = os.path.join(root, "core/shims/1.20.2/src", TRIGGER_IDS + ".java")
    with open(out_java, "w", encoding="utf-8", newline="\n") as f:
        f.write("package top.dext.centurybridge.rt.v1_20_2;\n\n"
                "/**\n * GENERATED by core/tools/gen_s1adv.py -- do not edit.\n *\n"
                " * super.getId() in a converted subclass of a vanilla criterion trigger (superRedirects): what\n"
                " * 1.20.1's own getId() returned -- the class's ID constant, or the id the trigger was built\n"
                " * with (CbAdvTriggers.storedId) for the classes registered under several ids.\n */\n"
                "public final class CbAdvTriggerIds {\n\n" + "\n".join(java) +
                "\n    private CbAdvTriggerIds() {\n    }\n}\n")
    for sec in list(frag):
        if not frag[sec]:
            del frag[sec]
        elif isinstance(frag[sec], dict):
            frag[sec] = dict(sorted(frag[sec].items()))
    with open(frag_path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(frag, f, indent=1)
        f.write("\n")
    for r in report:
        print(r)
    print(f"{len(triggers)} triggers, {len(instances)} instance classes -> {frag_path}")


if __name__ == "__main__":
    main()
