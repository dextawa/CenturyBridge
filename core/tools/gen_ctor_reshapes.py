#!/usr/bin/env python3
"""Generate superCtorReshapes wiring for 26.2 constructor-signature changes.

CB's full audit flags constructors whose signature changed across the chain as
"constructor signature changed -> tombstone/lazy fail". That verdict predates
superCtorReshapes: when the 26.2 class still exists and its new constructor's
parameters are a *reordering* (optionally with a few dropped) of the old
parameters, the call can be reshaped in place instead of tombstoned. A dropped
parameter must be genuinely redundant on 26.2 (folded into another arg or
removed); the tool lists every drop for review so a semantically-load-bearing
drop is caught rather than silently mis-bridged.

Input: a list of changed intermediary constructors (one `owner.<init>(desc)V`
per line, intermediary names), e.g. the de-duplicated
"constructor signature changed" / CTOR-ISSUE / SUPER-CTOR-ISSUE symbols from
one or more full-audit.tsv.

For each, it maps the owner + descriptor to mojang via the wall table, finds a
26.2 constructor whose parameter multiset is the old one (reorder) or a subset
(reorder + drop), and emits a superCtorReshapes entry keyed by the *old
intermediary* constructor with:
  - desc: the new constructor in intermediary namespace (new mojang params
          mapped back to intermediary where a mapping exists),
  - ops:  keep:I for each new parameter, I = its index in the old parameter
          list (so the reshape re-pushes the old args in the new order).

Usage:
  python3 core/tools/gen_ctor_reshapes.py <changed-ctors.txt> \
      [--stub data/jars/client-26.2.jar] \
      [--table data/mappings/inter-to-mojang-1.21.11.tsv] \
      [--apply core/segments/shims-26.2.json]
Without --apply it prints the generated entries and the drop/manual report.
"""
import io, json, re, subprocess, sys, collections

def load_table(path):
    fwd = {}  # intermediary class -> mojang
    bwd = {}  # mojang class -> intermediary
    for ln in io.open(path, encoding='utf-8'):
        p = ln.rstrip('\n').split('\t')
        if len(p) >= 3 and p[0] == 'C':
            fwd[p[1]] = p[2]
            bwd.setdefault(p[2], p[1])
    return fwd, bwd

def params(desc):
    inner = desc[1:desc.index(')')]
    out = []
    i = 0
    while i < len(inner):
        c = inner[i]
        if c == 'L':
            j = inner.index(';', i); out.append(inner[i:j+1]); i = j+1
        elif c == '[':
            k = i
            while inner[k] == '[':
                k += 1
            if inner[k] == 'L':
                j = inner.index(';', k); out.append(inner[i:j+1]); i = j+1
            else:
                out.append(inner[i:k+1]); i = k+1
        else:
            out.append(c); i += 1
    return out

def map_desc(desc, table):
    return re.sub(r'L([^;]+);', lambda m: 'L' + table.get(m.group(1), m.group(1)) + ';', desc)

_cache = {}
def moj_ctors(moj, stub):
    if moj in _cache:
        return _cache[moj]
    try:
        out = subprocess.run(['javap', '-p', '-s', '-classpath', stub, moj.replace('/', '.')],
                             capture_output=True, text=True, timeout=25).stdout
    except Exception:
        out = ''
    r = re.findall(r'descriptor: (\([^)]*\)V)', out)
    _cache[moj] = r
    return r

def main():
    args = sys.argv[1:]
    if not args:
        print(__doc__); return
    src = args[0]
    stub = 'data/jars/client-26.2.jar'
    table = 'data/mappings/inter-to-mojang-1.21.11.tsv'
    apply_to = None
    for i, a in enumerate(args):
        if a == '--stub': stub = args[i+1]
        elif a == '--table': table = args[i+1]
        elif a == '--apply': apply_to = args[i+1]
    fwd, bwd = load_table(table)
    rows = [l.strip() for l in io.open(src, encoding='utf-8') if l.strip()]
    gen = {}
    drops = []
    manual = []
    for r in rows:
        owner, od = r.split('.<init>')
        moj = fwd.get(owner)
        if not moj:
            manual.append((r, 'owner not in wall table')); continue
        oldp = params(map_desc(od, fwd))        # old params in mojang
        olds = sorted(oldp)
        best = None
        for nd in moj_ctors(moj, stub):
            np = params(nd)
            if sorted(np) == olds and np != oldp:
                best = ('reorder', nd, np); break
        if not best:
            # Only a pure reordering (same parameter multiset, different order)
            # is safe to auto-generate: no argument is lost, so no behaviour is
            # silently dropped. A "subset" match (26.2 ctor with fewer params)
            # is NOT auto-bridged -- blindly dropping args matched ShapedRecipe
            # / MerchantOffer / packets onto near-empty constructors, which is a
            # false bridge, not a repair. Those go to manual review.
            manual.append((r, 'no pure-reorder match (needs manual: dropped/added/typed args)')); continue
        kind, nd, np = best
        # build ops: for each new param, its index among the OLD params; consume
        # matched old slots left to right so duplicate types map stably
        used = [False]*len(oldp)
        ops = []
        ok = True
        for t in np:
            idx = next((k for k in range(len(oldp)) if not used[k] and oldp[k] == t), None)
            if idx is None:
                ok = False; break
            used[idx] = True
            ops.append(f'keep:{idx}')
        if not ok:
            manual.append((r, 'param type not found in old')); continue
        dropped = [oldp[k] for k in range(len(oldp)) if not used[k]]
        # new desc in intermediary namespace (map mojang params back)
        new_desc_inter = map_desc(nd, bwd)
        gen[r] = {'desc': new_desc_inter, 'ops': ops}
        if dropped:
            drops.append((moj.split('/')[-1], r.split('(')[0], [bwd.get(d[1:-1], d) if d.startswith('L') else d for d in dropped]))
    print(f'generated {len(gen)} reshapes; {len(drops)} with dropped params; {len(manual)} need manual')
    print('\n--- DROPPED PARAMS (review each is redundant on 26.2) ---')
    for name, key, dp in drops:
        print(f'  {name}: drops {dp}')
    print('\n--- MANUAL (new params / type change / no match) ---')
    for r, why in manual:
        print(f'  {r.split("(")[0]}  [{why}]')
    if apply_to:
        d = json.load(io.open(apply_to, encoding='utf-8'))
        scr = d.setdefault('superCtorReshapes', {})
        added = 0
        for k, v in gen.items():
            if k not in scr:
                scr[k] = v; added += 1
        json.dump(d, io.open(apply_to, 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
        print(f'\napplied {added} new reshapes to {apply_to} ({len(gen)-added} already present)')

if __name__ == '__main__':
    main()
