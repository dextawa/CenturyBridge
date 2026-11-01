package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * BlockLootSubProvider members 1.20.6 had as statics (HAS_SILK_TOUCH and the table builders reading it) that
 * 1.21 made protected instance members reading the provider's registries; CbLoot answers a 1.20.6 static
 * call with them, on the provider whose generate is running.
 */
@Mixin(net.minecraft.class_7788.class)
public interface CbLootBlockInvoker {

    @Accessor("field_51845")
    net.minecraft.class_7225.class_7874 cb$registries();

    @Invoker("method_60390")
    net.minecraft.class_5341.class_210 cb$hasSilkTouch();

    @Invoker("method_60391")
    net.minecraft.class_5341.class_210 cb$doesNotHaveSilkTouch();

    /** 1.21's private hasShearsOrSilkTouch() (1.20.6's static HAS_SHEARS_OR_SILK_TOUCH) */
    @Invoker("method_60392")
    net.minecraft.class_5341.class_210 cb$hasShearsOrSilkTouch();

    /** 1.21's private doesNotHaveShearsOrSilkTouch() (1.20.6's static HAS_NO_SHEARS_OR_SILK_TOUCH) */
    @Invoker("method_60393")
    net.minecraft.class_5341.class_210 cb$doesNotHaveShearsOrSilkTouch();

    /** 1.21's private createSilkTouchOnlyTable(item) (1.20.6's private static) */
    @Invoker("method_46003")
    net.minecraft.class_52.class_53 cb$createSilkTouchOnlyTable(net.minecraft.class_1935 item);

    @Invoker("method_45989")
    net.minecraft.class_52.class_53 cb$createSilkTouchDispatchTable(net.minecraft.class_2248 block, net.minecraft.class_79.class_80<?> entry);

    @Invoker("method_46001")
    net.minecraft.class_52.class_53 cb$createShearsDispatchTable(net.minecraft.class_2248 block, net.minecraft.class_79.class_80<?> entry);

    @Invoker("method_46008")
    net.minecraft.class_52.class_53 cb$createSilkTouchOrShearsDispatchTable(net.minecraft.class_2248 block, net.minecraft.class_79.class_80<?> entry);

    @Invoker("method_46014")
    net.minecraft.class_52.class_53 cb$createBeeNestDrop(net.minecraft.class_2248 block);

    @Invoker("method_46015")
    net.minecraft.class_52.class_53 cb$createBeeHiveDrop(net.minecraft.class_2248 block);

    @Invoker("method_46016")
    net.minecraft.class_52.class_53 cb$createCaveVinesDrop(net.minecraft.class_2248 block);

    @Invoker("method_46019")
    net.minecraft.class_52.class_53 cb$createDoublePlantShearsDrop(net.minecraft.class_2248 block);
}
