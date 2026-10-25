package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * HeaderAndFooterLayout's 1.20.1 LayoutSettings API, which 1.20.2 replaced with Consumer&lt;LayoutSettings&gt;
 * overloads: addToHeader / addToFooter / addToContents (element, settings) and the new*LayoutSettings()
 * factories. The 1.20.1 bodies: each puts the element into, or copies the child defaults of, the
 * header / footer / contents FrameLayout, which 1.20.2 still has with the same methods. 1.20.1's vanilla
 * code never called these; they serve a mod's calls and its subclass's overrides and super calls.
 */
@Mixin(value = net.minecraft.class_8132.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbGuiHeaderFooterBridge {

    @Shadow
    @Final
    private net.minecraft.class_7843 field_42491; // header
    @Shadow
    @Final
    private net.minecraft.class_7843 field_42492; // footer
    @Shadow
    @Final
    private net.minecraft.class_7843 field_42493; // contents

    /** 1.20.1 addToHeader(element, settings) */
    public <T extends net.minecraft.class_8021> T method_48993(T element, net.minecraft.class_7847 settings) {
        return this.field_42491.method_46444(element, settings);
    }

    /** 1.20.1 addToFooter(element, settings) */
    public <T extends net.minecraft.class_8021> T method_48997(T element, net.minecraft.class_7847 settings) {
        return this.field_42492.method_46444(element, settings);
    }

    /** 1.20.1 addToContents(element, settings) */
    public <T extends net.minecraft.class_8021> T method_49000(T element, net.minecraft.class_7847 settings) {
        return this.field_42493.method_46444(element, settings);
    }

    /** 1.20.1 newHeaderLayoutSettings() */
    public net.minecraft.class_7847 method_49001() {
        return this.field_42491.method_46448();
    }

    /** 1.20.1 newContentLayoutSettings() */
    public net.minecraft.class_7847 method_49002() {
        return this.field_42493.method_46448();
    }

    /** 1.20.1 newFooterLayoutSettings() */
    public net.minecraft.class_7847 method_49003() {
        return this.field_42492.method_46448();
    }
}
