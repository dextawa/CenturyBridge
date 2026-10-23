package top.dext.centurybridge.step.v1_21.mixin;

import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.JsonOps;
import net.minecraft.class_1255;
import net.minecraft.class_156;
import net.minecraft.class_1923;
import net.minecraft.class_2791;
import net.minecraft.class_2794;
import net.minecraft.class_2806;
import net.minecraft.class_3193;
import net.minecraft.class_3898;
import net.minecraft.class_8563;
import net.minecraft.class_9259;
import net.minecraft.class_9312;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbLevelChunks;

/**
 * 1.20.6 ChunkMap members 1.21 dropped, given back on ChunkMap so a 1.20.6 mod's calls, overrides and super calls
 * link (covers in s6fin-level.json). 1.21 itself calls none of them: generation is scheduled through the
 * holder's generation tasks, the light engine takes no tickets, and the command that reloaded the generator is
 * gone -- so an override is reached exactly where the mod itself calls it.
 */
@Mixin(class_3898.class)
public abstract class CbLevelChunkMapMixin {

    @Shadow
    @Final
    @Mutable
    private class_9312 field_51861;

    @Shadow
    @Final
    private class_1255<Runnable> field_17216;

    /**
     * 1.20.6 debugReloadGenerator() (behind ResetChunksCommand): the generator round-tripped through its codec as
     * JSON, and on success put in place of the old one in a WorldGenContext rebuilt around it. 1.21 keeps the
     * generator only in the context (with the main thread mailbox it also carries, kept as it was).
     */
    public void method_37904() {
        class_2794.field_24746.encodeStart(JsonOps.INSTANCE, this.field_51861.comp_2435())
            .flatMap(json -> class_2794.field_24746.parse(JsonOps.INSTANCE, json))
            .ifSuccess(generator -> {
                class_9312 old = this.field_51861;
                this.field_51861 = new class_9312(old.comp_2434(), generator, old.comp_2436(), old.comp_2437(), old.comp_2808());
            });
    }

    /**
     * 1.20.6 schedule(holder, status): the future of the holder's chunk reaching the status, loading or generating
     * it as needed. 1.21 schedules that through the holder (GenerationChunkHolder.scheduleChunkGenerationTask(status,
     * chunkMap)), which hands this chunk map a generation task and answers the same future.
     */
    public CompletableFuture<class_9259<class_2791>> method_17236(class_3193 holder, class_2806 status) {
        return holder.method_60458(status, (class_3898) (Object) this);
    }

    /** 1.20.6 releaseLightTicket(pos): on the main thread, the LIGHT ticket at the light status's level removed */
    protected void method_20441(class_1923 pos) {
        this.field_17216.method_18858(class_156.method_18839(
            () -> ((class_3898) (Object) this).method_17263().method_20444(CbLevelChunks.LIGHT, pos, class_8563.method_51829(class_2806.field_12805), pos),
            () -> "release light ticket " + pos));
    }
}
