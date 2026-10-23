package top.dext.centurybridge.step.v1_21_4.facade;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.rt.CbItemRender;

/**
 * 1.21.3 ItemModelShaper (class_763, gone in 1.21.4): an item model id's baked model. 1.21.4 bakes no
 * "&lt;id&gt;#inventory" models; an item model id now names an item model definition. The model answered is the one
 * that definition resolves for a stack carrying that id as its item model (its default state), as a
 * {@link CbItemRender.StackModel}, so ItemRenderer.render draws it through the definition.
 */
@Environment(EnvType.CLIENT)
public class ItemModelShaper {

    /** modelCache */
    public final Map<net.minecraft.class_2960, net.minecraft.class_1087> field_54157 = new HashMap<>();
    /** missingModel */
    public final Supplier<net.minecraft.class_1087> field_54158;
    /** modelGetter */
    public final Function<net.minecraft.class_2960, net.minecraft.class_1087> field_54159;

    public ItemModelShaper(net.minecraft.class_1092 modelManager) {
        this.field_54158 = modelManager::method_4744;
        this.field_54159 = id -> {
            net.minecraft.class_1799 stack = new net.minecraft.class_1799(net.minecraft.class_1802.field_8407);
            stack.method_57379(net.minecraft.class_9334.field_54199, id);
            return CbItemRender.getModel(null, stack, null, null, 0);
        };
    }

    /** getItemModel(stack): the model of the stack's item_model component, or the missing model */
    public net.minecraft.class_1087 method_3308(net.minecraft.class_1799 stack) {
        net.minecraft.class_2960 id = stack.method_57824(net.minecraft.class_9334.field_54199);
        return id == null ? this.field_54158.get() : this.method_64057(id);
    }

    /** getItemModel(id) */
    public net.minecraft.class_1087 method_64057(net.minecraft.class_2960 id) {
        return this.field_54157.computeIfAbsent(id, this.field_54159);
    }

    /** invalidateCache */
    public void method_64056() {
        this.field_54157.clear();
    }
}
