"""Carry a shim tree across the namespace wall: intermediary -> mojang, in source.

Same table the jar remapper uses, same consequence: symbols that died before
1.21.11 are absent from the table and keep their intermediary names -- which
is exactly right, because the renamed mods' residual call sites kept them too.
Bridge declarations whose old name survived to 1.21.11 are renamed in lockstep
with the call sites that reach them.

Two carve-outs make the flat-namespace scheme survive real names:
  - synthetic map targets (lambda$..., $values) are unusable in source: skipped
  - rt classes are addressed through the wiring, not by call-site name
    matching, and real names collide wildly (72 fields all map to CODEC) --
    their DECLARED names keep the intermediary token, and the wiring grows an
    explicit "class#member" target for every key whose member was renamed

  python3 translate_tree.py <table.tsv> <src-tree> <dst-tree> <old-ver> <new-ver>
       e.g. ... core/shims/1.21.11 core/shims/26.2 1.21.11 26.2
"""
import io, os, re, shutil, sys

table, src_root, dst_root, oldv, newv = sys.argv[1:6]
oldpkg = "v" + oldv.replace(".", "_")
newpkg = "v" + newv.replace(".", "_")

cmap = {}                    # slash+$ form: net/minecraft/class_1297 -> net/minecraft/world/entity/Entity
mmap, conflicts = {}, {}     # bare member token -> mojang simple name
for line in io.open(table, encoding="utf-8"):
    kind, inter, moj = line.rstrip("\n").split("\t")
    if kind == "C":
        cmap[inter] = moj
        continue
    tok = inter.rsplit(".", 1)[1]
    name = moj.rsplit(".", 1)[1]
    if "$" in name or name in ("<init>", "<clinit>"):
        continue
    if not re.fullmatch(r"(?:method|field|comp)_\d+", tok):
        continue
    if tok in mmap and mmap[tok] != name:
        conflicts.setdefault(tok, set()).update((mmap[tok], name))
        continue
    mmap[tok] = name
for tok in conflicts:
    mmap.pop(tok, None)
print(f"table: {len(cmap)} classes, {len(mmap)} member tokens, {len(conflicts)} conflicted tokens dropped")

def map_chain(chain):        # 'class_12079.class_12081' -> dotted mojang or None
    return (cmap.get("net/minecraft/" + chain.replace(".", "$")) or "").replace("/", ".").replace("$", ".") or None

CHAIN = r"class_\d+(?:\.class_\d+)*"
DECL = re.compile(r"(public static (?:final )?[^=(\n]*?)\b((?:method|field|comp)_\d+)\b(\s*[(=])")
SENTL = "\x00"

def xlate_java(s, protect_decls=False):
    if protect_decls:
        s = DECL.sub(lambda m: m.group(1) + SENTL + m.group(2) + SENTL + m.group(3), s)
    s = re.sub(r"^import\s+net\.minecraft\.[\w.]+;\s*\n", "", s, flags=re.M)
    # slash-form names live inside string literals: descriptors, @At targets,
    # reflection lookups. They map in slash form, and must be consumed before
    # the bare pass below or that pass turns them into dotted garbage.
    s = re.sub(r"net/minecraft/(class_\d+(?:\$class_\d+)*)",
               lambda m: cmap.get(m.group(0), m.group(0)), s)
    def dotted(m):
        full = map_chain(m.group(1))
        return full if full else m.group(0)
    s = re.sub(r"net\.minecraft\.(" + CHAIN + r")", dotted, s)
    def bare(m):
        full = map_chain(m.group(0))
        return full if full else m.group(0)
    s = re.sub(r"(?<![\w.$/])" + CHAIN, bare, s)
    s = re.sub(r"(?<!\x00)\b((?:method|field|comp)_\d+)\b(?!\x00)",
               lambda m: mmap.get(m.group(1), m.group(1)), s)
    s = s.replace(SENTL, "")
    return s.replace(oldpkg, newpkg).replace(oldv, newv)

def xlate_slashy(s):         # wiring JSON / accesswidener text: slash-form names + descriptors
    s = re.sub(r"net/minecraft/(class_\d+(?:\$class_\d+)*)",
               lambda m: cmap.get(m.group(0), m.group(0)), s)
    s = re.sub(r"\b((?:method|field|comp)_\d+)\b", lambda m: mmap.get(m.group(1), m.group(1)), s)
    return s.replace(oldpkg, newpkg).replace(oldv, newv)

if os.path.exists(dst_root):
    shutil.rmtree(dst_root)
for root, dirs, files in os.walk(src_root):
    rel = os.path.relpath(root, src_root).replace(oldpkg, newpkg)
    out_dir = os.path.join(dst_root, rel)
    os.makedirs(out_dir, exist_ok=True)
    for f in files:
        text = io.open(os.path.join(root, f), encoding="utf-8").read()
        if f.endswith(".java"):
            in_rt = (os.sep + "rt" + os.sep) in root or root.endswith(os.sep + "rt")
            text = xlate_java(text, protect_decls=in_rt)
        elif f.endswith(".accesswidener"):
            text = xlate_slashy(text).replace("accessWidener v2 intermediary",
                                              "accessWidener v2 official")
        else:
            text = xlate_slashy(text)
        io.open(os.path.join(out_dir, f.replace(oldv, newv)), "w",
                encoding="utf-8", newline="\n").write(text)
print(f"tree: {src_root} -> {dst_root}")
