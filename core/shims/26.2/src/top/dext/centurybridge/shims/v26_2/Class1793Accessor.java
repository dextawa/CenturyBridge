package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Key plumbing on Item Settings for the pending-key repair (see CbPending). */
@Mixin(net.minecraft.world.item.Item.Properties.class)
public interface Class1793Accessor {

    @Accessor("id")
    net.minecraft.resources.ResourceKey<net.minecraft.world.item.Item> cbGetKey();

    @Accessor("id")
    void cbSetKey(net.minecraft.resources.ResourceKey<net.minecraft.world.item.Item> key);

    @Invoker("effectiveDescriptionId")
    String cbTranslationKey();

    @Invoker("effectiveModel")
    net.minecraft.resources.Identifier cbModelId();

}
