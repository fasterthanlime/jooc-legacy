include dictionary

dictionary: extern cover {
    n: extern Int              /* Number of entries in dictionary */
    val: extern String*        /* List of string values */
    size: extern Int           /* Storage size */
    key: extern String*        /* List of string keys */
    hash: extern UInt*         /* List of hash values for keys */
}
DictPtr: cover from dictionary*

dictionary_hash:  extern func(key: String) -> UInt
dictionary_new:   extern func(size: Int) -> DictPtr
dictionary_del:   extern func(vd: DictPtr)
dictionary_get:   extern func(d: DictPtr, key, def: String) -> String
dictionary_set:   extern func(vd: DictPtr, key, val: String) -> Int
dictionary_unset: extern func(d: DictPtr, key: String)
dictionary_dump:  extern func(d: DictPtr, out: FILE*)
