package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's GameRenderer.ResourceCache (net/minecraft/class_757$class_7760, classRenames), gone in 1.21.2: a resource
 * provider answering the resources it was given, else the original provider's. 1.21.1's record with the same body and
 * the record's equality; a plain final class, because its component fields stand in for 1.21.1's and must be public
 * (a mod's access widener cannot reach a CenturyBridge class).
 */
@Environment(EnvType.CLIENT)
public final class GameRendererResourceCache implements net.minecraft.class_5912 {

    public final net.minecraft.class_5912 comp_1038;
    public final Map<net.minecraft.class_2960, net.minecraft.class_3298> comp_1039;

    public GameRendererResourceCache(net.minecraft.class_5912 original, Map<net.minecraft.class_2960, net.minecraft.class_3298> cache) {
        this.comp_1038 = original;
        this.comp_1039 = cache;
    }

    public net.minecraft.class_5912 comp_1038() {
        return this.comp_1038;
    }

    public Map<net.minecraft.class_2960, net.minecraft.class_3298> comp_1039() {
        return this.comp_1039;
    }

    @Override
    public Optional<net.minecraft.class_3298> method_14486(net.minecraft.class_2960 location) {
        net.minecraft.class_3298 resource = this.comp_1039.get(location);
        return resource != null ? Optional.of(resource) : this.comp_1038.method_14486(location);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof GameRendererResourceCache c && Objects.equals(this.comp_1038, c.comp_1038)
            && Objects.equals(this.comp_1039, c.comp_1039);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.comp_1038) + Objects.hashCode(this.comp_1039);
    }

    @Override
    public String toString() {
        return "class_7760[comp_1038=" + this.comp_1038 + ", comp_1039=" + this.comp_1039 + "]";
    }
}
