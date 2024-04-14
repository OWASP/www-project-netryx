# Beyond The Code: Exposing in Disguise
* Difficulty: **Medium**
* Target Audience: **Intermediate/Advanced users**

In the [Baisc Threats](basic_threats.md) article, we discussed basic threats in AppSec and concluded that many times more enhanced approaches should be applied for mitigation.

Just like in the previous article, in this one we will follow the principle of “explaining complex practices in simple words.” My goal is not to fill your head with a lot of theoretical terms, but to give competent and understandable explanations that a specialist of any level can understand (hopefully).

This part will discuss enhanced security techniques for detecting malicious traffic that can be used on application layer. We will describe such techniques as

- **JA3 Fingerprinting**
- **HTTP/2 Fingerprinting**

and how they play a big role in detecting

- **Malware based Session Hijacking**
- **Advanced Persistent Attacks**
- **DDoS Attacks**
- **Humanoid Bots**

In most of this tasks [**NetArmor**](../armor) will help us to get everything for mitigating this threats.

JA3 Fingerprinting
------------------

Nowadays TLS connection is not so much a recommendation as a mandatory measure in web application. Before we move to TLS (**JA3**) fingerprinting let's briefly elaborate on how the connection is created.

To establish TLS connection we should first do `Handshake` process, that can be illustrated in following way. Please note that it is short version of handshake without going deep in details:

#### Client Hello ****›****

At this step clients initiates a handshake with server. In this packet client includes information about:

- Maximum supported TLS version. As a fact, which one to use is agreed later in handshake.
- Unique random number that is a part of data used for generation session keys.
- Session ID, that was previously used in TLS 1.2 to continue session. Now PSK algorithms are used, but it still remains for backward compactibility.
- Supported Cipher Suites for encryption of connection
- Compression Methods - Always null (0) value, as TLS 1.3 doesn't support compression methods anymore. Included for backward compatibility.
- Extensions - Includes extensions information, most popular ones:   
  *****Server Name Indication***** ****-**** Information about hostname of target server  
  *****Elliptic Curves***** ****-**** Includes data about client supported curves algorithm, used in generating ephemeral keys.  
  *****Point Formats***** ****-**** Compression formats supported to receive curve points in.  
  *****Signature Algorithms***** ****-**** Which signature algorithms the client supports to verify Server's certificate signature furthermore.  
  *****Pre-Shared Key***** ****-**** PSK key to continue existing session  
  *****PSK Key Exchange Modes***** ****-**** What PSK Key exchange modes does client support.

#### ‹ Server Hello

"Answer" to client's hello message, that includes data about:

- Selected TLS protocol. The confirmation of TLS protocol is in `Supported Versions` extension.
- Server Random used later for ephemeral key generation
- Session ID. Same principle as with Client Hello, just legacy field for backward compatibility with TLS 1.2
- Compression methods - Always null (0). No compression methods used in TLS 1.3, so this field is also legacy.
- Selected Cipher Suite to use in encryption.
- Server Extensions - extensions used by the server. Most common ones:  
  *****Key Share***** - own public key for key exchange in TLS 1.3  
  *****Supported Versions***** - Agreed TLS version to use in this handshake.

#### ‹ ****Server Parameters &amp; Certificate****

In this section server sends the following packets:

*****Encrypted Extensions -***** contains additional parameters required for the session or application-specific data, that should be transferred during handshake securely. The message is encrypted by keys, negotiated at the `Server Hello` step.

*****Server Certificate***** - The server's certificate, contains a public key, hostname, and signature to ensure that the private key belongs to this host.  
Also, it allows the client to verify the server's identity using trusted root certificates, which really matters for mitigating MITM attacks.

*****Certificate Verify***** - Contains signature of all previous handshakes to verify ownership of the private key that belongs to the certificate.

#### ‹ ****Server Finished****

This message indicates that server has completed its handshake part. To prove that data is not tampered during the process it includes signature of all previous handshake packets signed with calculated epheremal private key. 

#### Client Finished ****›****

The client also calculates hash of all handshake messages and sends it to the server. After this, all communication between server and client is encrypted using calculated epheremal keys and TLS connection is established.

