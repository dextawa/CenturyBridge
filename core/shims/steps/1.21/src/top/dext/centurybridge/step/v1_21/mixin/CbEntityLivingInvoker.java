package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_2338;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * LivingEntity members CbEntityLiving reaches that 1.21 keeps protected or private: the 1.20.6 caller's call
 * lands in the layer, which makes the 1.21 call (virtually, so overrides answer).
 */
@Mixin(class_1309.class)
public interface CbEntityLivingInvoker {

    @Invoker("method_20237")
    static byte cb$entityEventForEquipmentBreak(class_1304 slot) {
        throw new AssertionError();
    }

    @Invoker("method_16080")
    void cb$dropAllDeathLoot(class_3218 level, class_1282 source);

    @Invoker("method_23883")
    void cb$dropExperience(class_1297 attacker);

    @Invoker("method_6126")
    void cb$onChangedBlock(class_3218 level, class_2338 pos);
}
