package org.ooc.nodes.libs;

import java.io.IOException;
import java.util.List;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.CHeader;
import org.ooc.nodes.preprocessor.PreprocessorDirective;
import org.ooc.parsers.CHeaderParser;
import org.ubi.FileLocation;

/**
 * An include, e.g.
 * <code>
 * #include <header.h>
 * </code>
 * or
 * <code>
 * include header;
 * </code>
 *
 * @author Amos Wenger
 */
public class Include extends PreprocessorDirective {

	/**
	 * Where should the include be written
	 *  
	 * @author Amos Wenger
	 */
	public enum IncludePosition {
		/** The include should be written in the header */
		HEADER,
		/** The include should be written in the source */
		SOURCE,
		/** The include should not be written at all, it's there only to resolve dependencies */
		NOWHERE,
	}
	
	/**
	 * The type of the include, e.g.
	 * @author Amos Wenger
	 */
    public enum IncludeType {
    	/**
    	 * <code>
    	 * #include "header.h" // local
    	 * </code>
    	 */
        LOCAL,
        /**
         * <code>
         * #include <header.h> // pathbound
         * </code>
         */
        PATHBOUND,
    }
    
    /**
     * Include language
     * @author Amos Wenger
     */
    public enum IncludeLanguage {
    	/** C language */
    	C,
    	/** C++ language */
    	CPP,
    }

    private final IncludeType type;
    private final IncludePosition position;
    private final IncludeLanguage language;
    private final String path;
	private transient CHeader header;

	/**
	 * Default constructor
	 * @param location
	 * @param type
	 * @param pos
	 * @param path
	 */
	public Include(FileLocation location, IncludeType type, IncludePosition pos, String path) {
    	this(location, type, pos, IncludeLanguage.C, path);
    }
    
	/**
	 * Default constructor
	 * @param location
	 * @param type
	 * @param pos
	 * @param lang
	 * @param path
	 */
	public Include(FileLocation location, IncludeType type, IncludePosition pos, IncludeLanguage lang, String path) {
        super(location);
        this.type = type;
        this.position = pos;
        this.language = lang;
        this.path = path;
        this.header = null; // We don't always parse it, @see assembleImpl
    }
    
    @Override
    protected void assembleImpl(AssemblyManager manager) {
    	
    	if(type == IncludeType.PATHBOUND && language == IncludeLanguage.C && header == null) {
    		try {
    			this.header = CHeaderParser.parse(path, manager.getContext().projInfo.props);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	List<Include> includes = manager.getContext().source.getRoot().getNodesTyped(Include.class);
    	for(int i = 0; i < includes.size(); i++) {
    		Include include = includes.get(i);
    		if(include != this && include.equals(this)) {
    			include.freeze(manager);
    			include.drop();
    		}
    	}
    	
    	manager.getContext().processInclude(this, manager);
    	
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	if(position == IncludePosition.SOURCE) {
    		write(a);
    	}    	
    }
    
    @Override
    public void writeToCHeader(Appendable a) throws IOException {
    	if(position == IncludePosition.HEADER) {
    		write(a);
    	}
    }
    
    private void write(Appendable a) throws IOException {
    	
    	a.append("#include ");
        switch(type) {
            case LOCAL:
                a.append('"');
                break;
            case PATHBOUND:
                a.append('<');
                break;
        }
        a.append(path);
        switch(type) {
            case LOCAL:
                a.append('"');
                break;
            case PATHBOUND:
                a.append('>');
                break;
        }
        a.append("\n");
    	
    }

	@Override
	public String getDescription() {
		return toString() + location;
	}

	/**
	 * @return the type of the include
	 */
	public IncludeType getType() {
		return type;
	}

	/**
	 * @return the position of the include
	 */
	public IncludePosition getPosition() {
		return position;
	}
	
	/**
	 * @return the language of the include
	 */
	public IncludeLanguage getLanguage() {
		return language;
	}
	
	/**
	 * @return the path of the include
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * @return the header. Note: may be null, if it has not been parsed for
	 * any reason.
	 */
	public CHeader getHeader() {
		return header;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Include) {
			Include include = (Include) obj;
			return type == include.type && language == include.language && path.equals(include.path);
		}
		
		return super.equals(obj);
	}

}
