package cn.myperf4j.base.algorithm.trie;

import java.util.List;

/**
 * A trie tree abstraction. Trie is an R way tree that is designed for efficient string searches.
 */
public interface Trie<T> {

    /**
     * Returns whether the trie does not contain any entries.
     *
     * @return true if trie does not have any entries
     */
    boolean isEmpty();

    /**
     * Returns the total number of entries.
     *
     * @return the total number of entries
     */
    int size();

    /**
     * Associated the value with specific key.
     *
     * @param key   the key that the value will be associated
     * @param value the value to insert
     * @return the previous value associated with the specific key
     * @throws IllegalArgumentException if {@code key} is {@code null} or empty string
     */
    T put(String key, T value);

    /**
     * Returns whether the trie contains the specific key.
     *
     * @param key the key to search
     * @return true if key exists, false otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null} or empty string
     */
    boolean containsKey(String key);

    /**
     * Returns the values associated with the specific key, or {@code null} otherwise.
     *
     * @param key the key to search
     * @return the associated key value or {@code null} if nothing was found
     * @throws IllegalArgumentException if {@code key} is {@code null} or empty string
     */
    T get(String key);

    T get(String key, int beginIdx, int endIdx);

    /**
     * Returns the longest common prefix of specified key.
     *
     * @param key the key to search
     * @return the prefix key value or {@code null} if nothing was found
     * @throws IllegalArgumentException if {@code key} is {@code null} or empty string
     */
    T prefix(String key);

    /**
     * Returns the common prefix of specified key, from shortest match to longest match
     *
     * @param key the key to search
     * @return the prefix key value or {@code null} if nothing was found
     * @throws IllegalArgumentException if {@code key} is {@code null} or empty string
     */
    List<T> prefixList(String key);

    /**
     * Removes the value associated with specific key.
     *
     * @param key the key to remove
     * @return the removed value associated with the specific key
     * @throws IllegalArgumentException if {@code key} is {@code null} or empty string
     */
    T remove(String key);
}
