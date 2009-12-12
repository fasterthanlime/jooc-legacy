getenv: extern func(path: String) -> String

Env: class {
    get: static func(variableName: String) -> String {
        return getenv(variableName)
    }
}
