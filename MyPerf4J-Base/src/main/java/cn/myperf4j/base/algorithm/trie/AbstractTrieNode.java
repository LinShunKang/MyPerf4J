package cn.myperf4j.base.algorithm.trie;

import cn.myperf4j.base.algorithm.trie.AbstractTrie.TrieNode;

/**
 * The base class that provides the common implementation for every trie node.
 */
public abstract class AbstractTrieNode<T, N extends AbstractTrieNode<T, N>> implements TrieNode<T, N> {

    /**
     * The node value.
     */
    private T value;

    /**
     * The total node size.
     */
    private int size;

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return getSize() == 0;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean hasValue() {
        return getValue() != null;
    }

    @Override
    public void removeValue() {
        value = null;
    }
}
