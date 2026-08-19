"""Concurrent bridge-body forge.

Each work order carries one damaged symbol with its old signature, the new
signature, and the target class's surviving API surface. A small model writes
ONLY the method body; everything structural -- the @Mixin vs Statics choice,
imports, the JSON wiring -- was already decided deterministically by
BridgeForge, because that is exactly where cheap models produce plausible
wrong answers that compile-check cannot always catch.

Bodies come back, get assembled into real source, and javac is the judge:
a body that does not compile against the actual stub is rejected and retried
with the compiler error fed back. Nothing lands unverified.

Env:
  CB_API_BASE   OpenAI-compatible endpoint  (default https://api.openai.com/v1)
  CB_API_KEY    key
  CB_MODEL      model id                    (default gpt-4o-mini)
  CB_WORKERS    concurrency                 (default 16)
"""
import json
import os
import re
import sys
import time
import urllib.error
import urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed

API_BASE = os.environ.get("CB_API_BASE", "https://api.openai.com/v1").rstrip("/")
API_KEY = os.environ.get("CB_API_KEY", "")
MODEL = os.environ.get("CB_MODEL", "gpt-4o-mini")
WORKERS = int(os.environ.get("CB_WORKERS", "16"))

SYSTEM = """You port Minecraft API call sites across versions for CenturyBridge.

You are given ONE damaged member: its signature on the OLD version, what
replaced it on the NEW version, and the NEW version's full API surface for
that class. You write the body that makes the old signature work again on the
new version.

Names are Fabric intermediary (class_1234 / method_5678 / field_9012). They are
stable identifiers, not obfuscation you need to decode -- use them verbatim.
Fully qualify Minecraft types as net.minecraft.class_1234; inner classes are
net.minecraft.class_1234$class_5678 in descriptors but
net.minecraft.class_1234.class_5678 in Java source.

Rules:
- Output ONLY the method body statements. No signature, no braces around the
  whole body, no imports, no commentary, no markdown fences.
- Delegate to the new API whenever a faithful equivalent exists. Preserve the
  old semantics; a plausible-looking call with different behaviour is worse
  than an honest failure.
- Widen or narrow primitives explicitly ((float) d), and cast reference types
  only when the new API guarantees the type.
- For a receiver-as-arg0 static shim, the first parameter IS the old receiver;
  call the instance method on it.
- If the new API genuinely cannot express the old contract, write exactly:
    throw new UnsupportedOperationException("CenturyBridge: <specific reason>");
  Do this only when there is truly no equivalent -- a wrong bridge is a silent
  corruption, a tombstone is a legible failure.
"""

TEMPLATE = """OLD (1.20.1-era, what mods call):
  {owner}.{name}{old_desc}

NEW ({to} target):
  {new_line}

Shim form: {shim_kind}
{form_note}

Java signature you are filling in:
  {java_sig}

NEW API surface of {owner_simple}:
{api}

Write the body."""

FORM_NOTES = {
    "MIXIN_OVERLOAD":
        "A Mixin into the target class re-adds the old descriptor as an overload.\n"
        "`this` IS the target instance -- cast it: ((net.minecraft.X) (Object) this).",
    "STATIC_SHIM":
        "The body lives in a static helper; call sites are rewritten to it.\n"
        "If the old member was an instance method, parameter 0 is the old receiver.",
    "FIELD_SHIM":
        "A dead static field, rebuilt as a constant expression in a static helper.\n"
        "Write `return <expression>;` producing the old field's type.",
}


def desc_to_java(desc):
    """JVM descriptor -> (param java types, return java type)."""
    def one(s, i):
        arr = 0
        while s[i] == "[":
            arr += 1
            i += 1
        c = s[i]
        prim = {"V": "void", "Z": "boolean", "B": "byte", "C": "char", "S": "short",
                "I": "int", "J": "long", "F": "float", "D": "double"}
        if c in prim:
            t, i = prim[c], i + 1
        else:
            j = s.index(";", i)
            t = s[i + 1:j].replace("/", ".").replace("$", ".")
            i = j + 1
        return t + "[]" * arr, i

    if not desc.startswith("("):
        t, _ = one(desc, 0)
        return [], t
    i = 1
    params = []
    while desc[i] != ")":
        t, i = one(desc, i)
        params.append(t)
    ret, _ = one(desc, i + 1)
    return params, ret


