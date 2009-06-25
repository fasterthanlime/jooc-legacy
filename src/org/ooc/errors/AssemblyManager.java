package org.ooc.errors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ooc.compiler.AssemblyErrorListener;
import org.ooc.nodes.RootNode;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.types.Type;
import org.ubi.SyntaxError;

/**
 * Collect errors during assembly, maintain a queue useful to know which
 * nodes should be assembled during the next pass, etc.
 * 
 * @author Amos Wenger
 */
public class AssemblyManager {

    private final Map<SyntaxNode, AssemblyError> errors = new HashMap<SyntaxNode, AssemblyError>();
    private final Map<SyntaxNode, String> warnings = new HashMap<SyntaxNode, String>();
    
    private final List<SyntaxNode> queue = new ArrayList<SyntaxNode>();
    private final List<SyntaxNode> queueCache = new ArrayList<SyntaxNode>();
    private final Set<SyntaxNode> set = new HashSet<SyntaxNode>();
    
    private final Map<SyntaxNode, String> reasons = new HashMap<SyntaxNode, String>();
	
	/* FIXME currently, the compiler is implemented in such a way that it will
	   reach MAX_PASSES if the code is something like (((((((((((((((((((( etc.
	   This is the result of a wrong design. It's especially visible in big projects
	   and especially annying. It is unclear how easy it would be to fix the Java
	   implementation of the ooc compiler (ie. this). If anyone is motivated enough
	   feel free to tackle this */
    
    private int passCount = 0;
	private final static int MAX_PASSES = 256;
	//private final static int MAX_PASSES = 24;
	
    private SourceContext context;

	private List<AssemblyErrorListener> assemblyErrorListeners;

    /**
     * Creates an assembly manager for a specific source context
     * @param context
     */
	public AssemblyManager(SourceContext context) {
		
    	this.context = context;
    	assemblyErrorListeners = new ArrayList<AssemblyErrorListener>();
    	
    }
	
	/**
	 * Assemble all nodes of a source and all its dependencies, recursively:
	 * do passes again and again, until no node is left dirty or until some node
	 * asked to fail (=fatal compilation error), @see errAndFail
	 * 
	 * @return
	 */
	public void assemble() {
		
		SourceContext initialContext = this.context;
		
		List<SourceContext> sources = context.getDependenciesRecursive();
		sources.add(0, context);
		
    	for(SourceContext dependency: sources) {
    		queueRecursive(dependency.source.getRoot(), "Source's initial request to queue all nodes");
    	}
    	
    	while(isDirty()) {
    		doPass();
			if(hasErrors()) {
				printAndClearErrors();
			}
			for(SourceContext dependency: sources) {
	    		queueVirgins(dependency.source.getRoot(), "Source's initial request to queue all of the dependencies' nodes.");
	    	}
    	}
    	
    	this.context = initialContext;
		
	}
	
	/**
	 * Queue list and all nodes in it, recursively.
	 * @param list
	 * @param reason why do you queue those nodes ?
	 */
    public void queueRecursive(SyntaxNodeList list, String reason) {
    	
    	queue(list, reason);
    	for(SyntaxNode node: list.nodes) {
    		if(node instanceof SyntaxNodeList) {
    			SyntaxNodeList childList = (SyntaxNodeList) node;
    			queueRecursive(childList, reason);
    		} else {
    			queue(node, reason);
    		}
    	}
    	
	}
    
    /**
     * Queue only virgin nodes in list, for a reason.
     * @param list
     * @param reason why do you queue those nodes ?
     */
    public void queueVirgins(SyntaxNodeList list, String reason) {
    	
    	for(SyntaxNode node: list.nodes) {
    		if(node.isVirgin()) {
	    		queue(node, reason);
    		}
    		if(node instanceof SyntaxNodeList) {
    			SyntaxNodeList childList = (SyntaxNodeList) node;
    			queueVirgins(childList, reason);
    		}
    	}
    	
	}
    
    /**
     * Queue a node, for a reason
     * @param node
     * @param reason why do you queue those nodes ?
     * @return
     */
    public SyntaxNode queue(SyntaxNode node, String reason) {
    	
    	if(node.isLocked()) {
    		return node;
    	}
    	
    	// set.contains is faster than queue.remove, and we don't care about the order in set
		if(set.contains(node)) {
    		queue.remove(node);
    	} else {
    		set.add(node);
    	}
    	queue.add(node);
    	reasons.put(node, reason);
    	
    	return node;
    	
    }
    
    /**
     * Completely remove a node from the queue
     * @param node
     * @return the node in question.
     */
    public boolean clean(SyntaxNode node) {

    	if(set.remove(node)) {
    		queue.remove(node);
    		return true;
    	}
    	return false;
    	
    }

    /**
     * Yield an AssemblyError. Note that it doesn't stop the comilation process.
     * To do that, fail() should be called.
     * @param message a description of the error
     * @param node the node which caused the error (or the nearest one)
     * @return the full error message, including stack trace
     */
    public String err(String message, SyntaxNode node) {
    	
    	return err(new AssemblyError(message, node)).getMessage();
    	
    }
    
