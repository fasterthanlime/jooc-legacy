cc=""
flags="-t -r -dyngc $cc"

mkdir -p test-suite
cd test-suite/ && ooc $(find ../tests/ -name "*.ooc") $flags 2>&1
#| grep -e TOTAL -e compiled
