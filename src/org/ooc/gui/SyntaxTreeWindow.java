package org.ooc.gui;

import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.ooc.backends.ProjectInfo;
import org.ooc.compiler.ContentProvider;
import org.ooc.compiler.FileContentProvider;
import org.ooc.compiler.SourcePath;
import org.ooc.errors.SourceContext;
import org.ooc.parsers.SourceParser;

/**
 * A tool to see the progress of the build process.
 * 
 * @author Amos Wenger
 */
public class SyntaxTreeWindow {
	
	private final JFrame frame;
	
	/**
	 * Default Constructor
	 * @param sourcePath
	 * @param fullSourceNameList
	 * @param projInfo 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public SyntaxTreeWindow(SourcePath sourcePath, List<String> fullSourceNameList, ProjectInfo projInfo) throws FileNotFoundException, IOException {
		
		frame = new JFrame("ooc - Syntax Tree Viewer");
		frame.setLayout(new BorderLayout());
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		frame.add(splitPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		splitPane.setLeftComponent(tabbedPane);
		
		ContentProvider provider = new FileContentProvider(sourcePath);
		SourceParser parser = new SourceParser(provider);
		ErrorsPanel errorsPanel = new ErrorsPanel();
		splitPane.setRightComponent(errorsPanel);
		splitPane.setDividerLocation(400);
		
		for(String fullSourceName: fullSourceNameList) {
			try {
				parser.parse(projInfo, fullSourceName, false);
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
		for(SourceContext context: parser.getSources().values()) {
			SyntaxTreeWidget tab = new SyntaxTreeWidget(errorsPanel, context);
			tabbedPane.addTab(tab.getTitle(), tab);
		}
		
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

	/**
	 * Show the window. This method is blocking.
	 */
	public void show() {
		
		while(frame.isVisible()) {
			try {
				Thread.sleep(200L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
