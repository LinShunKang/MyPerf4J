package MyPerf4J.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by LinShunkang on 2019/02/06
 */
public class DaoInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return new Object();
    }
}
