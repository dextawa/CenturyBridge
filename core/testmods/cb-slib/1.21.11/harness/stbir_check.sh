#!/usr/bin/env bash
# stbir_check.sh <workDir>: the wall layer's STBImageResize (rt/CbLibStbir1 + the cb_lwjgl_stbir1 natives) against
# LWJGL 3.3.3 itself, on beta2 linux x64, run from ~/cbwork after remote_step.sh has built <workDir>'s 26.1 layer.
#   A: StbirHarness compiled against 1.21.11's LWJGL 3.3.3, run there with LWJGL's own 3.3.3 natives (Maven) -- the reference.
#   B: the same jar converted 1.21.11 -> 26.1 by <workDir>'s engine and wiring, run on 26.1's LWJGL 3.4.1 with the layer jar.
# A and B must write byte-identical out.bin / out.txt.
set -euo pipefail
cd ~/cbwork
WD="$1"; X=$WD/stbircheck; JDK=~/cbtest/jdk-25.0.4.1+1/bin
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
V=data/runtime/client-1.21.11/libraries/org/lwjgl; W=data/runtime/client-26.1/libraries/org/lwjgl
V_CP=$V/lwjgl/3.3.3/lwjgl-3.3.3.jar:$V/lwjgl-stb/3.3.3/lwjgl-stb-3.3.3.jar
rm -rf $X/a $X/b $X/conv $X/classes && mkdir -p $X/a $X/b $X/ref $X/classes
for a in lwjgl lwjgl-stb; do
    [ -f $X/ref/$a-3.3.3-natives-linux.jar ] || curl -sfL -o $X/ref/$a-3.3.3-natives-linux.jar \
        https://repo1.maven.org/maven2/org/lwjgl/$a/3.3.3/$a-3.3.3-natives-linux.jar
done
"$JDK/javac" --release 21 -nowarn -cp $V_CP -d $X/classes $WD/core/testmods/cb-slib/1.21.11/harness/StbirHarness.java
(cd $X/classes && "$JDK/jar" --create --file ../cb-stbir-harness.jar --date=2026-01-01T00:00:00Z .)
"$JDK/java" -Dorg.lwjgl.system.SharedLibraryExtractPath=$X/a/ext \
    -cp $X/cb-stbir-harness.jar:$V_CP:$X/ref/lwjgl-3.3.3-natives-linux.jar:$X/ref/lwjgl-stb-3.3.3-natives-linux.jar \
    cb.slib.StbirHarness $X/a/out
"$JDK/java" -Xmx4g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.21.11 26.1 \
    $X/conv $X/cb-stbir-harness.jar > $X/convert.log 2>&1 || { tail -5 $X/convert.log; exit 1; }
CONV=$(ls $X/conv/*+26.1.jar | head -1)
"$JDK/java" -Dorg.lwjgl.util.Debug=true -Dorg.lwjgl.system.SharedLibraryExtractPath=$X/b/ext \
    -cp $CONV:$WD/build/centurybridge-0.3.0+26.1.jar:$W/lwjgl/3.4.1/lwjgl-3.4.1.jar:$W/lwjgl/3.4.1/lwjgl-3.4.1-natives-linux.jar:$W/lwjgl-stb/3.4.1/lwjgl-stb-3.4.1.jar:$W/lwjgl-stb/3.4.1/lwjgl-stb-3.4.1-natives-linux.jar \
    cb.slib.StbirHarness $X/b/out 2>&1 | grep -E 'STBIR|stbir1|ERROR|Exception' | head
H=$(unzip -p $CONV cb/slib/StbirHarness.class | strings)
echo "converted harness: $(grep -c CbLibStbir1 <<< "$H") reference(s) to CbLibStbir1, $(grep -c 'org/lwjgl/stb/STBImageResize' <<< "$H") to STBImageResize"
cmp $X/a/out.bin $X/b/out.bin && echo "out.bin byte-identical ($(stat -c %s $X/a/out.bin) bytes)"
cmp $X/a/out.txt $X/b/out.txt && echo "out.txt byte-identical: $(wc -l < $X/a/out.txt) calls, $(grep -c false $X/a/out.txt || true) returned false"
sha256sum $X/a/out.bin $X/b/out.bin