Going further, TLS fingerprinting is a technique that helps us to identitify a `TLS Client` which creates a secure connection with our server. It gives us an ability to create a unique "fingerprint" from the **Client Hello** packet, that we can use onwards to understand if the request is really sent by legitimate user or it is just spoofed data. It is also called as **JA3 Fingerprinting**.

**JA3 Fingerprint** consists of following parts:

- TLS Version
- Supported Cipher Suites
- Enabled Extensions
- Supported Elliptic Curves (Named Groups)
- Supported Point Formats

We can illustrate it in a string, where every part of fingerprint is separated by `,` and multiple elements in single part is connected by `-`. Such string is named `JA3 String`.

Let's check TLS Fingerprint of one of the clients used in stressor:

```
771,4866-4867-4865-49196-49200-159-52393-52392-52394-49195-49199-158-49188-49192-107-49187-49191-103-49162-49172-57-49161-49171-51-157-156-61-60-53-47-255,0-11-10-35-22-23-13-43-45-51-21,29-23-30-25-24,0-1-2
```

TLS FIngeprint of client used in Botnet

And TLS Fingerprint of Safari Browser:

```
771,4865-4866-4867-49196-49195-52393-49200-49199-52392-49162-49161-49172-49171,0-23-65281-10-11-16-5-13-18-51-45-43-27-21,29-23-24-25,0
```

Safari Browser's JA3 Fingerprint (macOS Ventura 13.4)

The best thing we can do for understanding is **comparing fingerprints** built from Client Hello packet. Let's look at them separately and then define core differences.

### Used in stressor client's JA3 Fingerprint

`771` - TLS Version. 771 belongs to TLS 1.2. But supported versions are specified in `SUPPORTED_VERSIONS` extension.

`4866-4867-4865-49196-49200-159-52393-52392-52394-49195-49199-158-49188-49192-107-49187-49191-103-49162-49172-57-49161-49171-51-157-156-61-60-53-47-255` - IDs of supported ciphersuites:

- ***`TLS_AES_256_GCM_SHA384`*** (ID: 4866)
- ***`TLS_CHACHA20_POLY1305_SHA256`*** (ID: 4867)
- ***`TLS_AES_128_GCM_SHA256`*** (ID: 4865)
- ***`TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384`*** (ID: 49196)
- ***`TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384`*** (ID: 49200)
- ***`TLS_DHE_RSA_WITH_AES_256_GCM_SHA384`*** (ID: 159)
- ***`TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256`*** (ID: 52393)
- ***`TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256`*** (ID: 52392)
- ***`TLS_DHE_RSA_WITH_CHACHA20_POLY1305_SHA256`*** (ID: 52394)
- ***`TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256`*** (ID: 49195)
- ***`TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256`*** (ID: 49199)
- ***`TLS_DHE_RSA_WITH_AES_128_GCM_SHA256`*** (ID: 158)
- ***`TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384`*** (ID: 49188)
- ***`TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384`*** (ID: 49192)
- ***`TLS_DHE_RSA_WITH_AES_256_CBC_SHA256`*** (ID: 107)
- ***`TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256`*** (ID: 49187)
- ***`TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256`*** (ID: 49191)
- ***`TLS_DHE_RSA_WITH_AES_128_CBC_SHA256`*** (ID: 103)
- ***`TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA`*** (ID: 49162)
- ***`TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA`*** (ID: 49172)
- ***`TLS_DHE_RSA_WITH_AES_256_CBC_SHA`*** (ID: 57)
- ***`TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA`*** (ID: 49161)
- ***`TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA`*** (ID: 49171)
- ***`TLS_DHE_RSA_WITH_AES_128_CBC_SHA`*** (ID: 51)
- ***`TLS_RSA_WITH_AES_256_GCM_SHA384`*** (ID: 157)
- ***`TLS_RSA_WITH_AES_128_GCM_SHA256`*** (ID: 156)
- ***`TLS_RSA_WITH_AES_256_CBC_SHA256`*** (ID: 61)
- ***`TLS_RSA_WITH_AES_128_CBC_SHA256`*** (ID: 60)
- ***`TLS_RSA_WITH_AES_256_CBC_SHA`*** (ID: 53)
- ***`TLS_RSA_WITH_AES_128_CBC_SHA`*** (ID: 47)
- ***`TLS_EMPTY_RENEGOTIATION_INFO_SCSV`*** (ID: 255)

