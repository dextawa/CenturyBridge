package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's BlockStateModelLoader.LoadedJson (net/minecraft/class_9824$class_7777, classRenames), gone in 1.21.2 (its
 * loader reads parsed LoadedBlockModelDefinitions): one blockstate file's json and the pack it came from, the record
 * 1.21.1's ModelManager handed BlockStateModelLoader per blockstate id. 1.21.1's record (source, data) with its
 * members public (an access widener never reaches a CenturyBridge class), equals / hashCode / toString as a record's.
 */
@Environment(EnvType.CLIENT)
public final class BlockStateLoadedJson {

    /** source */
    public final String comp_1056;

    /** data */
    public final com.google.gson.JsonElement comp_1057;

    public BlockStateLoadedJson(String source, com.google.gson.JsonElement data) {
        this.comp_1056 = source;
        this.comp_1057 = data;
    }

    /** source() */
    public String comp_1056() {
        return this.comp_1056;
    }

    /** data() */
    public com.google.gson.JsonElement comp_1057() {
        return this.comp_1057;
    }

    /** parse(id, context): 1.21.1's BlockModelDefinition.fromJsonElement(context, data), a failure naming the file and pack */
    public net.minecraft.class_790 method_61071(net.minecraft.class_2960 id, BlockModelDefinitionContext context) {
        try {
            return top.dext.centurybridge.step.v1_21_2.rt.CbRenderBlockDefs.method_45787(context, this.comp_1057);
        } catch (Exception e) {
            throw new IllegalStateException(String.format(java.util.Locale.ROOT, "Exception loading blockstate definition: '%s' in resourcepack: '%s': %s",
                id, this.comp_1056, e.getMessage()), e);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BlockStateLoadedJson other && Objects.equals(this.comp_1056, other.comp_1056) && Objects.equals(this.comp_1057, other.comp_1057);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.comp_1056) + Objects.hashCode(this.comp_1057);
    }

    @Override
    public String toString() {
        return "LoadedJson[source=" + this.comp_1056 + ", data=" + this.comp_1057 + "]";
    }
}
