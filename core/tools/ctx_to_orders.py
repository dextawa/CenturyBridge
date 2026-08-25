"""Turn a tree-context prune list (retry-context-<ver>.json: symbol +
compileErrors) back into re-forgeable orders.

prune_failed records only the symbol; the order that produced the bridge
lives in one of the segment order files. Join them, attach the compiler
errors as the retry feedback, and emit records in the shape retry.py eats.
Symbols from a mojang-named tree are matched through the 1.21.11 table.

  python3 ctx_to_orders.py <retry-context.json> <out-orders.json>
"""
import glob, io, json, re, sys
sys.path.insert(0, "core/tools")
import forge

ctx_path, out_path = sys.argv[1], sys.argv[2]
ctx = json.load(io.open(ctx_path, encoding="utf-8"))

cmap, mmap, conf = {}, {}, {}
for line in io.open("data/mappings/inter-to-mojang-1.21.11.tsv", encoding="utf-8"):
    k, i, m = line.rstrip("\n").split("\t")
    if k == "C":
        cmap[i] = m
        continue
    tok, name = i.rsplit(".", 1)[1], m.rsplit(".", 1)[1]
    if "$" in name or not re.fullmatch(r"(?:method|field|comp)_\d+", tok):
        continue
    if tok in mmap and mmap[tok] != name:
        conf[tok] = 1
        continue
    mmap[tok] = name
for t in conf:
    mmap.pop(t, None)

def fwd(s):
    s = re.sub(r"net/minecraft/(class_\d+(?:\$class_\d+)*)", lambda mo: cmap.get(mo.group(0), mo.group(0)), s)
    return re.sub(r"\b((?:method|field|comp)_\d+)\b", lambda mo: mmap.get(mo.group(1), mo.group(1)), s)

def id_to_symbol(oid):                      # net.minecraft.class_1058#method_4570(D)F -> slash form
    owner, member = oid.split("#", 1)
    return owner.replace(".", "/") + "." + member

rev_outer = {v: k for k, v in cmap.items() if "$" not in k}
def repair(sym):                            # undo the dotted-garbage corruption of an old translator run
    sym = re.sub(r"net/minecraft/net\.minecraft\.([A-Za-z0-9_.]+)", lambda m: "net/minecraft/" + m.group(1).replace(".", "/"), sym)
    def inner(m):                            # Outer$class_NNN left behind by the bare pass -> mapped inner
        io_ = rev_outer.get(m.group(1))
        full = cmap.get((io_ or "") + m.group(2))
        return full if full else m.group(0)
    return re.sub(r"(net/minecraft/[A-Za-z0-9_/]+)((?:\$class_\d+)+)", inner, sym)

index = {}
for f in sorted(glob.glob("core/out-diff/orders-*.json")):
    for o in json.load(io.open(f, encoding="utf-8")):
        sym = id_to_symbol(o["id"])
        index.setdefault(sym, o)
        index.setdefault(fwd(sym), o)

out, missed = [], []
for c in ctx:
    sym = repair(c["symbol"])
    o = index.get(sym) or index.get(fwd(sym))
    if o is None:
        missed.append(c["symbol"])
        continue
    rec = dict(o)
    rec["compileError"] = "\n".join(c.get("compileErrors", []))
    rec["body"] = ""
    rec["javaSig"] = forge.java_signature(rec)   # verify/assemble read the signature off the record
    out.append(rec)
json.dump(out, io.open(out_path, "w", encoding="utf-8"), indent=1, ensure_ascii=False)
print(f"{ctx_path}: {len(ctx)} symbols -> {len(out)} orders, {len(missed)} unmatched")
for m in missed[:6]:
    print("   miss", m)
