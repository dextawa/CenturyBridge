"""1.21.8 -> 1.21.9 input events: the wiring for the GUI listener methods whose raw GLFW values became records.

1.21.9 packs keyPressed(key, scancode, modifiers) into KeyEvent, charTyped(char, modifiers) into CharacterEvent and
mouseClicked(x, y, button) into MouseButtonEvent(x, y, MouseButtonInfo(button, modifiers)) plus a double-click flag;
see the layer's rt/CbInput for what each converter and bridge does. For every member below this writes

  - a callReshapes entry on each class V declares the old shape in (a call's JVM-resolved home), building the
    record from the call's values (CbInput's converters);
  - an overrideHooks entry in bridge mode on the topmost V declaration (onlyUnder): the new shape vanilla calls
    hands the old override 1.21.8's values (CbInput's bridges and their adapter interfaces);
  - staticRedirects for Screen's keyboard helpers 1.21.9 removed.

Only classes where W declares or inherits the new shape are wired; anything else is reported.

  python3 core/tools/gen_s9input.py <V code jar> <W stub jar> <out fragment.json>
  (data/jars/client-1.21.8-intermediary-code.jar data/jars/client-1.21.9-intermediary.jar
   core/segments/steps/1.21.8__1.21.9.json.d/s9-input-gen.json)
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

RT = "top/dext/centurybridge/step/v1_21_9/rt/CbInput"
KEY = "Lnet/minecraft/class_11908;"
CHAR = "Lnet/minecraft/class_11905;"
MOUSE = "Lnet/minecraft/class_11909;"
BUTTON = "Lnet/minecraft/class_11910;"
INPUT = "Lnet/minecraft/class_11907;"


def c(name, desc):
    return RT + "#" + name + desc


# name, old desc, new desc, call ops, (bridge static name, adapter) or None, basis
SPECS = [
    ("method_25404", "(III)Z", "(" + KEY + ")Z", ["conv:0,1,2 " + c("cbKey", "(III)" + KEY)],
     ("cbKeyPressed", "LegacyKeyPressed"), "keyPressed(key, scancode, modifiers) is keyPressed(KeyEvent)"),
    ("method_16803", "(III)Z", "(" + KEY + ")Z", ["conv:0,1,2 " + c("cbKey", "(III)" + KEY)],
     ("cbKeyReleased", "LegacyKeyReleased"), "keyReleased(key, scancode, modifiers) is keyReleased(KeyEvent)"),
    ("method_25400", "(CI)Z", "(" + CHAR + ")Z", ["conv:0,1 " + c("cbChar", "(CI)" + CHAR)],
     ("cbCharTyped", "LegacyCharTyped"), "charTyped(char, modifiers) is charTyped(CharacterEvent); a supplementary codepoint reaches the old override as the two chars 1.21.8's KeyboardHandler sent"),
    ("method_25402", "(DDI)Z", "(" + MOUSE + "Z)Z",
     ["conv:0,1,2 " + c("cbClick", "(DDI)" + MOUSE), "conv:2 " + c("cbDoubleClick", "(I)Z")],
     ("cbMouseClicked", "LegacyMouseClicked"), "mouseClicked(x, y, button) is mouseClicked(MouseButtonEvent, doubleClick); modifiers and the flag of the click being handled"),
    ("method_25406", "(DDI)Z", "(" + MOUSE + ")Z", ["conv:0,1,2 " + c("cbClick", "(DDI)" + MOUSE)],
     ("cbMouseReleased", "LegacyMouseReleased"), "mouseReleased(x, y, button) is mouseReleased(MouseButtonEvent)"),
    ("method_25403", "(DDIDD)Z", "(" + MOUSE + "DD)Z", ["conv:0,1,2 " + c("cbClick", "(DDI)" + MOUSE), "keep:3", "keep:4"],
     ("cbMouseDragged", "LegacyMouseDragged"), "mouseDragged(x, y, button, dx, dy) is mouseDragged(MouseButtonEvent, dx, dy)"),
    ("method_25348", "(DD)V", "(" + MOUSE + "Z)V", ["conv:0,1 " + c("cbClickAt", "(DD)" + MOUSE), "push " + c("cbDoubleClickCurrent", "()Z")],
     ("cbOnClick", "LegacyOnClick"), "AbstractWidget.onClick(x, y) is onClick(MouseButtonEvent, doubleClick); the button of the click being handled"),
    ("method_25357", "(DD)V", "(" + MOUSE + ")V", ["conv:0,1 " + c("cbClickAt", "(DD)" + MOUSE)],
     ("cbOnRelease", "LegacyOnRelease"), "AbstractWidget.onRelease(x, y) is onRelease(MouseButtonEvent)"),
    ("method_25349", "(DDDD)V", "(" + MOUSE + "DD)V", ["conv:0,1 " + c("cbClickAt", "(DD)" + MOUSE), "keep:2", "keep:3"],
     ("cbOnDrag", "LegacyOnDrag"), "AbstractWidget.onDrag(x, y, dx, dy) is onDrag(MouseButtonEvent, dx, dy)"),
    ("method_25351", "(I)Z", "(" + BUTTON + ")Z", ["conv " + c("cbButton", "(I)" + BUTTON)],
     ("cbValidClickButton", "LegacyValidClickButton"), "AbstractWidget.isValidClickButton(button) takes MouseButtonInfo"),
    ("method_25306", "()V", "(" + INPUT + ")V", ["push " + c("cbCurrentInput", "()" + INPUT)],
     ("cbOnPress", "LegacyOnPress"), "AbstractButton.onPress() takes the input that pressed it (CycleButton reads its shift); a call passes the input being handled"),
    ("method_1417", "(II)Z", "(" + KEY + ")Z", ["conv:0,1 " + c("cbKeyCode", "(II)" + KEY)],
     ("cbKeyMatches", "LegacyKeyMatches"), "KeyMapping.matches(key, scancode) takes KeyEvent (1.21.9 reads key and scancode only)"),
    ("method_1433", "(I)Z", "(" + MOUSE + ")Z", ["conv " + c("cbButtonEvent", "(I)" + MOUSE)],
     ("cbMouseMatches", "LegacyMouseMatches"), "KeyMapping.matchesMouse(button) takes MouseButtonEvent (1.21.9 reads the button only)"),
    ("method_15985", "(II)Lnet/minecraft/class_3675$class_306;", "(" + KEY + ")Lnet/minecraft/class_3675$class_306;",
     ["conv:0,1 " + c("cbKeyCode", "(II)" + KEY)], None, "InputConstants.getKey(key, scancode) takes KeyEvent (reads key and scancode only)"),
    ("method_16199", "(C)Z", "(" + CHAR + ")Z", ["conv " + c("cbCharFor", "(C)" + CHAR)],
     ("cbHelperCharTyped", "LegacyHelperCharTyped"), "TextFieldHelper.charTyped(char) takes CharacterEvent"),
    ("method_16202", "(I)Z", "(" + KEY + ")Z", ["conv " + c("cbKeyFor", "(I)" + KEY)],
     ("cbHelperKeyPressed", "LegacyHelperKeyPressed"), "TextFieldHelper.keyPressed(key) takes KeyEvent; 1.21.8 asked the live keyboard for the modifiers its shortcuts test"),
    ("method_44428", "(I)Z", "(" + KEY + ")Z", ["conv " + c("cbKeyFor", "(I)" + KEY)],
     ("cbTextFieldKeyPressed", "LegacyTextFieldKeyPressed"), "MultilineTextField.keyPressed(key) takes KeyEvent; 1.21.8 asked the live keyboard for the modifiers"),
    ("method_23924", "(III)Z", "(" + KEY + ")Z", ["conv:0,1,2 " + c("cbKey", "(III)" + KEY)],
     ("cbSuggestionsKeyPressed", "LegacySuggestionsKeyPressed"), "CommandSuggestions.keyPressed(key, scancode, modifiers) takes KeyEvent"),
    ("method_23922", "(DDI)Z", "(" + MOUSE + ")Z", ["conv:0,1,2 " + c("cbClick", "(DDI)" + MOUSE)],
     ("cbSuggestionsMouseClicked", "LegacySuggestionsMouseClicked"), "CommandSuggestions.mouseClicked(x, y, button) takes MouseButtonEvent"),
    ("method_2377", "(III)Z", "(" + KEY + ")Z", ["conv:0,1,2 " + c("cbKey", "(III)" + KEY)],
     ("cbSuggestionsListKeyPressed", "LegacySuggestionsListKeyPressed"), "CommandSuggestions.SuggestionsList.keyPressed takes KeyEvent"),
    ("method_65505", "(DDI)Z", "(" + MOUSE + ")Z", ["conv:0,1,2 " + c("cbClick", "(DDI)" + MOUSE)],
     ("cbUpdateScrolling", "LegacyUpdateScrolling"), "AbstractScrollArea.updateScrolling(x, y, button) takes MouseButtonEvent"),
    ("method_2384", "(II)Z", "(" + KEY + ")Z", ["conv:0,1 " + c("cbKeyCode", "(II)" + KEY)],
     ("cbHotbarKey", "LegacyHotbarKey"), "AbstractContainerScreen.checkHotbarKeyPressed(key, scancode) takes KeyEvent (matched through KeyMapping.matches)"),
    ("method_2381", "(DDIII)Z", "(DDII)Z", ["keep", "keep", "keep", "keep", "drop"],
     ("cbClickedOutside", "LegacyClickedOutside"), "AbstractContainerScreen.hasClickedOutside lost its button (V's body never read it); an old override gets the button of the click being handled"),
    ("method_2632", "(DDIIIII)Z", "(" + MOUSE + "IIIIZ)Z",
     ["conv:0,1,2 " + c("cbClick", "(DDI)" + MOUSE), "keep:3", "keep:4", "keep:5", "keep:6", "conv:2 " + c("cbDoubleClick", "(I)Z")],
     ("cbRecipePageClicked", "LegacyRecipePageClicked"), "RecipeBookPage.mouseClicked(x, y, button, left, top, width, height) takes MouseButtonEvent and the double-click flag"),
    ("method_1466", "(JIIII)V", "(JI" + KEY + ")V", ["keep:0", "keep:3", "conv:1,2,4 " + c("cbKey", "(III)" + KEY)],
     None, "KeyboardHandler.keyPress(window, key, scancode, action, modifiers) is keyPress(window, action, KeyEvent); private in 1.21.9, "
     "opened by the layer's access widener (vanilla calls it privately, so no override is ever reached)"),
    ("method_25414", "(Ljava/lang/String;CI)Z", "(Ljava/lang/String;II)Z", ["keep", "conv " + c("cbCodepoint", "(C)I"), "keep"],
     ("cbValidNameChar", "LegacyValidNameChar"), "Screen.isValidCharacterForName(name, char, cursor) takes a codepoint"),
]

# Screen's keyboard helpers 1.21.9 removed: same-name statics on CbInput (1.21.8's bodies over 1.21.9's live key state)
STATICS = ["method_25441()Z", "method_25442()Z", "method_25443()Z",
           "method_25436(I)Z", "method_25437(I)Z", "method_25438(I)Z", "method_25439(I)Z"]


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith("net/minecraft/"):
                k = classfile.read(z.read(n))
                if k:
                    out[k.name] = k
    return out


def supers(classes, c):
    out, todo = [], [c]
    while todo:
        x = todo.pop(0)
        if x is None or x in out:
            continue
        out.append(x)
        k = classes.get(x)
        if k:
            todo.append(k.super)
            todo.extend(k.interfaces)
    return out


def declares(k, name, desc):
    return any(m.name == name and m.desc == desc for m in k.methods)


def main():
    vjar, wjar, out = sys.argv[1:4]
    V, W = load(vjar), load(wjar)
    frag = {"callReshapes": {}, "overrideHooks": [], "staticRedirects": {}}
    report = []
    for name, old, new, ops, bridge, basis in SPECS:
        decl = sorted(c for c, k in V.items() if declares(k, name, old))
        if not decl:
            report.append(f"{name}{old}: no V declaration")
            continue
        for cl in decl:
            if cl not in W or not any(declares(W[s], name, new) for s in supers(W, cl) if s in W):
                report.append(f"{cl}.{name}{old}: W has no {name}{new} on it")
                continue
            e = {"desc": new, "ops": ops, "basis": "s9 input: " + basis}
            frag["callReshapes"][f"{cl}.{name}{old}"] = e
        if bridge:
            tops = [cl for cl in decl if not any(s in decl for s in supers(V, cl)[1:])]
            for top in tops:
                bname, adapter = bridge
                args = new[1:new.index(")")]
                bdesc = f"(L{RT}${adapter};{args}){new[new.index(')') + 1:]}"
                frag["overrideHooks"].append({
                    "old": name + old, "new": name + new,
                    "bridge": f"{RT}.{bname}{bdesc}", "adapter": f"{RT}${adapter}",
                    "guard": "top/dext/centurybridge/step/v1_21_9/rt/Guard", "onlyUnder": top,
                    "basis": "s9 input: " + basis + "; the old override gets 1.21.8's values (CbInput)"})
    for s in STATICS:
        frag["staticRedirects"]["net/minecraft/class_437." + s] = RT
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        json.dump(frag, f, indent=1, sort_keys=True)
        f.write("\n")
    print(f"{len(frag['callReshapes'])} callReshapes, {len(frag['overrideHooks'])} overrideHooks, "
          f"{len(frag['staticRedirects'])} staticRedirects -> {out}")
    for r in report:
        print("  " + r)


if __name__ == "__main__":
    main()
