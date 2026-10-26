package top.dext.centurybridge.step.v1_21_9.mixin;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.class_11560;
import net.minecraft.class_156;
import net.minecraft.class_3312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_9.rt.CbProfiles;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's GameProfileCache.getAsync(name) with its executor (setExecutor / clearExecutor and the executor and request
 * fields), which 1.21.9's CachedUserNameToIdResolver dropped: 1.21.8's bodies, the lookup being 1.21.9's get(name)
 * with its NameAndId handed out as a GameProfile (CbProfiles.toProfile), as every 1.21.8 reader of this cache gets it.
 * 1.21.9 never calls them: the server's executor is set and cleared where 1.21.8's server did it
 * (CbProfileCacheServerMixin). The fields keep 1.21.8's names, so a 1.21.8 accessor of the executor reaches it.
 */
@Mixin(value = class_3312.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbProfileCacheRestoredMixin implements top.dext.centurybridge.step.v1_21_9.rt.CbProfileCacheExecutor {

    private final Map<String, CompletableFuture<Optional<GameProfile>>> field_33860 = Maps.newConcurrentMap();

    private Executor field_33861;

    @Shadow
    public abstract Optional<class_11560> method_14515(String name);

    /** GameProfileCache.getAsync(name) */
    public CompletableFuture<Optional<GameProfile>> method_37156(String name) {
        if (this.field_33861 == null) {
            throw new IllegalStateException("No executor");
        }
        CompletableFuture<Optional<GameProfile>> pending = this.field_33860.get(name);
        if (pending != null) {
            return pending;
        }
        CompletableFuture<Optional<GameProfile>> request = CompletableFuture
            .supplyAsync(() -> this.method_14515(name).map(CbProfiles::toProfile), class_156.method_18349().method_64116("getProfile"))
            .whenCompleteAsync((profile, failure) -> this.field_33860.remove(name), this.field_33861);
        this.field_33860.put(name, request);
        return request;
    }

    /** GameProfileCache.setExecutor(executor) */
    public void method_37157(Executor executor) {
        this.field_33861 = executor;
    }

    /** GameProfileCache.clearExecutor() */
    public void method_39753() {
        this.field_33861 = null;
    }
}
