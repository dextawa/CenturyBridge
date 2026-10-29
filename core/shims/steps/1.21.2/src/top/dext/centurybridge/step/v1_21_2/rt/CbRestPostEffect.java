package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's GameRenderer.postEffect (class_757.field_4024) over 1.21.2's post effect id (field_53898): the pieces
 * CbRestPostEffectRestored (the restored field and loadEffect, at CbVanillaReads.PRIORITY), CbRestPostEffectRestoredHooks
 * (where 1.21.2 writes and reads the id) and CbRestShaderConfigsMixin (which post effects the ShaderManager has) share.
 */
@Environment(EnvType.CLIENT)
public final class CbRestPostEffect {
    private CbRestPostEffect() {
    }

    /** CbRestPostEffectRestored, called from the hooks */
    public interface Game {
        /** the mirror follows the id (after 1.21.2 wrote it); a mod chain the id replaced is closed, as 1.21.1 closed it */
        void cb$v1_21_2$mirrorPostEffect();

        /** a mirror written by a mod goes into the id (before 1.21.2 reads it) */
        void cb$v1_21_2$reconcilePostEffect();

        /** the chain a mod wrote that has no 1.21.2 id (a 1.21.1 json chain), or null */
        top.dext.centurybridge.step.v1_21_2.facade.PostChain cb$v1_21_2$modChain();

        /** GameRenderer.effectActive (field_4013) */
        boolean cb$v1_21_2$effectActive();

        /** the restored 1.21.1 loadEffect(location) (method_3168), for CbRestPostEffectRouteMixin */
        void cb$v1_21_2$loadEffect(net.minecraft.class_2960 location);
    }

    /** CbRestShaderConfigsMixin */
    public interface Configs {
        /** whether the ShaderManager's current configs define this post effect (1.21.2 throws for one they do not) */
        boolean cb$v1_21_2$hasPostEffect(net.minecraft.class_2960 id);
    }

    private static final String PREFIX = "shaders/post/";
    private static final String SUFFIX = ".json";

    /**
     * 1.21.1's spelling of a post effect (&lt;ns&gt;:shaders/post/&lt;name&gt;.json, the location loadEffect read the
     * json from) as 1.21.2's id (&lt;ns&gt;:&lt;name&gt;, the post_effect GameRenderer.setPostEffect takes); null for a
     * location that is not a post json.
     */
    public static net.minecraft.class_2960 id(net.minecraft.class_2960 location) {
        String path = location.method_12832();
        if (!path.startsWith(PREFIX) || !path.endsWith(SUFFIX) || path.length() <= PREFIX.length() + SUFFIX.length()) {
            return null;
        }
        return net.minecraft.class_2960.method_60655(location.method_12836(),
            path.substring(PREFIX.length(), path.length() - SUFFIX.length()));
    }

    /** the inverse of {@link #id}: 1.21.2's post effect id as the location 1.21.1 read its json from */
    public static net.minecraft.class_2960 location(net.minecraft.class_2960 id) {
        return net.minecraft.class_2960.method_60655(id.method_12836(), PREFIX + id.method_12832() + SUFFIX);
    }
}
