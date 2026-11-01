"""1.21.8 -> 1.21.9: particle constructors whose 1.21.9 shape is 1.21.8's plus a trailing TextureAtlasSprite
(SingleQuadParticle takes its sprite at construction; 1.21.8's started without one until pickSprite / setSprite).

For every constructor row of the public and access-widener damage tables whose W shape is exactly that, and whose
W body hands the new argument only to the superclass constructor or SingleQuadParticle's sprite field (checked on
W's bytecode; anything else is listed and left out), a superCtorReshapes entry: the old arguments, then
CbParticles.noSprite() -- 1.21.8's "no sprite yet". It serves a mod's super(..) and a mod's own `new` of a vanilla
particle its access widener opened alike. Keys some other fragment already wires are skipped.

  python3 core/tools/gen_s9k_sprite_ctors.py <damage.tsv> <damage-aw.tsv> <W code jar> <fragment.json.d dir> <out.json>
"""
import glob
import json
import os
import re
import subprocess
import sys

SPRITE = "Lnet/minecraft/class_1058;"
PUSH = "push top/dext/centurybridge/step/v1_21_9/rt/CbParticles#noSprite()" + SPRITE
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402


def rows(tables):
    seen = {}
    for t in tables:
        for line in open(t, encoding="utf-8"):
            c = line.rstrip("\n").split("\t")
            if len(c) < 6 or c[2] != "<init>":
                continue
            want = "(" + c[3][1:c[3].index(")")] + SPRITE + ")V"
            if re.search(r"(^|\s)" + re.escape(want) + r"(\s|$)", c[5]):
                seen.setdefault(c[1] + ".<init>" + c[3], (c[1], want))
    return seen


def only_passed_on(jar, owner, wdesc):
    """W's constructor reads the sprite slot only right before invokespecial <init> / putfield field_62632"""
    out = subprocess.run(["javap", "-p", "-c", "-s", "-cp", jar, owner.replace("/", ".")],
                         capture_output=True, text=True).stdout.splitlines()
    args, _ = classfile.split_args(wdesc)
    slot = 1 + sum(2 if a in ("J", "D") else 1 for a in args[:-1])
    on, ok, seen = False, True, False
    for i, ln in enumerate(out):
        if "descriptor:" in ln:
            on = ln.split()[-1] == wdesc and "(" in out[i - 1] and owner.split("/")[-1].replace("$", ".") in out[i - 1].replace("$", ".")
        elif on and re.search(r"aload(_| +)%d$" % slot, ln.strip()):
            seen = True
            nxt = next((x for x in out[i + 1:i + 4] if re.search(r"invoke|putfield|areturn|aload|getfield", x)), "")
            if not ("<init>" in nxt and "invokespecial" in nxt) and "field_62632" not in nxt:
                ok = False
    return ok and seen


def main():
    dmg, dmg_aw, wjar, dird, out = sys.argv[1:6]
    wired = set()
    for f in glob.glob(os.path.join(dird, "*.json")):
        if os.path.abspath(f) == os.path.abspath(out):
            continue
        d = json.load(open(f, encoding="utf-8"))
        for sec in ("superCtorReshapes", "ctorRedirects", "superCtorAdapts"):
            wired.update((d.get(sec) or {}).keys())
    frag, left = {}, []
    for key, (owner, want) in sorted(rows([dmg, dmg_aw]).items()):
        if key in wired:
            continue
        if not only_passed_on(wjar, owner, want):
            left.append(key)
            continue
        n = len(classfile.split_args(want)[0]) - 1
        frag[key] = {"desc": want, "ops": [f"keep:{i}" for i in range(n)] + [PUSH],
                     "basis": "s9k particles: 1.21.9's constructor takes the sprite (handed on to SingleQuadParticle); "
                              "1.21.8's started without one until pickSprite / setSprite"}
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        json.dump({"superCtorReshapes": frag}, f, indent=1, sort_keys=True)
        f.write("\n")
    print(f"{len(frag)} superCtorReshapes -> {out}")
    for k in left:
        print("  left (sprite used beyond the super call): " + k)


if __name__ == "__main__":
    main()
