package framework.utils.globalConstants;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public enum HttpStatus {

    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOL(101, "Switching Protocols"),
    PROCESSING(102, "Processing"),

    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    NO_CONTENT(204,  "No Content"),
    RESET_CONTENT(205, "Reset Content"),
    PARTIAL_CONTENT(206, "Partial Content"),
    MULTI_STATUS(207, "Multi-Status (WebDAV; RFC 4918"),
    ALREADY_REPORTED(208, "Already Reported (WebDAV; RFC 5842)" ),
    IM_USED(226, "IM Used (RFC 3229)"),

    MULTIPLE_CHOICES(300, "Multiple Choices"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other (since HTTP/1.1)"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy (since HTTP/1.1)"),
    SWITCH_PROXY(306, "Switch Proxy"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect (since HTTP/1.1)"),
    PERMANENT_REDIRECT(308, "Permanent Redirect (approved as experimental RFC)[12]"),

    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates (RFC 2295)"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage (WebDAV; RFC 4918)"),
    LOOP_DETECTED(508, "Loop Detected (WebDAV; RFC 5842)"),
    BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded (Apache bw/limited extension)"),
    NOT_EXTEND(510, "Not Extended (RFC 2774)"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required (RFC 6585)"),
    CONNECTION_TIMED_OUT(522, "Connection timed out"),
    PROXY_DECLINED_REQUEST(523, "Proxy Declined Request"),
    TIMEOUT_OCCURRED(524, "A timeout occurred")
    ;

    private int code;
    private String desc;
    private String text;

    HttpStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
        this.text = Integer.toString(code);
    }

    /**
     * Gets the HTTP status code
     * @return the status code number
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets the HTTP status code as a text string
     * @return the status code as a text string
     */
    public String asText() {
        return text;
    }

    /**
     * Get the description
     * @return the description of the status code
     */
    public String getDesc() {
        return desc;
    }

}
