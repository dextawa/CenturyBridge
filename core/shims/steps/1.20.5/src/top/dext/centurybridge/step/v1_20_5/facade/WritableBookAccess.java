package top.dext.centurybridge.step.v1_20_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's BookViewScreen.WritableBookAccess (classRenames): a book and quill's pages as plain text, read from
 * the stack's 1.20.4 tag (rt/CbItemTags: the tag 1.20.4 would have had) as 1.20.4 read it.
 */
@Environment(EnvType.CLIENT)
public class WritableBookAccess implements BookAccess {

    public final java.util.List<String> field_17419;

    public WritableBookAccess(net.minecraft.class_1799 stack) {
        this.field_17419 = method_17564(stack);
    }

    /** readPages(stack) */
    public static java.util.List<String> method_17564(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 tag = top.dext.centurybridge.step.v1_20_5.rt.CbItemTags.getTag(stack);
        return tag != null ? top.dext.centurybridge.step.v1_20_5.rt.CbClientBooks.method_17555(tag)
                           : com.google.common.collect.ImmutableList.of();
    }

    @Override
    public int method_17560() {
        return this.field_17419.size();
    }

    @Override
    public net.minecraft.class_5348 method_17561(int index) {
        return net.minecraft.class_5348.method_29430(this.field_17419.get(index));
    }
}
