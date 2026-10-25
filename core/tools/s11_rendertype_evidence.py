"""Step 1.21.10 -> 1.21.11, input of gen_s11_rendertypes.py. Evidence for RenderType -> RenderTypes factory pairs: for each static factory, the RenderPipelines fields and the
string constants (render type names) its code reaches (own static fields' <clinit> stretches, own methods, lambdas).

    python3 core/tools/s11_rendertype_evidence.py <V class_1921 javap -c -p -v> <W class_12249 javap -c -p -v> \n        <inter-to-mojang-1.21.10.tsv> <inter-to-mojang-1.21.11.tsv> <V factory ids...> > evidence.txt
"""
import re, sys, json

def parse(path, owner):
    lines = open(path, encoding='utf-8', errors='replace').read().split('\n')
    methods = {}
    boot = {}
    cur = None
    i = 0
    in_boot = False
    bidx = None
    for l in lines:
        if l.startswith('BootstrapMethods:'):
            in_boot = True
            continue
        if in_boot:
            m = re.match(r'\s+(\d+): #\d+ REF_invokeStatic java/lang/invoke/LambdaMetafactory', l)
            if m:
                bidx = int(m.group(1))
                continue
            m = re.search(r'REF_invoke\w+ ' + re.escape(owner) + r'\.(\w+):(\S+)', l)
            if m and bidx is not None and bidx not in boot:
                boot[bidx] = m.group(1) + m.group(2)
            continue
        m = re.match(r'  (?:[\w$<>.\[\], ]+ )?([\w$<>]+)\((.*)\);$', l)
        if m and not l.startswith('    '):
            cur = {'name': m.group(1), 'code': []}
            methods.setdefault(m.group(1), []).append(cur)
            continue
        if l.strip() == 'static {};':
            cur = {'name': '<clinit>', 'code': []}
            methods['<clinit>'] = [cur]
            continue
        if cur is not None and re.match(r'\s+\d+: ', l):
            cur['code'].append(l.strip())
    return methods, boot

def stretches(clinit):
    """own field -> instructions from the previous putstatic to its putstatic"""
    out = {}
    buf = []
    for ins in clinit:
        buf.append(ins)
        m = re.search(r'putstatic .*// Field (\w+):', ins)
        if m:
            out[m.group(1)] = buf
            buf = []
    return out

def evidence(methods, boot, owner, start):
    st = stretches(methods.get('<clinit>', [{'code': []}])[0]['code'])
    pipes, strs, seen = set(), set(), set()
    def walk(code):
        for ins in code:
            m = re.search(r'getstatic .*// Field net/minecraft/class_10799\.(\w+)', ins)
            if m:
                pipes.add(m.group(1))
            m = re.search(r'// String (.+)$', ins)
            if m:
                strs.add(m.group(1))
            m = re.search(r'getstatic .*// Field (\w+):', ins)
            if m and m.group(1) in st and ('f', m.group(1)) not in seen:
                seen.add(('f', m.group(1)))
                walk(st[m.group(1)])
            m = re.search(r'invokestatic .*// Method (\w+):', ins)
            if m and m.group(1) in methods and ('m', m.group(1)) not in seen:
                seen.add(('m', m.group(1)))
                for x in methods[m.group(1)]:
                    walk(x['code'])
            m = re.search(r'invokedynamic .*// InvokeDynamic #(\d+):', ins)
            if m and int(m.group(1)) in boot:
                n = boot[int(m.group(1))].split('(')[0]
                if ('m', n) not in seen and n in methods:
                    seen.add(('m', n))
                    for x in methods[n]:
                        walk(x['code'])
    for x in methods.get(start, []):
        walk(x['code'])
    return sorted(pipes), sorted(strs)

V = parse(sys.argv[1], 'net/minecraft/class_1921')
W = parse(sys.argv[2], 'net/minecraft/class_12249')
vn, wn = {}, {}
for l in open(sys.argv[3], encoding='utf-8'):
    p = l.rstrip('\n').split('\t')
    if p[0] == 'M' and p[1].startswith('net/minecraft/class_1921.'):
        vn[p[1].split('.')[1]] = p[2].split('.')[-1]
for l in open(sys.argv[4], encoding='utf-8'):
    p = l.rstrip('\n').split('\t')
    if p[0] == 'M' and p[1].startswith('net/minecraft/class_12249.'):
        wn.setdefault(p[2].split('.')[-1], set()).add(p[1].split('.')[1])
names = sys.argv[5:]
res = {}
for n in names:
    ev = evidence(V[0], V[1], 'net/minecraft/class_1921', n)
    cands = []
    for wname, ids in wn.items():
        for wid in ids:
            we = evidence(W[0], W[1], 'net/minecraft/class_12249', wid)
            if we[0] and set(we[0]) == set(ev[0]) and (set(we[1]) & set(ev[1]) or not ev[1]):
                cands.append((wid, wname, we))
    res[n] = (vn.get(n), ev, cands)
    print(n, vn.get(n), ev, '->', [(c[0], c[1], c[2][1]) for c in cands])
