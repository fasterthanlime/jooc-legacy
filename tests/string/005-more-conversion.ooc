use math

main: func {
    // NOTE: You can also use the 'as String' operator on all number classes as well
    
    mint: Int = -32767; muint:UInt = 65535
    mint8: Int8 = -128; muint8: UInt8 = 255; mint16: Int16 = -32768; muint16: UInt16 = 65535
    mint32: Int32 = -2147483648; muint32: UInt32 = 4294967295; mint64: Int64 = -9223372036854775808; muint64: UInt64 = 18446744073709551615
    moctet: Octet = 65535;
    
    mlong: Long = -2147483648; mulong: ULong = 4294967295; mllong: LLong = -9223372036854775808; mullong: ULLong = 18446744073709551615
    mbool: Bool = true; mbool2: Bool = false
    
    mint toString() println()
    muint toString() println()
    mint8 toString() println()
    muint8 toString() println()
    mint16 toString() println()
    muint16 toString() println()
    mint32 toString() println()
    muint32 toString() println()
    mint64 toString() println()
    muint64 toString() println()
    moctet toString() println()
    
    mlong toString() println()
    mulong toString() println()
    mllong toString() println()
    mullong toString() println()
    mbool toString() println()
    mbool2 toString() println()
    
    mfloat: Float = 100000.0; mdouble: Double = 1000000000.0; mldouble: LDouble = 1000000000000.0
    
    mfloat toString() println()
    mdouble toString() println()
    mldouble toString() println()
}

