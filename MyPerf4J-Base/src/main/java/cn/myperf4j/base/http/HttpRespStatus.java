package cn.myperf4j.base.http;

/**
 * Created by LinShunkang on 2020/05/15
 */
public class HttpRespStatus {

    public static final HttpRespStatus CONTINUE = new HttpRespStatus(100, "Continue");

    public static final HttpRespStatus SWITCHING_PROTOCOLS = new HttpRespStatus(101, "Switching Protocols");

    public static final HttpRespStatus PROCESSING = new HttpRespStatus(102, "Processing");

    public static final HttpRespStatus OK = new HttpRespStatus(200, "OK");

    public static final HttpRespStatus CREATED = new HttpRespStatus(201, "Created");

    public static final HttpRespStatus ACCEPTED = new HttpRespStatus(202, "Accepted");

    public static final HttpRespStatus NON_AUTHORITATIVE_INFORMATION = new HttpRespStatus(203, "Non-Authoritative Information");

    public static final HttpRespStatus NO_CONTENT = new HttpRespStatus(204, "No Content");

    public static final HttpRespStatus RESET_CONTENT = new HttpRespStatus(205, "Reset Content");

    public static final HttpRespStatus PARTIAL_CONTENT = new HttpRespStatus(206, "Partial Content");

    public static final HttpRespStatus MULTI_STATUS = new HttpRespStatus(207, "Multi-Status");

    public static final HttpRespStatus MULTIPLE_CHOICES = new HttpRespStatus(300, "Multiple Choices");

    public static final HttpRespStatus MOVED_PERMANENTLY = new HttpRespStatus(301, "Moved Permanently");

    public static final HttpRespStatus FOUND = new HttpRespStatus(302, "Found");

    public static final HttpRespStatus SEE_OTHER = new HttpRespStatus(303, "See Other");

    public static final HttpRespStatus NOT_MODIFIED = new HttpRespStatus(304, "Not Modified");

    public static final HttpRespStatus USE_PROXY = new HttpRespStatus(305, "Use Proxy");

    public static final HttpRespStatus TEMPORARY_REDIRECT = new HttpRespStatus(307, "Temporary Redirect");

    public static final HttpRespStatus PERMANENT_REDIRECT = new HttpRespStatus(308, "Permanent Redirect");

    public static final HttpRespStatus BAD_REQUEST = new HttpRespStatus(400, "Bad Request");

    public static final HttpRespStatus UNAUTHORIZED = new HttpRespStatus(401, "Unauthorized");

    public static final HttpRespStatus PAYMENT_REQUIRED = new HttpRespStatus(402, "Payment Required");

    public static final HttpRespStatus FORBIDDEN = new HttpRespStatus(403, "Forbidden");

    public static final HttpRespStatus NOT_FOUND = new HttpRespStatus(404, "Not Found");

    public static final HttpRespStatus METHOD_NOT_ALLOWED = new HttpRespStatus(405, "Method Not Allowed");

    public static final HttpRespStatus NOT_ACCEPTABLE = new HttpRespStatus(406, "Not Acceptable");

    public static final HttpRespStatus PROXY_AUTHENTICATION_REQUIRED = new HttpRespStatus(407, "Proxy Authentication Required");

    public static final HttpRespStatus REQUEST_TIMEOUT = new HttpRespStatus(408, "Request Timeout");

    public static final HttpRespStatus CONFLICT = new HttpRespStatus(409, "Conflict");

    public static final HttpRespStatus GONE = new HttpRespStatus(410, "Gone");

    public static final HttpRespStatus LENGTH_REQUIRED = new HttpRespStatus(411, "Length Required");

    public static final HttpRespStatus PRECONDITION_FAILED = new HttpRespStatus(412, "Precondition Failed");

    public static final HttpRespStatus REQUEST_ENTITY_TOO_LARGE = new HttpRespStatus(413, "Request Entity Too Large");

    public static final HttpRespStatus REQUEST_URI_TOO_LONG = new HttpRespStatus(414, "Request-URI Too Long");

    public static final HttpRespStatus UNSUPPORTED_MEDIA_TYPE = new HttpRespStatus(415, "Unsupported Media Type");

    public static final HttpRespStatus REQUESTED_RANGE_NOT_SATISFIABLE = new HttpRespStatus(416, "Requested Range Not Satisfiable");

    public static final HttpRespStatus EXPECTATION_FAILED = new HttpRespStatus(417, "Expectation Failed");

    public static final HttpRespStatus MISDIRECTED_REQUEST = new HttpRespStatus(421, "Misdirected Request");

    public static final HttpRespStatus UNPROCESSABLE_ENTITY = new HttpRespStatus(422, "Unprocessable Entity");

    public static final HttpRespStatus LOCKED = new HttpRespStatus(423, "Locked");

    public static final HttpRespStatus FAILED_DEPENDENCY = new HttpRespStatus(424, "Failed Dependency");

    public static final HttpRespStatus UNORDERED_COLLECTION = new HttpRespStatus(425, "Unordered Collection");

    public static final HttpRespStatus UPGRADE_REQUIRED = new HttpRespStatus(426, "Upgrade Required");

    public static final HttpRespStatus PRECONDITION_REQUIRED = new HttpRespStatus(428, "Precondition Required");

    public static final HttpRespStatus TOO_MANY_REQUESTS = new HttpRespStatus(429, "Too Many Requests");

