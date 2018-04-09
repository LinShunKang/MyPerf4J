package cn.perf4j.test.aspectJ;

/**
 * Created by LinShunkang on 2018/4/6
 */
public class UserService {

    private static final long MASK = 1024 * 1024 * 1024L - 1;

    @LogStartTime("Hello World")
    public long fetchUserById(long userId) {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        System.out.println("start time: " + MethodStartAspect.getStartTime());
//
        if ((userId & MASK) == 0) {
            System.out.println(userId);
        }
        return userId;
    }
}