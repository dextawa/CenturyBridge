"""Mixin priority gate for climb step layers (build.sh gate 1; also runnable over every layer).

    python3 core/tools/mixin_priority.py <shimDir>...      exit 1 when any layer breaks a rule below

A step layer that restores a member merges it into the vanilla class through a layer mixin. Mixin lets a mod's
injector other than HEAD / RETURN / TAIL into a merged member only when the merging mixin's priority is below the
mod's (InjectionPoint.checkPriority); mods sit at the default 1000. Two rules follow:

1. restorer below the mods: a layer mixin that merges members (a method body not marked @Shadow / @Overwrite /
   @Unique / @Accessor / @Invoker / an injector annotation, not abstract, not a cb$ name) must declare a priority
   below 1000 (CbVanillaReads.PRIORITY). At 1000 the converter disables every such injector of a converted mod
   ("restored member merged at priority 1000, not below the mod's 1000").
2. injectors at the default: a restorer below 1000 (it merges members, however its priority is spelled, or it is
   declared at CbVanillaReads.PRIORITY) must not carry injectors into W's own methods: they would run before every
   mod's injectors at the same point, which changes W's order for native mods. Split them into a default-priority
   <name>Hooks mixin (shared state through the layer's rt/CbPrioritySplit[Client]). The one exception is a route
   that must run first and says so: priority = CbVanillaReads.ROUTE_FIRST. A route mixin that merges nothing and
   declares its own low priority (a literal or a route constant such as CbRoutes.ROUTE_PRIORITY) is not judged here.

Sources are read as text (comments and string literals blanked); multi-line declarations are handled.
"""
import os
import re
import sys

INJECTORS = (r'Inject|Redirect|ModifyArg|ModifyArgs|ModifyVariable|ModifyConstant|ModifyExpressionValue|'
             r'ModifyReturnValue|WrapOperation|WrapWithCondition|WrapMethod|ModifyReceiver')
NOT_MERGED = re.compile(r'@(Shadow|Overwrite|Unique|Accessor|Invoker|' + INJECTORS + r')\b')
INJECTOR = re.compile(r'@(' + INJECTORS + r')\s*\(')
KEYWORDS = {'if', 'for', 'while', 'switch', 'catch', 'synchronized', 'return', 'new', 'else', 'try', 'do'}


def strip(s):
    s = re.sub(r'/\*.*?\*/', lambda m: ' ' * len(m.group(0)), s, flags=re.S)
    s = re.sub(r'//[^\n]*', '', s)
    s = re.sub(r'"(?:\\.|[^"\\])*"', '""', s)
    s = re.sub(r"'(?:\\.|[^'\\])'", "''", s)
    return s


def merged_members(s, name):
    """names of the methods with a body the mixin merges into its target"""
    head = re.search(r'\bclass\s+' + re.escape(name) + r'\b', s)
    if not head:
        return []
    body = s[s.index('{', head.end()) + 1:]
    depth, buf, merged, par = 0, '', [], 0
    for c in body:
        if depth == 0:
            if c in '()':
                par += 1 if c == '(' else -1
            if par > 0:
                buf += c
                continue
            if c == '{':
                st = buf.strip()
                mm = re.search(r'([\w$]+)\s*\([^()]*(?:\([^()]*\)[^()]*)*\)\s*(?:throws[\w.,\s]+)?$', st)
                if mm and '$' not in mm.group(1) and mm.group(1) not in KEYWORDS and mm.group(1) != name \
                        and 'abstract ' not in st and not NOT_MERGED.search(st) \
                        and not re.search(r'\b(class|interface|enum|record)\b', st):
                    merged.append(mm.group(1))
                buf = ''
                depth = 1
                continue
            if c == ';':
                buf = ''
                continue
            if c == '}':
                break
            buf += c
        else:
            depth += {'{': 1, '}': -1}.get(c, 0)
    return sorted(set(merged))


def check(shim):
    """(file, rule, detail) for every breach in one layer's src"""
    out = []
    src = os.path.join(shim, 'src')
    for dp, _, fs in os.walk(src):
        for f in sorted(fs):
            if not f.endswith('.java'):
                continue
            p = os.path.join(dp, f)
            s = strip(open(p, encoding='utf-8', errors='replace').read())
            m = re.search(r'@Mixin\s*\(', s)
            name = f[:-5]
            if not m or re.search(r'\binterface\s+' + re.escape(name) + r'\b', s):
                continue
            i, depth = m.end(), 1
            while depth:
                depth += {'(': 1, ')': -1}.get(s[i], 0)
                i += 1
            pm = re.search(r'priority\s*=\s*([\w.]+)', s[m.end():i - 1])
            prio = pm.group(1) if pm else '1000'
            below = prio.endswith('PRIORITY') or prio.endswith('ROUTE_FIRST') or (prio.isdigit() and int(prio) < 1000)
            rel = os.path.relpath(p, shim).replace('\\', '/')
            if not below:
                merged = merged_members(s, name)
                if merged:
                    out.append((rel, 'restorer-at-' + prio, ','.join(merged)))
            elif not prio.endswith('ROUTE_FIRST'):
                inj = sorted({x.group(1) for x in INJECTOR.finditer(s)})
                # a restorer (merges members) below the mods, however its priority is spelled, or a mixin that names
                # itself a restorer (CbVanillaReads.PRIORITY): no injectors. A route that merges nothing and is spelled
                # otherwise (a literal, a route constant of its own) is left to its own judgement.
                if inj and (prio.endswith('CbVanillaReads.PRIORITY') or prio == 'PRIORITY' or merged_members(s, name)):
                    out.append((rel, 'injectors-at-' + prio, ','.join('@' + x for x in inj)))
    return out


def main(shims):
    bad = 0
    for shim in shims:
        rows = check(shim)
        for rel, rule, detail in rows:
            print(f'{shim}: {rel}: {rule}: {detail}')
        bad += len(rows)
    if bad:
        print(f'mixin priority gate: {bad} layer mixins break the priority rules (core/tools/mixin_priority.py header): '
              'restorers go to CbVanillaReads.PRIORITY, their injectors into a default-priority <name>Hooks mixin; '
              'a route that must run first declares CbVanillaReads.ROUTE_FIRST')
        sys.exit(1)
    print(f'mixin priority gate: {len(shims)} layer(s), 0 breaches')


if __name__ == '__main__':
    main(sys.argv[1:])
