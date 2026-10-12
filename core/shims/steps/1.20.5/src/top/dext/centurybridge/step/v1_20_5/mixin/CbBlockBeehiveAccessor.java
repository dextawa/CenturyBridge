package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockBeehive;

/** BeehiveBlockEntity's private occupants, saved flower position and ignored tags, for CbBlockBeehive */
@Mixin(net.minecraft.class_4482.class)
public interface CbBlockBeehiveAccessor extends CbBlockBeehive.Hive {

    @Override
    @Invoker("method_57580")
    java.util.List<net.minecraft.class_4482.class_9309> cb$getBees();

    @Override
    @Accessor("field_20424")
    net.minecraft.class_2338 cb$savedFlowerPos();

    @Override
    @Accessor("field_20424")
    void cb$setSavedFlowerPos(net.minecraft.class_2338 pos);

    /** IGNORED_BEE_TAGS (package-private) */
    @Accessor("field_33570")
    static java.util.List<String> cb$ignoredBeeTags() {
        throw new AssertionError();
    }
}
