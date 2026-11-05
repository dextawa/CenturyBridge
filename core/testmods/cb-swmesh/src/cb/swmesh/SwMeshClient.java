package cb.swmesh;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_5607;
import net.minecraft.class_630;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * cb-swmesh: a 1.21.11-era client mod (intermediary names) dumping the part trees of vanilla 1.21.11 layer definitions a
 * mod builds by calling the model statics: RabbitModel.createBodyLayer(false/true), ArmadilloModel.createBodyLayer(),
 * DonkeyModel.createBabyLayer(0.87), DonkeyModel.createSaddleLayer(0.87, true), PolarBearModel.createBodyLayer(true).
 * Each layer is baked (LayerDefinition.bakeRoot) and visited with ModelPart.visit from an identity pose: per cube its
 * path, bounds and every vertex position (posed, in blocks, 4 decimals) and UV. Prints "CB-SWMESH part ..." lines, a
 * per-model "CB-SWMESH model <name> parts <n> cubes <c> sha <hex>" line, then "CB-SWMESH n/m" (models baked) and
 * stops the client. Run natively on 1.21.11 for the reference and climbed to 26.1; the dumps must be identical.
 */
public class SwMeshClient implements ClientModInitializer {
    private static int ticks;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(SwMeshClient::tick);
    }

    private static void tick(class_310 mc) {
        ticks++;
        if (ticks == 40) {
            run();
        } else if (ticks == 60) {
            mc.method_1592();
        }
    }

    private static void run() {
        List<Object[]> models = new ArrayList<>();
        models.add(new Object[] {"rabbit.adult", (Supplier<class_5607>) () -> net.minecraft.class_596.method_32034(false)});
        models.add(new Object[] {"rabbit.baby", (Supplier<class_5607>) () -> net.minecraft.class_596.method_32034(true)});
        models.add(new Object[] {"armadillo", (Supplier<class_5607>) net.minecraft.class_9082::method_55818});
        models.add(new Object[] {"donkey.baby", (Supplier<class_5607>) () -> net.minecraft.class_9945.method_62080(0.87F)});
        models.add(new Object[] {"donkey.saddle.baby", (Supplier<class_5607>) () -> net.minecraft.class_9945.method_66838(0.87F, true)});
        models.add(new Object[] {"polarbear.baby", (Supplier<class_5607>) () -> net.minecraft.class_590.method_32029(true)});
        int ok = 0;
        for (Object[] m : models) {
            String name = (String) m[0];
            try {
                @SuppressWarnings("unchecked")
                class_630 root = ((Supplier<class_5607>) m[1]).get().method_32109();
                StringBuilder all = new StringBuilder();
                int[] cubes = {0};
                root.method_35745(new class_4587(), (pose, path, index, cube) -> {
                    Matrix4f mat = pose.method_23761();
                    StringBuilder sb = new StringBuilder();
                    sb.append(path).append('#').append(index).append(String.format(Locale.ROOT, " box %.4f %.4f %.4f %.4f %.4f %.4f",
                        cube.field_3645, cube.field_3644, cube.field_3643, cube.field_3648, cube.field_3647, cube.field_3646));
                    for (var poly : cube.field_3649) {
                        sb.append(" |");
                        for (var v : poly.comp_3184()) {
                            Vector3f p = mat.transformPosition(new Vector3f(v.comp_4804() / 16.0F, v.comp_4805() / 16.0F, v.comp_4806() / 16.0F));
                            sb.append(String.format(Locale.ROOT, " %.4f,%.4f,%.4f/%.4f,%.4f", p.x + 0.0F, p.y + 0.0F, p.z + 0.0F, v.comp_3187(), v.comp_3188()));
                        }
                    }
                    cubes[0]++;
                    all.append(sb).append('\n');
                    System.out.println("CB-SWMESH part " + name + " " + sb);
                });
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] h = md.digest(all.toString().getBytes(StandardCharsets.UTF_8));
                StringBuilder hex = new StringBuilder();
                for (int i = 0; i < 8; i++) hex.append(String.format("%02x", h[i]));
                System.out.println("CB-SWMESH model " + name + " parts " + root.method_32088().size() + " cubes " + cubes[0] + " sha " + hex);
                ok++;
            } catch (Throwable t) {
                System.out.println("CB-SWMESH FAIL " + name + " " + t);
                t.printStackTrace(System.out);
            }
        }
        System.out.println("CB-SWMESH " + ok + "/" + models.size());
    }
}
