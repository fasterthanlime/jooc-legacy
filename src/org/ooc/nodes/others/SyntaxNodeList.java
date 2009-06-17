package org.ooc.nodes.others;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.ooc.errors.AssemblyManager;
import org.ubi.FileLocation;

/**
 * A SyntaxNodeList holds a an ordered list of SyntaxNode(s).
 * 
 * Although it is often subclassed for better uses, it is not abstract because
 * sometimes pretty convenient (to avoid reverse order instruction adding, for example). 
 * 
 * @author Amos Wenger
 */
public class SyntaxNodeList extends SyntaxNode {

	/** All the child nodes */
    public final List<SyntaxNode> nodes;

    /**
     * Default constructor
     * @param location
     */
    public SyntaxNodeList(FileLocation location) {
        super(location);
        //this.nodes = new ArrayList<SyntaxNode>();
        this.nodes = new LinkedList<SyntaxNode>();
    }

    /**
     * Add all nodes from "group" at the end of the node list.
     */
    public void addAll(SyntaxNodeList group) {
    	damage();
    	addAll(group.nodes);
    }
    
    /**
     * Add all nodes from the specified list at the end of this node list. 
     * @param nodes the nodes to add
     */
    public void addAll(List<SyntaxNode> nodes) {
    	damage();
        for(SyntaxNode node: nodes) {
            add(node);
        }
    }

    /**
     * Adds "node" at the end of the node list
     */
    public void add(SyntaxNode node) {
    	damage();
        nodes.add(node);
        node.setParent(this);
    }

    /**
     * Adds "node" to the node list, at the position just before "beforeWhat".
     */
    public void addBefore(SyntaxNode beforeWhat, SyntaxNode node) {
    	damage();
    	addBefore(beforeWhat, node, 0);
    }
    
    /**
     * Adds "node" to the node list, at the position just before "beforeWhat",
     * minus offset "offset"
     */
    public void addBefore(SyntaxNode beforeWhat, SyntaxNode node, int offset) {
    	damage();
        nodes.add(nodes.indexOf(beforeWhat) - offset, node);
        node.setParent(this);
    }
    
    /**
     * Adds "node" to the node list, at the position just after "afterWhat".
     */
    public void addAfter(SyntaxNode afterWhat, SyntaxNode node) {
    	damage();
    	addAfter(afterWhat, node, 0);
    }
    
    /**
     * Adds "node" to the node list, at the position just after "afterWhat",
     * adding an offset. (e.g. offset=0 for just after, offset=1 for one further, etc.)
     */
	public void addAfter(SyntaxNode afterWhat, SyntaxNode node, int offset) {
		damage();
        nodes.add(nodes.indexOf(afterWhat) + 1 + offset, node);
        node.setParent(this);
    }

    /**
     * Adds "node" to the head of the node list (ie. in first position,
     * in other words, at index 0)
     */
    public void addToHead(SyntaxNode node) {
    	damage();
        nodes.add(0, node);
        node.setParent(this);
    }

    /**
     * Removes "node" from the node list and set its parent to null.
     */
    public void remove(SyntaxNode node) {
    	damage();
        nodes.remove(node);
        node.setParent(null);
    }
    
