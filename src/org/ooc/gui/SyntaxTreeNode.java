package org.ooc.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.types.Type;

/**
 * A node in the syntax tree node representation
 * 
 * @author Amos Wenger
 */
public class SyntaxTreeNode extends DefaultMutableTreeNode {

	/**
	 * 
	 */
	protected static final long serialVersionUID = 1853169754176247105L;

	protected final SyntaxNode node;
	protected final String description;
	protected final String info;

	/**
	 * Default constructor
	 * @param manager
	 * @param node
	 */
	public SyntaxTreeNode(AssemblyManager manager, SyntaxNode node) {
		
		this.node = node;
		Type.resolveCheckEnabled = false;
		this.description = node.getClass().getSimpleName() + " " + node.hash + " | " + node.getDescription();
		
		this.info = "Description: "+description
					+ "\nVirgin: " + (node.isVirgin() ? "yes" : "no")
					+ "\nDirty: " + (manager.isDirty(node) ? "yes" : "no")
					+ (node instanceof Typed ? "\nType: "+((Typed) node).getType() : "")
					+ (node.getParent() == null ? "\nNo parent." : "\nHierarchy is "+node.getHierarchyRepr())
					//+ "\nStack trace: " + node.stackTrace
					;
		Type.resolveCheckEnabled = true;
		
	}

	@Override
	public String toString() {
		return description;
	}
	
	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}
	
	/**
	 * @return the node
	 */
	public SyntaxNode getNode() {
		return node;
	}

	/**
	 * Find a node by its hash
	 * @param hash
	 * @return
	 */
	public SyntaxTreeNode findNode(int hash) {

		if(node.hash == hash) {
			return this;
		} else if(children != null && !children.isEmpty()) {
			for(Object data: children) {
				SyntaxTreeNode child = (SyntaxTreeNode) data;
				SyntaxTreeNode candidate = child.findNode(hash);
				if(candidate != null) {
					return candidate;
				}
			}
		}
		
		return null;
		
	}

}
