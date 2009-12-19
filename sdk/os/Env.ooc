include unistd| (_BSD_SOURCE), stdlib | (_BSD_SOURCE)

import structs/HashMap

getenv: extern func (path: String) -> String
setenv: extern func (key, value: String, overwrite: Bool) -> Int
unsetenv: extern func (key: String) -> Int
clearenv: extern func -> Int

Env: class {
    get: static func (variableName: String) -> String {
        return getenv(variableName)
    }

    set: static func (key, value: String, overwrite: Bool) -> Int {
        setenv(key, value, overwrite)
    }

    set: static func ~overwrite (key, value: String) -> Int {
        set(key, value)
    }

    unset: static func (key: String) -> Int {
        unsetenv(key)
    }

    clear: static func {
        clearenv()
    }
}
