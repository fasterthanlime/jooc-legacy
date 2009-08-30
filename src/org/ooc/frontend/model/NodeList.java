package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class NodeList<T extends Node> extends Node implements Iterable<T> {
	
	T[] nodes;
	int size;
	
	public NodeList() {
		this(Token.defaultToken);
	}
	
	@SuppressWarnings("unchecked")
	public NodeList(Token startToken) {
		super(startToken);
		nodes = (T[]) new Node[20];
		size = 0;
	}

	private void realloc() {
		nodes = Arrays.copyOf(nodes, (size * 3) / 2 + 1);
	}
	
	private void ensureCapacity(int minCapacity) {
		if(minCapacity > nodes.length) {
			nodes = Arrays.copyOf(nodes, minCapacity);
		}
	}
	
	public void add(T element) {
		if(size >= nodes.length) realloc();
		nodes[size++] = element;
	}

	public void add(int index, T element) {
		if(size + 1 >= nodes.length) realloc();
		System.arraycopy(nodes, index, nodes, index + 1, size - index);
		nodes[index] = element;
		size++;
	}
	
	public boolean remove(T element) {
		for (int index = 0; index < size; index++) {
			if(element.equals(nodes[index])) {
				fastRemove(index);
				return true;
			}
		}
		return false;
	}
	
	public T remove(int index) {
		T o = nodes[index];
        fastRemove(index);
        return o;
	}

	private void fastRemove(int index) {
		int numMoved = size - index - 1;
		if (numMoved > 0) {
            System.arraycopy(nodes, index+1, nodes, index, numMoved);
        }
		size--;
	}
	
	public boolean contains(T element) {
		for (int index = 0; index < size; index++) {
			if(element.equals(nodes[index])) {
				return true;
			}
		}
		return false;
	}
	
	public int indexOf(T lostSheep) {
		for (int index = 0; index < size; index++) {
			if(lostSheep.equals(nodes[index])) {
				return index;
			}
		}
		return -1;
	}

	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public T get(int i) {
		if(i >= size) throw new ArrayIndexOutOfBoundsException(i);
		return nodes[i];
	}
	
	public void set(int i, T element) {
		if(i > size) throw new ArrayIndexOutOfBoundsException(i);
		nodes[i] = element;
	}
	
	public void setAll(NodeList<T> list) {
		nodes = list.nodes;
	}
	
	public T getFirst() {
		if(size == 0) throw new ArrayIndexOutOfBoundsException(0);
		return nodes[0];
	}
	
	public T getLast() {
		if(size == 0) throw new ArrayIndexOutOfBoundsException(0);
		return nodes[size - 1];
	}
	
	public T getBeforeLast() {
		if(size <= 1) throw new ArrayIndexOutOfBoundsException(size - 1);
		return nodes[size - 2];
	}

	@Override
	public Iterator<T> iterator() {		
		return new Iterator<T>() {

			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < size;
			}

			@Override
			public T next() {
				if(index >= size) throw new ArrayIndexOutOfBoundsException(index);
				return nodes[index++];
			}

			@Override
			public void remove() {
				NodeList.this.remove(index);
			}
			
		};
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		for(int i = 0; i < size; i++) {
			nodes[i].accept(visitor);
		}
	}

	@Override
	public boolean hasChildren() {
		return size > 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean replace(Node oldie, Node kiddo) {
		int index = indexOf((T) oldie);
		if(index == -1) {
			System.out.println("trying to replace "+oldie+" with "+kiddo+" in a list with "+nodes);
			throw new ArrayIndexOutOfBoundsException("Trying to replace a "
					+oldie.getClass().getName()+" with a "+kiddo.getClass().getSimpleName()+
					" in a "+this.getClass().getSimpleName()+", but couldn't find node to replace in NodeList.");
		}
		nodes[index] = (T) kiddo;
		return true;
	}

	@SuppressWarnings("unchecked")
	public void addBefore(Line ref, T kiddo) {
		int index = indexOf((T) ref);
		if(index == -1) {
			throw new ArrayIndexOutOfBoundsException("Trying to add a "
					+kiddo.getClass().getName()+" before a "+ref.getClass().getSimpleName()+
					" in a "+this.getClass().getSimpleName()+", but couldn't find reference node.");
		}
		add(index, (T) ref);
	}

	public void addAll(NodeList<T> list) {
		int newSize = size + list.size;
		ensureCapacity(newSize);
		System.arraycopy(list.nodes, 0, nodes, size, list.size);
		size = newSize;
	}
	
	public void addAll(List<T> list) {
		int newSize = size + list.size();
		ensureCapacity(newSize);
		int index = size;
		for(T o : list) {
			nodes[index++] = o;
		}
	}

	public T[] getNodes() {
		return nodes;
	}
	
	@Override
	public String toString() {
		if(size == 0) return "[]";
		StringBuilder sB = new StringBuilder("[");
		int index = 0;
		while(index < size) {
			sB.append(nodes[index++].toString());
			if(index < size) sB.append(", ");
		}
		sB.append(']');
		return sB.toString();
	}

	public void push(T node) {
		if(size + 1 > nodes.length) realloc();
		nodes[size++] = node;
	}

	public void pop() {
		if(size <= 0) throw new ArrayIndexOutOfBoundsException(0);
		size--;
	}

	public T peek() {
		return nodes[size - 1];
	}
	
	public int find(Class<?> clazz) {
		return find(clazz, size - 1);
	}
		
	public int find(Class<?> clazz, int offset) {
		int i = offset;
		while(i >= 0) {
			T node = nodes[i];
			if(clazz.isInstance(node)) {
				return i;
			}
			i--;
		}
		
		return -1;
	}

	public Module getModule() {
		return (Module) nodes[0];
	}
}
