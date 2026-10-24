package top.dext.centurybridge.step.v1_21_9.facade;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import top.dext.centurybridge.step.v1_21_9.rt.CbLibAuthlib;

/**
 * authlib 6.0.58's GameProfile.Serializer (gone in 7.0.61), its bodies as 6.0.58 had them: {"id": UUID via the
 * context, "name": string}, each written only when present; a read profile gets its own mutable property map.
 */
public class AuthlibGameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {
    public AuthlibGameProfileSerializer() {
    }

    @Override
    public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = (JsonObject) json;
        UUID id = (UUID) context.deserialize(object.get("id"), UUID.class);
        String name = object.getAsJsonPrimitive("name").getAsString();
        return CbLibAuthlib.gameProfile(id, name);
    }

    @Override
    public JsonElement serialize(GameProfile profile, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        if (profile.id() != null) {
            object.add("id", context.serialize(profile.id()));
        }
        if (profile.name() != null) {
            object.addProperty("name", profile.name());
        }
        return object;
    }
}
