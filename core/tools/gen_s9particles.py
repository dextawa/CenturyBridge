"""1.21.8 -> 1.21.9: ParticleProvider.createParticle(options, level, x, y, z, dx, dy, dz) gained the RandomSource the
particle draws from (1.21.9's ParticleEngine passes its own; 1.21.8's providers drew from level.random).

For every damage row of a provider's createParticle whose W shape is V's with a trailing RandomSource:
  - callReshapes on the row's owner: the old arguments, then the level's random (CbParticles.random(level)),
    1.21.8's source for what the provider drew;
  - frameDefaults for the matching override hooks (mechanical.json hooks the old shape onto the new one and passes
    vanilla's random to a super call through frames): a mod's own call of its old createParticle has no frame, and its
    super call gets the client level's random (CbParticles.frameRandom), again 1.21.8's source.
Rows whose first argument's type changed too (the options of FLASH, DRAGON_BREATH, INSTANT_EFFECT ...) are listed
and left to the particle-options wiring.

Also, from V's and W's bytecode, for the particle render bridge (rt/CbParticles):
  - superRedirects of getRenderType() on every V class declaring it that W keeps: a 1.21.8 override's
    super.getRenderType() is the vanilla superclass's 1.21.8 type (CbParticles.renderTypeOf; the generated
    rt/CbParticleSupers gives one overload per declaring class, the receiver typed as the key names it);
  - callReshapes of render(VertexConsumer, camera, partialTick) and renderRotatedQuad(VertexConsumer, camera,
    rotation, partialTick) on every V class declaring them whose W counterpart takes a QuadParticleRenderState:
    the consumer becomes a state drawing into it (CbParticles.immediate), mods' super calls included.

  python3 core/tools/gen_s9particles.py <damage.tsv> <V code jar> <W stub jar> <out fragment.json> <out CbParticleSupers.java>
  (core/segments/damage/1.21.8__1.21.9.damage.tsv data/jars/client-1.21.8-intermediary-code.jar
   data/jars/client-1.21.9-intermediary.jar core/segments/steps/1.21.8__1.21.9.json.d/s9-particles-gen.json
   core/shims/steps/1.21.9/src/top/dext/centurybridge/step/v1_21_9/rt/CbParticleSupers.java)
"""
import json
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

RT = "top/dext/centurybridge/step/v1_21_9/rt/CbParticles"
RS = "Lnet/minecraft/class_5819;"
VC = "Lnet/minecraft/class_4588;"
STATE = "Lnet/minecraft/class_11944;"
CAM = "Lnet/minecraft/class_4184;"
QUAT = "Lorg/joml/Quaternionf;"
TYPE = "()Lnet/minecraft/class_3999;"


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith("net/minecraft/"):
                k = classfile.read(z.read(n))
                if k:
                    out[k.name] = k
    return out


def declares(k, name, desc, concrete=True):
    return any(m.name == name and m.desc == desc and (not concrete or not m.access & 0x400) for m in k.methods)


def main():
    dmg, vjar, wjar, out, supers_java = sys.argv[1:6]
    V, W = load(vjar), load(wjar)
    calls, shapes, skipped = {}, set(), []
    for line in open(dmg, encoding="utf-8"):
        p = line.rstrip("\n").split("\t")
        if len(p) < 6 or p[0] == "usage":
            continue
        usage, owner, name, desc, w = p[0], p[1], p[2], p[3], p[5]
        if not re.search(r";DDDDDD\)L", desc):
            continue
        m = re.match(r"method (\S+?)(\(.*?\)\S+) ", w + " ")
        if not m or m.group(1) != name:
            continue
        wdesc = m.group(2)
        vargs, vret = desc[1:desc.index(")")], desc[desc.index(")") + 1:]
        if wdesc != "(" + vargs + RS + ")" + vret:
            skipped.append(f"{usage} {owner}.{name}{desc} -> {wdesc}")
            continue
        if usage == "call":
            calls[f"{owner}.{name}{desc}"] = wdesc
        if usage == "override+super":
            shapes.add(name + desc)
    frag = {"callReshapes": {}, "frameDefaults": {}}
    for key, wdesc in sorted(calls.items()):
        frag["callReshapes"][key] = {
            "desc": wdesc,
            "ops": [f"keep:{i}" for i in range(8)] + ["conv:1 " + RT + "#random(Lnet/minecraft/class_638;)" + RS],
            "basis": "s9 particles: createParticle takes the RandomSource the particle draws from; 1.21.8's providers drew from level.random"}
    for shape in sorted(shapes):
        frag["frameDefaults"][shape] = {"8": RT + "#frameRandom()" + RS}
    frag["superRedirects"] = {}
    sup = []
    for name in sorted(V):
        if declares(V[name], "method_18122", TYPE) and name in W:
            frag["superRedirects"][f"{name}.method_18122{TYPE}"] = RT.replace("CbParticles", "CbParticleSupers")
            sup.append(name)
    for vname, vdesc, wdesc in [
            ("method_3074", "(" + VC + CAM + "F)V", "(" + STATE + CAM + "F)V"),
            ("method_60373", "(" + VC + CAM + QUAT + "F)V", "(" + STATE + CAM + QUAT + "F)V")]:
        nargs = len(re.findall(r"L[^;]+;|[FIDJZ]", vdesc[1:vdesc.index(")")]))
        for name in sorted(V):
            if declares(V[name], vname, vdesc) and name in W and declares(W[name], vname, wdesc, False):
                frag["callReshapes"][f"{name}.{vname}{vdesc}"] = {
                    "desc": wdesc,
                    "ops": ["conv " + RT + "#immediate(" + VC + ")" + STATE] + ["keep"] * (nargs - 1),
                    "basis": "s9 particles: 1.21.9's quad code writes to a QuadParticleRenderState; a 1.21.8 caller's "
                             "consumer becomes one drawing into it (CbParticles.immediate)"}
    with open(supers_java, "w", encoding="utf-8", newline="\n") as f:
        f.write("package top.dext.centurybridge.step.v1_21_9.rt;\n\n")
        f.write("// generated by core/tools/gen_s9particles.py -- regenerate, never hand-edit\n\n")
        f.write("/** super.getRenderType() from a 1.21.8 particle subclass, one overload per vanilla class declaring it "
                "(CbParticles.renderTypeOf) */\n")
        f.write("@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)\npublic final class CbParticleSupers {\n")
        f.write("    private CbParticleSupers() {\n    }\n")
        for name in sup:
            jn = name.replace("/", ".").replace("$", ".")
            f.write(f"\n    public static net.minecraft.class_3999 method_18122({jn} p) {{\n"
                    f"        return CbParticles.renderTypeOf(p);\n    }}\n")
        f.write("}\n")
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        json.dump(frag, f, indent=1, sort_keys=True)
        f.write("\n")
    print(f"{len(frag['callReshapes'])} callReshapes, {len(frag['frameDefaults'])} frameDefaults, "
          f"{len(frag['superRedirects'])} superRedirects -> {out}")
    for s in skipped:
        print("  skipped " + s)


if __name__ == "__main__":
    main()
