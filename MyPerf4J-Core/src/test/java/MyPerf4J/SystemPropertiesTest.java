package MyPerf4J;

/**
 * Created by LinShunkang on 2019/07/28
 */
public class SystemPropertiesTest {

    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("file.separator"));
        System.out.println(System.getProperty("path.separator"));
        System.out.println(System.getProperty("line.separator"));
    }
}
