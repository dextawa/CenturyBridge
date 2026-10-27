package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import net.minecraft.class_161;
import net.minecraft.class_2960;
import net.minecraft.class_2989;
import net.minecraft.class_60;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.rt.v1_20_2.CbAdvBuilders;

/**
 * ServerAdvancementManager.apply's per-advancement step (method_20723): 1.20.2 reads the advancement with
 * Advancement.fromJson -- unchanged, so a 1.20.2-era mod's mixin on that path acts as without CenturyBridge. The read
 * advancement, as it is stored in the step's local, is what CbAdvBuilders.consult keeps unless the restored 1.20.1
 * Advancement.Builder.fromJson (CbAdvBuilderJson), changed by a converted mod's mixin, answers otherwise than its unmixed
 * copy: then the advancement that builder builds, as 1.20.1's manager built the builders Builder.fromJson answered.
 *
 * No handler captures the step's arguments by position, so a later release reshaping them leaves all three applicable
 * (1.20.5 adds a RegistryOps first argument): the id and the JSON are taken by their types at HEAD (argsOnly) and carried
 * to the STORE handler, which captures nothing.
 */
@Mixin(class_2989.class)
public abstract class CbAdvJsonVanillaMixin {

    @Shadow
    @Final
    private class_60 field_44468;

    @ModifyVariable(method = "method_20723", at = @At("HEAD"), argsOnly = true, require = 1)
    private class_2960 cb$carryId(class_2960 id) {
        CbAdvBuilders.carryId(id);
        return id;
    }

    @ModifyVariable(method = "method_20723", at = @At("HEAD"), argsOnly = true, require = 1)
    private JsonElement cb$carryJson(JsonElement json) {
        CbAdvBuilders.carryJson(json);
        return json;
    }

    @ModifyVariable(method = "method_20723", at = @At("STORE"), ordinal = 0, require = 1)
    private class_161 cb$consultLegacyReader(class_161 read) {
        return CbAdvBuilders.consultCarried(read, this.field_44468);
    }
}
