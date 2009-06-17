package org.ooc.compiler;

import java.io.IOException;

import org.ubi.SourceReader;

/**
 * Provides the source code of a file requested by the compiler (e.g. by dependency
 * or directly specified by the user). Often, it's just a {@link FileContentProvider}
 * but it can also be fetched directly from memory, e.g. a caching mechanism or
 * for an integrated development environment.
 * 
 * @author Amos Wenger
 */
public abstract class ContentProvider {
	
	/** The sourcepath this content provider takes its path information from */
	protected SourcePath sourcePath;
	
	/**
	 * Create a content provider for the specified sourcePath.
	 * @param sourcePath
	 */
	public ContentProvider(SourcePath sourcePath) {
		
		this.sourcePath = sourcePath;
		
	}
	
	/**
	 * Return the content of a source, given its fully qualified name, e.g.
	 * "my.package.MyClass"
	 * @param fullSourceName
	 * @return
	 * @throws IOException
	 */
	public String getContent(String fullSourceName) throws IOException {
		
		return getContent(new SourceInfo(fullSourceName));
		
	}
	
	/**
	 * Return the content of a source, given its source info
	 * @param info
	 * @return
	 * @throws IOException
	 */
	public abstract String getContent(SourceInfo info) throws IOException;

	/**
	 * Return a reader to this source, given its source info
	 * @param info
	 * @return
	 * @throws IOException
	 */
	public abstract SourceReader getReader(SourceInfo info) throws IOException;

	/**
	 * Change the source path of this provider
	 * @param sourcePath
	 */
	public void setSourcePath(SourcePath sourcePath) {

		this.sourcePath = sourcePath;
		
	}
	
}
