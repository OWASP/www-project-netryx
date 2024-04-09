package org.owasp.netryx.constant;

public enum HttpProtocol {
    HTTP_1_0("10"),
    HTTP_1_1("11"),
    HTTP_2_0("20"),
    HTTP_3_0("30");

    private final String number;

    HttpProtocol(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}
