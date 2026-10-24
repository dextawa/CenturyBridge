package top.dext.centurybridge.step.v1_20_5.mixin;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import java.util.Locale;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * AddNewChoices: 1.20.4's protected cap(name, inputType, outputType), back for a subclass's call
 * (1.20.5 made it private and takes the name from the fix). 1.20.4's body: the choice types must
 * share their key type, and every value is passed on unchanged, failing on a tag the output type
 * does not know.
 */
@Mixin(net.minecraft.class_3553.class)
public abstract class CbFinAddNewChoicesMixin extends DataFix {

    @Shadow
    @Final
    private DSL.TypeReference field_15780;

    private CbFinAddNewChoicesMixin(Schema schema, boolean changesType) {
        super(schema, changesType);
    }

    /** was cap(name, inputType, outputType) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final <K> TypeRewriteRule method_15476(String name, TaggedChoice.TaggedChoiceType<K> inputType,
            TaggedChoice.TaggedChoiceType<?> outputType) {
        if (inputType.getKeyType() != outputType.getKeyType()) {
            throw new IllegalStateException("Could not inject: key type is not the same");
        }
        TaggedChoice.TaggedChoiceType<K> out = (TaggedChoice.TaggedChoiceType<K>) outputType;
        return this.fixTypeEverywhere(name, (Type) inputType, (Type) out, ops -> value -> {
            Pair<K, ?> pair = (Pair<K, ?>) value;
            if (!out.hasType(pair.getFirst())) {
                throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown type %s in %s", pair.getFirst(), this.field_15780));
            }
            return pair;
        });
    }
}
