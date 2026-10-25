/*
 * cb_stbir1.c: stb_image_resize v0.97 (the copy LWJGL 3.3.3 builds into lwjgl_stb, byte-identical; see SHA256SUMS) behind
 * JNI entry points of top.dext.centurybridge.stbir1.STBImageResize$Native (the Fabric mod centurybridge-stbir1, which
 * CenturyBridge's 1.21.11 -> 26.1 layer depends on; its source tree is core/mods/stbir1).
 *
 * LWJGL 3.4.1 (Minecraft 26.1) replaced stb_image_resize with stb_image_resize2, which resizes differently (other kernels
 * and rounding, other edge-mode numbers, no >4-channel or mid-alpha layouts, no uint32): a 1.21.11 mod's resize is given
 * back with v1's own code. Each entry point is LWJGL 3.3.3's org_lwjgl_stb_STBImageResize.c one for the address form:
 * the arguments cast as there and the v1 function called. The array forms are done in Java (copied through direct memory,
 * as HotSpot's Get/Release<Type>ArrayElements copy them).
 *
 * STB_IMAGE_RESIZE_STATIC keeps every stbir_* symbol private to this library; the only exports are the JNI functions, so
 * nothing can clash with 3.4.1's lwjgl_stb (stb_image_resize2) loaded in the same process. v1's allocations use the C
 * library's malloc/free (LWJGL 3.3.3 routed them to its configured allocator; output does not depend on the allocator).
 * No JDK header is used: the JNI types this file needs are declared below as the JNI specification defines them.
 */
#include <stdint.h>

#if defined(_WIN32) && !defined(_WIN64)
/* 32-bit Windows: JNI functions are __stdcall, which MinGW-style linkers export as "Name@N"; the JVM looks a JNI function
   up as "_Name@N" (MSVC's decoration) or plain "Name", so the exports are listed in cb_stbir1_x86.def under their plain
   names instead of dllexport */
#define CB_JNIEXPORT
#define CB_JNICALL __stdcall
#elif defined(_WIN32)
#define CB_JNIEXPORT __declspec(dllexport)
#define CB_JNICALL __stdcall
#else
#define CB_JNIEXPORT __attribute__((visibility("default")))
#define CB_JNICALL
#endif
typedef int32_t jint;
typedef int64_t jlong;
typedef float jfloat;
typedef void JNIEnv_;
typedef void *jclass_;

#define STBIR_ASSERT(x)
#define STB_IMAGE_RESIZE_IMPLEMENTATION
#define STB_IMAGE_RESIZE_STATIC
#include "stb_image_resize.h"

#define CB_FN(name) CB_JNIEXPORT jint CB_JNICALL Java_top_dext_centurybridge_stbir1_STBImageResize_00024Native_##name
/* JNI name mangling: the "$" of the nested class is "_00024", a "_" in a method name is "_1" */
#define P(type, a) ((type)(uintptr_t)(a))

CB_FN(nstbir_1resize_1uint8)(JNIEnv_ *env, jclass_ clazz, jlong in, jint iw, jint ih, jint is, jlong out, jint ow, jint oh, jint os,
        jint channels) {
    (void)env; (void)clazz;
    return (jint)stbir_resize_uint8(P(unsigned char const *, in), iw, ih, is, P(unsigned char *, out), ow, oh, os, channels);
}

CB_FN(nstbir_1resize_1float)(JNIEnv_ *env, jclass_ clazz, jlong in, jint iw, jint ih, jint is, jlong out, jint ow, jint oh, jint os,
        jint channels) {
    (void)env; (void)clazz;
    return (jint)stbir_resize_float(P(float const *, in), iw, ih, is, P(float *, out), ow, oh, os, channels);
}

CB_FN(nstbir_1resize_1uint8_1srgb)(JNIEnv_ *env, jclass_ clazz, jlong in, jint iw, jint ih, jint is, jlong out, jint ow, jint oh,
        jint os, jint channels, jint alpha, jint flags) {
    (void)env; (void)clazz;
    return (jint)stbir_resize_uint8_srgb(P(unsigned char const *, in), iw, ih, is, P(unsigned char *, out), ow, oh, os, channels,
        alpha, flags);
}

CB_FN(nstbir_1resize_1uint8_1srgb_1edgemode)(JNIEnv_ *env, jclass_ clazz, jlong in, jint iw, jint ih, jint is, jlong out, jint ow,
        jint oh, jint os, jint channels, jint alpha, jint flags, jint edge) {
    (void)env; (void)clazz;
    return (jint)stbir_resize_uint8_srgb_edgemode(P(unsigned char const *, in), iw, ih, is, P(unsigned char *, out), ow, oh, os,
        channels, alpha, flags, (stbir_edge)edge);
}

