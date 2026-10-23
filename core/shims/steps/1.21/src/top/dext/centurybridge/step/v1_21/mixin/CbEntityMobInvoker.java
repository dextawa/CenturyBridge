package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1266;
import net.minecraft.class_1308;
import net.minecraft.class_5425;
import net.minecraft.class_5819;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Mob members the layer's 1.20.6 bodies reach that 1.21 keeps protected. */
@Mixin(class_1308.class)
public interface CbEntityMobInvoker {

    @Invoker("method_5984")
    void cb$populateDefaultEquipmentEnchantments(class_5425 level, class_5819 random, class_1266 difficulty);
}
