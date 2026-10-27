"""Audit of the climb steps' mixin ledgers: which ledgered rows are real losses.

Reads a step's cause-mixin.tsv (mixin-cause output) and the step's mixinLedger (every fragment), and groups the
ledgered rows by cause and by target member (owner.target): one member yields many rows (each INVOKE, constant,
field access and ordinal on it). Draws a stratified sample of members per cause for hand judgment with javap.

  python ledger_audit.py <cause-mixin.tsv> <wiring.json> [--sample N] [--seed S] [--out <prefix>]
      prints rows / members per cause and the ledger key kind; --out writes <prefix>-rows.tsv (ledgered rows,
      with the member key and ledger key) and <prefix>-sample.tsv (sampled members: cause, rows, one example row)
"""
import collections
import glob
import json
import os
import random
import sys


def ledger_of(wiring):
    led = {}
    files = ([wiring] if os.path.exists(wiring) else []) + sorted(glob.glob(wiring + ".d/*.json"))
    for p in files:
        with open(p, encoding="utf-8") as f:
            for k, v in json.load(f).get("mixinLedger", {}).items():
                led[k] = (v, os.path.basename(p))
    return led


def main():
    a = sys.argv[1:]
    opt = {}
    for flag in ("--sample", "--seed", "--out"):
        if flag in a:
            i = a.index(flag)
            opt[flag] = a[i + 1]
            del a[i:i + 2]
    cause_tsv, wiring = a
    led = ledger_of(wiring)
    rows = []
    total = 0
    with open(cause_tsv, encoding="utf-8") as f:
        head = next(f).rstrip("\n").split("\t")
        ix = {n: i for i, n in enumerate(head)}
        for line in f:
            r = line.rstrip("\n").split("\t")
            if len(r) < len(head):
                continue
            total += 1
            o, t, an = r[ix["owner"]], r[ix["target"]], r[ix["anchor"]]
            for k in (o, f"{o}.{t}", f"{o}.{t} @ {an}"):
                if k in led:
                    rows.append((r, k, led[k]))
                    break
    print(f"open rows {total}, ledgered {len(rows)}")
    cause = lambda r: r[ix["cause"]].split(":")[0]
    byc = collections.Counter(cause(r) for r, _, _ in rows)
    memc = collections.defaultdict(set)
    files = collections.Counter(src for _, _, (_, src) in rows)
    members = collections.defaultdict(list)
    for r, k, _ in rows:
        m = f"{r[ix['owner']]}.{r[ix['target']]}" if r[ix["target"]] else r[ix["owner"]]
        memc[cause(r)].add(m)
        members[(cause(r), m)].append((r, k))
    allm = {m for s in memc.values() for m in s}
    print(f"distinct target members {len(allm)}; ledger files: {dict(files)}")
    for c, n in byc.most_common():
        print(f"  {c:34s} rows {n:6d}  members {len(memc[c]):5d}")
    usage = collections.Counter(r[ix["usage"]] for r, _, _ in rows)
    print("  usage:", ", ".join(f"{u} {n}" for u, n in usage.most_common(12)))
    if "--out" in opt:
        p = opt["--out"]
        with open(p + "-rows.tsv", "w", encoding="utf-8", newline="\n") as o:
            o.write("\t".join(head + ["ledgerKey", "ledgerFile"]) + "\n")
            for r, k, (_, src) in rows:
                o.write("\t".join(r + [k, src]) + "\n")
        n = int(opt.get("--sample", "40"))
        rnd = random.Random(int(opt.get("--seed", "1")))
        # stratified: per cause, members in proportion to rows (at least 3 per cause), weighted by rows
        quota = {c: max(3, round(n * byc[c] / len(rows))) for c in byc}
        with open(p + "-sample.tsv", "w", encoding="utf-8", newline="\n") as o:
            o.write("cause\tmember\tmojang\trows\tusages\tanchors\tevidence\n")
            for c in byc:
                ms = sorted(memc[c])
                w = [len(members[(c, m)]) for m in ms]
                pick = set()
                while len(pick) < min(quota[c], len(ms)):
                    pick.add(rnd.choices(ms, weights=w)[0] if rnd.random() < 0.5 else rnd.choice(ms))
                for m in sorted(pick):
                    rs = members[(c, m)]
                    r0 = rs[0][0]
                    us = ",".join(sorted({r[ix["usage"]] for r, _ in rs}))
                    ans = " | ".join(sorted({r[ix["anchor"]] for r, _ in rs})[:4])
                    o.write(f"{c}\t{m}\t{r0[ix['mojang']]}\t{len(rs)}\t{us}\t{ans}\t{r0[ix['evidence']][:300]}\n")


if __name__ == "__main__":
    main()
