import structs.Array;
include unistd, stdio;

class Shell {
	
	static func exec(String executable, Array args) {
		
		String[args.length + 2] cmd;
		cmd[0] = executable;
		for(Int i: 0..args.length) {
			cmd[i + 1] = args.get(i);
		}
		cmd[args.length + 1] = (String) null;
		
		execvp(executable, cmd);
		
	}	
}
