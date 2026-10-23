"""Write the hand-designed families (GUI scroll, GUI misc) of core/segments/steps/1.21.3__1.21.4.json.d/s9client.json.

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
    ("method_53812(I)Z", "cb$isValidMouseClick", "CbLegacyValidMouseClick", ASL,
     "1.21.3's list mouseClicked first asked isValidMouseClick and refused the press on false; 1.21.4 has no such gate. The list's mouseClicked asks the override again (CbGuiScrollListMixin). Not renamed onto isValidClickButton: a list overriding both keeps each where 1.21.3 asked it"),
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
    gui_misc(frag)
    item_predicates(frag)
    item_renderer(frag)
    models(frag)
    render_dispatch(frag)
    render_states(frag)
    texture_metadata(frag)
    textures(frag)
    equipment(frag)
    block_entity_renders(frag)
    render_tail(frag)
    json.dump(frag, open(FRAG, "w"), indent=1, ensure_ascii=False)
    open(FRAG, "a").write("\n")


M = RT + "/CbGuiMisc"


def gui_misc(frag):
    """Small GUI constructor / static / return reshapes of 1.21.3 -> 1.21.4."""
    cr = frag.setdefault("callReshapes", {})
    cr["net/minecraft/class_332.method_51440(Lnet/minecraft/class_327;Lnet/minecraft/class_5348;IIII)V"] = {
        "desc": "(Lnet/minecraft/class_327;Lnet/minecraft/class_5348;IIIIZ)V",
        "ops": ["keep", "keep", "keep", "keep", "keep", "keep", "push " + M + "#cbNoShadow()Z"],
        "basis": "GuiGraphics.drawWordWrap drew its lines without shadow in 1.21.3 (drawString(.., false)); 1.21.4 takes the shadow flag"}
    cr["net/minecraft/class_425.method_18819(Lnet/minecraft/class_310;)V"] = {
        "desc": "(Lnet/minecraft/class_1060;)V",
        "ops": ["conv " + M + "#cbTextureManager(Lnet/minecraft/class_310;)Lnet/minecraft/class_1060;"],
        "basis": "LoadingOverlay.registerTextures takes the Minecraft's texture manager since 1.21.4 (1.21.3 read it off the Minecraft)"}
    cr["net/minecraft/class_310.method_1544()Lnet/minecraft/class_5195;"] = {
        "desc": "()Lnet/minecraft/class_10383;", "ops": [],
        "ret": "conv " + M + "#cbMusic(Lnet/minecraft/class_10383;)Lnet/minecraft/class_5195;",
        "basis": "Minecraft.getSituationalMusic answers MusicInfo(music, volume) since 1.21.4; 1.21.3 answered the music"}
    hooks = frag.setdefault("overrideHooks", [])
    hooks[:] = [h for h in hooks if h.get("family") != "gui-misc"]
    hooks.append({
        "old": "method_1544()Lnet/minecraft/class_5195;", "new": "method_1544()Lnet/minecraft/class_10383;",
        "unwrap": M + ".cbMusicInfo(Lnet/minecraft/class_5195;)Lnet/minecraft/class_10383;",
        "guard": GUARD, "onlyUnder": "net/minecraft/class_310", "family": "gui-misc",
        "basis": "a 1.21.3 Minecraft subclass's getSituationalMusic: the music manager asks the MusicInfo shape; full volume, as 1.21.4's one-argument MusicInfo"})
    scr = frag.setdefault("superCtorReshapes", {})
    font = "push " + M + "#cbFont()Lnet/minecraft/class_327;"
    scr["net/minecraft/class_372.<init>(Lnet/minecraft/class_372$class_373;Lnet/minecraft/class_2561;Lnet/minecraft/class_2561;ZI)V"] = {
        "desc": "(Lnet/minecraft/class_327;Lnet/minecraft/class_372$class_373;Lnet/minecraft/class_2561;Lnet/minecraft/class_2561;ZI)V",
        "ops": [font, "keep", "keep", "keep", "keep", "keep"],
        "basis": "TutorialToast lays its message out with the game's font in the constructor since 1.21.4"}
    scr["net/minecraft/class_372.<init>(Lnet/minecraft/class_372$class_373;Lnet/minecraft/class_2561;Lnet/minecraft/class_2561;Z)V"] = {
        "desc": "(Lnet/minecraft/class_327;Lnet/minecraft/class_372$class_373;Lnet/minecraft/class_2561;Lnet/minecraft/class_2561;Z)V",
        "ops": [font, "keep", "keep", "keep", "keep"],
        "basis": "TutorialToast lays its message out with the game's font in the constructor since 1.21.4"}
    scr["net/minecraft/class_382$class_10364.<init>(FFILnet/minecraft/class_382;Lnet/minecraft/class_2583;F)V"] = {
        "desc": "(FFIILnet/minecraft/class_382;Lnet/minecraft/class_2583;FF)V",
        "ops": ["keep", "keep", "keep", "push " + M + "#cbNoShadowColor()I", "keep", "keep", "keep",
                "push " + M + "#cbNoShadowOffset()F"],
        "basis": "BakedGlyph.GlyphInstance carries its own shadow since 1.21.4 (colour 0 = none); a 1.21.3 instance had none -- its shadow was a second instance"}
    fg = frag.setdefault("fieldGetters", {})
    fg["net/minecraft/class_372.field_32225:I"] = M
    sr = frag.setdefault("staticRedirects", {})
    sr["net/minecraft/class_442.method_18105(Lnet/minecraft/class_1060;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"] = M + "#method_18105"
    covers = set(frag.get("covers", []))
    covers.add("net/minecraft/class_516.method_2656()Z")
    frag["covers"] = sorted(covers)


FAC = "top/dext/centurybridge/step/v1_21_4/facade/"


def item_predicates(frag):
    """1.21.3 model predicates (ItemProperties and its functions) on 1.21.4's item model definitions."""
    cr = frag.setdefault("classRenames", {})
    cr["net/minecraft/class_1800"] = FAC + "ItemPropertyFunction"
    cr["net/minecraft/class_6395"] = FAC + "ClampedItemPropertyFunction"
    cr["net/minecraft/class_5272"] = FAC + "ItemProperties"
    cr["net/minecraft/class_7391"] = FAC + "CompassItemPropertyFunction"
    cr["net/minecraft/class_7391$class_7392"] = FAC + "CompassItemPropertyFunction$CompassTarget"
    cr["net/minecraft/class_7391$class_5171"] = FAC + "CompassItemPropertyFunction$CompassWobble"
    cr["net/fabricmc/fabric/api/object/builder/v1/client/model/FabricModelPredicateProviderRegistry"] = \
        "top/dext/centurybridge/step/v1_21_4/compat/fapi/FabricModelPredicateProviderRegistry"
    basis = frag.setdefault("classRenameBasis", {})
    basis["net/minecraft/class_1800"] = ("1.21.3 ItemPropertyFunction; 1.21.4 reused class_1800 for RangeSelectItemModelProperty, "
                                         "so every 1.21.3 reference meant the predicate function")
    basis["net/minecraft/class_5272"] = "ItemProperties, ClampedItemPropertyFunction, CompassItemPropertyFunction are gone in 1.21.4"
    rt = set(frag.get("resourceTypes", []))
    rt.add("centurybridge:legacy_overrides")
    rt.add("centurybridge:legacy_color")
    frag["resourceTypes"] = sorted(rt)
    # colours
    cr["net/minecraft/class_326"] = FAC + "ItemColor"
    cr["net/minecraft/class_325"] = FAC + "ItemColors"
    V0 = "top/dext/centurybridge/step/v1_21_4/compat/fapi/v0/"
    cr["net/fabricmc/fabric/api/client/render/ColorProviderRegistry"] = V0 + "ColorProviderRegistry"
    cr["net/fabricmc/fabric/api/client/render/InvalidateRenderStateCallback"] = V0 + "InvalidateRenderStateCallback"
    fg = frag.setdefault("fieldGetters", {})
    fg["net/fabricmc/fabric/api/client/rendering/v1/ColorProviderRegistry.ITEM:Lnet/fabricmc/fabric/api/client/rendering/v1/ColorProviderRegistry;"] = \
        "top/dext/centurybridge/step/v1_21_4/compat/fapi/CbItemColorProviderRegistry#item"
    frag.setdefault("depRenames", {})["fabric-rendering-v0"] = "centurybridge_step_1_21_4@*"
    frag["_s9client_depRenames_basis"] = ("1.21.4's Fabric API (0.119.4) no longer ships fabric-rendering-v0, which 1.21.3's (0.114.1) did "
                                          "(core/tools/fapi_modules.py); this layer gives its two classes back (compat/fapi/v0: "
                                          "ColorProviderRegistry, InvalidateRenderStateCallback with v0's forwarding from rendering-v1)")



