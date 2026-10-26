#!/usr/bin/env python3
"""Step 26.1.2 -> 26.2 (s26 C17): 26.2 moved the extraction half of LevelRenderer onto
client/renderer/extract/LevelExtractor; the rendering half stays. Writes the mixinMoves fragment
core/segments/steps/26.1.2__26.2.json.d/smixin6-extractor-moves-gen.json: a mod injector selecting a 26.1.2
LevelRenderer method follows onto the LevelExtractor method that holds the same code (MixinRepair moves it; an
instance handler reading `this` takes the "self" hop LevelExtractor.levelRenderer back to the LevelRenderer).

A pair is taken only on bytecode evidence (javap -c -p of both code jars):
  - 26.2's LevelRenderer no longer declares the method (name + descriptor), and
  - 26.2's LevelExtractor declares the method under the same name with the same descriptor, and
  - the two bodies are the same code: their instruction sequences (opcodes and operands, constant pool indices and
    offsets dropped, LevelRenderer as owner of a member LevelExtractor now declares spelled as LevelExtractor)
    match at a ratio >= MIN_RATIO (difflib), the ratio written into the entry's basis.
Every other V method missing from 26.2's LevelRenderer is printed with the reason it was not taken (open, never
ledgered: the code may live elsewhere).

    python3 core/tools/gen_s26_extractor_moves.py [<data dir>]    (from the repository root; javap only)
"""
import difflib
import json
import re
import subprocess
import sys

DATA = sys.argv[1] if len(sys.argv) > 1 else 'data'
V_JAR = f'{DATA}/jars/client-26.1.2.jar'
W_JAR = f'{DATA}/jars/client-26.2.jar'
LR = 'net/minecraft/client/renderer/LevelRenderer'
LE = 'net/minecraft/client/renderer/extract/LevelExtractor'
FRAG = 'core/segments/steps/26.1.2__26.2.json.d/smixin6-extractor-moves-gen.json'
MIN_RATIO = 0.6
SELF = [f'{LE}.levelRenderer:L{LR};']

PRIM = {'void': 'V', 'boolean': 'Z', 'byte': 'B', 'char': 'C', 'short': 'S', 'int': 'I', 'long': 'J', 'float': 'F', 'double': 'D'}


def jdesc(t):
    t = t.strip()
    dims = t.count('[]')
    t = t.replace('[]', '')
    base = PRIM.get(t) or 'L' + t.replace('.', '/') + ';'
    return '[' * dims + base


def javap(jar, cls):
    out = subprocess.run(['javap', '-c', '-p', '-cp', jar, cls.replace('/', '.')], capture_output=True, text=True,
                         encoding='utf-8', errors='replace').stdout
    methods = {}
    cur = None
    head = re.compile(r'^  (?:[\w$]+ )*?([\w.$<>\[\]]+) ([\w$<>]+)\((.*)\)(?: throws [\w.$, ]+)?;$')
    for line in out.splitlines():
        m = head.match(line)
        if m and '(' in line:
            ret, name, params = m.groups()
            desc = '(' + ''.join(jdesc(p) for p in params.split(',') if p.strip()) + ')' + jdesc(ret)
            cur = name + desc
            methods[cur] = []
            continue
        if cur and re.match(r'^\s+\d+: ', line):
            ins = re.sub(r'^\s+\d+: ', '', line)
            ins = re.sub(r'#\d+(, *\d+)?', '', ins)          # constant pool indices
            ins = re.sub(r'\b(goto|if\w*|jsr)\s+\d+', r'\1', ins)  # branch offsets
            methods[cur].append(ins.strip())
        elif line.strip() == '}' or line == '':
            pass
    return methods


def norm(body, moved_members):
    out = []
    for ins in body:
        # a LevelRenderer member that W's LevelExtractor declares is the same reference from the moved code
        def owner(m):
            ref = m.group(0)
            mem = ref.split('.', 1)[1].split(':', 1)[0]
            return ref.replace('net/minecraft/client/renderer/LevelRenderer.', f'{LE}.') if mem in moved_members else ref
        out.append(re.sub(r'net/minecraft/client/renderer/LevelRenderer\.[\w$<>]+(:\S+)?', owner, ins))
    return out


