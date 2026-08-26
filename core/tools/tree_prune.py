"""树清理循环: javac 判, 两类坏桥都清 --
  (1) mixin target private 类 -> 整文件删 (assemble 误生成, 无法 mixin private)
  (2) 方法编不过 -> prune_failed 方法级剪
反复到 build 过. 标准化 assemble 后的树修补.
  python3 tree_prune.py <mcver>
"""
import subprocess, os, re, io, json, sys, glob

ver = sys.argv[1]
pkg = "v" + ver.replace(".", "_")
srcdir = f"core/shims/{ver}/src"
pkgdir = f"{srcdir}/top/dext/centurybridge/shims/{pkg}"
shims_p = f"core/segments/shims-{ver}.json"
cfg_p = glob.glob(f"core/shims/{ver}/resources/*.mixins.json")[0]
stub = f"data/jars/client-{ver}-intermediary.jar"

cp = [stub]
# some versions ship only a server runtime (no client-<ver>) -- include both,
# else a missing client dir yields an incomplete classpath and phantom errors
# that make tree_prune excise good bodies.
for base in (f"data/runtime/client-{ver}/libraries", f"data/runtime/server-{ver}/libraries",
             f"data/runtime/client-{ver}/.fabric/processedMods", f"data/runtime/server-{ver}/.fabric/processedMods"):
    for root,_,fs in os.walk(base):
        cp += [os.path.join(root,f) for f in fs if f.endswith('.jar')]
sm = glob.glob("data/runtime/**/sponge-mixin-*.jar", recursive=True)
if sm: cp.append(sm[0])
CP = ";".join(cp)

def excise_file(bf, w, cfg):
    fp = f"{pkgdir}/{bf}.java"
    if os.path.exists(fp):
        txt = io.open(fp, encoding='utf-8').read()
        for m in re.finditer(r"/\*\* was (\S+?) \*/", txt):
            sym = m.group(1)
            if sym in w.get('covers',[]): w['covers'].remove(sym)
            for mk in ('staticRedirects','instanceRedirects','fieldRedirects','ctorRedirects'):
                w.get(mk,{}).pop(sym, None)
        os.remove(fp)
    for side in ('mixins','client','server'):
        if bf in cfg.get(side,[]): cfg[side].remove(bf)

for rnd in range(1, 40):
    srcs = []
    for root,_,fs in os.walk(srcdir): srcs += [os.path.join(root,f) for f in fs if f.endswith('.java')]
    io.open("/tmp/sl.txt","w").write("\n".join(srcs))
    r = subprocess.run(["javac","-J-Duser.language=en","-J-Duser.country=US","-Xmaxerrs","5000",
        "-encoding","UTF-8","-proc:none","--release","17","-cp",CP,"-d","/tmp/tpcls","@/tmp/sl.txt"],
        capture_output=True, text=True, encoding='utf-8', errors='replace')
    errs = [l for l in r.stderr.splitlines() if ": error:" in l]
    if not errs:
        print(f"round {rnd}: CLEAN ({len(srcs)} files)"); break
    w = json.load(io.open(shims_p, encoding='utf-8'))
    cfg = json.load(io.open(cfg_p, encoding='utf-8'))
    # (1) invalid mixin target -> whole file. private/inaccessible inner class,
    #     or any error in the class-header region (<=16, the @Mixin/class decl):
    #     assemble mis-generated a bridge onto a class that cannot be mixed into.
    priv_files = set()
    for l in errs:
        m = re.match(r"(.+\.java):(\d+):", l)
        if not m: continue
        if ("private access" in l or "inaccessible" in l or int(m.group(2)) <= 16):
            priv_files.add(os.path.basename(m.group(1).replace("\\","/"))[:-5])
    for bf in priv_files: excise_file(bf, w, cfg)
    io.open(cfg_p,'w',encoding='utf-8',newline='\n').write(json.dumps(cfg,indent=1))
    io.open(shims_p,'w',encoding='utf-8',newline='\n').write(json.dumps(w,indent=1,ensure_ascii=False))
    # (2) method-level prune for the rest
    errfiles = set()
    for l in errs:
        if "private access" in l: continue
        m = re.match(r"(.+\.java):", l)
        if m:
            b = os.path.basename(m.group(1).replace("\\","/"))
            for root,_,fs in os.walk(srcdir):
                if b in fs: errfiles.add(os.path.join(root,b))
    io.open("/tmp/tperr.txt","w",encoding='utf-8').write(r.stderr)
    pruned = 0
    if errfiles:
        pr = subprocess.run(["python3","core/tools/prune_failed.py","/tmp/tperr.txt",shims_p,
            f"core/out-diff/retry-tp-{ver}.json"]+list(errfiles), capture_output=True, text=True, encoding='utf-8')
        m = re.search(r"total pruned (\d+)", pr.stdout)
        pruned = int(m.group(1)) if m else 0
    print(f"round {rnd}: {len(errs)} errors, {len(priv_files)} priv-files deleted, {pruned} methods pruned")
    if not priv_files and pruned == 0:
        print("STUCK:"); [print("  "+l) for l in errs[:4]]; break
