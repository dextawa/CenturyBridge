"""Minimal driver for mcmcp's loopback JSON-RPC.

  python mc.py ping
  python mc.py list
  python mc.py call <tool> ['{"json":"args"}']

One request per invocation keeps every interaction visible in the transcript --
this is the self-test harness, so auditability beats connection reuse.
"""
import json
import socket
import sys

PORT = 25599


def rpc(method, params=None, timeout=90):
    req = {"jsonrpc": "2.0", "id": 1, "method": method}
    if params is not None:
        req["params"] = params
    with socket.create_connection(("127.0.0.1", PORT), timeout=timeout) as s:
        s.sendall((json.dumps(req) + "\n").encode())
        buf = b""
        while not buf.endswith(b"\n"):
            chunk = s.recv(65536)
            if not chunk:
                break
            buf += chunk
    return json.loads(buf.decode())


def main():
    cmd = sys.argv[1]
    if cmd == "ping":
        print(rpc("ping"))
    elif cmd == "list":
        r = rpc("tools/list")
        for t in r["result"]["tools"]:
            print(f"  {t['name']:28s} {t['description'][:70]}")
    elif cmd == "call":
        tool = sys.argv[2]
        args = json.loads(sys.argv[3]) if len(sys.argv) > 3 else {}
        r = rpc("tools/call", {"name": tool, "arguments": args})
        out = r.get("result", r.get("error"))
        s = json.dumps(out, ensure_ascii=False, indent=1)
        # screenshots are huge; keep the transcript readable
        if isinstance(out, dict) and "base64" in out:
            b64 = out.pop("base64")
            open("D:/tmp/mc-screenshot.png", "wb").write(
                __import__("base64").b64decode(b64))
            out["base64"] = f"<{len(b64)} chars -> D:/tmp/mc-screenshot.png>"
            s = json.dumps(out, ensure_ascii=False, indent=1)
        print(s[:3000])


if __name__ == "__main__":
    main()
