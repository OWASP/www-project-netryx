![](assets/images/netryx.png)

# Hi! 👋
Welcome to Netryx _(pronounced 'netriks')_, advanced java security framework lead by [exploit.org](https://exploit.org) group.
It was created to help developers make their web applications more secure and effective against various threats.

Let's see what we offer:

# Netryx Modules
### Netryx Armor

Netryx Armor is our flagship module, offering protection tools specifically tailored for Netty based servers. This includes:
- **HTTP/2 0day RST Flood Protection**: Safeguard your applications against zero-day reset (RST) flood attacks in HTTP/2.
- **HTTP/2, JA3, JA4, and JA4H Fingerprinting**: Utilize advanced fingerprinting techniques to detect and analyze patterns that help in identifying potential threats.
- **ReDoS Protected Validators**: Safely validate user inputs without the risk of Regular Expression Denial of Service (ReDoS) attacks.
- **XSS and Path Traversal Protection**: Guard against Cross-Site Scripting (XSS) and unauthorized path traversal to secure your web applications.
- **Comprehensive Policy Management**: Manage content security policies, request rate limiting, and IP whitelisting/blacklisting effectively.

Supported Netty server pipelines:
- [Reactor Netty Provider](/armor-reactor-netty) stands for all **Reactor Netty** based servers and frameworks support (like **Spring Boot Reactive**)

See [Netryx Armor](/armor) for all details.

### Netryx Memory
Included within Netryx Armor but also available separately, Netryx Memory is a Java-based library that manages sensitive data securely in memory using native interface:
- **Secure Memory Allocation and Deallocation**: Handle memory operations securely without your data being swapped to disk or leaked.
- **Data Obfuscation**: Read and write operations are obfuscated to enhance security.
- **Memory Protection**: Protect memory regions from unauthorized READ/WRITE/EXEC operations.

For further information, see [Netryx Memory](/memory).

### Netryx AntiBot
**Coming soon**

A dedicated AI module designed to protect your application against bot attacks, based
on Netryx Armor's fingerprinting modules.

# Education materials
Find articles here: [Educational Content](educational-content) 

# Contact
For security concerns or to discuss potential features that you'd prefer not to disclose publicly, please reach out to us at: `security@exploit.org`.

For general inquiries or to engage in discussions on various topics, join our Telegram channel and chat at: [@exploitorg](https://t.me/exploitorg).