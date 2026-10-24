# lib_dirs.sh: the one rule for "release V's game libraries directory" on beta2 (source it; run from ~/cbwork).
# data/runtime/client-1.20.1/libraries holds 1.20.2's libraries (29 artifacts at the wrong version); until the data
# directory is corrected, 1.20.1's own are read from ~/cbtest/client-1.20.1/libraries. Delete the case once fixed.
libdir() {
    case "$1" in
        1.20.1) echo "$HOME/cbtest/client-1.20.1/libraries" ;;
        *) echo "data/runtime/client-$1/libraries" ;;
    esac
}
