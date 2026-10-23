package top.dext.centurybridge.step.v1_21.facade;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

/**
 * 1.20.6's RealmsServerPlayerLists (net/minecraft/class_4882): the live player list of every Realms server, one
 * RealmsServerPlayerList each. 1.21's class of that name is a map of server id to resolved profiles under another
 * intermediary name; a 1.20.6 mod's references are renamed here (classRenames, s6fin-realms.json). 1.20.6's body.
 */
@Environment(EnvType.CLIENT)
public class class_4882 extends net.minecraft.class_4352 {
    public static final Logger field_22631 = com.mojang.logging.LogUtils.getLogger();
    /** servers */
    public List<class_4881> field_22630;

    public class_4882() {
    }

    /** parse(json): every object of the "lists" array, parsed */
    public static class_4882 method_25075(String json) {
        class_4882 lists = new class_4882();
        lists.field_22630 = Lists.newArrayList();
        try {
            JsonObject object = new JsonParser().parse(json).getAsJsonObject();
            if (object.get("lists").isJsonArray()) {
                for (JsonElement element : object.get("lists").getAsJsonArray()) {
                    lists.field_22630.add(class_4881.method_25074(element.getAsJsonObject()));
                }
            }
        } catch (Exception e) {
            field_22631.error("Could not parse RealmsServerPlayerLists: {}", e.getMessage());
        }
        return lists;
    }
}
