package cb.swallc;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.class_310;

/**
 * cb-swallc: a 1.21.11-era client mod (intermediary names) for the 26.1 wall layer, climbed 1.21.11 -> 26.1.
 * <ul>
 * <li>client check 20 (client_checks/swall.md): TurtleModel and a subclass, DonkeyRenderer(MULE) / UndeadHorseRenderer(ZOMBIE)
 * registered for vanilla's mule and zombie horse, an AbstractScrollArea subclass (scrollbar, scroll rate 9), GuiMessage /
 * GuiMessage.Line built by hand, a Camera subclass's setup(level, player, true, false, pt) and getPartialTickTime</li>
 * <li>the private-method mixin routes (mixin/*): Matrix4f -> Matrix4fc vertex helpers and glyphs, the tint getter of
 * BiomeColors.getAverageColor / LiquidBlockRenderer.calculateAverageHeight; each counts its calls</li>
 * </ul>
 * In a world, the integrated server builds a grass pad with water, a leashed pig, a pig, a saddled mule and zombie horse
 * (mule and zombie horse side-on at the pad's edges) and strikes lightning in view; chat gets an underlined line, a struck line and a player-head glyph. After 160 ticks it
 * prints "CB-SWALLC ok|FAIL check ..." and "CB-SWALLC n/m", waits 12 s for screenshots and stops the client.
 */
public class SwallClient implements ClientModInitializer {
    private static final Map<String, AtomicInteger> HITS = new ConcurrentHashMap<>();
    private static final Map<String, String> RESULTS = new TreeMap<>();
    static volatile int muleRenderers, zombieHorseRenderers;
    private static int ticks;
    private static int bx, by, bz;

    static final String[] ROUTES = {"leash.addVertexPair", "shadow.shadowVertex", "playerGlyph.renderQuad", "lightning.quad",
        "glyph.render", "glyph.renderEffect", "glyph.renderChar", "glyph.buildEffect", "biomeColors.getAverageColor",
        "fluid.calculateAverageHeight", "specialModels.blockMapping"};

    public static void hit(String route) {
        HITS.computeIfAbsent(route, k -> new AtomicInteger()).incrementAndGet();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void onInitializeClient() {
        // check 20 b): 1.21.11's DonkeyRenderer(ctx, Type) / UndeadHorseRenderer(ctx, Type) for vanilla's mule and zombie horse
        EntityRendererRegistry.register((net.minecraft.class_1299) net.minecraft.class_1299.field_6057, ctx -> {
            muleRenderers++;
            return new net.minecraft.class_9991((net.minecraft.class_5617.class_5618) ctx, net.minecraft.class_9991.class_10650.field_56096);
        });
        EntityRendererRegistry.register((net.minecraft.class_1299) net.minecraft.class_1299.field_6048, ctx -> {
            zombieHorseRenderers++;
            return new net.minecraft.class_961((net.minecraft.class_5617.class_5618) ctx, net.minecraft.class_961.class_10651.field_56104);
        });
        ClientTickEvents.END_CLIENT_TICK.register(SwallClient::tick);
    }

    private static void tick(class_310 mc) {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        ticks++;
        try {
            if (ticks == 20) {
                setUp(mc);
            } else if (ticks == 30) {
                Check20.run(mc, RESULTS);
                // check 20 f) against native 26.1: the game's own third-person camera at the same spot
                mc.field_1690.method_31043(net.minecraft.class_5498.field_26665);
            } else if (ticks == 36) {
                Check20.camera(mc, RESULTS);
                mc.field_1690.method_31043(net.minecraft.class_5498.field_26664);
            } else if (ticks == 50) {
                server(mc, "tellraw @a {text:\"cb-swallc underlined line\",underlined:true}");
                server(mc, "tellraw @a [{text:\"cb-swallc head \"},{type:\"object\",object:\"player\",player:{name:\"cbtest\"}}]");
                server(mc, "tellraw @a {text:\"cb-swallc struck\",strikethrough:true}");
            } else if (ticks == 160) {
                report();
            } else if (ticks == 400) {
                class_310.method_1551().method_1592();
            }
            if (ticks >= 40 && ticks <= 140 && ticks % 20 == 0) {
                server(mc, "summon lightning_bolt " + bx + " " + by + " " + (bz + 12));
            }
        } catch (Throwable t) {
            System.out.println("CB-SWALLC FAIL tick" + ticks + " " + t);
            t.printStackTrace();
        }
    }

    private static void setUp(class_310 mc) {
        net.minecraft.class_2338 p = mc.field_1724.method_24515();
        bx = p.method_10263();
        by = p.method_10264();
        bz = p.method_10260();
        String[] cmds = {
            "time set 6000", "weather clear",
            "tp @p " + bx + ".5 " + by + " " + bz + ".5 0 20",
            "fill " + (bx - 4) + " " + (by - 1) + " " + (bz + 2) + " " + (bx + 4) + " " + (by - 1) + " " + (bz + 13) + " grass_block",
            "fill " + (bx - 4) + " " + by + " " + (bz + 2) + " " + (bx + 4) + " " + (by + 5) + " " + (bz + 13) + " air",
            "setblock " + (bx + 2) + " " + (by - 1) + " " + (bz + 5) + " water",
            "setblock " + (bx - 2) + " " + by + " " + (bz + 4) + " oak_fence",
            "summon pig " + (bx - 1) + " " + by + " " + (bz + 5) + " {NoAI:1b,leash:[I;" + (bx - 2) + "," + by + "," + (bz + 4) + "]}",
            "summon pig " + (bx + 1) + " " + by + " " + (bz + 4) + " {NoAI:1b}",
            "summon mule " + (bx - 3) + ".5 " + by + " " + (bz + 7) + ".5 {NoAI:1b,Tame:1b,Rotation:[90f,0f],equipment:{saddle:{id:\"minecraft:saddle\",count:1}}}",
            "summon zombie_horse " + (bx + 3) + ".5 " + by + " " + (bz + 7) + ".5 {NoAI:1b,Tame:1b,Rotation:[270f,0f],equipment:{saddle:{id:\"minecraft:saddle\",count:1}}}",
        };
        for (String c : cmds) {
            server(mc, c);
        }
    }

    private static void server(class_310 mc, String cmd) {
        net.minecraft.server.MinecraftServer s = mc.method_1576();
        s.execute(() -> s.method_3734().method_44252(s.method_3739(), cmd));
    }

    private static void report() {
        int ok = 0, all = 0;
        for (String r : ROUTES) {
            int n = HITS.getOrDefault(r, new AtomicInteger()).get();
            RESULTS.put("route." + r, (n > 0 ? "ok" : "FAIL") + " calls=" + n);
        }
        RESULTS.put("check20.b.muleRenderer", (muleRenderers > 0 ? "ok" : "FAIL") + " created=" + muleRenderers);
        RESULTS.put("check20.b.zombieHorseRenderer", (zombieHorseRenderers > 0 ? "ok" : "FAIL") + " created=" + zombieHorseRenderers);
        for (Map.Entry<String, String> e : RESULTS.entrySet()) {
            all++;
            String[] v = e.getValue().split(" ", 2);
            if (v[0].equals("ok")) ok++;
            System.out.println("CB-SWALLC " + v[0] + " " + e.getKey() + (v.length > 1 ? " " + v[1] : ""));
        }
        System.out.println("CB-SWALLC " + ok + "/" + all);
    }
}
