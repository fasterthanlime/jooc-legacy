package org.ooc.outputting;

import java.io.IOException;

import org.ooc.backends.ProjectInfo;
import org.ooc.compiler.BuildProperties;
import org.ooc.errors.SourceContext;

/**
 * An outputter's task is to write the generated C code somewhere.
 * For a basic implementation (writing to files), see FileOutputter.
 * 
 * Additional outputters can/will be added in time. E.g. an outputter to the
 * standard input of a process would be nice (to pipe source code to gcc directly).
 * 
 * @author Amos Wenger
 */
public abstract class Outputter {
	
	/**
	 * Output specified source
	 * @param props
	 * @param projInfo
	 * @param source
	 * @throws IOException
	 */
	public abstract void output(BuildProperties props, ProjectInfo projInfo, SourceContext source) throws IOException;
	
}
