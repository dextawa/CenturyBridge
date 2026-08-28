"""Fix broken holder/getter field bridges: forge wired GETSTATIC field reads to
zero-arg getter METHODS on Statics via fieldRedirects, but the engine emits those
as GETSTATIC (field access) -> NoSuchFieldError at mod runtime. Move every such
entry to fieldGetters (engine emits GETSTATIC -> INVOKESTATIC getter). A
fieldRedirect whose target field-name has a real static field on Statics stays.
  python3 migrate_field_getters.py <mcver>   [--dry]
"""
import json, io, re, sys, glob

ver = sys.argv[1]
dry = "--dry" in sys.argv
pkg = "v" + ver.replace(".", "_")
statics = f"core/shims/{ver}/src/top/dext/centurybridge/rt/{pkg}/Statics.java"
shims_p = f"core/segments/shims-{ver}.json"
STAT = f"top/dext/centurybridge/rt/{pkg}/Statics"

t = io.open(statics, encoding='utf-8').read()
# zero-arg static methods on Statics (any name, any return type)
methods = set(re.findall(r'public static [^;{}=]+?\b(\w+)\(\)\s*\{', t))
# real static fields on Statics
fields = set(re.findall(r'public static (?:final )?[\w.$\[\]<>, ]+? (\w+)\s*=\s*', t))

w = json.load(io.open(shims_p, encoding='utf-8'))
fr = w.get('fieldRedirects') or {}
fg = w.setdefault('fieldGetters', {})
moved = 0; kept = 0; dangle = []
for k in list(fr):
    if fr[k] != STAT:
        continue
    fld = k.split('.')[-1].split(':')[0]
    if fld in methods:
        if not dry:
            fg[k] = fr.pop(k)
        moved += 1
    elif fld in fields:
        kept += 1
    else:
        dangle.append(fld)
w['fieldRedirects'] = fr
if not dry:
    io.open(shims_p, 'w', encoding='utf-8', newline='\n').write(json.dumps(w, indent=1, ensure_ascii=False))
print(f"{ver}: moved {moved} -> fieldGetters (now {len(fg)}); kept {kept} real fields; "
      f"fieldRedirects now {len(fr)}; dangling(no method/field): {dangle}")
