# lib_dirs.sh: the one rule for "release V's game libraries directory" on beta2 (source it; run from ~/cbwork).
libdir() {
    echo "data/runtime/client-$1/libraries"
}
