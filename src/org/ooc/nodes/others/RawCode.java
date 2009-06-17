package org.ooc.nodes.others;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ubi.FileLocation;
import org.ubi.SourceReader;

/**
 * Used to represent fragment of codes as strings. It's usually a sign that
 * the ooc compiler shouldn't mess further with it.
 * A class extending RawCode or instanciating it means that it's probably
 * a temporary working solution, until a better (and better designed) one
 * is added.
 *
 * @author Amos Wenger
 */
public class RawCode extends SyntaxNode {

	/** the code fragment, as a string */
    public String content;

    /**
     * Default constructor, with specified content
     * @param location
     * @param content
     */
    public RawCode(FileLocation location, String content) {
        super(location);
        this.content = content;
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	writeWhitespace(a);
        a.append(content);
    }
    
    @Override
    protected void assembleImpl(AssemblyManager manager) {
    	// Do nothing =)
    }
    
	@Override
	public String getDescription() {
		return getClass().getSimpleName()+": '"+SourceReader.spelled(content)+"'"+location;
	}

}