def item_renderer(frag):
    """1.21.3 ItemRenderer's model-based API and ItemModelShaper on 1.21.4."""
    IR = RT + "/CbItemRender"
    covers = set(frag.get("covers", []))
    for m in ["method_23179(Lnet/minecraft/class_1799;Lnet/minecraft/class_811;ZLnet/minecraft/class_4587;Lnet/minecraft/class_4597;IILnet/minecraft/class_1087;)V",
              "method_62477(Lnet/minecraft/class_1799;Lnet/minecraft/class_811;ZLnet/minecraft/class_4587;Lnet/minecraft/class_4597;IILnet/minecraft/class_1087;Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;I)V",
              "method_4019(Lnet/minecraft/class_1799;Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;I)Lnet/minecraft/class_1087;",
              "method_62475(Lnet/minecraft/class_1799;Lnet/minecraft/class_1309;Lnet/minecraft/class_811;)Lnet/minecraft/class_1087;",
              "method_14491(Lnet/minecraft/class_3300;)V"]:
        covers.add("net/minecraft/class_918." + m)
    frag["covers"] = sorted(covers)
    fg = frag.setdefault("fieldGetters", {})
    fg["net/minecraft/class_918.field_40532:Lnet/minecraft/class_1091;"] = IR
    fg["net/minecraft/class_918.field_40533:Lnet/minecraft/class_1091;"] = IR
    frag.setdefault("superCtorReshapes", {})["net/minecraft/class_918.<init>(Lnet/minecraft/class_1092;Lnet/minecraft/class_325;Lnet/minecraft/class_756;)V"] = {
        "desc": "(Lnet/minecraft/class_10442;)V",
        "ops": ["conv:0 " + IR + "#cbResolver(Lnet/minecraft/class_1092;)Lnet/minecraft/class_10442;"],
        "post": IR + "#cbConstructed(Lnet/minecraft/class_918;Lnet/minecraft/class_325;)V",
        "postArgs": [1],
        "basis": "ItemRenderer resolves items through an ItemModelResolver since 1.21.4 (built on the same model manager); the ItemColors given colours the renderer's models of a mod's own as in 1.21.3 (CbItemRender); the BlockEntityWithoutLevelRenderer has no place -- 1.21.4 draws special items through their definitions"}
    frag.setdefault("classRenames", {})["net/minecraft/class_763"] = FAC + "ItemModelShaper"
    # render layers that took the ItemRenderer (or ItemInHandRenderer) to draw held / worn items: 1.21.4's layers draw
    # the ItemStackRenderState their entity's render state carries, resolved by 1.21.4's own renderer
    scr = frag.setdefault("superCtorReshapes", {})
    RLP = "Lnet/minecraft/class_3883;"
    IRD = "Lnet/minecraft/class_918;"
    for owner in ["class_1005", "class_4004", "class_4043", "class_5697", "class_977", "class_989", "class_990"]:
        scr["net/minecraft/%s.<init>(%s%s)V" % (owner, RLP, IRD)] = {
            "desc": "(%s)V" % RLP, "ops": ["keep:0"],
            "basis": "the layer draws its item from the render state's ItemStackRenderState since 1.21.4; the ItemRenderer it drew with has no place"}
    scr["net/minecraft/class_996.<init>(%sLnet/minecraft/class_776;%s)V" % (RLP, IRD)] = {
        "desc": "(%sLnet/minecraft/class_776;)V" % RLP, "ops": ["keep:0", "keep:1"],
        "basis": "the layer draws its item from the render state's ItemStackRenderState since 1.21.4; the ItemRenderer it drew with has no place"}
    scr["net/minecraft/class_976.<init>(%sLnet/minecraft/class_5599;%s)V" % (RLP, IRD)] = {
        "desc": "(%sLnet/minecraft/class_5599;)V" % RLP, "ops": ["keep:0", "keep:1"],
        "basis": "CustomHeadLayer draws a head item from the render state's ItemStackRenderState since 1.21.4"}
    scr["net/minecraft/class_976.<init>(%sLnet/minecraft/class_5599;Lnet/minecraft/class_976$class_9994;%s)V" % (RLP, IRD)] = {
        "desc": "(%sLnet/minecraft/class_5599;Lnet/minecraft/class_976$class_9994;)V" % RLP, "ops": ["keep:0", "keep:1", "keep:2"],
        "basis": "CustomHeadLayer draws a head item from the render state's ItemStackRenderState since 1.21.4"}
    scr["net/minecraft/class_759.<init>(Lnet/minecraft/class_310;Lnet/minecraft/class_898;%s)V" % IRD] = {
        "desc": "(Lnet/minecraft/class_310;Lnet/minecraft/class_898;%sLnet/minecraft/class_10442;)V" % IRD,
        "ops": ["keep:0", "keep:1", "keep:2", "push " + IR + "#cbGameResolver()Lnet/minecraft/class_10442;"],
        "basis": "ItemInHandRenderer also takes the item model resolver since 1.21.4: the game's"}
    # BlockEntityWithoutLevelRenderer and Fabric API's builtin item renderers
    cr = frag.setdefault("classRenames", {})
    cr["net/minecraft/class_756"] = FAC + "BlockEntityWithoutLevelRenderer"
    FR = "top/dext/centurybridge/step/v1_21_4/compat/fapi/rendering/v1/"
    for c in ["BuiltinItemRenderer", "BuiltinItemRendererRegistry", "BuiltinItemRendererRegistry$DynamicItemRenderer"]:
        cr["net/fabricmc/fabric/api/client/rendering/v1/" + c] = FR + c
    gaps = frag.setdefault("facadeGaps", {})
    why = ("private state of the dead class: 1.21.3's own block entity instances and models it drew special items with; "
           "the facade draws them through 1.21.4's special model renderers, and only an access widener reached these")
    for f in ["field_3981:[Lnet/minecraft/class_2627;", "field_3984:Lnet/minecraft/class_2627;", "field_3976:Lnet/minecraft/class_2595;",
              "field_3978:Lnet/minecraft/class_2595;", "field_3977:Lnet/minecraft/class_2611;", "field_3983:Lnet/minecraft/class_2573;",
              "field_3982:Lnet/minecraft/class_2587;", "field_3979:Lnet/minecraft/class_2597;", "field_42909:Lnet/minecraft/class_8172;",
              "field_3980:Lnet/minecraft/class_600;", "field_3985:Lnet/minecraft/class_613;", "field_27737:Ljava/util/Map;"]:
        gaps["net/minecraft/class_756." + f] = why
    cr["net/minecraft/class_806"] = FAC + "BakedOverrides"
    cr["net/minecraft/class_806$class_5827"] = FAC + "BakedOverrides$BakedOverride"
    cr["net/minecraft/class_806$class_5828"] = FAC + "BakedOverrides$PropertyMatcher"
    cr["net/minecraft/class_799"] = FAC + "ItemOverride"
    cr["net/minecraft/class_799$class_5826"] = FAC + "ItemOverride$Predicate"
    cr["net/minecraft/class_1090"] = FAC + "BuiltInModel"
    orr = frag.setdefault("overrideRenames", [])
    orr[:] = [r for r in orr if r.get("family") != "baked-model"]
    orr.append({"old": "method_4713()Z", "new": "cb$isCustomRenderer", "onlyUnder": "net/minecraft/class_1087",
                "adapter": IR + "$CbLegacyCustomRenderer", "family": "baked-model",
                "basis": "BakedModel.isCustomRenderer is gone in 1.21.4; ItemRenderer.render(.., model) asks a 1.21.3 model again (CbItemRender)"})
    orr.append({"old": "method_4710()Lnet/minecraft/class_806;", "new": "cb$getOverrides", "onlyUnder": "net/minecraft/class_1087",
                "adapter": IR + "$CbLegacyOverrides", "family": "baked-model",
                "basis": "BakedModel.getOverrides is gone in 1.21.4; calls through BakedModel reach a 1.21.3 model's own (CbItemRender.getOverrides)"})
    ir = frag.setdefault("instanceRedirects", {})
    ir["net/minecraft/class_1087.method_4713()Z"] = IR + "#isCustomRenderer"
    ir["net/minecraft/class_1087.method_4710()Lnet/minecraft/class_806;"] = IR + "#getOverrides"
    frag.setdefault("superRedirects", {})["net/minecraft/class_1087.method_4710()Lnet/minecraft/class_806;"] = IR + "#superGetOverrides"
    rt = set(frag.get("resourceTypes", []))
    rt.add("centurybridge:legacy_builtin")
    rt.add("centurybridge:legacy_override")
    frag["resourceTypes"] = sorted(rt)


def models(frag):
    """The 1.21.4 model-loading rework: 1.21.3's unbaked-model API (UnbakedModel, BlockModel, ItemTransforms, Variant,
    FaceBakery...) on 1.21.4's. Each entry checked against both releases' bytecode."""
    cr = frag.setdefault("classRenames", {})
    notes = frag.setdefault("classRenameBasis", {})
    cr["net/minecraft/class_793$class_4751"] = "net/minecraft/class_1100$class_4751"
    notes["net/minecraft/class_793$class_4751"] = ("BlockModel.GuiLight moved to UnbakedModel.GuiLight in 1.21.4 (same enum: "
                                                   "FRONT / SIDE, getByName, lightLikeBlock)")
    cr["net/minecraft/class_1100$class_10103"] = "net/minecraft/class_10526$class_10103"
    notes["net/minecraft/class_1100$class_10103"] = ("UnbakedModel.Resolver moved to ResolvableModel.Resolver in 1.21.4 (same "
                                                     "resolve(ResourceLocation) -> UnbakedModel); UnbakedModel extends ResolvableModel, "
                                                     "whose resolveDependencies is 1.21.3's")
    covers = set(frag.get("covers", []))
    mr = frag.setdefault("methodRenames", {})
    # Variant became a record: its getters are the components
    mr["net/minecraft/class_813.method_3510()Lnet/minecraft/class_2960;"] = "comp_3379"
    mr["net/minecraft/class_813.method_3511()I"] = "comp_3382"
    # ItemTransforms became a record
    T = RT + "/CbModelTransforms"
    ifg = frag.setdefault("instanceFieldGetters", {})
    for f in ["field_4305", "field_4307", "field_4302", "field_4304", "field_4311", "field_4300", "field_4303", "field_4306"]:
        ifg["net/minecraft/class_809.%s:Lnet/minecraft/class_804;" % f] = T
    ir = frag.setdefault("instanceRedirects", {})
    ir["net/minecraft/class_809.method_3501(Lnet/minecraft/class_811;)Z"] = T
    frag.setdefault("ctorRedirects", {})["net/minecraft/class_809.<init>(Lnet/minecraft/class_809;)V"] = T + "#copy"
    # FaceBakery's instance bakeQuad / rotateVertexBy became static
    F = RT + "/CbModelFaceBakery"
    FB = "net/minecraft/class_796"
    BQ = ("method_3468(Lorg/joml/Vector3f;Lorg/joml/Vector3f;Lnet/minecraft/class_783;Lnet/minecraft/class_1058;"
          "Lnet/minecraft/class_2350;Lnet/minecraft/class_3665;Lnet/minecraft/class_789;ZI)Lnet/minecraft/class_777;")
    RV = "method_3455(Lorg/joml/Vector3f;Lnet/minecraft/class_4590;)V"
    ir[FB + "." + BQ] = F
    ir[FB + "." + RV] = F
    orr = frag.setdefault("overrideRenames", [])
    orr[:] = [r for r in orr if r.get("family") != "models"]
    for old, new in [(BQ, "cb$bakeQuad"), (RV, "cb$rotateVertexBy")]:
        orr.append({"old": old, "new": new, "onlyUnder": FB, "adapter": F + "$CbLegacyFaceBakery", "superCalls": "rename",
                    "family": "models",
                    "basis": "FaceBakery's 1.21.3 instance method is static in 1.21.4; every FaceBakery has it again as an instance method "
                             "under this name (CbModelFaceBakeryMixin), which a mod's calls reach (CbModelFaceBakery)"})
    frag["covers"] = sorted(covers)
    model_baking(frag)
    block_model(frag)


