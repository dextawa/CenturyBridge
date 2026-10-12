"""Step 1.20.6 -> 1.21, area s6item: the vanilla enchantments of 1.20.6 as facade objects, read off 1.20.6's bytecode.

1.20.6's Enchantments class built every vanilla enchantment in its static initializer --
`register(name, new <Class>(Enchantment.definition(<tags>, <weight>, <max level>, <costs>, <anvil cost>,
<slots>), <extra arguments>))` -- and a 1.20.6 mod reads them as `Enchantments.SHARPNESS` objects of those
classes (DamageEnchantment, ProtectionEnchantment ...). On 1.21 the fields hold ResourceKeys. This
generator evaluates that static initializer symbolically (the instructions are a straight line of
constants, tag and slot constants, static factory calls and constructor calls) and emits the same
construction against the step's facades, so `Enchantments.X` reads, through a fieldGetter, an object
of the very class 1.20.6 made with the very definition 1.20.6 gave it, registered under the same name
in CenturyBridge's stand-in for BuiltInRegistries.ENCHANTMENT.

  python gen_s6item_enchantments.py <V code jar> <W stub jar> <layer src root> <fragment.json>

V code jar: client-1.20.6-intermediary-code.jar (javap reads it); W stub: client-1.21-intermediary.jar
(every vanilla constant the construction names must still exist there, or the generator stops).
Writes <layer src>/top/dext/centurybridge/step/v1_21/rt/CbEnchVanilla.java and the fieldGetters of
<fragment.json> (s6item-gen.json), replacing that section.
"""
import json
import os
import re
import subprocess
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

FACADE = "top.dext.centurybridge.step.v1_21.facade."
RT = "top/dext/centurybridge/step/v1_21/rt/CbEnchVanilla"
ENCHANTMENTS = "net/minecraft/class_1893"
# 1.20.6's Enchantment and the classes of its vanilla enchantments: renamed to facades in this step
FACADED = {"net/minecraft/class_1887", "net/minecraft/class_1887$class_9427", "net/minecraft/class_1900$class_1901",
           "net/minecraft/class_1880", "net/minecraft/class_1881", "net/minecraft/class_1883", "net/minecraft/class_9467",
           "net/minecraft/class_1882", "net/minecraft/class_9468", "net/minecraft/class_1885", "net/minecraft/class_1894",
           "net/minecraft/class_1896", "net/minecraft/class_1899", "net/minecraft/class_1898", "net/minecraft/class_1900",
           "net/minecraft/class_4948", "net/minecraft/class_7267", "net/minecraft/class_1906", "net/minecraft/class_1910",
           "net/minecraft/class_1909", "net/minecraft/class_1911", "net/minecraft/class_1913", "net/minecraft/class_9469"}


def java_class(internal):
    if internal in FACADED:
        return FACADE + internal.rsplit("/", 1)[1]
    return internal.replace("/", ".").replace("$", ".")


def javap_clinit(jar, cls):
    out = subprocess.run(["javap", "-c", "-p", "-cp", jar, cls.replace("/", ".")],
                         capture_output=True, text=True, check=True).stdout
    start = out.index("  static {};")
    return out[start:].splitlines()[2:]


def args_of(desc):
    return classfile.split_args(desc[1:desc.index(")")]) if hasattr(classfile, "split_args") else None


def n_args(desc):
    inner = desc[1:desc.index(")")]
    n, i = 0, 0
    while i < len(inner):
        c = inner[i]
        while c == "[":
            i += 1
            c = inner[i]
        if c == "L":
            i = inner.index(";", i)
        i += 1
        n += 1
    return n


