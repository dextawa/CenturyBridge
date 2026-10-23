package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Lighting's level and nether light directions, for 1.20.4's setupLevel(matrix) / setupNetherLevel(matrix) (rt/CbClientRender) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_308.class)
public interface CbClientLightingAccessor {

    /** DIFFUSE_LIGHT_0 */
    @Accessor("field_24428")
    static org.joml.Vector3f cb$level0() {
        throw new AssertionError();
    }

    /** DIFFUSE_LIGHT_1 */
    @Accessor("field_24429")
    static org.joml.Vector3f cb$level1() {
        throw new AssertionError();
    }

    /** NETHER_DIFFUSE_LIGHT_0 */
    @Accessor("field_24426")
    static org.joml.Vector3f cb$nether0() {
        throw new AssertionError();
    }

    /** NETHER_DIFFUSE_LIGHT_1 */
    @Accessor("field_24427")
    static org.joml.Vector3f cb$nether1() {
        throw new AssertionError();
    }
}
