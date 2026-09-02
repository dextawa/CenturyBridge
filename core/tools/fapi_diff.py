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
    super_name = cls(super_c) if super_c else None
    # interfaces
    ic = struct.unpack('>H', data[p:p+2])[0]; p += 2
    ifaces = [cls(struct.unpack('>H', data[p+2*k:p+2*k+2])[0]) for k in range(ic)]
    p += 2 * ic
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
    return consts, this_name, methods, fields, refs, classes, (super_name, [x for x in ifaces if x])

def index_jar(path, only_prefix=None):
    """class internal name -> (methods set, fields set, super, ifaces) for every class in a (possibly nested) jar."""
    idx = {}
    def scan(z):
        for e in z.namelist():
            if e.endswith('.class'):
                if only_prefix and not e.startswith(only_prefix):
                    continue
                r = parse_cp(z.read(e))
                if r: idx[r[1]] = (r[2], r[3], r[6][0], r[6][1])
            elif e.startswith('META-INF/jars/') and e.endswith('.jar'):
                scan(zipfile.ZipFile(io.BytesIO(z.read(e))))
    scan(zipfile.ZipFile(path))
    return idx

OBJECT_MEMBERS = {'equals', 'hashCode', 'toString', 'getClass', 'notify', 'notifyAll', 'wait'}

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('corpus'); ap.add_argument('fapi'); ap.add_argument('--wall'); ap.add_argument('--out')
    ap.add_argument('--handled', help='shims-<ver>.json: fapi symbols already wired there count as handled -- '
                    'classRenames (class + every member on it), staticRedirects/instanceRedirects/fieldRedirects/'
                    'methodRenames/fieldRenames (that member), ledger (reported as LEDGERED, not missing)')
    ap.add_argument('--mods', type=int, default=0, help='append up to N referencing mod names per row')
    ap.add_argument('--stub', help='game stub jar the fapi targets: member lookups climb the class hierarchy '
                    '(fapi supers/interfaces, then vanilla ones from this jar) -- javac names the static receiver '
                    'type as owner, so FabricItemGroupEntries.method_45421 (an ItemGroup.Entries default) or '
                    'CompostingChanceRegistry.add (an Item2ObjectMap member) are inherited, not gone')
    a = ap.parse_args()
    handled = {}; handled_mem = set(); ledger = {}
    if a.handled:
        import json
        cfg = json.load(io.open(a.handled, encoding='utf-8'))
        handled = {k: v for k, v in cfg.get('classRenames', {}).items() if k.startswith('net/fabricmc/')}
        for sec in ('staticRedirects', 'instanceRedirects', 'methodRenames'):
            for k in cfg.get(sec, {}):
                if k.startswith('net/fabricmc/'):
                    o, _, rest = k.partition('.')
                    handled_mem.add((o, rest[:rest.index('(')], rest[rest.index('('):]))
        for sec in ('fieldRedirects', 'fieldRenames'):
            for k in cfg.get(sec, {}):
                if k.startswith('net/fabricmc/'):
                    o, _, rest = k.partition('.')
                    n, _, d = rest.partition(':')
                    handled_mem.add((o, n, d))
        ledger = {k: v for k, v in cfg.get('ledger', {}).items() if k.startswith('net/fabricmc/')}
    wall = {}
    if a.wall:
        for ln in io.open(a.wall, encoding='utf-8'):
            p = ln.rstrip('\n').split('\t')
            if len(p) >= 3 and p[0] == 'C': wall[p[1]] = p[2]
    def tr(desc):
        # vanilla names cross the wall; renamed fapi classes follow their classRename (so a member
        # taking the old PacketType compares against the target's CbPacketType-typed slot)
        return re.sub(r'L([^;]+);', lambda m: 'L' + handled.get(m.group(1), wall.get(m.group(1), m.group(1))) + ';', desc)
    fapi = index_jar(a.fapi)
    stub = index_jar(a.stub, 'net/minecraft/') if a.stub else {}

    def lookup(c):
        if c in fapi: return fapi[c]
        return stub.get(wall.get(c, c)) if stub else None

    def declares(cls, name, tdesc):
        """member declared on cls or up its hierarchy; None = the walk left the indexed jars."""
        seen = set(); todo = [cls]; unknown = False
        while todo:
            c = todo.pop()
            if c in seen: continue
            seen.add(c)
            e = lookup(c)
            if e is None:
                if c.startswith('java/'):
                    if name in OBJECT_MEMBERS: return True
                    continue
                unknown = True; continue
            if (name + tdesc) in e[0] or (name + tdesc) in e[1]:
                return True
            if e[2]: todo.append(e[2])
            todo.extend(e[3])
        return None if unknown else False

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
    def ledgered(sym):
        # a ledger entry may name the class, or the class with an outer-class prefix (nested)
        return sym in ledger or sym.split('$')[0] in ledger
    def ledgered_member(owner, name, desc):
        # engine ledger keys: the owner class, owner.name, or the full member (owner.name(desc) / owner.NAME:desc)
        return ledgered(owner) or f"{owner}.{name}" in ledger or f"{owner}.{name}{desc}" in ledger or f"{owner}.{name}:{desc}" in ledger
    missing_cls = {}; ledgered_cls = {}
    for c, m in cls_refs.items():
        if c in fapi or c in handled: continue
        (ledgered_cls if ledgered(c) else missing_cls)[c] = m
    missing_mem = {}; ledgered_mem = {}; renamed_missing = {}; inherited = 0
    for (owner, name, desc), mods in mem_refs.items():
        if (owner, name, desc) in handled_mem: continue
        if owner in handled:
            # renamed onto another fapi class: that class must carry the member (a compat class
            # under top/dext owns the contract itself -- javac judges it at build time)
            tgt = handled[owner]
            if tgt in fapi:
                methods, fields = fapi[tgt][:2]
                td = tr(desc)
                if (name + td) not in methods and (name + td) not in fields and not declares(tgt, name, td):
                    same = [m for m in methods | fields if m.startswith(name + '(') or m == name + td]
                    renamed_missing[(owner, name, desc)] = (mods, tgt, same[:2])
            continue
        if owner not in fapi:
            if ledgered_member(owner, name, desc): ledgered_mem[(owner, name, desc)] = mods
            continue
        methods, fields = fapi[owner][:2]
        td = tr(desc)
        ok = (name + td) in methods or (name + td) in fields
        if not ok:
            up = declares(owner, name, td)
            if up:
                inherited += 1; continue
            if ledgered_member(owner, name, desc):
                ledgered_mem[(owner, name, desc)] = mods; continue
            # tolerate a same-name member with any descriptor as "renamed desc" (report separately)
            same = [m for m in methods | fields if m.startswith(name + '(') or m == name + td]
            if up is None:
                same = ['hierarchy leaves the indexed jars'] + same[:1]
            missing_mem[(owner, name, desc)] = (mods, same[:2])
    def modcol(mods):
        return ('\t' + ','.join(sorted(mods)[:a.mods])) if a.mods else ''
    lines = []
    lines.append(f"# fapi kill-list vs {os.path.basename(a.fapi)}: classes referenced {len(cls_refs)}, missing {len(missing_cls)}"
                 f" (ledgered {len(ledgered_cls)}); members referenced {len(mem_refs)}, missing {len(missing_mem)}"
                 f" (ledgered {len(ledgered_mem)}, rename-target lacks {len(renamed_missing)}, inherited {inherited}"
                 f"{'' if a.stub else ' [no --stub: vanilla supers not climbed]'})")
    lines.append("kind\tmods\trefs\tsymbol\tnote")
    for c, mods in sorted(missing_cls.items(), key=lambda x: -len(x[1])):
        lines.append(f"CLASS\t{len(mods)}\t{cls_cnt[c]}\t{c}\t{modcol(mods)}")
    for (o, n, d), (mods, same) in sorted(missing_mem.items(), key=lambda x: -len(x[1][0])):
        lines.append(f"MEMBER\t{len(mods)}\t{mem_cnt[(o,n,d)]}\t{o}.{n}{d}\t{'same-name: ' + '; '.join(same) if same else ''}{modcol(mods)}")
    for (o, n, d), (mods, tgt, same) in sorted(renamed_missing.items(), key=lambda x: -len(x[1][0])):
        lines.append(f"RENAMED-MISSING\t{len(mods)}\t{mem_cnt[(o,n,d)]}\t{o}.{n}{d}\ttarget {tgt}{': ' + '; '.join(same) if same else ''}{modcol(mods)}")
    for c, mods in sorted(ledgered_cls.items(), key=lambda x: -len(x[1])):
        lines.append(f"LEDGERED\t{len(mods)}\t{cls_cnt[c]}\t{c}\t{ledger.get(c, ledger.get(c.split('$')[0], ''))}{modcol(mods)}")
    for (o, n, d), mods in sorted(ledgered_mem.items(), key=lambda x: -len(x[1])):
        lines.append(f"LEDGERED\t{len(mods)}\t{mem_cnt[(o,n,d)]}\t{o}.{n}{d}\t{modcol(mods)}")
    txt = '\n'.join(lines)
    if a.out: io.open(a.out, 'w', encoding='utf-8', newline='\n').write(txt + '\n')
    print('\n'.join(lines[:60]))

if __name__ == '__main__':
    main()
