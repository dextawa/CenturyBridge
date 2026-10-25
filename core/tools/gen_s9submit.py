"""1.21.8 -> 1.21.9 render submission: renderer methods that drew into a MultiBufferSource and now submit to a
SubmitNodeCollector (entity renderers, render layers, special model renderers, item / block helpers ...).

For every damage row whose member takes a MultiBufferSource in V and that W keeps under the same name with
  - the MultiBufferSource replaced by a SubmitNodeCollector,
  - optionally a CameraRenderState added (fed from the same buffers),
  - optionally the int light right after the buffers dropped (when an EntityRenderState argument carries it),
  - optionally trailing ints added (the outline colour: 0, EntityRenderState.NO_OUTLINE, as 1.21.8 drew no outline
    through these calls),
  - every other argument the same, in order,
this writes
  - callReshapes on the row's owner (CbSubmit.collector / camera: a capture's own, else an immediate collector over
    the caller's buffers);
  - an overrideHooks entry in bridge mode on the topmost V owner among the rows naming the member: 1.21.9's submit
    hands the old override a CbSubmit.Capture over its collector, flushed when the override returns;
  - the layer source rt/CbSubmitBridges.java (the adapter interfaces and bridges, deterministic templates).
Everything else is reported (design).

  python3 core/tools/gen_s9submit.py <damage.tsv> <V stub> <W stub> <out fragment.json> <out CbSubmitBridges.java>
  (core/segments/damage/1.21.8__1.21.9.damage.tsv data/jars/client-1.21.8-intermediary.jar data/jars/client-1.21.9-intermediary.jar
   core/segments/steps/1.21.8__1.21.9.json.d/s9-submit-gen.json
   core/shims/steps/1.21.9/src/top/dext/centurybridge/step/v1_21_9/rt/CbSubmitBridges.java)
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402
from classfile import ACC_FINAL, ACC_PRIVATE, ACC_STATIC, split_args  # noqa: E402

MBS = "Lnet/minecraft/class_4597;"
COL = "Lnet/minecraft/class_11659;"
CAM = "Lnet/minecraft/class_12075;"
ERS = "net/minecraft/class_10017"
PKG = "top/dext/centurybridge/step/v1_21_9/rt/"
RT = PKG + "CbSubmit"
BR = PKG + "CbSubmitBridges"
GUARD = PKG + "Guard"


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and (n.startswith("net/minecraft/") or n.startswith("com/mojang/")):
                k = classfile.read(z.read(n))
                if k:
                    out[k.name] = k
    return out


def supers(classes, c):
    out, todo = [], [c]
    while todo:
        x = todo.pop(0)
        if x is None or x in out:
            continue
        out.append(x)
        k = classes.get(x)
        if k:
            todo.append(k.super)
            todo.extend(k.interfaces)
    return out


def find(classes, owner, name, desc=None):
    out = []
    for s in supers(classes, owner):
        k = classes.get(s)
        if k:
            for m in k.methods:
                if m.name == name and (desc is None or m.desc == desc):
                    out.append((s, m))
    return out


def is_state(classes, t):
    return t.startswith("L") and ERS in supers(classes, t[1:-1])


def plan(V, W, va, wa):
    """ops (call side) and the bridge's recipe, or None: walk V's and W's arguments together"""
    i = j = 0
    ops, recipe = [], []  # recipe: per old argument, ("w", j) | ("cap",) | ("light", stateW)
    mbs_old = None
    state_w = next((k for k, t in enumerate(wa) if is_state(W, t)), None)
    col_w = cam_w = None
    while i < len(va) or j < len(wa):
        if i < len(va) and va[i] == MBS:
            if j >= len(wa) or wa[j] != COL:
                return None
            mbs_old, col_w = i, j
            ops.append(f"conv:{i} {RT}#collector({MBS}){COL}")
            recipe.append(("cap",))
            i += 1
            j += 1
            if j < len(wa) and wa[j] == CAM:
                ops.append(f"conv:{mbs_old} {RT}#camera({MBS}){CAM}")
                cam_w = j
                j += 1
            if i < len(va) and va[i] == "I" and (j >= len(wa) or wa[j] != "I"):
                if state_w is None:
                    return None
                recipe.append(("light", state_w))
                i += 1
            continue
        if j < len(wa) and wa[j] == CAM and cam_w is None and mbs_old is not None:
            ops.append(f"conv:{mbs_old} {RT}#camera({MBS}){CAM}")
            cam_w = j
            j += 1
            continue
        if i < len(va) and j < len(wa) and va[i] == wa[j]:
            ops.append(f"keep:{i}")
            recipe.append(("w", j))
            i += 1
            j += 1
            continue
        if i >= len(va) and all(t == "I" for t in wa[j:]) and mbs_old is not None:
            for _ in wa[j:]:
                ops.append(f"push {RT}#noOutline()I")
            j = len(wa)
            continue
        return None
    if mbs_old is None:
        return None
    return ops, recipe, col_w, cam_w


