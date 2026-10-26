"""gen_bsd_targets.py: the BlockStateData (class_3580) wiring of step 1.21.4 -> 1.21.5 -- mixinTargets for the table chunks
1.21.5 renamed (paired by their register-id sequence, which must be identical) and the register reshape.

    python3 core/tools/gen_bsd_targets.py <V code jar> <W code jar> <out fragment>

A V chunk pairs with the W method whose sequence of register ids (the int pushed before each register call) is the
same, in the same order; chunks that keep their name are left alone, a V chunk spread over several W methods (1.21.5
split method_31451 into method_68179 + method_68180) has no single target and is reported, not wired.
"""
import collections, json, re, subprocess, sys

OWNER = "net/minecraft/class_3580"
REGISTER_V = "method_15596(ILjava/lang/String;[Ljava/lang/String;)V"
REGISTER_W = "(ILcom/mojang/serialization/Dynamic;[Lcom/mojang/serialization/Dynamic;)V"
RT = "top/dext/centurybridge/step/v1_21_5/rt/CbDataFixBlockStates"


def chunks(jar):
    out = subprocess.run(["javap", "-c", "-p", "-cp", jar, OWNER.replace("/", ".")], capture_output=True, text=True,
                         encoding="utf-8", errors="replace").stdout
    cur, ids, want = None, collections.defaultdict(list), False
    for line in out.splitlines():
        h = re.match(r"^  \S.*\b(method_\d+)\(\);$", line)
        if h:
            cur, want = h.group(1), True
            continue
        if re.match(r"^  \S", line):
            cur = None
            continue
        if cur is None:
            continue
        if want:
            v = re.search(r"(?:sipush|bipush)\s+(-?\d+)|iconst_(\d)|ldc(?:_w)?\s+#\d+\s+// int (\d+)", line)
            if v:
                ids[cur].append(int(next(g for g in v.groups() if g is not None)))
                want = False
                continue
        if re.search(r"invokestatic .*Method method_15596:", line):
            want = True
    # the push after the last register call is not a register id
    return {k: tuple(v) for k, v in ids.items() if len(v) > 1}


def main():
    vjar, wjar, out = sys.argv[1:4]
    v, w = chunks(vjar), chunks(wjar)
    by_seq = {s: k for k, s in w.items()}
    targets = {}
    for k, s in sorted(v.items()):
        if k in w and w[k] == s:
            continue
        to = by_seq.get(s)
        if to is None:
            print(f"{k}: no W method holds its {len(s) - 1} register calls alone (split): not wired", file=sys.stderr)
            continue
        targets[f"{OWNER}.{k}()V"] = {
            "to": f"{to}()V",
            "args": [],
            "basis": f"BlockStateData table chunk: 1.21.5 renamed {k} -> {to}; the same {len(s) - 1} register calls with "
                     f"the same ids {s[0]}..{s[-2]} in the same order (javap V/W), called from <clinit> where V called it",
        }
    frag = {
        "_bsd_generated": "core/tools/gen_bsd_targets.py <V code jar> <W code jar> <this>; regenerate, never hand-edit",
        "mixinTargets": targets,
        "callReshapes": {
            f"{OWNER}.{REGISTER_V}": {
                "basis": "BlockStateData.register(id, newState, oldStates...): 1.21.5 takes the states as Dynamic values "
                         "(built from name + properties by method_68177 / method_68178) where 1.21.4 took SNBT strings and "
                         "parsed each with parse(String) first (javap V method_15596); the strings go through 1.21.4's parse",
                "desc": REGISTER_W,
                "ops": [
                    "keep:0",
                    f"conv:1 {RT}#parse(Ljava/lang/String;)Lcom/mojang/serialization/Dynamic;",
                    f"conv:2 {RT}#parseAll([Ljava/lang/String;)[Lcom/mojang/serialization/Dynamic;",
                ],
            }
        },
        "staticRedirects": {
            f"{OWNER}.method_15598(Ljava/lang/String;)Lcom/mojang/serialization/Dynamic;": f"{RT}#parse",
        },
    }
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        json.dump(frag, f, indent=1)
        f.write("\n")
    print(f"{len(targets)} mixinTargets -> {out}")


if __name__ == "__main__":
    main()
