"""Port LWJGL 3.3.2's IOUringNotificationRegister / IOUringNotificationSlot (dropped by 3.3.3) into the 1.20.5 step layer.

  python3 port_uring.py <3.3.2 source dir> <out dir>

Mechanical: the class renamed CbLibUring<Name>, Struct's 3.3.3 type parameter and create(long, ByteBuffer) added, the
3.3.2 Struct.wrap / Pointer.Default.wrap factories (gone in 3.3.3) replaced by the constructors 3.3.3's generator
uses for the same thing. Members, layout and bodies otherwise as 3.3.2's generated source.
"""
import re
import sys

src, out = sys.argv[1:3]
for name in ("IOUringNotificationRegister", "IOUringNotificationSlot"):
    cb = "CbLibUring" + name[len("IOUring"):]
    s = open(f"{src}/{name}.java", encoding="utf-8").read()
    s = s.replace("package org.lwjgl.system.linux.liburing;",
                  "package top.dext.centurybridge.step.v1_20_5.rt;\n\nimport net.fabricmc.api.EnvType;\nimport net.fabricmc.api.Environment;")
    s = s.replace(" * MACHINE GENERATED FILE, DO NOT EDIT\n",
                  f" * LWJGL 3.3.2's org.lwjgl.system.linux.liburing.{name} (3.3.3 dropped it), ported to 3.3.3's Struct by\n"
                  " * core/tools/port_uring.py: same layout and members; 3.3.2's wrap factories are 3.3.3's constructors.\n")
    s = s.replace(name, cb)
    # javax.annotation (jsr305) is not on the 1.20.5 runtime; @Nullable is documentation only
    s = s.replace("import javax.annotation.*;\n\n", "").replace("@Nullable\n    ", "").replace("@Nullable ", "")
    s = s.replace(f"public class {cb} extends Struct implements NativeResource {{",
                  f"@Environment(EnvType.CLIENT)\npublic class {cb} extends Struct<{cb}> implements NativeResource {{")
    s = s.replace("    public static class Buffer extends StructBuffer",
                  "    @Environment(EnvType.CLIENT)\n    public static class Buffer extends StructBuffer")
    anchor = "    @Override\n    public int sizeof() { return SIZEOF; }"
    assert anchor in s
    s = s.replace(anchor,
                  f"    protected {cb}(long address, ByteBuffer container) {{\n"
                  "        super(address, container);\n    }\n\n"
                  "    /** 3.3.2's StructBuffer made its elements as Struct.wrap did: instances of the factory's own class */\n"
                  "    @Override\n"
                  f"    protected {cb} create(long address, ByteBuffer container) {{\n"
                  f"        return ({cb}) CbLibLwjgl.create(this, address, container);\n    }}\n\n" + anchor, 1)
    n = 0
    for pat, rep in ((rf"wrap\({cb}\.class, ([^;]+?), container\)", rf"new {cb}(\1, container)"),
                     (rf"wrap\({cb}\.class, ([^;]+?)\)(;)", rf"new {cb}(\1, null)\2"),
                     (r"wrap\(Buffer\.class, ([^;]+?), capacity, container\)", r"new Buffer(\1, container, -1, 0, capacity, capacity)"),
                     (r"wrap\(Buffer\.class, ([^;]+?), capacity\)", r"new Buffer(\1, capacity)")):
        s, k = re.subn(pat, rep, s)
        n += k
    assert "wrap(" not in s, name
    open(f"{out}/{cb}.java", "w", encoding="utf-8", newline="\n").write(s)
    print(cb, n, "factories")
