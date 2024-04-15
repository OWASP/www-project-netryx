# Netryx Armor
## Reactor Netty Provider
This provider is used to integrate NetArmor Enhanced security features to Reactor Netty based servers.

### Examples:
#### Spring Boot Reactive
```java
@Component
public class NetArmorCustomizer implements NettyServerCustomizer {
    @Override
    public HttpServer apply(HttpServer server) {
        CommonSecurityConfig config = new CommonSecurityConfig(new CommonConfig());

        NetArmorPipeline armorPipeline = NetArmorPipeline.newBuilder(new ReactorNettyProvider(config))
            .intrusion(new MyIntrusionHandler()) // add your handlers
            .build();

        return armorPipeline.pipeline().configure(server);
    }
}
```