def model_baking(frag):
    """UnbakedModel.bake and ModelBaker (1.21.3 -> 1.21.4)."""
    B = RT + "/CbModelBaking"
    OLD_BAKE = "method_4753(Lnet/minecraft/class_7775;Ljava/util/function/Function;Lnet/minecraft/class_3665;)Lnet/minecraft/class_1087;"
    NEW_BAKE = ("method_4753(Lnet/minecraft/class_10419;Lnet/minecraft/class_7775;Lnet/minecraft/class_3665;ZZLnet/minecraft/class_809;)"
                "Lnet/minecraft/class_1087;")
    hooks = frag.setdefault("overrideHooks", [])
    hooks[:] = [h for h in hooks if h.get("family") != "model-baking"]
    hooks.append({
        "old": OLD_BAKE, "new": NEW_BAKE,
        "bridge": B + ".bake(L" + B + "$CbLegacyUnbakedModel;" + NEW_BAKE[len("method_4753("):].replace(")Lnet/minecraft/class_1087;", "") + ")Lnet/minecraft/class_1087;",
        "adapter": B + "$CbLegacyUnbakedModel", "onlyUnder": "net/minecraft/class_1100", "family": "model-baking",
        "basis": "UnbakedModel.bake(baker, spriteGetter, state) is bake(textureSlots, baker, state, ao, blockLight, transforms) since 1.21.4, "
                 "the sprites carried by the baker; a 1.21.3 model's own bake is reached with the baker's sprites as its sprite getter "
                 "(1.21.3's model bakery handed every model its texture getter for the model being baked)"})
    ir = frag.setdefault("instanceRedirects", {})
    ir["net/minecraft/class_1100." + OLD_BAKE] = B
    fills = frag.setdefault("abstractFills", [])
    fills[:] = [f for f in fills if f.get("family") != "model-baking"]
    fills.append({"under": "net/minecraft/class_7775", "method": "method_65732()Lnet/minecraft/class_10527;",
                  "call": B + ".sprites(Lnet/minecraft/class_7775;)Lnet/minecraft/class_10527;", "family": "model-baking",
                  "basis": "ModelBaker.sprites() is new in 1.21.4; a 1.21.3 baker resolves materials as 1.21.3 did (atlas sprite, missing texture)"})
    fills.append({"under": "net/minecraft/class_7775", "method": "method_65733()Lnet/minecraft/class_10525;",
                  "call": B + ".rootName(Lnet/minecraft/class_7775;)Lnet/minecraft/class_10525;", "family": "model-baking",
                  "basis": "ModelBaker.rootName() is new in 1.21.4, a debug name only log messages read"})
    frag.setdefault("lambdaAdapters", {})["net/minecraft/class_7775.method_45873(Lnet/minecraft/class_2960;Lnet/minecraft/class_3665;)Lnet/minecraft/class_1087;"] = {
        "iface": B + "$CbLegacyModelBaker",
        "adapt": B + "#adaptModelBaker(L" + B + "$CbLegacyModelBaker;)Lnet/minecraft/class_7775;"}



def block_model(frag):
    """1.21.3 BlockModel on 1.21.4's (CbModelBlockModel, CbModelBlockModelMixin). Descriptors in 1.21.3 names: the
    converter rewrites constructors and calls before it applies classRenames."""
    BM = "net/minecraft/class_793"
    C = RT + "/CbModelBlockModel"
    GL = "Lnet/minecraft/class_793$class_4751;"
    OLD_CTOR = BM + ".<init>(Lnet/minecraft/class_2960;Ljava/util/List;Ljava/util/Map;Ljava/lang/Boolean;" + GL + "Lnet/minecraft/class_809;Ljava/util/List;)V"
    scr = frag.setdefault("superCtorReshapes", {})
    scr[OLD_CTOR] = {
        "desc": "(Lnet/minecraft/class_2960;Ljava/util/List;Lnet/minecraft/class_10419$class_10420;Ljava/lang/Boolean;" + GL + "Lnet/minecraft/class_809;)V",
        "ops": ["keep:0", "keep:1", "conv:2 " + C + "#cbData(Ljava/util/Map;)Lnet/minecraft/class_10419$class_10420;", "keep:3", "keep:4", "keep:5", "drop:6"],
        "post": C + "#constructed(L" + BM + ";Ljava/util/Map;Ljava/util/List;)V",
        "postArgs": [2, 6],
        "basis": "BlockModel takes its texture slots as TextureSlots.Data and no item overrides since 1.21.4; the model keeps 1.21.3's very "
                 "texture map (which then answers its texture slots) and its overrides (CbModelBlockModel)"}
    SB = "net/minecraft/class_1093$class_1094"
    scr[SB + ".<init>(L" + BM + ";Z)V"] = {
        "desc": "(ZZZLnet/minecraft/class_809;)V",
        "ops": ["conv:0 " + C + "#builderAmbientOcclusion(L" + BM + ";)Z", "conv:0 " + C + "#builderBlockLight(L" + BM + ";)Z", "keep:1",
                "conv:0 " + C + "#builderTransforms(L" + BM + ";)Lnet/minecraft/class_809;"],
        "basis": "SimpleBakedModel.Builder(model, guiLight3d) read the model's hasAmbientOcclusion, getGuiLight().lightLikeBlock and "
                 "getTransforms (1.21.3 bodies); 1.21.4 takes the values"}
    ir = frag.setdefault("instanceRedirects", {})
    ir[BM + ".method_3433()Ljava/util/List;"] = C
    ir[BM + ".method_24298()" + GL] = C
    frag.setdefault("superRedirects", {})[BM + ".method_24298()" + GL] = C + "#superGetGuiLight"
    orr = frag.setdefault("overrideRenames", [])
    orr[:] = [r for r in orr if r.get("family") != "block-model"]
    orr.append({"old": "method_3433()Ljava/util/List;", "new": "cb$getElements", "onlyUnder": BM, "adapter": C + "$CbLegacyBlockModel",
                "superCalls": "rename", "family": "block-model",
                "basis": "1.21.4 declares a package-private getElements() answering the model's own elements; 1.21.3's public one fell back to "
                         "the parent's and was asked by bake, which asks it again under this name (CbModelBlockModel)"})
    ifg = frag.setdefault("instanceFieldGetters", {})
    ifg[BM + ".field_4251:Ljava/util/Map;"] = C
    ifg[BM + ".field_4253:L" + BM + ";"] = C
    frag.setdefault("instanceFieldSetters", {})[BM + ".field_4253:L" + BM + ";"] = C
    fg = frag.setdefault("fieldGetters", {})
    fg[BM + ".field_54858:Lnet/minecraft/class_4730;"] = C
    fg[BM + ".field_32792:Ljava/lang/String;"] = C
    covers = set(frag.get("covers", []))
    for m in ["method_3444()Z", "method_35789()Z", "method_3434()Ljava/util/List;",
              "method_4753(Lnet/minecraft/class_7775;Ljava/util/function/Function;Lnet/minecraft/class_3665;)Lnet/minecraft/class_1087;",
              "method_3446(Ljava/util/function/Function;Lnet/minecraft/class_3665;Z)Lnet/minecraft/class_1087;",
              "method_3432(Ljava/lang/String;)Z", "method_24077(Ljava/lang/String;)Lnet/minecraft/class_4730;",
              "method_3431()L" + BM + ";", "method_3443()Lnet/minecraft/class_809;"]:
        covers.add(BM + "." + m)
    covers.add(BM + "$class_795.method_3452(Lcom/google/gson/JsonDeserializationContext;Lcom/google/gson/JsonObject;)Ljava/util/List;")
    frag["covers"] = sorted(covers)
    cr = frag.setdefault("classRenames", {})
    cr["net/minecraft/class_10101"] = FAC + "SpecialModels"
    frag.setdefault("classRenameBasis", {})["net/minecraft/class_10101"] = (
        "SpecialModels is gone in 1.21.4: builtin/generated is the ItemModelGenerator model, builtin/entity has none (facade)")
    frag.setdefault("facadeGaps", {})["net/minecraft/class_10101.method_62665(Ljava/lang/String;Lnet/minecraft/class_793$class_4751;)Lnet/minecraft/class_1100;"] = (
        "private: SpecialModels' own factory of its two markers (an empty named BlockModel); the facade makes them in CbModelSpecial")
    model_loading(frag)


