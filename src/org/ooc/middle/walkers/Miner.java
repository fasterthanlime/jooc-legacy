package org.ooc.middle.walkers;

import java.io.IOException;

import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;

/**
 * A miner takes a list and searches for a node, from the top (end of the list)
 * to the bottom (start of the list).
 * 
 * @author Amos Wenger
 */
public class Miner {
	
	public static <T> void mine(Class<T> clazz, Opportunist<T> oppo, NodeList<Node> orig) throws IOException {
		
		NodeList<Node> copy = new NodeList<Node>();
		copy.setAll(orig);
		
		int index = orig.size();
		while(index >= 0) {
			index = orig.find(clazz, index - 1);
			if(index == -1) return;
			while(copy.size() > index) copy.pop();
			if(!oppo.take(clazz.cast(orig.get(index)), copy)) break;
		}
		
	}

}
