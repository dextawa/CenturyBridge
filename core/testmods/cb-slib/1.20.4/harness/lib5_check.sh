#!/usr/bin/env bash
# lib5_check.sh <workDir>: the 1.20.5 step layer's library bridges against 1.20.4's libraries themselves, on beta2 (run
# from ~/cbwork after remote_step.sh has built <workDir>'s 1.20.5 layer).
#   A: Lib5Harness compiled against 1.20.4's libraries (log4j 2.19.0, DFU 6.0.8, LWJGL 3.3.2) and run on them -- the reference.
#   B: the same jar converted 1.20.4 -> 1.20.5 by <workDir>'s engine and wiring, run on 1.20.5's libraries with the layer jar.
# A and B must write identical output.
set -euo pipefail
cd ~/cbwork
WD="$1"; X=$WD/lib5check; JDK=~/cbtest/jdk-25.0.4.1+1/bin
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
cp_of() { find data/runtime/client-$1/libraries -name '*.jar' ! -name '*natives*' | sort | tr '\n' ':'; }
VCP=$(cp_of 1.20.4); WCP=$(cp_of 1.20.5)
natives() { # <version> <lwjgl version>: linux x64 natives of lwjgl and lwjgl-stb... only core is needed
    local d=$X/natives-$2; mkdir -p $d
    [ -f $d/lwjgl-$2-natives-linux.jar ] || curl -sfL -o $d/lwjgl-$2-natives-linux.jar \
        https://repo1.maven.org/maven2/org/lwjgl/lwjgl/$2/lwjgl-$2-natives-linux.jar
    echo $d/lwjgl-$2-natives-linux.jar
}
rm -rf $X/a $X/b $X/conv $X/classes && mkdir -p $X/a $X/b $X/classes
"$JDK/javac" --release 17 -nowarn -cp "$VCP" -d $X/classes $WD/core/testmods/cb-slib/1.20.4/harness/Lib5Harness.java
(cd $X/classes && "$JDK/jar" --create --file ../cb-lib5-harness.jar --date=2026-01-01T00:00:00Z .)
"$JDK/java" -Dorg.lwjgl.system.allocator=system -Dorg.lwjgl.system.SharedLibraryExtractPath=$X/a/ext -cp "$X/cb-lib5-harness.jar:$VCP$(natives 1.20.4 3.3.2)" \
    cb.slib.Lib5Harness $X/a/out.txt
"$JDK/java" -Xmx4g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.20.4 1.20.5 \
    $X/conv $X/cb-lib5-harness.jar > $X/convert.log 2>&1 || { tail -5 $X/convert.log; exit 1; }
CONV=$(ls $X/conv/*+1.20.5.jar | head -1)
"$JDK/java" -Dorg.lwjgl.system.allocator=system -Dorg.lwjgl.system.SharedLibraryExtractPath=$X/b/ext \
    -cp "$CONV:$WD/build/centurybridge-0.3.0+1.20.5.jar:$WCP$(natives 1.20.5 3.3.3)" cb.slib.Lib5Harness $X/b/out.txt
echo "lines: $(wc -l < $X/a/out.txt) reference, $(wc -l < $X/b/out.txt) converted; threw: $(grep -c ': threw ' $X/a/out.txt || true) / $(grep -c ': threw ' $X/b/out.txt || true)"
if cmp -s $X/a/out.txt $X/b/out.txt; then echo "IDENTICAL"; else echo "DIFFER"; diff $X/a/out.txt $X/b/out.txt || true; fi
