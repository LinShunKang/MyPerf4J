package cn.myperf4j.base.algorithm.trie;

/**
 * An array backed Trie tree.
 */
public class CharArrayTrie<T> extends AbstractTrie<T, CharArrayTrieNode<T>> {

    /**
     * Creates new instance of {@link CharArrayTrie} with specific capacity.
     *
     * @param capacity the node capacity
     */
    public CharArrayTrie(final int capacity) {
        super(() -> new CharArrayTrieNode<>(capacity));
    }
}
