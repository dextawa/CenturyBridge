package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's StructureManager queries on 1.20.5.
 *
 * <p>getStructureWithPieceAt(pos, ResourceKey&lt;Structure&gt;): 1.20.4 looked the key up in the
 * structure registry -- INVALID_START when it is not there -- and asked getStructureWithPieceAt(pos,
 * structure), which 1.20.5 still has; so does the bridge. 1.20.5 asks a HolderSet in its place
 * (LocationPredicate, 1.20.4's only caller, now holds a set): a mod class overriding the key shape is
 * asked with the key of a single-structure set -- the query 1.20.4 made -- and any other set gets
 * 1.20.5's own answer (its HolderSet overload is getStructureWithPieceAt(pos, set::contains)).
 *
 * <p>checkStructurePresence(chunk, structure, skipKnown): 1.20.5 added the structure's placement,
 * whose only use in StructureCheck.checkStart is applyAdditionalChunkRestrictions -- the frequency
 * reduction, which 1.20.4's checkStart did not apply (it was part of 1.20.4's isStructureChunk). The
 * old call passes a placement that restricts nothing (frequency 1).
 */
public final class CbBlockStructures {

    /** the 1.20.4 getStructureWithPieceAt(pos, key) a mod StructureManager subclass overrides */
    public interface LegacyByKey {
        net.minecraft.class_3449 method_41034(net.minecraft.class_2338 pos,
            net.minecraft.class_5321<net.minecraft.class_3195> key);
    }

    /** a placement whose frequency is 1: applyAdditionalChunkRestrictions holds for every chunk */
    public static final net.minecraft.class_6874 NO_RESTRICTION =
        new net.minecraft.class_6872(1, 0, net.minecraft.class_6873.field_36421, 0);

    /** 1.20.4's getStructureWithPieceAt(pos, key) on any structure manager (a call, or a mod's super call) */
    public static net.minecraft.class_3449 method_41034(net.minecraft.class_5138 self, net.minecraft.class_2338 pos,
            net.minecraft.class_5321<net.minecraft.class_3195> key) {
        if (self instanceof LegacyByKey legacy) {
            return legacy.method_41034(pos, key);
        }
        return superWithPieceAt(self, pos, key);
    }

    /** 1.20.4's implementation (a mod's super call) */
    public static net.minecraft.class_3449 superWithPieceAt(net.minecraft.class_5138 self, net.minecraft.class_2338 pos,
            net.minecraft.class_5321<net.minecraft.class_3195> key) {
        net.minecraft.class_3195 structure = self.method_41036().method_30530(net.minecraft.class_7924.field_41246).method_29107(key);
        return structure == null ? net.minecraft.class_3449.field_16713 : self.method_38854(pos, structure);
    }

    /** getStructureWithPieceAt(pos, set) of a mod class overriding the key shape */
    public static net.minecraft.class_3449 withPieceAt(LegacyByKey self, net.minecraft.class_2338 pos,
            net.minecraft.class_6885<net.minecraft.class_3195> set) {
        if (set.method_40247() == 1) {
            java.util.Optional<net.minecraft.class_5321<net.minecraft.class_3195>> key = set.method_40240(0).method_40230();
            if (key.isPresent()) {
                return self.method_41034(pos, key.get());
            }
        }
        return ((net.minecraft.class_5138) self).method_41413(pos, set::method_40241);
    }

    /** 1.20.4's checkStructurePresence(chunk, structure, skipKnown) called on any structure manager */
    public static net.minecraft.class_6833 method_39783(net.minecraft.class_5138 self, net.minecraft.class_1923 chunk,
            net.minecraft.class_3195 structure, boolean skipKnownStructures) {
        return self.method_39783(chunk, structure, NO_RESTRICTION, skipKnownStructures);
    }

    /** a frame fill of the checkStructurePresence hook: vanilla's placement, else (the mod calling its old method itself) none */
    public static net.minecraft.class_6874 orNoRestriction(Object passed, net.minecraft.class_5138 self) {
        return passed != null ? (net.minecraft.class_6874) passed : NO_RESTRICTION;
    }

    private CbBlockStructures() {
    }
}
