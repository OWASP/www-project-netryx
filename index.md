---

layout: col-sidebar
title: OWASP Netryx
tags: example-tag
level: 2
type: code
pitch: A very brief, one-line description of your project

---

NetArmor - our answer to boosting the security of web apps built in Java. 
It’s straightforward, tackling both the user and server sides to block out cyber threats. 
This framework is all about making your app secure, without all the hassle. Let’s get into how NetArmor keeps your web applications safe:

NetArmor splits its functionality into two parts: User-Side and Pipeline-Side, each designed to target specific security needs of your web applications.

User-Side Features:

1. Input Validators
Improper input validation is the root of many security vulnerabilities, including XSS and SQL Injection. NetArmor includes configurable validators to check inputs such as emails, usernames, and credit card numbers, effectively preventing common entry points for attackers.

2. XSS Protection & HTML Sanitization
By leveraging OWASP's AntiSamy, we provide a powerful HTML encoder that can encode user input before displaying it on a webpage, thus sanitizing HTML from XSS attacks. This robust protection ensures that malicious scripts are not executed, keeping your application safe from cross-site scripting vulnerabilities.

3. Path Traversal Protection
Path traversal is a vulnerability that allows attackers to access files outside the web root directory. NetArmor's feature prevents unauthorized file access, securing your application's data from being compromised by ensuring that users can only access files within specified boundaries.

4. Password Hashing
A strong password hashing algorithm is crucial for protecting user passwords from brute-force attacks. NetArmor offers a selection of algorithms, emphasizing the importance of using slow hashing processes to deter cracking attempts. We recommend Argon2id, the winner of the Password Hashing Competition in July 2015, along with SCrypt and BCrypt, for their proven security and effectiveness in safeguarding passwords.

5. Secure Memory Allocation
NetArmor provides an advanced and secure method to handle sensitive data within your application's memory. 
This functionality ensures that critical information, such as passwords and tokens, is stored in a cache using a way that minimizes the risk of unauthorized access or leakage.
Memory is protected from being swapped on disk, can be obfuscated and if the size of memory is page-aligned you can manage access levels for reading, writing, and executing.

Pipeline-Side Features:

1. Reactive Protection from HTTP/2 0day RST Flood Attacks
This feature protects your website from flooding with RESET STREAM packets that take advantage of HTTP/2 weaknesses. Cloudflare has highlighted these issues, and our tool specifically targets and blocks these types of attacks to prevent your site from going down: [https://blog.cloudflare.com/zero-day-rapid-reset-http2-record-breaking-ddos-attack/|https://blog.cloudflare.com/zero-day-rapid-reset-http2-record-breaking-ddos-attack/]

2. JA3 Fingerprinting
JA3 fingerprinting identifies users based on how their devices start a secure connection. It looks at the unique "hello" messages these devices send, helping to distinguish between regular users and potential attackers by analyzing these packets.

3. Reactive Intrusion Detection Interface
Our intrusion detection gathers important data that includes:
1. JA3 fingerprint, 
2. HTTP/2 Fingerprint (according to Akamai specified format: [https://www.blackhat.com/docs/eu-17/materials/eu-17-Shuster-Passive-Fingerprinting-Of-HTTP2-Clients-wp.pdf|https://www.blackhat.com/docs/eu-17/materials/eu-17-Shuster-Passive-Fingerprinting-Of-HTTP2-Clients-wp.pdf])
3. Related HTTP Request
4. Related IP Address.

This information is used to detect and block malicious activities, acting as a comprehensive security system for your website.

4. Security Policy Management
Security Policy management refers to the implementation and enforcement of security policies like Content Security Policy (CSP), X-Frame-Options (XFO), and similar directives. These policies are crucial for defining how resources on a website can be loaded or interacted with, essentially setting the boundaries for what is considered secure behavior within a web application.

5. Reactive HTTP Request Rate Limiter
The rate limiter controls the number of requests a user can make to your site in a given timeframe, preventing overload and potential DoS attacks. This keeps your site stable and accessible by managing traffic flow effectively.

6. Reactive IP Whitelisting/Blacklisting
This feature allows you to quickly adjust who can access your site by enabling you to add or remove IP addresses from your access list in real time. If a user is trusted, you can whitelist them for uninterrupted access. Conversely, if an IP is deemed a threat, you can blacklist it to block access immediately.

Thus NetArmor is a powerful security solution, that can be used to protect your application from various attacks,
but although it can be used as a complete security solution, it is recommended to use it as a part of your security solution.
Remember, there is no 100% security. Security is a continuous process, that requires a lot of effort and our goal is to help you with it.

### Road Map
NetArmor Approximate Roadmap

Now - End of Year 1
Q1: Improve documentation and create training materials for developers on the main features of NetArmor.
Q2: Adaptation of User-Side and Pipeline-Side functionality to meet user requirements and potentially new threats.
Q3-Q4: Extending NetArmor support for additional Java frameworks, collecting feedback for further improvements.

Year 2
Q1: Designing AI driven IDS system following privacy and security requirements of processed data. Define possibilities of turning it into decentralized (p2p) view.
Q2-Q3: Development and testing of IDS system, collecting feedback from relevant stakeholders
Q4: First autonomous IDS system release

Year 3
Q1-Q2: Exploration of how NetArmor can be adapted for other programming languages, such as Python, C#, JavaScript and Rust.
Q3-Q4: Develop NetArmor prototypes for selected languages, begin testing with community participation.

Year 4
Q1-Q2: Launching versions of NetArmor for new programming languages, organizing events to train developers.
Q3-Q4: Develop partnerships with academic and research organizations, strengthening interaction with the community to jointly develop new features and improvements.

Basic assumptions and conditions:

* Active community participation and openness to innovation are key to the success of the project.
* Staying alert to changes in cybersecurity and adapting to new threats.
* Collaboration with leading experts and organizations to ensure high quality and relevance of the solutions offered.
