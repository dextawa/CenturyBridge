#!/usr/bin/env bash
# smixin3 (m) test: a 1.21.3 mod implementing ParticleRenderType and overriding Particle.getRenderType, converted 1.21.3 -> 1.21.4 with a test splitRenames entry
set -e
cd ~/cbwork; A=agents/smixin3; T=$A/mt
J=$(ls -d ~/cbtest/jdk-25*/bin | head -1)
rm -rf $T/cls $T/out $T/x $T/wiring; mkdir -p $T/cls $T/out $T/x
$J/javac -nowarn --release 21 -proc:none -cp "data/jars/client-1.21.3-intermediary.jar" -d $T/cls $(find $T/src -name "*.java")
cp $T/res/* $T/cls/
(cd $T/cls && $J/jar cf ../cb-mtest.jar .)
# the step's wiring plus a test entry (the converters' descriptors as the layer's would be; this test only reads bytecode)
cp $A/core/segments/steps/1.21.3__1.21.4.json $T/wiring.json; cp -r $A/core/segments/steps/1.21.3__1.21.4.json.d $T/wiring.json.d
cat > $T/wiring.json.d/zz-mtest.json <<'Y'
{"splitRenames": {"net/minecraft/class_3999": {"facade": "cbm/Facade", "toW": "cbm/Conv#toW(Lcbm/Facade;)Lnet/minecraft/class_3999;", "toV": "cbm/Conv#toV(Lnet/minecraft/class_3999;)Lcbm/Facade;", "basis": "test"}}}
Y
LIBS=$(ls data/runtime/client-1.21.4/libraries/org/ow2/asm/*/*/*.jar data/runtime/client-1.21.4/libraries/com/google/code/gson/gson/*/*.jar | tr '\n' ':')
CP="$A/core/build/classes:$LIBS"
$J/java -Xmx8g -Dcb.wiring=$T/wiring.json -Dcb.layerJar=$A/build/centurybridge-0.3.0+1.21.4.jar -cp "$CP" top.dext.centurybridge.Main convert $A/core/segments 1.21.3 1.21.4 $T/out $T/cb-mtest.jar > $T/convert.log 2>&1 || { tail -20 $T/convert.log; exit 1; }
OUT=$(ls $T/out/*.jar | head -1); echo "converted: $OUT"
(cd $T/x && unzip -q -o ~/cbwork/$OUT)
grep -a "split rename" $T/out/*.md | head
for c in cbm.MyType cbm.MyParticle; do $J/javap -c -p -cp $T/x $c | grep -E "class |implements|class_3999|Facade|Conv|public|static" | head -30; done
mkdir -p $T/vcls; $J/javac -nowarn -cp "$CP" -d $T/vcls $A/core/tools/BytecodeVerify.java; $J/java -cp "$T/vcls:$CP" BytecodeVerify $T/out 2>&1 | tail -4
