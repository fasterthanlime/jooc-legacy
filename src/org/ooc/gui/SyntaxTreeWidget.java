package org.ooc.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ubi.CompilationFailedError;

/**
 * A widget which represents every node in the syntax tree.
 * 
 * @author Amos Wenger
 */
public class SyntaxTreeWidget extends JPanel {

	/**
	 * 
	 */
	protected static final long serialVersionUID = 6559650063649450237L;
	
	SyntaxTreeNode root;
	TreeModel model;
	JTree tree;
	AssemblyManager manager;
	int passNumber;
	protected String title;
	
	/**
	 * Shows tree of 'source', show errors in errorsPanel
	 * @param errorsPanel
	 * @param source
	 */
	public SyntaxTreeWidget(final ErrorsPanel errorsPanel, SourceContext source) {
		
		this(errorsPanel, new AssemblyManager(source), 0);
		
	}

	SyntaxTreeWidget(final ErrorsPanel errorsPanel, final AssemblyManager manager, final int passNumber) {
	
		super(new BorderLayout());
		
		this.manager = manager;
		manager.addAssemblyErrorListener(errorsPanel);
		
		if(passNumber == 0) {
			manager.queueRecursive(manager.getContext().source.getRoot(), "Initial SyntaxTreeTab request to queue all nodes recursively.");
		}
		
		this.root = new SyntaxTreeNode(manager, manager.getContext().source.getRoot());
		this.model = new DefaultTreeModel(root);
		this.tree = new JTree(model);
		this.passNumber = passNumber;
		this.add(new JScrollPane(tree));
		
		JPanel toolbar = new JPanel();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.LINE_AXIS));
		this.add(toolbar, BorderLayout.NORTH);
		
		JButton next = new JButton();
		try {
			next.setIcon(new ImageIcon(ImageIO.read(Thread.currentThread()
					.getContextClassLoader().getResource("org/ooc/gui/skip_forward-40x40.png"))));
		} catch (IOException ex) {
			next.setText(">> next pass >>");
		}
		next.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent ev) {
				try {
					manager.doPass();
				} catch(CompilationFailedError e) { /* Well, we've gotta let the people debug, ain't it ? */ }
				
				SyntaxTreeWidget nextTab = new SyntaxTreeWidget(errorsPanel, manager, passNumber+1);
				JTabbedPane tabbedPane = (JTabbedPane) SyntaxTreeWidget.this.getParent();
				tabbedPane.addTab(nextTab.getTitle(), nextTab);
				tabbedPane.setSelectedComponent(nextTab);

				try {
					SyntaxTreeNode selected = (SyntaxTreeNode) tree.getSelectionPath().getLastPathComponent();
					SyntaxTreeNode node = nextTab.root.findNode(selected.getNode().hash);
					nextTab.tree.setSelectionPath(new TreePath(node.getPath()));
				} catch(Exception e) { /* Well, then I guess you'll just have to fsck off. */ }
			}
		});
		toolbar.add(next);
		
		final JTextField go = new JTextField();
		go.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					int hash = Integer.parseInt(go.getText());
					SyntaxTreeNode node = root.findNode(hash);
					if(node == null) {
						JOptionPane.showMessageDialog(SyntaxTreeWidget.this,
								"SyntaxNode with hash #"+hash+" not found!",
								"Easter Egg", JOptionPane.WARNING_MESSAGE);
					} else {
						tree.setSelectionPath(new TreePath(node.getPath()));
					}
				}
			}
		});
		go.setFont(Font.decode("Sans-plain-24"));
		toolbar.add(go);
		
		tree.addMouseListener(new MouseAdapter() {
			
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 3) {
					TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
					SyntaxTreeNode node = (SyntaxTreeNode) path.getLastPathComponent();
					JOptionPane.showMessageDialog(SyntaxTreeWidget.this, node.getInfo());
				}
			}
			
		});
		
		process(root);
		
		title = manager.getContext().source.getInfo().fullName+" #"+passNumber;
		
	}
	
	protected void process(SyntaxTreeNode treeNode) {
		
		SyntaxNode node = treeNode.getNode();
		if(node instanceof SyntaxNodeList) {
			SyntaxNodeList list = (SyntaxNodeList) node;
			for(SyntaxNode child: list.nodes) {
				SyntaxTreeNode childTreeNode = new SyntaxTreeNode(manager, child);
				treeNode.add(childTreeNode);
				process(childTreeNode);
			}
		}
		
	}

	/**
	 * @return the title of this source context
	 */
	public String getTitle() {
		
		return title;
		
	}

}
