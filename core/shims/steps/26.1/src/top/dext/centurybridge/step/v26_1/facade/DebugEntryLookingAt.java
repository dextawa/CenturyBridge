package top.dext.centurybridge.step.v26_1.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * 1.21.11's DebugEntryLookingAtBlock / DebugEntryLookingAtFluid (net/minecraft/class_11615 / class_11617), which 26.1
 * folded into DebugEntryLookingAt. 1.21.11's bodies on 26.1's API: the camera entity's pick within 20 blocks (fluids
 * included for the fluid entry), then under the entry's group the underlined "Targeted Block/Fluid: x, y, z", the
 * registry name, one "name: value" line per property (true green, false red) and one "#tag" line per tag. The level is
 * the server's when 1.21.11's debug flag showed server values, else the client's.
 */
@Environment(EnvType.CLIENT)
public abstract class DebugEntryLookingAt implements DebugScreenEntry {

    private final Identifier group;
    private final boolean fluids;
    private final String targeted;

    DebugEntryLookingAt(String group, boolean fluids, String targeted) {
        this.group = Identifier.withDefaultNamespace(group);
        this.fluids = fluids;
        this.targeted = targeted;
    }

    /** the looked-at state's registry name, property values and tags */
    abstract void describe(Level level, BlockPos pos, List<String> lines);

    @Override
    public void display(DebugScreenDisplayer displayer, Level serverOrClientLevel, LevelChunk clientChunk, LevelChunk serverChunk) {
        Entity camera = Minecraft.getInstance().getCameraEntity();
        Level level = SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES ? serverOrClientLevel : Minecraft.getInstance().level;
        if (camera == null || level == null) {
            return;
        }
        HitResult hit = camera.pick(20.0, 0.0F, this.fluids);
        List<String> lines = new ArrayList<>();
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            lines.add(ChatFormatting.UNDERLINE + this.targeted + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
            describe(level, pos, lines);
        }
        displayer.addToGroup(this.group, lines);
    }

    static void properties(Stream<Property.Value<?>> values, List<String> lines) {
        values.forEach(v -> lines.add(property(v.property(), v.value())));
    }

    private static String property(Property<?> property, Comparable<?> value) {
        String text = Util.getPropertyName(property, value);
        if (Boolean.TRUE.equals(value)) {
            text = ChatFormatting.GREEN + text;
        } else if (Boolean.FALSE.equals(value)) {
            text = ChatFormatting.RED + text;
        }
        return property.getName() + ": " + text;
    }

    static String tag(TagKey<?> tag) {
        return "#" + tag.location();
    }
}
