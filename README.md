![](assets/images/netryx.png)

# Hi! 👋
Welcome to Netryx, advanced java security framework lead by [exploit.org](https://exploit.org) group.
It was created to help developers make their projects more secure and effective against various threats.

Let's see what we offer:

# Netryx Modules
### Netryx Armor

It is complex yet simple to use reactive web security framework designed to be used 
in Netty based servers that includes:
- **HTTP/2 0day RST flood protection**
- **HTTP/2 fingerprinting**
- **JA3 Fingerprinting**
- **IDS interface**
- **ReDoS protected validators**
- **XSS Protection**
- **Path Traversal Protection**
- **Industry standard password encoders for use**
- **Content Security Policy management**
- **HTTP request rate limiters**
- **IP whitelisting/blacklisting**

See [Netryx Armor](/armor) for all details.

### Netryx Memory
It is simple **native** library, included in [Netryx Armor](/armor), but available separately to manage sensitive data in memory.
Must-have for storing private keys, passwords and other crucial data in cache, without worrying if data would be swapped to disk
or leaked.

With this library you can:
- **Securely allocate and deallocate memory region**
- **Reading and writing data with obfuscation**
- **Protect memory region from READ/WRITE/EXEC operations**

See [Netryx Memory](/memory) for all details.

# Education materials
We prepare series of articles for you in order to help you to dive in the world of cyber-security.
Studying these articles will not only broaden your understanding of web application security, but also provide you with practical knowledge that you can apply when developing secure applications. Together with Netryx, these materials will give you a powerful set of tools to secure your projects:

### Common Application Security Threats
**[Diving in basic threats](https://blog.exploit.org/java-appsec/)** - This article is your guide to the world of Java application security. You will learn about such common threats as:

- SQL and NoSQL injections
- XSS (Cross Site Scripting)
- Parameter tampering
- Path traversal attacks
- CSRF attacks
- DoS attacks
- Session attacks: Session fixation and Session hijacking

This knowledge will help you better understand how to protect your applications from the most common types of attacks.

### Uncovering Hidden Threats
**[Exposing in Disguise](https://blog.exploit.org/exposing-in-disguise/)** - This article introduces advanced techniques for detecting malicious traffic used at the application level. In particular, we are talking about the following:

**TLS Fingerprinting and HTTP/2 Fingerprinting**: These techniques help to identify malicious traffic, including ransomware, Advanced Persistent Attacks, DDoS attacks, and humanoid bots.
These methods play a key role in detecting and preventing dangerous threats such as:

- **Malware-based session hijacking**: Using fingerprints to identify unusual changes in session characteristics that indicate possible hijacking.
- **Advanced Persistent Attacks**: Monitor complex and time-consuming campaigns aimed at undetected infiltration of organizational networks.
- **DDoS attacks**: Identifying anomalous traffic patterns that may indicate DDoS attempts.
- **Humanoid Bots**: Identify bots designed to mimic human behavior, which pose a particular threat to detection systems.

# Contact
