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
        Unsafe unsafe = null;
        try {
            unsafe = AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>) () -> {
                final Class<Unsafe> k = Unsafe.class;
                for (Field f : k.getDeclaredFields()) {
                    f.setAccessible(true);

                    final Object x = f.get(null);
                    if (k.isInstance(x)) {
                        return k.cast(x);
                    }
                }
                // The sun.misc.Unsafe field does not exist.
                return null;
            });
        } catch (Throwable t) {
            // Catching Throwable here due to the fact that Google AppEngine raises NoClassDefFoundError
            // for Unsafe.
        }
        return unsafe;
    }

    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    public static long fieldOffset(Class<?> clz, String fieldName) throws RuntimeException {
        try {
            return UNSAFE.objectFieldOffset(clz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getAndAddInt(Object obj, long offset, int delta) {
        int oldVal;
        do {
            oldVal = UNSAFE.getIntVolatile(obj, offset);
        } while (!UNSAFE.compareAndSwapInt(obj, offset, oldVal, oldVal + delta));
        return oldVal;
    }

    public static int getAndSetInt(Object obj, long offset, int newVal) {
        int oldVal;
        do {
            oldVal = UNSAFE.getIntVolatile(obj, offset);
        } while (!UNSAFE.compareAndSwapInt(obj, offset, oldVal, newVal));
        return oldVal;
    }

    public static Object getAndSetObject(Object obj, long offset, Object newVal) {
        Object oldVal;
        do {
            oldVal = UNSAFE.getObjectVolatile(obj, offset);
        } while (!UNSAFE.compareAndSwapObject(obj, offset, oldVal, newVal));
        return oldVal;
    }

    public static long getAndAddLong(Object obj, long offset, long delta) {
        long oldVal;
        do {
            oldVal = UNSAFE.getLongVolatile(obj, offset);
        } while (!UNSAFE.compareAndSwapLong(obj, offset, oldVal, oldVal + delta));
        return oldVal;
    }

    private UnsafeUtils() {
        //empty
    }
}
