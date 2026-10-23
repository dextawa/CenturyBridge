"""Write the GUI scroll family of core/segments/steps/1.21.3__1.21.4.json.d/s9client.json.

Hand-designed entries (each checked against 1.21.3 / 1.21.4 bytecode); kept as a script so the
family's renames, adapters and covers stay in one table. Merges into the existing fragment.
"""
import json, os, sys

FRAG = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "segments/steps/1.21.3__1.21.4.json.d/s9client.json")
RT = "top/dext/centurybridge/step/v1_21_4/rt"
G = RT + "/CbGuiScroll"
GUARD = RT + "/Guard"
ASL = "net/minecraft/class_350"
ASW = "net/minecraft/class_7528"
AW = "net/minecraft/class_339"

# (old shape, new name, onlyUnder, basis) -- same descriptor, same meaning, super calls follow
RENAMES = [
    ("method_25341()D", "method_44387", ASL, "AbstractSelectionList.getScrollAmount is AbstractScrollArea.scrollAmount since 1.21.4 (the same field read)"),
    ("method_25307(D)V", "method_44382", ASL, "AbstractSelectionList.setScrollAmount (clamped through setClampedScrollAmount) is AbstractScrollArea.setScrollAmount (clamped) since 1.21.4; vanilla sets the amount through it"),
    ("method_60322()V", "method_65506", ASL, "AbstractSelectionList.clampScrollAmount is AbstractScrollArea.refreshScrollAmount since 1.21.4 (set the current amount again, clamped)"),
    ("method_25331()I", "method_44390", ASL, "AbstractSelectionList.getMaxScroll is AbstractScrollArea.maxScrollAmount: max(0, maxPosition - (height - 4)) = max(0, contentHeight - height)"),
    ("method_57717()Z", "method_44392", ASL, "AbstractSelectionList.scrollbarVisible is AbstractScrollArea.scrollbarVisible (max scroll > 0)"),
    ("method_25329()I", "method_65507", ASL, "AbstractSelectionList.getScrollbarPosition is scrollBarX since 1.21.4 (rows' right + 8 both ways)"),
    ("method_53812(I)Z", "method_25351", ASL, "AbstractSelectionList.isValidMouseClick is AbstractWidget.isValidClickButton (button 0), which 1.21.4's list asks; the list's own isValidClickButton was unused in 1.21.3"),
    ("method_44381()I", "method_65509", ASW, "AbstractScrollWidget.innerPadding is AbstractTextAreaWidget.innerPadding (4)"),
    ("method_44385()I", "method_65512", ASW, "AbstractScrollWidget.totalInnerPadding is AbstractTextAreaWidget.totalInnerPadding (innerPadding * 2)"),
    ("method_52233(Lnet/minecraft/class_332;IIII)V", "method_65511", ASW, "AbstractScrollWidget.renderBorder(GuiGraphics, x, y, w, h) is AbstractTextAreaWidget.renderBorder"),
    ("method_44383(II)Z", "method_65510", ASW, "AbstractScrollWidget.withinContentAreaTopBottom is AbstractTextAreaWidget.withinContentAreaTopBottom"),
]

# (old shape, adapter method, adapter interface, onlyUnder, basis) -- no 1.21.4 counterpart vanilla calls
ADAPTERS = [
    ("method_60321(D)V", "cb$setClampedScrollAmount", "CbLegacyClampedScroll", ASL,
     "1.21.3 set every list scroll through setClampedScrollAmount (setScrollAmount called it); 1.21.4's setScrollAmount on a list hands the value to the override (CbGuiScrollListMixin)"),
    ("method_57718()I", "cb$getDefaultScrollbarPosition", "CbLegacyDefaultScrollbar", ASL,
     "getDefaultScrollbarPosition was getScrollbarPosition's default; 1.21.4's scrollBarX asks the override (CbGuiScrollListMixin)"),
    ("method_25310(II)Z", "cb$clickedHeader", "CbLegacyClickedHeader", ASL,
     "1.21.3's mouseClicked offered a press hitting no entry to clickedHeader; 1.21.4 has no header clicks, the list's mouseClicked asks the override again (CbGuiScrollListMixin)"),
    ("method_44388(DD)Z", "cb$withinContentAreaPoint", "CbLegacyContentAreaPoint", ASW,
     "AbstractScrollWidget.mouseClicked took a press inside withinContentAreaPoint; 1.21.4 asks isMouseOver, so a widget overriding it gets 1.21.3's mouseClicked (CbGuiScrollTextAreaMixin)"),
    ("method_25361(DD)Z", "cb$clicked", "CbLegacyClicked", AW,
     "AbstractWidget.mouseClicked asked clicked(x, y); 1.21.4 asks isMouseOver, so a widget overriding clicked is asked it there again (CbGuiScrollWidgetMixin)"),
]