def names(methods):
    return {k.split('(', 1)[0] for k in methods}


def callers(jar, owner):
    """{name+desc: set of classes (other than LevelRenderer / LevelExtractor) whose constant pool calls owner.name+desc}"""
    sys.path.insert(0, 'core/tools')
    import classfile
    import zipfile
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if not n.endswith('.class') or not n.startswith('net/minecraft/'):
                continue
            ci = classfile.read(z.read(n), code=True)
            if ci is None or ci.name in (LR, LE):
                continue
            pool, utf8 = ci.cp['pool'], ci.cp['utf8']
            for tag, val in pool.values():
                if tag not in (10, 11):
                    continue
                c, nat = val
                if pool.get(c, (0, 0))[0] != 7 or utf8.get(pool[c][1]) != owner:
                    continue
                t2, (ni, di) = pool.get(nat, (0, (0, 0)))
                if t2 == 12:
                    out.setdefault(utf8.get(ni) + utf8.get(di), set()).add(ci.name.split('$')[0])
    return out


def main():
    v = javap(V_JAR, LR)
    w_lr = javap(W_JAR, LR)
    w_le = javap(W_JAR, LE)
    v_calls = callers(V_JAR, LR)
    w_calls = callers(W_JAR, LE)
    le_fields = subprocess.run(['javap', '-p', '-cp', W_JAR, LE.replace('/', '.')], capture_output=True, text=True).stdout
    moved = names(w_le) | set(re.findall(r' (\w+);$', le_fields, re.M))
    moves = {}
    for key, body in sorted(v.items()):
        if key in w_lr or key.startswith('<') or key.startswith('lambda$'):
            continue
        if key not in w_le:
            same = [k for k in w_le if k.split('(')[0] == key.split('(')[0]]
            print(f'open {key}: not on LevelExtractor with this descriptor' + (f' (LevelExtractor has {same})' if same else ''))
            continue
        a, b = norm(body, moved), norm(w_le[key], moved)
        r = difflib.SequenceMatcher(None, a, b, autojunk=False).ratio()
        vc, wc = v_calls.get(key, set()), w_calls.get(key, set())
        same_callers = bool(vc) and vc == wc
        if r < MIN_RATIO and not same_callers:
            print(f'open {key}: bodies differ (ratio {r:.2f} < {MIN_RATIO}) and the callers differ '
                  f'(V {sorted(vc)}, W {sorted(wc)})')
            continue
        why = (f'same code (javap -c instruction ratio {r:.2f}, {len(a)} vs {len(b)} instructions)' if r >= MIN_RATIO
               else f'the same callers call it (V LevelRenderer.{key} from {sorted(vc)}; W LevelExtractor.{key} from the '
                    f'same classes), its body partly moved (ratio {r:.2f}): an anchor is judged in the W body')
        moves[f'{LR}.{key}'] = {
            'to': f'{LE}.{key}',
            'self': SELF,
            'basis': f'26.2 LevelRenderer lacks {key}; LevelExtractor declares it, {why}; LevelExtractor.levelRenderer is the LevelRenderer '
                     f'it extracts for (26.2 Minecraft.<init>: new LevelExtractor(this, state, this.levelRenderer))',
        }
        print(f'move {key} (ratio {r:.2f}{", same callers" if same_callers else ""})')
    with open(FRAG, 'w', encoding='utf-8', newline='\n') as f:
        json.dump({'_smixin6_extractor_moves_gen_basis': 'generated by core/tools/gen_s26_extractor_moves.py (s26 C17): '
                   'LevelRenderer methods 26.2 moved, the same code, onto LevelExtractor -- regenerate, never hand-edit',
                   'mixinMoves': moves}, f, indent=1, sort_keys=True)
        f.write('\n')
    print(f'{len(moves)} mixinMoves -> {FRAG}')


if __name__ == '__main__':
    main()
