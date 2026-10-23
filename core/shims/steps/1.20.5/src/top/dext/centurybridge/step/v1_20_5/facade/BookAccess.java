package top.dext.centurybridge.step.v1_20_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's BookViewScreen.BookAccess (classRenames): the interface a book screen reads its pages through,
 * which mods implement. 1.20.5's BookAccess is a record over a page list; a 1.20.4 access reaches 1.20.5's
 * screen as such a record over a live view of its pages (rt/CbClientBooks).
 */
@Environment(EnvType.CLIENT)
public interface BookAccess {

    /** getPageCount() */
    int method_17560();

    /** getPageRaw(index) */
    net.minecraft.class_5348 method_17561(int index);

    /** getPage(index): the raw page within range, FormattedText.EMPTY outside */
    default net.minecraft.class_5348 method_17563(int index) {
        return index >= 0 && index < this.method_17560() ? this.method_17561(index) : net.minecraft.class_5348.field_25310;
    }

    /** fromItem(stack): a written book's or a writable book's pages, else the empty access */
    static BookAccess method_17562(net.minecraft.class_1799 stack) {
        if (stack.method_31574(net.minecraft.class_1802.field_8360)) {
            return new WrittenBookAccess(stack);
        }
        if (stack.method_31574(net.minecraft.class_1802.field_8674)) {
            return new WritableBookAccess(stack);
        }
        return top.dext.centurybridge.step.v1_20_5.rt.CbClientBooks.EMPTY_ACCESS;
    }
}
