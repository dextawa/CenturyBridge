package top.dext.centurybridge.step.v1_21_2.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.facade.BlockModelDefinitionContext;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBlockDefs;

/**
 * 1.21.1 MultiPart.Deserializer(context): a deserializer a 1.21.1 mod built keeps its context, and its Gson-facing
 * deserialize answers 1.21.1's MultiPart built against the context's definition (a 1.21.1 Gson asks it for
 * MultiPart.class); one 1.21.2 built (no context) answers 1.21.2's Definition unchanged. See CbRenderBlockDefs.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_816.class_817.class)
public abstract class CbRenderBlockDefDeserializerMixin implements CbRenderBlockDefs.Deser {

    @Unique
    private BlockModelDefinitionContext cb$context;

    @Shadow
    private List<net.minecraft.class_819> method_3522(JsonDeserializationContext context, JsonArray json) {
        throw new AssertionError();
    }

    @Override
    public BlockModelDefinitionContext cb$context() {
        return this.cb$context;
    }

    @Override
    public void cb$setContext(BlockModelDefinitionContext context) {
        this.cb$context = context;
    }

    @Override
    public List<net.minecraft.class_819> cb$getSelectors(JsonDeserializationContext context, JsonArray json) {
        return this.method_3522(context, json);
    }

    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Ljava/lang/Object;",
        at = @At("RETURN"), cancellable = true)
    private void cb$legacyAnswer(JsonElement json, Type type, JsonDeserializationContext context, CallbackInfoReturnable<Object> cir) {
        Object answer = CbRenderBlockDefs.deserialized((net.minecraft.class_816.class_817) (Object) this, cir.getReturnValue());
        if (answer != cir.getReturnValue()) {
            cir.setReturnValue(answer);
        }
    }
}
