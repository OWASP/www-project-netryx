# Netryx Armor
## Introduction
Netryx Armor (**NetArmor**) is a reactive security solution for modern digital environments. It combines ESAPI features and is designed for Netty-based applications, offering strong protection for high-risk areas including finance.

It is backbone of Netryx eco-system, designed to provide core functionality for other modules and user-side intrusion prevention mechanism (e.g encoders, validators, etc).
## Get started
### Gradle
```groovy
implementation "org.owasp.netryx:armor:1.0.0"
```
### Maven
```xml
<dependency>
    <groupId>org.owasp.netryx</groupId>
    <artifactId>armor</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Features
- [x] ReDoS resistant Validators (Custom and implemented like email, credit card, etc.)
- [x] Injection protection via HTML, JS, LDAP and CMD encoders
- [x] Secure memory allocation and obfuscation for storing sensitive data in cache via Netryx Memory
- [x] Path Traversal protection

# Usage 
First of all, you need to create a NetArmor instance.
```java
public class Main {
    public static void main(String[] args) {
        NetArmor armor = NetArmor.create();
    }
}
```

To specify custom your `SecurityConfig` instance, or already implemented JSON based CommonSecurityConfig, use:

```java
public class Main {
    public static void main(String[] args) {
        var securityConfig = new CommonSecurityConfig(new CommonConfig());
        NetArmor armor = NetArmor.create(securityConfig);
    }
}
```

## Validators
Improper input validation is one of the most common security vulnerabilities, and it is the root cause of many other security vulnerabilities, including XSS, SQL Injection, etc.

NetArmor provides a set of **configurable** rules, that can be used to validate user input,
with ability to customize them or create your own validators. Rules are fetched from `SecurityConfig` instance.

### Default rules:
- `username`
- `password`
- `email`
- `uuid`
- `phoneNumber`
- `url`
- `ipv4`
- `ipv6`
- `creditCard`
- `ssn`

Thanks to `ihateregex.io` for providing some of the regexes.

Please note, that as password should be strong enough, it is recommended to use `password` rule for password validation.
Rule specifies, that password should be at least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character.

### Exception thrown:
- `ValidationException` - if input is invalid
- `UnknownRuleException` - if rule is not found.

Unknown rule exception is thrown, because it is better to throw an exception, than to allow the input to pass through the validation, and cause security vulnerability.
Every rule can be defined in `SecurityConfig` instance.

### Sample

```java
public class Main {
    public static void main(String[] args) {
        NetArmor armor = NetArmor.newArmor();

        CompletableFuture<String> result = armor.validator().input().validate("email", "my@mail.com");
    }
}
```

## Encoders
NetArmor provides encoders for securing from:
* XSS injection in HTML and JS contexts
* LDAP injection
* Command injection

For html sanitizaion, AntiSamy library by OWASP is used: [AntiSamy](https://owasp.org/www-project-antisamy/)

### Exception thrown
- `SanitizationException` - If sanitization fails
- `IllegalArgumentException` - If null input passed to encoders

### HTML Encoder
HTML encoder allows you to encode input in several contexts:

* `ALL`: Encodes all characters specified in the string `&<>'"/=``.
* `CONTENT`: Encodes only &<>.
* `ATTRIBUTE`: Encodes `&<>'"/=``.
* `SINGLE_QUOTED_ATTRIBUTE`: Encodes `&<'/=``.
* `DOUBLE_QUOTED_ATTRIBUTE`: Encodes `&<"/=``.

`ALL` mode is used as default encoding mode, if none is specified.

#### Default encoder
```java
public class Main {
    public static void main(String[] args) {
        NetArmor armor = getNetArmor(); // Implementation of NetArmor
        
        var encoded = armor.encoder()
                .html()
                .encode("<script>alert('XSS')</script>");
        
        var sanitizedHtml = armor.encoder()
                .html()
                .sanitize("your html");
    }
}
```
#### Encoder with custom parameters
To configure HTML Encoder mode and specify custom policy for Antisamy, you should pass `HtmlEncoderConfig` instance to method:

`HtmlEncoderConfig.withMode(HtmlEncodingMode.ALL)` - Custom encoding mode and default AntiSamy policy
`HtmlEncoderConfig.withPolicy(Policy)` - Default encoding mode (`ALL`), custom AntiSamy policy

or just specify both of them:
`HtmlEncoderConfig.create(HtmlEncodingMode, Policy)`

Please note, that by default `antisamy-slashdot.xml` is used as policy.

### JavaScript Encoder
The JavaScript encoder defines various encoding modes available for different contexts:

* `ATTRIBUTE`: Encodes characters suitable for JavaScript attributes. Specially handles quotes and characters that could break out of attribute context.
* `BLOCK`: Encodes characters for safe inclusion in JavaScript code blocks.
* `HTML`: Encodes characters suitable for embedding JavaScript within HTML, taking into account characters that are problematic in HTML contexts.

#### Usage
```java
public static void main(String[] args) {
    var armor = NetArmor.create();
    
    var encodedInput = armor.encoder()
            .js(JavaScriptEncoding.ATTRIBUTE)
            .encode("var str = \"name=John\";");
}
```

JavaScript encoder supports ascii-only encoding. To do this, specify `JavaScriptEncoderConfig`:
```java
public static void main(String[] args) {
    var armor = NetArmor.create();
    var config = JavaScriptEncoderConfig.create(JavaScriptEncoding.HTML, true);
    
    var encodedInput = armor.encoder()
            .js(config)
            .encode("var str = \"name=John\";");
}
```

### LDAP Encoder
It is used for encoding input strings to be safely used in LDAP distinguished names (DNs) and search filters. 
This encoder ensures that all potentially dangerous characters are properly encoded to prevent LDAP injection attacks

#### Usage
```java
public static void main(String[] args) {
    var armor = NetArmor.create();

    var encodedInput = armor.encoder()
            .ldap()
            .encode("admin(user)");
}
```

### Command Encoder
It is used to for encoding input strings to be safely used in command-line execution. 
This encoder ensures that all potentially dangerous characters are properly escaped to prevent command injection attacks.

#### Usage
```java
public static void main(String[] args) {
    var armor = NetArmor.create();

    var encodedInput = armor.encoder()
            .cmd()
            .encode("unsafe;command");
}
```

## File Path Transversal Protection
It is a common security vulnerability, that allows an attacker to access files, that are outside of the web root directory.
NetArmor provides a simple protection against this vulnerability, that can be used to protect your application.

### Exception thrown
- `SanitizationException` - if input is invalid or contains path transversal characters

### Sample
```java
public class Main {
    public static void main(String[] args) {
        NetArmor armor = getNetArmor(); // Implementation of NetArmor
        
        var sanitizedPath = armor.validator()
                .path()
                .validate("/etc/passwd");
    }
}
```

## Secure Memory Allocation
NetArmor provides a simple solution for storing sensitive data in memory, that is not swappable to disk, and is obfuscated.

Valid memory allocation is required for storing sensitive data, including passwords, tokens, etc, that are crucial for security of your application.
```
Please note, that only Linux and MacOS are supported for now.
```

See [Netryx Memory](../memory) for more info

### Sample
```java
public class Main {
    public static void main(String[] args) {
        NetArmor armor = getNetArmor(); // Implementation of NetArmor
        
        var secureMemory = armor.memory().allocate(11); // Allocates 11 bytes of secure memory
        secureMemory.write("Hello World".getBytes()); // Writes "Hello World" to secure memory
        secureMemory.obfuscate(); // Obfuscates secure memory
        
        var helloWorld = secureMemory.deobfuscate(bytes -> new String(bytes)); // Deobfuscates secure memory and returns "Hello World"
        // Memory is still obfuscated, if you want to deobfuscate it permanently, call secureMemory.deobfuscate()
    }
}
```

## Securing the pipeline
Please refer to [Netryx Pipeline](../armor-pipeline)