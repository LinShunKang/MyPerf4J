package cn.myperf4j.base.config;

import cn.myperf4j.base.constant.ClassLevels;
import cn.myperf4j.base.util.MapUtils;
import cn.myperf4j.base.util.StrMatchUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by LinShunkang on 2019/05/04
 * <p>
 * MethodLevelMapping=Controller:[*Controller];Api:[*Api*];
 */
public final class LevelMappingFilter {

    private static final Map<String, List<String>> LEVEL_EXPS_MAP = MapUtils.createLinkedHashMap(20);

    static {
        //初始化默认的映射关系
        LEVEL_EXPS_MAP.put(ClassLevels.CONTROLLER, Collections.singletonList("*Controller"));
        LEVEL_EXPS_MAP.put(ClassLevels.INTERCEPTOR, Collections.singletonList("*Interceptor"));
        LEVEL_EXPS_MAP.put(ClassLevels.CONSUMER, Collections.singletonList("*Consumer"));
        LEVEL_EXPS_MAP.put(ClassLevels.LISTENER, Collections.singletonList("*Listener"));
        LEVEL_EXPS_MAP.put(ClassLevels.API, Arrays.asList("*Api", "*ApiImpl"));
        LEVEL_EXPS_MAP.put(ClassLevels.SERVICE, Arrays.asList("*Service", "*ServiceImpl"));
        LEVEL_EXPS_MAP.put(ClassLevels.CACHE, Arrays.asList("*Cache", "*CacheImpl"));
        LEVEL_EXPS_MAP.put(ClassLevels.DAO, Collections.singletonList("*DAO"));
        LEVEL_EXPS_MAP.put(ClassLevels.UTILS, Collections.singletonList("*Utils"));
    }

    /**
     * 根据 simpleClassName 返回 ClassLevel
     */
    public static String getClassLevel(String simpleClassName) {
        for (Map.Entry<String, List<String>> entry : LEVEL_EXPS_MAP.entrySet()) {
            String level = entry.getKey();
            List<String> mappingExps = entry.getValue();
            for (int i = 0; i < mappingExps.size(); ++i) {
                if (StrMatchUtils.isMatch(simpleClassName, mappingExps.get(i))) {
                    return level;
                }
            }
        }
        return ClassLevels.OTHERS;
    }

    public static void putLevelMapping(String classLevel, List<String> expList) {
        LEVEL_EXPS_MAP.put(classLevel, expList);
    }

}
