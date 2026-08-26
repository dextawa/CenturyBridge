"""facade verify-retry: 编不过的 facade 带编译错误 retry gpt, 循环到编过.
仍编不过的删除 + classRename 移除 (回退墓碑). 全 facade 一起编 (相互依赖).
  python3 facade_prune.py <mcver> <stub>
"""
import subprocess, json, io, os, re, sys, glob
sys.path.insert(0,'core/tools')
import forge
import urllib.request

ver, stub = sys.argv[1], sys.argv[2]
pkg = "v"+ver.replace(".","_")
facdir = f"core/shims/{ver}/src/top/dext/centurybridge/facade/{pkg}"
shims_p = f"core/segments/shims-{ver}.json"
srcstub = "data/jars/client-1.20.1-intermediary.jar"

cp=[stub]
for base in (f"data/runtime/client-{ver}/libraries", f"data/runtime/client-{ver}/.fabric/processedMods"):
    for root,_,fs in os.walk(base): cp+=[os.path.join(root,f) for f in fs if f.endswith('.jar')]
sm=glob.glob("data/runtime/**/sponge-mixin-*.jar",recursive=True)
if sm: cp.append(sm[0])
CP=";".join(cp)
SRCDIR=f"core/shims/{ver}/src"

SYS=(f"You fix a facade class that failed to compile. Output ONLY the corrected complete Java type "
     f"(no fences/prose). Package top.dext.centurybridge.facade.{pkg}. Keep the class name and ALL public "
     f"signatures. Use fully-qualified net.minecraft.class_XXXX names. Unresolvable members -> throw "
     f"new UnsupportedOperationException. Fix exactly the compiler errors given.")

def sig_of(cls):
    r=subprocess.run(["javap","-p","-cp",srcstub,cls.replace("/",".")],capture_output=True,text=True,encoding='utf-8',errors='replace')
    return r.stdout

def gpt(sysp,usr):
    payload={"model":"gpt-5.6-sol","messages":[{"role":"system","content":sysp},{"role":"user","content":usr}],"temperature":0}
    req=urllib.request.Request(forge.API_BASE+"/chat/completions",data=json.dumps(payload).encode(),
        headers={"Content-Type":"application/json","Authorization":f"Bearer {forge.API_KEY}"})
    forge.LIMITER.acquire()
    with urllib.request.urlopen(req,timeout=90) as rp: d=json.load(rp)
    b=d["choices"][0]["message"].get("content") or ""
    return re.sub(r"^```\w*\n|```$","",b.strip(),flags=re.M).strip()

for rnd in range(1,5):
    srcs=[]
    for root,_,fs in os.walk(SRCDIR): srcs+=[os.path.join(root,f) for f in fs if f.endswith('.java')]
    io.open("/tmp/fsl.txt","w").write("\n".join(srcs))
    r=subprocess.run(["javac","-J-Duser.language=en","-Xmaxerrs","5000","-encoding","UTF-8","-proc:none",
        "--release","17","-cp",CP,"-d","/tmp/fc","@/tmp/fsl.txt"],capture_output=True,text=True,encoding='utf-8',errors='replace')
    errs=[l for l in r.stderr.splitlines() if ": error:" in l]
    # facade + mixin both live under core/shims/; tell apart by 'facade' segment
    facerr={}
    for l in errs:
        if 'facade' in l:
            m=re.search(r"(\w+)\.java:(\d+): error: (.*)",l)
            if m: facerr.setdefault(m.group(1),[]).append(f"line {m.group(2)}: {m.group(3)}")
    shim_errs=sum(1 for l in errs if 'facade' not in l)
    print(f"round {rnd}: {len(errs)} errors, {len(facerr)} facade classes bad, {shim_errs} shim errors")
    if not facerr: break
    from concurrent.futures import ThreadPoolExecutor
    def fix(item):
        cls,msgs=item
        # find original net/minecraft class from name
        nm = "net/minecraft/"+cls
        sig=sig_of(nm)
        cur=io.open(f"{facdir}/{cls}.java",encoding='utf-8').read()
        usr=f"Facade {cls} failed to compile:\n{chr(10).join(msgs[:8])}\n\nOriginal 1.20.1 signature:\n{sig}\nCurrent code:\n{cur[:1500]}\n\nEmit corrected full type."
        try:
            b=gpt(SYS,usr)
            if not b.startswith("package"): b=f"package top.dext.centurybridge.facade.{pkg};\n\n"+b
            io.open(f"{facdir}/{cls}.java","w",encoding='utf-8',newline='\n').write(b)
        except Exception as e: print(f"    {cls} retry failed: {str(e)[:40]}")
    with ThreadPoolExecutor(max_workers=8) as ex: list(ex.map(fix, facerr.items()))

# final: delete still-broken facades + remove classRename
srcs=[]
for root,_,fs in os.walk(SRCDIR): srcs+=[os.path.join(root,f) for f in fs if f.endswith('.java')]
io.open("/tmp/fsl.txt","w").write("\n".join(srcs))
r=subprocess.run(["javac","-J-Duser.language=en","-Xmaxerrs","5000","-encoding","UTF-8","-proc:none","--release","17","-cp",CP,"-d","/tmp/fc","@/tmp/fsl.txt"],capture_output=True,text=True,encoding='utf-8',errors='replace')
bad=set()
for l in r.stderr.splitlines():
    if 'facade' in l and ": error:" in l:
        m=re.search(r"(\w+)\.java:",l)
        if m: bad.add(m.group(1))
w=json.load(io.open(shims_p,encoding='utf-8'))
for b in bad:
    fp=f"{facdir}/{b}.java"
    if os.path.exists(fp): os.remove(fp)
    w.get("classRenames",{}).pop(f"net/minecraft/{b}",None)
io.open(shims_p,'w',encoding='utf-8',newline='\n').write(json.dumps(w,indent=1,ensure_ascii=False))
print(f"deleted {len(bad)} unfixable facades, kept {40-len(bad)}")
