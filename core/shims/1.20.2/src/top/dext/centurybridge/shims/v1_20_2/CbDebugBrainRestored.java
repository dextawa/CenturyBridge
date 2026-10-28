package top.dext.centurybridge.shims.v1_20_2;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_11;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1646;
import net.minecraft.class_2338;
import net.minecraft.class_2540;
import net.minecraft.class_3544;
import net.minecraft.class_4095;
import net.minecraft.class_4139;
import net.minecraft.class_4140;
import net.minecraft.class_4168;
import net.minecraft.class_4208;
import net.minecraft.class_4209;
import net.minecraft.class_4617;
import net.minecraft.class_6067;
import net.minecraft.class_7260;
import net.minecraft.class_7893;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * DebugPackets.writeBrain(LivingEntity, FriendlyByteBuf) (method_36158) and the writers it hands to the buffer
 * (method_43893 path, method_36164 activity, method_36165 truncated memory line, method_36161 / method_36160 gossip
 * lines), restored on DebugPackets as private statics with 1.20.1's bodies.
 *
 * 1.20.2 removed them: the brain dump became BrainDebugPayload.BrainDump, a record a sender fills in itself and that
 * writes its own fields. Whole-jar evidence (javap of client-1.20.2-intermediary-code.jar): nothing outside Brain calls
 * Brain.getActiveActivities (method_35059) and nothing outside Warden calls getClientAngerLevel (method_42222); W's
 * DebugPackets keeps only the memory descriptions (method_36157, used here as 1.20.1 did) and its sendEntityBrain is
 * empty. In 1.20.1 vanilla never called writeBrain either (its only caller, sendEntityBrain, was already empty), so a
 * mod's mixin on it acted only where a mod called it through an @Invoker -- the same here: vanilla's path is not
 * involved, no route is needed.
 */
@Mixin(value = class_4209.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbDebugBrainRestored {

    @Shadow
    private static List<String> method_36157(class_1309 entity, long time) {
        throw new AssertionError();
    }

    private static void method_36158(class_1309 entity, class_2540 buf) {
        class_4095<?> brain = entity.method_18868();
        long time = entity.method_37908().method_8510();
        if (entity instanceof class_6067 carrier) {
            // 1.20.1 holds it as a Container: isEmpty is an interface call there (an anchor mods name)
            net.minecraft.class_1263 inventory = carrier.method_35199();
            // Object.toString as 1.20.1 calls it (an anchor): javac now names an interface receiver's toString on the interface
            buf.method_10814(inventory.method_5442() ? "" : ((Object) inventory).toString());
        } else {
            buf.method_10814("");
        }
        buf.method_37435(brain.method_18896(class_4140.field_18449) ? brain.method_18904(class_4140.field_18449) : Optional.empty(),
            CbDebugBrainRestored::method_43893);
        if (entity instanceof class_1646 villager) {
            boolean wantsGolem = villager.method_20687(time);
            buf.writeBoolean(wantsGolem);
        } else {
            buf.writeBoolean(false);
        }
        if (entity.method_5864() == class_1299.field_38095) {
            class_7260 warden = (class_7260) entity;
            buf.writeInt(warden.method_42222());
        } else {
            buf.writeInt(-1);
        }
        buf.method_34062(brain.method_35059(), CbDebugBrainRestored::method_36164);
        Set<String> behaviors = brain.method_27074().stream().map(class_7893::method_46910).collect(Collectors.toSet());
        buf.method_34062(behaviors, class_2540::method_10814);
        buf.method_34062(method_36157(entity, time), CbDebugBrainRestored::method_36165);
        if (entity instanceof class_1646) {
            Set<class_2338> pois = Stream.of(class_4140.field_18439, class_4140.field_18438, class_4140.field_18440)
                .map(brain::method_18904).flatMap(Optional::stream).map(class_4208::method_19446).collect(Collectors.toSet());
            buf.method_34062(pois, class_2540::method_10807);
        } else {
            buf.method_10804(0);
        }
        if (entity instanceof class_1646) {
            Set<class_2338> potential = Stream.of(class_4140.field_25160)
                .map(brain::method_18904).flatMap(Optional::stream).map(class_4208::method_19446).collect(Collectors.toSet());
            buf.method_34062(potential, class_2540::method_10807);
        } else {
            buf.method_10804(0);
        }
        if (entity instanceof class_1646 villager) {
            Map<UUID, Object2IntMap<class_4139>> gossips = villager.method_21651().method_35120();
            List<String> lines = Lists.newArrayList();
            gossips.forEach((uuid, values) -> method_36161(lines, uuid, values));
            buf.method_34062(lines, class_2540::method_10814);
        } else {
            buf.method_10804(0);
        }
    }

    private static void method_43893(class_2540 buf, class_11 path) {
        path.method_35498(buf);
    }

    private static void method_36164(class_2540 buf, class_4168 activity) {
        buf.method_10814(activity.method_19634());
    }

    private static void method_36165(class_2540 buf, String line) {
        String truncated = class_3544.method_34963(line, 255, true);
        buf.method_10814(truncated);
    }

    private static void method_36161(List<String> lines, UUID uuid, Object2IntMap<class_4139> values) {
        String name = class_4617.method_23267(uuid);
        values.forEach((type, value) -> method_36160(lines, name, type, value));
    }

    private static void method_36160(List<String> lines, String name, class_4139 type, Integer value) {
        lines.add(name + ": " + type + ": " + value);
    }
}
