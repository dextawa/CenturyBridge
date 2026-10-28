package cb.s9r;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_20;
import net.minecraft.class_21;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3302;
import net.minecraft.class_4724;

/**
 * 1.21.8 atlas holders: what Minecraft's getters answer (paintings, map decorations, gui sprites and their scaling,
 * getTextureAtlas), and a mod's own TextureAtlasHolder reloaded through 1.21.8's reload(barrier, manager, background,
 * game). Read by the native driver through reflection, which compares each with 1.21.9's AtlasManager.
 */
public final class AtlasCheck {
    private AtlasCheck() {
    }

    /** on the render thread */
    public static Map<String, Object> holders() {
        class_310 mc = class_310.method_1551();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("paintings.back", mc.method_18321().method_18342());
        out.put("map.player", mc.method_58476().method_58516(new class_20(class_21.field_91, (byte) 0, (byte) 0, (byte) 0, Optional.empty())));
        class_1058 heart = mc.method_52699().method_18667(class_2960.method_60656("hud/heart/full"));
        out.put("gui.heart", heart);
        class_1058 button = mc.method_52699().method_18667(class_2960.method_60656("widget/button"));
        out.put("gui.button", button);
        out.put("gui.button.scaling", mc.method_52699().method_52714(button));
        out.put("blocks.stone", mc.method_1549(class_1059.field_5275).apply(class_2960.method_60656("block/stone")));
        out.put("same.holder", mc.method_18321() == mc.method_18321());
        out.put("models.blocks", mc.method_1554().method_24153(class_1059.field_5275));
        return out;
    }

    /** off the render thread: a mod's own holder, reloaded the 1.21.8 way */
    public static class_1058 ownAtlas() throws Exception {
        class_310 mc = class_310.method_1551();
        TestAtlas atlas = CompletableFuture.supplyAsync(() -> new TestAtlas(mc.method_1531()), mc).get(20, TimeUnit.SECONDS);
        class_3302.class_4045 barrier = new class_3302.class_4045() {
            @Override
            public <T> CompletableFuture<T> method_18352(T value) {
                return CompletableFuture.completedFuture(value);
            }
        };
        atlas.method_25931(barrier, mc.method_1478(), ForkJoinPool.commonPool(), mc).get(30, TimeUnit.SECONDS);
        return atlas.sprite(class_2960.method_60656("block/stone"));
    }

    /** off the render thread: a mod's own AtlasSet, stitched and uploaded the 1.21.8 way; null when atlas and result disagree */
    public static class_1058 ownAtlasSet() throws Exception {
        class_310 mc = class_310.method_1551();
        class_2960 location = class_2960.method_60655("cb_s9r", "textures/atlas/test2.png");
        class_4724 set = CompletableFuture.supplyAsync(() -> new class_4724(Map.of(location, class_2960.method_60655("cb_s9r", "test")),
            mc.method_1531()), mc).get(20, TimeUnit.SECONDS);
        class_4724.class_7774 result = set.method_45862(mc.method_1478(), 0, ForkJoinPool.commonPool()).get(location).get(30, TimeUnit.SECONDS);
        result.method_45870().get(30, TimeUnit.SECONDS);
        CompletableFuture.runAsync(result::method_45871, mc).get(20, TimeUnit.SECONDS);
        class_1058 sprite = result.method_45869(class_2960.method_60656("block/stone"));
        return set.method_24098(location).method_4608(class_2960.method_60656("block/stone")) == sprite ? sprite : null;
    }
}
