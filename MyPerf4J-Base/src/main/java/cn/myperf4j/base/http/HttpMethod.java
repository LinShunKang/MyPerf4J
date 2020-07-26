package cn.myperf4j.base.http;

/**
 * Created by LinShunkang on 2020/05/15
 */
public enum HttpMethod {

    UNKNOWN("UNKNOWN", false),
    HEAD("HEAD", false),
    GET("GET", false),
    POST("POST", true),
    ;

    private final String name;

    private final boolean permitsBody;

    HttpMethod(String name, boolean permitsBody) {
        this.name = name;
        this.permitsBody = permitsBody;
    }

    public String getName() {
        return name;
    }

    public boolean isPermitsBody() {
        return permitsBody;
    }

    public static HttpMethod parse(String name) {
        switch (name) {
            case "HEAD":
                return HEAD;
            case "GET":
                return GET;
            case "POST":
                return POST;
            default:
                return UNKNOWN;
        }
    }
}
