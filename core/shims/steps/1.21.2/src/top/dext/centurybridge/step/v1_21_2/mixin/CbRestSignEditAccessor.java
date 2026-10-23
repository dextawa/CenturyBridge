package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2625;
import net.minecraft.class_7743;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** AbstractSignEditScreen.sign (protected) for CbRestGuiClient's renderSignBackground bridge */
@Environment(EnvType.CLIENT)
@Mixin(class_7743.class)
public interface CbRestSignEditAccessor {

    @Accessor("field_40424")
    class_2625 cb$sign();
}