def model_loading(frag):
    """1.21.3's model loading pipeline (ModelDiscovery, BlockStateModelLoader, ModelBakery, TextureGetter, ItemModel,
    ItemModelGenerator.generateBlockModel, ModelResourceLocation.inventory) on 1.21.4's (CbModelLoading)."""
    L = RT + "/CbModelLoading"
    B = RT + "/CbModelBaking"
    MRL = "net/minecraft/class_1091"
    frag.setdefault("staticRedirects", {})[MRL + ".method_61078(Lnet/minecraft/class_2960;)L" + MRL + ";"] = L
    fg = frag.setdefault("fieldGetters", {})
    fg[MRL + ".field_52277:Ljava/lang/String;"] = L
    fg["net/minecraft/class_10097.field_54194:Ljava/lang/String;"] = L
    covers = set(frag.get("covers", []))
    covers.add("net/minecraft/class_10097.method_62632(Lnet/minecraft/class_9824$class_10095;)V")
    covers.add("net/minecraft/class_10097.method_62637()Ljava/util/Map;")
    covers.add("net/minecraft/class_9824.method_62627(Lnet/minecraft/class_2960;Lnet/minecraft/class_2689;Ljava/util/List;)Lnet/minecraft/class_9824$class_10095;")
    covers.add("net/minecraft/class_1088.method_4734()Ljava/util/Map;")
    covers.add("net/minecraft/class_801.method_3479(Ljava/util/function/Function;Lnet/minecraft/class_793;)Lnet/minecraft/class_793;")
    frag["covers"] = sorted(covers)
    scr = frag.setdefault("superCtorReshapes", {})
    scr["net/minecraft/class_9824.<init>(Lnet/minecraft/class_1100;)V"] = {
        "desc": "()V", "ops": ["drop:0"],
        "post": L + "#cbStateLoaderConstructed(Lnet/minecraft/class_9824;Lnet/minecraft/class_1100;)V", "postArgs": [0],
        "basis": "BlockStateModelLoader loads with a missing model it is handed per call since 1.21.4; the loader keeps 1.21.3's"}
    scr["net/minecraft/class_9824$class_9825.<init>(Lnet/minecraft/class_2680;Lnet/minecraft/class_1100;)V"] = {
        "desc": "(Lnet/minecraft/class_2680;Lnet/minecraft/class_9979;)V",
        "ops": ["keep:0", "conv:1 " + L + "#cbStateModel(Lnet/minecraft/class_1100;)Lnet/minecraft/class_9979;"],
        "basis": "BlockStateModelLoader.LoadedModel holds an UnbakedBlockStateModel since 1.21.4 (1.21.3: an UnbakedModel); the model is wrapped, "
                 "unwrapped again by model() (CbModelLoading)"}
    scr["net/minecraft/class_1088.<init>(Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/class_1100;)V"] = {
        "desc": "(Lnet/minecraft/class_5599;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/class_1100;)V",
        "ops": ["push " + L + "#cbEntityModels()Lnet/minecraft/class_5599;", "conv:0 " + L + "#cbStateModels(Ljava/util/Map;)Ljava/util/Map;",
                "push " + L + "#cbClientItems()Ljava/util/Map;", "keep:1", "keep:2"],
        "basis": "ModelBakery takes the entity models, block state models and client items apart since 1.21.4; 1.21.3's top models are its block "
                 "state models (the game's entity models; 1.21.3's bakery baked no item definitions)"}
    ir = frag.setdefault("instanceRedirects", {})
    ir["net/minecraft/class_9824$class_9825.comp_2871()Lnet/minecraft/class_1100;"] = L
    ir["net/minecraft/class_1088.method_45876(Lnet/minecraft/class_1088$class_9826;)V"] = L + "#bakeModels"
    TG = "net/minecraft/class_1088$class_9826"
    ir[TG + ".get(L" + MRL + ";Lnet/minecraft/class_4730;)Lnet/minecraft/class_1058;"] = L + "#get"
    orr = frag.setdefault("overrideRenames", [])
    orr[:] = [r for r in orr if r.get("family") != "model-loading"]
    orr.append({"old": "method_45876(L" + TG + ";)V", "new": "cb$bakeModels", "onlyUnder": "net/minecraft/class_1088",
                "adapter": L + "$CbLegacyBakery", "superCalls": "rename", "family": "model-loading",
                "basis": "ModelBakery.bakeModels answers a BakingResult since 1.21.4; 1.21.3's void bakeModels kept the baked top models, "
                         "every bakery has it under this name (CbModelBakeryMixin), which a mod's calls reach"})
    hooks = frag.setdefault("overrideHooks", [])
    hooks[:] = [h for h in hooks if h.get("family") != "model-loading"]
    hooks.append({
        "old": "get(L" + MRL + ";Lnet/minecraft/class_4730;)Lnet/minecraft/class_1058;",
        "new": "method_65739(Lnet/minecraft/class_10525;Lnet/minecraft/class_4730;)Lnet/minecraft/class_1058;",
        "bridge": L + ".bridgeGet(L" + L + "$CbLegacyTextureGetter;Lnet/minecraft/class_10525;Lnet/minecraft/class_4730;)Lnet/minecraft/class_1058;",
        "adapter": L + "$CbLegacyTextureGetter", "onlyUnder": TG, "family": "model-loading",
        "basis": "ModelBakery.TextureGetter.get takes a model debug name since 1.21.4 (the model location's string, read back into it)"})
    OLD_BAKE = "method_4753(Lnet/minecraft/class_7775;Ljava/util/function/Function;Lnet/minecraft/class_3665;)Lnet/minecraft/class_1087;"
    hooks.append({
        "old": OLD_BAKE, "new": "method_65542(Lnet/minecraft/class_7775;)Lnet/minecraft/class_1087;",
        "bridge": L + ".bakeState(L" + B + "$CbLegacyUnbakedModel;Lnet/minecraft/class_7775;)Lnet/minecraft/class_1087;",
        "adapter": B + "$CbLegacyUnbakedModel", "onlyUnder": "net/minecraft/class_9979", "family": "model-loading",
        "basis": "UnbakedBlockStateModel was an UnbakedModel in 1.21.3; 1.21.4 bakes it with bake(baker): its 1.21.3 bake, with the baker's "
                 "sprites and rotation X0_Y0 (1.21.3's bakery baked every top model so)"})
    fills = frag.setdefault("abstractFills", [])
    fills[:] = [f for f in fills if f.get("family") != "model-loading"]
    fills.append({"under": TG, "method": "method_65740(Lnet/minecraft/class_10525;Ljava/lang/String;)Lnet/minecraft/class_1058;",
                  "call": L + ".missingReference(L" + TG + ";Lnet/minecraft/class_10525;Ljava/lang/String;)Lnet/minecraft/class_1058;",
                  "family": "model-loading",
                  "basis": "TextureGetter.reportMissingReference is new in 1.21.4; 1.21.3 answered a missing reference with the missing material"})
    frag.setdefault("lambdaAdapters", {})[TG + ".get(L" + MRL + ";Lnet/minecraft/class_4730;)Lnet/minecraft/class_1058;"] = {
        "iface": L + "$CbLegacyTextureGetter",
        "adapt": L + "#adaptTextureGetter(L" + L + "$CbLegacyTextureGetter;)L" + TG + ";"}
    frag.setdefault("classRenames", {})["net/minecraft/class_10202"] = FAC + "ItemModel"
    frag.setdefault("classRenameBasis", {})["net/minecraft/class_10202"] = (
        "ItemModel (an item's unbaked model naming its model by id) is gone in 1.21.4; the facade is 1.21.3's class as a 1.21.4 UnbakedModel")
    final_records(frag)


def final_records(frag):
    """ItemTransforms and Variant, final records since 1.21.4, extended by a 1.21.3 mod (the layer's access widener makes them
    extendable; a record's components are its accessors, overridable as 1.21.3's getters were)."""
    T = RT + "/CbModelTransforms"
    IT = "net/minecraft/class_809"
    scr = frag.setdefault("superCtorReshapes", {})
    scr[IT + ".<init>(L" + IT + ";)V"] = {
        "desc": "(" + "Lnet/minecraft/class_804;" * 8 + ")V",
        "ops": ["conv:0 %s#%s(L%s;)Lnet/minecraft/class_804;" % (T, n, IT)
                for n in ["cbThirdLeft", "cbThirdRight", "cbFirstLeft", "cbFirstRight", "cbHead", "cbGui", "cbGround", "cbFixed"]],
        "basis": "ItemTransforms is a record since 1.21.4: a subclass's super(other) is the canonical constructor with the other's eight transforms"}
    orr = frag.setdefault("overrideRenames", [])
    orr[:] = [r for r in orr if r.get("family") != "final-records"]
    orr.append({"old": "method_3501(Lnet/minecraft/class_811;)Z", "new": "cb$hasTransform", "onlyUnder": IT,
                "adapter": T + "$CbLegacyHasTransform", "family": "final-records",
                "basis": "ItemTransforms.hasTransform is gone in 1.21.4; a subclass's answer is asked where 1.21.3 asked it (BlockModel's transforms, "
                         "a mod's calls: CbModelTransforms)"})
    frag.setdefault("superRedirects", {})[IT + ".method_3501(Lnet/minecraft/class_811;)Z"] = T + "#superHasTransform"
    V = "net/minecraft/class_813"
    for old, new in [("method_3510()Lnet/minecraft/class_2960;", "comp_3379"), ("method_3511()I", "comp_3382")]:
        orr.append({"old": old, "new": new, "onlyUnder": V, "superCalls": "rename", "family": "final-records",
                    "basis": "Variant is a record since 1.21.4: getModelLocation / getWeight are its modelLocation / weight accessors, "
                             "which 1.21.4 reads where 1.21.3 asked the getters"})



