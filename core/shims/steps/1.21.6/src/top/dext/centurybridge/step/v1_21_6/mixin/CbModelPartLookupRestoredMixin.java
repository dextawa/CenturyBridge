package top.dext.centurybridge.step.v1_21_6.mixin;

import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's Model.getAnyDescendantWithName(String) (class_3879.method_62104), which 1.21.6 removed (javap of both),
 * given back for mods' calls and overrides' super calls (and the climbed 1.21.2 layer's AgeableListModel parts).
 * <ul>
 * <li>1.21.5: "root" answers root(); else root().getAllParts() (the part, then its children's getAllParts, depth-first
 *     preorder) .filter(part -> part.hasChild(name)).findFirst().map(part -> part.getChild(name)).</li>
 * <li>1.21.6: ModelPart.createPartLookup() (method_72015) maps "root" to the part first, then every child name to its
 *     part with putIfAbsent, emitting a part's direct children when the part is visited in the same preorder -- so the
 *     first parent in preorder with a child of that name wins, as in 1.21.5, and "root" is the root even where a child
 *     is called "root". Absent names answer null: Optional.ofNullable gives 1.21.5's empty.</li>
 * </ul>
 * Nothing in 1.21.6 calls it. Priority below every mod's.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(value = net.minecraft.class_3879.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbModelPartLookupRestoredMixin {

    /** getAnyDescendantWithName */
    public Optional<net.minecraft.class_630> method_62104(String name) {
        return Optional.ofNullable(((net.minecraft.class_3879) (Object) this).method_63512().method_72015().apply(name));
    }
}
