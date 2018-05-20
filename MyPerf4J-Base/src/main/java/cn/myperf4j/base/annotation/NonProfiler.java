package cn.myperf4j.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by LinShunkang on 2018/3/11
 */

/**
 * 注意：NonProfiler的优先级要高于Profiler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NonProfiler {

}
