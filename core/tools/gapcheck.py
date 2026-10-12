"""Cross the owner-qualified stub diffs against what the bridge actually handles.

Every damaged symbol must land in exactly one bucket: handled (cover / redirect
/ rename / facade), ledgered with a reason, or OPEN.  OPEN is the kill list --
percentage floors are not an argument, an unexplained OPEN symbol is a crash
waiting for the player who happens to use that feature.
"""
import json
import sys
from collections import Counter, defaultdict

import glob as _glob
SHIMS = sorted(_glob.glob("core/segments/shims-*.json"))
DIFFS = [
    ("1.20.1->1.20.2", "core/out-diff/diff-1.20.1-1.20.2.tsv"),
    ("1.20.2->1.20.3", "core/out-diff/diff-1.20.2-1.20.3.tsv"),
    ("1.20.3->1.20.4", "core/out-diff/diff-1.20.3-1.20.4.tsv"),
    ("1.20.4->1.20.5", "core/out-diff/diff-1.20.4-1.20.5.tsv"),
    ("1.20.5->1.20.6", "core/out-diff/diff-1.20.5-1.20.6.tsv"),
    ("1.20.6->1.21",   "core/out-diff/diff-1.20.6-1.21.tsv"),
    ("1.21->1.21.1",    "core/out-diff/diff-1.21-1.21.1.tsv"),
    ("1.21.1->1.21.2",  "core/out-diff/diff-1.21.1-1.21.2.tsv"),
    ("1.21.2->1.21.3",  "core/out-diff/diff-1.21.2-1.21.3.tsv"),
    ("1.21.3->1.21.4",  "core/out-diff/diff-1.21.3-1.21.4.tsv"),
    ("1.21.4->1.21.5",  "core/out-diff/diff-1.21.4-1.21.5.tsv"),
    ("1.21.5->1.21.6",  "core/out-diff/diff-1.21.5-1.21.6.tsv"),
    ("1.21.6->1.21.7",  "core/out-diff/diff-1.21.6-1.21.7.tsv"),
    ("1.21.8->1.21.9",  "core/out-diff/diff-1.21.8-1.21.9.tsv"),
    ("1.21.9->1.21.10", "core/out-diff/diff-1.21.9-1.21.10.tsv"),
    ("1.21.10->1.21.11","core/out-diff/diff-1.21.10-1.21.11.tsv"),
    ("1.21.11->26.1",   "core/out-diff/diff-1.21.11-26.1.tsv"),
    ("26.1->26.1.1",    "core/out-diff/diff-26.1-26.1.1.tsv"),
    ("26.1.1->26.1.2",  "core/out-diff/diff-26.1.1-26.1.2.tsv"),
    ("26.1.2->26.2",    "core/out-diff/diff-26.1.2-26.2.tsv"),
]

_sides = json.load(open("core/segments/sides-1.20.1.json", encoding="utf-8"))
CLIENT_ONLY = set(_sides.get("client", []))
DATAGEN = set(_sides.get("datagen", []))


def side_of(owner):
    """Datagen classes never execute in play; client classes only matter to a
    player's game, never to a dedicated server."""
    for probe in (owner, owner.split("$")[0]):
        if probe in DATAGEN:
            return "datagen"
        if probe in CLIENT_ONLY:
            return "client"
    return "common"


covers, redirects, renames, class_renames = set(), {}, {}, {}
getters, ctors, hooked = set(), set(), set()
ledger = {}
for path in SHIMS:
    d = json.load(open(path, encoding="utf-8"))
    covers |= set(d.get("covers", []))
    for key in ("staticRedirects", "instanceRedirects", "fieldRedirects", "superRedirects"):
        redirects.update(d.get(key, {}))
    for key in ("fieldGetters", "instanceFieldGetters", "instanceFieldSetters"):
        getters |= set(d.get(key, {}))
    for key in ("ctorRedirects", "superCtorReshapes", "superCtorAdapts"):
        ctors |= set(d.get(key, {}))
    # owner-agnostic: the converter rewrites every site/override of that name+desc
    for h in d.get("overrideHooks", []):
        hooked.add(h["old"])
    for name, descs in d.get("lookupHooks", {}).items():
        if descs:
            hooked.add(name + descs[0])
    for key in ("methodRenames", "fieldRenames"):
        renames.update(d.get(key, {}))
    class_renames.update(d.get("classRenames", {}))
    ledger.update(d.get("ledger", {}))


