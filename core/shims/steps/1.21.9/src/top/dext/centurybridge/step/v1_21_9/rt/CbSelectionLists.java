package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import net.minecraft.class_350;

/**
 * AbstractSelectionList, 1.21.8 -> 1.21.9 (javap of both code jars). 1.21.9 gives each entry its own row
 * (Entry.x / y / width / height, set by repositionEntries) and Entry.render(graphics, index, top, left, width, height,
 * mouseX, mouseY, hovered, delta) became renderContent(graphics, mouseX, mouseY, hovered, delta). 1.21.8's renderItem
 * passed index = the entry's index in children(), top = getRowTop(index), left = getRowLeft(), width = getRowWidth(),
 * height = itemHeight - 4; on 1.21.9 those are the entry's getContentY(), getContentX(), getWidth() and
 * getContentHeight() (repositionEntries: x = getRowLeft(), which lost 1.21.8's "+ 2" that getContentX adds back;
 * y = the running row top; width = getRowWidth(); height = defaultEntryHeight, 1.21.8's itemHeight).
 */
@Environment(EnvType.CLIENT)
public final class CbSelectionLists {

    private CbSelectionLists() {
    }

    /** the restored 1.21.8 header state of a list (CbSelectionListRestoredMixin) */
    public interface LegacyHeader {
        boolean cb$hasHeader();

        int cb$headerHeight();

        void cb$header(int headerHeight);

        /** renderHeader(graphics, x, y), called virtually (a 1.21.8 subclass override answers) */
        void cb$renderHeader(class_332 graphics, int x, int y);

        /** renderDecorations(graphics, mouseX, mouseY), called virtually */
        void cb$renderDecorations(class_332 graphics, int mouseX, int mouseY);

        /** isSelectedItem(index), called virtually */
        boolean cb$isSelectedItem(int index);
    }

    /** the restored AbstractSelectionList.isSelectedItem(int), as the layer declares it (CbMixedMembers key) */
    public static final String IS_SELECTED_ITEM = "net/minecraft/class_350.method_25332(I)Z";

    /** a 1.21.8 entry class declaring renderBack(graphics, index, top, left, width, height, mouseX, mouseY, hovered, delta) */
    public interface LegacyRenderBack {
        void method_49568(class_332 graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY,
                          boolean hovered, float delta);
    }

    /** a 1.21.8 list class declaring isSelectedItem(index) (the adapter hook adds it and makes that method public) */
    public interface LegacyIsSelectedItem {
        boolean method_25332(int index);
    }

    /**
     * after 1.21.9's five-argument constructor, for a 1.21.8 list built with the six-argument one
     * (minecraft, width, height, y, itemHeight, headerHeight): 1.21.8's body then set renderHeader and headerHeight
     */
    public static void cbHeader(class_350<?> list, int headerHeight) {
        ((LegacyHeader) list).cb$header(headerHeight);
    }

    /** ObjectSelectionList's six-argument constructor (it passed the header height on) */
    public static void cbHeader(net.minecraft.class_4280<?> list, int headerHeight) {
        ((LegacyHeader) list).cb$header(headerHeight);
    }

    /** ContainerObjectSelectionList's six-argument constructor (it passed the header height on) */
    public static void cbHeader(net.minecraft.class_4265<?> list, int headerHeight) {
        ((LegacyHeader) list).cb$header(headerHeight);
    }

    /** a 1.21.8 list class declaring renderItem(graphics, mouseX, mouseY, delta, index, left, top, width, height) */
    public interface LegacyRenderItem {
        void method_44397(class_332 graphics, int mouseX, int mouseY, float delta, int index, int left, int top, int width, int height);
    }

    /** 1.21.9's renderItem(graphics, mouseX, mouseY, delta, entry) answered by a 1.21.8 override: the entry's index and row */
    public static void cbRenderItem(LegacyRenderItem self, class_332 graphics, int mouseX, int mouseY, float delta, class_350.class_351<?> entry) {
        int index = ((class_350<?>) self).method_25396().indexOf(entry);
        self.method_44397(graphics, mouseX, mouseY, delta, index, entry.method_73380(), entry.method_73382(), entry.method_25368(),
            entry.method_73384());
    }

    /** a 1.21.8 list class declaring renderSelection(graphics, top, width, height, outerColor, innerColor) */
    public interface LegacyRenderSelection {
        void method_44398(class_332 graphics, int top, int width, int height, int outerColor, int innerColor);
    }

    /**
     * 1.21.9's renderSelection(graphics, entry, outerColor) answered by a 1.21.8 override: the entry's content top, its
     * width and content height, the outer colour 1.21.9 passes and 1.21.8's inner colour (opaque black, the constant
     * both releases fill the inside with)
     */
    public static void cbRenderSelection(LegacyRenderSelection self, class_332 graphics, class_350.class_351<?> entry, int outerColor) {
        self.method_44398(graphics, entry.method_73382(), entry.method_25368(), entry.method_73384(), outerColor, -16777216);
    }

    /** a 1.21.8 entry class declaring render(graphics, index, top, left, width, height, mouseX, mouseY, hovered, delta) */
    public interface LegacyEntryRender {
        void method_25343(class_332 graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY,
                          boolean hovered, float delta);
    }

    /** 1.21.9's renderContent answered by a 1.21.8 render override, with the row 1.21.8's list passed */
    public static void cbRender(LegacyEntryRender self, class_332 graphics, int mouseX, int mouseY, boolean hovered, float delta) {
        class_350.class_351<?> entry = (class_350.class_351<?>) self;
        class_350<?> list = entry.field_22752;
        int index = list == null ? -1 : list.method_25396().indexOf(entry);
        self.method_25343(graphics, index, entry.method_73382(), entry.method_73380(), entry.method_25368(), entry.method_73384(),
            mouseX, mouseY, hovered, delta);
    }
}
