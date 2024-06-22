# Netryx Memory

This library securely handles sensitive data in memory, offering tools to manage memory regions safely.
**Currently only Linux and Mac OS are supported**.

## Features
- **Secure Allocation:** Allocates memory, that is protected from being swapped to disk.
- **Read and Write Operations:** Safely read and write data to the secured memory region with built-in obfuscation.
- **Memory Protection:** Allows setting memory protection flags to control read, write, and execute permissions.
- **Obfuscation:** Data is can be obfuscated using XOR operations to increase security.
- **Resource Management:** Implements the `Closeable` interface for safe resource management, ensuring sensitive data is cleared from memory when no longer needed.

## Dependency
Please note, that if you use **Netryx Armor**, you don't need to specify this dependency explicitly.
### Gradle
```groovy
implementation "org.owasp.netryx:memory:1.0.0"
```
### Maven
```xml
<dependency>
    <groupId>org.owasp.netryx</groupId>
    <artifactId>memory</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Initialization
Create a new instance of `SecureMemory` by specifying the size of the memory region you want to allocate. The constructor ensures that the memory is allocated securely and locked immediately to prevent paging.

```java
SecureMemory secureMemory = new SecureMemory(1024); // Allocate 1024 bytes
```

### Writing Data
To write data into the secure memory region, use the `write` method. You can specify an offset if needed.

```java
byte[] data = "sensitive data".getBytes();
secureMemory.write(data);
```

### Reading Data
Data can be read from the memory region using the `read` method. You can specify the offset and length to read specific portions of the memory.

```java
byte[] readData = secureMemory.read(0, data.length);
```

### Memory Protection
You can set memory protection modes (e.g., read, write, execute permissions) using the `protect` method.
When setting up SecureMemory to protect your data, there's an essential step to ensure your memory size is aligned correctly. This means the size of the memory you're working with should be a multiple of the system's memory page size, ensuring it's "page-aligned."

```java
var secureMemory = new SecureMemory(n * MemoryUtil.pageSize()); //n >= 1
secureMemory.protect(Access.READ | Access.WRITE); // allow only READ and WRITE operations
```

### Obfuscation and Deobfuscation
Just call `obfuscate` and `deobfuscate` methods for these operations.

```java
secureMemory.obfuscate();
// Work with data
secureMemory.deobfuscate();
```

For scenarios requiring direct manipulation of deobfuscated data while ensuring that this sensitive information is securely handled and automatically cleared from memory afterwards, SecureMemory offers a specialized method:
```java
<T> T deobfuscate(int offset, int length, Function<byte[], T> function)
```

Example:
```java
String result = secureMemory.deobfuscate(0, data.length, bytes -> {
    // Convert bytes to String or another type T as required
    return new String(bytes);
});
```

### Closing and Cleaning Up
Once done, ensure to close the `SecureMemory` instance to clear sensitive data and unlock the memory.

```java
secureMemory.close();
```

## Note
Remember that handling sensitive data requires careful consideration of security practices beyond just memory management. Always use up-to-date security measures and review your code for potential vulnerabilities.

## Conclusion
SecureMemory provides a foundational layer for managing sensitive data in memory in Java applications. It offers a balance between usability and security, making it easier for developers to store sensitive data (like private keys) in cache.