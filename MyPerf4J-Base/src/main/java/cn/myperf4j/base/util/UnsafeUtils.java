package cn.myperf4j.base.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * Created by LinShunkang on 2020/11/24
 */
public final class UnsafeUtils {

    private static final Unsafe UNSAFE = generateUnsafe();

    private static Unsafe generateUnsafe() {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>) () -> {
                final Class<Unsafe> k = Unsafe.class;
                for (Field f : k.getDeclaredFields()) {
                    f.setAccessible(true);

                    final Object x = f.get(null);
                    if (k.isInstance(x)) {
                        return k.cast(x);
                    }
                }
                return null; // The sun.misc.Unsafe field does not exist.
            });
        } catch (Throwable t) {
            return null;
        }
    }

    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    public static long fieldOffset(Class<?> clz, String fieldName) throws RuntimeException {
        try {
            assert UNSAFE != null;
            return UNSAFE.objectFieldOffset(clz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private UnsafeUtils() {
        //empty
    }
}
