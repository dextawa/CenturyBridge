package top.dext.centurybridge.facade.v1_20_2;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * Facade for 1.20.1 net/minecraft/class_47$class_50$class_51 (LootContext.EntityTarget.Serializer),
 * a Gson TypeAdapter deleted in 1.20.2 (EntityTarget became a StringIdentifiable with a codec).
 * The 1.20.1 bodies are ported: the target is written as its name and read back by name
 * (EntityTarget.fromString, which 1.20.2 still has). 1.20.1 read the private name field; 1.20.2's
 * asString() returns that field.
 */
public class class_47$class_50$class_51 extends com.google.gson.TypeAdapter<net.minecraft.class_47.class_50> {

    public class_47$class_50$class_51() {
    }

    /** write(out, target) */
    public void method_318(JsonWriter out, net.minecraft.class_47.class_50 target) throws IOException {
        out.value(target.method_15434());
    }

    /** read(in) */
    public net.minecraft.class_47.class_50 method_317(JsonReader in) throws IOException {
        return net.minecraft.class_47.class_50.method_314(in.nextString());
    }

    @Override
    public net.minecraft.class_47.class_50 read(JsonReader in) throws IOException {
        return method_317(in);
    }

    @Override
    public void write(JsonWriter out, net.minecraft.class_47.class_50 target) throws IOException {
        method_318(out, target);
    }
}
