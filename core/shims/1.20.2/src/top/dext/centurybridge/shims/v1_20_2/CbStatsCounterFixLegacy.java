package top.dext.centurybridge.shims.v1_20_2;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbStatsCounterFix;

/**
 * A 1.20.1 StatsCounterFix subclass's upgradeItem / upgradeBlock overrides, asked again by the stats
 * rule (CbStatsCounterFix). 1.20.1's rule called them on the fix; 1.20.2's stats rule (method_52496:
 * fixTypeEverywhereTyped("StatsCounterFix", input STATS type, output STATS type, method_52502) whose
 * function calls the private static upgrade methods) has no fix to call them on. For a fix that
 * overrides one, the same rule is built with its function run with the fix recorded, and the private
 * static upgrade methods answer through the override while it is.
 */
@Mixin(net.minecraft.class_1214.class)
public abstract class CbStatsCounterFixLegacy extends DataFix {

    private CbStatsCounterFixLegacy(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Shadow
    private static Typed<?> method_52502(Type<?> outputType, Typed<?> typed) {
        throw new AssertionError();
    }

    @Inject(method = "method_52496", at = @At("RETURN"), cancellable = true)
    private void cb$legacyUpgrades(CallbackInfoReturnable<TypeRewriteRule> cir) {
        Object self = this;
        if (!(self instanceof CbStatsCounterFix.LegacyItem) && !(self instanceof CbStatsCounterFix.LegacyBlock)) {
            return;
        }
        Type<?> input = this.getInputSchema().getType(net.minecraft.class_1208.field_5730);
        Type<?> output = this.getOutputSchema().getType(net.minecraft.class_1208.field_5730);
        cir.setReturnValue(this.fixTypeEverywhereTyped("StatsCounterFix", input, output,
            CbStatsCounterFix.applying(self, (Typed<?> typed) -> method_52502(output, typed))));
    }

    @Inject(method = "method_5172", at = @At("HEAD"), cancellable = true)
    private static void cb$upgradeItem(String name, CallbackInfoReturnable<String> cir) {
        CbStatsCounterFix.LegacyItem own = CbStatsCounterFix.itemOverride();
        if (own != null) {
            cir.setReturnValue(own.method_5172(name));
        }
    }

    @Inject(method = "method_5173", at = @At("HEAD"), cancellable = true)
    private static void cb$upgradeBlock(String name, CallbackInfoReturnable<String> cir) {
        CbStatsCounterFix.LegacyBlock own = CbStatsCounterFix.blockOverride();
        if (own != null) {
            cir.setReturnValue(own.method_5173(name));
        }
    }
}
