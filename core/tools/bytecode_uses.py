"""What a method body does with each of its parameters -- the evidence gen_step_reshapes.py needs to tell
apart arguments of one type when a release reshaped a member.

Types alone cannot say which of two floats is which: 1.20.5's PanoramaRenderer.render(graphics, width,
height, fade, partialTick) is 1.20.4's render(delta, alpha) with the two floats in the other order, and
pairing them by position hands an override the alpha as its frame delta. What each parameter flows into
can: the delta advances the spin (multiplied by the panorama-speed option, then by 0.1 and 0.001), the
alpha is the cube map's fourth argument.

  uses(cls, method) -> [set of use features] per parameter
      a symbolic run of the body: every value on the stack and in the locals carries the parameters it was
      computed from, and where such a value is consumed the parameters get a feature -- the call it is
      passed to and at which argument (raw: index, callee descriptor and owner; the reader names the
      position as the n-th of its type from the start and from the end, ordinal()), or its receiver, the
      field it is stored to or read from,
      the arithmetic it takes part in and the constant on the other side, a comparison, a return
  call_sites(classes) -> {(name, desc): [per argument: set of producer features]}
      the vanilla callers of every member: for each argument position, what the caller passed (a constant,
      a field read, a call's result, a parameter of its own)

Branches are followed in order with the stack the branch had at its target (a ternary's two arms meet with
their values merged); nothing here needs more precision than "where does this value go".
Features name members by their name only (a call through a subclass names the same member); across the
1.21.11 -> 26.1 wall the caller translates names first.
"""
import struct

import classfile


def _resolve(cp, idx):
    pool, utf8 = cp["pool"], cp["utf8"]
    e = pool.get(idx)
    if e is None:
        return None
    tag, v = e
    if tag == 7:
        return ("class", utf8.get(v))
    if tag in (9, 10, 11):
        ci, nti = v
        owner = utf8.get(pool[ci][1]) if ci in pool else None
        nm, ds = pool[nti][1]
        return ("ref", owner, utf8.get(nm), utf8.get(ds))
    if tag == 18:
        bsm, nti = v
        nm, ds = pool[nti][1]
        return ("indy", None, utf8.get(nm), utf8.get(ds))
    if tag == 8:
        return ("const", "s:" + str(utf8.get(v)))
    if tag in (3, 5):
        return ("const", "i:%d" % v)
    if tag in (4, 6):
        return ("const", "f:%r" % float(v))
    if tag == 16:
        return ("const", "mt")
    return ("const", "?")


class V:
    """a symbolic value: the parameters it was computed from, what produced it, its slot size"""
    __slots__ = ("orig", "prod", "size")

    def __init__(self, orig=frozenset(), prod=None, size=1):
        self.orig, self.prod, self.size = orig, prod, size


_NONE = V()
_ARITH = {}
for _k, _n in enumerate(("add", "sub", "mul", "div", "rem")):
    for _t in range(4):
        _ARITH[0x60 + 4 * _k + _t] = _n
for _op, _n in ((0x78, "shl"), (0x79, "shl"), (0x7a, "shr"), (0x7b, "shr"), (0x7c, "ushr"), (0x7d, "ushr"),
                (0x7e, "and"), (0x7f, "and"), (0x80, "or"), (0x81, "or"), (0x82, "xor"), (0x83, "xor")):
    _ARITH[_op] = _n
_WIDE = {0x16, 0x18, 0x37, 0x39}  # lload dload lstore dstore (2-slot)


def _desc_args(desc):
    a, r = classfile.split_args(desc)
    return a, r


def _size(t):
    return 2 if t in ("J", "D") else 1


