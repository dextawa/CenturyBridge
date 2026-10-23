package top.dext.centurybridge.step.v1_20_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's BookViewScreen.WrittenBookAccess (classRenames): a written book's pages, each a text component's
 * JSON (plain text when it does not parse), read from the stack's 1.20.4 tag (rt/CbItemTags) when
 * WrittenBookItem.makeSureTagIsValid accepts it -- otherwise the one red "book.invalid.tag" page -- as 1.20.4
 * read them.
 */
@Environment(EnvType.CLIENT)
public class WrittenBookAccess implements BookAccess {

    public final java.util.List<String> field_17420;

    public WrittenBookAccess(net.minecraft.class_1799 stack) {
        this.field_17420 = method_17565(stack);
    }

    /** readPages(stack) */
    public static java.util.List<String> method_17565(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 tag = top.dext.centurybridge.step.v1_20_5.rt.CbItemTags.getTag(stack);
        if (tag != null && top.dext.centurybridge.step.v1_20_5.rt.CbFinItemStatics.method_8053(tag)) {
            return top.dext.centurybridge.step.v1_20_5.rt.CbClientBooks.method_17555(tag);
        }
        return com.google.common.collect.ImmutableList.of(net.minecraft.class_2561.class_2562.method_10867(
            net.minecraft.class_2561.method_43471("book.invalid.tag").method_27692(net.minecraft.class_124.field_1079),
            top.dext.centurybridge.step.v1_20_5.rt.CbRegistries.current()));
    }

    @Override
    public int method_17560() {
        return this.field_17420.size();
    }

    @Override
    public net.minecraft.class_5348 method_17561(int index) {
        String page = this.field_17420.get(index);
        try {
            net.minecraft.class_5348 component = net.minecraft.class_2561.class_2562.method_10877(page,
                top.dext.centurybridge.step.v1_20_5.rt.CbRegistries.current());
            if (component != null) {
                return component;
            }
        } catch (Exception e) {
            // 1.20.4 fell back to the page as plain text on any parse failure
        }
        return net.minecraft.class_5348.method_29430(page);
    }
}
