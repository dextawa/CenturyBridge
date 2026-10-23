"""gen_s6fin_loot.py: the facades of 1.20.6's vanilla loot packs that 1.21 made final records (climb step 1.20.6 -> 1.21).

  python gen_s6fin_loot.py <V stub: client-1.20.6-intermediary.jar> <W stub: client-1.21-intermediary.jar>

1.20.6's VanillaChestLoot, TradeRebalanceChestLoot, VanillaFishingLoot, VanillaGiftLoot, VanillaPiglinBarterLoot,
VanillaArchaeologyLoot, VanillaEquipmentLoot and VanillaShearingLoot were plain classes: built with no arguments,
open to subclassing, generate(registries, consumer), their table builders public statics. 1.21 made each a final
record of the registries (new VanillaChestLoot(registries)), generate(consumer), the builders instance members.
A 1.20.6 mod's references to such a class are renamed (classRenames) to a facade of the same name in the layer:
a CbLoot.LegacyPack (built on the registries of the run, generating through 1.21's record on them), with every
public member 1.20.6's class had -- a static builder answers 1.21's instance builder of a record on CbLoot's
registries, a static 1.21 kept static is forwarded. The member lists are read off both stubs; a 1.20.6 member
this cannot place fails the generator.

Writes facade/class_NNNN.java and core/segments/steps/1.20.6__1.21.json.d/s6fin-loot-gen.json.
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402
from classfile import ACC_PUBLIC, ACC_STATIC  # noqa: E402

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
FAC = "top/dext/centurybridge/step/v1_21/facade"
FAC_SRC = os.path.join(ROOT, "core/shims/steps/1.21/src", FAC)
FRAGMENT = os.path.join(ROOT, "core/segments/steps/1.20.6__1.21.json.d/s6fin-loot-gen.json")

# intermediary name -> Mojang name (for the comments)
PACKS = {
    "class_2432": "VanillaChestLoot",
    "class_8794": "TradeRebalanceChestLoot",
    "class_2437": "VanillaFishingLoot",
    "class_3714": "VanillaGiftLoot",
    "class_4845": "VanillaPiglinBarterLoot",
    "class_8255": "VanillaArchaeologyLoot",
    "class_9486": "VanillaEquipmentLoot",
    "class_9340": "VanillaShearingLoot",
}
LOOKUP = "Lnet/minecraft/class_7225$class_7874;"
GEN_V = "method_10399(" + LOOKUP + "Ljava/util/function/BiConsumer;)V"
GEN_W = "method_10399(Ljava/util/function/BiConsumer;)V"


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith("net/minecraft/") and n.endswith(".class"):
                c = classfile.read(z.read(n))
                if c:
                    out[c.name] = c
    return out


def java_type(d):
    """a field descriptor as Java source (nested classes with '.')"""
    dims = 0
    while d.startswith("["):
        dims += 1
        d = d[1:]
    prim = {"V": "void", "Z": "boolean", "I": "int", "F": "float", "J": "long", "D": "double", "B": "byte", "S": "short", "C": "char"}
    t = prim[d] if d in prim else d[1:-1].replace("/", ".").replace("$", ".")
    return t + "[]" * dims


def args_of(desc):
    return classfile.split_args(desc)[0]


def main():
    v, w = load(sys.argv[1]), load(sys.argv[2])
    frag = {"classRenames": {}}
    for cls, mojang in PACKS.items():
        owner = "net/minecraft/" + cls
        vc, wc = v[owner], w[owner]
        wm = {m.name + m.desc: m for m in wc.methods}
        wctor = "<init>(" + LOOKUP + ")V"
        assert wctor in wm, (cls, "1.21 has no (registries) constructor")
        assert GEN_W in wm, (cls, "1.21 has no generate(consumer)")
        body = []
        for m in vc.methods:
            if not (m.access & ACC_PUBLIC) or m.name.startswith("lambda$"):
                continue
            shape = m.name + m.desc
            if m.name == "<init>":
                assert m.desc == "()V", (cls, shape)
                continue
            if shape == GEN_V:
                continue  # CbLoot.LegacyPack's
            wmem = wm.get(shape)
            assert wmem is not None, (cls, shape, "no 1.21 member of that shape")
            ret = java_type(m.desc[m.desc.index(")") + 1:])
            params = args_of(m.desc)
            decl = ", ".join(java_type(p) + " a" + str(i) for i, p in enumerate(params))
            call = ", ".join("a" + str(i) for i in range(len(params)))
            target = ("net.minecraft." + cls + "." if wmem.access & ACC_STATIC
                      else "new net.minecraft." + cls + "(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries()).")
            how = "1.21 kept it static" if wmem.access & ACC_STATIC else "an instance member of 1.21's record, on CbLoot's registries"
            static = "static " if m.access & ACC_STATIC else ""
            assert m.access & ACC_STATIC, (cls, shape, "an instance member other than generate")
            body.append(f"    /** 1.20.6 {mojang}.{m.name}: {how} */\n"
                        f"    public {static}{ret} {m.name}({decl}) {{\n"
                        f"        {'' if ret == 'void' else 'return '}{target}{m.name}({call});\n"
                        f"    }}\n")
        src = (f"package top.dext.centurybridge.step.v1_21.facade;\n\n"
               f"/**\n * 1.20.6's {mojang} (net/minecraft/{cls}), which 1.21 made a final record of the registries: a 1.20.6\n"
               f" * mod's references are renamed here (classRenames). Built on the registries of the run (CbLoot), generating\n"
               f" * through 1.21's record on them; the table builders 1.20.6 had as statics answer 1.21's. Generated by\n"
               f" * core/tools/gen_s6fin_loot.py from both stubs.\n */\n"
               f"public class {cls} extends top.dext.centurybridge.step.v1_21.rt.CbLoot.LegacyPack {{\n\n"
               f"    public {cls}() {{\n    }}\n\n"
               f"    @Override\n"
               f"    protected net.minecraft.class_7791 cb$pack(net.minecraft.class_7225.class_7874 registries) {{\n"
               f"        return new net.minecraft.{cls}(registries);\n    }}\n"
               + ("\n" + "\n".join(body) if body else "")
               + "}\n")
        path = os.path.join(FAC_SRC, cls + ".java")
        with open(path, "w", encoding="utf-8", newline="\n") as o:
            o.write(src)
        print(path)
        frag["classRenames"][owner] = FAC + "/" + cls
    with open(FRAGMENT, "w", encoding="utf-8", newline="\n") as o:
        json.dump(frag, o, indent=1)
        o.write("\n")
    print(FRAGMENT, {k: len(x) for k, x in frag.items()})


if __name__ == "__main__":
    main()
