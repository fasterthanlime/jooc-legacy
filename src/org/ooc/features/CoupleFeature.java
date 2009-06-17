package org.ooc.features;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.others.SyntaxNode;

/**
 * A feature that is tried on every consecutive couple of nodes of specified types
 * 
 * @author Amos Wenger
 *
 * @param <K> the type of the first node which this feature might do stuff to.
 * @param <V> the type of the second node which this feature might do stuff to.
 */
public abstract class CoupleFeature<K,V> extends Feature {
	
	final Class<K> firstType;
	final Class<V> secondType;
	
	/**
	 * first and second types argument are needed because Java practices erasure on generic types.
	 * @param firstType
	 * @param secondType
	 */
	public CoupleFeature(Class<K> firstType, Class<V> secondType) {
		this.firstType = firstType;
		this.secondType = secondType;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void applyImpl(AssemblyManager manager, SyntaxNode first) {
		
		if(!(firstType.isInstance(first))) {
			return;
		}
		
		SyntaxNode second = first.getNext();
		if(!(secondType.isInstance(second))) {
			return;
		}
		
		applyImpl(manager, (K) first, (V) second);
		
	}
	
	protected abstract void applyImpl(AssemblyManager manager, K first, V second);
	
}
