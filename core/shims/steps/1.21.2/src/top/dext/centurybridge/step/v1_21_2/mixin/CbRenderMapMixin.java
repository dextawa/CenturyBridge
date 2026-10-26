package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 MapRenderer's own map textures (update / render / resetData / close), which 1.21.2 moved to MapTextureManager
 * (the renderer keeps one): update(id, data) is the texture manager's update (1.21.1: the map instance's forceUpload,
 * the same flag); render(pose, buffers, id, data, active, light) is 1.21.2's extraction of the map into a render state
 * and its render of that state (the same texture quad and decorations); resetData / close release the textures
 * (1.21.1's close was its resetData).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_330.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderMapMixin {

    @Shadow
    @Final
    private net.minecraft.class_10093 field_2043;

    public void method_1769(net.minecraft.class_9209 id, net.minecraft.class_22 data) {
        this.field_2043.method_62622(id, data);
    }

    public void method_1773(net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, net.minecraft.class_9209 id, net.minecraft.class_22 data,
                            boolean active, int light) {
        net.minecraft.class_10090 state = new net.minecraft.class_10090();
        net.minecraft.class_330 self = (net.minecraft.class_330) (Object) this;
        self.method_62230(id, data, state);
        self.method_1773(state, pose, buffers, active, light);
    }

    public void method_1771() {
        this.field_2043.method_62621();
    }

    public void close() {
        this.method_1771();
    }
}
