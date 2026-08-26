"""gpt 批量重建死类为 facade. 走廊链上死类(整类删除/重构)不能桥, 只能 facade
重建旧 API. 提取 1.20.1 signature -> gpt 写完整 facade 类 -> 写 facade/ + classRename.
  python3 facade_forge.py <mcver> <audit.tsv> <srcStub>
"""
import subprocess, json, io, os, re, sys, glob, time
from concurrent.futures import ThreadPoolExecutor
sys.path.insert(0,'core/tools')
import forge

ver, audit, srcstub = sys.argv[1], sys.argv[2], sys.argv[3]
pkg = "v"+ver.replace(".","_")
facdir = f"core/shims/{ver}/src/top/dext/centurybridge/facade/{pkg}"
os.makedirs(facdir, exist_ok=True)
shims_p = f"core/segments/shims-{ver}.json"

# dead classes from CLASS-ISSUE, by ref desc
dead = []
for l in io.open(audit, encoding='utf-8'):
    c = l.split('\t')
    if len(c) >= 3 and 'CLASS-ISSUE' in c[1]:
        cnt = int(c[0]) if c[0].isdigit() else 1
        dead.append((cnt, c[2].strip()))
dead.sort(reverse=True)
print(f"{len(dead)} dead classes to facade")

SYS = ("You rebuild deleted Minecraft classes as facade classes so old mods still compile and run. "
       "Output ONLY a complete Java class/interface/enum body -- no markdown fences, no prose, no explanation. "
       f"Package: top.dext.centurybridge.facade.{pkg}. Keep the intermediary class name (class_XXXX). "
       "Rebuild EVERY public/protected member with the SAME signature (ctor/methods/fields/static factories/nested types). "
       "Self-contained logic (data wrappers, enums, simple holders) -- implement directly with intermediary names. "
       "Members needing a deleted/incompatible API -- throw new UnsupportedOperationException(\"CenturyBridge: ...\"). "
       "Match the exact kind (class/interface/enum/abstract) and generics/extends/implements.")

def sig_of(cls):
    dotted = cls.replace("/",".")
    r = subprocess.run(["javap","-p","-cp",srcstub,dotted], capture_output=True, text=True, encoding='utf-8', errors='replace')
    return r.stdout

def facade_one(item):
    cnt, cls = item
    simple = cls.split("/")[-1]
    if "$" in simple: return None  # nested -- handled with parent
    sig = sig_of(cls)
    if not sig.strip() or "Error" in sig[:40]: return None
    usr = f"This 1.20.1 class was deleted/refactored away in {ver}. Rebuild it as a facade (name: {simple}):\n\n{sig}\nEmit the full type."
    try:
        forge.MODEL = "gpt-5.6-sol"
        body = forge.call_model_raw(SYS, usr) if hasattr(forge,'call_model_raw') else None
    except Exception:
        body = None
    if body is None:
        # inline call
        import urllib.request
        payload={"model":"gpt-5.6-sol","messages":[{"role":"system","content":SYS},{"role":"user","content":usr}],"temperature":0}
        req=urllib.request.Request(forge.API_BASE+"/chat/completions",data=json.dumps(payload).encode(),
            headers={"Content-Type":"application/json","Authorization":f"Bearer {forge.API_KEY}"})
        forge.LIMITER.acquire()
        try:
            with urllib.request.urlopen(req,timeout=90) as rp: d=json.load(rp)
            body=d["choices"][0]["message"].get("content") or ""
        except Exception as e:
            return (cls, None, str(e)[:60])
    body = re.sub(r"^```\w*\n|```$", "", body.strip(), flags=re.M).strip()
    if not body.startswith("package"):
        body = f"package top.dext.centurybridge.facade.{pkg};\n\n" + body
    io.open(f"{facdir}/{simple}.java","w",encoding='utf-8',newline='\n').write(body)
    return (cls, simple, "ok")

results = []
with ThreadPoolExecutor(max_workers=8) as ex:
    for r in ex.map(facade_one, dead):
        if r: results.append(r); print(f"  {r[0]}: {r[2]}")
ok = [r for r in results if r[2]=="ok"]
# classRename wiring
w = json.load(io.open(shims_p, encoding='utf-8'))
cr = w.setdefault("classRenames", {})
for cls, simple, st in ok:
    cr[cls] = f"top/dext/centurybridge/facade/{pkg}/{simple}"
io.open(shims_p,'w',encoding='utf-8',newline='\n').write(json.dumps(w,indent=1,ensure_ascii=False))
print(f"\nfacade written: {len(ok)}, classRename wired")
