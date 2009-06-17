package org.ooc.gui;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.ooc.compiler.AssemblyErrorListener;
import org.ooc.errors.AssemblyError;

/**
 * Display errors in the source code, and allows to jump to them
 * 
 * @author Amos Wenger
 */
public class ErrorsPanel extends JPanel implements AssemblyErrorListener {

	private static class AssemblyErrorNode {

		final AssemblyError error;
		
		AssemblyErrorNode(AssemblyError error, DefaultTableModel model) {
			super();
			this.error = error;
			model.addRow(new Object[] {
					new DescriptionPart(this),
					new ResourcePart(this),
					new LinePart(this),
			});
		}
		
		@Override
		public String toString() {
			return error.getSimpleMessage()+" "+error.node.location;
		}
		
		private class NodePart {
			
			AssemblyErrorNode node;
			NodePart(AssemblyErrorNode node) {
				this.node = node;
			}
			
		}
		
		private class DescriptionPart extends NodePart {
			
			DescriptionPart(AssemblyErrorNode node) {
				super(node);
			}

			@Override
			public String toString() {
				return node.error.getSimpleMessage();
			}
			
		}
		
		private class ResourcePart extends NodePart {
			
			ResourcePart(AssemblyErrorNode node) {
				super(node);
			}

			@Override
			public String toString() {
				return node.error.node.location.getFileName();
			}
			
		}
		
		private class LinePart extends NodePart {
			
			LinePart(AssemblyErrorNode node) {
				super(node);
			}

			@Override
			public String toString() {
				return "line "+node.error.node.location.getLineNumber()+", col "
				+node.error.node.location.getLinePos();
			}
			
		}
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2538471777650663317L;
	
	JTable table;
	private DefaultTableModel errorNodes;

	/**
	 * Default constructor
	 * @param parser
	 */
	public ErrorsPanel() {

		super(new GridLayout());
		errorNodes = new DefaultTableModel(new String[] {"Description", "Resource", "Location"}, 0);
		table = new JTable(errorNodes);
		table.setDefaultEditor(Object.class, null);
		add(new JScrollPane(table));
		
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				table.getComponentAt(e.getX(), e.getY());
				
			}			
			
		});
		
	}

	@Override
	public void onAssemblyError(AssemblyError error) {

		new AssemblyErrorNode(error, errorNodes);
		
	}
	
}
