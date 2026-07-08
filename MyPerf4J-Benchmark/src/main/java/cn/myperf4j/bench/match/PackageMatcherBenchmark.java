package cn.myperf4j.bench.match;

import cn.myperf4j.base.match.PackageMatcher;
import cn.myperf4j.base.util.StrMatchUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashSet;
import java.util.Set;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

/**
 * Created by LinShunkang on 2026/07/07
 * <p>
 * Benchmark                                   (packageCount)                     (scenario)  Mode  Cnt     Score     Error  Units
 * PackageMatcherBenchmark.oldLinearMatcher                16                     PREFIX_HIT  avgt   10    64.994 ±   0.756  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher                16                    PREFIX_MISS  avgt   10   238.137 ±   3.690  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher                16                   WILDCARD_HIT  avgt   10   257.414 ±   8.370  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher                16  WILDCARD_PREFIX_HIT_EXPR_MISS  avgt   10   437.838 ±  13.976  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher                16                  WILDCARD_MISS  avgt   10   122.105 ±   1.788  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher                64                     PREFIX_HIT  avgt   10    92.747 ±   0.723  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher                64                    PREFIX_MISS  avgt   10   963.208 ±  12.996  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher                64                   WILDCARD_HIT  avgt   10   960.381 ±  31.387  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher                64  WILDCARD_PREFIX_HIT_EXPR_MISS  avgt   10  1729.387 ±  67.024  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher                64                  WILDCARD_MISS  avgt   10   485.089 ±  13.510  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher               256                     PREFIX_HIT  avgt   10   640.250 ±   6.158  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher               256                    PREFIX_MISS  avgt   10  4659.612 ±  65.304  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher               256                   WILDCARD_HIT  avgt   10  2348.474 ±  51.759  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher               256  WILDCARD_PREFIX_HIT_EXPR_MISS  avgt   10  8549.425 ± 187.956  ns/op
 * PackageMatcherBenchmark.oldLinearMatcher               256                  WILDCARD_MISS  avgt   10  2547.335 ±  63.073  ns/op
 * PackageMatcherBenchmark.triePackageMatcher              16                     PREFIX_HIT  avgt   10    49.916 ±   0.175  ns/op
 * PackageMatcherBenchmark.triePackageMatcher              16                    PREFIX_MISS  avgt   10    28.382 ±   0.135  ns/op
 * PackageMatcherBenchmark.triePackageMatcher              16                   WILDCARD_HIT  avgt   10    90.471 ±   0.586  ns/op
 * PackageMatcherBenchmark.triePackageMatcher              16  WILDCARD_PREFIX_HIT_EXPR_MISS  avgt   10    90.390 ±   0.652  ns/op
 * PackageMatcherBenchmark.triePackageMatcher              16                  WILDCARD_MISS  avgt   10     3.414 ±   0.010  ns/op
 * PackageMatcherBenchmark.triePackageMatcher              64                     PREFIX_HIT  avgt   10    50.586 ±   0.193  ns/op
 * PackageMatcherBenchmark.triePackageMatcher              64                    PREFIX_MISS  avgt   10    28.410 ±   0.199  ns/op
 * PackageMatcherBenchmark.triePackageMatcher              64                   WILDCARD_HIT  avgt   10    91.737 ±   1.090  ns/op
 * PackageMatcherBenchmark.triePackageMatcher              64  WILDCARD_PREFIX_HIT_EXPR_MISS  avgt   10    91.486 ±   0.884  ns/op
 * PackageMatcherBenchmark.triePackageMatcher              64                  WILDCARD_MISS  avgt   10     3.409 ±   0.009  ns/op
 * PackageMatcherBenchmark.triePackageMatcher             256                     PREFIX_HIT  avgt   10    52.008 ±   0.286  ns/op
 * PackageMatcherBenchmark.triePackageMatcher             256                    PREFIX_MISS  avgt   10    31.388 ±   0.135  ns/op
 * PackageMatcherBenchmark.triePackageMatcher             256                   WILDCARD_HIT  avgt   10    93.055 ±   1.112  ns/op
 * PackageMatcherBenchmark.triePackageMatcher             256  WILDCARD_PREFIX_HIT_EXPR_MISS  avgt   10    92.940 ±   0.801  ns/op
 * PackageMatcherBenchmark.triePackageMatcher             256                  WILDCARD_MISS  avgt   10     3.418 ±   0.021  ns/op
 */
