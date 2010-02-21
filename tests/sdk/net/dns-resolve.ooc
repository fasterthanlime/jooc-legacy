import net/DNS

main: func {
    // perform DNS lookup for ooc-lang.org
    ip := DNS resolveOne("ooc-lang.org")
    "ooc-lang.org IP address is %s" format(ip toString()) println()
}
