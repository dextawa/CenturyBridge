"""Nest whole library artifacts in a step layer, verbatim: the libraries a release dropped that the step gives back.

  python3 core/tools/nest_libraries.py <layer resources dir> <runtimeDir> <classesDir>

<layer resources dir>/nested-libraries.tsv lists, per row, "release  group:artifact:version  sha1  licence" -- the sha1
being the one Mojang's version JSON of that release gives for that artifact (core/tools/nest_libraries.py never takes a jar whose bytes do not match it).
Each jar is read from <runtimeDir>/client-<release>/libraries (maven layout), checked, and written to <classesDir>/META-INF/jars/ with one entry
added: a fabric.mod.json, since Fabric Loader loads only nested jars that are mods. It is the one Loom generates for a
nested library (id group_artifact with dots as underscores, lower case, at most 64 characters; custom
"fabric-loom:generated"), so Fabric Loader treats ours and another mod's nested copy of the same library as one mod and
keeps the newest version. Every other entry -- the classes and the jar's own LICENSE / NOTICE -- is copied byte for byte.
The custom value "centurybridge:verbatim" tells the converter, the wall remapper and the mixin repair to carry the jar
through untouched when the layer climbs. The layer's fabric.mod.json gets a "jars" entry per library.
"""
import hashlib
import io
import json
import os
import sys
import zipfile


def loom_id(group, artifact):
    return (group + "_" + artifact).replace(".", "_").lower()[:64]


def main():
    res, runtime, classes = sys.argv[1:4]
    table = os.path.join(res, "nested-libraries.tsv")
    rows = [l.rstrip("\n").split("\t") for l in open(table, encoding="utf-8") if l.strip() and not l.startswith("#")]
    out_dir = os.path.join(classes, "META-INF", "jars")
    os.makedirs(out_dir, exist_ok=True)
    jars = []
    for release, coord, sha1, licence in rows:
        libdir = os.path.join(runtime, f"client-{release}", "libraries")
        group, artifact, version = coord.split(":")
        src = os.path.join(libdir, *group.split("."), artifact, version, f"{artifact}-{version}.jar")
        data = open(src, "rb").read()
        got = hashlib.sha1(data).hexdigest()
        if got != sha1:
            sys.exit(f"{coord}: sha1 {got} is not the version JSON's {sha1} ({src})")
        zin = zipfile.ZipFile(io.BytesIO(data))
        if "fabric.mod.json" in zin.namelist():
            sys.exit(f"{coord}: already carries a fabric.mod.json")
        fmj = {"schemaVersion": 1, "id": loom_id(group, artifact), "version": version, "name": artifact,
               "custom": {"fabric-loom:generated": True, "centurybridge:verbatim": True,
                          "centurybridge:source": f"{coord} of Minecraft {release}, sha1 {sha1}", "centurybridge:license": licence}}
        buf = io.BytesIO()
        with zipfile.ZipFile(buf, "w", zipfile.ZIP_DEFLATED) as zout:
            for item in zin.infolist():
                zout.writestr(item, zin.read(item.filename))
            # a fixed date: the same inputs give the same nested jar, byte for byte, on every build
            zout.writestr(zipfile.ZipInfo("fabric.mod.json", date_time=(1980, 1, 1, 0, 0, 0)), json.dumps(fmj, indent=1))
        name = f"{artifact}-{version}.jar"
        with open(os.path.join(out_dir, name), "wb") as f:
            f.write(buf.getvalue())
        jars.append(name)
    fmj_path = os.path.join(classes, "fabric.mod.json")
    layer = json.load(open(fmj_path, encoding="utf-8"))
    layer["jars"] = [{"file": f"META-INF/jars/{n}"} for n in jars]
    with open(fmj_path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(layer, f, indent=1)
    print(f"nested libraries: {len(jars)} verbatim ({', '.join(jars)})")


if __name__ == "__main__":
    main()
