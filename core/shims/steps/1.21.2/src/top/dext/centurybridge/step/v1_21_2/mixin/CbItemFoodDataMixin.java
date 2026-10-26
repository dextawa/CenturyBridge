package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_1702;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * FoodData keeps 1.21.1's lastFoodLevel (the food level at the start of the last tick; 20 before the first) and
 * declares 1.21.1's members 1.21.2 dropped (covers): getLastFoodLevel, getExhaustionLevel, setExhaustion, and
 * tick(Player) -- 1.21.2's tick takes a ServerPlayer; 1.21.1's Player.tick ticked food on the server only, so a
 * 1.21.1 call is made with one, and a client player is refused by the cast as 1.21.2 refuses it. Declared here
 * they are real members: a converted subclass's overrides override them and its super calls reach them.
 * The tick's capture of lastFoodLevel is in CbItemFoodDataMixinHooks (default priority).
 */
@Mixin(value = class_1702.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemFoodDataMixin implements CbPrioritySplit.FoodData {

    @Shadow
    private int field_7756;

    @Shadow
    private float field_7752;

    /** null before the first tick (no field initializer): 1.21.1's constructor set 20 */
    @Unique
    private Integer cb$lastFoodLevel;

    /** set at the head of 1.21.2's tick (CbItemFoodDataMixinHooks) */
    @Override
    public void cb$v1_21_2$lastFoodLevel(int level) {
        this.cb$lastFoodLevel = level;
    }

    @Unique
    private int cb$v1_21_2$lastFoodLevel() {
        return cb$lastFoodLevel != null ? cb$lastFoodLevel : 20;
    }

    /** getLastFoodLevel() */
    public int method_35217() {
        return cb$v1_21_2$lastFoodLevel();
    }

    /** getExhaustionLevel() */
    public float method_35219() {
        return field_7752;
    }

    /** setExhaustion(exhaustion) */
    public void method_35218(float exhaustion) {
        this.field_7752 = exhaustion;
    }

    /** tick(player): 1.21.2's tick for the server player it is */
    public void method_7588(class_1657 player) {
        ((class_1702) (Object) this).method_7588((class_3222) player);
    }
}
