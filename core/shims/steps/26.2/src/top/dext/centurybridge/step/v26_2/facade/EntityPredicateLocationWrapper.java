package top.dext.centurybridge.step.v26_2.facade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.LocationPredicate;

/**
 * 26.1.2's EntityPredicate.LocationWrapper (gone on 26.2, whose EntityPredicate keeps the three location predicates
 * as separate parts: EntityLocationPredicate, SteppingOnPredicate, MovementAffectedByPredicate): reached by
 * classRenames; the same record and map codec as 26.1.2's (rt/CbS26EntityPredicates builds and reads the parts).
 */
public record EntityPredicateLocationWrapper(Optional<LocationPredicate> located, Optional<LocationPredicate> steppingOn,
        Optional<LocationPredicate> affectsMovement) {
    public static final MapCodec<EntityPredicateLocationWrapper> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        LocationPredicate.CODEC.optionalFieldOf("location").forGetter(EntityPredicateLocationWrapper::located),
        LocationPredicate.CODEC.optionalFieldOf("stepping_on").forGetter(EntityPredicateLocationWrapper::steppingOn),
        LocationPredicate.CODEC.optionalFieldOf("movement_affected_by").forGetter(EntityPredicateLocationWrapper::affectsMovement)
    ).apply(i, EntityPredicateLocationWrapper::new));
}