def java_signature(order):
    params, ret = desc_to_java(order["oldDesc"])
    owner = "net.minecraft." + order["owner"].split("/")[-1].replace("$", ".")
    args = ", ".join(f"{t} a{i}" for i, t in enumerate(params))
    if order["shimKind"] == "FIELD_SHIM":
        return f"public static {ret} {order['name']}()"
    if order["shimKind"] == "STATIC_SHIM":
        # instance members become receiver-as-arg0
        if order["fate"] != "GONE" or not order.get("wasStatic"):
            args = f"{owner} self" + (", " + args if args else "")
        return f"public static {ret} {order['name']}({args})"
    return f"public {ret} {order['name']}({args})"


def build_prompt(order, to_version, retry_error=None):
    if order["fate"] == "DESC_CHANGED":
        new_line = f"  {order['owner']}.{order['name']}{order['newDesc']}"
    elif order["fate"] == "MOVED":
        new_line = f"  moved to: {order['newDesc']}"
    else:
        new_line = "  (deleted -- no direct replacement)"
    api = "\n".join("  " + a for a in order["ownerApi"])
    p = TEMPLATE.format(
        owner=order["owner"], name=order["name"], old_desc=order["oldDesc"],
        to=to_version, new_line=new_line, shim_kind=order["shimKind"],
        form_note=FORM_NOTES.get(order["shimKind"], ""),
        java_sig=java_signature(order),
        owner_simple=order["owner"].split("/")[-1], api=api)
    if retry_error:
        p += ("\n\nYour previous body did not compile:\n"
              f"{retry_error}\n"
              "Fix it. If no correct bridge exists, use the "
              "UnsupportedOperationException form.")
    return p


def call_model(prompt, attempt=0):
    body = json.dumps({
        "model": MODEL,
        "messages": [{"role": "system", "content": SYSTEM},
                     {"role": "user", "content": prompt}],
        "temperature": 0,
        "max_tokens": 700,
    }).encode()
    req = urllib.request.Request(
        f"{API_BASE}/chat/completions", data=body,
        headers={"Content-Type": "application/json",
                 "Authorization": f"Bearer {API_KEY}"})
    try:
        with urllib.request.urlopen(req, timeout=120) as r:
            data = json.load(r)
        return data["choices"][0]["message"]["content"]
    except (urllib.error.HTTPError, urllib.error.URLError, TimeoutError) as e:
        if attempt < 3:
            time.sleep(2 ** attempt)
            return call_model(prompt, attempt + 1)
        raise RuntimeError(f"api failed after retries: {e}") from e


def clean_body(text):
    text = text.strip()
    fence = re.match(r"^```(?:java)?\s*\n(.*?)\n```\s*$", text, re.S)
    if fence:
        text = fence.group(1).strip()
    # a model that emitted the whole method anyway: keep only the body
    m = re.match(r"^\s*(?:public|private|protected|static).*?\{(.*)\}\s*$", text, re.S)
    if m:
        text = m.group(1).strip()
    return text


def main():
    if not API_KEY:
        sys.exit("CB_API_KEY not set")
    orders_path, to_version, out_path = sys.argv[1], sys.argv[2], sys.argv[3]
    limit = int(sys.argv[4]) if len(sys.argv) > 4 else 0

    orders = json.load(open(orders_path, encoding="utf-8"))
    orders = [o for o in orders if o["shimKind"] != "TOMBSTONE"]
    if limit:
        orders = orders[:limit]

    done = {}
    if os.path.exists(out_path):
        done = {d["id"]: d for d in json.load(open(out_path, encoding="utf-8"))}
        orders = [o for o in orders if o["id"] not in done]
        print(f"resuming: {len(done)} already forged, {len(orders)} to go")

    print(f"forging {len(orders)} bodies via {MODEL} x{WORKERS}")
    results = list(done.values())
    failed = 0
    t0 = time.time()

    with ThreadPoolExecutor(max_workers=WORKERS) as pool:
        futures = {pool.submit(call_model, build_prompt(o, to_version)): o
                   for o in orders}
        for n, fut in enumerate(as_completed(futures), 1):
            o = futures[fut]
            try:
                body = clean_body(fut.result())
                results.append({**o, "body": body, "javaSig": java_signature(o)})
            except Exception as e:  # noqa: BLE001 -- one bad order must not sink the run
                failed += 1
                results.append({**o, "body": None, "error": str(e)})
            if n % 25 == 0 or n == len(orders):
                rate = n / max(time.time() - t0, 1e-9)
                print(f"  {n}/{len(orders)}  {rate:.1f}/s  failed={failed}", flush=True)

    json.dump(results, open(out_path, "w", encoding="utf-8"),
              indent=1, ensure_ascii=False)
    ok = sum(1 for r in results if r.get("body"))
    print(f"forged {ok}/{len(results)} -> {out_path}")


if __name__ == "__main__":
    main()
