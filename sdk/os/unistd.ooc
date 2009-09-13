include unistd

/* Functions */
dup2: extern func(Int, Int) -> Int
fork: extern func -> Int
execv: extern func(String, String*) -> Int

execvp: extern func(String, String*) -> Int
execve: extern func(String, String*, String*) -> Int