    /**
     * Replace "oldNode" with "newNode". Beware of foreaches, though,
     * you may run into a ConcurrentModificationException.
     * @param manager 
     */
    public void replace(AssemblyManager manager, SyntaxNode oldNode, SyntaxNode newNode) {
    	try {
	    	damage();
	        int index = nodes.indexOf(oldNode);
			nodes.set(index, newNode);
	        oldNode.setParent(null);
	        newNode.setParent(this);
	        manager.queue(newNode, "Turned a "+oldNode.getClass().getSimpleName()+" into a "
	        		+newNode.getClass().getSimpleName());
	        manager.clean(oldNode);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private void damage() {
    	setLocked(false);
    }

	/**
     * @return all nodes of type clazz in this SyntaxGroup.
     */
    public<T> List<T> getNodesTyped(Class<T> clazz) {
    	return getNodesTyped(clazz, false);
    }
    
    /**
     * @return all nodes of type clazz in this SyntaxGroup, and all its children
     * if recursive is true.
     */
    @SuppressWarnings("unchecked")
	public<T> List<T> getNodesTyped(Class<T> clazz, boolean recursive) {
    	
        ArrayList<T> list = new ArrayList<T>();
        for(SyntaxNode node: nodes) {
            if(clazz.isInstance(node)) {
                list.add((T) node);
            }
            if(recursive && node instanceof SyntaxNodeList) {
            	list.addAll(((SyntaxNodeList) node).getNodesTyped(clazz, true));
            }
        }
        
        return list;
        
    }

    /**
     * The node just after "node", or null if it's the end of the node list.
     */
    public SyntaxNode getNext(SyntaxNode node) {
        return getNext(node, 0);
    }

    /**
     * @return the node after "node", with an offset of "offset".
     * (e.g. if offset = 0, gets the one just after, and
     * if offset = 1, it gets the one after the one just after ;))
     * or null if we've hit the boundaries of the node list.
     */
    private SyntaxNode getNext(SyntaxNode node, int offset) {
        int index = nodes.indexOf(node) + 1 - offset;
        if(index >= 0 && index < nodes.size()) {
            return nodes.get(index);
        }
		return null;
    }

    /**
     * The node just before "node", or null if it's the end of the node list.
     */
    public SyntaxNode getPrev(SyntaxNode node) {
        return getPrev(node, 0);
    }

    /**
     * @return the node before "node", with an offset of "offset".
     * (e.g. if offset = 0, gets the one just before, and
     * if offset = 1, it gets the one before the one just before ;)),
     * or null if we've hit the boundaries of the node list.
     */
    private SyntaxNode getPrev(SyntaxNode node, int offset) {
        int index = nodes.indexOf(node) - 1 - offset;
        if(index >= 0 && index < nodes.size()) {
            return nodes.get(index);
        }
		return null;
    }

    /**
     * Browse all parents recursively, from father to grand-father, to grand-grand-father, etc.
     * and return the first (=nearest) to be of class clazz.
     * @param clazz The type to search for.
     * @return the nearest parent of type clazz.
     */
    @SuppressWarnings("unchecked")
	public <T extends SyntaxNode> T getNearest(Class<T> clazz) {
        if(clazz.isInstance(this)) {
            return (T) this;
        } else if(getParent() != null) {
            return getParent().getNearest(clazz);
        } else {
            return null;
        }
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	for(SyntaxNode node: nodes) {
            node.writeToCSource(a);
        }
    }

    @Override
    protected void assembleImpl(AssemblyManager manager) {
    	assembleAll(manager);
    }
    
    /**
     * Assemble all child nodes
     * @return true if clean, false if dirty.
     */
    public boolean assembleAll(AssemblyManager manager) {
        for(int i = 0; i < nodes.size(); i++) {
            nodes.get(i).assemble(manager);
        }
        boolean isDirty = manager.isDirty(this);
        if(isDirty) {
        	manager.queue(this, "Dirty children");
        }
		return !isDirty;
    }

    @Override
    public void writeToCHeader(Appendable a) throws IOException {
        for(SyntaxNode node: nodes) {
            node.writeToCHeader(a);
        }
    }
    
    @Override
	public String getDescription() {
    	StringBuilder desc = new StringBuilder();
    	desc.append(this.getClass().getSimpleName());
    	desc.append(" with ");
    	desc.append(nodes.size());
    	desc.append(" nodes [");
    	ListIterator<SyntaxNode> it = nodes.listIterator();
    	while(it.hasNext()) {
    		desc.append(it.next().getClass().getSimpleName());
    		if(it.hasNext()) {
    			desc.append(", ");
    		}
    	}
    	desc.append("] nodes");
		return desc.toString();
    }

    /**
     * Replace this list with all its nodes, in its parent
     */
	public void flatten() {
		SyntaxNode[] toMove = nodes.toArray(new SyntaxNode[] {});
		for(SyntaxNode node: toMove) {
			getParent().addBefore(this, node);
		}
		getParent().remove(this);
	}
	
	/**
	 * Move all children of this syntax node list to another
	 * @param receiver
	 */
	public void moveChildrenTo(SyntaxNodeList receiver) {
	
		while(!nodes.isEmpty()) {
			nodes.get(0).moveTo(receiver);
		}
		
	}

}