CB_FN(nstbir_1resize_1uint8_1generic)(JNIEnv_ *env, jclass_ clazz, jlong in, jint iw, jint ih, jint is, jlong out, jint ow, jint oh,
        jint os, jint channels, jint alpha, jint flags, jint edge, jint filter, jint space, jlong ctx) {
    (void)env; (void)clazz;
    return (jint)stbir_resize_uint8_generic(P(unsigned char const *, in), iw, ih, is, P(unsigned char *, out), ow, oh, os, channels,
        alpha, flags, (stbir_edge)edge, (stbir_filter)filter, (stbir_colorspace)space, P(void *, ctx));
}

CB_FN(nstbir_1resize_1uint16_1generic)(JNIEnv_ *env, jclass_ clazz, jlong in, jint iw, jint ih, jint is, jlong out, jint ow, jint oh,
        jint os, jint channels, jint alpha, jint flags, jint edge, jint filter, jint space, jlong ctx) {
    (void)env; (void)clazz;
    return (jint)stbir_resize_uint16_generic(P(stbir_uint16 const *, in), iw, ih, is, P(stbir_uint16 *, out), ow, oh, os, channels,
        alpha, flags, (stbir_edge)edge, (stbir_filter)filter, (stbir_colorspace)space, P(void *, ctx));
}

CB_FN(nstbir_1resize_1float_1generic)(JNIEnv_ *env, jclass_ clazz, jlong in, jint iw, jint ih, jint is, jlong out, jint ow, jint oh,
        jint os, jint channels, jint alpha, jint flags, jint edge, jint filter, jint space, jlong ctx) {
    (void)env; (void)clazz;
    return (jint)stbir_resize_float_generic(P(float const *, in), iw, ih, is, P(float *, out), ow, oh, os, channels, alpha, flags,
        (stbir_edge)edge, (stbir_filter)filter, (stbir_colorspace)space, P(void *, ctx));
}

CB_FN(nstbir_1resize)(JNIEnv_ *env, jclass_ clazz, jlong in, jint iw, jint ih, jint is, jlong out, jint ow, jint oh, jint os,
        jint datatype, jint channels, jint alpha, jint flags, jint edge_h, jint edge_v, jint filter_h, jint filter_v, jint space,
        jlong ctx) {
    (void)env; (void)clazz;
    return (jint)stbir_resize(P(void const *, in), iw, ih, is, P(void *, out), ow, oh, os, (stbir_datatype)datatype, channels, alpha,
        flags, (stbir_edge)edge_h, (stbir_edge)edge_v, (stbir_filter)filter_h, (stbir_filter)filter_v, (stbir_colorspace)space,
        P(void *, ctx));
}

CB_FN(nstbir_1resize_1subpixel)(JNIEnv_ *env, jclass_ clazz, jlong in, jint iw, jint ih, jint is, jlong out, jint ow, jint oh,
        jint os, jint datatype, jint channels, jint alpha, jint flags, jint edge_h, jint edge_v, jint filter_h, jint filter_v,
        jint space, jlong ctx, jfloat x_scale, jfloat y_scale, jfloat x_offset, jfloat y_offset) {
    (void)env; (void)clazz;
    return (jint)stbir_resize_subpixel(P(void const *, in), iw, ih, is, P(void *, out), ow, oh, os, (stbir_datatype)datatype, channels,
        alpha, flags, (stbir_edge)edge_h, (stbir_edge)edge_v, (stbir_filter)filter_h, (stbir_filter)filter_v, (stbir_colorspace)space,
        P(void *, ctx), x_scale, y_scale, x_offset, y_offset);
}

CB_FN(nstbir_1resize_1region)(JNIEnv_ *env, jclass_ clazz, jlong in, jint iw, jint ih, jint is, jlong out, jint ow, jint oh,
        jint os, jint datatype, jint channels, jint alpha, jint flags, jint edge_h, jint edge_v, jint filter_h, jint filter_v,
        jint space, jlong ctx, jfloat s0, jfloat t0, jfloat s1, jfloat t1) {
    (void)env; (void)clazz;
    return (jint)stbir_resize_region(P(void const *, in), iw, ih, is, P(void *, out), ow, oh, os, (stbir_datatype)datatype, channels,
        alpha, flags, (stbir_edge)edge_h, (stbir_edge)edge_v, (stbir_filter)filter_h, (stbir_filter)filter_v, (stbir_colorspace)space,
        P(void *, ctx), s0, t0, s1, t1);
}
