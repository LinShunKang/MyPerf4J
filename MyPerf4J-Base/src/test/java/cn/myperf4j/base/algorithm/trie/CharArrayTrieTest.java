package cn.myperf4j.base.algorithm.trie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Created by LinShunkang on 2026/07/07
 */
public class CharArrayTrieTest {

    @Test
    public void testPutGetContainsAndSize() {
        final Trie<String> trie = new CharArrayTrie<>(128);

        Assertions.assertTrue(trie.isEmpty());
        Assertions.assertEquals(0, trie.size());
        Assertions.assertNull(trie.put("java/", "java"));
        Assertions.assertNull(trie.put("javax/", "javax"));

        Assertions.assertFalse(trie.isEmpty());
        Assertions.assertEquals(2, trie.size());
        Assertions.assertTrue(trie.containsKey("java/"));
        Assertions.assertTrue(trie.containsKey("javax/"));
        Assertions.assertFalse(trie.containsKey("sun/"));
        Assertions.assertEquals("java", trie.get("java/"));
        Assertions.assertEquals("javax", trie.get("javax/"));
        Assertions.assertNull(trie.get("java/lang/String"));
    }

    @Test
    public void testPutExistingKeyReturnsOldValueAndKeepsSize() {
        final Trie<String> trie = new CharArrayTrie<>(128);

        Assertions.assertNull(trie.put("org/junit/", "junit-v1"));
        Assertions.assertEquals("junit-v1", trie.put("org/junit/", "junit-v2"));

        Assertions.assertEquals(1, trie.size());
        Assertions.assertEquals("junit-v2", trie.get("org/junit/"));
    }

    @Test
    public void testGetWithRange() {
        final Trie<String> trie = new CharArrayTrie<>(128);
        trie.put("org/junit/", "junit");
        trie.put("junit", "simple");

        Assertions.assertEquals("junit", trie.get("xxorg/junit/yy", 2, 12));
        Assertions.assertEquals("simple", trie.get("org/junit/Before", 4, 9));
        Assertions.assertNull(trie.get("org/test/Before", 4, 8));
    }

    @Test
    public void testGetWithIllegalRange() {
        final Trie<String> trie = new CharArrayTrie<>(128);

        Assertions.assertThrows(IllegalArgumentException.class, () -> trie.get("abc", -1, 1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> trie.get("abc", 2, 1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> trie.get("abc", 0, 4));
    }

    @Test
    public void testPrefixReturnsLongestMatchedValue() {
        final Trie<String> trie = new CharArrayTrie<>(128);
        trie.put("com/", "com");
        trie.put("com/example/", "example");
        trie.put("com/example/service/", "service");

        Assertions.assertEquals("service", trie.prefix("com/example/service/UserService"));
        Assertions.assertEquals("example", trie.prefix("com/example/controller/UserController"));
        Assertions.assertEquals("com", trie.prefix("com/other/Foo"));
        Assertions.assertNull(trie.prefix("org/example/Foo"));
    }

    @Test
    public void testPrefixListReturnsShortestToLongestMatches() {
        final Trie<String> trie = new CharArrayTrie<>(128);
        trie.put("com/", "com");
        trie.put("com/example/", "example");
        trie.put("com/example/service/", "service");

        Assertions.assertEquals(
                Arrays.asList("com", "example", "service"),
                trie.prefixList("com/example/service/UserService")
        );
        Assertions.assertEquals(Arrays.asList("com", "example"), trie.prefixList("com/example/User"));
        Assertions.assertTrue(trie.prefixList("org/example/User").isEmpty());
    }

    @Test
    public void testEmptyKeyWorksAsRootValue() {
        final Trie<String> trie = new CharArrayTrie<>(128);

        Assertions.assertNull(trie.put("", "root"));
        trie.put("cn/", "cn");

        Assertions.assertEquals(2, trie.size());
        Assertions.assertTrue(trie.containsKey(""));
        Assertions.assertEquals("root", trie.get(""));
        Assertions.assertEquals("cn", trie.prefix("cn/myperf4j/base/Version"));
        Assertions.assertEquals("root", trie.prefix("org/junit/Test"));
        Assertions.assertEquals(Arrays.asList("root", "cn"), trie.prefixList("cn/myperf4j/base/Version"));
        Assertions.assertEquals(Arrays.asList("root"), trie.prefixList("org/junit/Test"));
    }

    @Test
    public void testRemoveLeafKey() {
        final Trie<String> trie = new CharArrayTrie<>(128);
        trie.put("com/example/api/", "api");
        trie.put("com/example/service/", "service");

        Assertions.assertEquals("service", trie.remove("com/example/service/"));
        Assertions.assertEquals(1, trie.size());
        Assertions.assertNull(trie.get("com/example/service/"));
        Assertions.assertFalse(trie.containsKey("com/example/service/"));
        Assertions.assertEquals("api", trie.get("com/example/api/"));
        Assertions.assertNull(trie.remove("com/example/service/"));
    }

    @Test
    public void testRemovePrefixKeyKeepsLongerKey() {
        final Trie<String> trie = new CharArrayTrie<>(128);
        trie.put("com/example/", "example");
        trie.put("com/example/service/", "service");

        Assertions.assertEquals("example", trie.remove("com/example/"));
        Assertions.assertEquals(1, trie.size());
        Assertions.assertNull(trie.get("com/example/"));
        Assertions.assertEquals("service", trie.get("com/example/service/"));
        Assertions.assertEquals("service", trie.prefix("com/example/service/UserService"));
    }

    @Test
    public void testRemoveRootValueKeepsChildren() {
        final Trie<String> trie = new CharArrayTrie<>(128);
        trie.put("", "root");
        trie.put("cn/", "cn");

        Assertions.assertEquals("root", trie.remove(""));
        Assertions.assertEquals(1, trie.size());
        Assertions.assertNull(trie.get(""));
        Assertions.assertEquals("cn", trie.get("cn/"));
        Assertions.assertEquals(Arrays.asList("cn"), trie.prefixList("cn/myperf4j/base/Version"));
        Assertions.assertNull(trie.remove(""));
    }

    @Test
    public void testRemoveMissingKeyDoesNotChangeSize() {
        final Trie<String> trie = new CharArrayTrie<>(128);
        trie.put("java/", "java");

        Assertions.assertNull(trie.remove("javax/"));
        Assertions.assertEquals(1, trie.size());
        Assertions.assertEquals("java", trie.get("java/"));
    }

    @Test
    public void testCapacityBounds() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CharArrayTrie<String>(-1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CharArrayTrie<String>(0xFFFF + 1));

        final Trie<String> trie = new CharArrayTrie<>(128);
        Assertions.assertThrows(IllegalArgumentException.class, () -> trie.put("\u0080", "out-of-capacity"));
        Assertions.assertNull(trie.get("\u0080"));
    }
}
