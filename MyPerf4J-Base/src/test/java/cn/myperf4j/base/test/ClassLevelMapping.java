package cn.myperf4j.base.test;

import cn.myperf4j.base.config.LevelMappingFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by LinShunkang on 2019/05/12
 */
public class ClassLevelMapping {

    @Before
    public void init() {
        LevelMappingFilter.putLevelMapping("Controller", Collections.singletonList("*Controller"));
        LevelMappingFilter.putLevelMapping("Api", Collections.singletonList("*Api*"));
        LevelMappingFilter.putLevelMapping("Service", Arrays.asList("*Service", "*ServiceImpl"));
    }

    @Test
    public void test() {
        Assert.assertEquals(LevelMappingFilter.getClassLevel("com.google.UserController"), "Controller");
        Assert.assertEquals(LevelMappingFilter.getClassLevel("com.google.UserApi"), "Api");
        Assert.assertEquals(LevelMappingFilter.getClassLevel("com.google.UserApiImpl"), "Api");
        Assert.assertEquals(LevelMappingFilter.getClassLevel("com.google.UserApiService"), "Api");
        Assert.assertEquals(LevelMappingFilter.getClassLevel("com.google.UserApiServiceImpl"), "Api");
        Assert.assertEquals(LevelMappingFilter.getClassLevel("com.google.UserService"), "Service");
        Assert.assertEquals(LevelMappingFilter.getClassLevel("com.google.UserServiceImpl"), "Service");
    }
}
