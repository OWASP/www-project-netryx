# Netryx Armor
## Reactor Netty Provider
This provider is used to integrate NetArmor Enhanced security features to Reactor Netty based servers.

## Dependency
### Gradle
```groovy
implementation "org.owasp.netryx:reactor-netty-provider:1.0.0"
```
### Maven
```xml
<dependency>
    <groupId>org.owasp.netryx</groupId>
    <artifactId>reactor-netty-provider</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Examples:
#### Spring Boot Reactive
```java
@Component
public class NetArmorCustomizer implements NettyServerCustomizer {
    @Override
    public HttpServer apply(HttpServer server) {
        CommonSecurityConfig config = new CommonSecurityConfig(new CommonConfig());

        NetArmorPipeline armor = NetArmorPipeline.newBuilder(new ReactorNettyProvider(config))
            .intrusion(new MyIntrusionHandler()) // add your handlers
            .build();

        return armor.pipeline().configure(server);
    }
}
```