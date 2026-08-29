package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.20.2 Advancement.Builder no longer exposes getCriteria(); the map lives in an ImmutableMap.Builder. */
@Mixin(net.minecraft.class_161.class_162.class)
public interface CbAdvancementBuilderCriteria {
    @Accessor("field_1148")
    com.google.common.collect.ImmutableMap.Builder<String, net.minecraft.class_175<?>> cbCriteria();
}
