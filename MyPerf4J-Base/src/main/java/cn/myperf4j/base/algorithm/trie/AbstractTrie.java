package cn.myperf4j.base.algorithm.trie;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * The base class for all {@link Trie} instances.
 */
abstract class AbstractTrie<T, N extends AbstractTrie.TrieNode<T, N>> implements Trie<T> {

    private final TrieNodeFactory<T, N> nodeFactory;

    private final N root;

    AbstractTrie(TrieNodeFactory<T, N> nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.root = createTrieNode();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public int size() {
        return root.getSize();
    }

    @Override
    public T put(String key, T value) {
        return put(root, key, value);
    }

    private T put(N root, String key, T value) {
        int index = 0;
        final Deque<N> stack = new LinkedList<>();
        N next, node = root;
        stack.push(root);
        while (index < key.length()) {
            final char c = getChar(key, index);
            next = node.getNext(c);
            if (next == null) {
                next = createTrieNode();
                node.setNext(c, next);
            }
            node = next;
            stack.push(node);
            index++;
        }

        final boolean replaced = node.hasValue();
        final T old = node.getValue();
        node.setValue(value);
        if (replaced) {
            return old;
        }

        while (!stack.isEmpty()) {
            node = stack.pop();
            node.setSize(node.getSize() + 1);
        }
        return null;
    }

    private char getChar(String key, int index) {
        return key.charAt(index);
    }

    @Override
    public boolean containsKey(String key) {
        return get(key) != null;
    }

    @Override
    public T get(String key) {
        return get(root, key, 0, key.length());
    }

    private T get(N node, final String key, int beginIdx, final int endIdx) {
        for (int i = beginIdx; i < endIdx; i++) {
            node = node.getNext(getChar(key, i));
            if (node == null) {
                return null;
            }
        }
        return node.getValue();
    }

    @Override
    public T get(String key, int beginIdx, int endIdx) {
        if (beginIdx < 0 || beginIdx > endIdx || endIdx > key.length()) {
            throw new IllegalArgumentException("AbstractTrie.get(" + key + ", " + beginIdx + ", " + endIdx + "): " +
                    "beginIdx < 0 || beginIdx > endIdx || endIdx > key.length()");
        }
        return get(root, key, beginIdx, endIdx);
    }

    @Override
    public T prefix(String key) {
        return prefix(root, key);
    }

    private T prefix(N node, String key) {
        T value = null;
        int index = 0;
        while (node != null) {
            if (node.hasValue()) {
                value = node.getValue();
            }
            if (index == key.length()) {
                break;
            }
            node = node.getNext(getChar(key, index));
            index++;
        }
        return value;
    }

    @Override
    public List<T> prefixList(String key) {
        final List<T> result = new ArrayList<>(4);
        prefixList(root, key, result);
        return result;
    }

    private void prefixList(N node, String key, List<T> values) {
        int index = 0;
        while (node != null) {
            if (node.hasValue()) {
                values.add(node.getValue());
            }

            if (index == key.length()) {
                break;
            }
            node = node.getNext(getChar(key, index++));
        }
    }

    @Override
    public T remove(String key) {
        return remove(root, key);
    }

    private T remove(N root, String key) {
        int index = 0;
        N next, node = root;
        final Deque<N> stack = new LinkedList<>();
        while (index < key.length()) {
            stack.push(node);
            next = node.getNext(getChar(key, index));
            if (next == null) {
                return null;
            }
            node = next;
            index++;
        }

        if (!node.hasValue()) {
            return null;
        }

        final T value = node.getValue();
        node.setSize(node.getSize() - 1);
        node.removeValue();
        index = key.length() - 1;
        while (!stack.isEmpty()) {
            final char c = getChar(key, index);
            node = stack.pop();
            if (node.getNext(c).isEmpty()) {
                node.removeNext(c);
            }
            node.setSize(node.getSize() - 1);
            index--;
        }
        return value;
    }

    private N createTrieNode() {
        return nodeFactory.createNode();
    }

    interface TrieNodeFactory<T, N extends TrieNode<T, N>> {

        N createNode();
    }

    interface TrieNode<T, N extends TrieNode<T, N>> {

        boolean isEmpty();

        void setSize(int size);

        int getSize();

        void setNext(char c, N next);

        N getNext(char c);

        void removeNext(char c);

        void setValue(T value);

        T getValue();

        boolean hasValue();

        void removeValue();
    }
}
