package org.ooc.structures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ooc.compiler.SourceInfo;
import org.ooc.nodes.RootNode;
import org.ooc.nodes.interfaces.WriteableToPureC;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;


/**
 * Structure that holds the root node, and some headers.
 * Manages the order includes are written in.
 * @see RootNode
 *
 * @author Amos Wenger
 */
public class Source implements WriteableToPureC {

	private final SourceInfo info;
    private final String headerDefine;
    private final RootNode root;
    /**
     * Default constructor
     * @param root
     * @param info
     */
    public Source(RootNode root, SourceInfo info) {
    	
        this.info = info;
        
        String underName = info.fullName.replace(".", SyntaxNode.SEPARATOR)+"_h";
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < underName.length(); i++) {
        	int c = underName.codePointAt(i);
        	if(Character.isLetter(c) || (i > 0 && Character.isDigit(c))) {
        		builder.appendCodePoint(c);
        	} else {
        		builder.append('_');
        	}
        }
        headerDefine = builder.toString();
        
        this.root = root;
        
	}
    
    /**
     * @return information about the paths etc. of this source
     */
    public SourceInfo getInfo() {
    	
    	return info;
    	
    }

    /**
     * @return the root node of this source
     */
    public SyntaxNodeList getRoot() {
    	
        return root;
        
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	
        root.writeToCSource(a);
        
    }

    @Override
    public void writeToCHeader(Appendable a) throws IOException {
    	
    	// TODO there _has_ to be a better way to do that.
        a.append("#ifndef ");
        a.append(headerDefine);
        a.append("\n#define ");
        a.append(headerDefine);
        a.append("\n\n");
        
        root.writeToCHeader(a);
        
        a.append("\n#endif // ");
        a.append(headerDefine);
        a.append("\n");
        
    }

    /**
     * Generate C code for this source, and writes it to specified files
     * @param cFile
     * @param hFile
     * @throws IOException
     */
	public void writeToC(File cFile, File hFile) throws IOException {
		
		BufferedWriter bW = new BufferedWriter(new FileWriter(hFile));
        writeToCHeader(bW);
        bW.close();
		
		bW = new BufferedWriter(new FileWriter(cFile));
        writeToCSource(bW);
        bW.close();
		
	}
	
	@Override
	public String toString() {
		
		return info.fullName;
		
	}

}
