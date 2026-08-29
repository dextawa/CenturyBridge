"""Fabric-API kill list: every net/fabricmc/fabric/api class / member the corpus references,
checked against a target Fabric API jar. The vanilla audit never sees these (non-minecraft owners),
so fapi breakage only surfaced as client crashes. Constant pools are parsed directly (no ASM).

  python3 fapi_diff.py <corpusJarDir> <fabric-api.jar> [--wall inter-to-mojang.tsv] [--out report.tsv]

--wall: the target fapi is mojang-named (26.x): translate vanilla class names inside descriptors
before comparing. Output: missing classes (mods, refs) and missing members on surviving classes.
"""
import sys, os, glob, struct, zipfile, io, re, argparse
from collections import defaultdict

def parse_cp(data):
    """Return (consts, this_class_name, methods{name+desc}, fields{name+desc}) of a class file."""
    if data[:4] != b'\xca\xfe\xba\xbe':
        return None
    n = struct.unpack('>H', data[8:10])[0]
    consts = [None] * n
    i, p = 1, 10
    while i < n:
        tag = data[p]
        if tag == 1:
            ln = struct.unpack('>H', data[p+1:p+3])[0]
            consts[i] = ('utf8', data[p+3:p+3+ln].decode('utf-8', 'replace')); p += 3 + ln
        elif tag in (3, 4): consts[i] = (tag, None); p += 5
        elif tag in (5, 6): consts[i] = (tag, None); p += 9; i += 1
        elif tag == 7: consts[i] = ('class', struct.unpack('>H', data[p+1:p+3])[0]); p += 3
        elif tag == 8: consts[i] = ('string', None); p += 3
        elif tag in (9, 10, 11):
            c, nt = struct.unpack('>HH', data[p+1:p+5]); consts[i] = ('ref', c, nt, tag); p += 5
        elif tag == 12:
            a, b = struct.unpack('>HH', data[p+1:p+5]); consts[i] = ('nt', a, b); p += 5
        elif tag == 15: consts[i] = (15, None); p += 4
        elif tag == 16: consts[i] = (16, None); p += 3
        elif tag == 17 or tag == 18: consts[i] = (tag, None); p += 5
        elif tag == 19 or tag == 20: consts[i] = (tag, None); p += 3
        else:
            return None
        i += 1
    def u(idx): return consts[idx][1] if consts[idx] and consts[idx][0] == 'utf8' else None
    def cls(idx): return u(consts[idx][1]) if consts[idx] and consts[idx][0] == 'class' else None
    acc, this_c, super_c = struct.unpack('>HHH', data[p:p+6]); p += 6
    this_name = cls(this_c)
    # interfaces
    ic = struct.unpack('>H', data[p:p+2])[0]; p += 2 + 2 * ic
    def read_members():
        nonlocal p
        cnt = struct.unpack('>H', data[p:p+2])[0]; p += 2
        out = set()
        for _ in range(cnt):
            a, nm, ds, ac = struct.unpack('>HHHH', data[p:p+8]); p += 8
            out.add((u(nm) or '') + (u(ds) or ''))
            for _ in range(ac):
                ln = struct.unpack('>I', data[p+2:p+6])[0]; p += 6 + ln
        return out
    fields = read_members(); methods = read_members()
    refs = []
    for c in consts:
        if c and c[0] == 'ref':
            owner = cls(c[1]); nt = consts[c[2]]
            if owner and nt and nt[0] == 'nt':
                refs.append((owner, u(nt[1]) or '', u(nt[2]) or '', c[3]))
    classes = [u(c[1]) for c in consts if c and c[0] == 'class' and u(c[1])]
    return consts, this_name, methods, fields, refs, classes

def index_jar(path):
    """class internal name -> (methods set, fields set) for every class in a (possibly nested) fapi jar."""
    idx = {}
    def scan(z):
        for e in z.namelist():
            if e.endswith('.class'):
                r = parse_cp(z.read(e))
                if r: idx[r[1]] = (r[2], r[3])
            elif e.startswith('META-INF/jars/') and e.endswith('.jar'):
                scan(zipfile.ZipFile(io.BytesIO(z.read(e))))
    scan(zipfile.ZipFile(path))
    return idx

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('corpus'); ap.add_argument('fapi'); ap.add_argument('--wall'); ap.add_argument('--out')
    a = ap.parse_args()
    wall = {}
    if a.wall:
        for ln in io.open(a.wall, encoding='utf-8'):
            p = ln.rstrip('\n').split('\t')
            if len(p) >= 3 and p[0] == 'C': wall[p[1]] = p[2]
    def tr(desc):
        if not wall: return desc
        return re.sub(r'L([^;]+);', lambda m: 'L' + wall.get(m.group(1), m.group(1)) + ';', desc)
    fapi = index_jar(a.fapi)
    cls_refs = defaultdict(set); mem_refs = defaultdict(set); mem_cnt = defaultdict(int); cls_cnt = defaultdict(int)
    for j in sorted(glob.glob(os.path.join(a.corpus, '*.jar'))):
        try: z = zipfile.ZipFile(j)
        except Exception: continue
        mod = os.path.basename(j)
        for e in z.namelist():
            if not e.endswith('.class'): continue
            r = parse_cp(z.read(e))
            if not r: continue
            for c in r[5]:
                if c.startswith('net/fabricmc/fabric/api/'):
                    cls_refs[c].add(mod); cls_cnt[c] += 1
            for owner, name, desc, tag in r[4]:
                if owner.startswith('net/fabricmc/fabric/api/'):
                    k = (owner, name, desc); mem_refs[k].add(mod); mem_cnt[k] += 1
    missing_cls = {c: m for c, m in cls_refs.items() if c not in fapi}
    missing_mem = {}
    for (owner, name, desc), mods in mem_refs.items():
        if owner not in fapi: continue
        methods, fields = fapi[owner]
        td = tr(desc)
        ok = (name + td) in methods or (name + td) in fields
        if not ok:
            # tolerate a same-name member with any descriptor as "renamed desc" (report separately)
            same = [m for m in methods | fields if m.startswith(name + '(') or m == name + td]
            missing_mem[(owner, name, desc)] = (mods, same[:2])
    lines = []
    lines.append(f"# fapi kill-list vs {os.path.basename(a.fapi)}: classes referenced {len(cls_refs)}, missing {len(missing_cls)}; members referenced {len(mem_refs)}, missing {len(missing_mem)}")
    lines.append("kind\tmods\trefs\tsymbol\tnote")
    for c, mods in sorted(missing_cls.items(), key=lambda x: -len(x[1])):
        lines.append(f"CLASS\t{len(mods)}\t{cls_cnt[c]}\t{c}\t")
    for (o, n, d), (mods, same) in sorted(missing_mem.items(), key=lambda x: -len(x[1][0])):
        lines.append(f"MEMBER\t{len(mods)}\t{mem_cnt[(o,n,d)]}\t{o}.{n}{d}\t{'same-name: ' + '; '.join(same) if same else ''}")
    txt = '\n'.join(lines)
    if a.out: io.open(a.out, 'w', encoding='utf-8', newline='\n').write(txt + '\n')
    print('\n'.join(lines[:60]))

if __name__ == '__main__':
    main()
