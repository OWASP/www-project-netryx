package org.owasp.netryx.policy;


import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * ContentSecurityPolicy
 * Security policy that sets the Content-Security-Policy header
 */
public class ContentSecurityPolicy implements SecurityPolicy {
    // defines the default policy for fetching resources
    private List<String> defaultSrc = new ArrayList<>(List.of("'self'"));
    // defines valid sources for JavaScript
    private List<String> scriptSrc = new ArrayList<>(List.of("'self'"));
    // defines valid sources for loading media using the <object> element
    private List<String> objectSrc = new ArrayList<>(List.of("'none'"));
    // defines valid sources for embedding the resource using <frame> <iframe> <object> <embed> <applet>
    private List<String> frameAncestors = new ArrayList<>(List.of("'none'"));
    // defines valid sources for loading stylesheets
    private List<String> styleSrc = new ArrayList<>();
    // defines valid sources for loading images
    private List<String> imgSrc = new ArrayList<>();
    // defines valid sources for loading media using the <audio> and <video> elements
    private List<String> mediaSrc = new ArrayList<>();
    // defines valid sources for loading fonts using @font-face
    private List<String> fontSrc = new ArrayList<>();
    // defines valid sources for loading Workers and embedded frame contents
    private List<String> connectSrc = new ArrayList<>(List.of("'self'"));
    // defines valid sources for web workers and nested browsing contexts loaded using elements such as <frame> and <iframe>
    private List<String> childSrc = new ArrayList<>(List.of("'self'"));
    // defines valid sources for form submissions
    private List<String> formAction = new ArrayList<>(List.of("'self'"));
    // defines valid sources for the <base> element which specifies the base URL to use for all relative URLs contained within a document
    private List<String> baseUri = new ArrayList<>(List.of("'self'"));

    // defines the endpoint where the browser will send reports when a content security policy is violated
    private String reportTo = "";

    public ContentSecurityPolicy setDefaultSrc(List<String> defaultSrc) {
        this.defaultSrc = defaultSrc;
        return this;
    }

    public ContentSecurityPolicy setScriptSrc(List<String> scriptSrc) {
        this.scriptSrc = scriptSrc;
        return this;
    }

    public ContentSecurityPolicy setObjectSrc(List<String> objectSrc) {
        this.objectSrc = objectSrc;
        return this;
    }

    public ContentSecurityPolicy setFrameAncestors(List<String> frameAncestors) {
        this.frameAncestors = frameAncestors;
        return this;
    }

    public ContentSecurityPolicy setStyleSrc(List<String> styleSrc) {
        this.styleSrc = styleSrc;
        return this;
    }

    public ContentSecurityPolicy setImgSrc(List<String> imgSrc) {
        this.imgSrc = imgSrc;
        return this;
    }

    public ContentSecurityPolicy setMediaSrc(List<String> mediaSrc) {
        this.mediaSrc = mediaSrc;
        return this;
    }

    public ContentSecurityPolicy setFontSrc(List<String> fontSrc) {
        this.fontSrc = fontSrc;
        return this;
    }

    public ContentSecurityPolicy setConnectSrc(List<String> connectSrc) {
        this.connectSrc = connectSrc;
        return this;
    }

    public ContentSecurityPolicy setChildSrc(List<String> childSrc) {
        this.childSrc = childSrc;
        return this;
    }

    public ContentSecurityPolicy setFormAction(List<String> formAction) {
        this.formAction = formAction;
        return this;
    }

    public ContentSecurityPolicy setBaseUri(List<String> baseUri) {
        this.baseUri = baseUri;
        return this;
    }

    public ContentSecurityPolicy setReportTo(String reportTo) {
        this.reportTo = reportTo;
        return this;
    }

    private String buildDirectiveString(List<String> sources) {
        return String.join(" ", sources) + ";";
    }

    private void add(StringJoiner builder, String name, List<String> sources) {
        if (!sources.isEmpty())
            builder.add(name).add(buildDirectiveString(sources));
    }

    private String buildHeader() {
        StringJoiner cspBuilder = new StringJoiner(" ");

        add(cspBuilder, "default-src", defaultSrc);
        add(cspBuilder, "script-src", scriptSrc);
        add(cspBuilder, "object-src", objectSrc);
        add(cspBuilder, "frame-ancestors", frameAncestors);
        add(cspBuilder, "style-src", styleSrc);
        add(cspBuilder, "img-src", imgSrc);
        add(cspBuilder, "media-src", mediaSrc);
        add(cspBuilder, "font-src", fontSrc);
        add(cspBuilder, "connect-src", connectSrc);
        add(cspBuilder, "child-src", childSrc);
        add(cspBuilder, "form-action", formAction);
        add(cspBuilder, "base-uri", baseUri);

        if (!reportTo.isEmpty())
            cspBuilder.add("report-to").add(reportTo);

        return cspBuilder.toString().trim();
    }

    @Override
    public void apply(HttpResponse response) {
        response.headers().set(HttpHeaderNames.CONTENT_SECURITY_POLICY, buildHeader());
    }
}
