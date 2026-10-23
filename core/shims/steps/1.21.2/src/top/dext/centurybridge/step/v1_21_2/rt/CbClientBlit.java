package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_332;

/**
 * 1.21.1's GuiGraphics blit / blitSprite with a blitOffset, overridden by a GuiGraphics subclass, on 1.21.2.
 *
 * <p>1.21.1's blit(texture, x, y, blitOffset, u, v, width, height, textureWidth, textureHeight) and
 * blitSprite(sprite, x, y, blitOffset, width, height) drew at depth blitOffset; 1.21.2's take the render type
 * first and a colour last, and have no depth (bytecode: 1.21.1 hands x, y, blitOffset, width, height on in that
 * order, 1.21.2 x, y, width, height, colour -- the same number of ints, not the same values, which is why
 * pairing them by position gave the old override the width as its blitOffset). The override hooks (the
 * s4client-blit wiring fragment) map every argument by what it is:
 *
 * <ul>
 * <li>1.21.2 reaching a 1.21.1 override: the old method gets x, y, u, v, width, height and the texture size from
 *     vanilla's call, and blitOffset 0 -- what 1.21.1's own blit and blitSprite overloads without a blitOffset
 *     passed (blitSprite(sprite, x, y, w, h) and blit(texture, x, y, u, v, w, h, tw, th) both call through with
 *     0). Vanilla's render type and colour are kept in the frame for the override's super call.</li>
 * <li>the override's super call: 1.21.2's shape with the render type and colour vanilla passed (outside a call
 *     vanilla started: 1.21.2's GUI texture type, and white, -1, what 1.21.2's own colour-less overloads pass),
 *     drawn at the blitOffset the override gave, as 1.21.1 placed its vertices at that depth: the pose is
 *     translated by it around the call.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbClientBlit {

    private CbClientBlit() {
    }

    /** 1.21.1's blitOffset where its offset-less overloads called through */
    public static final int BLIT_OFFSET = 0;

    /** 1.21.2's colour where its colour-less overloads call through: white */
    public static final int COLOR = -1;

    // ------------------------------------------------------------------ blit(texture, x, y, blitOffset, u, v, w, h, tw, th)

    /** the 1.21.1 shape a GuiGraphics subclass overrides (adapter of the override hook) */
    public interface LegacyBlit {
        void method_25291(class_2960 texture, int x, int y, int blitOffset, float u, float v, int width, int height,
                          int textureWidth, int textureHeight);
    }

    /** super's 1.21.2 shape, reached from such a subclass's super call */
    public interface SuperBlit {
        void cb$super$method_25291(Function<class_2960, class_1921> renderType, class_2960 texture, int x, int y, float u, float v,
                                   int width, int height, int textureWidth, int textureHeight, int color);
    }

    /** 1.21.2's blit(renderType, texture, x, y, u, v, w, h, tw, th, colour) of a 1.21.1 override */
    public static void blit(LegacyBlit self, Function<class_2960, class_1921> renderType, class_2960 texture, int x, int y, float u,
                            float v, int width, int height, int textureWidth, int textureHeight, int color) {
        Guard.cbPush(self, new Object[] {renderType, color});
        try {
            self.method_25291(texture, x, y, BLIT_OFFSET, u, v, width, height, textureWidth, textureHeight);
        } finally {
            Guard.cbPop();
        }
    }

    /** a 1.21.1 override's super.blit(texture, x, y, blitOffset, u, v, w, h, tw, th) */
    @SuppressWarnings("unchecked")
    public static void superBlit(SuperBlit self, class_2960 texture, int x, int y, int blitOffset, float u, float v, int width, int height,
                                 int textureWidth, int textureHeight) {
        Function<class_2960, class_1921> renderType =
            (Function<class_2960, class_1921>) Guard.cbArgOrL(self, 0, CbRestGuiClient.guiTextured());
        int color = Guard.cbArgOrI(self, 1, COLOR);
        atDepth((class_332) self, blitOffset,
            () -> self.cb$super$method_25291(renderType, texture, x, y, u, v, width, height, textureWidth, textureHeight, color));
    }

    // ------------------------------------------------------------------ blitSprite(sprite, x, y, blitOffset, w, h)

    public interface LegacySprite {
        void method_52707(class_2960 sprite, int x, int y, int blitOffset, int width, int height);
    }

    public interface SuperSprite {
        void cb$super$method_52707(Function<class_2960, class_1921> renderType, class_2960 sprite, int x, int y, int width, int height,
                                   int color);
    }

    /** 1.21.2's blitSprite(renderType, sprite, x, y, w, h, colour) of a 1.21.1 override */
    public static void blitSprite(LegacySprite self, Function<class_2960, class_1921> renderType, class_2960 sprite, int x, int y,
                                  int width, int height, int color) {
        Guard.cbPush(self, new Object[] {renderType, color});
        try {
            self.method_52707(sprite, x, y, BLIT_OFFSET, width, height);
        } finally {
            Guard.cbPop();
        }
    }

    /** a 1.21.1 override's super.blitSprite(sprite, x, y, blitOffset, w, h) */
    @SuppressWarnings("unchecked")
    public static void superBlitSprite(SuperSprite self, class_2960 sprite, int x, int y, int blitOffset, int width, int height) {
        Function<class_2960, class_1921> renderType =
            (Function<class_2960, class_1921>) Guard.cbArgOrL(self, 0, CbRestGuiClient.guiTextured());
        int color = Guard.cbArgOrI(self, 1, COLOR);
        atDepth((class_332) self, blitOffset, () -> self.cb$super$method_52707(renderType, sprite, x, y, width, height, color));
    }

    /** the draw at depth blitOffset: 1.21.1 put its vertices there, 1.21.2 draws at the pose's depth */
    private static void atDepth(class_332 graphics, int blitOffset, Runnable draw) {
        if (blitOffset == 0) {
            draw.run();
            return;
        }
        graphics.method_51448().method_22903();
        graphics.method_51448().method_46416(0.0F, 0.0F, (float) blitOffset);
        try {
            draw.run();
        } finally {
            graphics.method_51448().method_22909();
        }
    }
}
