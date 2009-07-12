include stdlib;
include memory;

import lang.String;
import ArrayList;

/**
 * Container for key/value entries in the hash table
 */
class HashEntry {

	/**
	 * The key associated with the entry
	 */
	String key;
	
	/**
	 * The value associated with the entry
	 */
	Object value;
	
	/**
	 * Builds a new hash entry
	 * @param String key The key for the hash entry
	 * @param Object value The value for the hash entry
	 * @return HashEntry
	 */
	func new(=key, =value);
	
}

/**
 * Simple hash table implementation
 */
class HashTable {

	/**
	 * Current size of the table
	 */
	UInt size;
	
	/**
	 * Total capacity of the table (number of hash buckets)
	 */
	UInt capacity;
	
	/**
	 * Collection of hash buckets
	 */
	ArrayList* buckets;
	
	/**
	 * Returns a hash table with 100 buckets
	 * @return HashTable
	 */
	func new {
		this(100);
	}
	
	/**
	 * Returns a hash table of a specified bucket capacity.
	 * @param UInt capacity The number of buckets to use
	 * @return HashTable
	 */
	func new(=capacity) {
		size = 0;
		buckets = malloc(capacity * sizeof(ArrayList));
		for (UInt i = 0; i < capacity; i++) {
			buckets[i] = new;
		}
	}

	/*
	 * Port of Austin Appleby's Murmur Hash implementation
	 * http://murmurhash.googlepages.com/
	 * TODO: Use this to hash not just strings, but any type of object
	 * @param Object key The key to hash
	 * @param Int len The size of the key (in bytes)
	 * @param UInt seed The seed value
	 */
	func murmurHash(Object key, Int len, UInt seed) -> UInt {
		const UInt m = 0x5bd1e995;
		const Int r = 24;

		UInt h = seed ^ len;
		const Octet* data = (Octet*)key;
	   	
		while(len >= 4) {
			UInt k = *(UInt*)data;
		   	
			k *= m; 
			k ^= k >> r; 
			k *= m; 
		   	
			h *= m; 
			h ^= k;
		
			data += 4;
			len -= 4;
		}

	   	switch(len) {
			case 3: h ^= data[2] << 16;
			case 2: h ^= data[1] << 8;
			case 1: h ^= data[0];
				    h *= m;
		}

		h ^= h >> 13;
		h *= m;
		h ^= h >> 15;
	   
		return h;
	}
	
	/*
	 * khash's ac_X31_hash_string
	 * http://attractivechaos.awardspace.com/khash.h.html
	 * @access private
	 * @param String s The string to hash
	 * @return UInt
	 */
	func ac_X31_hash(String s) -> UInt {
		UInt h = *s;
		if (h) {
			for (++s; *s; ++s) {
				h = (h << 5) - h + *s;
			}
		}
		return h;
	}
	
	/**
	 * Returns the HashEntry associated with a key.
	 * @access private
	 * @param String key The key associated with the HashEntry
	 * @return HashEntry
	 */
	func getEntry(String key) -> HashEntry {
		HashEntry entry = null;
		UInt hash = ac_X31_hash(key) % capacity;
		Iterator iter = buckets[hash].iterator();
		while (iter.hasNext()) {
			entry = iter.next();
			if (entry.key.equals(key)) {
				return entry;
			}
		}
		return null;
	}
	
	/**
	 * Puts a key/value pair in the hash table. If the pair already exists,
	 * it is overwritten.
	 * @param String key The key to be hashed
	 * @param Object value The value associated with the key
	 * @return Bool
	 */
	func put(String key, Object value) -> Bool {
		UInt hash;
		HashEntry entry = getEntry(key);
		if (entry) {
			entry.value = value;
		}
		else {
			hash = ac_X31_hash(key) % capacity;
			entry = new(key, value);
			buckets[hash].add(entry);
			size++;
			/* TODO: Resize table */
		}
		return true;
	}
	
	/**
	 * Returns the value associated with the key. Returns null if the key
	 * does not exist.
	 * @param String key The key associated with the value
	 * @return Object
	 */
	func get(String key) -> Object {
		Object value = null;
		HashEntry entry = getEntry(key);
		return entry ? entry.value : null;
	}
	
	/**
	 * Returns whether or not the key exists in the hash table.
	 * @param String key The key to check
	 * @return Bool
	 */
	func contains(String key) -> Bool {
		return getEntry(key) ? true : false;
	}
	
	/**
	 * Removes the entry associated with the key.
	 * @param String key The key to remove
	 * @return Bool
	 */
	func remove(String key) -> Bool {
		HashEntry entry = getEntry(key);
		UInt hash = ac_X31_hash(key) % capacity;
		if (entry) {
			return buckets[hash].removeElement(entry);
		}
		else {
			return false;
		}
	}
	
}
