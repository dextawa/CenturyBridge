package top.dext.centurybridge.step.v1_21_6.facade.fapi;

/**
 * Fabric API 1.21.5's IdentifiedLayer (fabric-rendering-v1), which 1.21.6's Fabric API replaced with HudElementRegistry /
 * HudElement / VanillaHudElements. The ids are 1.21.5's (minecraft:&lt;name&gt;); 1.21.6's VanillaHudElements keeps every
 * one of them under the same id except hotbar_and_bars, which 1.21.6 splits into its pieces (see CbHudLayers).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface IdentifiedLayer extends top.dext.centurybridge.step.v1_21_6.facade.LayeredDraw.Layer {

    net.minecraft.class_2960 MISC_OVERLAYS = net.minecraft.class_2960.method_60656("misc_overlays");
    net.minecraft.class_2960 CROSSHAIR = net.minecraft.class_2960.method_60656("crosshair");
    net.minecraft.class_2960 HOTBAR_AND_BARS = net.minecraft.class_2960.method_60656("hotbar_and_bars");
    net.minecraft.class_2960 EXPERIENCE_LEVEL = net.minecraft.class_2960.method_60656("experience_level");
    net.minecraft.class_2960 STATUS_EFFECTS = net.minecraft.class_2960.method_60656("status_effects");
    net.minecraft.class_2960 BOSS_BAR = net.minecraft.class_2960.method_60656("boss_bar");
    net.minecraft.class_2960 SLEEP = net.minecraft.class_2960.method_60656("sleep");
    net.minecraft.class_2960 DEMO_TIMER = net.minecraft.class_2960.method_60656("demo_timer");
    net.minecraft.class_2960 DEBUG = net.minecraft.class_2960.method_60656("debug");
    net.minecraft.class_2960 SCOREBOARD = net.minecraft.class_2960.method_60656("scoreboard");
    net.minecraft.class_2960 OVERLAY_MESSAGE = net.minecraft.class_2960.method_60656("overlay_message");
    net.minecraft.class_2960 TITLE_AND_SUBTITLE = net.minecraft.class_2960.method_60656("title_and_subtitle");
    net.minecraft.class_2960 CHAT = net.minecraft.class_2960.method_60656("chat");
    net.minecraft.class_2960 PLAYER_LIST = net.minecraft.class_2960.method_60656("player_list");
    net.minecraft.class_2960 SUBTITLES = net.minecraft.class_2960.method_60656("subtitles");

    net.minecraft.class_2960 id();

    /** 1.21.5's of: a layer with that id drawing the given layer (Fabric's WrappedLayer) */
    static IdentifiedLayer of(net.minecraft.class_2960 id, top.dext.centurybridge.step.v1_21_6.facade.LayeredDraw.Layer layer) {
        return new IdentifiedLayer() {
            @Override
            public net.minecraft.class_2960 id() {
                return id;
            }

            @Override
            public void render(net.minecraft.class_332 graphics, net.minecraft.class_9779 delta) {
                layer.render(graphics, delta);
            }
        };
    }
}
