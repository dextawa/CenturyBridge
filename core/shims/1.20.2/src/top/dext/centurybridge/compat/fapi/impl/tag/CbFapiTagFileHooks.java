package top.dext.centurybridge.compat.fapi.impl.tag;

import java.util.List;

/** 1.20.1's TagFileHooks: how the tag file codec hands a TagFile its removal list. */
public interface CbFapiTagFileHooks {

    void cb$setRemove(List<net.minecraft.class_3497> remove);
}
