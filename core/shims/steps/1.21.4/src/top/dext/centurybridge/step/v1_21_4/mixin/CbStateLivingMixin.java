package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/**
 * LivingEntityRenderState's 1.21.3 item fields (see {@link CbRenderStates}): the head item and its model, the main arm, both
 * hands' items and models. The head item keeps its 1.21.3 name elsewhere (1.21.4's field of that name is its render state).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10042.class)
public abstract class CbStateLivingMixin implements CbRenderStates.CbLegacyLivingState, CbRenderStates.CbLegacyHands {

    /** headItemModel */
    public net.minecraft.class_1087 field_53466;
    /** mainArm */
    public net.minecraft.class_1306 field_53468 = net.minecraft.class_1306.field_6183;
    /** rightHandItemModel */
    public net.minecraft.class_1087 field_53469;
    /** rightHandItem */
    public net.minecraft.class_1799 field_53470 = net.minecraft.class_1799.field_8037;
    /** leftHandItemModel */
    public net.minecraft.class_1087 field_53471;
    /** leftHandItem */
    public net.minecraft.class_1799 field_53472 = net.minecraft.class_1799.field_8037;

    private net.minecraft.class_1799 cb$headItem = net.minecraft.class_1799.field_8037;

    @Override
    public net.minecraft.class_1799 cb$headItem() {
        return this.cb$headItem;
    }

    @Override
    public void cb$setHeadItem(net.minecraft.class_1799 stack) {
        this.cb$headItem = stack;
    }

    @Override
    public void cb$setHeadItemModel(net.minecraft.class_1087 model) {
        this.field_53466 = model;
    }

    @Override
    public void cb$setLegacyItems(net.minecraft.class_1087 headItemModel, net.minecraft.class_1306 mainArm, net.minecraft.class_1799 rightHandItem,
            net.minecraft.class_1799 leftHandItem, net.minecraft.class_1087 rightHandItemModel, net.minecraft.class_1087 leftHandItemModel) {
        this.field_53466 = headItemModel;
        this.field_53468 = mainArm;
        this.field_53470 = rightHandItem;
        this.field_53472 = leftHandItem;
        this.field_53469 = rightHandItemModel;
        this.field_53471 = leftHandItemModel;
    }

    @Override
    public net.minecraft.class_1087 cb$rightHandItemModel() {
        return this.field_53469;
    }

    @Override
    public net.minecraft.class_1087 cb$leftHandItemModel() {
        return this.field_53471;
    }

    @Override
    public net.minecraft.class_1799 cb$rightHandItem() {
        return this.field_53470;
    }

    @Override
    public net.minecraft.class_1799 cb$leftHandItem() {
        return this.field_53472;
    }

    /** was getMainHandItem() */
    public net.minecraft.class_1799 method_62614() {
        return this.field_53468 == net.minecraft.class_1306.field_6183 ? this.field_53470 : this.field_53472;
    }

    /** was getMainHandItemModel() */
    public net.minecraft.class_1087 method_62615() {
        return this.field_53468 == net.minecraft.class_1306.field_6183 ? this.field_53469 : this.field_53471;
    }
}