def jtype(d):
    dims = 0
    while d.startswith("["):
        dims += 1
        d = d[1:]
    prim = {"I": "int", "F": "float", "D": "double", "J": "long", "Z": "boolean", "B": "byte", "S": "short", "C": "char", "V": "void"}
    t = prim.get(d) or d[1:-1].replace("/", ".").replace("$", ".")
    return t + "[]" * dims


def main():
    dmg, vjar, wjar, out, java = sys.argv[1:6]
    V, W = load(vjar), load(wjar)
    members = {}
    for line in open(dmg, encoding="utf-8"):
        p = line.rstrip("\n").split("\t")
        if p[0] == "usage" or "(" not in p[3] or MBS not in p[3] or p[2] == "<init>":
            continue
        members.setdefault((p[2], p[3]), set()).add(p[1])
    frag = {"callReshapes": {}, "overrideHooks": []}
    src = []
    report = []
    adapters = set()
    for (name, desc), owners in sorted(members.items()):
        va, vr = split_args(desc)
        live = {}
        for o in sorted(owners):
            cands = [(s, m) for s, m in find(W, o, name) if m.desc != desc]
            fits = []
            for s, m in cands:
                wa, wr = split_args(m.desc)
                if wr != vr:
                    continue
                pl = plan(V, W, va, wa)
                if pl:
                    fits.append((m.desc, pl, s, m))
            descs = {f[0] for f in fits}
            if len(descs) != 1:
                report.append(f"{o}.{name}{desc}: {'no' if not descs else 'several'} 1.21.9 shape(s) by the submit rules")
                continue
            live[o] = fits[0]
        if not live:
            continue
        for o, (wdesc, (ops, recipe, col_w, cam_w), wdecl, wm) in sorted(live.items()):
            frag["callReshapes"][f"{o}.{name}{desc}"] = {
                "desc": wdesc, "ops": ops,
                "basis": "s9 submit: 1.21.9 submits to a SubmitNodeCollector where 1.21.8 drew into a MultiBufferSource (CbSubmit: a capture's collector, else an immediate one over the caller's buffers)"}
        vown = sorted(live)
        vm = find(V, vown[0], name, desc)
        if not vm or vm[0][1].access & (ACC_STATIC | ACC_PRIVATE):
            continue
        tops = [o for o in vown if not any(s in vown for s in supers(V, o)[1:])]
        wdesc, (ops, recipe, col_w, cam_w), wdecl, wm = live[tops[0]]
        if wm.access & ACC_FINAL:
            report.append(f"{name}{desc}: 1.21.9's {name}{wdesc} is final, no hook")
            continue
        wa, wr = split_args(wdesc)
        adapter = f"Legacy_{tops[0].split('/')[-1].replace('$', '_')}_{name}"
        if adapter in adapters:
            continue
        adapters.add(adapter)
        params = ", ".join(f"{jtype(t)} a{k}" for k, t in enumerate(wa))
        old_params = ", ".join(f"{jtype(t)} p{k}" for k, t in enumerate(va))
        args = []
        for r in recipe:
            if r[0] == "w":
                args.append(f"a{r[1]}")
            elif r[0] == "cap":
                args.append("cap")
            else:
                args.append(f"{RT.replace('/', '.')}.light(a{r[1]})")
        ret = "" if vr == "V" else "return "
        cam = f"a{cam_w}" if cam_w is not None else "null"
        src.append(f"""    /** {tops[0]}.{name}{desc} */
    public interface {adapter} {{
        {jtype(vr)} {name}({old_params});
    }}

    public static {jtype(vr)} cb_{adapter}({adapter} self{', ' if params else ''}{params}) {{
        CbSubmit.Capture cap = CbSubmit.capture(a{col_w}, {cam});
        try {{
            {ret}self.{name}({', '.join(args)});
        }} finally {{
            cap.flush();
        }}
    }}
""")
        frag["overrideHooks"].append({
            "old": name + desc, "new": name + wdesc, "onlyUnder": tops[0],
            "bridge": f"{BR}.cb_{adapter}(L{BR}${adapter};{''.join(wa)}){wr}", "adapter": f"{BR}${adapter}",
            "guard": GUARD,
            "basis": "s9 submit: 1.21.9 calls the submitting shape; the old override draws into a CbSubmit.Capture over the collector, submitted when it returns (its super call is reshaped back to the submitting shape with the capture's collector)"})
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        json.dump(frag, f, indent=1, sort_keys=True)
        f.write("\n")
    with open(java, "w", encoding="utf-8", newline="\n") as f:
        f.write("""package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Generated by core/tools/gen_s9submit.py -- do not edit. The override bridges of 1.21.8 renderer methods that
 * drew into a MultiBufferSource: 1.21.9's submitting shape hands the old method a CbSubmit.Capture over its
 * collector and submits what it drew when it returns.
 */
@Environment(EnvType.CLIENT)
public final class CbSubmitBridges {

    private CbSubmitBridges() {
    }

""")
        f.write("\n".join(src))
        f.write("}\n")
    print(f"{len(frag['callReshapes'])} callReshapes, {len(frag['overrideHooks'])} overrideHooks -> {out}")
    for r in report:
        print("  design: " + r)


if __name__ == "__main__":
    main()
