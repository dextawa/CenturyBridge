"""A segment's damage table: the per-segment probe's findings as frozen segment data.

probe_segments.sh measures, for one boundary V -> W, every way a mod written against V can
touch V's API and which of them W no longer links (probe_report.py maps each finding back to
its V member and usage). This turns that into the segment's damage table -- one row per V
member and usage that W breaks, owner-qualified, constructors included, with what W has in
its place -- so the engine can work from the whole diff rather than from name lists:

  usage  owner  name  desc  kinds  w (what W has: other descriptors of the name on the owner,
                                      the owner's constructors, the class's new shape, ...)

  python segment_damage.py <probe report.tsv> <W jar> <out.tsv> [<inter-to-mojang.tsv>] [--control <report.tsv>]
                           [--w-extra <jar|dir>...]
(the tsv translates V's intermediary owner and member names on the 1.21.11 -> 26.1 wall; --control is
probe_report.py's report of the control -- the same probe on V's own classpath -- and its sites are left
out: a site that does not link on V either is no damage of the step (Fabric API's datagen interfaces are
declared for loom's dev-time injection only, nothing gives them to the vanilla classes at runtime);
--w-extra adds W classes from other jars -- Fabric API's modules, for the Fabric API probe)
"""
import os
import struct
import sys
import zipfile

ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL = 0x1, 0x2, 0x4, 0x8, 0x10
ACC_INTERFACE, ACC_ABSTRACT, ACC_ENUM = 0x200, 0x400, 0x4000


def parse(b):
    """name, access, super, interfaces, fields [(name, desc, acc)], methods [(name, desc, acc)], sealed"""
    n = struct.unpack(">H", b[8:10])[0]
    i, idx = 10, 1
    utf8, cls = {}, {}
    while idx < n:
        tag = b[i]
        if tag == 1:
            ln = struct.unpack(">H", b[i + 1:i + 3])[0]
            utf8[idx] = b[i + 3:i + 3 + ln].decode("utf-8", "replace")
            i += 3 + ln
        elif tag in (3, 4):
            i += 5
        elif tag in (5, 6):
            i += 9
            idx += 1
        elif tag == 7:
            cls[idx] = struct.unpack(">H", b[i + 1:i + 3])[0]
            i += 3
        elif tag in (8, 16, 19, 20):
            i += 3
        elif tag in (9, 10, 11, 12, 17, 18):
            i += 5
        elif tag == 15:
            i += 4
        else:
            return None
        idx += 1
    acc = struct.unpack(">H", b[i:i + 2])[0]
    name = utf8.get(cls.get(struct.unpack(">H", b[i + 2:i + 4])[0]))
    sup_i = struct.unpack(">H", b[i + 4:i + 6])[0]
    sup = utf8.get(cls.get(sup_i)) if sup_i else None
    i += 6
    nif = struct.unpack(">H", b[i:i + 2])[0]
    itfs = [utf8.get(cls.get(struct.unpack(">H", b[i + 2 + 2 * k:i + 4 + 2 * k])[0])) for k in range(nif)]
    i += 2 + 2 * nif

    def members(i):
        cnt = struct.unpack(">H", b[i:i + 2])[0]
        i += 2
        out = []
        for _ in range(cnt):
            a = struct.unpack(">H", b[i:i + 2])[0]
            nm = utf8.get(struct.unpack(">H", b[i + 2:i + 4])[0])
            ds = utf8.get(struct.unpack(">H", b[i + 4:i + 6])[0])
            i += 6
            na = struct.unpack(">H", b[i:i + 2])[0]
            i += 2
            for _ in range(na):
                i += 6 + struct.unpack(">I", b[i + 2:i + 6])[0]
            out.append((nm, ds, a))
        return i, out

    i, fields = members(i)
    i, methods = members(i)
    sealed = False
    na = struct.unpack(">H", b[i:i + 2])[0]
    i += 2
    for _ in range(na):
        an = utf8.get(struct.unpack(">H", b[i:i + 2])[0])
        if an == "PermittedSubclasses":
            sealed = True
        i += 6 + struct.unpack(">I", b[i + 2:i + 6])[0]
    return name, acc, sup, itfs, fields, methods, sealed