def ordinal(types, k, crename=None, from_end=False):
    """argument k of a descriptor's arguments as "<type>:<n>", the n-th of that type (0-based; from_end:
    "<type>:-<n>", counted from the last); the type's class part is its simple name (crename translates
    it first across the wall)"""
    t = types[k] if k < len(types) else "?"
    n = -sum(1 for x in types[k:] if x == t) if from_end else sum(1 for x in types[:k] if x == t)
    if t.endswith(";") and crename:
        t = "L" + crename(t.lstrip("[")[1:-1]) + ";"
    simple = t.rstrip(";").rsplit("/", 1)[-1].rsplit("$", 1)[-1] if t.endswith(";") else t
    return "%s:%d" % (simple, n)


def _describe(v):
    """the other operand of an arithmetic or comparison, as a feature part"""
    if v.prod and v.prod.startswith("const:"):
        return v.prod
    return "p" if v.orig else "v"


def run(cls, method, on_use=None, on_call=None, rename=None, crename=None):
    """Run the body symbolically. on_use(param, feature) for every consumption of a parameter-derived
    value; on_call(owner, name, desc, args) at every invoke (args: V per argument, receiver excluded).
    rename(owner, name) translates a member name (across the wall) before it goes into a feature."""
    nm_ = rename or (lambda o, n: n)
    code, cp = method.code, cls.cp
    if code is None or cp is None:
        return
    static = bool(method.access & classfile.ACC_STATIC)
    args, _ = _desc_args(method.desc)
    locals_ = {}
    slot = 0 if static else 1
    if not static:
        locals_[0] = frozenset()
    for k, t in enumerate(args):
        locals_[slot] = frozenset([k])
        slot += _size(t)
    stack = []
    targets = {}
    dead = False

    def use(v, feat):
        if on_use:
            for p in v.orig:
                on_use(p, feat)

    def pop():
        return stack.pop() if stack else _NONE

    def push(v):
        stack.append(v)

    def snap(t):
        prev = targets.get(t)
        cur = list(stack)
        if prev is None or len(prev) != len(cur):
            targets[t] = cur
        else:
            targets[t] = [V(a.orig | b.orig, a.prod if a.prod == b.prod else None, a.size) for a, b in zip(prev, cur)]

    i = 0
    n = len(code)
    while i < n:
        if i in targets:
            if dead:
                stack = list(targets[i])
            elif len(targets[i]) == len(stack):
                stack = [V(a.orig | b.orig, a.prod if a.prod == b.prod else None, a.size) for a, b in zip(stack, targets[i])]
        elif dead:
            stack = []
        dead = False
        op = code[i]
        nxt = None
        if op == 0x00:
            pass
        elif op == 0x01:
            push(V(prod="const:null"))
        elif 0x02 <= op <= 0x08:
            push(V(prod="const:i:%d" % (op - 3)))
        elif op in (0x09, 0x0a):
            push(V(prod="const:i:%d" % (op - 9), size=2))
        elif 0x0b <= op <= 0x0d:
            push(V(prod="const:f:%r" % float(op - 0x0b)))
        elif op in (0x0e, 0x0f):
            push(V(prod="const:f:%r" % float(op - 0x0e), size=2))
        elif op == 0x10:
            push(V(prod="const:i:%d" % struct.unpack(">b", code[i + 1:i + 2])[0]))
        elif op == 0x11:
            push(V(prod="const:i:%d" % struct.unpack(">h", code[i + 1:i + 3])[0]))
        elif op in (0x12, 0x13, 0x14):
            idx = code[i + 1] if op == 0x12 else struct.unpack(">H", code[i + 1:i + 3])[0]
            r = _resolve(cp, idx)
            push(V(prod="const:" + (r[1] if r and r[0] == "const" else "class:%s" % (r[1] if r else "?")), size=2 if op == 0x14 else 1))
        elif 0x15 <= op <= 0x19:
            s = code[i + 1]
            push(V(locals_.get(s, frozenset()), "local", 2 if op in _WIDE else 1))
        elif 0x1a <= op <= 0x2d:
            s = (op - 0x1a) % 4
            kind = (op - 0x1a) // 4
            push(V(locals_.get(s, frozenset()), "local", 2 if kind in (1, 3) else 1))
        elif 0x2e <= op <= 0x35:
            ix, arr = pop(), pop()
            use(arr, ("arrayload",))
            use(ix, ("arrayindex",))
            push(V(arr.orig, "arrayload", 2 if op in (0x2f, 0x31) else 1))
        elif 0x36 <= op <= 0x3a:
            v = pop()
            locals_[code[i + 1]] = v.orig
        elif 0x3b <= op <= 0x4e:
            v = pop()
            locals_[(op - 0x3b) % 4] = v.orig
        elif 0x4f <= op <= 0x56:
            val, ix, arr = pop(), pop(), pop()
            use(val, ("arraystore",))
            use(ix, ("arrayindex",))
            use(arr, ("arraywritten",))
        elif op == 0x57:
            pop()
        elif op == 0x58:
            v = pop()
            if v.size == 1:
                pop()
        elif op == 0x59:
            v = pop()
            push(v)
            push(v)
        elif op == 0x5a:
            a, b = pop(), pop()
            push(a), push(b), push(a)
        elif op == 0x5b:
            a, b = pop(), pop()
            c = pop() if b.size == 1 else None
            push(a)
            if c:
                push(c)
            push(b), push(a)
        elif op == 0x5c:
            a = pop()
            if a.size == 2:
                push(a), push(a)
            else:
                b = pop()
                push(b), push(a), push(b), push(a)
        elif op == 0x5d:
            a = pop()
            if a.size == 2:
                b = pop()
                push(a), push(b), push(a)
            else:
                b, c = pop(), pop()
                push(b), push(a), push(c), push(b), push(a)
        elif op == 0x5e:
            a = pop()
            top = [a] if a.size == 2 else [pop(), a]
            b = pop()
            under = [b] if b.size == 2 else [pop(), b]
            for v in top + under + top:
                push(v)
        elif op == 0x5f:
            a, b = pop(), pop()
            push(a), push(b)
        elif op in _ARITH:
            b, a = pop(), pop()
            name = _ARITH[op]
            use(a, ("arith", name, "l", _describe(b)))
            use(b, ("arith", name, "r", _describe(a)))
            push(V(a.orig | b.orig, "arith:" + name, a.size))
        elif 0x74 <= op <= 0x77:
            a = pop()
            use(a, ("neg",))
            push(V(a.orig, "neg", a.size))
        elif op == 0x84:
            s, c = code[i + 1], struct.unpack(">b", code[i + 2:i + 3])[0]
            for p in locals_.get(s, frozenset()):
                if on_use:
                    on_use(p, ("iinc", c))
        elif 0x85 <= op <= 0x93:
            a = pop()
            size = 2 if op in (0x85, 0x87, 0x8a, 0x8c, 0x8d, 0x8f) else 1
            push(V(a.orig, a.prod, size))
        elif 0x94 <= op <= 0x98:
            b, a = pop(), pop()
            use(a, ("cmp", _describe(b)))
            use(b, ("cmp", _describe(a)))
            push(V(a.orig | b.orig, "cmp"))
        elif 0x99 <= op <= 0x9e:
            a = pop()
            use(a, ("if", op))
            snap(i + struct.unpack(">h", code[i + 1:i + 3])[0])
        elif 0x9f <= op <= 0xa6:
            b, a = pop(), pop()
            use(a, ("ifcmp", _describe(b)))
            use(b, ("ifcmp", _describe(a)))
            snap(i + struct.unpack(">h", code[i + 1:i + 3])[0])
        elif op == 0xa7:
            snap(i + struct.unpack(">h", code[i + 1:i + 3])[0])
            dead = True
        elif op in (0xa8, 0xa9):
            dead = op == 0xa9
        elif op in (0xaa, 0xab):
            a = pop()
            use(a, ("switch",))
            j = i + 1 + (3 - i % 4)
            default = struct.unpack(">i", code[j:j + 4])[0]
            snap(i + default)
            if op == 0xaa:
                low, high = struct.unpack(">ii", code[j + 4:j + 12])
                for k in range(high - low + 1):
                    snap(i + struct.unpack(">i", code[j + 12 + 4 * k:j + 16 + 4 * k])[0])
                nxt = j + 12 + 4 * (high - low + 1)
            else:
                npairs = struct.unpack(">i", code[j + 4:j + 8])[0]
                for k in range(npairs):
                    snap(i + struct.unpack(">i", code[j + 12 + 8 * k:j + 16 + 8 * k])[0])
                nxt = j + 8 + 8 * npairs
            dead = True
        elif 0xac <= op <= 0xb0:
            use(pop(), ("return",))
            dead = True
        elif op == 0xb1:
            dead = True
        elif 0xb2 <= op <= 0xb5:
            r = _resolve(cp, struct.unpack(">H", code[i + 1:i + 3])[0]) or ("ref", "?", "?", "?")
            _, owner, name, desc = r
            name = nm_(owner, name)
            sz = _size(desc)
            if op == 0xb2:
                push(V(prod="get:" + name, size=sz))
            elif op == 0xb3:
                use(pop(), ("put", name))
            elif op == 0xb4:
                obj = pop()
                use(obj, ("getfrom", name))
                push(V(obj.orig, "get:" + name, sz))
            else:
                val, obj = pop(), pop()
                use(val, ("put", name))
                use(obj, ("putinto", name))
        elif 0xb6 <= op <= 0xba:
            r = _resolve(cp, struct.unpack(">H", code[i + 1:i + 3])[0]) or ("ref", "?", "?", "()V")
            _, owner, rawname, desc = r
            name = nm_(owner, rawname) if op != 0xba else rawname
            a, ret = _desc_args(desc)
            vals = [pop() for _ in a][::-1]
            recv = pop() if op in (0xb6, 0xb7, 0xb9) else None
            for k, v in enumerate(vals):
                # raw: (kind, name, argument index, the callee's descriptor, its owner) -- the reader turns the
                # index into ordinals (ordinal()), translating it first when the callee was reshaped too
                use(v, ("indy", name, k, desc, owner) if op == 0xba else ("call", rawname, k, desc, owner))
            # the call's shape as far as the parameters go, whatever the callee: how often each parameter is
            # handed over in it, and in which order the parameters of one type first appear among its
            # arguments (1.20.4's blit(.., width, height, texWidth, texHeight) passes width and height twice
            # each, then the texture size -- 1.21.2's does the same into a reshaped callee)
            if on_use:
                count, order = {}, {}
                for v in vals:
                    if len(v.orig) == 1:
                        p = next(iter(v.orig))
                        count[p] = count.get(p, 0) + 1
                        seq = order.setdefault(args[p] if p < len(args) else "?", [])
                        if p not in seq:
                            seq.append(p)
                for p, c in count.items():
                    on_use(p, ("handed", c))
                for seq in order.values():
                    if len(seq) > 1:
                        for r, p in enumerate(seq):
                            on_use(p, ("rank", r, len(seq)))
            if recv is not None:
                use(recv, ("recv", name))
            if on_call and op != 0xba:
                on_call(owner, rawname, desc, vals)
            if ret != "V":
                orig = frozenset().union(*[v.orig for v in vals]) if vals else frozenset()
                if recv is not None:
                    orig = orig | recv.orig
                push(V(orig, ("indy:" if op == 0xba else "ret:") + name, _size(ret)))
        elif op == 0xbb:
            r = _resolve(cp, struct.unpack(">H", code[i + 1:i + 3])[0])
            push(V(prod="new:%s" % (r[1] if r else "?")))
        elif op in (0xbc, 0xbd):
            c = pop()
            use(c, ("arraysize",))
            push(V(prod="newarray"))
        elif op == 0xbe:
            a = pop()
            use(a, ("arraylength",))
            push(V(a.orig, "arraylength"))
        elif op == 0xbf:
            use(pop(), ("throw",))
            dead = True
        elif op == 0xc0:
            a = pop()
            r = _resolve(cp, struct.unpack(">H", code[i + 1:i + 3])[0])
            use(a, ("cast", r[1] if r else "?"))
            push(a)
        elif op == 0xc1:
            a = pop()
            r = _resolve(cp, struct.unpack(">H", code[i + 1:i + 3])[0])
            use(a, ("instanceof", r[1] if r else "?"))
            push(V(a.orig, "instanceof"))
        elif op in (0xc2, 0xc3):
            pop()
        elif op == 0xc4:
            sub = code[i + 1]
            s = struct.unpack(">H", code[i + 2:i + 4])[0]
            if 0x15 <= sub <= 0x19:
                push(V(locals_.get(s, frozenset()), "local", 2 if sub in _WIDE else 1))
            elif 0x36 <= sub <= 0x3a:
                locals_[s] = pop().orig
            elif sub == 0x84:
                c = struct.unpack(">h", code[i + 4:i + 6])[0]
                for p in locals_.get(s, frozenset()):
                    if on_use:
                        on_use(p, ("iinc", c))
            nxt = i + (6 if sub == 0x84 else 4)
        elif op == 0xc5:
            dims = code[i + 3]
            for _ in range(dims):
                use(pop(), ("arraysize",))
            push(V(prod="newarray"))
        elif op in (0xc6, 0xc7):
            use(pop(), ("ifnull",))
            snap(i + struct.unpack(">h", code[i + 1:i + 3])[0])
        elif op in (0xc8, 0xc9):
            snap(i + struct.unpack(">i", code[i + 1:i + 5])[0])
            dead = op == 0xc8
        else:
            raise ValueError("unknown opcode 0x%02x at %d" % (op, i))
        i = nxt if nxt is not None else i + 1 + classfile._LEN[op]


