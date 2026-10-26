package top.dext.centurybridge.step.v1_20_5.mixin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * EntityZombieSplitFix: 1.20.4's protected getNewNameAndTag(name, tag), back for a subclass's call
 * and super call (1.20.5 renames through the typed instead; a subclass's override is reached through
 * the override hook, CbFinDatafix.cbZombieSplit). 1.20.4's body: a Zombie of ZombieType 1-5 becomes a
 * ZombieVillager with that profession minus one, of type 6 a Husk, ZombieType removed.
 */
@Mixin(value = net.minecraft.class_1175.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFinZombieSplitMixin {

    /** was getNewNameAndTag(name, tag) */
    protected Pair<String, Dynamic<?>> method_5164(String name, Dynamic<?> tag) {
        if (!Objects.equals("Zombie", name)) {
            return Pair.of(name, tag);
        }
        String newName = "Zombie";
        int type = tag.get("ZombieType").asInt(0);
        switch (type) {
            case 1, 2, 3, 4, 5 -> {
                newName = "ZombieVillager";
                tag = tag.set("Profession", tag.createInt(type - 1));
            }
            case 6 -> newName = "Husk";
            default -> {
            }
        }
        tag = tag.remove("ZombieType");
        return Pair.of(newName, tag);
    }
}
