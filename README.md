![](assets/images/netryx.png)

# Hi! 👋
Welcome to Netryx _(pronounced 'netriks')_, advanced java security framework lead by [exploit.org](https://exploit.org) group.
It was created to help developers make their web applications more secure and effective against various threats.

It is **modular** security framework that is designed to integrate to as many applications, as possible:

## Notes
- **Continual Evolution**: Keep up with regular updates as Netryx improves daily.
- **Code Quality & Contribution**: We follow strict code quality standards. Join us in our dedication to excellent security.
- **Holistic Security Approach**: Security is a continuous journey. Netryx is an important part, but it fits into a broader security strategy.
- **Community-Driven Development**: We believe in "Building Security Together". Your feedback and contributions are essential.

## Netryx Modules
### Armor
Armor is a backbone module of Netryx.
#### Brief overview:

- ReDoS Protected Input Validators based on rules
- Secure Memory Allocation for sensitive data storage and obfuscation
- JS, HTML, LDAP and CMD Encoders to protect from various injection attacks
- Centralized security event scope

#### Additional features:
- TLS Packet Parsing Engine - Base for TLS packet inspection
- Akamai HTTP/2, JA3, JA4, JA4H Fingerprinting Base - Fingerprint generation utilities 

See [Netryx Armor](/armor) for all details.

### Pipeline
**Secure By Default**

Reactive security pipeline based for Netty based servers, that uses Armor as a backbone.

#### Brief overview:
- HTTP/2 0day RST Flood Protection
- HTTP Flood Protection
- IP Whitelisting/Blacklisting
- Security Policy Management
- Intrusion Detection System (IDS)
- JA3, JA4, JA4H, HTTP/2 Fingerprinting

Currently supported Netty server pipelines:
- [Reactor Netty Provider](/armor-reactor-netty) stands for all **Reactor Netty** based servers and frameworks support (like **Spring Boot Reactive**)

See [Netryx Armor Pipeline](/armor-pipeline) for all details.

### WAF
**Secure By Default**\
**Under Active Development**

Advanced Web Application Firewall for Netty based servers, that uses Armor Pipeline's backend.

#### Brief overview:
- Passive Injection protection (SQL, XSS, LDAP, CMD, etc)
- Passive Path Traversal protection
- Malicious client detection using JA3, JA4, JA4H, and HTTP/2 fingeprints
- Flexible Rule Management System

Check road map here: [Netryx WAF](/waf)

### Machine Learning Core
**Under Active Development**

Lightweight Machine Learning library for learning and running models in an intensive environment. Utilized in Netryx WAF for mitigating threats.

Implements following algorithms:
- **Linear Regression**
- **Logistic Regression**
- **kNN**
- **Kernel SVM**
- **Naive Bayes**
- **Decision Trees**
- **Random Forest**
- **Gradient Boosting**

See [Netryx ML](/mlcore) for all details.

### Memory
Netryx Memory manages sensitive data securely in memory using Java native interface for UNIX systems.

#### Brief overview:
- Unswappable memory allocation
- Memory obfuscation
- Memory regions with protection from unauthorized READ/WRITE/EXEC 

For further information, see [Netryx Memory](/memory).

### Events
Zero dependency event manager designed for building even-based applications. Used by Netryx WAF for Security Events management.

Check it here: [Netryx Events](/events)

# Education materials
Find articles here: [OWASP Path To Secure Software series](https://dev.to/owasp/intro-to-application-security-3cj3) 

# Contact
For security concerns or to discuss potential features that you'd prefer not to disclose publicly, please reach out to us at: `security@exploit.org`.

For general inquiries or to engage in discussions on various topics, join our Telegram channel and chat at: [@exploitorg](https://t.me/exploitorg).