def render_dispatch(frag):
    """1.21.3's renderer dispatcher / provider context constructors on 1.21.4's (CbRenderDispatch, CbRenderDispatchMixin)."""
    D = RT + "/CbRenderDispatch"
    scr = frag.setdefault("superCtorReshapes", {})
    scr["net/minecraft/class_824.<init>(Lnet/minecraft/class_327;Lnet/minecraft/class_5599;Ljava/util/function/Supplier;Ljava/util/function/Supplier;Ljava/util/function/Supplier;)V"] = {
        "desc": "(Lnet/minecraft/class_327;Ljava/util/function/Supplier;Lnet/minecraft/class_776;Lnet/minecraft/class_10442;Lnet/minecraft/class_918;Lnet/minecraft/class_898;)V",
        "ops": ["keep:0", "conv:1 " + D + "#cbModelSupplier(Lnet/minecraft/class_5599;)Ljava/util/function/Supplier;",
                "push " + D + "#cbLaterBlockRenderer()Lnet/minecraft/class_776;", "push " + D + "#cbLaterResolver()Lnet/minecraft/class_10442;",
                "push " + D + "#cbLaterItemRenderer()Lnet/minecraft/class_918;", "push " + D + "#cbLaterEntityRenderer()Lnet/minecraft/class_898;",
                "drop:2", "drop:3", "drop:4"],
        "post": D + "#cbBlockEntityDispatcherConstructed(Lnet/minecraft/class_824;Ljava/util/function/Supplier;Ljava/util/function/Supplier;Ljava/util/function/Supplier;)V",
        "postArgs": [2, 3, 4],
        "basis": "BlockEntityRenderDispatcher takes the renderers (and the item model resolver) as values since 1.21.4; 1.21.3 took Suppliers it asked "
                 "only on reload, which a 1.21.3 dispatcher does again (CbRenderDispatchMixin); the entity models become the Supplier 1.21.4 takes"}
    scr["net/minecraft/class_5614$class_5615.<init>(Lnet/minecraft/class_824;Lnet/minecraft/class_776;Lnet/minecraft/class_918;Lnet/minecraft/class_898;Lnet/minecraft/class_5599;Lnet/minecraft/class_327;)V"] = {
        "desc": "(Lnet/minecraft/class_824;Lnet/minecraft/class_776;Lnet/minecraft/class_10442;Lnet/minecraft/class_918;Lnet/minecraft/class_898;Lnet/minecraft/class_5599;Lnet/minecraft/class_327;)V",
        "ops": ["keep:0", "keep:1", "push " + D + "#cbGameResolver()Lnet/minecraft/class_10442;", "keep:2", "keep:3", "keep:4", "keep:5"],
        "basis": "BlockEntityRendererProvider.Context also carries the item model resolver since 1.21.4: the game's"}
    scr["net/minecraft/class_5617$class_5618.<init>(Lnet/minecraft/class_898;Lnet/minecraft/class_918;Lnet/minecraft/class_330;Lnet/minecraft/class_776;Lnet/minecraft/class_3300;Lnet/minecraft/class_5599;Lnet/minecraft/class_10201;Lnet/minecraft/class_327;)V"] = {
        "desc": "(Lnet/minecraft/class_898;Lnet/minecraft/class_10442;Lnet/minecraft/class_330;Lnet/minecraft/class_776;Lnet/minecraft/class_3300;Lnet/minecraft/class_5599;Lnet/minecraft/class_10201;Lnet/minecraft/class_327;)V",
        "ops": ["keep:0", "conv:1 " + D + "#cbResolver(Lnet/minecraft/class_918;)Lnet/minecraft/class_10442;", "keep:2", "keep:3", "keep:4", "keep:5", "keep:6", "keep:7"],
        "post": D + "#cbEntityContextConstructed(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_918;)V", "postArgs": [1],
        "basis": "EntityRendererProvider.Context carries the item model resolver where 1.21.3 carried the item renderer (the game's resolver); the "
                 "context keeps the renderer for getItemRenderer()"}
    scr["net/minecraft/class_898.<init>(Lnet/minecraft/class_310;Lnet/minecraft/class_1060;Lnet/minecraft/class_918;Lnet/minecraft/class_330;Lnet/minecraft/class_776;Lnet/minecraft/class_327;Lnet/minecraft/class_315;Lnet/minecraft/class_5599;Lnet/minecraft/class_10201;)V"] = {
        "desc": "(Lnet/minecraft/class_310;Lnet/minecraft/class_1060;Lnet/minecraft/class_10442;Lnet/minecraft/class_918;Lnet/minecraft/class_330;Lnet/minecraft/class_776;Lnet/minecraft/class_327;Lnet/minecraft/class_315;Ljava/util/function/Supplier;Lnet/minecraft/class_10201;)V",
        "ops": ["keep:0", "keep:1", "push " + D + "#cbGameResolver()Lnet/minecraft/class_10442;", "keep:2", "keep:3", "keep:4", "keep:5", "keep:6",
                "conv:7 " + D + "#cbModelSupplier(Lnet/minecraft/class_5599;)Ljava/util/function/Supplier;", "keep:8"],
        "basis": "EntityRenderDispatcher also takes the item model resolver (the game's) and the entity models as a Supplier since 1.21.4"}
    scr["net/minecraft/class_5599.<init>()V"] = {
        "desc": "(Ljava/util/Map;)V", "ops": ["push " + D + "#cbEmptyModels()Ljava/util/Map;"],
        "basis": "EntityModelSet is made from its roots since 1.21.4; 1.21.3's started empty and took them on reload (CbRenderEntityModelSetMixin)"}
    C = "net/minecraft/class_5617$class_5618"
    frag.setdefault("instanceRedirects", {})[C + ".method_32168()Lnet/minecraft/class_918;"] = D
    frag.setdefault("superRedirects", {})[C + ".method_32168()Lnet/minecraft/class_918;"] = D + "#superGetItemRenderer"
    orr = frag.setdefault("overrideRenames", [])
    orr[:] = [r for r in orr if r.get("family") != "render-dispatch"]
    orr.append({"old": "method_32168()Lnet/minecraft/class_918;", "new": "cb$getItemRenderer", "onlyUnder": C,
                "adapter": D + "$CbLegacyContextItemRenderer", "family": "render-dispatch",
                "basis": "EntityRendererProvider.Context.getItemRenderer is gone in 1.21.4; a subclass's answer is what a mod's call gets (CbRenderDispatch)"})
    covers = set(frag.get("covers", []))
    covers.add("net/minecraft/class_5599.method_14491(Lnet/minecraft/class_3300;)V")
    covers.add("net/minecraft/class_824.method_23077(Lnet/minecraft/class_2586;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;II)Z")
    frag["covers"] = sorted(covers)



def render_states(frag):
    """1.21.3's item fields of the entity render states, filled by 1.21.4's extraction as 1.21.3's did (CbRenderStates, CbState*Mixin)."""
    S = RT + "/CbRenderStates"
    ifg = frag.setdefault("instanceFieldGetters", {})
    ifs = frag.setdefault("instanceFieldSetters", {})
    for k in ["net/minecraft/class_10042.field_53467:Lnet/minecraft/class_1799;", "net/minecraft/class_10025.field_53369:Lnet/minecraft/class_1799;"]:
        ifg[k] = S
        ifs[k] = S
    cr = frag.setdefault("classRenames", {})
    notes = frag.setdefault("classRenameBasis", {})
    cr["net/minecraft/class_10055$class_10056"] = FAC + "LegacyPlayerHandState"
    notes["net/minecraft/class_10055$class_10056"] = "PlayerRenderState.HandState is gone in 1.21.4; the player renderer fills the facade per hand again"
    cr["net/minecraft/class_10048"] = FAC + "OminousItemSpawnerRenderState"
    notes["net/minecraft/class_10048"] = ("OminousItemSpawnerRenderState is gone in 1.21.4 (the renderer draws an ItemClusterRenderState); the facade is one, "
                                          "with 1.21.3's fields, and the renderer makes and fills it")
    covers = set(frag.get("covers", []))
    covers.add("net/minecraft/class_10042.method_62614()Lnet/minecraft/class_1799;")
    covers.add("net/minecraft/class_10042.method_62615()Lnet/minecraft/class_1087;")
    frag["covers"] = sorted(covers)
    OS = "Lnet/minecraft/class_10048;"
    CS = "Lnet/minecraft/class_10428;"
    R = "net/minecraft/class_9483"
    hooks = frag.setdefault("overrideHooks", [])
    hooks[:] = [h for h in hooks if h.get("family") != "render-states"]
    hooks.append({"old": "method_62496()" + OS, "new": "method_62496()" + CS,
                  "unwrap": S + ".cbOminousState(L" + FAC + "OminousItemSpawnerRenderState;)" + CS, "guard": GUARD, "onlyUnder": R,
                  "family": "render-states", "basis": "OminousItemSpawnerRenderer's state is an ItemClusterRenderState since 1.21.4; 1.21.3's state (the facade) is one"})
    hooks.append({"old": "method_62497(Lnet/minecraft/class_9461;" + OS + "F)V", "new": "method_62497(Lnet/minecraft/class_9461;" + CS + "F)V",
                  "narrow": True, "guard": GUARD, "onlyUnder": R, "family": "render-states",
                  "basis": "1.21.4 extracts into its ItemClusterRenderState, which for this renderer is 1.21.3's state (CbStateOminousRendererMixin)"})
    hooks.append({"old": "method_58753(" + OS + "Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V",
                  "new": "method_58753(" + CS + "Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V",
                  "narrow": True, "guard": GUARD, "onlyUnder": R, "family": "render-states",
                  "basis": "1.21.4 renders its ItemClusterRenderState, which for this renderer is 1.21.3's state"})
    ir = frag.setdefault("instanceRedirects", {})
    ir[R + ".method_62496()" + OS] = S
    ir[R + ".method_62497(Lnet/minecraft/class_9461;" + OS + "F)V"] = S
    ir[R + ".method_58753(" + OS + "Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V"] = S



