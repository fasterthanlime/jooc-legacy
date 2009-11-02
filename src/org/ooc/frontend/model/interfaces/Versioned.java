package org.ooc.frontend.model.interfaces;

import org.ooc.frontend.model.VersionBlock;

public interface Versioned {

	public void setVersion(VersionBlock block);
	public VersionBlock getVersion();
	
}
