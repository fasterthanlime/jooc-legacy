package org.ooc.features;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.others.SyntaxNode;

/**
 * A feature that is tried on every consecutive trio of nodes of specified types
 * 
 * @author Amos Wenger
 *
 * @param <K> the type of the first node which this feature might do stuff to.
 * @param <V> the type of the second node which this feature might do stuff to.
 * @param <M> the type of the third node which this feature might do stuff to.
 */
public abstract class TrioFeature<K,V,M> extends Feature {
	
	final Class<K> firstType;
	final Class<V> secondType;
	final Class<M> thirdType;
	
	/**
	 * first, second, and third types argument are needed because Java practices erasure on generic types.
	 * @param firstType
	 * @param secondType
	 */
	public TrioFeature(Class<K> firstType, Class<V> secondType, Class<M> thirdType) {
		this.firstType = firstType;
		this.secondType = secondType;
		this.thirdType = thirdType;
	}
	
	@Override
	public void applyImpl(AssemblyManager manager, SyntaxNode node) {
		
		if(applyForward(manager, node)) {
			return;
		}
		
		//if(applyBackward(manager, node)) {
		//	return;
		//}
		
		// snif snif :( no one loved us
		
	}

	@SuppressWarnings("unchecked")
	private boolean applyForward(AssemblyManager manager, SyntaxNode first) {
		
		if(!(firstType.isInstance(first))) {
			return false;
		}
		
		SyntaxNode second = first.getNext();
		if(!(secondType.isInstance(second))) {
			return false;
		}
		
		SyntaxNode third = second.getNext();
		if(!(thirdType.isInstance(third))) {
			return false;
		}
		
		applyImpl(manager, (K) first, (V) second, (M) third);
		return true;
		
	}
	
	/*
	@SuppressWarnings("unchecked")
	private boolean applyBackward(AssemblyManager manager, SyntaxNode third) {
		
		if(!(thirdType.isInstance(third))) {
			return false;
		}
		
		SyntaxNode second = third.getPrev();
		if(!(secondType.isInstance(second))) {
			return false;
		}
		
		SyntaxNode first = second.getPrev();
		if(!(firstType.isInstance(first))) {
			return false;
		}
		
		applyImpl(manager, (K) first, (V) second, (M) third);
		return true;
		
	}
	*/
	
	protected abstract void applyImpl(AssemblyManager manager, K first, V second, M third);
	
}
