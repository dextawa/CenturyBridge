package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** the storage an ordered collection belongs to (rt/CbFapiSubmitBlocks: 1.21.11 submitted special block renderers there) */
@Environment(EnvType.CLIENT)
@Mixin(SubmitNodeCollection.class)
public interface CbFapiSubmitNodeCollectionAccess {
    @Accessor("submitNodeStorage")
    SubmitNodeStorage cb$submitNodeStorage();
}
