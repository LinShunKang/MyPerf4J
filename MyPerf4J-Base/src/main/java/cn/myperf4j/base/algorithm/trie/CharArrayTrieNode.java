package cn.myperf4j.base.algorithm.trie;

/**
 * A character array backed Trie node.
 */
class CharArrayTrieNode<T> extends AbstractTrieNode<T, CharArrayTrieNode<T>> {

    /**
     * The number of distinct children - resembles the 2 byte char distinct values.
     */
    private static final int R = 0xFFFF;

    /**
     * The array of child nodes.
     */
    private final CharArrayTrieNode<T>[] next;

    /**
     * Creates new instance of {@link CharArrayTrieNode} class with specific capacity.
     *
     * @param capacity the maximum number of distinct characters stored by this node
     */
    @SuppressWarnings("unchecked")
    CharArrayTrieNode(int capacity) {
        if (capacity < 0 || capacity > R) {
            throw new IllegalArgumentException(String.format("Capacity exceeds bounds must be in range [0, %d]", R));
        }

        this.next = (CharArrayTrieNode<T>[]) new CharArrayTrieNode[capacity];
    }

    @Override
    public void setNext(char c, CharArrayTrieNode<T> next) {
        this.next[getIndex(c)] = next;
    }

    @Override
    public CharArrayTrieNode<T> getNext(char c) {
        return isValid(c) ? next[c] : null;
    }

    @Override
    public void removeNext(char c) {
        this.next[getIndex(c)] = null;
    }

    /**
     * Retrieves the code point of the given character.
     *
     * @param c the character
     * @return the character code point
     * @throws IllegalArgumentException if character exceeds the node capacity
     */
    private int getIndex(char c) {
        if (!isValid(c)) {
            throw new IllegalArgumentException(String.format("The character %c exceeds bounds.", c));
        }
        return c;
    }

    /**
     * Returns whether the character is in bounds for this node.
     *
     * @param c the character
     * @return true if character is in bounds
     */
    private boolean isValid(char c) {
        return c < this.next.length;
    }
}
