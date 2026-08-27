"""build 前清干净 assemble 的坏产出: 编不过的 Gen/facade 整文件删, Statics 坏方法
brace-aware 删, 循环到 javac clean. gpt body 各种语法错(多余}/匿名类/unclosed)通吃.
  python3 build_clean.py <mcver> <stub>
"""
import subprocess, json, io, os, re, sys, glob
ver, stub = sys.argv[1], sys.argv[2]
pkg="v"+ver.replace(".","_")
shimdir=f"core/shims/{ver}/src/top/dext/centurybridge/shims/{pkg}"
facdir=f"core/shims/{ver}/src/top/dext/centurybridge/facade/{pkg}"
statics=f"core/shims/{ver}/src/top/dext/centurybridge/rt/{pkg}/Statics.java"
cfg_p=glob.glob(f"core/shims/{ver}/resources/*.mixins.json")[0]
shims_p=f"core/segments/shims-{ver}.json"
SRCDIR=f"core/shims/{ver}/src"
cp=[stub]
for base in (f"data/runtime/client-{ver}/libraries",f"data/runtime/server-{ver}/libraries",
             f"data/runtime/client-{ver}/.fabric/processedMods",f"data/runtime/server-{ver}/.fabric/processedMods"):
    for root,_,fs in os.walk(base): cp+=[os.path.join(root,f) for f in fs if f.endswith('.jar')]
sm=glob.glob("data/runtime/**/sponge-mixin-*.jar",recursive=True)
if sm: cp.append(sm[0])
CP=";".join(cp)

def method_spans(text):
    spans=[]
    for m in re.finditer(r'/\*\* was (\S+?) \*/\n', text):
        a=m.start(); i=text.find('{',m.end())
        if i<0: continue
        depth=1; j=i+1; instr=None
        while j<len(text) and depth>0:
            ch=text[j]
            if instr:
                if ch=='\\': j+=1
                elif ch==instr: instr=None
            elif ch in '"' + "'": instr=ch
            elif ch=='{': depth+=1
            elif ch=='}': depth-=1
            j+=1
        spans.append((m.group(1),a,j))
    return spans

for rnd in range(1,30):
    srcs=[]
    for root,_,fs in os.walk(SRCDIR): srcs+=[os.path.join(root,f) for f in fs if f.endswith('.java')]
    io.open("/tmp/bcsl.txt","w").write("\n".join(srcs))
    r=subprocess.run(["javac","-J-Duser.language=en","-Xmaxerrs","9000","-encoding","UTF-8","-proc:none",
        "--release","17","-cp",CP,"-d","/tmp/bccls","@/tmp/bcsl.txt"],capture_output=True,text=True,encoding='utf-8',errors='replace')
    errs=[l for l in r.stderr.splitlines() if ": error:" in l]
    if not errs: print(f"round {rnd}: CLEAN"); break
    w=json.load(io.open(shims_p,encoding='utf-8'))
    cfg=json.load(io.open(cfg_p,encoding='utf-8'))
    errfiles=set()
    for l in errs:
        m=re.match(r"(.+\.java):",l)
        if m: errfiles.add(os.path.basename(m.group(1).replace("\\","/")))
    del_files=0; pruned=0
    for bn in errfiles:
        cls=bn[:-5]
        # Statics: excise erroring methods brace-aware
        if bn=="Statics.java":
            s=io.open(statics,encoding='utf-8').read()
            errlines=sorted({int(x) for l in errs if bn in l for x in re.findall(bn+r":(\d+):",l)})
            offs=[0]
            for i,ch in enumerate(s):
                if ch=='\n': offs.append(i+1)
            spans=method_spans(s)
            doomed=set()
            for ln in errlines:
                off=offs[min(ln-1,len(offs)-1)]
                for sym,a,b in spans:
                    if a<=off<b: doomed.add((sym,a,b)); break
            for sym,a,b in sorted(doomed,key=lambda x:-x[1]):
                s=s[:a]+s[b:]
                if sym in w.get('covers',[]): w['covers'].remove(sym)
                for mk in ('staticRedirects','instanceRedirects','fieldRedirects','ctorRedirects'): w.get(mk,{}).pop(sym,None)
                pruned+=1
            io.open(statics,'w',encoding='utf-8',newline='\n').write(s)
        else:
            # Gen/facade/Invoker file: delete whole file + config + wiring
            for d in (shimdir,facdir):
                fp=f"{d}/{bn}"
                if os.path.exists(fp):
                    txt=io.open(fp,encoding='utf-8').read()
                    for mm in re.finditer(r"/\*\* was (\S+?) \*/",txt):
                        sym=mm.group(1)
                        if sym in w.get('covers',[]): w['covers'].remove(sym)
                        for mk in ('staticRedirects','instanceRedirects','fieldRedirects','ctorRedirects'): w.get(mk,{}).pop(sym,None)
                    os.remove(fp); del_files+=1
            for side in ('mixins','client','server'):
                if cls in cfg.get(side,[]): cfg[side].remove(cls)
    io.open(cfg_p,'w',encoding='utf-8',newline='\n').write(json.dumps(cfg,indent=1))
    io.open(shims_p,'w',encoding='utf-8',newline='\n').write(json.dumps(w,indent=1,ensure_ascii=False))
    print(f"round {rnd}: {len(errs)} errors, {del_files} files deleted, {pruned} Statics methods excised")
