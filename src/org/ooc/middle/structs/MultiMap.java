package org.ooc.middle.structs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.tokens.Token;

/**
 * A MultiMap allows you to store several values for the same
 * key, in a relatively lightweight fashion (memory-wise) 
 * @author Amos Wenger
 *
 * @param <K> the keys type
 * @param <V> the values type
 */
public class MultiMap<K, V> extends Node {

	final Map<K, Object> map;
	
	public MultiMap() {
		super(Token.defaultToken);
		map = new LinkedHashMap<K, Object>();
	}
	
	@SuppressWarnings("unchecked")
	public void add(K key, V value) {
		
		Object o = map.get(key);
		if(o == null) {
			map.put(key, value);
		} else {
			if(o instanceof List<?>) {
				List<V> list = (List<V>) o;
				list.add(value);
			} else {
				List<V> list = new ArrayList<V>();
				list.add((V) o);
				list.add(value);
				map.put(key, list);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<V> getAll(final K key) {
		
		final Object o = map.get(key);
		if(o instanceof List<?>) {
			List<V> list = (List<V>) o;
			return list;
		} else if(o != null) {
			return new Iterable<V>() {
			@Override
			public Iterator<V> iterator() {
				return new Iterator<V>() {
					
					boolean hasNext = true;
					
					@Override
					public boolean hasNext() {
						return hasNext;
					}

					@Override
					public V next() {
						hasNext = false;
						return (V) o;
					}

					@Override
					public void remove() {
						map.remove(key);
					}
				};
			}
			};
		} else {
			return Collections.emptySet(); // it's iterable, and empty. what else? (Nespresso)
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public V get(final K key) {
		
		final Object o = map.get(key);
		if(o instanceof List<?>) {
			List<V> list = (List<V>) o;
			return list.get(0);
		}
		return (V) o;
		
	}
	
	@Override
	public String toString() {
		return map.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		for(Object key: map.keySet()) {
			final Object o = map.get(key);
			if(o instanceof List<?>) {
				List<V> list = (List<V>) o;
				for(int index = 0; index < list.size(); index++) {
					V value = list.get(index);
					if(oldie == value) {
						list.set(index, (V) kiddo);
						return true;
					}
				}
			} else if(o != null && o == oldie) {
				map.put((K) oldie, kiddo);
				return true;
			}
		}
		return false;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		for(Object key: map.keySet()) {
			final Object o = map.get(key);
			if(o instanceof List<?>) {
				List<V> list = (List<V>) o;
				for(V value: list) {
					((Node) value).accept(visitor);
				}
			} else if(o != null) {
				((Node) o).accept(visitor);
			}
		}
	}

	@Override
	public boolean hasChildren() {
		return map.size() > 0;
	}

	public Set<K> keySet() {
		return map.keySet();
	}
	
}
