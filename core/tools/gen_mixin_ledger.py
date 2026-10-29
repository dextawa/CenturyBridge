"""The mixin ledger of one climb step: the open mixin damage rows that have no hook point in W.

A mixin names a point of V's code; when W no longer has that code anywhere, no rewriting of the mixin
can give the mod its point back -- the injector stays disabled (or the mixin stripped) and the loss is
reported. This writes those rows down per member, with the reason Main mixin-cause read off V's and W's
bytecode (mixin_step.sh runs it after every measurement: <step>/cause-mixin.tsv), so the rows left open
without a ledger entry are the step's real mixin work list.

Ledgered (each reason is what mixin-cause verified; nothing else is):
  class-gone               the target class (an intermediary name: stable across releases) is gone from W and no
                           classRenames sends it to a vanilla class (a mojang-named class W lacks may only have
                           been renamed: class-missing-unwired, left open)
  class-renamed-to-layer   gone from W; classRenames sends mods' references to a layer class vanilla never runs
  target-private-gone      a private / package member gone from W; no member of the class holds its code
                           (methods: MixinBridge.bodyFate found none; fields: no field with the same uses)
  target-wired: ... no W method holds its code
                           gone from W; the step's wiring rebuilds it for mods' calls in layer code (redirects),
                           vanilla no longer runs it, and no W method holds its code (mixin-cause searched W's bodies;
                           "disabled, target gone" did not search, so it stays open)
Never ledgered: a member or class the step's other fragments say W runs elsewhere (mixinMoves, mixinTargets,
mixinAnchorMoves, mixinSplits) -- moved code -- or give back (fieldGetters / staticFieldSetters).
  anchor-member-gone / anchor-private-gone / constant-gone   (positional @Inject rows only; never
                           anchor-unused-here: there the member still exists and only moved)
                           the anchor no longer stands in the target, in what the target calls (depth 2) or in
                           the lambdas it makes, and no single instruction of its kind stands between the same
                           neighbours (MixinBridge's stand-in search); an anchor on an API member W dropped is
                           the API side's work, and call-handling injectors were never searched for a stand-in
  target-moved-up-anchor   W dropped the override (MixinBridge restores it as a super call for HEAD / RETURN);
                           an anchor of V's override body has no code to stand in
Left open (mechanisms or a step agent's judgment): moved code (moved-private-member: another W class holds the
member's id or its code; class-moved: the class id under another outer class), the API side still open
(target-api), members moved up,
reshaped or only similar, anchors moved into callees with several call sites, wired anchors, counts and
ordinals, constants with a candidate, renamed vanilla classes.

  python gen_mixin_ledger.py [--strict] [--keep-open <keys.json>] <cause-mixin.tsv> <out fragment.json>
      (--strict leaves rows on members the wiring speaks of open: facade classes, redirects, covers, reshapes)
      writes {"mixinLedger": {key: reason}}: key "<owner>" (class), "<owner>.<target>" (member) or
      "<owner>.<target> @ <anchor>" (one anchor of a member)
      (--keep-open, opt-in: a step agent's judgment that some rows are moved code, not removed code -- e.g. behaviour
      W runs from data -- so the generated ledger must never write them down. keys.json is {"keepOpen": [key, ...]}
      or a plain list, keys in the same three forms; a row is skipped when its owner, its member or its anchor key is
      listed. Keep the keys file beside the fragment so regeneration stays reproducible.)
  python gen_mixin_ledger.py --count <open-mixin.tsv> <wiring.json>
      prints how many open rows the wiring's mixinLedger (base file and fragments) covers
"""
import collections
import glob
import json
import os
import sys

# anchors whose own member is not API (an API member gone is the step's API work: wired, MixinBridge carries
# the anchor), judged only for positional @Inject rows -- the only injectors MixinBridge looks for a stand-in for
# not "anchor-unused-here": the anchor's member still exists, the code moved -- mechanism work, never ledger
ANCHOR_GONE = {"anchor-member-gone", "anchor-private-gone", "constant-gone"}


def reason(cause, mojang, target, anchor, evidence, usage):
    if cause == "class-gone":
        return "class", "class gone from W, no classRenames to a vanilla class"
    if cause == "class-renamed-to-layer":
        to = evidence.split(" -> ")[-1]
        return "class", f"class gone from W; mods go to the layer's {to.split('/')[-1]}, which vanilla never runs"
    if cause == "target-private-gone":
        return "member", "private member gone from W; no member of the class holds its code"
    if cause.startswith("target-wired") and "no W method holds its code" in cause:
        # only the cause that searched W's bodies; "disabled, target gone" never looked (s9: DripParticle's
        # providers at 1.21.9, whose code W runs in another class, were ledgered through it)
        how = cause.split(" / ")[-1] if " / " in cause else "the step's wiring"
        return "member", f"gone from W; rebuilt for mods' calls only ({how}); no W method holds its code"
    if cause == "target-moved-up-anchor":
        return "anchor", "W dropped this override (the class inherits it now): the anchor was in the override's own body, which no longer exists"
    if cause in ANCHOR_GONE and usage.startswith("inject-"):
        return "anchor", "anchor gone: not in the target, its callees (depth 2) or lambdas; no instruction of its kind between the same neighbours"
    return None, None


