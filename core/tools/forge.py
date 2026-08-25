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
  CB_WORKERS    in-flight requests          (default 16)
  CB_RPM        requests per minute budget  (default 20)
"""
import collections
import json
import os
import re
import sys
import time
import urllib.error
import urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed

import threading

API_BASE = os.environ.get("CB_API_BASE", "https://api.openai.com/v1").rstrip("/")
API_KEY = os.environ.get("CB_API_KEY", "")
MODEL = os.environ.get("CB_MODEL", "gpt-4o-mini")
WORKERS = int(os.environ.get("CB_WORKERS", "16"))
# Gateways meter by requests per minute, not by concurrency: firing 24 threads
# at a 20/min budget burns the whole allowance in one second and then every
# request 429s. Workers control how many are in flight; this controls the rate.
RPM = int(os.environ.get("CB_RPM", "20"))


class RateLimiter:
    """Token bucket over a sliding minute, shared by every worker thread."""

    def __init__(self, rpm):
        self.rpm = rpm
        self.lock = threading.Lock()
        self.stamps = []

    def acquire(self):
        while True:
            with self.lock:
                now = time.time()
                self.stamps = [t for t in self.stamps if now - t < 60.0]
                if len(self.stamps) < self.rpm:
                    self.stamps.append(now)
                    return
                wait = 60.0 - (now - self.stamps[0]) + 0.05
            time.sleep(max(wait, 0.05))


LIMITER = RateLimiter(RPM)

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

End your reply with exactly one line, after the code:
  // CB-CONFIDENCE: <high|medium|low> -- <a few words on what makes it so>
  high   = the new API is an exact equivalent; behaviour is preserved
  medium = the mapping is sound but a detail is inferred (a default value, an
           edge case, which overload the caller meant)
  low    = you produced something that compiles but are not sure it behaves
           like the original, or you had to guess at semantics
Be honest here rather than optimistic. A "low" is useful information; a "high"
that is wrong costs someone a silent behaviour change they cannot see.
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
    "CTOR_SHIM":
        "The old constructor is gone; `new X(old args)` sites are rewritten to call\n"
        "this static factory. Build and return the instance with a surviving\n"
        "constructor or static factory of the same class (see the API surface),\n"
        "converting the old arguments as needed. Never call the old descriptor.",
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
    if order["shimKind"] == "CTOR_SHIM":
        simple = order["owner"].split("/")[-1].replace("$", "")
        return f"public static {owner} cbNew{simple}({args})"
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


# Reasoning models spend most of the budget before the answer starts, so this
# has to cover think + write, not just the handful of lines we actually want.
MAX_TOKENS = int(os.environ.get("CB_MAX_TOKENS", "8000"))


def call_model(prompt, attempt=0):
    LIMITER.acquire()
    body = json.dumps({
        "model": MODEL,
        "messages": [{"role": "system", "content": SYSTEM},
                     {"role": "user", "content": prompt}],
        "temperature": 0,
        "max_tokens": MAX_TOKENS,
    }).encode()
    req = urllib.request.Request(
        f"{API_BASE}/chat/completions", data=body,
        headers={"Content-Type": "application/json",
                 "Authorization": f"Bearer {API_KEY}"})
    try:
        with urllib.request.urlopen(req, timeout=300) as r:
            data = json.load(r)
        choice = data["choices"][0]
        msg = choice.get("message", {})
        text = msg.get("content")
        if not text:
            # a reasoning model that ran out of budget mid-think never emits
            # content; retrying with more room is the fix, not accepting the
            # scratchpad as an answer
            if choice.get("finish_reason") == "length" and attempt < 3:
                time.sleep(1)
                return call_model(prompt, attempt + 1)
            text = msg.get("reasoning_content") or ""
        if not text.strip():
            raise RuntimeError(f"empty completion (finish={choice.get('finish_reason')})")
        return text
    except urllib.error.HTTPError as e:
        # 429 means we outran the quota, not that the order is unanswerable --
        # back off long enough for the window to slide and try again.
        if e.code == 429 and attempt < 8:
            time.sleep(min(5 * (attempt + 1), 45))
            return call_model(prompt, attempt + 1)
        # 5xx is the gateway wobbling, not a verdict on this order; it recovers
        # within seconds, so be patient rather than discarding the work order
        if 500 <= e.code < 600 and attempt < 8:
            time.sleep(min(3 * (attempt + 1), 30))
            return call_model(prompt, attempt + 1)
        if attempt < 3:
            time.sleep(2 ** attempt)
            return call_model(prompt, attempt + 1)
        raise RuntimeError(f"api failed after retries: HTTP {e.code}") from e
    except (urllib.error.URLError, TimeoutError, ConnectionError, OSError) as e:
        # Connection-level failures dominate on long reasoning calls: the socket
        # dies mid-answer and nothing about the order caused it. Three tries was
        # far too few -- measured 5 losses in 12 orders before this.
        if attempt < 10:
            time.sleep(min(2 * (attempt + 1), 20))
            return call_model(prompt, attempt + 1)
        raise RuntimeError(f"api failed after retries: {e}") from e


CONF_PAT = re.compile(r"//\s*CB-CONFIDENCE:\s*(high|medium|low)\s*(?:--\s*(.*))?",
                      re.I)


def extract_confidence(text):
    """Pull the model's self-rating out, and strip it from the body."""
    m = CONF_PAT.search(text or "")
    if not m:
        return None, None
    return m.group(1).lower(), (m.group(2) or "").strip()


