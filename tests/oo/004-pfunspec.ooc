include sys/socket

PF_UNSPEC: extern Int

ProtocolFamily: class {
    unspec = PF_UNSPEC: static const Int
    unspec2 : static const Int = PF_UNSPEC
    unspec3 := static const PF_UNSPEC
}

printf("unspec is (%d, %d, %d)\n", ProtocolFamily unspec, ProtocolFamily unspec2, ProtocolFamily unspec3)
