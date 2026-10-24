package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.MapMaker;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.glfw.GLFW;

/**
 * Client records whose components 26.1 changed, 1.21.11's values on 26.1's:
 * <ul>
 * <li>submit poses: 1.21.11 held a Matrix4f, 26.1 a Matrix4fc. A Matrix4f is one; a pose that is not (26.1 hands
 *     out its own PoseStack matrices, which are) is copied;</li>
 * <li>ModelRenderProperties: 1.21.11's particle sprite is 26.1's particle material's (not forced translucent:
 *     1.21.11 had no such notion);</li>
 * <li>CharacterEvent: 1.21.11 carried the GLFW modifier bits of the typed character; 26.1 drops them. An event a
 *     1.21.11 constructor made keeps its own; one 26.1 made answers the modifier keys held now (the event is
 *     handled as the character arrives, so they are the keys held when it was typed);</li>
 * <li>BookModel.State: 1.21.11 passed the animation time and the openness and let the model compute
 *     (sin(time * 0.02) * 0.1 + 1.25) * open; 26.1 passes that product. A state a 1.21.11 constructor made keeps
 *     its time and openness; for one 26.1 made the time is not known, and is answered as 0 (the product's
 *     factor 1.25) so that the two answers still give back 26.1's openness.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallClientRecords {

    private CbWallClientRecords() {
    }

    // ---- submit poses ----

    public static Matrix4fc fc(Matrix4f pose) {
        return pose;
    }

    public static Matrix4f mutable(Matrix4fc pose) {
        if (pose == null || pose instanceof Matrix4f) {
            return (Matrix4f) pose;
        }
        return new Matrix4f(pose);
    }

    // ---- ModelRenderProperties ----

    public static ModelRenderProperties modelRenderProperties(boolean usesBlockLight, TextureAtlasSprite particle,
                                                              ItemTransforms transforms) {
        return new ModelRenderProperties(usesBlockLight, new Material.Baked(particle, false), transforms);
    }

    public static TextureAtlasSprite particleIcon(ModelRenderProperties properties) {
        Material.Baked material = properties.particleMaterial();
        return material == null ? null : material.sprite();
    }

    // ---- CharacterEvent ----

    private static final Map<CharacterEvent, Integer> MODIFIERS = new MapMaker().weakKeys().makeMap();

    public static CharacterEvent characterEvent(int codepoint, int modifiers) {
        CharacterEvent event = new CharacterEvent(codepoint);
        MODIFIERS.put(event, modifiers);
        return event;
    }

    private static boolean down(long window, int left, int right) {
        return GLFW.glfwGetKey(window, left) == GLFW.GLFW_PRESS || GLFW.glfwGetKey(window, right) == GLFW.GLFW_PRESS;
    }

    public static int modifiers(CharacterEvent event) {
        Integer own = MODIFIERS.get(event);
        if (own != null) {
            return own;
        }
        long window = Minecraft.getInstance().getWindow().handle();
        int mods = 0;
        if (down(window, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            mods |= GLFW.GLFW_MOD_SHIFT;
        }
        if (down(window, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL)) {
            mods |= GLFW.GLFW_MOD_CONTROL;
        }
        if (down(window, GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT)) {
            mods |= GLFW.GLFW_MOD_ALT;
        }
        if (down(window, GLFW.GLFW_KEY_LEFT_SUPER, GLFW.GLFW_KEY_RIGHT_SUPER)) {
            mods |= GLFW.GLFW_MOD_SUPER;
        }
        return mods;
    }

    // ---- BookModel.State ----

    private static final Map<BookModel.State, float[]> BOOK = new MapMaker().weakKeys().makeMap();

    /** 1.21.11 new BookModel.State(animationPos, pageFlip1, pageFlip2, open) */
    public static BookModel.State bookState(float animationPos, float pageFlip1, float pageFlip2, float open) {
        float openness = (Mth.sin(animationPos * 0.02F) * 0.1F + 1.25F) * open;
        BookModel.State state = new BookModel.State(openness, pageFlip1, pageFlip2);
        BOOK.put(state, new float[] {animationPos, open});
        return state;
    }

    public static float animationPos(BookModel.State state) {
        float[] own = BOOK.get(state);
        return own != null ? own[0] : 0.0F;
    }

    public static float open(BookModel.State state) {
        float[] own = BOOK.get(state);
        return own != null ? own[1] : state.openness() / 1.25F;
    }
}