def access(a):
    return "public" if a & ACC_PUBLIC else "protected" if a & ACC_PROTECTED else "private" if a & ACC_PRIVATE else "package"


def flags(a):
    out = [access(a)]
    if a & ACC_STATIC:
        out.append("static")
    if a & ACC_FINAL:
        out.append("final")
    if a & ACC_ABSTRACT:
        out.append("abstract")
    return " ".join(out)


def main():
    args = sys.argv[1:]
    extra = []
    if "--w-extra" in args:
        k = args.index("--w-extra")
        extra = args[k + 1:]
        args = args[:k]
    control = set()
    if "--control" in args:
        k = args.index("--control")
        with open(args[k + 1], encoding="utf-8") as f:
            next(f)
            control = {line.split("\t", 1)[0] for line in f if line.strip()}
        args = args[:k] + args[k + 2:]
    report, wjar, out = args[0:3]
    cmap, mmap = {}, {}
    if len(args) > 3:
        for line in open(args[3], encoding="utf-8"):
            f = line.rstrip("\n").split("\t")
            if len(f) < 3:
                continue
            if f[0] == "C":
                cmap[f[1]] = f[2]
            else:
                mmap[f[1]] = f[2].rsplit(".", 1)[1]

    def tcls(c):
        if not cmap:
            return c
        if c in cmap:
            return cmap[c]
        d = c.rfind("$")
        return tcls(c[:d]) + c[d:] if d > 0 else c

    W = {}
    jars = [wjar]
    for e in extra:
        jars += sorted(os.path.join(e, f) for f in os.listdir(e) if f.endswith(".jar")) if os.path.isdir(e) else [e]
    for i, jar in enumerate(jars):
        with zipfile.ZipFile(jar) as z:
            for n in z.namelist():
                if n.endswith(".class") and (i > 0 or n.startswith("net/minecraft/") or n.startswith("com/mojang/")):
                    p = parse(z.read(n))
                    if p and p[0] not in W:
                        W[p[0]] = p
    rows = dropped = 0
    with open(report, encoding="utf-8") as f, open(out, "w", encoding="utf-8", newline="\n") as o:
        next(f)
        o.write("usage\towner\tname\tdesc\tkinds\tw\n")
        for line in f:
            r = line.rstrip("\n").split("\t")
            if r[0] in control:
                dropped += 1
                continue
            usage, owner, name, desc, kinds = r[1], r[2], r[3], r[4], r[6]
            # the access-widener surface (api-probe -Dcb.apiprobe.nonpublic): aw-<usage>, judged as the usage
            base = usage[3:] if usage.startswith("aw-") else usage
            wo = tcls(owner)
            wn = mmap.get(owner + "." + name, name) if mmap else name
            c = W.get(wo)
            if c is None:
                w = "class gone" + (f" ({wo})" if wo != owner else "")
            elif base in ("extend", "implement-interface"):
                _, a, sup, itfs, _, _, sealed = c
                w = "class " + flags(a) + (" interface" if a & ACC_INTERFACE else "") + (" enum" if a & ACC_ENUM else "") \
                    + (" sealed" if sealed else "") + (f" extends {sup}" if sup else "")
            elif name == "<init>":
                ctors = [d for (nm, d, a) in c[5] if nm == "<init>" and not a & ACC_PRIVATE]
                w = "constructors " + (" ".join(ctors) if ctors else "(none reachable)")
            elif base.startswith(("get", "put")):
                same = [(d, a) for (nm, d, a) in c[4] if nm == wn]
                w = "field " + (" ; ".join(f"{d} {flags(a)}" for d, a in same) if same else "not declared on the owner")
            else:
                same = [(d, a) for (nm, d, a) in c[5] if nm == wn]
                w = "method " + (" ; ".join(f"{wn}{d} {flags(a)}" for d, a in same) if same else "not declared on the owner")
            o.write("\t".join([usage, owner, name, desc, kinds, w]) + "\n")
            rows += 1
    print(f"segment damage: {rows} rows -> {out}" + (f" ({dropped} sites broken on V too, left out)" if dropped else ""))


if __name__ == "__main__":
    main()