def key_of(kind, owner, target, anchor):
    return owner if kind == "class" else f"{owner}.{target}" if kind == "member" else f"{owner}.{target} @ {anchor}"


# --strict: rows on members the step's wiring speaks of (a facade class, a redirect / cover / reshape) are
# left open: whether the layer's code gives a hook back is the area agent's question, not a removal
WIRED = ("class-renamed-to-layer", "target-wired")


def keep_open_keys(path):
    if path is None:
        return set()
    with open(path, encoding="utf-8") as f:
        j = json.load(f)
    return set(j.get("keepOpen", []) if isinstance(j, dict) else j)


def moved_by_wiring(out):
    """the members and classes the step's other fragments say W runs elsewhere -- moved code, never ledgered:
    mixinMoves / mixinTargets / mixinAnchorMoves keys ("owner.name(desc)" -> "owner.name"), mixinSplits classes,
    and the static fields the wiring gives back (fieldGetters / staticFieldSetters keys "owner.name:desc": mods'
    @Shadow / @Accessor on them are served there)"""
    members, classes = set(), set()
    for p in sorted(glob.glob(os.path.join(os.path.dirname(os.path.abspath(out)), "*.json"))):
        if os.path.abspath(p) == os.path.abspath(out):
            continue
        with open(p, encoding="utf-8") as f:
            j = json.load(f)
        for sec in ("fieldGetters", "staticFieldSetters"):
            for k in j.get(sec, {}):
                members.add(k)
        for sec in ("mixinMoves", "mixinTargets", "mixinAnchorMoves"):
            for k in j.get(sec, {}):
                members.add(k.split("(", 1)[0])
                members.add(k)
        classes.update(j.get("mixinSplits", {}))
    return members, classes


def generate(cause_tsv, out, strict=False, keep_open=None):
    keep = keep_open_keys(keep_open)
    moved_members, moved_classes = moved_by_wiring(out)
    led = {}
    by = collections.Counter()
    with open(cause_tsv, encoding="utf-8") as f:
        head = next(f).rstrip("\n").split("\t")
        ix = {n: i for i, n in enumerate(head)}
        for line in f:
            r = line.rstrip("\n").split("\t")
            if len(r) < len(head):
                continue  # a broken line (evidence text with a line break)
            if strict and r[ix["cause"]].startswith(WIRED):
                continue
            o, t, a = r[ix["owner"]], r[ix["target"]], r[ix["anchor"]]
            if o in keep or f"{o}.{t}" in keep or f"{o}.{t} @ {a}" in keep:
                continue
            if o in moved_classes or f"{o}.{t}" in moved_members or f"{o}.{t.split('(', 1)[0]}" in moved_members:
                continue
            kind, why = reason(r[ix["cause"]], r[ix["mojang"]], r[ix["target"]], r[ix["anchor"]], r[ix["evidence"]], r[ix["usage"]])
            if kind is None:
                continue
            led.setdefault(key_of(kind, r[ix["owner"]], r[ix["target"]], r[ix["anchor"]]), why)
            by[r[ix["cause"]].split(":")[0]] += 1
    with open(out, "w", encoding="utf-8", newline="\n") as o:
        json.dump({"_mixinLedger_basis": "gen_mixin_ledger.py from mixin-cause (V/W bytecode); regenerate, never hand-edit",
                   "mixinLedger": dict(sorted(led.items()))}, o, indent=1, ensure_ascii=False)
        o.write("\n")
    print(f"mixin ledger: {len(led)} entries covering {sum(by.values())} open rows -> {out}")
    print("  " + ", ".join(f"{c} {n}" for c, n in by.most_common()))


def ledger_of(wiring):
    led = {}
    files = ([wiring] if os.path.exists(wiring) else []) + sorted(glob.glob(wiring + ".d/*.json"))
    for p in files:
        with open(p, encoding="utf-8") as f:
            led.update(json.load(f).get("mixinLedger", {}))
    return led


def count(open_tsv, wiring):
    led = ledger_of(wiring)
    n = total = 0
    with open(open_tsv, encoding="utf-8") as f:
        head = next(f).rstrip("\n").split("\t")
        ix = {k: i for i, k in enumerate(head)}
        for line in f:
            r = line.rstrip("\n").split("\t")
            if len(r) < len(head):
                continue
            total += 1
            o, t, a = r[ix["owner"]], r[ix["target"]], r[ix["anchor"]]
            if o in led or f"{o}.{t}" in led or f"{o}.{t} @ {a}" in led:
                n += 1
    print(f"  {n} of the {total} open rows are ledgered (mixinLedger: no hook point in W), {total - n} are the work list")


if __name__ == "__main__":
    if sys.argv[1] == "--count":
        count(sys.argv[2], sys.argv[3])
    else:
        args = sys.argv[1:]
        strict = "--strict" in args
        if strict:
            args.remove("--strict")
        keep_open = None
        if "--keep-open" in args:
            i = args.index("--keep-open")
            keep_open = args[i + 1]
            del args[i:i + 2]
        generate(args[0], args[1], strict=strict, keep_open=keep_open)
