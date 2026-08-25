"""After assemble.py lands forged bridges into the 26.2 tree, bring the wiring
it wrote back into the analysis namespace.

Orders for the year-era walls carry mojang names (their damage exists only
after the rename stage), so assemble writes mojang-named keys. The converter
matches wiring against a mod in intermediary names and renames afterwards, so
every key that has an owner-qualified reverse mapping is translated back;
keys for members born after 1.21.11 stay as they are (no old mod names them).

  python3 land_26x.py <inter-to-mojang.tsv> <shims-26.2.json>
"""
import io, json, re, sys

table, wp = sys.argv[1], sys.argv[2]
rclass, rmember = {}, {}
for line in io.open(table, encoding="utf-8"):
    k, i, m = line.rstrip("\n").split("\t")
    if k == "C":
        rclass[m] = i
    else:
        rmember[m] = i.rsplit(".", 1)[1]

def back(sym):
    """owner.member(desc) in mojang -> intermediary, descriptor classes included"""
    m = re.match(r"([\w/$]+)\.([^(:]+)(.*)", sym)
    if not m:
        return sym
    owner, member, rest = m.groups()
    tok = rmember.get(owner + "." + member, member)
    owner2 = rclass.get(owner, owner)
    rest2 = re.sub(r"L([\w/$]+);", lambda mo: "L" + rclass.get(mo.group(1), mo.group(1)) + ";", rest)
    return owner2 + "." + tok + rest2

d = json.load(io.open(wp, encoding="utf-8"))
changed = 0
for sec in ("covers",):
    new = []
    for k in d.get(sec, []):
        k2 = back(k); changed += k2 != k; new.append(k2)
    d[sec] = new
for sec in ("staticRedirects", "instanceRedirects", "fieldRedirects", "ledger", "ctorRedirects"):
    new = {}
    for k, v in d.get(sec, {}).items():
        k2 = back(k); changed += k2 != k; new[k2] = v
    d[sec] = new
io.open(wp, "w", encoding="utf-8", newline="\n").write(json.dumps(d, indent=1, ensure_ascii=False))
print(f"{wp}: {changed} keys back-translated to the analysis namespace")
