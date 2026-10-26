package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallBossEvents;

/** 1.21.11 CustomBossEvents members 26.1 dropped, given back (covers; bodies in rt/CbWallBossEvents) */
@Mixin(value = CustomBossEvents.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallBossEventsMixin {

    public CustomBossEvent create(Identifier id, Component name) {
        return CbWallBossEvents.create((CustomBossEvents) (Object) this, id, name);
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        return CbWallBossEvents.save((CustomBossEvents) (Object) this, registries);
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        CbWallBossEvents.load((CustomBossEvents) (Object) this, tag, registries);
    }
}
