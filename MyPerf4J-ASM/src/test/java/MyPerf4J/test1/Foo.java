package MyPerf4J.test1;

/**
 * Created by LinShunkang on 2018/4/23
 */
public final class Foo {

    private Foo() {
        //empty
    }

    public static long test1() {
        System.out.println("Foo.test1()");
        return System.currentTimeMillis();
    }

    public static long test2() {
        try {
            System.out.println("Foo.test1()");
            return System.currentTimeMillis();
        } catch (Throwable t) {
            System.err.println(System.err);
            return -1L;
        }
    }

    public static long test3() {
        try {
            System.out.println("Foo.test1()");
            return System.currentTimeMillis();
        } catch (Throwable t) {
            System.err.println(System.err);
            return -1L;
        } finally {
            System.out.println(System.err);
        }
    }

    public static long test4() {
        try {
            System.out.println("Foo.test1()");
            return 1L;
        } catch (Throwable t) {
            System.err.println(System.err);
            return 2L;
        } finally {
            System.out.println(System.err);
        }
    }
}