def texture_metadata(frag):
    """1.21.3's texture metadata sections (animation, texture, villager) on 1.21.4's records (CbTextureMetadata)."""
    M = RT + "/CbTextureMetadata"
    cr = frag.setdefault("classRenames", {})
    notes = frag.setdefault("classRenameBasis", {})
    for old, new in [("net/minecraft/class_1081", "AnimationMetadataSectionSerializer"), ("net/minecraft/class_1085", "TextureMetadataSectionSerializer"),
                     ("net/minecraft/class_3890", "VillagerMetadataSectionSerializer"), ("net/minecraft/class_1079$class_5792", "AnimationFrameOutput")]:
        cr[old] = FAC + new
        notes[old] = "gone in 1.21.4 (sections are records read by codecs); the facade is 1.21.3's, body for body"
    fg = frag.setdefault("fieldGetters", {})
    for k in ["net/minecraft/class_1079.field_5337:Lnet/minecraft/class_1081;", "net/minecraft/class_1079.field_32974:Ljava/lang/String;",
              "net/minecraft/class_1079.field_32975:I", "net/minecraft/class_1079.field_32976:I", "net/minecraft/class_1079.field_21768:Lnet/minecraft/class_1079;",
              "net/minecraft/class_1080.field_32973:I", "net/minecraft/class_1084.field_5344:Lnet/minecraft/class_1085;",
              "net/minecraft/class_3888.field_17158:Lnet/minecraft/class_3890;", "net/minecraft/class_3888.field_32977:Ljava/lang/String;"]:
        fg[k] = M
    scr = frag.setdefault("superCtorReshapes", {})
    scr["net/minecraft/class_1079.<init>(Ljava/util/List;IIIZ)V"] = {
        "desc": "(Ljava/util/Optional;Ljava/util/Optional;Ljava/util/Optional;IZ)V",
        "ops": ["conv:0 " + M + "#cbFrames(Ljava/util/List;)Ljava/util/Optional;", "conv:1 " + M + "#cbSize(I)Ljava/util/Optional;",
                "conv:2 " + M + "#cbSize(I)Ljava/util/Optional;", "keep:3", "keep:4"],
        "basis": "AnimationMetadataSection is a record of Optionals since 1.21.4: 1.21.3's empty frame list / UNKNOWN_SIZE (-1) are the empty Optionals"}
    scr["net/minecraft/class_1080.<init>(II)V"] = {
        "desc": "(ILjava/util/Optional;)V", "ops": ["keep:0", "conv:1 " + M + "#cbTime(I)Ljava/util/Optional;"],
        "basis": "AnimationFrame's time is an Optional since 1.21.4: 1.21.3's UNKNOWN_FRAME_TIME (-1) is the empty one"}
    mr = frag.setdefault("methodRenames", {})
    orr = frag.setdefault("overrideRenames", [])
    orr[:] = [r for r in orr if r.get("family") != "texture-metadata"]
    for owner, old, new in [("net/minecraft/class_1079", "method_4684()I", "comp_3456"), ("net/minecraft/class_1079", "method_4685()Z", "comp_3457"),
                            ("net/minecraft/class_1080", "method_4690()I", "comp_3451"), ("net/minecraft/class_1084", "method_4696()Z", "comp_3459"),
                            ("net/minecraft/class_1084", "method_4697()Z", "comp_3460"),
                            ("net/minecraft/class_3888", "method_17167()Lnet/minecraft/class_3888$class_3889;", "comp_3458")]:
        mr[owner + "." + old] = new
        orr.append({"old": old, "new": new, "onlyUnder": owner, "superCalls": "rename", "family": "texture-metadata",
                    "basis": "the section is a record since 1.21.4: the 1.21.3 getter is its component accessor, which 1.21.4 reads"})
    mr["net/minecraft/class_3888$class_3889.method_17168()Ljava/lang/String;"] = "method_15434"
    A = "net/minecraft/class_1079"
    FE = "method_33460(Lnet/minecraft/class_1079$class_5792;)V"
    frag.setdefault("instanceRedirects", {})[A + "." + FE] = M
    frag.setdefault("superRedirects", {})[A + "." + FE] = M + "#superForEachFrame"
    orr.append({"old": FE, "new": "cb$forEachFrame", "onlyUnder": A, "adapter": M + "$CbLegacyForEachFrame", "family": "texture-metadata",
                "basis": "AnimationMetadataSection.forEachFrame is gone in 1.21.4; a subclass's frames are what 1.21.4 builds the animation from "
                         "(CbTextureAnimationMixin), as 1.21.3's sprite loader asked it"})
    frag.setdefault("staticRedirects", {})["net/minecraft/class_3888$class_3889.method_17170(Ljava/lang/String;)Lnet/minecraft/class_3888$class_3889;"] = M



def textures(frag):
    """1.21.3's texture lifecycle (load on register, reset on reload, SimpleTexture.getTextureImage, PreloadedTexture) on 1.21.4's."""
    X = RT + "/CbTextures"
    TM = "net/minecraft/class_1060"
    frag.setdefault("instanceRedirects", {})[TM + ".method_4616(Lnet/minecraft/class_2960;Lnet/minecraft/class_1044;)V"] = X + "#register"
    frag.setdefault("staticRedirects", {})["net/minecraft/class_1047.method_4540()Lnet/minecraft/class_1043;"] = X
    frag.setdefault("instanceFieldGetters", {})["net/minecraft/class_1049.field_5224:Lnet/minecraft/class_2960;"] = X
    cr = frag.setdefault("classRenames", {})
    notes = frag.setdefault("classRenameBasis", {})
    cr["net/minecraft/class_4005"] = FAC + "PreloadedTexture"
    notes["net/minecraft/class_4005"] = "PreloadedTexture is gone in 1.21.4; the facade is 1.21.3's, a SimpleTexture whose contents are its getTextureImage"
    cr["net/minecraft/class_1049$class_4006"] = FAC + "LegacyTextureImage"
    notes["net/minecraft/class_1049$class_4006"] = "SimpleTexture.TextureImage is gone in 1.21.4 (TextureContents); the facade is 1.21.3's"
    ST = "net/minecraft/class_1049"
    GTI = "method_18153(Lnet/minecraft/class_3300;)Lnet/minecraft/class_1049$class_4006;"
    hooks = frag.setdefault("overrideHooks", [])
    hooks[:] = [h for h in hooks if h.get("family") != "textures"]
    hooks.append({"old": GTI, "new": "method_65809(Lnet/minecraft/class_3300;)Lnet/minecraft/class_10539;",
                  "unwrap": X + ".contents(L" + FAC + "LegacyTextureImage;)Lnet/minecraft/class_10539;", "guard": GUARD, "onlyUnder": ST,
                  "family": "textures",
                  "basis": "1.21.3's SimpleTexture loaded the image its getTextureImage answered; 1.21.4 loads a ReloadableTexture's contents, which "
                           "for a 1.21.3 subclass are that image and its metadata"})
    frag.setdefault("instanceRedirects", {})[ST + "." + GTI] = X
    frag.setdefault("superRedirects", {})[ST + "." + GTI] = X + "#method_18153"
    ah = frag.setdefault("adapterHooks", [])
    ah[:] = [h for h in ah if h.get("family") != "textures"]
    for req in ["method_4625(Lnet/minecraft/class_3300;)V",
                "method_18169(Lnet/minecraft/class_1060;Lnet/minecraft/class_3300;Lnet/minecraft/class_2960;Ljava/util/concurrent/Executor;)V"]:
        ah.append({"under": "net/minecraft/class_1044", "adapter": X + "$CbLegacyTexture", "requires": [req], "synth": [], "family": "textures",
                   "basis": "a 1.21.3 texture class that loads or resets itself: reset on reload as 1.21.3 reset every texture (CbTextureManagerMixin)"})
    covers = set(frag.get("covers", []))
    for o in ["net/minecraft/class_1044", "net/minecraft/class_1059", "net/minecraft/class_1043", ST, "net/minecraft/class_380"]:
        covers.add(o + ".method_4625(Lnet/minecraft/class_3300;)V")
    covers.add("net/minecraft/class_1044.method_18169(Lnet/minecraft/class_1060;Lnet/minecraft/class_3300;Lnet/minecraft/class_2960;Ljava/util/concurrent/Executor;)V")
    covers.add("net/minecraft/class_1044.method_62619()Z")
    covers.add(TM + ".method_34590(Lnet/minecraft/class_2960;Lnet/minecraft/class_1044;)Lnet/minecraft/class_1044;")
    covers.add(TM + ".method_4617(Ljava/lang/String;Lnet/minecraft/class_1043;)Lnet/minecraft/class_2960;")
    covers.add(TM + ".method_18168(Lnet/minecraft/class_2960;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;")
    covers.add("net/minecraft/class_1059.method_24198(Lnet/minecraft/class_7766$class_7767;)V")
    frag["covers"] = sorted(covers)
    cr["net/minecraft/class_1046"] = FAC + "HttpTexture"
    notes["net/minecraft/class_1046"] = ("HttpTexture is gone in 1.21.4 (skins download through SkinTextureDownloader); the facade is 1.21.3's, "
                                         "its legacy-skin processing 1.21.4's same code")
    frag.setdefault("superCtorReshapes", {})["net/minecraft/class_1071.<init>(Lnet/minecraft/class_1060;Ljava/nio/file/Path;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Ljava/util/concurrent/Executor;)V"] = {
        "desc": "(Ljava/nio/file/Path;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Ljava/util/concurrent/Executor;)V",
        "ops": ["drop:0", "keep:1", "keep:2", "keep:3"],
        "basis": "SkinManager registers downloaded skins with the game's texture manager since 1.21.4 (SkinTextureDownloader); 1.21.3 took it as an "
                 "argument, the game's in every vanilla use"}



