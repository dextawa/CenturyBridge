package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 1.21.11's SubmitNodeStorage.BlockSubmit (net/minecraft/class_11661$class_11663), a record 26.1 dropped with
 * OrderedSubmitNodeCollector.submitBlock (26.1 resolves a block's model and special renderer where it is submitted):
 * reached by classRenames, 1.21.11's components (pose, state, light, overlay, outline) under their intermediary names
 * (a classRenames facade keeps them past the wall), public (an access widener cannot reach a CenturyBridge class), and
 * the record's value semantics. SubmitNodeCollection.getBlockSubmits() lists them (rt/CbWallBlockSubmits).
 */
@Environment(EnvType.CLIENT)
public final class BlockSubmit {
    /** pose */
    public final PoseStack.Pose comp_4484;
    /** state */
    public final BlockState comp_4485;
    /** lightCoords */
    public final int comp_4486;
    /** overlayCoords */
    public final int comp_4487;
    /** outlineColor */
    public final int comp_4660;

    public BlockSubmit(PoseStack.Pose pose, BlockState state, int lightCoords, int overlayCoords, int outlineColor) {
        this.comp_4484 = pose;
        this.comp_4485 = state;
        this.comp_4486 = lightCoords;
        this.comp_4487 = overlayCoords;
        this.comp_4660 = outlineColor;
    }

    public PoseStack.Pose comp_4484() {
        return this.comp_4484;
    }

    public BlockState comp_4485() {
        return this.comp_4485;
    }

    public int comp_4486() {
        return this.comp_4486;
    }

    public int comp_4487() {
        return this.comp_4487;
    }

    public int comp_4660() {
        return this.comp_4660;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BlockSubmit other && Objects.equals(this.comp_4484, other.comp_4484) && Objects.equals(this.comp_4485, other.comp_4485)
            && this.comp_4486 == other.comp_4486 && this.comp_4487 == other.comp_4487 && this.comp_4660 == other.comp_4660;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.comp_4484, this.comp_4485, this.comp_4486, this.comp_4487, this.comp_4660);
    }

    @Override
    public String toString() {
        return "BlockSubmit[pose=" + this.comp_4484 + ", state=" + this.comp_4485 + ", lightCoords=" + this.comp_4486 + ", overlayCoords="
            + this.comp_4487 + ", outlineColor=" + this.comp_4660 + "]";
    }
}
