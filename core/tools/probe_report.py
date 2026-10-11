"""The API probe's verdict per target: the full diff, entry by entry.

Joins link-check's findings on a converted probe with the probe's own index (Main api-probe):
every probe site is one way a 1.20.1 mod can touch one API member (a call, a field read or
write, a NEW, a super(...), an override, a super call, an implementation, a lambda), so the
sites with no finding are the part of the diff the conversion carries, and every site with
one is an entry still open -- independent of which mods happen to use it.

  python probe_report.py <index.tsv> <converted probe jar> <link-findings.tsv> <out.tsv>

Converted method names and descriptors may differ from the probe's (that is the conversion
working); each probe method starts with an ldc "cbprobe#<site>" that maps it back.
"""
import collections
import struct
import sys
import zipfile


def methods_with_marker(b):
    """(name, desc) -> site id for one class file, from each method's leading ldc marker"""
    if b[:4] != bytes([0xCA, 0xFE, 0xBA, 0xBE]):
        return "", {}
    n = struct.unpack(">H", b[8:10])[0]
    i, idx = 10, 1
    utf8, strs, cls = {}, {}, {}
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
        elif tag == 8:
            strs[idx] = struct.unpack(">H", b[i + 1:i + 3])[0]
            i += 3
        elif tag in (16, 19, 20):
            i += 3
        elif tag in (9, 10, 11, 12, 17, 18):
            i += 5
        elif tag == 15:
            i += 4
        else:
            return "", {}
        idx += 1
    i += 2  # access
    this = utf8.get(cls.get(struct.unpack(">H", b[i:i + 2])[0]), "")
    i += 4  # this, super
    nif = struct.unpack(">H", b[i:i + 2])[0]
    i += 2 + 2 * nif

    def skip_attrs(i):
        na = struct.unpack(">H", b[i:i + 2])[0]
        i += 2
        for _ in range(na):
            ln = struct.unpack(">I", b[i + 2:i + 6])[0]
            i += 6 + ln
        return i

    nf = struct.unpack(">H", b[i:i + 2])[0]
    i += 2
    for _ in range(nf):
        i = skip_attrs(i + 6)
    nm = struct.unpack(">H", b[i:i + 2])[0]
    i += 2
    out = {}
    for _ in range(nm):
        name = utf8.get(struct.unpack(">H", b[i + 2:i + 4])[0])
        desc = utf8.get(struct.unpack(">H", b[i + 4:i + 6])[0])
        i += 6
        na = struct.unpack(">H", b[i:i + 2])[0]
        i += 2
        for _ in range(na):
            an = utf8.get(struct.unpack(">H", b[i:i + 2])[0])
            ln = struct.unpack(">I", b[i + 2:i + 6])[0]
            if an == "Code":
                code = b[i + 14:i + 14 + struct.unpack(">I", b[i + 10:i + 14])[0]]
                ref = None
                if code[:1] == bytes([0x12]):
                    ref = code[1]
                elif code[:1] == bytes([0x13]):
                    ref = struct.unpack(">H", code[1:3])[0]
                s = utf8.get(strs.get(ref)) if ref is not None else None
                if s and s.startswith("cbprobe#"):
                    out[(name, desc)] = int(s[8:])
            i += 6 + ln
    return this, out


def main():
    index_path, jar, findings, out = sys.argv[1:5]
    sites = []
    with open(index_path, encoding="utf-8") as f:
        next(f)
        for line in f:
            sites.append(line.rstrip("\n").split("\t"))
    by_class_usage = collections.defaultdict(list)  # class-level sites (extend / implement-interface)
    for sid, s in enumerate(sites):
        if s[1] == "":
            by_class_usage[s[0]].append(sid)
    where = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith("cbprobe/") and n.endswith(".class"):
                this, ms = methods_with_marker(z.read(n))
                for (name, desc), sid in ms.items():
                    where[f"{this}.{name}{desc}"] = sid
    hits = collections.defaultdict(list)
    unmapped = collections.Counter()
    with open(findings, encoding="utf-8", errors="replace") as f:
        next(f)
        for line in f:
            r = line.rstrip("\n").split("\t")
            if len(r) < 5:
                continue
            bucket, kind, _, w, ref = r[:5]
            sid = where.get(w)
            if sid is None:
                sid = where.get(ref)
            if sid is None:
                cls = w.split(".")[0] if "." in w else w
                cls = cls.split("(")[0]
                for c in (cls, ref.split(".")[0]):
                    if by_class_usage.get(c):
                        sid = by_class_usage[c][0]
                        break
            if sid is None:
                unmapped[(bucket, kind)] += 1
                continue
            hits[sid].append((bucket, kind, ref))
    usage_total = collections.Counter(s[2] for s in sites)
    usage_broken = collections.Counter()
    bucket_sites = collections.Counter()
    owner_broken = collections.Counter()
    with open(out, "w", encoding="utf-8") as f:
        f.write("site\tusage\towner\tname\tdesc\tbuckets\tkinds\trefs\n")
        for sid, s in enumerate(sites):
            h = hits.get(sid)
            if not h:
                continue
            usage_broken[s[2]] += 1
            worst = "CLAIMED" if any(b == "CLAIMED" for b, _, _ in h) else ("BLIND" if any(b == "BLIND" for b, _, _ in h)
                     else ("KNOWN" if any(b == "KNOWN" for b, _, _ in h) else h[0][0]))
            bucket_sites[worst] += 1
            owner_broken[s[3]] += 1
            f.write("\t".join([str(sid), s[2], s[3], s[4], s[5],
                               ",".join(sorted({b for b, _, _ in h})), ",".join(sorted({k for _, k, _ in h})),
                               " | ".join(sorted({x for _, _, x in h}))[:400]]) + "\n")
    total = len(sites)
    broken = sum(usage_broken.values())
    print(f"probe sites: {total}; with a finding: {broken} ({broken * 100 / total:.1f}%); clean: {total - broken}")
    print("by worst bucket:", dict(bucket_sites))
    print("by usage (broken / total):")
    for u, t in usage_total.most_common():
        print(f"  {u:24} {usage_broken[u]:6} / {t:6}")
    print("owners with most open sites:")
    for o, c in owner_broken.most_common(15):
        print(f"  {c:5} {o}")
    if unmapped:
        print("findings not mapped to a site:", dict(unmapped))


if __name__ == "__main__":
    main()
