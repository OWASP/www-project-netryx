---

layout: col-sidebar
title: OWASP Netryx
tags: security, java, netty, appsec
level: 2
type: code
pitch: Advanced Java Security Framework

---

# Hi! 👋
Welcome to Netryx _(pronounced 'netriks')_, advanced java security framework lead by [exploit.org](https://exploit.org) group.
It was created to help developers make their web applications more secure and effective against various threats.

### Netryx Modules
It is **modular** security framework that is designed to integrate to as many applications, as possible:

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

### WAF
**Secure By Default**

Advanced Web Application Firewall for Netty based servers, that uses Armor Pipeline's backend.

#### Brief overview:
- Passive Injection protection (SQL, XSS, LDAP, CMD, etc)
- Passive Path Traversal protection
- Malicious client detection using JA3, JA4, JA4H, and HTTP/2 fingeprints
- Flexible Rule Management System

### Machine Learning Core

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

### Memory
Netryx Memory manages sensitive data securely in memory using Java native interface for UNIX systems.

#### Brief overview:
- Unswappable memory allocation
- Memory obfuscation
- Memory regions with protection from unauthorized READ/WRITE/EXEC

### Events
Zero dependency event manager designed for building even-based applications. Used by Netryx WAF for Security Events management.

### Education materials
Find articles here: [OWASP Path To Secure Software series](https://dev.to/owasp/intro-to-application-security-3cj3)

### Contact
For security concerns or to discuss potential features that you'd prefer not to disclose publicly, please reach out to us at: `security@exploit.org`.

For general inquiries or to engage in discussions on various topics, join our Telegram channel and chat at: [@exploitorg](https://t.me/exploitorg).
### Road Map

Now - End of Year 1
Q1: Improve documentation and create training materials for developers on the main features of Netryx.
Q2: Adaptation of User-Side and Pipeline-Side functionality to meet user requirements and potentially new threats.
Q3-Q4: Extending Netryx support for additional Java frameworks, collecting feedback for further improvements.

Year 2
Q1: Designing AI driven WAF system following privacy and security requirements of processed data. Define possibilities of turning it into decentralized (p2p) view.
Q2-Q3: Development and testing of WAF system, collecting feedback from relevant stakeholders
Q4: First autonomous WAF system release

Year 3
Q1-Q2: Exploration of how Netryx can be adapted for other programming languages, such as Python, C#, JavaScript and Rust.
Q3-Q4: Develop Netryx prototypes for selected languages, begin testing with community participation.

Year 4
Q1-Q2: Launching versions of Netryx for new programming languages, organizing events to train developers.
Q3-Q4: Develop partnerships with academic and research organizations, strengthening interaction with the community to jointly develop new features and improvements.

Basic assumptions and conditions:

* Active community participation and openness to innovation are key to the success of the project.
* Staying alert to changes in cybersecurity and adapting to new threats.
* Collaboration with leading experts and organizations to ensure high quality and relevance of the solutions offered.
