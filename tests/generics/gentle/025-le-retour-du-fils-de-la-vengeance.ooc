import structs/ArrayList

Value: class <T> {

}

ValueList: class extends ArrayList<Value<Pointer>> {}

test: func (ctx: Pointer) {
    /* works */
    ctx as ArrayList<Value<Pointer>> get(1) as Value<Pointer>
    /* does not work */
    //ctx as ValueList get(1) as Value<Pointer>
}

main: func {
    vl := ArrayList<Value<Pointer>> new()
    test(vl)
}
