package cn.myperf4j.base.test;

import cn.myperf4j.base.util.PkgExpUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by LinShunkang on 2019-01-01
 */
public class PkgExpUtilsTest {

    @Test
    public void test() {
        Assert.assertTrue(PkgExpUtils.parse("cn.myperf4j.util.[Logger,DateUtils]")
                .containsAll(Arrays.asList("cn.myperf4j.util.Logger;cn.myperf4j.util.DateUtils".split(";"))));

        Assert.assertTrue(PkgExpUtils.parse("cn.myperf4j.util.[Logger,DateUtils].abc")
                .containsAll(Arrays.asList("cn.myperf4j.util.Logger.abc;cn.myperf4j.util.DateUtils.abc".split(";"))));

        Assert.assertTrue(PkgExpUtils.parse("cn.myperf4j.util.[Logger,DateUtils].abc.[e,f]")
                .containsAll(Arrays.asList("cn.myperf4j.util.Logger.abc.e;cn.myperf4j.util.DateUtils.abc.f;cn.myperf4j.util.Logger.abc.e;cn.myperf4j.util.DateUtils.abc.f".split(";"))));

    }
}
