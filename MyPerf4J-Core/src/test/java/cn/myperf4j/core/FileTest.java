package cn.myperf4j.core;

import cn.myperf4j.base.util.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * 文件测试
 */
public class FileTest {

    private static final File baseDir = new File(System.getProperty("user.dir"));

    private static final File testDir = new File(baseDir, "temp");

    @BeforeClass
    public static void prepare() {
        getDir(FileTest.class.getSimpleName());
    }

    @AfterClass
    public static void cleanup() {
        deleteDir(FileTest.class.getSimpleName());
    }

    @Test
    public void testFileRename() {
        String testFile = getTestDir().getPath() + "/testFileRename";
        final Path testFilePath1 = Paths.get(testFile + 1);
        final Path testFilePath2 = Paths.get(testFile + 2);
        final Path tmpFilePath = Paths.get(testFile + "_tmp");

        for (int i = 0; i < 2; i++) {
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(tmpFilePath.toFile(), false), 8192)) {
                fileWriter.write("#This is a file automatically generated by MyPerf4J, please do not edit!\n");
                fileWriter.flush();
                // 流还没释放导致
                Assert.assertFalse(tmpFilePath.toFile().renameTo(testFilePath1.toFile()));
                Assert.assertFalse(testFilePath1.toFile().setReadOnly());
            } catch (Exception e) {
                Logger.error("testFileRename error", e);
            }

            if (i == 0) {
                // 第一次rename能成功 文件存在的情况setReadOnly始终能成功
                Assert.assertTrue(tmpFilePath.toFile().renameTo(testFilePath2.toFile()));
                Assert.assertTrue(testFilePath2.toFile().setReadOnly());
            } else {
                // 第一次rename能成功，后续就失败
                Assert.assertFalse(tmpFilePath.toFile().renameTo(testFilePath2.toFile()));
                Assert.assertTrue(testFilePath2.toFile().setReadOnly());
            }
        }
        // tmp文件存在
        Assert.assertTrue(tmpFilePath.toFile().exists());
        // BufferedWriter没释放时rename的文件不存在
        Assert.assertFalse(testFilePath1.toFile().exists());
        // BufferedWriter释放后rename的文件存在
        Assert.assertTrue(testFilePath2.toFile().exists());
    }

    @Test
    public void testFilesMove() throws IOException {
        String testFile = getTestDir().getPath() + "/testFilesMove";
        final Path testFilePath = Paths.get(testFile);
        final Path tmpFilePath = Paths.get(testFile + "_tmp");
        if (!tmpFilePath.toFile().exists()) {
            Files.createFile(tmpFilePath);
            tmpFilePath.toFile().setReadOnly();
        }
        Assert.assertFalse(tmpFilePath.toFile().canWrite());

        final Path moved = Files.move(tmpFilePath, testFilePath, StandardCopyOption.REPLACE_EXISTING);
        moved.toFile().setReadOnly();
        Assert.assertFalse(moved.toFile().canWrite());

        Files.move(moved, tmpFilePath, StandardCopyOption.REPLACE_EXISTING);
        Assert.assertFalse(tmpFilePath.toFile().canWrite());
    }

    private File getTestDir() {
        return getDir(FileTest.class.getSimpleName());
    }

    private static File getDir(String dir) {
        File file = new File(testDir, dir);
        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                throw new IllegalStateException("Can't create dir: " + file);
            }
        }
        return file;
    }

    private static void deleteDir(String dir) {
        deleteDir(testDir, dir);
    }

    private static void deleteDir(final File parent, String dir) {
        final File dirFile = new File(parent, dir);
        if (dirFile.isDirectory()) {
            for (final File f: Objects.requireNonNull(dirFile.listFiles())) {
                if (f.isDirectory()) {
                    deleteDir(dirFile, f.getName());
                    continue;
                }

                if (f.isFile()) {
                    if (!f.delete()) {
                        throw new IllegalStateException("Can't delete file: " + f);
                    }
                    Logger.info("Delete file: " + f);
                }
            }

            if (!dirFile.delete()) {
                throw new IllegalStateException("Can't delete directory: " + dirFile);
            }
            Logger.info("Delete directory: " + dirFile);
        }

        testDir.deleteOnExit();
        Logger.info("Delete testDir: " + testDir);
    }
}
