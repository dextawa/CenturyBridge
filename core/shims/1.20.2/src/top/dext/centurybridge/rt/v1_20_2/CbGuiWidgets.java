package top.dext.centurybridge.rt.v1_20_2;

/** Small 1.20.1 -> 1.20.2 GUI conversions the step's wiring names (see each member). */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiWidgets {

    /**
     * callReshapes conv: 1.20.1 AbstractWidget.renderScrollingString(graphics, font, text, minX, minY, maxX,
     * maxY, color) centred an unscrolled label at (minX + maxX) / 2; 1.20.2's takes that centre as an
     * argument and clamps it into [minX + w/2, maxX - w/2], which holds it for any label that fits -- the
     * only labels it centres.
     */
    public static int cbCentre(int minX, int maxX) {
        return (minX + maxX) / 2;
    }

    /**
     * callReshapes ret conv: 1.20.1 ChatComponent.getRecentChat() returned the List of sent messages;
     * 1.20.2's getMessageHistory() returns the same history, oldest first, as an ArrayListDeque -- which is
     * that List.
     */
    public static java.util.List<String> cbHistoryList(net.minecraft.class_8623<String> history) {
        return history;
    }

    /**
     * overrideHooks unwrap: a 1.20.1 ChatComponent subclass's getRecentChat() override returns a List;
     * 1.20.2's chat screen asks the ArrayListDeque shape, and only reads it (size, get). The list vanilla
     * handed back through super passes as it is; any other list is read into a deque in the same order.
     */
    public static net.minecraft.class_8623<String> cbChatHistory(java.util.List<String> recent) {
        if (recent instanceof net.minecraft.class_8623<String> deque) {
            return deque;
        }
        net.minecraft.class_8623<String> out = new net.minecraft.class_8623<>(recent.size());
        out.addAll(recent);
        return out;
    }

    /**
     * instanceFieldGetters: 1.20.1 GuiMessageTag.Icon.u / v, the icon's cell in chat_tags.png (which
     * CbGuiLegacyTextures rebuilds); 1.20.2 keeps a sprite instead. The values of 1.20.1's one constant,
     * CHAT_MODIFIED(0, 0, 9, 9) -- 1.20.2 has no other.
     */
    public static int field_39764(net.minecraft.class_7591.class_7592 icon) {
        return cbIconCell(icon)[0];
    }

    public static int field_39765(net.minecraft.class_7591.class_7592 icon) {
        return cbIconCell(icon)[1];
    }

    private static int[] cbIconCell(net.minecraft.class_7591.class_7592 icon) {
        if (icon.name().equals("CHAT_MODIFIED")) {
            return new int[] {0, 0};
        }
        throw new IllegalStateException("GuiMessageTag.Icon." + icon.name() + " has no 1.20.1 chat_tags.png cell");
    }

    private CbGuiWidgets() {
    }
}
