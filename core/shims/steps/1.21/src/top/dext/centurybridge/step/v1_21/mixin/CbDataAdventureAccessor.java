package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** VanillaAdventureAdvancements.MOBS_TO_KILL (protected static), for 1.20.6's UpdateOneTwentyOneAdventureAdvancements facade */
@Mixin(net.minecraft.class_2412.class)
public interface CbDataAdventureAccessor {

    @Accessor("field_11294")
    static java.util.List<net.minecraft.class_1299<?>> cb$mobsToKill() {
        throw new AssertionError("mixin accessor");
    }
}
