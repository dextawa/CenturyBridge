"""Remove landed bridge methods that fail to compile in tree context.

verify.py probes each forged body in isolation; a body can pass there and
still fail once spliced (mojmap identifiers, instance-style code in Statics,
private inner types...). This tool closes the loop: feed it the javac error
output, and every failing method is cut back out -- body, its `/** was ... */`
marker, and its wiring entry -- and recorded for re-forge with the compiler
message attached, so the next attempt sees what went wrong.

  python3 prune_failed.py <javac-errors.txt> <shims.json> <retry-out.json> <src-file...>
"""
import io
import json
import os
import re
import sys


def method_spans(text):
    """[(marker_symbol, start_offset, end_offset)] for every generated method.

    Brace counting is string- and comment-aware: tombstone bodies carry braces
    inside their message strings, and a naive counter runs past the real close
    and swallows the neighboring methods (this silently amputated hundreds of
    valid bridges before it was caught)."""
    spans = []
    for m in re.finditer(r"[ \t]*/\*\* was (\S+?) \*/\n", text):
        start = m.start()
        i = text.index("{", m.end())
        depth, j = 0, i
        state = None  # None | '"' | "'" | '//' | '/*'
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
        spans.append((m.group(1), start, end))
    return spans


def main():
    err_path, shims_path, retry_path = sys.argv[1], sys.argv[2], sys.argv[3]
    files = sys.argv[4:]

    errors = {}  # (path-suffix, line) -> message
    for ln in io.open(err_path, encoding="utf-8", errors="replace"):
        m = re.match(r"(.+\.java):(\d+): error: (.*)", ln.strip())
        if m:
            errors.setdefault(m.group(1).replace("\\", "/"), []).append(
                (int(m.group(2)), m.group(3)))

    shims = json.load(io.open(shims_path, encoding="utf-8"))
    removed, retry = [], []
    for path in files:
        norm = path.replace("\\", "/")
        errs = None
        for k, v in errors.items():
            if norm.endswith(k.split("/")[-1]) and k.split("/")[-1] == norm.split("/")[-1]:
                errs = v
                break
        if not errs or not os.path.exists(path):
            continue  # may have been removed by prune_dead_targets this round
        text = io.open(path, encoding="utf-8").read()
        spans = method_spans(text)
        # map error lines to spans
        offsets = [0]
        for i, ch in enumerate(text):
            if ch == "\n":
                offsets.append(i + 1)
        doomed = {}
        for line, msg in errs:
            off = offsets[min(line - 1, len(offsets) - 1)]
            for sym, a, b in spans:
                if a <= off < b:
                    doomed.setdefault((sym, a, b), []).append(msg)
                    break
        if not doomed:
            continue
        for (sym, a, b), msgs in sorted(doomed.items(), key=lambda x: -x[0][1]):
            text = text[:a] + text[b:]
            removed.append(sym)
            retry.append({"symbol": sym, "compileErrors": msgs})
            # wiring: covers list + redirect maps keyed by the same symbol
            if sym in shims.get("covers", []):
                shims["covers"].remove(sym)
            # field wiring keys carry a colon the source marker does not
            flat = sym.replace(":", "")
            for mkey in ("staticRedirects", "instanceRedirects", "fieldRedirects"):
                mp = shims.get(mkey, {})
                for k in [k for k in mp if k.replace(":", "") == flat]:
                    del mp[k]
        io.open(path, "w", encoding="utf-8", newline="\n").write(text)
        print(f"{path}: pruned {len(doomed)}")

    json.dump(shims, io.open(shims_path, "w", encoding="utf-8", newline="\n"),
              indent=1, ensure_ascii=False)
    old = []
    try:
        old = json.load(io.open(retry_path, encoding="utf-8"))
    except Exception:
        pass
    json.dump(old + retry, io.open(retry_path, "w", encoding="utf-8", newline="\n"),
              indent=1, ensure_ascii=False)
    print(f"total pruned {len(removed)}, retry list -> {retry_path} ({len(old) + len(retry)})")


if __name__ == "__main__":
    main()
