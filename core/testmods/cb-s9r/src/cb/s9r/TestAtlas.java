package cb.s9r;

import net.minecraft.class_1058;
import net.minecraft.class_1060;
import net.minecraft.class_2960;
import net.minecraft.class_4075;

/** a 1.21.8 mod's own TextureAtlasHolder: atlas cb_s9r:textures/atlas/test.png from assets/cb_s9r/atlases/test.json */
public class TestAtlas extends class_4075 {
    public TestAtlas(class_1060 textureManager) {
        super(textureManager, class_2960.method_60655("cb_s9r", "textures/atlas/test.png"), class_2960.method_60655("cb_s9r", "test"));
    }

    public class_1058 sprite(class_2960 id) {
        return this.method_18667(id);
    }
}
