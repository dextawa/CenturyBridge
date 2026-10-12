package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1297;
import net.minecraft.class_1297.class_5529;
import net.minecraft.class_5582;
import net.minecraft.class_638;
import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Shadow;

/**
 * ClientWorld.addPlayer(id, player) and addEntity(id, entity) are gone from 1.20.2, folded into
 * addEntity(entity). 1.20.1's bodies, verbatim: both removed whatever entity had the given id and
 * added the new one to the entity storage. (Vanilla 1.20.2 only calls addEntity(entity); for a mod
 * subclass overriding the 1.20.1 methods see CbNetClient.cbAddEntity.)
 */
@Mixin(class_638.class)
public abstract class CbNetClientLevelBridge {

    @Shadow
    @Final
    private class_5582<class_1297> field_27734;

    /** was addPlayer(int, AbstractClientPlayerEntity) */
    public void method_18107(int id, class_742 player) {
        cb$addEntity(id, player);
    }

    /** was addEntity(int, Entity) */
    public void method_2942(int id, class_1297 entity) {
        cb$addEntity(id, entity);
    }

    @Unique
    private void cb$addEntity(int id, class_1297 entity) {
        ((class_638) (Object) this).method_2945(id, class_5529.field_26999);
        field_27734.method_31870(entity);
    }
}
