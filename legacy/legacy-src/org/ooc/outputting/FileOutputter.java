package org.ooc.outputting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ooc.backends.ProjectInfo;
import org.ooc.compiler.BuildProperties;
import org.ooc.compiler.SourceInfo;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.libs.Include;
import org.ooc.nodes.libs.Include.IncludeLanguage;
import org.ooc.nodes.libs.Include.IncludeType;

/**
 * A simple file outputter. Writes C source and headers files in the
 * output path specified in build properties.
 * 
 * @author Amos Wenger
 */
public class FileOutputter extends Outputter {

	/**
	 * Write all modules and directories in the output path specified
	 * in build properties. 
	 */
	
	@Override
	public void output(BuildProperties props, ProjectInfo projInfo, SourceContext mainSource) throws IOException {

		File outPathFile = new File(props.outPath);
		if(!props.outPath.isEmpty() && !outPathFile.exists() && !outPathFile.mkdirs()) {
			throw new IOException("Could not create directory structure for directory '"+props.outPath+"'");
		}
		
		for(SourceContext context: projInfo.modules.values()) {
			write(context, projInfo, props);
		}
		
		for(SourceContext context: projInfo.executables.values()) {
			write(context, projInfo, props);
		}
		
	}

	/**
	 * Writes the source context in the output path specified in build
	 * properties, creating the necessary subdirectories (for packages)
	 * if needed.
	 * @param context
	 * @param projInfo
	 * @param props
	 * @throws IOException
	 */
	protected void write(SourceContext context, ProjectInfo projInfo, BuildProperties props) throws IOException {
		
        SourceInfo info = context.source.getInfo();
		
		String fullName = info.fullName;
		File cFile = new File(projInfo.getOutPath(fullName, ".c"));
		File hFile = new File(projInfo.getOutPath(fullName, ".h"));
		if(!cFile.getParentFile().exists() && !cFile.getParentFile().mkdirs()) {
			throw new IOException("Could not create directory structure for file '"+cFile.getPath());
		}

        write(context, cFile, hFile);
        
        handleIncludes(context, projInfo, props, info, hFile);
		
	}

	protected void write(SourceContext context, File cFile, File hFile)
			throws IOException {
		
		context.source.writeToC(cFile, hFile);
		
	}

	protected void handleIncludes(SourceContext context, ProjectInfo projInfo,
			BuildProperties props, SourceInfo info, File hFile)
			throws FileNotFoundException, IOException {
		
		for(Include include: context.source.getRoot().getNodesTyped(Include.class, true)) {
        	if(include.getType() == IncludeType.LOCAL) {
        		//System.out.println("Reviewing local include '"+include.getPath()+"'");
        		
        		File pathElement = props.sourcePath.getFile(info);
        		File hDst = new File(hFile.getParent(), include.getPath());
        		File hSrc = new File(pathElement.getParent(), include.getPath());
        		
        		if((!hDst.exists() || include.getLanguage() == IncludeLanguage.CPP) && hSrc.exists()) {
        			//System.out.println("Copying H file '"+hSrc.getPath()+"' to '"+hDst.getPath()+"'");
        			FileUtils.copy(hSrc, hDst);
        			
        			if(include.getLanguage() == IncludeLanguage.CPP) {
        				String cppPath = include.getPath().replace(".h", ".cpp");
						File cppDst = new File(hFile.getParent(), cppPath);
                		File cppSrc = new File(pathElement.getParent(), include.getPath().replace(".h", ".cpp"));
                		if(cppSrc.exists()) {
                			//System.out.println("Copying CPP module '"+cppPath+"' to '"+cppDst.getPath()+"'");
                			projInfo.addCppModule(cppDst);
                			FileUtils.copy(cppSrc, cppDst);
                		}
        			} else if(include.getLanguage() == IncludeLanguage.C) {
        				String cPath = include.getPath().replace(".h", ".c");
						File cDst = new File(hFile.getParent(), cPath);
                		File cSrc = new File(pathElement.getParent(), include.getPath().replace(".h", ".c"));
                		if(cSrc.exists()) {
                			FileUtils.copy(cSrc, cDst);
                		}
        			}
        		}
        	}
        }
	}
	
}
