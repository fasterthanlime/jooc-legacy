package org.ooc.features.checks;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.SingleFeature;
import org.ooc.nodes.types.Cast;

/**
 * Check that a cast is valid (ie. the destination type is a super-class/subclass
 * of the source type 
 * 
 * @author Amos Wenger
 */
public class CastTypeCheck extends SingleFeature<Cast> {

	/**
	 * Default constructor
	 */
	public CastTypeCheck() {
		super(Cast.class);
	}

	
	@Override
	protected void applyImpl(AssemblyManager manager, Cast cast) {

		cast.getSourceType().checkCast(cast.getDestinationType(), manager);

	}

}
