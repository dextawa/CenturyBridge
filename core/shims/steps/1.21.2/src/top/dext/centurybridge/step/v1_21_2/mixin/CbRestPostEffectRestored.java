package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.facade.PostChain;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderPostNative;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestPostEffect;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * GameRenderer's (class_757) post effect. 1.21.1 kept the chain itself, postEffect (field_4024, a PostChain loaded from
 * &lt;ns&gt;:shaders/post/&lt;name&gt;.json by loadEffect, method_3168): checkEntityPostEffect and shutdownEffect closed and
 * dropped it, loadEffect closed it and loaded the next, render ran it while effectActive (field_4013), resize resized it,
 * close closed it, currentEffect (method_3183) answered it. 1.21.2 keeps the effect's id instead (field_53898,
 * &lt;ns&gt;:&lt;name&gt;): checkEntityPostEffect / clearPostEffect / setPostEffect (method_3167 / method_62905 /
 * method_62904) write it, render asks the ShaderManager for the chain of that post_effect and runs it (javap both).
 *
 * <p>The id stays the truth. This mixin restores postEffect as its mirror -- null for no id, else 1.21.1's chain for the
 * id (rt/CbRenderPostNative, the same object while the id is the same) -- with the value last synced (cb$v1_21_2$synced),
 * and loadEffect, which loads the effect through the id. CbRestPostEffectRestoredHooks copies the id into the mirror
 * wherever 1.21.2 writes it, and before 1.21.2 reads it (render, getPostEffect) takes a mirror a converted mod wrote (its
 * @Shadow / @Accessor) into the id: null clears it, a chain answered for an id sets that id. A chain a mod loaded from a
 * 1.21.1 json itself has no 1.21.2 id: the id is cleared and the hooks run, resize and close that chain where 1.21.1's
 * render, resize and close did. The layer writes the id only when the mirror was written or loadEffect is called:
 * unmixed, 1.21.2's state is untouched. Priority 0, below every mixin shadowing these members (as CbVanillaReads.PRIORITY
 * puts it below every mod's, so a mod's 1.21.1 injectors anchor inside loadEffect): Mixin applies a target's mixins in
 * priority order, and the climbed 1.20.5 layer's CbClientGameRendererShadersMixin (cycleEffect) shadows postEffect and
 * loadEffect at that layer's CbVanillaReads.PRIORITY (1); at 1 the 1.21.1 -> 1.21.2 step strips it (its shadow field
 * would not be declared yet when it applies).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_757.class, priority = 0)
public abstract class CbRestPostEffectRestored implements CbRestPostEffect.Game {

    @Shadow
    @Final
    private static org.slf4j.Logger field_3993;

    @Shadow
    @Final
    private net.minecraft.class_310 field_4015;

    @Shadow
    private net.minecraft.class_2960 field_53898;

    @Shadow
    private boolean field_4013;

    @Shadow
    private void method_62904(net.minecraft.class_2960 id) {
        throw new AssertionError();
    }

    /** 1.21.1 GameRenderer.postEffect: mirror of the post effect id */
    private PostChain field_4024;

    /** what the mirror held when last synced with the id: a mirror that differs was written by a mod */
    @Unique
    private PostChain cb$v1_21_2$synced;

    @Override
    public void cb$v1_21_2$mirrorPostEffect() {
        PostChain old = this.field_4024;
        net.minecraft.class_2960 id = this.field_53898;
        PostChain now;
        if (id == null) {
            now = null;
        } else if (old instanceof CbRenderPostNative n && n.id().equals(id)) {
            now = n;
        } else if (this.cb$v1_21_2$synced instanceof CbRenderPostNative n && n.id().equals(id)) {
            now = n;
        } else {
            now = CbRenderPostNative.of(id);
        }
        if (old != null && old != now && !(old instanceof CbRenderPostNative)) {
            // 1.21.1's checkEntityPostEffect / shutdownEffect / loadEffect closed the chain they replaced
            old.close();
        }
        this.field_4024 = this.cb$v1_21_2$synced = now;
    }

    @Override
    public void cb$v1_21_2$reconcilePostEffect() {
        PostChain written = this.field_4024;
        if (written == this.cb$v1_21_2$synced) {
            // the id written past 1.21.2's own methods (a native 1.21.2 mod's mixin): the mirror follows
            net.minecraft.class_2960 mirrored = written instanceof CbRenderPostNative n ? n.id() : null;
            if (!java.util.Objects.equals(mirrored, this.field_53898)) {
                this.cb$v1_21_2$mirrorPostEffect();
            }
            return;
        }
        // a 1.21.1 field write replaces the chain without closing the old one
        this.field_53898 = written instanceof CbRenderPostNative n ? n.id() : null;
        this.cb$v1_21_2$synced = written;
    }

    @Override
    public PostChain cb$v1_21_2$modChain() {
        this.cb$v1_21_2$reconcilePostEffect();
        PostChain chain = this.field_4024;
        return chain instanceof CbRenderPostNative ? null : chain;
    }

    @Override
    public boolean cb$v1_21_2$effectActive() {
        return this.field_4013;
    }

    /** 1.21.1 currentEffect() */
    public PostChain method_3183() {
        this.cb$v1_21_2$reconcilePostEffect();
        return this.field_4024;
    }

    /**
     * 1.21.1 loadEffect(location): the current chain closed, then the effect at location made the active one -- through
     * 1.21.2's setPostEffect with the id 1.21.2 spells it (CbRestPostEffect.id) when the ShaderManager has that
     * post_effect, else the 1.21.1 json at location loaded as 1.21.1 loaded it; one that fails to load is logged and
     * leaves the effect inactive, as in 1.21.1 (1.21.2's ShaderManager treats an unknown id as a broken resource pack).
     */
    @CbVanillaReads("loadEffect is private in 1.21.1: only a converted mod's @Shadow / @Invoker calls it (the 1.20.5 layer's "
        + "cycleEffect among them); body 1.21.1's, the chain loaded through 1.21.2's post effect id")
    private void method_3168(net.minecraft.class_2960 location) {
        this.cb$v1_21_2$reconcilePostEffect();
        PostChain old = this.field_4024;
        if (old != null) {
            old.close();
            if (!(old instanceof CbRenderPostNative)) {
                this.field_4024 = this.cb$v1_21_2$synced = null;
            }
        }
        net.minecraft.class_2960 id = CbRestPostEffect.id(location);
        if (id != null && ((CbRestPostEffect.Configs) this.field_4015.method_62887()).cb$v1_21_2$hasPostEffect(id)) {
            this.method_62904(id);
            return;
        }
        try {
            PostChain chain = new PostChain(this.field_4015.method_1531(), this.field_4015.method_1478(), this.field_4015.method_1522(), location);
            chain.method_1259(this.field_4015.method_22683().method_4489(), this.field_4015.method_22683().method_4506());
            this.field_53898 = null;
            this.field_4024 = this.cb$v1_21_2$synced = chain;
            this.field_4013 = true;
        } catch (java.io.IOException e) {
            field_3993.warn("Failed to load shader: {}", location, e);
            this.field_4013 = false;
        } catch (com.google.gson.JsonSyntaxException e) {
            field_3993.warn("Failed to parse shader: {}", location, e);
            this.field_4013 = false;
        }
    }
}