`0-11-10-35-22-23-13-43-45-51-21` - IDs of enabled extensions:

- ***`SERVER_NAME`*** (ID: 0)
- ***`EC_POINT_FORMATS`*** (ID: 11)
- ***`SUPPORTED_GROUPS`*** (ID: 10)
- ***`SESSION_TICKET`*** (D: 35)
- ***`ENCRYPT_THEN_MAC`*** (ID: 22)
- ***`EXTENDED_MASTER_SECRET`*** (ID: 23)
- ***`SIGNATURE_ALGORITHMS`*** (ID: 13)
- ***`SUPPORTED_VERSIONS`*** (ID: 43)
- ***`PSK_KEY_EXCHANGE_MODES`*** (ID: 45)
- ***`KEY_SHARE`*** (ID: 51)
- ***`PADDING`*** (ID: 21)

`29-23-30-25-24` - Supported Elliptic Curves:

- ***`X25519`*** (ID: 29)
- ***`SECP256R1`*** (ID: 23)
- ***`X448`*** (ID: 30)
- ***`SECP521R1`*** (ID: 25)
- ***`SECP384R1`*** (ID: 24)

`0-1-2` - Supported Point Formats:

- ***`UNCOMPRESSED`*** (ID: 0)
- ***`ANSIX962_COMPRESSED_PRIME`*** (ID: 1)
- ***`ANSIX962_COMPRESSED_CHAR2`*** (ID: 2)

### Safari's JA3 Fingerprint

`771` - TLS Version. 771 belongs to TLS 1.2. It was actually TLS 1.3 handshake furthermore, supported versions are selected in `SUPPORTED_VERSIONS` extension.

`4865-4866-4867-49196-49195-52393-49200-49199-52392-49162-49161-49172-49171` - List of supported Cipher Suites. If we check ids we will see, that Safari prefers:

- ***`TLS_AES_128_GCM_SHA256`*** (ID: 4865)
- ***`TLS_AES_256_GCM_SHA384`*** (ID: 4866)
- ***`TLS_CHACHA20_POLY1305_SHA256`*** (ID: 4867)
- ***`TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384`*** (ID: 49196)
- ***`TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256`*** (ID: 49195)
- ***`TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256`*** (ID: 52393)
- ***`TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384`*** (ID: 49200)
- ***`TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256`*** (ID: 49199)
- ***`TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256`*** (ID: 52392)
- ***`TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA`*** (ID: 49162)
- ***`TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA`*** (ID: 49161)
- ***`TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA`*** (ID: 49172)
- ***`TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA`*** (ID: 49171)

`0-23-65281-10-11-16-5-13-18-51-45-43-27-21` - IDs of enabled extensions:

- ***`SERVER_NAME`*** (ID: 0)
- ***`EXTENDED_MASTER_SECRET`*** (ID: 23)
- ***`RENEGOTIATION_INFO`*** (ID: 65281)
- ***`SUPPORTED_GROUPS`*** (ID: 10)
- ***`EC_POINT_FORMATS`*** (ID: 11)
- ***`APPLICATION_LAYER_PROTOCOL_NEGOTIATION`*** (ID: 16)
- ***`STATUS_REQUEST`*** (ID: 5)
- ***`SIGNATURE_ALGORITHMS`*** (ID: 13)
- ***`SIGNED_CERTIFICATE_TIMESTAMP`*** (ID: 18)
- ***`KEY_SHARE`*** (ID: 51)
- ***`PSK_KEY_EXCHANGE_MODES`*** (ID: 45)
- ***`SUPPORTED_VERSIONS`*** (ID: 43)
- ***`COMPRESS_CERTIFICATE`*** (ID: 27)
- ***`PADDING`*** (ID: 21)

