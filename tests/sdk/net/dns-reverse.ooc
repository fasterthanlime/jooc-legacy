import net/[DNS, Address]

main: func {
    "Performing reverse DNS lookup of 69.65.33.41" println()

    ip := IP4Address new("69.65.33.41")
    hostname := DNS reverse(ip as IPAddress)
    "Hostname = %s" format(hostname) println()
}
