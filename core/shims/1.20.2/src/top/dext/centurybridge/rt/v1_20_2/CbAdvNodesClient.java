package top.dext.centurybridge.rt.v1_20_2;

/** The client side of CbAdvNodes: the advancement screen's tabs. */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbAdvNodesClient {

    /** AdvancementTab.getAdvancement(): the root advancement of the tab (1.20.2 keeps its node) */
    public static net.minecraft.class_161 method_2307(net.minecraft.class_454 self) {
        return CbAdvNodes.valueOf(self.method_53813());
    }

    /**
     * AdvancementsScreen.TABS_LOCATION, 1.20.1's tab sheet textures/gui/advancements/tabs.png: 1.20.2 draws
     * the tabs from sprites; the layer rebuilds the 1.20.1 sheet from them whenever it is loaded
     * (CbGuiLegacyTextures, every visible 1.20.1 pixel reproduced), so the id draws what it drew in 1.20.1
     */
    public static final net.minecraft.class_2960 field_2716 = new net.minecraft.class_2960("textures/gui/advancements/tabs.png");

    private CbAdvNodesClient() {
    }
}
