![](assets/images/netryx.png)

# Hi! 👋
Welcome to Netryx _(pronounced 'netriks')_, advanced java security framework lead by [exploit.org](https://exploit.org) group.
It was created to help developers make their web applications more secure and effective against various threats.

Let's see what we offer:

## Netryx Modules
### Netryx Armor

Netryx Armor is our flagship module, offering protection tools. This includes:
- **HTTP/2 0day RST Flood Protection**: Safeguard your applications against zero-day reset (RST) flood attacks in HTTP/2.
- **HTTP/2, JA3, JA4, and JA4H Fingerprinting**: Utilize advanced fingerprinting techniques to detect and analyze patterns that help in identifying potential threats.
- **ReDoS Protected Validators**: Safely validate user inputs without the risk of Regular Expression Denial of Service (ReDoS) attacks.
- **Robust Encoders**: HTML, JS, LDAP and CMD encoders to protect from various injection attacks
- **Comprehensive Policy Management**: Manage content security policies, request rate limiting, and IP whitelisting/blacklisting effectively.

Pipeline features (like JA4 Fingerprinting) is available only for **Netty** based servers.

Currently supported Netty server pipelines:
- [Reactor Netty Provider](/armor-reactor-netty) stands for all **Reactor Netty** based servers and frameworks support (like **Spring Boot Reactive**)

See [Netryx Armor](/armor) for all details.

### Netryx WAF
**Secure By Default**\
**Under Active Development**

Reactive AI-based Firewall designed to protect Netty-based applications against threats with minimum effort. It allows defining comprehensive rules, including AI conditions, JA3, JA4, and HTTP/2 fingerprints. Passively defends your application from various threats, including SQL Injection, XSS Injection, and more, ensuring robust API security.

Check road map here: [Netryx WAF](/waf)

### Netryx ML
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

Check road map here: [Netryx ML](/mlcore)

### Netryx Memory
Netryx Memory manages sensitive data securely in memory using native interface:
- **Secure Memory Allocation and Deallocation**: Handle memory operations securely without your data being swapped to disk or leaked.
- **Data Obfuscation**: Read and write operations are obfuscated to enhance security.
- **Memory Protection**: Protect memory regions from unauthorized READ/WRITE/EXEC operations.

For further information, see [Netryx Memory](/memory).

### Netryx Events
Zero dependency event manager designed to building even-based applications. Used by Netryx WAF for Security Events management.

Check it here: [Netryx Events](/events)

# Education materials
Find articles here: [OWASP Path To Secure Software series](educational-content) 

# Contact
For security concerns or to discuss potential features that you'd prefer not to disclose publicly, please reach out to us at: `security@exploit.org`.

For general inquiries or to engage in discussions on various topics, join our Telegram channel and chat at: [@exploitorg](https://t.me/exploitorg).