def equipment(frag):
    """1.21.3's client equipment API keyed by model id on 1.21.4's, keyed by EquipmentAsset keys (s9rest's CbRestEquipment converts)."""
    R = RT + "/CbRestEquipment"
    TO_KEY = "conv " + R + "#asset(Lnet/minecraft/class_2960;)Lnet/minecraft/class_5321;"
    cr = frag.setdefault("callReshapes", {})
    hooks = frag.setdefault("overrideHooks", [])
    hooks[:] = [h for h in hooks if h.get("family") != "equipment"]
    LT = "Lnet/minecraft/class_10186$class_10190;"
    REST = "Lnet/minecraft/class_3879;Lnet/minecraft/class_1799;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I"
    shapes = [("net/minecraft/class_10201", "method_64087", "(Lnet/minecraft/class_2960;)Lnet/minecraft/class_10186;",
               "(Lnet/minecraft/class_5321;)Lnet/minecraft/class_10186;", [TO_KEY],
               "EquipmentAssetManager.get takes the asset's key since 1.21.4 (1.21.3: its model id, the key's location)"),
              ("net/minecraft/class_10197", "method_64077", "(" + LT + "Lnet/minecraft/class_2960;" + REST + ")V",
               "(" + LT + "Lnet/minecraft/class_5321;" + REST + ")V", ["keep", TO_KEY, "keep", "keep", "keep", "keep", "keep"],
               "EquipmentLayerRenderer.renderLayers takes the equipment asset's key since 1.21.4 (1.21.3: its model id)"),
              ("net/minecraft/class_10197", "method_64078", "(" + LT + "Lnet/minecraft/class_2960;" + REST + "Lnet/minecraft/class_2960;)V",
               "(" + LT + "Lnet/minecraft/class_5321;" + REST + "Lnet/minecraft/class_2960;)V",
               ["keep", TO_KEY, "keep", "keep", "keep", "keep", "keep", "keep"],
               "EquipmentLayerRenderer.renderLayers takes the equipment asset's key since 1.21.4 (1.21.3: its model id)")]
    for owner, name, old, new, ops, basis in shapes:
        cr[owner + "." + name + old] = {"desc": new, "ops": ops, "basis": basis}
        hooks.append({"old": name + old, "new": name + new, "wrap": R + ".id(Lnet/minecraft/class_5321;)Lnet/minecraft/class_2960;",
                      "guard": GUARD, "onlyUnder": owner, "family": "equipment", "basis": basis + "; a 1.21.3 override is reached with the key's location"})
    covers = set(frag.get("covers", []))
    covers.add("net/minecraft/class_310.method_64036()Lnet/minecraft/class_10201;")
    frag["covers"] = sorted(covers)



def block_entity_renders(frag):
    """1.21.3 block entity renderer members on 1.21.4 (signs, hanging signs, banner, skulls, vault, item clusters): CbBlockEntityRenders."""
    B = RT + "/CbBlockEntityRenders"
    sr = frag.setdefault("staticRedirects", {})
    for k in ["net/minecraft/class_7761.method_45795()Lnet/minecraft/class_5607;",
              "net/minecraft/class_823.method_32135()Lnet/minecraft/class_5607;",
              "net/minecraft/class_836.method_32160(Lnet/minecraft/class_5599;)Ljava/util/Map;",
              "net/minecraft/class_9214.method_56851(FLnet/minecraft/class_1937;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1799;Lnet/minecraft/class_918;FFLnet/minecraft/class_5819;)V",
              "net/minecraft/class_916.method_56856(Lnet/minecraft/class_1799;)I",
              "net/minecraft/class_916.method_56857(Lnet/minecraft/class_918;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1799;Lnet/minecraft/class_5819;Lnet/minecraft/class_1937;)V",
              "net/minecraft/class_916.method_56858(Lnet/minecraft/class_918;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1799;Lnet/minecraft/class_1087;ZLnet/minecraft/class_5819;)V"]:
        sr[k] = B
    frag.setdefault("fieldGetters", {})["net/minecraft/class_823.field_32816:Ljava/lang/String;"] = B
    mr = frag.setdefault("methodRenames", {})
    mr["net/minecraft/class_837.method_37311(Lnet/minecraft/class_8242;)I"] = "method_65822"
    mr["net/minecraft/class_836.method_3578(Lnet/minecraft/class_2484$class_2485;Lnet/minecraft/class_9296;)Lnet/minecraft/class_1921;"] = "method_65832"
    RENDER = "method_23083(Lnet/minecraft/class_2625;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;II)V"
    mr["net/minecraft/class_837." + RENDER] = "method_65820"
    mr["net/minecraft/class_7761." + RENDER] = "method_65820"
    orr = frag.setdefault("overrideRenames", [])
    orr[:] = [r for r in orr if r.get("family") != "block-entity-renders"]
    orr.append({"old": RENDER, "new": "method_65820", "onlyUnder": "net/minecraft/class_837", "superCalls": "rename", "family": "block-entity-renders",
                "basis": "SignRenderer / HangingSignRenderer.render(sign, ..) is AbstractSignRenderer.render(sign, ..) since 1.21.4 (the bridge render "
                         "calls it)"})
    cr = frag.setdefault("classRenames", {})
    cr["net/minecraft/class_7761$class_7762"] = FAC + "LegacyHangingSignModel"
    frag.setdefault("classRenameBasis", {})["net/minecraft/class_7761$class_7762"] = (
        "HangingSignRenderer.HangingSignModel is gone in 1.21.4 (a model per attachment); the facade is 1.21.3's, over its one layer")


