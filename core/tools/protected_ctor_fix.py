"""protected 构造器不能从 Statics 工厂 new (访问权限). 用 sponge-mixin @Invoker("<init>")
从 @Mixin 内部暴露一个 public 静态工厂. 处理 forged 里的构造器 body:
  new class_XXXX(args) -> ClassXXXXCtorInvoker.cbNew(args)  + 生成 Invoker interface.
  python3 protected_ctor_fix.py <mcver> <forged.json> <stub>
"""
import subprocess, json, io, os, re, sys, glob
ver, forged_p, stub = sys.argv[1], sys.argv[2], sys.argv[3]
pkg = "v"+ver.replace(".","_")
shimdir = f"core/shims/{ver}/src/top/dext/centurybridge/shims/{pkg}"
cfg_p = glob.glob(f"core/shims/{ver}/resources/*.mixins.json")[0]

def ctor_access(owner, desc):
    dotted = owner.replace("/",".")
    r = subprocess.run(["javap","-p","-cp",stub,dotted],capture_output=True,text=True,encoding='utf-8',errors='replace')
    simple = owner.split("/")[-1]
    for l in r.stdout.splitlines():
        if simple.split("$")[-1]+"(" in l and ("protected" in l or "private" in l):
            return "restricted", l.strip()
    return "public", None

d = json.load(io.open(forged_p, encoding='utf-8'))
cfg = json.load(io.open(cfg_p, encoding='utf-8'))
fixed = 0
invokers = {}  # owner -> list of (method_name, new_sig_java)
for o in d:
    if o.get("name") != "<init>" or not o.get("body"): continue
    owner = o["owner"]; simple = owner.split("/")[-1]
    body = o["body"]
    # only bodies that new the target directly
    m = re.search(r"new\s+net\.minecraft\."+re.escape(simple.replace("$","."))+r"\s*\(", body)
    if not m: continue
    acc, sigline = ctor_access(owner, o["oldDesc"])
    if acc != "restricted": continue
    # invoker method name unique per body
    inv_name = f"cbNew{o['javaSig'].__hash__() & 0xffffff:06x}"
    invcls = f"CtorInvoker_{simple.replace('$','_')}"
    # the new-call args + types: parse from body 'new X(a0, a1, ..., extra)' and the real ctor sig
    call = body[m.end():]
    depth=1; i=0
    while i < len(call) and depth>0:
        if call[i]=='(': depth+=1
        elif call[i]==')': depth-=1
        i+=1
    args = call[:i-1]
    # invoker declares the NEW ctor signature (from javap sigline)
    newsig = re.search(r"\((.*)\)", sigline).group(1) if sigline else ""
    inv_decl = f"    @org.spongepowered.asm.mixin.gen.Invoker(\"<init>\")\n    static net.minecraft.{simple.replace('$','.')} {inv_name}({newsig});"
    invokers.setdefault((owner,invcls),[]).append(inv_decl)
    # rewrite body: new X(...) -> Invoker.inv_name(...)
    o["body"] = body[:m.start()] + f"top.dext.centurybridge.shims.{pkg}.{invcls}.{inv_name}(" + call
    fixed += 1

# write invoker interface files + config
for (owner,invcls), decls in invokers.items():
    simple = owner.split("/")[-1]
    src = (f"package top.dext.centurybridge.shims.{pkg};\n\n"
           f"import org.spongepowered.asm.mixin.Mixin;\n\n"
           f"/** @Invoker factories for protected constructors of {owner}. */\n"
           f"@Mixin(net.minecraft.{simple.replace('$','.')}.class)\n"
           f"public interface {invcls} {{\n" + "\n\n".join(decls) + "\n}\n")
    io.open(f"{shimdir}/{invcls}.java","w",encoding='utf-8',newline='\n').write(src)
    side = "mixins"
    if invcls not in cfg.get(side,[]): cfg.setdefault(side,[]).append(invcls)
io.open(cfg_p,'w',encoding='utf-8',newline='\n').write(json.dumps(cfg,indent=1))
json.dump(d, io.open(forged_p,'w',encoding='utf-8',newline='\n'), indent=1, ensure_ascii=False)
print(f"{fixed} protected ctor bodies rerouted via @Invoker, {len(invokers)} invoker interfaces")
