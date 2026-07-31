package cn.myperf4j.base.test;

import cn.myperf4j.base.config.LevelMappingFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by LinShunkang on 2019/05/12
 */
public class ClassLevelMappingTest {

    @BeforeEach
    public void init() {
        LevelMappingFilter.putLevelMapping("Controller", Collections.singletonList("*Controller"));
        LevelMappingFilter.putLevelMapping("Api", Collections.singletonList("*Api*"));
        LevelMappingFilter.putLevelMapping("Service", Arrays.asList("*Service", "*ServiceImpl"));
    }

    @Test
    public void test() {
        Assertions.assertEquals("Controller", LevelMappingFilter.getClassLevel("com.google.UserController"));
        Assertions.assertEquals("Api", LevelMappingFilter.getClassLevel("com.google.UserApi"));
        Assertions.assertEquals("Api", LevelMappingFilter.getClassLevel("com.google.UserApiImpl"));
        Assertions.assertEquals("Api", LevelMappingFilter.getClassLevel("com.google.UserApiService"));
        Assertions.assertEquals("Api", LevelMappingFilter.getClassLevel("com.google.UserApiServiceImpl"));
        Assertions.assertEquals("Service", LevelMappingFilter.getClassLevel("com.google.UserService"));
        Assertions.assertEquals("Service", LevelMappingFilter.getClassLevel("com.google.UserServiceImpl"));
    }
}