def render_tail(frag):
    """1.21.3 rendering members 1.21.4 reshaped (sky, weather, particles, screen effects, level render, input, music, NativeImage,
    renderer and particle constructors, renderer scale overrides): CbRenderTail."""
    T = RT + "/CbRenderTail"
    G = RT + "/CbGuiMisc"
    ir = frag.setdefault("instanceRedirects", {})
    for k in ["net/minecraft/class_9975.method_62306(Lnet/minecraft/class_4587;Lnet/minecraft/class_289;FI)V",
              "net/minecraft/class_9975.method_62307(Lnet/minecraft/class_4587;Lnet/minecraft/class_289;FIFFLnet/minecraft/class_9958;)V",
              "net/minecraft/class_9975.method_62312(Lnet/minecraft/class_4587;)V",
              "net/minecraft/class_9976.method_62316(Lnet/minecraft/class_1937;Lnet/minecraft/class_765;IFLnet/minecraft/class_243;)V",
              "net/minecraft/class_702.method_3049(Lnet/minecraft/class_765;Lnet/minecraft/class_4184;F)V",
              "net/minecraft/class_1011.method_22619(IIIIIIIZZZZ)V", "net/minecraft/class_1011.method_4312(IIIIIIIZZ)V",
              "net/minecraft/class_744.method_3129(ZF)V",
              "net/minecraft/class_751.method_18143(Lnet/minecraft/class_1060;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
              "net/minecraft/class_945.method_4117(Lnet/minecraft/class_10067;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V",
              "net/minecraft/class_917.method_48302(Lnet/minecraft/class_10067;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V"]:
        ir[k] = T
    frag.setdefault("superRedirects", {})["net/minecraft/class_744.method_3129(ZF)V"] = T + "#superTick"
    sr = frag.setdefault("staticRedirects", {})
    sr["net/minecraft/class_4603.method_23067(Lnet/minecraft/class_310;Lnet/minecraft/class_4587;)V"] = T
    sr["com/mojang/blaze3d/systems/RenderSystem.overlayBlendFunc()V"] = T
    sr["net/minecraft/class_5602.method_45719(Lnet/minecraft/class_4719;)Lnet/minecraft/class_5601;"] = T
    frag.setdefault("instanceFieldGetters", {})["net/minecraft/class_922.field_53193:Lnet/minecraft/class_918;"] = T
    cr = frag.setdefault("callReshapes", {})
    LR = "(Lnet/minecraft/class_9922;Lnet/minecraft/class_9779;ZLnet/minecraft/class_4184;Lnet/minecraft/class_757;"
    LR_OLD = LR + "Lnet/minecraft/class_765;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"
    LR_NEW = LR + "Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"
    cr["net/minecraft/class_761.method_22710" + LR_OLD] = {
        "desc": LR_NEW, "ops": ["keep", "keep", "keep", "keep", "keep", "drop", "keep", "keep"],
        "basis": "LevelRenderer.renderLevel no longer takes the LightTexture since 1.21.4 (its render types carry the lightmap)"}
    cr["net/minecraft/class_1142.method_4858(Lnet/minecraft/class_5195;)V"] = {
        "desc": "(Lnet/minecraft/class_10383;)V", "ops": ["conv " + G + "#cbMusicInfo(Lnet/minecraft/class_5195;)Lnet/minecraft/class_10383;"],
        "basis": "MusicManager.startPlaying takes a MusicInfo since 1.21.4 (the music at full volume, as 1.21.3 played it)"}
    cr["net/minecraft/class_909.method_62461(Lnet/minecraft/class_1309;Lnet/minecraft/class_10034;F)V"] = {
        "desc": "(Lnet/minecraft/class_1309;Lnet/minecraft/class_10034;FLnet/minecraft/class_10442;)V",
        "ops": ["keep", "keep", "keep", "push " + T + "#cbResolver()Lnet/minecraft/class_10442;"],
        "basis": "HumanoidMobRenderer.extractHumanoidRenderState also resolves the held items since 1.21.4: with the game's item model resolver"}
    for m in ["method_62079", "method_62080"]:
        cr["net/minecraft/class_9945." + m + "()Lnet/minecraft/class_5607;"] = {
            "desc": "(F)Lnet/minecraft/class_5607;", "ops": ["push " + T + "#cbUnitScale()F"],
            "basis": "DonkeyModel's layer takes a scale since 1.21.4; 1.21.3's was unscaled (the renderer scaled), scale 1"}
    mr = frag.setdefault("methodRenames", {})
    mr["net/minecraft/class_761.method_52815(Lnet/minecraft/class_1923;)V"] = "method_65201"
    orr = frag.setdefault("overrideRenames", [])
    orr[:] = [r for r in orr if r.get("family") != "render-tail"]
    orr.append({"old": "method_52815(Lnet/minecraft/class_1923;)V", "new": "method_65201", "onlyUnder": "net/minecraft/class_761", "superCalls": "rename",
                "family": "render-tail", "basis": "LevelRenderer.onChunkLoaded is onChunkReadyToRender since 1.21.4 (same body)"})
    hooks = frag.setdefault("overrideHooks", [])
    hooks[:] = [h for h in hooks if h.get("family") != "render-tail"]
    BS = "Lnet/minecraft/class_4597$class_4598;"
    TES = T + ".tesselator(" + BS + ")Lnet/minecraft/class_289;"
    LT = T + ".lightTexture(Lnet/minecraft/class_4597;)Lnet/minecraft/class_765;"
    hooks.append({"old": "method_62306(Lnet/minecraft/class_4587;Lnet/minecraft/class_289;FI)V", "new": "method_62306(Lnet/minecraft/class_4587;" + BS + "FI)V",
                  "wrap": TES, "guard": GUARD, "onlyUnder": "net/minecraft/class_9975", "family": "render-tail",
                  "basis": "SkyRenderer draws into the frame's buffer source since 1.21.4; a 1.21.3 override draws with the Tesselator 1.21.3 handed it"})
    hooks.append({"old": "method_62307(Lnet/minecraft/class_4587;Lnet/minecraft/class_289;FIFFLnet/minecraft/class_9958;)V",
                  "new": "method_62307(Lnet/minecraft/class_4587;" + BS + "FIFFLnet/minecraft/class_9958;)V",
                  "wrap": TES, "guard": GUARD, "onlyUnder": "net/minecraft/class_9975", "family": "render-tail",
                  "basis": "SkyRenderer draws into the frame's buffer source since 1.21.4; a 1.21.3 override draws with the Tesselator 1.21.3 handed it"})
    hooks.append({"old": "method_62312(Lnet/minecraft/class_4587;)V", "new": "method_62312()V",
                  "bridge": T + ".endSky(L" + T + "$CbLegacyEndSky;)V", "adapter": T + "$CbLegacyEndSky", "onlyUnder": "net/minecraft/class_9975",
                  "family": "render-tail", "basis": "SkyRenderer.renderEndSky takes no pose since 1.21.4; 1.21.3's sky pass handed a fresh (identity) one"})
    hooks.append({"old": "method_62316(Lnet/minecraft/class_1937;Lnet/minecraft/class_765;IFLnet/minecraft/class_243;)V",
                  "new": "method_62316(Lnet/minecraft/class_1937;Lnet/minecraft/class_4597;IFLnet/minecraft/class_243;)V",
                  "wrap": LT, "guard": GUARD, "onlyUnder": "net/minecraft/class_9976", "family": "render-tail",
                  "basis": "WeatherEffectRenderer.render takes the buffer source in the LightTexture's place since 1.21.4; a 1.21.3 override gets the game's LightTexture"})
    hooks.append({"old": "method_3049(Lnet/minecraft/class_765;Lnet/minecraft/class_4184;F)V", "new": "method_3049(Lnet/minecraft/class_4184;F" + BS + ")V",
                  "args": [2, 0, 1], "wrap": LT, "guard": GUARD, "onlyUnder": "net/minecraft/class_702", "family": "render-tail",
                  "basis": "ParticleEngine.render takes the camera, partial tick and buffer source since 1.21.4; a 1.21.3 override gets the game's LightTexture"})
    hooks.append({"old": "method_22710" + LR_OLD, "new": "method_22710" + LR_NEW,
                  "bridge": T + ".renderLevel(L" + T + "$CbLegacyRenderLevel;" + LR_NEW[1:],
                  "adapter": T + "$CbLegacyRenderLevel", "onlyUnder": "net/minecraft/class_761", "family": "render-tail",
                  "basis": "LevelRenderer.renderLevel no longer takes the LightTexture; a 1.21.3 override gets the game's, as 1.21.3's game renderer passed it"})
    hooks.append({"old": "method_3129(ZF)V", "new": "method_3129()V", "bridge": T + ".tick(L" + T + "$CbLegacyInputTick;)V",
                  "adapter": T + "$CbLegacyInputTick", "onlyUnder": "net/minecraft/class_744", "family": "render-tail",
                  "basis": "ClientInput.tick takes no sneaking slowdown since 1.21.4 (the player applies it after the input's tick); a 1.21.3 input is "
                           "ticked without it, so it applies once"})
    hooks.append({"old": "method_4858(Lnet/minecraft/class_5195;)V", "new": "method_4858(Lnet/minecraft/class_10383;)V",
                  "wrap": G + ".cbMusic(Lnet/minecraft/class_10383;)Lnet/minecraft/class_5195;", "guard": GUARD,
                  "onlyUnder": "net/minecraft/class_1142", "family": "render-tail",
                  "basis": "MusicManager.startPlaying takes a MusicInfo since 1.21.4; a 1.21.3 override gets its music"})
    for owner, old in [("net/minecraft/class_875", "method_3874(Lnet/minecraft/class_10019;Lnet/minecraft/class_4587;)V"),
                       ("net/minecraft/class_963", "method_4149(Lnet/minecraft/class_10080;Lnet/minecraft/class_4587;)V"),
                       ("net/minecraft/class_4999", "method_56155(Lnet/minecraft/class_10070;Lnet/minecraft/class_4587;)V")]:
        hooks.append({"old": old, "new": "method_4042(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;)V", "narrow": True,
                      "frames": GUARD, "superCalls": "rewrite", "onlyUnder": owner, "family": "render-tail",
                      "basis": "the renderer's typed scale(state, pose) is gone in 1.21.4: LivingEntityRenderer.scale is what vanilla asks, and a 1.21.3 "
                               "override's super call reaches it (1.21.4 scales babies and horse sizes in the model layers)"})
    for owner, old in [("net/minecraft/class_945", "method_4117"), ("net/minecraft/class_917", "method_48302")]:
        hooks.append({"old": old + "(Lnet/minecraft/class_10067;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V",
                      "new": "method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V", "narrow": True,
                      "frames": GUARD, "superCalls": "rewrite", "onlyUnder": owner, "family": "render-tail",
                      "basis": "the renderer's typed render(state, ..) is gone in 1.21.4 (its shadow is asked from the state): LivingEntityRenderer.render "
                               "is what vanilla calls and what a 1.21.3 override's super call reaches"})
    scr = frag.setdefault("superCtorReshapes", {})
    scr["net/minecraft/class_647.<init>(Lnet/minecraft/class_638;DDDLnet/minecraft/class_1799;)V"] = {
        "desc": "(Lnet/minecraft/class_638;DDDLnet/minecraft/class_10444;)V",
        "ops": ["keep:0", "keep:1", "keep:2", "keep:3", "conv:4 " + T + "#cbEmptyState(Lnet/minecraft/class_1799;)Lnet/minecraft/class_10444;"],
        "post": T + "#cbBreakingSprite(Lnet/minecraft/class_647;Lnet/minecraft/class_638;Lnet/minecraft/class_1799;)V", "postArgs": [0, 4],
        "basis": "BreakingItemParticle takes the item's render state since 1.21.4; the sprite is 1.21.3's, the particle icon of the stack's model"}
    scr["net/minecraft/class_8253.<init>(Lnet/minecraft/class_638;DDDLnet/minecraft/class_4002;)V"] = {
        "desc": "(Lnet/minecraft/class_638;DDDLnet/minecraft/class_4002;FFZZFF)V",
        "ops": ["keep:0", "keep:1", "keep:2", "keep:3", "keep:4", "push " + T + "#cbLeafGravity()F", "push " + T + "#cbLeafWind()F",
                "push " + T + "#cbLeafSwirl()Z", "push " + T + "#cbLeafFlowAway()Z", "push " + T + "#cbLeafSize()F", "push " + T + "#cbLeafYSpeed()F"],
        "basis": "FallingLeavesParticle takes its motion as parameters since 1.21.4; 1.21.3's was the cherry leaf, whose values 1.21.4's CherryProvider passes"}
    scr["net/minecraft/class_693.<init>(Lnet/minecraft/class_898;Lnet/minecraft/class_4599;Lnet/minecraft/class_638;Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;)V"] = {
        "desc": "(Lnet/minecraft/class_898;Lnet/minecraft/class_638;Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;)V",
        "ops": ["keep:0", "drop:1", "keep:2", "keep:3", "keep:4"],
        "basis": "ItemPickupParticle draws into the buffer source its render call hands it since 1.21.4 (1.21.3 kept the RenderBuffers to get it)"}
    scr["net/minecraft/class_776.<init>(Lnet/minecraft/class_773;Lnet/minecraft/class_756;Lnet/minecraft/class_324;)V"] = {
        "desc": "(Lnet/minecraft/class_773;Ljava/util/function/Supplier;Lnet/minecraft/class_324;)V",
        "ops": ["keep:0", "conv:1 " + T + "#cbSpecialRenderer(Lnet/minecraft/class_756;)Ljava/util/function/Supplier;", "keep:2"],
        "basis": "BlockRenderDispatcher draws special blocks with the SpecialBlockModelRenderer since 1.21.4 (1.21.3: the BlockEntityWithoutLevelRenderer); the game's"}
    scr["net/minecraft/class_875.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_583;Lnet/minecraft/class_583;F)V"] = {
        "desc": "(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_583;Lnet/minecraft/class_583;)V",
        "ops": ["keep:0", "keep:1", "keep:2", "drop:3"],
        "post": T + "#cbHorseScale(Lnet/minecraft/class_875;F)V", "postArgs": [3],
        "basis": "AbstractHorseRenderer takes no scale since 1.21.4 (vanilla scales the layers); the renderer applies 1.21.3's (CbTailHorseScaleMixin)"}
    scr["net/minecraft/class_9991.<init>(Lnet/minecraft/class_5617$class_5618;FLnet/minecraft/class_5601;Lnet/minecraft/class_5601;Z)V"] = {
        "desc": "(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;Z)V",
        "ops": ["keep:0", "drop:1", "keep:2", "keep:3", "keep:4"],
        "post": T + "#cbDonkeyScale(Lnet/minecraft/class_9991;FLnet/minecraft/class_5601;)V", "postArgs": [1, 2],
        "basis": "DonkeyRenderer takes no scale since 1.21.4; 1.21.3's applies unless the layer is a vanilla donkey / mule layer, which 1.21.4 scales itself"}
    scr["net/minecraft/class_7288.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_2960;Lnet/minecraft/class_7288$class_7289;Lnet/minecraft/class_7288$class_7311;Ljava/util/function/Function;)V"] = {
        "desc": "(Lnet/minecraft/class_3883;Lnet/minecraft/class_2960;Lnet/minecraft/class_7288$class_7289;Lnet/minecraft/class_7288$class_7311;Ljava/util/function/Function;Z)V",
        "ops": ["keep:0", "keep:1", "keep:2", "keep:3", "keep:4", "push " + T + "#cbNotAlwaysVisible()Z"],
        "basis": "LivingEntityEmissiveLayer takes whether it shows on an invisible entity since 1.21.4; 1.21.3's never did"}


if __name__ == "__main__":
    main()
