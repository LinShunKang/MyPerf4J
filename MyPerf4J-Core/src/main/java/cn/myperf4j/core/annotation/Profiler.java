package cn.myperf4j.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by LinShunkang on 2018/3/11
 */

/**
 * 该注解可以作用于类上也可以作用于具体接口上；
 * 如果作用于类上，则对该类的所有接口都起作用；
 * 如果只作用于接口上，则只针对该接口起作用，其余接口不起作用；
 * 如果既作用于类上，也作用于类中的接口上，那么以接口上的配置优先于类上的配置。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Profiler {

    int mostTimeThreshold() default 1000;//单位:ms

    int outThresholdCount() default 10;

}
