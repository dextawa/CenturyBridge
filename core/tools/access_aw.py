#!/usr/bin/env python3
"""Regenerate each shim tree's access-narrowing block in its cb.accesswidener.

For every tree, `Main access-diff` compares the 1.20.1 stub with the tree's stub and lists
what a 1.20.1 mod could reach / override / extend / write that the target took away
(public -> private, made final, made sealed); the lines land between the markers below,
replacing the previous block, minus what the hand-written part already says.

    python3 core/tools/access_aw.py [version...]      # default: every tree

The engine must be compiled first (core/build/classes). Afterwards rebuild the access-widened
compile stubs (core/tools/make_aw_stub.sh) for the trees that compile against one.
"""
import glob
import os
import subprocess
import sys

BEGIN = '# ---- BEGIN GENERATED: access narrowed since 1.20.1 (Main access-diff) ----'
END = '# ---- END GENERATED ----'
TREES = ['1.20.2', '1.20.4', '1.21.1', '1.21.4', '1.21.8', '1.21.11', '26.2']
OLD = 'data/jars/client-1.20.1-intermediary.jar'
WALL = 'data/mappings/inter-to-mojang-1.21.11.tsv'


def stub(v):
    """the tree's plain stub: intermediary before the namespace wall, the real jar after"""
    p = f'data/jars/client-{v}-intermediary.jar'
    return p if os.path.exists(p) else f'data/jars/client-{v}.jar'


def classpath():
    sep = ';' if os.name == 'nt' else ':'
    libs = sorted(set(glob.glob('data/runtime/client-*/libraries/org/ow2/asm/**/*.jar', recursive=True)
                      + glob.glob('data/runtime/client-*/libraries/com/google/code/gson/**/*.jar', recursive=True)))
    return sep.join(['core/build/classes'] + libs)


def main(versions):
    os.makedirs('core/out-verify', exist_ok=True)
    for v in versions:
        out = f'core/out-verify/access-{v}.aw'
        cmd = ['java', '-cp', classpath(), 'top.dext.centurybridge.Main', 'access-diff', OLD, stub(v)]
        if not stub(v).endswith('-intermediary.jar'):
            cmd.append(WALL)
        # the converter's own member moves (renames, lookup reshapes) land on target members too
        cmd.append(f'core/segments/shims-{v}.json')
        subprocess.run(cmd + [out], check=True)
        p = f'core/shims/{v}/resources/cb.accesswidener'
        s = open(p, encoding='utf-8').read()
        if BEGIN in s:
            s = s[:s.index(BEGIN)].rstrip('\n') + '\n' + s[s.index(END) + len(END):].lstrip('\n')
        have = {' '.join(l.split()) for l in s.splitlines() if l.strip() and not l.startswith('#')}
        gen = open(out, encoding='utf-8').read().splitlines()
        lines = [l for l in gen if l.strip() and not l.startswith('#') and ' '.join(l.split()) not in have]
        notes = [l for l in gen if l.startswith('#   ')]
        block = [BEGIN,
                 '# public/protected in 1.20.1, narrowed, made final or sealed in this release: the same',
                 '# members, so a converted mod calling, overriding, extending or writing them gets the',
                 '# access back (core/tools/access_aw.py).'] + lines
        if notes:
            block += ['# narrowed too, but outside the classes the loader tweaks -- not bridged by this:'] + notes
        block.append(END)
        s = s.rstrip('\n') + '\n' + '\n'.join(block) + '\n'
        open(p, 'w', encoding='utf-8', newline='\n').write(s)
        print(f'{v}: {len(lines)} lines, {len(notes)} unreachable by access widener')


if __name__ == '__main__':
    main(sys.argv[1:] or TREES)
