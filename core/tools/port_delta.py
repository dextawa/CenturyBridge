"""Additively port newly landed bridges from one version tree to another.

Copies whole Gen* classes that are missing, appends missing marker-methods to
classes present in both trees, and appends the missing generated Statics
methods inside the destination's generated block. All span extraction is
string- and comment-aware (see prune_failed.py for the incident that made
that non-negotiable). Package tokens (vX_Y_Z) are rewritten on the way over.

  python3 port_delta.py <src-tree-pkg-dir> <dst-tree-pkg-dir>

e.g. core/shims/1.21.1/src/.../shims/v1_21_1 core/shims/1.21.4/src/.../shims/v1_21_4
"""
import io
import os
import re
import sys

END_MARK = "    // ==== END GENERATED ===="


def markers(text):
    return set(re.findall(r"/\*\* was (\S+?) \*/", text))


def spans(text):
    out = []
    for m in re.finditer(r"[ \t]*/\*\* was (\S+?) \*/\n", text):
        start = m.start()
        i = text.index("{", m.end())
        depth, j, state = 0, i, None
        while j < len(text):
            ch = text[j]
            if state == '"':
                if ch == "\\":
                    j += 1
                elif ch == '"':
                    state = None
            elif state == "'":
                if ch == "\\":
                    j += 1
                elif ch == "'":
                    state = None
            elif state == "//":
                if ch == "\n":
                    state = None
            elif state == "/*":
                if ch == "*" and text[j + 1:j + 2] == "/":
                    j += 1
                    state = None
            else:
                if ch == '"':
                    state = '"'
                elif ch == "'":
                    state = "'"
                elif ch == "/" and text[j + 1:j + 2] == "/":
                    state = "//"
                elif ch == "/" and text[j + 1:j + 2] == "*":
                    state = "/*"
                elif ch == "{":
                    depth += 1
                elif ch == "}":
                    depth -= 1
                    if depth == 0:
                        break
            j += 1
        end = text.index("\n", j) + 1
        out.append((m.group(1), start, end))
    return out


def main():
    src_dir, dst_dir = sys.argv[1], sys.argv[2]
    sv = os.path.basename(os.path.normpath(src_dir))
    dv = os.path.basename(os.path.normpath(dst_dir))

    def port(text):
        return text.replace(sv, dv)

    copied = merged = methods_added = 0
    for f in sorted(os.listdir(src_dir)):
        if not f.endswith(".java"):
            continue
        sp, dp = os.path.join(src_dir, f), os.path.join(dst_dir, f)
        st = io.open(sp, encoding="utf-8").read()
        if not os.path.exists(dp):
            io.open(dp, "w", encoding="utf-8", newline="\n").write(port(st))
            copied += 1
            continue
        dt = io.open(dp, encoding="utf-8").read()
        missing = markers(st) - markers(dt)
        if not missing:
            continue
        adds = [st[a:b] for sym, a, b in spans(st) if sym in missing]
        if adds:
            dt = dt.rstrip()
            assert dt.endswith("}"), dp
            dt = dt[:-1].rstrip() + "\n\n" + port("\n".join(adds)) + "}\n"
            io.open(dp, "w", encoding="utf-8", newline="\n").write(dt)
            merged += 1
            methods_added += len(adds)
    print(f"gen classes: copied {copied}, merged {merged} (+{methods_added} methods)")

    def statics_path(pkg_dir, v):
        tree = os.path.dirname(os.path.dirname(os.path.normpath(pkg_dir)))
        return os.path.join(tree, "rt", v, "Statics.java")

    ssp, sdp = statics_path(src_dir, sv), statics_path(dst_dir, dv)
    st = io.open(ssp, encoding="utf-8").read()
    dt = io.open(sdp, encoding="utf-8").read()
    missing = markers(st) - markers(dt)
    adds = [st[a:b] for sym, a, b in spans(st) if sym in missing]
    if adds:
        assert END_MARK in dt, sdp
        dt = dt.replace(END_MARK, port("\n".join(adds)) + "\n" + END_MARK, 1)
        io.open(sdp, "w", encoding="utf-8", newline="\n").write(dt)
    print(f"statics: +{len(adds)} methods")


if __name__ == "__main__":
    main()
