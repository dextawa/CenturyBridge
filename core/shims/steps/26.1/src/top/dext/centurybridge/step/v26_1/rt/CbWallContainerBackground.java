package top.dext.centurybridge.step.v26_1.rt;

import java.util.IdentityHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

/**
 * 1.21.11's AbstractContainerScreen.renderBg(graphics, partialTick, mouseX, mouseY) on 26.1.
 * <p>
 * 1.21.11: Screen.renderBackground drew the screen backdrop (blur / menu background, or the in-game dim);
 * AbstractContainerScreen's override then called the abstract renderBg, where each container screen drew its
 * texture. 26.1 dropped renderBg: a container screen overrides extractBackground, calls super (the backdrop) and
 * draws its texture after it. So a 1.21.11 screen's renderBg is called after the backdrop
 * (mixin/CbWallContainerScreenBgMixin): by AbstractContainerScreen's extractBackground when no 26.1 screen class
 * between overrides it, else right after the screen's whole extractBackground (mixin/CbWallScreenContainerBgMixin)
 * -- the vanilla screen's texture first, then the mod's renderBg over it. renderBg's own body on the vanilla class
 * (what a mod's super.renderBg reaches) is the vanilla screen's texture: its extractBackground run with the
 * backdrop left out.
 */
@Environment(EnvType.CLIENT)
public final class CbWallContainerBackground {

    private CbWallContainerBackground() {
    }

    /** implemented on AbstractContainerScreen by mixin/CbWallContainerScreenBgMixin */
    public interface Screen {
        void cb$renderBg(GuiGraphicsExtractor graphics, float partialTick, int mouseX, int mouseY);
    }

    private static final ThreadLocal<Map<Object, Integer>> TEXTURE_ONLY = ThreadLocal.withInitial(IdentityHashMap::new);

    /** whether the screen's class (a 1.21.11 one) declares renderBg below AbstractContainerScreen */
    public static boolean overridesRenderBg(Object screen) {
        return CbWallLegacySplit.overrides(screen.getClass(), AbstractContainerScreen.class, "renderBg",
                GuiGraphicsExtractor.class, float.class, int.class, int.class);
    }

    private static final ClassValue<Boolean> VANILLA_BELOW = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> c = type; c != null && c != AbstractContainerScreen.class; c = c.getSuperclass()) {
                if (!c.getName().startsWith("net.minecraft.")) {
                    continue;
                }
                try {
                    c.getDeclaredMethod("extractBackground", GuiGraphicsExtractor.class, int.class, int.class, float.class);
                    return true;
                } catch (NoSuchMethodException e) {
                    // not on this class
                }
            }
            return false;
        }
    };

    /**
     * whether a 26.1 (Minecraft) screen class below AbstractContainerScreen overrides extractBackground -- a mod's own
     * override (its 1.21.11 renderBackground) reaches AbstractContainerScreen's through super, as 1.21.11's did
     */
    public static boolean vanillaBackgroundBelow(Object screen) {
        return VANILLA_BELOW.get(screen.getClass());
    }

    /** inside a renderBg super call: AbstractContainerScreen's extractBackground draws no backdrop */
    public static boolean textureOnly(Object screen) {
        return TEXTURE_ONLY.get().containsKey(screen);
    }

    /** vanilla renderBg: the vanilla screen's extractBackground without the backdrop */
    public static void vanillaTexture(AbstractContainerScreen<?> screen, GuiGraphicsExtractor graphics, float partialTick,
                                      int mouseX, int mouseY) {
        Map<Object, Integer> m = TEXTURE_ONLY.get();
        m.merge(screen, 1, Integer::sum);
        try {
            screen.extractBackground(graphics, mouseX, mouseY, partialTick);
        } finally {
            Integer n = m.get(screen);
            if (n == null || n <= 1) {
                m.remove(screen);
            } else {
                m.put(screen, n - 1);
            }
        }
    }
}
