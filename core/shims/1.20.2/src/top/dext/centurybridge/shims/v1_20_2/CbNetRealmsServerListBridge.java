package top.dext.centurybridge.shims.v1_20_2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.class_310;
import net.minecraft.class_4877;
import net.minecraft.class_7580;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * RealmsServerList: 1.20.1's updateServersList / removeItem answered the list they left behind;
 * 1.20.2's return nothing (the list is Iterable now). The 1.20.1 bodies, on the same fields 1.20.2
 * keeps. A subclass's 1.20.1 overrides are reached from 1.20.2's shapes by overrideHooks.
 */
@Mixin(class_7580.class)
public abstract class CbNetRealmsServerListBridge {

    @Shadow
    @Final
    private class_310 field_39692;

    @Shadow
    @Final
    private Set<class_4877> field_39693;

    @Shadow
    private List<class_4877> field_39694;

    /** was updateServersList(servers): sort by the player's ownership, drop the ones removed meanwhile */
    public List<class_4877> method_44623(List<class_4877> servers) {
        List<class_4877> list = new ArrayList<>(servers);
        list.sort(new class_4877.class_4319(field_39692.method_1548().method_1676()));
        boolean removed = list.removeAll(field_39693);
        if (!removed) {
            field_39693.clear();
        }
        field_39694 = list;
        return List.copyOf(field_39694);
    }

    /** was removeItem(server) */
    public synchronized List<class_4877> method_44622(class_4877 server) {
        field_39694.remove(server);
        field_39693.add(server);
        return List.copyOf(field_39694);
    }
}
