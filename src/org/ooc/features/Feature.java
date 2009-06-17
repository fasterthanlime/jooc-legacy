package org.ooc.features;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.others.SyntaxNode;

/**
 * A feature is an operation applied to the source tree when specific patterns
 * are recognized.
 * @see SingleFeature
 * @see CoupleFeature
 * @see TrioFeature
 * 
 * @author Amos Wenger
 */
public abstract class Feature {

	protected boolean assertNonNullParent = false;
	
	/**
	 * Apply the feature to a node. It may modify other nodes, and it may have
	 * no effect at all.
	 * @param manager
	 * @param node
	 */
	public final void apply(AssemblyManager manager, SyntaxNode node) {
		
		applyImpl(manager, node);
		
	}
	
	protected abstract void applyImpl(AssemblyManager manager, SyntaxNode node);

}
