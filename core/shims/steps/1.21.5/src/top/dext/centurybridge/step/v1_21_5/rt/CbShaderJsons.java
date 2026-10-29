package top.dext.centurybridge.step.v1_21_5.rt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/**
 * 1.21.4's core shader program descriptions (assets/minecraft/shaders/core/&lt;name&gt;.json, 56 files) on 1.21.5, which ships
 * none: 1.21.5 describes each program in code, as a RenderPipeline (class_10799's constants: vertex and fragment shader,
 * samplers, shader defines, uniforms), and keeps only the .vsh / .fsh sources. 1.21.4-era code that reads a core shader
 * json through a ResourceProvider -- the climbed 1.21.2 layer's 1.21.1 ShaderInstance give-back (CbRenderShaderLoad, run
 * by the 1.21 layer's CbRenderShaders.cbShadersLoaded for position_tex_color on every resource reload) -- reaches
 * ResourceProvider.openAsReader (interfaceCallRedirects).
 *
 * <p>{@link #openAsReader} is W's call first: whatever the provider holds (a resource pack's json, a mod's own) is what is
 * read, and every other resource is untouched. Only when the provider has no minecraft:shaders/core/&lt;name&gt;.json on a
 * client is the 1.21.4 description built from the 1.21.5 pipeline of that program ({@link CbShaderJsonsClient}); a name
 * no 1.21.5 pipeline describes stays the FileNotFoundException W throws.
 */
public final class CbShaderJsons {
    private CbShaderJsons() {
    }

    /** ResourceProvider.openAsReader(location) */
    public static BufferedReader openAsReader(net.minecraft.class_5912 provider, net.minecraft.class_2960 location) throws IOException {
        try {
            return provider.openAsReader(location);
        } catch (FileNotFoundException e) {
            String path = location.method_12832();
            if (!"minecraft".equals(location.method_12836()) || !path.startsWith("shaders/core/") || !path.endsWith(".json")
                || path.indexOf('/', "shaders/core/".length()) >= 0
                || FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
                throw e;
            }
            String json = CbShaderJsonsClient.describe(path.substring("shaders/core/".length(), path.length() - ".json".length()));
            if (json == null) {
                throw e;
            }
            return new BufferedReader(new StringReader(json));
        }
    }
}
