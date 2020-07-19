package cn.myperf4j.base.http.server;

import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpResponse;

/**
 * Created by LinShunkang on 2020/07/12
 */
public interface Dispatcher {

    HttpResponse dispatch(HttpRequest request);

}