def main():
    vjar, wjar, src, frag = sys.argv[1:5]
    with zipfile.ZipFile(wjar) as z:
        def w_has_field(owner, name, desc):
            try:
                c = classfile.read(z.read(owner + ".class"))
            except KeyError:
                return False
            return any(f[0] == name and f[1] == desc for f in c.fields)

        stack, entries, locals_ = [], [], {}
        for line in javap_clinit(vjar, ENCHANTMENTS):
            m = re.match(r"\s+\d+: (\w+)\s*(.*)", line)
            if not m:
                continue
            op, rest = m.group(1), m.group(2)
            ref = re.search(r"// (?:Field|Method|InterfaceMethod|class|String) (.+)$", rest)
            ref = ref.group(1).strip() if ref else None
            if op.startswith("iconst_"):
                stack.append(op[7:].replace("m1", "-1"))
            elif op in ("bipush", "sipush"):
                stack.append(rest.split()[0])
            elif op in ("ldc", "ldc_w"):
                stack.append(json.dumps(ref))  # the only ldc constants are the names
            elif op == "dup":
                stack.append(stack[-1])
            elif op == "new":
                stack.append(("new", ref))
            elif op == "anewarray":
                stack.append(("array", ref, [None] * int(stack.pop())))
            elif op == "aastore":
                v, i, arr = stack.pop(), int(stack.pop()), stack.pop()
                arr[2][i] = v
            elif op == "getstatic":
                owner_name, desc = ref.split(":")
                owner, name = owner_name.rsplit(".", 1) if "." in owner_name else (ENCHANTMENTS, owner_name)
                if owner == ENCHANTMENTS:
                    stack.append(locals_[name])
                else:
                    if owner not in FACADED and not w_has_field(owner, name, desc):
                        raise SystemExit(f"{owner}.{name}:{desc} is gone in 1.21")
                    stack.append(java_class(owner) + "." + name)
            elif op == "putstatic":
                name = ref.split(":")[0]
                v = stack.pop()
                if name == "field_9102":
                    locals_[name] = "ARMOR_SLOTS"
                    entries.append(("ARMOR_SLOTS", None, v))
                else:
                    entries[-1] = (entries[-1][0], name, entries[-1][2])
            elif op in ("invokestatic", "invokespecial"):
                owner_name, desc = ref.rsplit(":", 1)
                owner, name = owner_name.rsplit(".", 1) if "." in owner_name else (ENCHANTMENTS, owner_name)
                name = name.strip('"')
                n = n_args(desc)
                args = stack[len(stack) - n:]
                del stack[len(stack) - n:]
                if op == "invokespecial":
                    target = stack.pop()
                    stack.pop() if stack and stack[-1] == target else None
                    assert target == ("new", owner), (target, owner)
                    stack.append(("expr", f"new {java_class(owner)}({', '.join(render(a) for a in args)})"))
                elif owner == ENCHANTMENTS and name == "method_8235":  # register(String, Enchantment)
                    entries.append((args[0], None, args[1]))
                    stack.append(("registered", len(entries) - 1))
                elif owner == "java/util/Optional" and name == "of":
                    stack.append(("expr", f"java.util.Optional.of({render(args[0])})"))
                elif owner == "net/minecraft/class_1304" and name == "values":
                    stack.append(("expr", "net.minecraft.class_1304.values()"))
                else:
                    stack.append(("expr", f"{java_class(owner)}.{name}({', '.join(render(a) for a in args)})"))
            elif op == "return":
                break
            else:
                raise SystemExit(f"unhandled instruction {op} {rest}")
    # registered results are consumed by putstatic: fix the stack bookkeeping above
    write_java(src, entries)
    write_fragment(frag, entries)


def render(v):
    if isinstance(v, tuple):
        if v[0] == "expr":
            return v[1]
        if v[0] == "array":
            return "new " + java_class(v[1]) + "[] {" + ", ".join(render(x) for x in v[2]) + "}"
        if v[0] == "registered":
            raise SystemExit("a registered enchantment used as an argument")
    return str(v)


def write_java(src, entries):
    lines = []
    for name, field, expr in entries:
        if name == "ARMOR_SLOTS":
            continue
        lines.append(f"        {field} = reg({name}, {render(expr)});")
    fields = [f for n, f, e in entries if n != "ARMOR_SLOTS"]
    armor = next(render(e) for n, f, e in entries if n == "ARMOR_SLOTS")
    path = os.path.join(src, RT + ".java")
    body = f'''package top.dext.centurybridge.step.v1_21.rt;

import top.dext.centurybridge.step.v1_21.facade.class_1887;

/**
 * GENERATED by core/tools/gen_s6item_enchantments.py from 1.20.6's Enchantments static initializer
 * (net/minecraft/class_1893.&lt;clinit&gt; of the 1.20.6 client): every vanilla enchantment as 1.20.6 built
 * it -- same class, same definition, same extra arguments, same registration order and name -- made of
 * this step's facades, and registered in CbEnchantments' stand-in for 1.20.6's
 * BuiltInRegistries.ENCHANTMENT. A 1.20.6 mod's read of Enchantments.X is wired to the getter of the
 * same name. Do not edit; regenerate.
 */
public final class CbEnchVanilla {{

    /** 1.20.6's Enchantments.ARMOR_SLOTS */
    private static final net.minecraft.class_1304[] ARMOR_SLOTS = {armor};

{chr(10).join(f"    private static final class_1887 {f};" for f in fields)}

    static {{
{chr(10).join(lines)}
    }}

    private static class_1887 reg(String name, class_1887 enchantment) {{
        return CbEnchantments.registerVanilla(name, enchantment);
    }}

    /** forces the registration of every vanilla enchantment, in 1.20.6's order */
    static void bootstrap() {{
    }}

{chr(10).join(f"    public static class_1887 {f}() {{{chr(10)}        return {f};{chr(10)}    }}{chr(10)}" for f in fields)}
    private CbEnchVanilla() {{
    }}
}}
'''
    with open(path, "w", encoding="utf-8", newline="\n") as o:
        o.write(body)
    print(f"{len(fields)} vanilla enchantments -> {path}")


def write_fragment(frag, entries):
    data = {}
    if os.path.exists(frag):
        data = json.load(open(frag, encoding="utf-8"))
    getters = {f"{ENCHANTMENTS}.{f}:Lnet/minecraft/class_1887;": RT for n, f, e in entries if n != "ARMOR_SLOTS"}
    data["fieldGetters"] = dict(sorted({**{k: v for k, v in data.get("fieldGetters", {}).items()
                                          if not k.startswith(ENCHANTMENTS + ".")}, **getters}.items()))
    with open(frag, "w", encoding="utf-8", newline="\n") as o:
        json.dump(data, o, indent=1)
        o.write("\n")
    print(f"{len(getters)} fieldGetters -> {frag}")


if __name__ == "__main__":
    main()
