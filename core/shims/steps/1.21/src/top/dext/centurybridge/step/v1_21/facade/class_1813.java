package top.dext.centurybridge.step.v1_21.facade;

import java.util.List;
import java.util.Map;

/**
 * 1.20.6's RecordItem on 1.21 (classRenames net/minecraft/class_1813): 1.20.6's fields and bodies; the disc
 * also carries 1.21's jukebox_playable component naming its song, which CenturyBridge adds to the worlds'
 * jukebox_song registries (CbItemRecords). 1.21's own discs are plain items now: getBySound answers the
 * discs of 1.20.6 mods.
 */
public class class_1813 extends net.minecraft.class_1792 {

    /** BY_NAME: the discs by sound */
    public static final Map<net.minecraft.class_3414, class_1813> field_8901 = com.google.common.collect.Maps.newHashMap();

    /** analogOutput */
    public final int field_8902;

    /** sound */
    public final net.minecraft.class_3414 field_8900;

    /** lengthInTicks */
    public final int field_39480;

    public class_1813(int analogOutput, net.minecraft.class_3414 sound, net.minecraft.class_1792.class_1793 properties, int lengthInSeconds) {
        super(top.dext.centurybridge.step.v1_21.rt.CbItemRecords.withSong(properties, sound));
        this.field_8902 = analogOutput;
        this.field_8900 = sound;
        this.field_39480 = lengthInSeconds * 20;
        field_8901.put(this.field_8900, this);
        top.dext.centurybridge.step.v1_21.rt.CbItemRecords.track(this);
    }

    /** useOn: into an empty jukebox, as 1.20.6 put it (1.21's jukebox plays the disc's song from its component) */
    @Override
    public net.minecraft.class_1269 method_7884(net.minecraft.class_1838 context) {
        net.minecraft.class_1937 level = context.method_8045();
        net.minecraft.class_2338 pos = context.method_8037();
        net.minecraft.class_2680 state = level.method_8320(pos);
        if (!state.method_27852(net.minecraft.class_2246.field_10223) || state.method_11654(net.minecraft.class_2387.field_11180)) {
            return net.minecraft.class_1269.field_5811;
        }
        net.minecraft.class_1799 stack = context.method_8041();
        if (!level.field_9236) {
            net.minecraft.class_1657 player = context.method_8036();
            if (level.method_8321(pos) instanceof net.minecraft.class_2619 jukebox) {
                jukebox.method_54077(stack.method_7972());
                level.method_43276(net.minecraft.class_5712.field_28733, pos, net.minecraft.class_5712.class_7397.method_43286(player, state));
            }
            stack.method_7934(1);
            if (player != null) {
                player.method_7281(net.minecraft.class_3468.field_15375);
            }
        }
        return net.minecraft.class_1269.method_29236(level.field_9236);
    }

    /** getAnalogOutput */
    public int method_8010() {
        return this.field_8902;
    }

    /** appendHoverText: the disc's description, grey */
    @Override
    public void method_7851(net.minecraft.class_1799 stack, net.minecraft.class_1792.class_9635 context, List<net.minecraft.class_2561> tooltip,
            net.minecraft.class_1836 flag) {
        tooltip.add(this.method_8011().method_27692(net.minecraft.class_124.field_1080));
    }

    /** getDisplayName: the description, &lt;descriptionId&gt;.desc */
    public net.minecraft.class_5250 method_8011() {
        return net.minecraft.class_2561.method_43471(this.method_7876() + ".desc");
    }

    /** getBySound */
    public static class_1813 method_8012(net.minecraft.class_3414 sound) {
        return field_8901.get(sound);
    }

    /** getSound */
    public net.minecraft.class_3414 method_8009() {
        return this.field_8900;
    }

    /** getLengthInTicks */
    public int method_44369() {
        return this.field_39480;
    }
}
