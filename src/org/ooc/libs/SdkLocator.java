package org.ooc.libs;

import java.io.File;
import java.util.Map;

import org.ooc.utils.ReadEnv;

public class SdkLocator {

	public static File locate() {
		
		Map<String, String> env = ReadEnv.getEnv();
		Object envDist = env.get("OOC_SDK");
		if(envDist != null) {
			return new File(envDist.toString());
		}
		
		return new File(DistLocator.locate(), "sdk");
		
	}

}
