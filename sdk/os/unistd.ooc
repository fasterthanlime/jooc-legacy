//import structs/ArrayList

include unistd
include linux/limits

PIPE_BUF: extern Int
STDOUT_FILENO: extern Int
STDERR_FILENO: extern Int

/* Functions */
dup2: extern func(Int, Int) -> Int
fork: extern func -> Int
execv: extern func(String, String*) -> Int
execvp: extern func(String, String*) -> Int
execve: extern func(String, String*, String*) -> Int
pipe: extern func(arg: Int*) -> Int
  
