package org.ooc.gui;

import java.util.Vector;

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
	private static final long serialVersionUID = 1853169754176247105L;

	private final SyntaxNode node;
	private final String description;
	private final String info;

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
	@SuppressWarnings("unchecked")
	public SyntaxTreeNode findNode(int hash) {

		if(node.hash == hash) {
			return this;
		} else if(children != null && !children.isEmpty()) {
			for(SyntaxTreeNode child: (Vector<SyntaxTreeNode>) children) {
				SyntaxTreeNode candidate = child.findNode(hash);
				if(candidate != null) {
					return candidate;
				}
			}
		}
		
		return null;
		
	}

}
