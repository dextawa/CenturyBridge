package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2561;
import net.minecraft.class_8052;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemSmithingTemplates;

/**
 * A SmithingTemplateItem built by 1.21.1's constructor or factories keeps its upgrade description, which 1.21.1's
 * tooltip showed as its first line where 1.21.2's shows a fixed "Smithing Template" (CbItemSmithingTemplates); a
 * 1.21.2-built template keeps 1.21.2's line.
 */
@Mixin(class_8052.class)
public abstract class CbItemSmithingTemplateMixin implements CbItemSmithingTemplates.Upgrade {

    @Shadow
    @Final
    private static class_2561 field_52584;

    @Unique
    private class_2561 cb$upgrade;

    @Override
    public void cb$setUpgrade(class_2561 upgrade) {
        this.cb$upgrade = upgrade;
    }

    @Redirect(method = "method_7851", at = @At(value = "FIELD", target = "Lnet/minecraft/class_8052;field_52584:Lnet/minecraft/class_2561;",
        opcode = Opcodes.GETSTATIC))
    private class_2561 cb$upgradeLine() {
        return this.cb$upgrade != null ? this.cb$upgrade : field_52584;
    }
}
