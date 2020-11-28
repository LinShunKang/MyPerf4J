package cn.myperf4j.base.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * Created by LinShunkang on 2020/11/24
 */
public final class UnsafeUtils {

    private static final Unsafe UNSAFE;

    static {
        UNSAFE = generateUnsafe();
    }

    private static Unsafe generateUnsafe() {
        sun.misc.Unsafe unsafe = null;
        try {
            unsafe = AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                @Override
                public Unsafe run() throws Exception {
                    Class<Unsafe> k = Unsafe.class;
                    for (Field f : k.getDeclaredFields()) {
                        f.setAccessible(true);
                        Object x = f.get(null);
                        if (k.isInstance(x)) {
                            return k.cast(x);
                        }
                    }
                    // The sun.misc.Unsafe field does not exist.
                    return null;
                }
            });
        } catch (Throwable e) {
            // Catching Throwable here due to the fact that Google AppEngine raises NoClassDefFoundError
            // for Unsafe.
        }
        return unsafe;
    }

    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    private UnsafeUtils() {
        //empty
    }
}
