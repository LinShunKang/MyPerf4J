package cn.myperf4j.base.match;

import cn.myperf4j.base.algorithm.trie.CharArrayTrie;
import cn.myperf4j.base.algorithm.trie.Trie;
import cn.myperf4j.base.util.StrMatchUtils;
import cn.myperf4j.base.util.collections.ListUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.myperf4j.base.util.StrMatchUtils.WILDCARD;
import static cn.myperf4j.base.util.collections.ConvertUtils.groupBy;

/**
 * Created by LinShunkang on 2026/07/07
 */
public class PackageMatcher {

    private final Trie<PackagePredicates> trie;

    private boolean inited;

    public PackageMatcher() {
        this.trie = new CharArrayTrie<>(128);
        this.inited = false;
    }

    public void init(Set<String> packageExprSet) {
        if (inited) {
            throw new IllegalStateException("PackageMatcher is already inited!");
        }

        final Map<String, List<String>> prefixGroups = groupBy(packageExprSet, PackageMatcher::prefix);
        prefixGroups.forEach((prefix, exprList) -> trie.put(prefix, new PackagePredicates(prefix, exprList)));
        inited = true;
    }

    private static String prefix(String packageExpr) {
        final int index = packageExpr.indexOf(WILDCARD);
        return index < 0 ? packageExpr : packageExpr.substring(0, index);
    }

    public boolean isMatch(String packageName) {
        final List<PackagePredicates> predicatesList = trie.prefixList(packageName);
        if (ListUtils.isEmpty(predicatesList)) {
            return false;
        }

        for (PackagePredicates predicates : predicatesList) {
            if (predicates.isMatch(packageName)) {
                return true;
            }
        }
        return false;
    }

    private static class PackagePredicates {

        private final PackagePredicate[] predicates;

        PackagePredicates(String prefix, List<String> packageExps) {
            this.predicates = generatePredicates(prefix.length(), packageExps);
        }

        private PackagePredicate[] generatePredicates(int prefixLength, List<String> packageExps) {
            final PackagePredicate[] result = new PackagePredicate[packageExps.size()];
            for (int i = 0; i < packageExps.size(); i++) {
                result[i] = new PackagePredicate(packageExps.get(i), prefixLength);
            }
            return result;
        }

        public boolean isMatch(String packageName) {
            for (PackagePredicate exp : predicates) {
                if (exp.isMatch(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class PackagePredicate {

        private final String expression;

        private final int prefixLen;

        private final boolean prefixOnly;

        PackagePredicate(String expression, int prefixLen) {
            this.expression = expression;
            this.prefixLen = prefixLen;
            this.prefixOnly = expression.length() == prefixLen || expression.length() - 1 == prefixLen;
        }

        public boolean isMatch(String packageName) {
            return prefixOnly || StrMatchUtils.isMatch(packageName, prefixLen, expression, prefixLen);
        }
    }
}
