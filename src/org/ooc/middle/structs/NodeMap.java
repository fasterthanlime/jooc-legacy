package org.ooc.middle.structs;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.tokens.Token;

public class NodeMap<K, V extends Node> extends Node implements Map<K, V> {

	private Map<K, V> map;

	public NodeMap(Map<K, V> map) {
		super(Token.defaultToken);
		this.map = map;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2959512676544142235L;

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		for(V child: map.values()) {
			child.accept(visitor);
		}
	}

	public boolean hasChildren() {
		return size() > 0;
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		for(K key : keySet()) {
			V value = map.get(key);
			if(value == oldie) {
				map.put(key, value);
				return true;
			}
		}
		return false;
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	public V get(Object key) {
		return map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public V put(K key, V value) {
		return map.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> t) {
		map.putAll(t);
	}

	public V remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<V> values() {
		return map.values();
	}
	
}
