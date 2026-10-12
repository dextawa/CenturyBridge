"""Stand up the bridge layer of a climb step: core/shims/steps/<W> and core/segments/steps/<V>__<W>.json.

A layer is a mod of its own (id centurybridge_step_<W>), written against W, giving back V's API:
  resources/fabric.mod.json, cb-step-<W>.mixins.json (package top.dext.centurybridge.step.v<W>.mixin),
  cb-step-<W>.accesswidener, src/top/dext/centurybridge/step/v<W>/rt/Guard.java (the re-entry guard
  override hooks use), and the step's base wiring {"step": "<V>__<W>"}. Existing files are left alone.

  python new_step_layer.py <V> <W> [<V> <W> ...]
"""
import json
import os
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

GUARD = """package {package};

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Runtime support for this layer's override hooks.
 *
 * Guard (cbEnter / cbExit): the new shape synthesized in a mod class forwards to the mod's old
 * override once per object and call; if that override's own super call comes back into the
 * synthesized method, it falls through to super instead of forwarding again.
 *
 * Frames (cbPush / cbPop / cbArg*): a hook that carries its arguments records what vanilla passed
 * to the new shape before forwarding to the old method; the old method's super call, rewritten to
 * the new shape, reads back the arguments the old shape has no place for. The innermost frame for
 * the object answers, so a nested call on the same instance (a block is a singleton) reads its own
 * arguments; with no frame for the object (a super call from outside a hooked call) the argument is
 * its type's zero value -- or, through cbArgOr*, the default the wiring names for it (frameDefaults:
 * what V used when there was no such argument).
 */
public final class Guard {{

    private static final ThreadLocal<Set<Object>> ACTIVE =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    private static final ThreadLocal<ArrayDeque<Object[]>> FRAMES = ThreadLocal.withInitial(ArrayDeque::new);

    public static boolean cbEnter(Object self) {{
        return ACTIVE.get().add(self);
    }}

    public static void cbExit(Object self) {{
        ACTIVE.get().remove(self);
    }}

    public static void cbPush(Object self, Object[] args) {{
        FRAMES.get().push(new Object[] {{self, args}});
    }}

    public static void cbPop() {{
        FRAMES.get().pop();
    }}

    public static Object cbArgL(Object self, int i) {{
        for (Object[] f : FRAMES.get()) {{ // innermost first
            if (f[0] == self) {{
                return ((Object[]) f[1])[i];
            }}
        }}
        return null;
    }}

    /** the frame's argument i when a frame holds the object (whatever vanilla passed, null too), else dflt */
    public static Object cbArgOrL(Object self, int i, Object dflt) {{
        for (Object[] f : FRAMES.get()) {{
            if (f[0] == self) {{
                return ((Object[]) f[1])[i];
            }}
        }}
        return dflt;
    }}

    private static boolean framed(Object self) {{
        for (Object[] f : FRAMES.get()) {{
            if (f[0] == self) {{
                return true;
            }}
        }}
        return false;
    }}

    public static boolean cbArgOrZ(Object self, int i, boolean dflt) {{
        return framed(self) ? cbArgZ(self, i) : dflt;
    }}

    public static byte cbArgOrB(Object self, int i, byte dflt) {{
        return framed(self) ? cbArgB(self, i) : dflt;
    }}

    public static char cbArgOrC(Object self, int i, char dflt) {{
        return framed(self) ? cbArgC(self, i) : dflt;
    }}

    public static short cbArgOrS(Object self, int i, short dflt) {{
        return framed(self) ? cbArgS(self, i) : dflt;
    }}

    public static int cbArgOrI(Object self, int i, int dflt) {{
        return framed(self) ? cbArgI(self, i) : dflt;
    }}

    public static long cbArgOrJ(Object self, int i, long dflt) {{
        return framed(self) ? cbArgJ(self, i) : dflt;
    }}

    public static float cbArgOrF(Object self, int i, float dflt) {{
        return framed(self) ? cbArgF(self, i) : dflt;
    }}

    public static double cbArgOrD(Object self, int i, double dflt) {{
        return framed(self) ? cbArgD(self, i) : dflt;
    }}

    public static boolean cbArgZ(Object self, int i) {{
        Object v = cbArgL(self, i);
        return v != null && (Boolean) v;
    }}

    public static byte cbArgB(Object self, int i) {{
        Object v = cbArgL(self, i);
        return v == null ? 0 : (Byte) v;
    }}

    public static char cbArgC(Object self, int i) {{
        Object v = cbArgL(self, i);
        return v == null ? 0 : (Character) v;
    }}

    public static short cbArgS(Object self, int i) {{
        Object v = cbArgL(self, i);
        return v == null ? 0 : (Short) v;
    }}

    public static int cbArgI(Object self, int i) {{
        Object v = cbArgL(self, i);
        return v == null ? 0 : (Integer) v;
    }}

    public static long cbArgJ(Object self, int i) {{
        Object v = cbArgL(self, i);
        return v == null ? 0L : (Long) v;
    }}

    public static float cbArgF(Object self, int i) {{
        Object v = cbArgL(self, i);
        return v == null ? 0f : (Float) v;
    }}

    public static double cbArgD(Object self, int i) {{
        Object v = cbArgL(self, i);
        return v == null ? 0d : (Double) v;
    }}

    private Guard() {{
    }}
}}
"""


