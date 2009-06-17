package org.ooc.compiler;

import java.io.File;
import java.io.IOException;

import org.ubi.SourceReader;

/**
 * A simple content provider which reads them from file
 * 
 * @author Amos Wenger
 */
public class FileContentProvider extends ContentProvider {

	/**
	 * Default constructor
	 * @param sourcePath
	 */
	public FileContentProvider(SourcePath sourcePath) {
		
		super(sourcePath);
		
	}

	@Override
	public String getContent(SourceInfo info) throws IOException {
			
		File file = sourcePath.getFile(info);
		return SourceReader.readToString(file);
			
	}

	@Override
	public SourceReader getReader(SourceInfo info) throws IOException {

		File file = sourcePath.getFile(info);
		return SourceReader.getReaderFromFile(file);
		
	}
	
}