package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_2048;
import net.minecraft.class_2096;
import net.minecraft.class_6404;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * The 1.20.1 readers of the entity sub-predicate types LightningBoltPredicate (method_37238: blocks_set_on_fire,
 * entity_struck through EntityPredicate's restored reader), FishingHookPredicate (method_26094: in_open_water, else ANY)
 * and SlimePredicate (method_43158: size), gone from 1.20.2 (their MapCodecs read them), restored on those classes as
 * private statics with 1.20.1's bodies (one mixin class each; this file holds the lightning one, CbAdvFishingHookJson and
 * CbAdvSlimeJson the others). 1.20.1 reached them through EntitySubPredicate.fromJson; the restored EntityPredicate reader
 * dispatches type_specific the same way (CbAdvPredicateJson.subPredicateFromJson; CbAdvLightningAccess, CbAdvFishingHookAccess, CbAdvSlimeAccess).
 */
@Mixin(value = class_6404.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvLightningJson {

    /** 1.20.1 LightningBoltPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored EntityPredicate.fromJson reaches it through type_specific \"lightning\";"
        + " 1.20.2's EntityPredicate readers consult that against its unmixed copy")
    private static class_6404 method_37238(JsonObject json) {
        class_2096.class_2100 blocksSetOnFire = class_2096.class_2100.method_9056(json.get("blocks_set_on_fire"));
        class_2048 entityStruck = CbAdvEntityPredicateAccess.cb$fromJson(json.get("entity_struck"));
        return new class_6404(blocksSetOnFire, CbAdvAny.entity(entityStruck));
    }
}
