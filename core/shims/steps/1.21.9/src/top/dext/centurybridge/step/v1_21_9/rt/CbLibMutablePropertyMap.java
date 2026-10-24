package top.dext.centurybridge.step.v1_21_9.rt;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

/**
 * authlib 6.0.58's (1.21.8) PropertyMap: a mutable LinkedHashMultimap per instance (its no-argument constructor).
 * authlib 7.0.61 (1.21.9) keeps the class but copies every map into an ImmutableMultimap and drops that constructor,
 * so a put on it is an UnsupportedOperationException. This subclass answers W's PropertyMap everywhere (its delegate
 * is all ForwardingMultimap reads) while holding V's mutable map.
 */
public class CbLibMutablePropertyMap extends PropertyMap {
    private final Multimap<String, Property> properties = LinkedHashMultimap.create();

    public CbLibMutablePropertyMap() {
        super(ImmutableMultimap.of());
    }

    @Override
    protected Multimap<String, Property> delegate() {
        return properties;
    }
}