COVERS_ONLY = [
    ASL + ".method_25315(ZI)V",   # setRenderHeader: called by mods only
    ASW + ".method_53532()I",     # scrollbarWidth: never read by vanilla 1.21.3
    ASL + ".method_25317()I",
    ASL + ".method_25318(DDI)V",
]


def main():
    frag = json.load(open(FRAG)) if os.path.exists(FRAG) else {}
    orr = [r for r in frag.get("overrideRenames", []) if r.get("family") != "gui-scroll"]
    covers = set(frag.get("covers", []))
    for old, new, under, basis in RENAMES:
        orr.append({"old": old, "new": new, "onlyUnder": under, "superCalls": "rename", "family": "gui-scroll", "basis": basis})
        covers.add(under + "." + old)
    for old, meth, itf, under, basis in ADAPTERS:
        orr.append({"old": old, "new": meth, "onlyUnder": under, "adapter": G + "$" + itf, "family": "gui-scroll", "basis": basis})
        covers.add(under + "." + old)
    covers.update(COVERS_ONLY)
    frag["overrideRenames"] = orr
    frag["covers"] = sorted(covers)

    hooks = [h for h in frag.get("overrideHooks", []) if h.get("family") != "gui-scroll"]
    hooks.append({
        "old": "method_25317()I", "new": "method_44395()I",
        "unwrap": G + ".cbContentHeight(I)I", "guard": GUARD, "onlyUnder": ASL, "family": "gui-scroll",
        "basis": "AbstractSelectionList.getMaxPosition (rows * height + header) is contentHeight less 4 since 1.21.4; vanilla sizes the scroll range by contentHeight"})
    hooks.append({
        "old": "method_25318(DDI)V", "new": "method_65505(DDI)Z",
        "bridge": G + ".cbUpdateScrolling(L" + G + "$CbLegacyUpdateScrollingState;DDI)Z",
        "adapter": G + "$CbLegacyUpdateScrollingState", "guard": GUARD, "onlyUnder": ASL, "family": "gui-scroll",
        "basis": "AbstractSelectionList.updateScrollingState set the drag state; 1.21.4's updateScrolling sets it and answers it"})
    frag["overrideHooks"] = hooks

    fg = frag.get("fieldGetters", {})
    fg[ASL + ".field_45909:I"] = G
    frag["fieldGetters"] = fg

    cr = frag.get("classRenames", {})
    cr[ASW] = "net/minecraft/class_10415"
    frag["classRenames"] = cr
    notes = frag.get("classRenameBasis", {})
    notes[ASW] = ("1.21.3's AbstractScrollWidget (class_7528) is 1.21.4's AbstractTextAreaWidget (class_10415, same members, "
                  "same abstract getInnerHeight / scrollRate / renderContents); 1.21.4 reused class_7528 for the new AbstractScrollArea "
                  "above it. Every 1.21.3 reference to class_7528 meant the text-area widget")
    frag["classRenameBasis"] = notes

    # ChatSelectionList.Entry's constructor: mechanical.json drops the outer list; PaddingEntry passes it on to
    # Entry (read, so the generator leaves it), and Entry no longer takes it
    frag["superCtorReshapes"] = {
        "net/minecraft/class_7543$class_7544$class_7550.<init>(Lnet/minecraft/class_7543$class_7544;)V": {
            "desc": "()V", "ops": ["drop:0"],
            "basis": "ChatSelectionScreen.ChatSelectionList.PaddingEntry is a static class since 1.21.4; 1.21.3 only handed the list on to Entry, which no longer takes it"}}
    sr = frag.get("superRenames", {})
    sr["net/minecraft/class_9017"] = "top/dext/centurybridge/step/v1_21_4/facade/LegacyAbstractContainerWidget"
    frag["superRenames"] = sr
    json.dump(frag, open(FRAG, "w"), indent=1, ensure_ascii=False)
    open(FRAG, "a").write("\n")


if __name__ == "__main__":
    main()
