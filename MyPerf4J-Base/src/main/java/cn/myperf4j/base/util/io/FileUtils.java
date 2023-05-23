package cn.myperf4j.base.util.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Created by LinShunkang on 2023/05/21
 */
public final class FileUtils {

    public static void writeToFile(byte[] bytes, File file) {
        try (OutputStream os = Files.newOutputStream(file.toPath())) {
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileUtils() {
        //empty
    }
}
