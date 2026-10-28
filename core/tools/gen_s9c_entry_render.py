"""gen_s9c_entry_render.py: AbstractSelectionList.Entry.render, 1.21.8 -> 1.21.9, for every vanilla entry class.

  python core/tools/gen_s9c_entry_render.py <V code jar> <W code jar>
      (data/jars/client-1.21.8-intermediary-code.jar data/jars/client-1.21.9-intermediary-code.jar)

1.21.9 gave each list entry its own position (Entry.x / y / width / height, set by AbstractSelectionList.repositionEntries)
and render(graphics, index, top, left, width, height, mouseX, mouseY, hovered, delta) became
renderContent(graphics, mouseX, mouseY, hovered, delta) (same intermediary name method_25343): the entry reads its
row from itself. 1.21.8's AbstractSelectionList.renderItem passed index = the entry's index, top = getRowTop(index),
left = getRowLeft(), width = getRowWidth(), height = itemHeight - 4; 1.21.9's repositionEntries puts the entry at
x = getRowLeft() (1.21.9's has no "+ 2"), y = the running row top, width = getRowWidth(), height = the entry height
(defaultEntryHeight = 1.21.8's itemHeight), so 1.21.8's values are the entry's getContentY() (y + 2), getContentX()
(x + 2: 1.21.8's getRowLeft added the 2), getWidth() and getContentHeight() (height - 4) (javap of both).

Writes core/segments/steps/1.21.8__1.21.9.json.d/s9c-entry-render-gen.json: a callReshapes entry for each vanilla
class that declares render concretely in 1.21.8 and renderContent in 1.21.9 -- a 1.21.8 mod entry's super.render(..)
(and any call) keeps graphics, mouse position, hovered and delta and drops the position arguments, which the entry's
own fields hold in 1.21.9. The override hook (1.21.9 calls renderContent; a 1.21.8 override gets 1.21.8's values) is
s9c-lists.json's.
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import bytecode  # noqa: E402

OLD = "(Lnet/minecraft/class_332;IIIIIIIZF)V"
NEW = "(Lnet/minecraft/class_332;IIZF)V"
NAME = "method_25343"
OUT = "core/segments/steps/1.21.8__1.21.9.json.d/s9c-entry-render-gen.json"


def declarers(jar, desc):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class"):
                k = bytecode.read(z.read(n))
                if k is None:
                    continue
                for m in k.methods:
                    if m.name == NAME and m.desc == desc and not m.access & bytecode.ACC_BRIDGE:
                        out[k.name] = m.insns is not None
    return out


def main():
    v = declarers(sys.argv[1], OLD)
    w = declarers(sys.argv[2], NEW)
    reshapes = {}
    for cls in sorted(v):
        if not v[cls] or cls not in w:
            continue
        reshapes[cls + "." + NAME + OLD] = {
            "basis": "s9c lists: Entry.render(graphics, index, top, left, width, height, mouseX, mouseY, hovered, delta) is"
                     " renderContent(graphics, mouseX, mouseY, hovered, delta) on 1.21.9; the row comes from the entry's own"
                     " position, which 1.21.9's list sets to 1.21.8's values (gen_s9c_entry_render.py)",
            "desc": NEW,
            "ops": ["keep", "drop", "drop", "drop", "drop", "drop", "keep", "keep", "keep", "keep"],
        }
    with open(OUT, "w", encoding="utf-8", newline="\n") as f:
        json.dump({"callReshapes": reshapes}, f, indent=1)
        f.write("\n")
    print(f"{len(reshapes)} callReshapes -> {OUT}", file=sys.stderr)


if __name__ == "__main__":
    main()
