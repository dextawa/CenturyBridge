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
3. layer order: a climbing earlier layer's mixins @Shadow / @Accessor / @Invoker members a later layer restores, and
   Mixin applies a target's mixins by priority, breaking ties by load order (MixinInfo.compareTo), so a later layer's
   restorers sit strictly below every earlier layer's mixin. The layer's CbVanillaReads.PRIORITY is
   1 - (index of its release - index of 1.20.2) in Release.ALL (1.20.2: 1, 1.20.3: 0, ... 26.2: -19); a restorer (merged
   methods, or intermediary-named fields it declares) spells its priority CbVanillaReads.PRIORITY, never a literal; no
   other mixin of the layer sits at or below that value.

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


def releases():
    """Release.ALL, the engine's release list (core/src/.../res/Release.java)"""
    here = os.path.dirname(os.path.abspath(__file__))
    rel = os.path.join(here, '..', 'src', 'top', 'dext', 'centurybridge', 'res', 'Release.java')
    s = re.sub(r'//.*', '', open(rel, encoding='utf-8').read())  # not strip(): it blanks string literals
    body = s[s.index('ALL = List.of('):]
    return re.findall(r'"([\d.]+)"', body[:body.index(');')])


def layer_priority(shim):
    """rule 3's CbVanillaReads.PRIORITY for the layer at shim (core/shims/1.20.2 or core/shims/steps/<W>), or None"""
    p = os.path.normpath(os.path.abspath(shim)).replace('\\', '/').rstrip('/')
    ver = p.rsplit('/', 1)[-1]
    all_ = releases()
    if ver not in all_ or not ('/steps/' in p or ver == '1.20.2'):
        return None
    return 1 - (all_.index(ver) - all_.index('1.20.2'))


RESTORED_FIELD = re.compile(r'((?:@[\w.]+(?:\([^)]*\))?\s*)*)(?:(?:private|protected|public|static|final|transient|volatile)\s+)+'
                            r'[\w.$<>\[\], ]+?\s+((?:field|comp)_\d+)\s*[;=]')


def restored_fields(s, name):
    """intermediary-named fields the class mixin declares unannotated (merged onto the target: a restored V field)"""
    head = re.search(r'\bclass\s+' + re.escape(name) + r'\b', s)
    if not head:
        return []
    return sorted({m.group(2) for m in RESTORED_FIELD.finditer(s[head.end():]) if not m.group(1).strip()})


def check(shim):
    """(file, rule, detail) for every breach in one layer's src"""
    out = []
    src = os.path.join(shim, 'src')
    want = layer_priority(shim)
    for dp, _, fs in os.walk(src):
        for f in fs:
            if f == 'CbVanillaReads.java' and want is not None:
                v = re.search(r'\bint\s+PRIORITY\s*=\s*(-?\d+)\s*;', strip(open(os.path.join(dp, f), encoding='utf-8').read()))
                if not v or int(v.group(1)) != want:
                    out.append((os.path.relpath(os.path.join(dp, f), shim).replace('\\', '/'), 'layer-order',
                                'PRIORITY = %s, rule 3 wants %d' % (v.group(1) if v else '?', want)))
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
            pm = re.search(r'priority\s*=\s*(-?[\w.]+)', s[m.end():i - 1])
            prio = pm.group(1) if pm else '1000'
            literal = re.fullmatch(r'-?\d+', prio) is not None
            below = prio.endswith('PRIORITY') or prio.endswith('ROUTE_FIRST') or (literal and int(prio) < 1000)
            rel = os.path.relpath(p, shim).replace('\\', '/')
            restores = merged_members(s, name) + restored_fields(s, name)
            if want is not None and restores and not prio.endswith('CbVanillaReads.PRIORITY') and prio != 'PRIORITY':
                out.append((rel, 'layer-order-restorer-at-' + prio, ','.join(restores)))
            elif want is not None and not restores and literal and int(prio) <= want:
                out.append((rel, 'layer-order-at-or-below-' + str(want), prio))
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
