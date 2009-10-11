package org.ooc.backend;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.ooc.utils.FileUtils;

public class CachedFileWriter extends StringWriter {

	private File file;

	public CachedFileWriter(File file) {
		super();
		this.file = file;
	}

	@Override
	public void close() throws IOException {
		String thisContent = toString();
		
		if(!file.exists()) {
			FileUtils.write(file, thisContent);
		} else {		
			String fileContent = FileUtils.read(file);
			if(!fileContent.equals(thisContent)) {
				FileUtils.write(file, thisContent);
			}
		}
		
		super.close();
	}
	
}
