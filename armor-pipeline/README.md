# Netryx Pipeline

## Get started
### Gradle
```groovy
implementation "org.owasp.netryx:pipeline:1.0.1"
```
### Maven
```xml
<dependency>
    <groupId>org.owasp.netryx</groupId>
    <artifactId>pipeline</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Usage

As netty based servers may have different handlers in different order, you should first create NettyServer instance.

By the current date (2024-02-19) following servers are implemented via separate dependencies:
- [ReactorNettyProvider](../armor-reactor-netty) - For reactor netty based servers (like Spring Boot Reactive)

We are burning the midnight oil to make NetArmorPipeline available to more Netty based web frameworks,
but nothing stops you to implement own provider and use it.

See example of implementing netty server provider in [Reactor Netty's Provider](../armor-reactor-netty)

To create own instance of NettyServer, implement `NettyServer` interfaces:
```java
public interface NettyServerProvider<T> {
    void addFirst(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addBeforeHttpRequestHandler(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addBeforeHttp1RequestHandler(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addBeforeHttp2Handler(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addBeforeSslHandler(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addAfterHttpTrafficHandler(ChannelPipeline pipeline, String name, ChannelHandler handler);

    void addLast(ChannelPipeline pipeline, String name, ChannelHandler handler);

    NettyServerPipeline<T> newPipeline();
}
```

And pipeline itself:
```java
public interface NettyServerPipeline<T> {
    void addMitigationHandler(Supplier<MitigationHandler> mitigationHandler);

    T configure(T bootstrap);
}
```

## Example pipeline configuration

```java
public class Main {
    public static void main(String[] args) {
        SecurityConfig config = getSecurityConfig(); //e.g. method to get security config
        
        NetArmorPipeline<HttpServer> armor = NetArmorPipeline.newBuilder(new ReactorNettyProvider(config))
                .config(config) // if not specified default is used
                .intrusion(intrusionHandler)
                .whitelist(whitelistLimiter)
                .blacklist(blacklistLimiter)
                .tlsFingerprint(tlsPacketHandler)
                .build();
        
        // Now configure your server
        // As we specified ReactorNettyProvider, we will use HttpServer itself.
        // You can still implement your own provider, for you case.
        var server = HttpServer.create();
        armor.configure(server);
    }
}
```

To specify custom handlers, please extend `MitigationHandler` interface, or one of its implementations:
- **InboundMitigationHandler** - for inbound mitigation (`ChannelInboundHandlerAdapter` under the hood)
- **OutboundMitigationHandler** - for outbound mitigation (`ChannelOutboundHandlerAdapter` under the hood)

And add them to the builder:
```java
public class Main {
    public static void main(String[] args) {
        SecurityConfig config = getConfig(); // implement
        NetArmorPipeline<HttpServer> armor = NetArmorPipeline.newBuilder(new ReactorNettyProvider(config))
                .mitigation(mitigationHandler)
                .build();
    }
}
```

## Basic Security Handlers
### HTTP Flood Protection
HTTP Flood is a type of Distributed Denial of Service (DDoS) attack, that is used to make a web server unavailable by sending a large number of requests to the server.
Protection is enabled by default, but you can change the configuration in SecurityConfig instance.

#### Secure by default.
### HTTP/2 0day RST Flood Protection
HTTP/2 0day RST Flood is pretty new attack, that is based on sending a large number of RST frames and resetting the connection.
This type of protection is enabled by default, but you can change the configuration in SecurityConfig instance.

#### Secure by default.
### Security Policy Management
Security policy management is a set of rules, that are used to protect your application from XSS, Clickjacking, etc.
SecurityConfig instance contains default security policy management rules, but you can change them, if you want to make them more secure.

#### Secure by default.

### IP Whitelisting/Blacklisting
#### Reactive.

NetArmor provides a simple interfaces, that can be used to implement IP whitelisting/blacklisting:

```java
public class MyWhitelist implements WhitelistLimiter {
    // Assuming that whitelist is stored in memory
    private final Set<String> whitelist = new HashSet<>();

    @Override
    public Mono<Boolean> isAllowed(String address) {
        return Mono.just(whitelist.contains(address));
    }
    
    @Override
    public String name() {
        return "my-whitelist"; // Unique name used in the pipeline
    }
}
```
```java
public class MyBlacklist implements BlacklistLimiter {
    // Assuming that blacklist is stored in memory
    private final Set<String> blacklist = new HashSet<>();

    @Override
    public Mono<Boolean> isBlocked(String address) {
        return Mono.just(blacklist.contains(address));
    }
    
    @Override
    public String name() {
        return "my-blacklist"; // Unique name used in the pipeline
    }
}
```

Don't forget to add your whitelist/blacklist handler to NetArmor instance:
```
NetArmorPileine.newBuilder(<your provider>)
        .whitelist(new MyWhitelist())
        .blacklist(new MyBlacklist())
        .build();
```

#### Possible use cases:
- Blacklisting malicious IP addresses
- Enabling access only for specific IP addresses

## Enhanced Security Handlers
NetArmor provides a set of enhanced security features, that can be used to protect your application from various attacks,
including bot attacks, traffic from malicious/infected clients etc.

### TLS (JA3) Fingerprinting
#### Reactive.

It is a technique, that is used to identify a client based on the TLS handshake,
exactly on the Client Hello message. It is a pretty new yet popular technique, that is used by many companies, including Cloudflare, to identify malicious clients.

As it is specific to every application, NetArmor provides a simple interface, that can be used to implement TLS fingerprinting:
```java
public class MyTlsFingerprinter implements FingerprintPacketHandler {
    @Override
    public Mono<ResultCode> handle(ChannelHandlerContext ctx, ClientHello ch) {
        var ja3 = ch.ja3();
        
        var hash = ja3.md5(); // md5 hash of ja3 fingerprint
        var raw = ja3.value(); // raw ja3 fingerprint
        
        // Do something with ja3
        // for example, check if it is in the blacklist
        // P.S isBlacklisted should be implemented by you
        
        if (isBlacklisted(ja3))
            return Mono.just(ResultCode.BLOCK);
        
        return Mono.just(ResultCode.OK);
    }
    
    @Override
    public String name() {
        return "my-tls-fingerprinter"; // Unique name used in the pipeline
    }
}
```
When ResultCode.BLOCK is returned, the connection is closed immediately,
there is no need to close it manually. (e.q calling `ctx.close()`)

Don't forget to add your fingerprint handler to NetArmor instance:
```
NetArmorPipeline.newBuilder(<your provider>)
        .tlsFingerprint(new MyTlsFingerprinter())
        .build();
```

#### Possible use cases:
- Allowing only specific clients to connect to your application (e.g. mobile clients)
- Blocking malicious clients (e.g. requests sent by malware on user's computer)
- Detecting bot attacks

**NOTE** JA4 fingerprints are part of **IDS** system.

### Intrusion Detection System (IDS)
#### Reactive.

IDS gives you ability to detect attacks, that are not detected by other security features, including 0day attacks,
or even identify new attacks, that are not known yet.

Although realization depends on the application, we already work on common AI Based IDS, that will be available soon.
We plan to make it open source, so that everyone can contribute to it and make it better.

It will be advanced enough to detect attacks, that are not detected by other security features.

The process of gathering information is meticulously structured into four essential phases:

- The first phase involves the collection of Remote Addresses, which helps pinpoint the origin of the traffic.
- When TLS is used, the next step is capturing TLS (**JA3** and **JA4**) fingerprints, that is very helpful in detecting spoofing.
- For traffic over HTTP/2, we also gather **HTTP/2 Fingerprints**, following the guidelines set by Akamai: [AKAMAI WHITE PAPER](https://www.blackhat.com/docs/eu-17/materials/eu-17-Shuster-Passive-Fingerprinting-Of-HTTP2-Clients-wp.pdf)
- Finally, we compile data from HTTP Requests for examination the traffic's characteristics and prepare JA4 HTTP Fingerprint (**JA4_H**).

Example of implementation:
```java
public class MyIntrusionDetector implements IntrusionDetector {
    @Override
    public Mono<DetectionResult> detect(IntrusionDetectionData data) {
        var clientHello = data.getClientHello(); // Client hello packet, if TLS is used
        
        var ja3 = data.getJa3Fingerprint(); // TLS JA3 fingerprint
        var ja4 = data.getJa4Fingerprint(); // TLS JA4 fingerprint
        var ja4h = data.getJa4HttpFingerprint(); // Http Client JA4_H fingerprint
        var http2Fingerprint = data.getHttp2Fingerprint(); // Akamai suggested HTTP/2 fingerprint

        var rawJa3 = ja3.getValue(); // raw JA3 fingerprint
        var encodedJa3 = ja3.getHash(); // md5 hash of JA3 fingerprint
        
        var rawJa4 = ja4.getValue(); // raw JA4 fingerprint
        var encodedJa4 = ja4.getEncoded(); // hashed JA4 fingerprint
        
        var ja4hValue = ja4h.getValue(); // JA4_H fingerprint
        
        var request = data.getRequest(); // The request that is being checked
        var ip = data.getRemoteAddress(); // IP address of the client
        
        var formattedHttp2Fingerprint = http2Fingerprint.toString(); // Akamai suggested format
        // P.S you can collect data in case you want to train own model in future
        // or even to contribute the dataset to us.
        // Everything depends on privacy policy of your application, if it is allowed.
        
        // DetectCode can be OK, SUSPICIOUS or MALICIOUS
        return Mono.just(new DetectionResult(DetectCode.OK, data, "User is OK"));
    }

    // Will be called if the request is detected as malicious or suspicious
    @Override
    public Mono<HandleCode> onDetected(DetectionResult result) {
        // Do something with the result
        // for example, block the user or recheck the request
        // P.S you can use result.data() to get the data, that was passed to detect method
        
        // HandleCode can be PROCEED or BLOCK
        return Mono.just(HandleCode.BLOCK);
    }
    
    @Override
    public String name() {
        return "my-intrusion-handler"; // Unique name used in the pipeline
    }
}
```

Don't forget to add your intrusion handler to NetArmor instance:
```
NetArmorPipeline.newBuilder(<your provider>)
        .intrusion(new MyIntrusionDetector())
        .build();
```