    public static final HttpRespStatus REQUEST_HEADER_FIELDS_TOO_LARGE = new HttpRespStatus(431, "Request Header Fields Too Large");

    public static final HttpRespStatus INTERNAL_SERVER_ERROR = new HttpRespStatus(500, "Internal Server Error");

    public static final HttpRespStatus NOT_IMPLEMENTED = new HttpRespStatus(501, "Not Implemented");

    public static final HttpRespStatus BAD_GATEWAY = new HttpRespStatus(502, "Bad Gateway");

    public static final HttpRespStatus SERVICE_UNAVAILABLE = new HttpRespStatus(503, "Service Unavailable");

    public static final HttpRespStatus GATEWAY_TIMEOUT = new HttpRespStatus(504, "Gateway Timeout");

    public static final HttpRespStatus HTTP_VERSION_NOT_SUPPORTED = new HttpRespStatus(505, "HTTP Version Not Supported");

    public static final HttpRespStatus VARIANT_ALSO_NEGOTIATES = new HttpRespStatus(506, "Variant Also Negotiates");

    public static final HttpRespStatus INSUFFICIENT_STORAGE = new HttpRespStatus(507, "Insufficient Storage");

    public static final HttpRespStatus NOT_EXTENDED = new HttpRespStatus(510, "Not Extended");

    public static final HttpRespStatus NETWORK_AUTHENTICATION_REQUIRED = new HttpRespStatus(511, "Network Authentication Required");

    private final int code;

    private final String phrase;

    public HttpRespStatus(int code) {
        this.code = code;
        this.phrase = HttpStatusClass.valueOf(code).getDefaultPhrase() + '(' + code + ')';
    }

    public HttpRespStatus(int code, String phrase) {
        this.code = code;
        this.phrase = phrase;
    }

    public int getCode() {
        return code;
    }

    public String getPhrase() {
        return phrase;
    }

    public static HttpRespStatus valueOf(int code) {
        HttpRespStatus status = valueOf0(code);
        return status != null ? status : new HttpRespStatus(code);
    }

    private static HttpRespStatus valueOf0(int code) {
        switch (code) {
            case 100:
                return CONTINUE;
            case 101:
                return SWITCHING_PROTOCOLS;
            case 102:
                return PROCESSING;
            case 200:
                return OK;
            case 201:
                return CREATED;
            case 202:
                return ACCEPTED;
            case 203:
                return NON_AUTHORITATIVE_INFORMATION;
            case 204:
                return NO_CONTENT;
            case 205:
                return RESET_CONTENT;
            case 206:
                return PARTIAL_CONTENT;
            case 207:
                return MULTI_STATUS;
            case 300:
                return MULTIPLE_CHOICES;
            case 301:
                return MOVED_PERMANENTLY;
            case 302:
                return FOUND;
            case 303:
                return SEE_OTHER;
            case 304:
                return NOT_MODIFIED;
            case 305:
                return USE_PROXY;
            case 307:
                return TEMPORARY_REDIRECT;
            case 308:
                return PERMANENT_REDIRECT;
            case 400:
                return BAD_REQUEST;
            case 401:
                return UNAUTHORIZED;
            case 402:
                return PAYMENT_REQUIRED;
            case 403:
                return FORBIDDEN;
            case 404:
                return NOT_FOUND;
            case 405:
                return METHOD_NOT_ALLOWED;
            case 406:
                return NOT_ACCEPTABLE;
            case 407:
                return PROXY_AUTHENTICATION_REQUIRED;
            case 408:
                return REQUEST_TIMEOUT;
            case 409:
                return CONFLICT;
            case 410:
                return GONE;
            case 411:
                return LENGTH_REQUIRED;
            case 412:
                return PRECONDITION_FAILED;
            case 413:
                return REQUEST_ENTITY_TOO_LARGE;
            case 414:
                return REQUEST_URI_TOO_LONG;
            case 415:
                return UNSUPPORTED_MEDIA_TYPE;
            case 416:
                return REQUESTED_RANGE_NOT_SATISFIABLE;
            case 417:
                return EXPECTATION_FAILED;
            case 421:
                return MISDIRECTED_REQUEST;
            case 422:
                return UNPROCESSABLE_ENTITY;
            case 423:
                return LOCKED;
            case 424:
                return FAILED_DEPENDENCY;
            case 425:
                return UNORDERED_COLLECTION;
            case 426:
                return UPGRADE_REQUIRED;
            case 428:
                return PRECONDITION_REQUIRED;
            case 429:
                return TOO_MANY_REQUESTS;
            case 431:
                return REQUEST_HEADER_FIELDS_TOO_LARGE;
            case 500:
                return INTERNAL_SERVER_ERROR;
            case 501:
                return NOT_IMPLEMENTED;
            case 502:
                return BAD_GATEWAY;
            case 503:
                return SERVICE_UNAVAILABLE;
            case 504:
                return GATEWAY_TIMEOUT;
            case 505:
                return HTTP_VERSION_NOT_SUPPORTED;
            case 506:
                return VARIANT_ALSO_NEGOTIATES;
            case 507:
                return INSUFFICIENT_STORAGE;
            case 510:
                return NOT_EXTENDED;
            case 511:
                return NETWORK_AUTHENTICATION_REQUIRED;
        }
        return null;
    }
}
