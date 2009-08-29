package org.ooc.features;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.others.SyntaxNode;

/**
 * A feature that is tried on every node of a specified type. 
 * 
 * @author Amos Wenger
 *
 * @param <K> the type of the node which this feature might do stuff to.
 */
public abstract class SingleFeature<K> extends Feature {
	
	final Class<K> type;
	
	/**
	 * type argument is needed because Java practices erasure on generic types.
	 * @param type
	 */
	public SingleFeature(Class<K> type) {
		this.type = type;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public void applyImpl(AssemblyManager manager, SyntaxNode node) {
		
		if(!(type.isInstance(node))) {
			return;
		}
		
		applyImpl(manager, (K) node);
		
	}
	
	protected abstract void applyImpl(AssemblyManager manager, K node);
	
}