    /**
     * Yield an AssemblyError. Note that it doesn't stop the comilation process.
     * To do that, fail() should be called.
     * @param error the error to yield
     * @return the error yielded
     */
    public SyntaxError err(AssemblyError error) {
    	
    	errors.put(error.node, error);
    	for(AssemblyErrorListener l: assemblyErrorListeners) {
    		l.onAssemblyError(error);
    	}
    	return error;
        
    }
    
    /**
	 * Yield an error on a node and stop the compilation process there.
	 * @param message
	 * @param node
	 */
	public void errAndFail(String message, SyntaxNode node) {

		err(message, node);
		fail(node);
		
	}
    
    /**
     * @return true if there's any errors in the error queue.
     */
    public boolean hasErrors() {
    	
    	return !errors.isEmpty();
    	
    }
    
    /**
     * @return true if the queue isn't empty
     */
    public boolean isDirty() {
    	
    	return !set.isEmpty() || !errors.isEmpty();
    	
    }
    
    /**
     * @param node
     * @return true if node is currently in the queue
     */
    public boolean isInQueue(SyntaxNode node) {
    	
    	return set.contains(node);
    	
    }
    
    /**
     * @param node
     * @return true if node is non-locked and, virgin in queue, or has errors.
     */
    public boolean isDirty(SyntaxNode node) {
    	
    	//return !node.isLocked() && (node.isVirgin() || isInQueue(node) || errors.containsKey(node));
    	return !node.isLocked() && (node.isVirgin() || isInQueue(node));
    	
    }
    
    /**
     * @param node
     * @return true if node, or any children, recursively, is non-locked and,
     * virgin in queue, or has errors.
     */
    public boolean isDirtyRecursive(SyntaxNode node) {
		
		if(isDirty(node)) {
			return true;
		}
		
		if(node instanceof SyntaxNodeList) {
			SyntaxNodeList list = (SyntaxNodeList) node;
			for(SyntaxNode child: list.nodes) {
				if(isDirtyRecursive(child)) {
					return true;
				}
			}
		}
		
		return false;
		
	}
    
    /**
     * Print all errors, then remove them from the error queue.
     */
    public void printAndClearErrors() {
    	
    	print(false);
    	errors.clear();
    	
    }
    
    /**
     * Print all errors
     * @param printQueueToo if true, also print a description of all elements
     * in the queue, and the reason they're in.
     */
    public void print(boolean printQueueToo) {
    	
    	Type.resolveCheckEnabled = false;
    	
        for(AssemblyError error: errors.values()) {
        	if(context.projInfo.props.verbose) {
        		error.printStackTrace();
        	} else {
        		System.err.println(error.node.location.toString().trim()+": " + error.getSimpleMessage());
        	}
        }
        if(printQueueToo) {
        	for(SyntaxNode node: queue) {
	        	
        		String reason = reasons.get(node);
        		if(reason == null) {
        			reason = "<unknown reason>";
        		}
        		
	        	System.err.println(node.location + ": " + reason);
	        	
	        }
        }
        
        Type.resolveCheckEnabled = true;
        
    }

    /**
     * Stop the compilation process on a particular node, by throwing a {@link CompilationFailedError}
     * @param node
     * @throws CompilationFailedError it's caught by the {@link Compiler} anyway.
     */
    private void fail(SyntaxNode node) throws CompilationFailedError {
    	
        print(false);
        throw new MaxedOutPassLimit(passCount);
        
    }

    /**
     * Do an assemble step of all nodes in the queue
     */
	public void doPass() {
		
		if(passCount >= MAX_PASSES) {
			print(true);
			throw new MaxedOutPassLimit(MAX_PASSES);
		}
		queueCache.clear();
		queueCache.addAll(queue);
		for(SyntaxNode node: queueCache) {
			try {
				RootNode root = node.getRoot();
				if(root != null) {
					this.context = root.context;
				}
				node.assemble(this);
			} catch(CompilationFailedError e) {
				throw e;
			} catch(Throwable t) {
				System.err.println("AssemblyManager caught a '"+t.getClass().getSimpleName()+"', printing and continuing.");
				t.printStackTrace();
				System.err.println("Was in queue because "+reasons.get(node));
			}
		}
		passCount++;
		
	}

	/**
	 * @return the number of nodes in the queue
	 */
	public int getQueueLength() {
		
		return queue.size();
		
	}
	
	/**
	 * @return the number of errors in the error queue
	 */
	public int getErrorCount() {
		
		return errors.size();
		
	}

	/**
	 * @return a map of nodes to their errors, if any
	 */
	public Map<SyntaxNode, AssemblyError> getErrors() {
		
		return errors;
		
	}
	
	/**
	 * Yield a warning message about a node
	 * @param msg a description of the warning
	 * @param node
	 */
	public void warn(String msg, SyntaxNode node) {

		System.err.println(node.location+", WARNING: "+msg);
		warnings.put(node, msg);
		
	}

	/**
	 * @return all warnings issued
	 */
	public Map<SyntaxNode, String> getWarnings() {

		return warnings;
		
	}

	/**
	 * Subscribe to assembly errors
	 * @param listener
	 * @see AssemblyErrorListener
	 */
	public void addAssemblyErrorListener(AssemblyErrorListener listener) {

		if(!assemblyErrorListeners.contains(listener)) {
			assemblyErrorListeners.add(listener);
		}
		
	}

	/**
	 * 
	 * @return
	 */
	public SourceContext getContext() {
		
		return context;
		
	}

}
