package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.facade.AnimationFrameOutput;

/**
 * 1.21.3's texture metadata sections on 1.21.4, where they are records read by codecs.
 * <ul>
 * <li>AnimationMetadataSection: 1.21.3 kept "no frames" as an empty list and "no size" as -1 (UNKNOWN_SIZE); 1.21.4 as empty
 *     Optionals. AnimationFrame: "no time" was -1 (UNKNOWN_FRAME_TIME), an empty Optional now. Each 1.21.3 value converts to
 *     the one 1.21.4 means by it.</li>
 * <li>forEachFrame: 1.21.3's sprite loader asked it (virtually) for the frames; a 1.21.3 section overriding it is asked so
 *     again where 1.21.4 builds the animation (CbTextureAnimationMixin).</li>
 * <li>The section serializers are facades with 1.21.3's bodies; the pack metadata API takes them as any 1.21.3 serializer
 *     (CbRestMetadata).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbTextureMetadata {
    private CbTextureMetadata() {
    }

    /** new AnimationMetadataSection(frames, frameWidth, frameHeight, defaultFrameTime, interpolatedFrames) */
    public static net.minecraft.class_1079 animation(List<net.minecraft.class_1080> frames, int frameWidth, int frameHeight, int defaultFrameTime,
        boolean interpolated) {
        return new net.minecraft.class_1079(cbFrames(frames), cbSize(frameWidth), cbSize(frameHeight), defaultFrameTime, interpolated);
    }

    public static Optional<List<net.minecraft.class_1080>> cbFrames(List<net.minecraft.class_1080> frames) {
        return frames.isEmpty() ? Optional.empty() : Optional.of(frames);
    }

    /** UNKNOWN_SIZE (-1) is no size */
    public static Optional<Integer> cbSize(int size) {
        return size == -1 ? Optional.empty() : Optional.of(size);
    }

    /** new AnimationFrame(index, time): UNKNOWN_FRAME_TIME (-1) is no time */
    public static net.minecraft.class_1080 frame(int index, int time) {
        return new net.minecraft.class_1080(index, cbTime(time));
    }

    public static Optional<Integer> cbTime(int time) {
        return time == -1 ? Optional.empty() : Optional.of(time);
    }

    /** AnimationMetadataSection.SERIALIZER */
    public static final top.dext.centurybridge.step.v1_21_4.facade.AnimationMetadataSectionSerializer ANIMATION =
        new top.dext.centurybridge.step.v1_21_4.facade.AnimationMetadataSectionSerializer();
    /** TextureMetadataSection.SERIALIZER */
    public static final top.dext.centurybridge.step.v1_21_4.facade.TextureMetadataSectionSerializer TEXTURE =
        new top.dext.centurybridge.step.v1_21_4.facade.TextureMetadataSectionSerializer();
    /** VillagerMetadataSection.SERIALIZER */
    public static final top.dext.centurybridge.step.v1_21_4.facade.VillagerMetadataSectionSerializer VILLAGER =
        new top.dext.centurybridge.step.v1_21_4.facade.VillagerMetadataSectionSerializer();

    /** AnimationMetadataSection.EMPTY: no frames, no size, frame time 1, not interpolated */
    public static final net.minecraft.class_1079 EMPTY = animation(List.of(), -1, -1, 1, false);

    public static top.dext.centurybridge.step.v1_21_4.facade.AnimationMetadataSectionSerializer field_5337() {
        return ANIMATION;
    }

    public static net.minecraft.class_1079 field_21768() {
        return EMPTY;
    }

    /** SECTION_NAME */
    public static String field_32974() {
        return "animation";
    }

    /** DEFAULT_FRAME_TIME */
    public static int field_32975() {
        return 1;
    }

    /** UNKNOWN_SIZE */
    public static int field_32976() {
        return -1;
    }

    /** AnimationFrame.UNKNOWN_FRAME_TIME */
    public static int field_32973() {
        return -1;
    }

    public static top.dext.centurybridge.step.v1_21_4.facade.TextureMetadataSectionSerializer field_5344() {
        return TEXTURE;
    }

    public static top.dext.centurybridge.step.v1_21_4.facade.VillagerMetadataSectionSerializer field_17158() {
        return VILLAGER;
    }

    /** VillagerMetadataSection.SECTION_NAME */
    public static String field_32977() {
        return "villager";
    }

    /** VillagerMetadataSection.Hat.getByName(name): the hat of that name, else NONE */
    public static net.minecraft.class_3888.class_3889 method_17170(String name) {
        for (net.minecraft.class_3888.class_3889 hat : net.minecraft.class_3888.class_3889.values()) {
            if (hat.method_15434().equals(name)) {
                return hat;
            }
        }
        return net.minecraft.class_3888.class_3889.field_17160;
    }

    /** a 1.21.3 AnimationMetadataSection subclass overriding forEachFrame (renamed onto this, overrideRenames) */
    public interface CbLegacyForEachFrame {
        void cb$forEachFrame(AnimationFrameOutput output);
    }

    /** was forEachFrame(output) (method_33460): a subclass's own, else every frame's index and time (the default time where it has none) */
    public static void method_33460(net.minecraft.class_1079 section, AnimationFrameOutput output) {
        if ((Object) section instanceof CbLegacyForEachFrame own) { // final at compile time, extendable at run time
            own.cb$forEachFrame(output);
            return;
        }
        superForEachFrame(section, output);
    }

    /** a subclass's super.forEachFrame(output): 1.21.3's body */
    public static void superForEachFrame(net.minecraft.class_1079 section, AnimationFrameOutput output) {
        section.comp_3453().ifPresent(frames -> frames.forEach(f -> output.accept(f.comp_3451(), f.method_4691(section.comp_3456()))));
    }

    /** the section 1.21.4 builds the animation from, for a 1.21.3 section whose frames its forEachFrame answers */
    public static net.minecraft.class_1079 framesOf(net.minecraft.class_1079 section) {
        if (!((Object) section instanceof CbLegacyForEachFrame own)) {
            return section;
        }
        List<net.minecraft.class_1080> frames = new ArrayList<>();
        own.cb$forEachFrame((index, time) -> frames.add(new net.minecraft.class_1080(index, Optional.of(time))));
        return new net.minecraft.class_1079(cbFrames(frames), section.comp_3454(), section.comp_3455(), section.comp_3456(), section.comp_3457());
    }
}
