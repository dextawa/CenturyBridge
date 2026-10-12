package top.dext.centurybridge.step.v1_20_4.rt;

import java.util.Set;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.loader.api.metadata.ModMetadata;

/**
 * 1.20.4's Fabric API gave ModResourcePack an abstract createOverlay(String): the pack of one overlay
 * directory its pack.mcmeta lists (Fabric's ModNioResourcePack answers itself rooted at that directory,
 * which its ModResourcePackFactory stacks on the base pack). Fabric API asks this only of the packs it
 * builds itself. 1.20.3's Fabric API had no such method and applied overlays only to its own packs too:
 * ModResourcePackCreator built each overlay from the mods' directories (appendModResourcePacks with the
 * overlay as sub path), never from a ModResourcePack a mod implemented, so no overlay content of such a
 * pack was ever served. The method synthesized for a 1.20.3 implementation answers that: an overlay that
 * holds nothing -- no files, no namespaces, no metadata of its own -- under the base pack's name and mod.
 * No Fabric API default exists for the method (it is abstract), so 1.20.3's behaviour is what is given.
 */
public final class CbModResourcePacks {

    private CbModResourcePacks() {
    }

    public static ModResourcePack createOverlay(ModResourcePack pack, String overlay) {
        return new NoOverlay(pack);
    }

    /** the overlay of a pack 1.20.3 never served overlays for: nothing in it */
    static final class NoOverlay implements ModResourcePack {
        private final ModResourcePack base;

        NoOverlay(ModResourcePack base) {
            this.base = base;
        }

        @Override
        public ModMetadata getFabricModMetadata() {
            return base.getFabricModMetadata();
        }

        @Override
        public ModResourcePack createOverlay(String overlay) {
            return this;
        }

        @Override
        public net.minecraft.class_7367<java.io.InputStream> method_14410(String... segments) {
            return null;
        }

        @Override
        public net.minecraft.class_7367<java.io.InputStream> method_14405(net.minecraft.class_3264 type, net.minecraft.class_2960 id) {
            return null;
        }

        @Override
        public void method_14408(net.minecraft.class_3264 type, String namespace, String prefix, net.minecraft.class_3262.class_7664 consumer) {
        }

        @Override
        public Set<String> method_14406(net.minecraft.class_3264 type) {
            return Set.of();
        }

        @Override
        public <T> T method_14407(net.minecraft.class_3270<T> reader) {
            return null;
        }

        @Override
        public String method_14409() {
            return base.method_14409();
        }

        @Override
        public void close() {
        }
    }
}