@Threads(value = 1)
@State(Scope.Thread)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(NANOSECONDS)
@Warmup(iterations = 3, time = 3, timeUnit = SECONDS)
@Measurement(iterations = 5, time = 3, timeUnit = SECONDS)
@Fork(value = 2, jvmArgs = {"-server", "-Xmx2G", "-Xms2G"})
public class PackageMatcherBenchmark {

    private OldLinearMatcher oldLinearMatcher;

    private PackageMatcher triePackageMatcher;

    private String[] packageNames;

    private int index;

    @Param(value = {"16", "64", "256"})
    private int packageCount;

    @Param(value = {
            "PREFIX_HIT",
            "PREFIX_MISS",
            "WILDCARD_HIT",
            "WILDCARD_PREFIX_HIT_EXPR_MISS",
            "WILDCARD_MISS"
    })
    private String scenario;

    @Setup
    public void setup() {
        final Set<String> packageExprs = packageExprSet(packageCount);
        oldLinearMatcher = new OldLinearMatcher(packageExprs);
        triePackageMatcher = new PackageMatcher();
        triePackageMatcher.init(packageExprs);
        packageNames = packageNames(scenario, packageCount);
        index = 0;
    }

    @Benchmark
    public boolean oldLinearMatcher() {
        return oldLinearMatcher.isMatch(nextPackageName());
    }

    @Benchmark
    public boolean triePackageMatcher() {
        return triePackageMatcher.isMatch(nextPackageName());
    }

    private String nextPackageName() {
        final String result = packageNames[index++];
        if (index == packageNames.length) {
            index = 0;
        }
        return result;
    }

    private static Set<String> packageExprSet(int packageCount) {
        final Set<String> result = new HashSet<>(packageCount << 3);
        for (int i = 0; i < packageCount; i++) {
            result.add("com/example/pkg" + i + "/api/");
            result.add("com/example/pkg" + i + "/service/*Service");
            result.add("com/example/pkg" + i + "/dao/*Repository");
            result.add("org/sample/pkg" + i + "/controller/*Controller");
        }
        return result;
    }

    private static String[] packageNames(String scenario, int packageCount) {
        final int middleIndex = packageCount >>> 1;
        final int lastIndex = packageCount - 1;
        if ("PREFIX_HIT".equals(scenario)) {
            return new String[]{
                    "com/example/pkg0/api/UserApi",
                    "com/example/pkg" + middleIndex + "/api/OrderApi",
                    "com/example/pkg" + lastIndex + "/api/PayApi"
            };
        }

        if ("PREFIX_MISS".equals(scenario)) {
            return new String[]{
                    "com/example/pkg" + packageCount + "/api/UserApi",
                    "com/example/pkg" + (packageCount + 1) + "/service/UserService",
                    "org/sample/pkg" + packageCount + "/controller/UserController"
            };
        }

        if ("WILDCARD_HIT".equals(scenario)) {
            return new String[]{
                    "com/example/pkg0/service/UserService",
                    "com/example/pkg" + middleIndex + "/dao/OrderRepository",
                    "org/sample/pkg" + lastIndex + "/controller/UserController"
            };
        }

        if ("WILDCARD_PREFIX_HIT_EXPR_MISS".equals(scenario)) {
            return new String[]{
                    "com/example/pkg0/service/UserController",
                    "com/example/pkg" + middleIndex + "/dao/OrderService",
                    "org/sample/pkg" + lastIndex + "/controller/UserService"
            };
        }

        //WILDCARD_MISS
        return new String[]{
                "io/unknown/pkg0/service/UserService",
                "cn/other/pkg" + middleIndex + "/dao/OrderRepository",
                "net/sample/pkg" + lastIndex + "/controller/UserController"
        };
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(PackageMatcherBenchmark.class.getSimpleName())
                .build()).run();
    }

    private static class OldLinearMatcher {

        private final Set<String> packagePrefixSet = new HashSet<>();

        private final Set<String> packageExpSet = new HashSet<>();

        OldLinearMatcher(Set<String> packageExprs) {
            for (String packageExpr : packageExprs) {
                if (packageExpr.indexOf('*') > 0) {
                    packageExpSet.add(packageExpr);
                } else {
                    packagePrefixSet.add(packageExpr);
                }
            }
        }

        boolean isMatch(String packageName) {
            for (String prefix : packagePrefixSet) {
                if (packageName.startsWith(prefix)) {
                    return true;
                }
            }

            for (String exp : packageExpSet) {
                if (StrMatchUtils.isMatch(packageName, exp)) {
                    return true;
                }
            }
            return false;
        }
    }
}
