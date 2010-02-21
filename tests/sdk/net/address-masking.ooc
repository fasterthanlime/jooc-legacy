import net/Address

main: func {
    addr := IP4Address new("192.168.10.1")
    "Before masking: %s" format(addr toString()) println()

    mask := IP4Address new("255.255.0.255")
    addr mask(mask)

    "After masking: %s" format(addr toString()) println()
}