def uses(cls, method, rename=None, crename=None):
    """per parameter, the set of use features of the body (None: no body). rename(owner, name)
    translates member names (the wall step)."""
    if method.code is None or cls.cp is None:
        return None
    n = len(classfile.split_args(method.desc)[0])
    out = [set() for _ in range(n)]
    try:
        run(cls, method, on_use=lambda p, feat: out[p].add(feat), rename=rename, crename=crename)
    except (ValueError, KeyError, IndexError, struct.error):
        return None
    return out


def call_sites(classes, rename=None, crename=None):
    """{(name, desc): {owner: [per argument position: set of producer features]}} over every body of the jar.
    The key is the callee as the call site names it; producers name members after rename."""
    index = {}

    def prod(v, caller):
        # a value computed from the caller's own parameters: which parameters of which member (a bridge or
        # a delegating overload hands its parameters on in its own contract's order, the same in V and W)
        out = [v.prod or "?"] if not v.orig or (v.prod and v.prod not in ("local", "?")) else []
        if v.orig:
            out.append("param:%s#%s" % (caller, ",".join(ordinal(cargs, p, crename) for p in sorted(v.orig))))
        return out

    for c in classes.values():
        if c.cp is None:
            continue
        for m in c.methods:
            if m.code is None:
                continue
            caller = rename(c.name, m.name) if rename else m.name
            cargs = classfile.split_args(m.desc)[0]

            def on_call(owner, name, desc, vals, caller=caller, cargs=cargs):
                slot = index.setdefault((name, desc), {}).setdefault(owner, [set() for _ in vals])
                if len(slot) != len(vals):
                    return
                for k, v in enumerate(vals):
                    slot[k].update(prod(v, caller))

            try:
                run(c, m, on_call=on_call, rename=rename, crename=crename)
            except (ValueError, KeyError, IndexError, struct.error):
                continue
    return index