def ledger_reason(owner, name, fate, desc=None):
    """Ledger keys are the exact shape (owner.name(desc) / owner.name:desc), every
    member of that name (owner.name), or -- only for a member whose class is itself
    gone (CLASS_GONE) -- the owner or the outer class of an inner class. A class
    entry cannot account for a dead member of a class that survives: that is a
    real NoSuchMethodError/NoSuchFieldError needing its own disposition."""
    keys = [f"{owner}.{name}"]
    if desc:
        keys.insert(0, f"{owner}.{name}{desc}" if desc.startswith("(") else f"{owner}.{name}:{desc}")
    if fate == "CLASS_GONE":
        keys += [owner, owner.split("$")[0]]
    for key in keys:
        if key in ledger:
            return ledger[key]
    return None


MAPPED = ("method_", "field_", "comp_", "<init>")


def is_noise(name):
    """Intermediary only names members that have a stable identity; the rest keep
    their obfuscated name and get reshuffled every release. A mod cannot compile
    against an obf name, so churn there is not damage the bridge has to carry.
    Synthetic access$/lambda$ bodies are likewise never call targets."""
    if name.startswith(("access$", "lambda$", "this$", "val$")):
        return True
    return not name.startswith(MAPPED)


def verdict(kind, fate, symbol, desc):
    owner, name = symbol.rsplit(".", 1)
    full = f"{symbol}{desc}" if kind == "method" else f"{symbol}:{desc}"
    if full in covers:
        return "cover", ""
    if full in redirects:
        return "redirect", ""
    if full in getters:
        return "getter", ""
    if full in ctors:
        return "ctor", ""
    if kind == "method" and f"{name}{desc}" in hooked:
        return "hook", ""
    if full in renames or symbol in renames:
        return "rename", ""
    if owner in class_renames:
        return "facade", ""
    reason = ledger_reason(owner, name, fate, desc)
    if reason:
        return "ledger", reason
    return "OPEN", ""


for label, path in DIFFS:
    try:
        rows = open(path, encoding="utf-8").read().splitlines()[1:]
    except FileNotFoundError:
        continue
    buckets = Counter()
    open_by_fate = Counter()
    open_by_side = Counter()
    open_by_owner = Counter()
    open_rows = []
    for line in rows:
        if not line.strip():
            continue
        parts = line.split("\t")
        kind, fate, symbol, dsc = parts[0], parts[1], parts[2], parts[3]
        if is_noise(symbol.rsplit(".", 1)[1]):
            buckets["obf-churn"] += 1
            continue
        if len(parts) > 5 and parts[5] == "priv":
            # no call site can reach a private member; only a mod mixin can
            # (@Shadow/@Accessor), and triage handles those from segment data
            buckets["private-mixin-only"] += 1
            continue
        bucket, _ = verdict(kind, fate, symbol, dsc)
        buckets[bucket] += 1
        if bucket == "OPEN":
            owner = symbol.rsplit(".", 1)[0]
            side = side_of(owner)
            open_by_fate[fate] += 1
            open_by_side[side] += 1
            open_by_owner[owner] += 1
            open_rows.append((side, fate, symbol, dsc, parts[4] if len(parts) > 4 else ""))

    print(f"=== {label}: {len(rows)} damaged symbols")
    for k, v in buckets.most_common():
        print(f"    {v:6d}  {k}")
    if open_rows:
        print(f"    OPEN by fate: {dict(open_by_fate)}")
        print(f"    OPEN by side: {dict(open_by_side)}")
        print(f"    OPEN owners (top 15):")
        for owner, n in open_by_owner.most_common(15):
            print(f"      {n:5d}  {owner}")
        out = path.replace("diff-", "open-")
        with open(out, "w", encoding="utf-8") as f:
            f.write("fate\tsymbol\tdesc\tdetail\n")
            for r in open_rows:
                f.write("\t".join(r) + "\n")
        print(f"    -> {out}")
    print()