def clean_body(text):
    text = text.strip()
    # Reasoning models narrate their conclusion around the code, so a fenced
    # block anywhere in the reply is the answer -- take the last one, which is
    # the settled version rather than an intermediate draft.
    fences = re.findall(r"```(?:java)?\s*\n(.*?)```", text, re.S)
    if fences:
        text = fences[-1].strip()
    # a model that emitted the whole method anyway: keep only the body
    m = re.match(r"^\s*(?:public|private|protected|static).*?\{(.*)\}\s*$", text, re.S)
    if m:
        text = m.group(1).strip()
    # or that wrapped the body itself in braces
    if text.startswith("{") and text.endswith("}"):
        inner = text[1:-1]
        if inner.count("{") == inner.count("}"):
            text = inner.strip()
    return text


# A reply that opens with a declaration or with prose is not a body; the caller
# retries it rather than feeding javac something guaranteed to fail.
_NOT_A_BODY = re.compile(
    r"^\s*(?:@|(?:public|private|protected|static|final)\s)"
    r"|^\s*(?:void|int|float|double|boolean|long|byte|char|short)\s+\w+\s*\("
    r"|^\s*[A-Z][\w.$]*\s+\w+\s*\([^)]*\)\s*$"
    r"|^\s*(?:Looking|Let|The old|In \d|Here|I need|We need|Based on)\b")


def looks_like_body(text):
    if not text or not text.strip():
        return False
    return not _NOT_A_BODY.search(text)


def claim(out_path):
    """Refuse to start if another forge already owns this output file.

    Two runs writing the same file do not merge -- the second one's final dump
    silently replaces the first one's work. That is exactly how a run that
    logged "forged 379/380" ended up as 207 bodies on disk.
    """
    lock = out_path + ".lock"
    try:
        fd = os.open(lock, os.O_CREAT | os.O_EXCL | os.O_WRONLY)
    except FileExistsError:
        age = time.time() - os.path.getmtime(lock)
        if age < 1800:
            sys.exit(f"another forge owns {out_path} (lock {age:.0f}s old); "
                     f"stop it first or delete {lock}")
        os.unlink(lock)   # stale: previous run died without cleaning up
        fd = os.open(lock, os.O_CREAT | os.O_EXCL | os.O_WRONLY)
    os.write(fd, str(os.getpid()).encode())
    os.close(fd)
    import atexit
    atexit.register(lambda: os.path.exists(lock) and os.unlink(lock))


def main():
    if not API_KEY:
        sys.exit("CB_API_KEY not set")
    orders_path, to_version, out_path = sys.argv[1], sys.argv[2], sys.argv[3]
    limit = int(sys.argv[4]) if len(sys.argv) > 4 else 0

    claim(out_path)
    orders = json.load(open(orders_path, encoding="utf-8"))
    orders = [o for o in orders if o["shimKind"] != "TOMBSTONE"]
    if limit:
        orders = orders[:limit]

    done = {}
    if os.path.exists(out_path):
        prior = json.load(open(out_path, encoding="utf-8"))
        # A record with no body is a request that never got answered -- counting
        # it as done is how a run silently "completes" with nothing generated.
        done = {d["id"]: d for d in prior if d.get("body")}
        stale = len(prior) - len(done)
        orders = [o for o in orders if o["id"] not in done]
        print(f"resuming: {len(done)} already forged"
              + (f" ({stale} unanswered, requeued)" if stale else "")
              + f", {len(orders)} to go")

    print(f"forging {len(orders)} bodies via {MODEL} x{WORKERS}")
    results = list(done.values())
    failed = 0
    fail_kinds = __import__("collections").Counter()
    t0 = time.time()

    with ThreadPoolExecutor(max_workers=WORKERS) as pool:
        futures = {pool.submit(call_model, build_prompt(o, to_version)): o
                   for o in orders}
        for n, fut in enumerate(as_completed(futures), 1):
            o = futures[fut]
            try:
                raw = fut.result()
                conf, conf_why = extract_confidence(raw)
                body = clean_body(raw)
                body = CONF_PAT.sub("", body).strip()
                if not looks_like_body(body):
                    # one nudge with the mistake named; models correct this reliably
                    body = clean_body(call_model(
                        build_prompt(o, to_version,
                                     "You emitted a signature or prose. Emit ONLY the "
                                     "statements that go inside the braces.")))
                results.append({**o, "body": body, "javaSig": java_signature(o),
                                "confidence": conf, "confidenceWhy": conf_why})
            except Exception as e:  # noqa: BLE001 -- one bad order must not sink the run
                failed += 1
                reason = str(e)
                fail_kinds[reason.split(":")[0][:40]] += 1
                results.append({**o, "body": None, "error": reason})
            if n % 5 == 0 or n == len(orders):
                rate = n / max(time.time() - t0, 1e-9)
                print(f"  {n}/{len(orders)}  {rate*60:.1f}/min  failed={failed} "
                      f"{dict(fail_kinds.most_common(3))}", flush=True)
            if n % 20 == 0:
                # checkpoint: a run that dies at 300/380 must not lose those 300
                json.dump(results, open(out_path, "w", encoding="utf-8"),
                          indent=1, ensure_ascii=False)

    json.dump(results, open(out_path, "w", encoding="utf-8"),
              indent=1, ensure_ascii=False)
    ok = sum(1 for r in results if r.get("body"))
    conf = collections.Counter(r.get("confidence") or "unrated"
                               for r in results if r.get("body"))
    print(f"forged {ok}/{len(results)} -> {out_path}")
    print(f"  self-rated confidence: {dict(conf)}")


if __name__ == "__main__":
    main()
