package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.List;
import net.minecraft.class_2922;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelCarving;

/** BiomeGenerationSettings' LIQUID carvers, 1.21.1's second carving step (CbLevelCarving): none unless a builder added some */
@Mixin(net.minecraft.class_5485.class)
public abstract class CbLevelCarvingBiomeMixin implements CbLevelCarving.SettingsState {

    @Unique
    private List<class_6880<class_2922<?>>> cb$liquidCarvers = List.of();

    @Override
    public List<class_6880<class_2922<?>>> cb$liquidCarvers() {
        return this.cb$liquidCarvers;
    }

    @Override
    public void cb$liquidCarvers(List<class_6880<class_2922<?>>> carvers) {
        this.cb$liquidCarvers = carvers;
    }
}
