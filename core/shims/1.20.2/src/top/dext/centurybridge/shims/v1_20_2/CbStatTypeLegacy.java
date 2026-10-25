package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_3448;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbStatTypes;

/**
 * 1.20.1's StatType.getTranslationKey() back (s1misc), 1.20.1 body: "stat_type." and the registry
 * id with ':' as '.'. A stat type constructed the 1.20.1 way (CbStatTypes) is displayed as 1.20.1
 * displayed it: the translation of getTranslationKey() -- a converted subclass's override
 * included -- computed when first asked and kept. Every other stat type whose 1.20.2 name is the one
 * 1.20.1 computed is named through it too (CbStatTypeLegacyHooks.cb$routeDisplayName), so vanilla's
 * read of the name goes through this member as 1.20.1's getDisplayName() did.
 */
@Mixin(value = class_3448.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbStatTypeLegacy implements CbStatTypes.Legacy {

    @top.dext.centurybridge.rt.v1_20_2.CbVanillaReads("CbStatTypeLegacyHooks: StatType.getDisplayName() answers the translation of it, asked once, as 1.20.1 did")
    public String method_14957() {
        return "stat_type." + class_7923.field_41193.method_10221((class_3448<?>) (Object) this).toString().replace(':', '.');
    }
}