def write(path, text):
    if os.path.exists(path):
        return False
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(text)
    return True


def write_guard(path, package):
    """the Guard is generated, always current: (re)written whenever it differs from the template"""
    text = GUARD.format(package=package)
    if os.path.exists(path) and open(path, encoding="utf-8").read() == text:
        return False
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(text)
    return True


def layer(v, w):
    pkg = "v" + w.replace(".", "_")
    base = os.path.join(ROOT, "core", "shims", "steps", w)
    res = os.path.join(base, "resources")
    made = []
    fmj = {
        "schemaVersion": 1,
        "id": "centurybridge_step_" + w.replace(".", "_"),
        "version": "0.3.0+" + w,
        "name": f"CenturyBridge: {v} API on {w}",
        "environment": "*",
        "mixins": [f"cb-step-{w}.mixins.json"],
        "accessWidener": f"cb-step-{w}.accesswidener",
        "depends": {"fabricloader": ">=0.15.0"},
    }
    if write(os.path.join(res, "fabric.mod.json"), json.dumps(fmj, indent=1) + "\n"):
        made.append("fabric.mod.json")
    cfg = {
        "required": True,
        "minVersion": "0.8",
        "package": f"top.dext.centurybridge.step.{pkg}.mixin",
        "compatibilityLevel": "JAVA_17",
        "mixins": [],
        "client": [],
        "server": [],
        "injectors": {"defaultRequire": 1},
    }
    if write(os.path.join(res, f"cb-step-{w}.mixins.json"), json.dumps(cfg, indent=2) + "\n"):
        made.append("mixins.json")
    namespace = "official" if w.startswith("26.") else "intermediary"
    if write(os.path.join(res, f"cb-step-{w}.accesswidener"), f"accessWidener\tv2\t{namespace}\n"):
        made.append("accesswidener")
    if write_guard(os.path.join(base, "src", "top", "dext", "centurybridge", "step", pkg, "rt", "Guard.java"),
                   f"top.dext.centurybridge.step.{pkg}.rt"):
        made.append("Guard.java")
    wiring = os.path.join(ROOT, "core", "segments", "steps", f"{v}__{w}.json")
    if write(wiring, json.dumps({"step": f"{v}__{w}"}, indent=1) + "\n"):
        made.append("wiring")
    print(f"{v} -> {w}: {', '.join(made) if made else 'already there'}")


def main():
    # step 1's layer is the 1.20.2 tree: its Guard lives with that tree's runtime helpers
    if write_guard(os.path.join(ROOT, "core", "shims", "1.20.2", "src", "top", "dext", "centurybridge", "rt", "v1_20_2", "Guard.java"),
                   "top.dext.centurybridge.rt.v1_20_2"):
        print("1.20.1 -> 1.20.2: Guard.java")
    a = sys.argv[1:]
    for i in range(0, len(a), 2):
        layer(a[i], a[i + 1])


if __name__ == "__main__":
    main()
