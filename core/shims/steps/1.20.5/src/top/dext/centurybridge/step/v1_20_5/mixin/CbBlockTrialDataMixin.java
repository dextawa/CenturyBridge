package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockTrial;

/**
 * TrialSpawnerData as 1.20.4 code sees it (CbBlockTrial): the spawner it belongs to, the spawn
 * potentials the 1.20.4 API set -- in force for 1.20.5's spawning (getOrCreateNextSpawnData,
 * hasMobToSpawn) where set -- and the protected state 1.20.4's bodies read. For a spawner whose
 * block has no OMINOUS property (a 1.20.4 trial spawner block) tryDetectPlayers does not see the
 * players' omens, so it does not try to set a property the block lacks.
 */
@Mixin(net.minecraft.class_8966.class)
public abstract class CbBlockTrialDataMixin implements CbBlockTrial.Data {

    @Shadow
    @Final
    protected Set<UUID> field_47371;

    @Shadow
    protected long field_47373;

    @Shadow
    protected long field_47374;

    @Shadow
    protected Optional<net.minecraft.class_1952> field_47376;

    @Shadow
    protected Optional<net.minecraft.class_5321<net.minecraft.class_52>> field_47377;

    @Unique
    private net.minecraft.class_8963 cb$spawner;

    @Unique
    private net.minecraft.class_6005<net.minecraft.class_1952> cb$legacyPotentials;

    @Override
    public net.minecraft.class_8963 cb$spawner() {
        return this.cb$spawner;
    }

    @Override
    public void cb$setSpawner(net.minecraft.class_8963 spawner) {
        this.cb$spawner = spawner;
    }

    @Override
    public net.minecraft.class_6005<net.minecraft.class_1952> cb$legacyPotentials() {
        return this.cb$legacyPotentials;
    }

    @Override
    public void cb$setLegacyPotentials(net.minecraft.class_6005<net.minecraft.class_1952> potentials) {
        this.cb$legacyPotentials = potentials;
    }

    @Override
    public Set<UUID> cb$detectedPlayers() {
        return this.field_47371;
    }

    @Override
    public long cb$nextMobSpawnsAt() {
        return this.field_47374;
    }

    @Override
    public void cb$setNextMobSpawnsAt(long t) {
        this.field_47374 = t;
    }

    @Override
    public long cb$cooldownEndsAt() {
        return this.field_47373;
    }

    @Override
    public Optional<net.minecraft.class_1952> cb$nextSpawnData() {
        return this.field_47376;
    }

    @Override
    public Optional<net.minecraft.class_5321<net.minecraft.class_52>> cb$ejectingLootTable() {
        return this.field_47377;
    }

    @Override
    public void cb$setEjectingLootTable(Optional<net.minecraft.class_5321<net.minecraft.class_52>> table) {
        this.field_47377 = table;
    }

    /** getOrCreateNextSpawnData / hasMobToSpawn: the potentials in force (CbBlockTrial.potentialsInForce) */
    @Redirect(method = {"method_55188", "method_55197"}, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_8965;comp_2110()Lnet/minecraft/class_6005;"))
    private net.minecraft.class_6005<net.minecraft.class_1952> cb$potentialsInForce(net.minecraft.class_8965 config) {
        return CbBlockTrial.potentialsInForce((net.minecraft.class_8966) (Object) this, config);
    }

    /** tryDetectPlayers: a player's Bad / Trial Omen, seen only by a spawner whose block can turn ominous */
    @Redirect(method = "method_55186", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1657;method_6059(Lnet/minecraft/class_6880;)Z"))
    private boolean cb$omen(net.minecraft.class_1657 player, net.minecraft.class_6880<net.minecraft.class_1291> effect,
            net.minecraft.class_3218 level, net.minecraft.class_2338 pos, net.minecraft.class_8963 spawner) {
        return level.method_8320(pos).method_28498(net.minecraft.class_2741.field_50193) && player.method_6059(effect);
    }
}
