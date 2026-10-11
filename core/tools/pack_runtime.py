"""Package a climbed CenturyBridge runtime as the one jar a player installs.

climb_runtime.sh leaves one Fabric mod per bridge layer in <runtime dir> (each layer built against its own
release, then climbed to the target like any mod). This wraps them as jar-in-jar mods of a single
"centurybridge" mod for that target: Fabric Loader loads every nested layer with its own mixin config and
access widener, exactly as it would load them side by side. Step 1's layer is the 1.20.2 tree, whose own
id is "centurybridge"; inside the package it becomes centurybridge_step_1_20_2 so the wrapper can carry the
name. Classes the layers share byte for byte (the res/ data-format library every layer compiles in) stay
in each layer; a class two layers carry with different bytes is refused, since only one of them would load.

  python pack_runtime.py <runtime dir> <target> <version> <out.jar>
"""
import hashlib
import io
import json
import os
import sys
import zipfile


def main():
    rdir, target, version, out = sys.argv[1:5]
    layers = sorted(f for f in os.listdir(rdir) if f.endswith(".jar"))
    seen = {}
    nested = []
    for f in layers:
        data = open(os.path.join(rdir, f), "rb").read()
        zin = zipfile.ZipFile(io.BytesIO(data))
        fmj = json.loads(zin.read("fabric.mod.json").decode("utf-8"))
        for n in zin.namelist():
            if n.endswith(".class"):
                h = hashlib.sha1(zin.read(n)).hexdigest()
                if n in seen and seen[n][0] != h:
                    sys.exit(f"{n} differs between {seen[n][1]} and {f}: only one would load")
                seen.setdefault(n, (h, f))
        if fmj.get("id") == "centurybridge":
            # the wrapper carries the name; step 1's layer gets the name the others follow
            fmj["id"] = "centurybridge_step_1_20_2"
            buf = io.BytesIO()
            with zipfile.ZipFile(buf, "w", zipfile.ZIP_DEFLATED) as zout:
                for item in zin.infolist():
                    body = json.dumps(fmj, indent=1).encode("utf-8") if item.filename == "fabric.mod.json" else zin.read(item.filename)
                    zout.writestr(item, body)
            data = buf.getvalue()
        nested.append((f, data, fmj["id"]))
    wrapper = {
        "schemaVersion": 1,
        "id": "centurybridge",
        "version": f"{version}+{target}",
        "name": "CenturyBridge",
        "description": "Runs Fabric mods written for 1.20.1 and later on " + target + ": every release boundary in between has its own bridge layer.",
        "environment": "*",
        "license": "MIT",
        "jars": [{"file": f"META-INF/jars/{f}"} for f, _, _ in nested],
        "depends": {"fabricloader": ">=0.15.0", "minecraft": target},
    }
    with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as z:
        z.writestr("fabric.mod.json", json.dumps(wrapper, indent=1))
        for f, data, _ in nested:
            z.writestr(f"META-INF/jars/{f}", data)
    print(f"{out}: {len(nested)} layers ({', '.join(i for _, _, i in nested)})")


if __name__ == "__main__":
    main()