`29-23-24-25` - Supported Elliptic Curves IDs:

- ***`X25519`*** (ID: 29)
- ***`SECP256R1`*** (ID: 23)
- ***`SECP384R1`*** (ID: 24)
- ***`SECP521R1`*** (ID: 25)

`0` - EC Point Formats

- ***`UNCOMPRESSED`*** (ID: 0)

The value of JA3 String depends firstly on **TLS Engine** which is used under the hood. As an example:

**Chrome** uses `BoringSSL` engine.  
**Safari** uses `Secure Transport` (part of Security Framework).  
**Firefox** uses `NSS (Network Security Services)`.

To detect `Malware` acting as a user or `Humanoid Bots` that spoofed every data used in requests (like User Agents and etc), you should first take attention at values in every JA3 related parts, **order of their preference** and Browser TLS Engine features that usually are not included in engines of programming languages.  
For instance, in Safari's and Chrome's TLS Engines [GREASE](https://datatracker.ietf.org/doc/rfc8701/) is included to most parts of Client Hello packet. Although GREASE is not part of JA3 String, but you can extract it directly from packet.

### Comparing JA3s: Devil is in the details


#### TLS Version

Both Safari and bot specified same TLS Version.

#### Supported CipherSuites

Let's divide the list by `Common Ciphersuites` and `Uncommon Ciphersuites` and analyze their positions.

**Common:**  
`TLS_AES_128_GCM_SHA256`: position in bot's client **3**, in Safari's **1**  
`TLS_AES_256_GCM_SHA384`: position in bot's client **1**, in Safari's **2**  
`TLS_CHACHA20_POLY1305_SHA256`: position in bot's client **2**, in Safari's **3**  
`TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384`: position in bot's client **4**, in Safari's **5**  
`TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256`: position in bot's client **10**, in Safari's **5**  
`TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256`: position in bot's client **7**, in Safari's **6**  
`TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384`: position in bot's client **5**, in Safari's **7**  
`TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256`: position in bot's client **11**, in Safari's **8**  
`TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256`: position in bot's client **8**, in Safari's **9**  
`TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA`: position in bot's client **19**, in Safari's **10**  
`TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA`: position in bot's client **22**, in Safari's **11**  
`TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA`: position in bot's client **20**, in Safari's **12**  
`TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA`: position in bot's client **23**, in Safari's **13**

**Botnet client only:**

- `TLS_DHE_RSA_WITH_AES_256_GCM_SHA384`
- `TLS_DHE_RSA_WITH_CHACHA20_POLY1305_SHA25`
- `TLS_DHE_RSA_WITH_AES_128_GCM_SHA256`
- `TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384`
- `TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384`
- `TLS_DHE_RSA_WITH_AES_256_CBC_SHA256`
- `TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256`
- `TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256`
- `TLS_DHE_RSA_WITH_AES_128_CBC_SHA256`
- `TLS_DHE_RSA_WITH_AES_256_CBC_SHA`
- `TLS_DHE_RSA_WITH_AES_128_CBC_SHA`
- `TLS_RSA_WITH_AES_256_GCM_SHA384`
- `TLS_RSA_WITH_AES_128_GCM_SHA256`
- `TLS_RSA_WITH_AES_256_CBC_SHA256`
- `TLS_RSA_WITH_AES_128_CBC_SHA256`
- `TLS_RSA_WITH_AES_256_CBC_SHA`
- `TLS_RSA_WITH_AES_128_CBC_SHA`
- `TLS_EMPTY_RENEGOTIATION_INFO_SCSV`

**Safari only:**  
Botnet's client includes all Safari's ciphers.

In cipher suite comparison we can see, that Safari doesn't support anymore ciphers based on:

- **DHE** exchange, **RSA** signature and **AES/GCM** encryption algorithms
- **DHE** exchange, **RSA** signature and **AES/CBC** encryption algorithms
- **DHE** exchange, **RSA** signature and **ChaCha20-Poly1305** encryption algorithms
- **ECDHE** exchange, **ECDSA** signature and **AES/CBC** encryption algorithms
- **ECDHE** exchange, **RSA** signature and **AES/CBC** encryption algorithms
- **RSA** exchange and **AES/CBC** encryption algorithms

### Extensions

We will follow the same principle as with cipher suites. Dividing by common with positions and uncommon and

**Common:**

- `SERVER_NAME`: position in bot's client **1**, in Safari's **1**
- `EC_POINT_FORMATS`: position in bot's client **2**, in Safari's **5**
- `SUPPORTED_GROUPS`: position in bot's client **3**, in Safari's **4**
- `EXTENDED_MASTER_SECRET`: position in bot's client **6**, in Safari's **2**
- `SIGNATURE_ALGORITHMS`: position in bot's client **7**, in Safari's **8**
- `SUPPORTED_VERSIONS`: position in bot's client **8**, in Safari's **12**
- `PSK_KEY_EXCHANGE_MODES`: position in bot's client **9**, in Safari's **11**
- `KEY_SHARE`: position in bot's client **10**, in Safari's **10**
- `PADDING`: position in bot's client **11**, in Safari's **14**

**Safari only:**

- `RENEGOTIATION_INFO`
- `APPLICATION_LAYER_PROTOCOL_NEGOTIATION`
- `STATUS_REQUEST`
- `SIGNED_CERTIFICATE_TIMESTAMP`
- `COMPRESS_CERTIFICATE`

**Botnet Client Only**:

- `SESSION_TICKET`
- `ENCRYPT_THEN_MAC`

Concluding the information given above, we can see, that Safari doesn't include **Session Ticket** and **Encrypt Then Mac** extension and most common extensions are in different orders.

### Elliptic Curves:

Common curves are:

- `X25519`: position in bot's client: **1**, in Safari: **1**
- `SECP256R1`: position in bot's client: **2**, in Safari: **2**
- `SECP521R1`: position in bot's client: **4**, in Safari: **4**
- `SECP384R1`: position in bot's client: **5**, in Safari: **3**

The only difference is in order of `SECP521R1` and `SECP384R1` order in clients. Also Safari doesn't support `X448` curve, while it was specified in bot's client.

### Compression Methods

While Safari doesn't support compression for packets sent by TLS Connection (by specifiying only `UNCOMPRESSED` id, bot's client also included `ANSIX962_COMPRESSED_PRIME` and `ANSIX962_COMPRESSED_CHAR2` methods.

---

For convenience and privacy purposes we usually handle with not JA3 Strings, but **JA3 Hash**. Its calculation is simple and straightforward:

```pseudocode
ja3_hash = hex(md5(ja3_string))
```

You can use this hash furthermore in order to check in public JA3 databases like [ja3.zone](https://ja3.zone). Let's calculate JA3 Hash of the client used in stressor and check it:

```pseudocode
ja3_string = "771,4866-4867-4865-49196-49200-159-52393-52392-52394-49195-49199-158-49188-49192-107-49187-49191-103-49162-49172-57-49161-49171-51-157-156-61-60-53-47-255,0-11-10-35-22-23-13-43-45-51-21,29-23-30-25-24,0-1-2"

ja3_hash = hex(md5(ja3_string))
# JA3 Hash: c199b43d41b470f8f68c5561f8f1ce3e

```

Checking this hash in [ja3.zone](https://ja3.zone) results in following:

![](__GHOST_URL__/content/images/2024/02/image.png)ja3.zone hash checkSimply talking it means that stressors' clients are simple Python HTTP Clients.

Thus, even if in the requests to our server the client specified Safari's user-agents, we can easily detect, that it wasn't Safari at all. This method needs accuracy and good amount of data for handling false positives, but in return you get the ability to detect malicious requests using a method that is difficult to spoof.

You can use [NetArmor](../armor) to fingerprint connections and analyze `ClientHello` packets:

```java
public class MyTlsFingerprinter implements FingerprintPacketHandler {
    @Override
    public Mono handle(ChannelHandlerContext ctx, ClientHello ch) {
        var ja3 = ch.ja3();
        
        var hash = ja3.md5(); // md5 hash of ja3 fingerprint
        var raw = ja3.value(); // raw ja3 fingerprint
        
        // Do something with ja3
        // for example, check if it is in the blacklist
        // P.S isBlacklisted should be implemented by you
        
        if (isBlacklisted(ja3))
            return Mono.just(ResultCode.BLOCK);
        
        return Mono.just(ResultCode.OK);
    }
    
    @Override
    public String name() {
        return "my-tls-fingerprinter"; // Unique name used in the pipeline
    }
}
```

### HTTP/2 Fingerprinting

It is another powerful fingerprinting technique that can be used to detect spoofed client.  
Before we move to fingerprinting, let's elaborate on how HTTP/2 Connection is built. Our goal is to understand which parts of connection we can fingerprint, not to describe whole process:

1. Client sends `CONNECTION PREFACE` to acknowledge a server, that HTTP/2 connection is built and a magic string.

2. Client sends `HTTP/2 Settings Frame` that usually contains:  
   **SETTINGS\_HEADER\_TABLE\_SIZE (0x1)** - Defines max size of HPACK compressed headers table

   **SETTINGS\_ENABLE\_PUSH (0x2)** - Defines if push algorithm is allowed (1 for allow, 0 to disallow)

   **SETTINGS\_MAX\_CONCURRENT\_STREAMS (0x3)** - Maximum amount of concurrently opened streams (1 request per stream).

   **SETTINGS\_INITIAL\_WINDOW\_SIZE (0x4)** - Initial per stream window size for accepting data

   **SETTINGS\_MAX\_FRAME\_SIZE (0x5)** - Max frame size that server can accept.

   **SETTINGS\_MAX\_HEADER\_LIST\_SIZE (0x6)** - Maximum allowed size of header list for request or response.
3. Server sends own SETTINGS frame.
4. After client and server exchanged SETTINGS frame, they can begin the requests.

Each HTTP request in HTTP/2 connection contains from:

- **HEADERS** frame:  
  Contains request headers and **pseudo** headers.  
  Pseudo headers for HTTP request are:

  - `:method` - HTTP method used (GET, POST, PUT and etc)  
  - `:path` - HTTP request path including query string  
  - `:authority` - Same as `Host` header in HTTP/1.1 request  
  - `:scheme` - Scheme used, like `http` or `https`
- **DATA** frame *(optional)*

When defined in settings `INITIAL_WINDOW_SIZE` is not enough, client sends to a server `WINDOW_UPDATE` frame with additional fixed value of amount of data that he can accept. This process is called **flow control**.

If a client wants to specify the priority for specific requests, that would be processed by server, he sends `PRIORITY` frame, that consists of:

- `Stream Identifier` - ID of stream to set priority
- `Exclusive Flag` - boolean flag, that specifies if this stream should be the only with such priority
- `Stream Dependency` - Stream ID on which the priority of the current one depends.
- `Weight` - Weight of a stream, a value from 1 to 256 that indicates the relative priority of a thread within its dependency level. Bigger value - bigger priority.

After we have described usual processes appearing in HTTP/2, we can select following aspects for our fingerprinting techniques:

- Defined settings from `SETTINGS` frame, that can vary from client to client.
- Value from `WINDOW_UPDATE` frame that a client selects for increasing acceptable amount of data within 1 stream, if present.
- Streams for which the client wanted to change the priority in `PRIORITY` frames.
- Order of `pseudo` headers included in `HEADERS` frame.

It is everything we can collect from server's side about HTTP/2 connection for identifiying clients. Exactly these values were selected in HTTP/2 fingerprinting format specified in [Passive Fingerprinting of HTTP/2 Clients](https://www.blackhat.com/docs/eu-17/materials/eu-17-Shuster-Passive-Fingerprinting-Of-HTTP2-Clients-wp.pdf) by Akamai.

According to the format, HTTP/2 fingerprint is suggested to view like:  
`S[key:value]|W|P[streamId:exlusiveBit:dependentStreamId:weight]|H(m, a, s, p)`, where

- **S\[key:value;\]** stands for defined client settings separated by `;` symbol.
- **W** stands for window update frame's value.
- **P\[streamId:exlusiveBit:dependentStreamId:weight,\]** is list of priority streams, separated by `,`.
- **H(m, a, s, p)** stands for order of pseudo headers (:method, :authority, :scheme, :path).

Let's compare `Safari`'s, `Chrome`'s and Python `HTTPX` clients' fingerprints:

#### Safari

User-Agent: `Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.5 Safari/605.1.15`  
Fingerprint: `3:100;4:4194304|4194304|0|m,s,p,a`

****HTTP/2 Settings****  
`SETTINGS_MAX_CONCURRENT_STREAMS` (ID: 3) = 100  
`SETTINGS_INITIAL_WINDOW_SIZE` (ID: 4) = 4194304

****Window Update Value**** = 4194304¹

****Priority streams****: Not specified¹

****HTTP/2 Pseudo Header Order****  
`:method`  
`:scheme`  
`:protocol`  
`:authority`

¹ - Value may be present or not under different circumstances.

#### Chrome

User-Agent: `Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36`  
Fingerprint: `1:65536;2:0;4:6291456;6:262144|3149930|0|m,a,s,p`

****HTTP/2 Settings****  
`SETTINGS_HEADER_TABLE_SIZE` (ID: 1) = 65536  
`SETTINGS_ENABLE_PUSH` (ID: 2) = 0 (means false)  
`SETTINGS_INITIAL_WINDOW_SIZE` (ID: 4) = 6291456  
`SETTINGS_MAX_HEADER_LIST_SIZE` (ID: 6) = 262144

****Window Update Value**** = 3149930¹

****Priority streams****: Not specified¹

****HTTP/2 Pseudo Header Order****  
`:method`  
`:authority`  
`:scheme`  
`:protocol`

¹ - Value may be present or not under different circumstances.

#### HTTPX

User-Agent: `python-httpx/0.27.0`  
Fingerprint: `1:4096;2:0;3:100;4:65535;5:16384;6:65536|16777216|0|m,a,s,p`

****HTTP/2 Settings****  
`SETTINGS_HEADER_TABLE_SIZE` (ID: 1) = 65536  
`SETTINGS_ENABLE_PUSH` (ID: 2) = 0 (means false)  
`SETTINGS_MAX_CONCURRENT_STREAMS` (ID: 3) = 100  
`SETTINGS_INITIAL_WINDOW_SIZE` (ID: 4) = 65535  
`SETTINGS_MAX_FRAME_SIZE` (ID: 5) = 16384  
`SETTINGS_MAX_HEADER_LIST_SIZE` (ID: 6) = 65536

****Window Update Value**** = 16777216¹

****Priority streams****: Not specified¹

****HTTP/2 Pseudo Header Order****  
`:method`  
`:authority`  
`:scheme`  
`:protocol`

¹ - Value may be present or not under different circumstances.

Now let's do small comparison between 2 pairs: Safari-HTTPX and Chrome-HTTPX:

### Safari vs HTTPX

- **Common**:  
  - Both defined `SETTINGS_MAX_CONCURRENT_STREAMS` with value 100
- **Differences**:  
  - Safari didn't specify `SETTINGS_HEADER_TABLE_SIZE`, while HTTPX defined it with value **65536**  
  - `SETTINGS_INITIAL_WINDOW_SIZE` in Safari is defined by value **4194304**, while HTTPX specified **65535**  
  - Safari didn't specify `SETTINGS_ENABLE_PUSH` and `SETTINGS_MAX_FRAME_SIZE`, while HTTPX set them with values 0 and **16384** correspondigly.  
  - `SETTINGS_MAX_HEADER_LIST_SIZE` isn't specified in Safari, while HTTPX defined **65536**  
  - Safari defined **3149930** window update value, while HTTPX **16777216**  
  - Different header orders. Safari's is `m,s,p,a`, while HTTPX's one is `m,a,s,p`

### Chrome vs HTTPX

- **Common**:  
  - Both set `SETTINGS_ENABLE_PUSH` as 0  
  - Both defined `SETTINGS_HEADER_TABLE_SIZE` as **65536**  
  - Both have same header order: `m,a,s,p`
- **Differences**:  
  - Chrome didn't specify `SETTINGS_MAX_CONCURRENT_STREAMS` and `SETTINGS_MAX_FRAME_SIZE`, while HTTPX did with values **100** and **16384** correspondigly  
  - Chrome has bigger `SETTINGS_INITIAL_WINDOW_SIZE` value: **6291456**, while HTTPX defined **65535**  
  - Chrome and HTTPX have different `SETTINGS_MAX_HEADER_LIST_SIZE`: **262144** and **65536** correspondigly.  
  - Window update value for Chrome is **3149930**, while for HTTPX: **16777216**

Thus, we see that actually we can find enough differences in browsers' and potentially malicious clients, that just call themselves as a browser by specifiying User-Agent.

Power is in an integrated approach
----------------------------------

When we try to detect a malicious client, just one of described approaches (JA3 and HTTP/2 fingerprinting) may not be enough.  
We should understand that accuracy really matters. Not only you need enough of data (like legitimate fingerprints), but also to use both of described approaches to detect bad guys and enhance accuracy with quality of detection.

Using JA3 Fingerprinting and HTTP/2 Fingerprinting together gives you much more detailed picture of network. It becomes much more complicated for potential abuser to spoof both of them in ideal way. If applicable, use additional techniques (like browser fingerprinting and JS based anti-bot checks) to make detection even more precise.

Check a short list of what can you do having this data:

- Identify **inconsistencies** between fingerprints and HTTP request and gather related list of IP addresses that may act as a coordinated **APT** campaign.
- Correlate your fingerprints with known **indicators of compromise** as a part of **APT** detection.
- Detect sharp increase of traffic and find common patterns in fingerprints to mitigate **DDoS** attack.
- Compare your data with existing databases of botnets and malware fingerprints to detect malware acting from the side of user.

For instance, [NetArmor](../armor) allows you to analyze every request with related fingerprints in a reactive way, by implementing `IntrusionDetector` interface:

```java
public class MyIntrusionDetector implements IntrusionDetector {
    @Override
    public Mono detect(IntrusionDetectionData data) {
        var clientHello = data.getClientHello(); // Client hello packet, if TLS is used
        var ja3 = clientHello.ja3(); // Get Ja3 fingerprint
        
        var encoded = ja3.md5(); // md5 hash of ja3 fingerprint
        var raw = ja3.value(); // raw ja3 fingerprint
        
        var request = data.getRequest(); // The request that is being checked
        var ip = data.getRemoteAddress(); // IP address of the client
        
        var http2Fingerprint = data.getHttp2Fingerprint();
        var formattedHttp2Fingerprint = http2Fingerprint.toString(); // Akamai suggested format
        // P.S you can collect data in case you want to train own model in future
        // or even to contribute the dataset to us.
        // Everything depends on privacy policy of your application, if it is allowed.
        
        // DetectCode can be OK, SUSPICIOUS or MALICIOUS
        return Mono.just(new DetectionResult(DetectCode.OK, data, "User is OK"));
    }

    // Will be called if the request is detected as malicious or suspicious
    @Override
    public Mono onDetected(DetectionResult result) {
        // Do something with the result
        // for example, block the user or recheck the request
        // P.S you can use result.data() to get the data, that was passed to detect method
        
        // HandleCode can be PROCEED or BLOCK
        return Mono.just(HandleCode.BLOCK);
    }
    
    @Override
    public String name() {
        return "my-intrusion-handler"; // Unique name used in the pipeline
    }
}
```

### Yes, it is not so easy

But our goal is to make security available to everyone. We do everything to develop AI based IDS for making most of described tasks instead of you and make your life easier.

Conclusion
----------

In this article we have discussed JA3 and HTTP/2 fingerprinting techniques.  
We learned how to use these techniques to detect malicious actors and tried to describe it all in a simple manner for specialists of almost any level.

Worth to Read
----------
- [JA4+ Fingerprinting](https://blog.foxio.io/ja4%2B-network-fingerprinting) - Article regarding fresh JA4+ fingerprinting algorithms by FoxIO.

*This article was dublicated to GitHub from our blog: https://blog.exploit.org/*