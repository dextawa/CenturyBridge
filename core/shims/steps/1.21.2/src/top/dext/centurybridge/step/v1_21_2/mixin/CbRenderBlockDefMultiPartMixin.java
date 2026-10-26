package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBlockDefs;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 MultiPart's members over 1.21.2's (see CbRenderBlockDefs): the state definition and selectors it was built
 * from, getSelectors() and getMultiVariants() with 1.21.1's bodies.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_816.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderBlockDefMultiPartMixin implements CbRenderBlockDefs.Part {

    @Unique
    private net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> cb$stateDefinition;

    @Unique
    private List<net.minecraft.class_819> cb$selectors;

    /** 1.21.1 getSelectors() */
    public List<net.minecraft.class_819> method_3519() {
        return this.cb$selectors;
    }

    /** 1.21.1 getMultiVariants(): every selector's variant */
    public Set<net.minecraft.class_807> method_3520() {
        Set<net.minecraft.class_807> variants = new HashSet<>();
        for (net.minecraft.class_819 selector : this.cb$selectors) {
            variants.add(selector.method_3529());
        }
        return variants;
    }

    @Override
    public net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> cb$stateDefinition() {
        return this.cb$stateDefinition;
    }

    @Override
    public List<net.minecraft.class_819> cb$selectors() {
        return this.cb$selectors;
    }

    @Override
    public void cb$attach(net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition, List<net.minecraft.class_819> selectors) {
        this.cb$stateDefinition = definition;
        this.cb$selectors = selectors;
    }
}
