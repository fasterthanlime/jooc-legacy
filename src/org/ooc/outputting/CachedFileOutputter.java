package org.ooc.outputting;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.ooc.errors.SourceContext;

/**
 * An improved FileOutputter, which only overwrite a file
 * when its' different. 
 * 
 * @author Amos Wenger
 */
public class CachedFileOutputter extends FileOutputter {
	
	@Override
	protected void write(SourceContext context, File cFile, File hFile)
			throws IOException {
		
		if(!cFile.exists() || !hFile.exists()) {
			
			super.write(context, cFile, hFile);
			
		} else {
		
			String cOld = FileUtils.read(cFile);
			String hOld = FileUtils.read(hFile);
			
			StringWriter cNewWriter = new StringWriter();
			StringWriter hNewWriter = new StringWriter();
			context.source.writeToCSource(cNewWriter);
			context.source.writeToCHeader(hNewWriter);
			
			String cNew = cNewWriter.toString();
			String hNew = hNewWriter.toString();
			
			if(!cOld.equals(cNew)) {
				FileUtils.write(cFile, cNew);
			}
			if(!hOld.equals(hNew)) {
				FileUtils.write(hFile, hNew);
			}
			
		}
		
	}
	
}
