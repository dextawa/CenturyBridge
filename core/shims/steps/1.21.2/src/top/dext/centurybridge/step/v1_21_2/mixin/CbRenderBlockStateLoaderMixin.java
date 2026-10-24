package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.facade.BlockStateLoadedJson;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBlockStates;

/** 1.21.1 BlockStateModelLoader's constructor state, loadAllBlockStates() and getModelGroups() on 1.21.2's loader (see CbRenderBlockStates) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_9824.class)
public abstract class CbRenderBlockStateLoaderMixin implements CbRenderBlockStates.Legacy {

    @Shadow
    @Final
    private static Map<net.minecraft.class_2960, net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680>> field_52266;

    @Shadow
    @Final
    private net.minecraft.class_1100 field_53658;

    @Unique
    private Map<net.minecraft.class_2960, List<BlockStateLoadedJson>> cb$blockStates;

    @Unique
    private net.minecraft.class_3695 cb$profiler;

    @Unique
    private net.minecraft.class_324 cb$blockColors;

    @Unique
    private BiConsumer<net.minecraft.class_1091, net.minecraft.class_1100> cb$output;

    @Unique
    private final Map<net.minecraft.class_1091, net.minecraft.class_9824.class_9825> cb$loaded = new HashMap<>();

    @Override
    public void cb$legacy(Map<net.minecraft.class_2960, List<BlockStateLoadedJson>> blockStates, net.minecraft.class_3695 profiler,
                          net.minecraft.class_324 blockColors, BiConsumer<net.minecraft.class_1091, net.minecraft.class_1100> output) {
        this.cb$blockStates = blockStates;
        this.cb$profiler = profiler;
        this.cb$blockColors = blockColors;
        this.cb$output = output;
    }

    @Override
    public Map<net.minecraft.class_1091, net.minecraft.class_9824.class_9825> cb$loaded() {
        return this.cb$loaded;
    }

    /** 1.21.1 BlockStateModelLoader.loadAllBlockStates() */
    @Override
    public void method_61051() {
        if (this.cb$blockStates == null) {
            throw new IllegalStateException("1.21.1 loadAllBlockStates on a BlockStateModelLoader 1.21.1's constructor did not build");
        }
        net.minecraft.class_9824 self = (net.minecraft.class_9824) (Object) this;
        this.cb$profiler.method_15396("static_definitions");
        field_52266.forEach((id, definition) -> CbRenderBlockStates.load(self, this.cb$blockStates, this.cb$output, this.field_53658, this.cb$loaded,
            id, definition));
        this.cb$profiler.method_15405("blocks");
        for (net.minecraft.class_2248 block : net.minecraft.class_7923.field_41175) {
            CbRenderBlockStates.load(self, this.cb$blockStates, this.cb$output, this.field_53658, this.cb$loaded,
                block.method_40142().method_40237().method_29177(), block.method_9595());
        }
        this.cb$profiler.method_15407();
    }

    /** 1.21.1 BlockStateModelLoader.getModelGroups() */
    @Override
    public it.unimi.dsi.fastutil.objects.Object2IntMap<net.minecraft.class_2680> method_61069() {
        return CbRenderBlockStates.groups(this.cb$blockColors, this.cb$loaded);
    }
}
