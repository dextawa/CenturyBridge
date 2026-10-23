package top.dext.centurybridge.rt.v1_20_2;

/** The client side of CbAdvNodes: the advancement screen's tabs. */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbAdvNodesClient {

    /** AdvancementTab.getAdvancement(): the root advancement of the tab (1.20.2 keeps its node) */
    public static net.minecraft.class_161 method_2307(net.minecraft.class_454 self) {
        return CbAdvNodes.valueOf(self.method_53813());
    }

    private CbAdvNodesClient() {
    }
}
