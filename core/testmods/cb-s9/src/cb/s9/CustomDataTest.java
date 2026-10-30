package cb.s9;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.UUID;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_2487;
import net.minecraft.class_2509;
import net.minecraft.class_3218;
import net.minecraft.class_3730;
import net.minecraft.class_7924;
import net.minecraft.class_9279;
import net.minecraft.server.MinecraftServer;

/** 1.21.8 CustomData: parseEntityId / parseEntityType, loadInto(entity), size / contains / getUnsafe, read / update, CODEC_WITH_ID */
final class CustomDataTest {
    private CustomDataTest() {
    }

    static void run(MinecraftServer server) {
        class_3218 level = server.method_30002();
        S9TestMod.run("custom data: 1.21.8 entity members", () -> {
            class_2487 tag = new class_2487();
            tag.method_10582("id", "minecraft:pig");
            tag.method_10548("Health", 3.0f);
            class_9279 data = class_9279.method_57456(tag);
            S9TestMod.check("minecraft:pig".equals(String.valueOf(data.method_65131())),
                "CustomData.parseEntityId() reads the id, got " + data.method_65131());
            Object type = data.method_65904(level.method_30349(), class_7924.field_41266);
            S9TestMod.check(type == class_1299.field_6093, "CustomData.parseEntityType(registries, ENTITY_TYPE) is the pig type, got " + type);
            class_1297 pig = class_1299.field_6093.method_5883(level, class_3730.values()[0]);
            UUID uuid = pig.method_5667();
            data.method_57445(pig);
            S9TestMod.check(((class_1309) pig).method_6032() == 3.0f && pig.method_5667().equals(uuid),
                "CustomData.loadInto(entity) loads the tag and keeps the entity's UUID, got health " + ((class_1309) pig).method_6032()
                    + " same uuid " + pig.method_5667().equals(uuid));
            S9TestMod.check(data.method_57444() == 2 && data.method_57450("Health") && !data.method_57450("Air")
                    && data.method_57463().method_10545("id"),
                "CustomData.size / contains / getUnsafe, got " + data.method_57444() + " " + data.method_57450("Health"));
            DataResult<Float> health = data.method_57446(Codec.FLOAT.fieldOf("Health"));
            S9TestMod.check(health.result().orElse(-1f) == 3.0f, "CustomData.read(decoder) reads a field, got " + health);
            DataResult<class_9279> updated = data.method_57447(class_2509.field_11560, Codec.INT.fieldOf("Extra"), 7);
            S9TestMod.check(updated.result().map(d -> d.method_57450("Extra") && d.method_57450("Health")).orElse(false),
                "CustomData.update(ops, encoder, value) adds the field to the data, got " + updated);
            class_2487 noId = new class_2487();
            noId.method_10569("x", 1);
            boolean withId = class_9279.field_49304.parse(class_2509.field_11560, tag).result().isPresent();
            boolean without = class_9279.field_49304.parse(class_2509.field_11560, noId).error().isPresent();
            S9TestMod.check(withId && without, "CustomData.CODEC_WITH_ID takes data with an id and refuses data without, got " + withId + " " + without);
        });
    }
}
