package top.dext.centurybridge.step.v1_21_9.facade;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_1060;
import net.minecraft.class_156;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_7766;

/**
 * 1.21.8's AtlasSet (class_4724) with its StitchResult and AtlasEntry, which 1.21.9 replaced by AtlasManager. 1.21.8's
 * bodies (javap -c): one TextureAtlas per (atlas location -> atlas definition) entry, each registered with the texture
 * manager under its location; scheduleLoad stitches each atlas with SpriteLoader.loadAndStitch(manager, definition,
 * mipLevel, executor) (1.21.8's default metadata sections, Set.of(AnimationMetadataSection.TYPE)) into a StitchResult; close closes
 * every atlas and forgets them. A 1.21.8 mod's AtlasSet keeps its own atlases, as on 1.21.8.
 */
@Environment(EnvType.CLIENT)
public class AtlasSet implements AutoCloseable {
    // public: a facade's members stand in for the class's, and access wideners never reach a CenturyBridge class
    public final Map<class_2960, AtlasEntry> field_21746;

    public AtlasSet(Map<class_2960, class_2960> atlases, class_1060 textureManager) {
        this.field_21746 = atlases.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> method_45864(textureManager, entry)));
    }

    public class_1059 method_24098(class_2960 location) {
        return this.field_21746.get(location).comp_1051();
    }

    @Override
    public void close() {
        this.field_21746.values().forEach(AtlasEntry::close);
        this.field_21746.clear();
    }

    public Map<class_2960, CompletableFuture<StitchResult>> method_45862(class_3300 manager, int mipLevel, Executor executor) {
        return class_156.method_65968(this.field_21746, entry -> method_45863(manager, mipLevel, executor, entry));
    }

    public static CompletableFuture<StitchResult> method_45863(class_3300 manager, int mipLevel, Executor executor, AtlasEntry entry) {
        return cb$loadAndStitch(class_7766.method_45837(entry.comp_1051()), manager, entry.comp_1168(), mipLevel, executor)
            .thenApply(preparations -> method_45867(entry, preparations));
    }

    /** 1.21.8 SpriteLoader.loadAndStitch(manager, definition, mipLevel, executor): its default metadata sections */
    private static CompletableFuture<class_7766.class_7767> cb$loadAndStitch(class_7766 loader, class_3300 manager, class_2960 definition,
                                                                       int mipLevel, Executor executor) {
        return loader.method_47661(manager, definition, mipLevel, executor, java.util.Set.of(net.minecraft.class_1079.field_55537));
    }

    public static StitchResult method_45867(AtlasEntry entry, class_7766.class_7767 preparations) {
        return new StitchResult(entry.comp_1051(), preparations);
    }

    public static AtlasEntry method_45864(class_1060 textureManager, Map.Entry<class_2960, class_2960> entry) {
        class_1059 atlas = new class_1059(entry.getKey());
        textureManager.method_4616(entry.getKey(), atlas);
        return new AtlasEntry(atlas, entry.getValue());
    }

    /** 1.21.8 AtlasSet.AtlasEntry (class_4724$class_7772, a record): the atlas and its definition */
    @Environment(EnvType.CLIENT)
    public static final class AtlasEntry implements AutoCloseable {
        public final class_1059 comp_1051;
        public final class_2960 comp_1168;

        public AtlasEntry(class_1059 atlas, class_2960 definition) {
            this.comp_1051 = atlas;
            this.comp_1168 = definition;
        }

        public class_1059 comp_1051() {
            return this.comp_1051;
        }

        public class_2960 comp_1168() {
            return this.comp_1168;
        }

        @Override
        public void close() {
            this.comp_1051.method_4601();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof AtlasEntry e && e.comp_1051.equals(this.comp_1051) && e.comp_1168.equals(this.comp_1168);
        }

        @Override
        public int hashCode() {
            return 31 * this.comp_1051.hashCode() + this.comp_1168.hashCode();
        }

        @Override
        public String toString() {
            return "AtlasEntry[atlas=" + this.comp_1051 + ", atlasInfoLocation=" + this.comp_1168 + "]";
        }
    }

    /** 1.21.8 AtlasSet.StitchResult (class_4724$class_7774) */
    @Environment(EnvType.CLIENT)
    public static class StitchResult {
        public final class_1059 field_40564;
        public final class_7766.class_7767 field_40565;

        public StitchResult(class_1059 atlas, class_7766.class_7767 preparations) {
            this.field_40564 = atlas;
            this.field_40565 = preparations;
        }

        public class_1058 method_45869(class_2960 sprite) {
            return this.field_40565.comp_1044().get(sprite);
        }

        public class_1058 method_45868() {
            return this.field_40565.comp_1043();
        }

        public CompletableFuture<Void> method_45870() {
            return this.field_40565.comp_1045();
        }

        public void method_45871() {
            this.field_40564.method_45848(this.field_40565);
        }
    }
}
