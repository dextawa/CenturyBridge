package top.dext.centurybridge.compat.fapi;

import java.io.IOException;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.Identifier;

/** was CoreShaderRegistrationCallback.RegistrationContext; never handed out on 26.2 (see CbCoreShaderRegistrationCallback) */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbCoreShaderRegistrationContext {
    @SuppressWarnings("rawtypes")
    void register(Identifier id, VertexFormat vertexFormat, Consumer loadCallback) throws IOException;
}
