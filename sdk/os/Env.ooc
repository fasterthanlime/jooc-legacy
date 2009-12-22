include unistd| (_BSD_SOURCE), stdlib | (_BSD_SOURCE)

getenv: extern func (path: String) -> String
setenv: extern func (key, value: String, overwrite: Bool) -> Int
unsetenv: extern func (key: String) -> Int

Env: class {
    get: static func (variableName: String) -> String {
        return getenv(variableName)
    }

    set: static func (key, value: String, overwrite: Bool) -> Int {
        setenv(key, value, overwrite)
    }

    set: static func ~overwrite (key, value: String) -> Int {
        set(key, value, true)
    }

    unset: static func (key: String) -> Int {
        unsetenv(key)
    }
    
    /* clearenv is not used since it's not part of the POSIX-2001 standard
     * and not available, for example, on OSX */
}
