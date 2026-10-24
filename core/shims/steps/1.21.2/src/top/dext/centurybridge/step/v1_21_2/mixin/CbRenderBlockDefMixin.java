package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBlockDefs;

/**
 * 1.21.1 BlockModelDefinition's members over 1.21.2's (see CbRenderBlockDefs): hasVariant, getVariants, isMultiPart
 * with 1.21.1's bodies on the same fields, and the state definition / 1.21.1 MultiPart the definition answers.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_790.class)
public abstract class CbRenderBlockDefMixin implements CbRenderBlockDefs.Def, CbRenderBlockDefs.Legacy {

    @Shadow
    @Final
    private Map<String, net.minecraft.class_807> field_4241;

    @Shadow
    @Final
    private net.minecraft.class_816.class_9982 field_4240;

    @Unique
    private net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> cb$stateDefinition;

    @Unique
    private net.minecraft.class_816 cb$multiPart;

    /** 1.21.1 hasVariant(key) */
    public boolean method_35790(String key) {
        return this.field_4241.get(key) != null;
    }

    /** 1.21.1 getVariants() */
    public Map<String, net.minecraft.class_807> method_3423() {
        return this.field_4241;
    }

    /** 1.21.1 isMultiPart() */
    @Override
    public boolean method_3422() {
        return this.field_4240 != null;
    }

    @Override
    public net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> cb$stateDefinition() {
        return this.cb$stateDefinition;
    }

    @Override
    public void cb$setStateDefinition(net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition) {
        this.cb$stateDefinition = definition;
    }

    @Override
    public net.minecraft.class_816 cb$multiPart() {
        return this.cb$multiPart;
    }

    @Override
    public void cb$setMultiPart(net.minecraft.class_816 multiPart) {
        this.cb$multiPart = multiPart;
    }

    @Override
    public net.minecraft.class_816.class_9982 cb$definition() {
        return this.field_4240;
    }

    @Override
    public Map<String, net.minecraft.class_807> cb$variants() {
        return this.field_4241;
    }
}
