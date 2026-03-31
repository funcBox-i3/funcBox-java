# FuncBox — Java Utility Library

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-11%2B-ED8B00.svg?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.1.0-blue.svg?logo=apachemaven)](https://search.maven.org/artifact/io.github.funcbox-i3/funcBox)

**FuncBox** is a lightweight Java utility library with minimal dependencies (including Jackson for JSON handling) that provides ready-to-use functions for mathematics, string operations, and graph algorithms — all in one clean package.

---

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
  - [Maven](#maven)
  - [Gradle (Groovy)](#gradle-groovy)
  - [Gradle (Kotlin DSL)](#gradle-kotlin-dsl)
  - [Manual JAR](#manual-jar)
- [Quick Start](#quick-start)
- [API Reference](#api-reference)
  - [Misc — Mathematics & String Utilities](#misc--mathematics--string-utilities)
    - [isPrime](#isprime)
    - [primes](#primes)
    - [fibonacci](#fibonacci)
    - [getFactors](#getfactors)
    - [isPalindrome](#ispalindrome)
    - [splitPrimeComposite](#splitprimecomposite)
  - [DataUtil — Safe Data Navigation](#datautil--safe-data-navigation)
    - [safeGet](#safeget)
  - [Dijkstra — Graph Algorithms](#dijkstra--graph-algorithms)
    - [dijkstra (single source)](#dijkstra-single-source)
    - [dijkstra (source to target)](#dijkstra-source-to-target)
- [Error Handling](#error-handling)
- [Contributing](#contributing)
- [License](#license)

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 11 or higher |
| Maven / Gradle | Any modern version |

---

## Installation

Add FuncBox to your project using your preferred build tool. The dependency lives on **Maven Central** so no extra repository configuration is required.

### Maven

Open your `pom.xml` and add the following inside the `<dependencies>` block:

```xml
<dependencies>
    <!-- FuncBox Utility Library -->
    <dependency>
        <groupId>io.github.funcbox-i3</groupId>
        <artifactId>funcBox</artifactId>
        <version>1.1.0</version>
    </dependency>
</dependencies>
```

After adding it, run:

```bash
mvn dependency:resolve
```

### Gradle (Groovy)

Open your `build.gradle` and add the dependency inside the `dependencies` block:

```groovy
dependencies {
    implementation 'io.github.funcbox-i3:funcBox:1.1.0'
}
```

After adding it, run:

```bash
./gradlew dependencies
```

### Gradle (Kotlin DSL)

Open your `build.gradle.kts` and add:

```kotlin
dependencies {
    implementation("io.github.funcbox-i3:funcBox:1.1.0")
}
```

### Manual JAR

If you are not using a build tool, download the JAR directly from [MVN Repository](https://mvnrepository.com/artifact/io.github.funcbox-i3/funcBox/1.1.0) and add it to your project's classpath.

---

## Quick Start

```java
import funcBox.Misc;
import funcBox.dijkstra.Dijkstra;
import funcBox.dijkstra.Result;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        // --- Math Utilities ---
        System.out.println(Misc.isPrime(17));              // true
        System.out.println(Misc.primes(2, 20));            // [2, 3, 5, 7, 11, 13, 17, 19]
        System.out.println(Misc.fibonacci(10));            // 55
        System.out.println(Misc.getFactors(12));           // [1, 2, 3, 4, 6]

        // --- String Utilities ---
        System.out.println(Misc.isPalindrome("Racecar")); // true

        // --- Split into primes and composites ---
        List<Integer> numbers = List.of(2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<List<Integer>> split = Misc.splitPrimeComposite(numbers);
        System.out.println(split.get(0)); // Primes:     [2, 3, 5, 7]
        System.out.println(split.get(1)); // Composites: [4, 6, 8, 9, 10]

        // --- Graph Shortest Path ---
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        graph.put("A", Map.of("B", 4, "C", 2));
        graph.put("B", Map.of("D", 5, "E", 1));
        graph.put("C", Map.of("B", 1, "E", 3));
        graph.put("D", Map.of("F", 2));
        graph.put("E", Map.of("D", 1, "F", 4));
        graph.put("F", Map.of());

        Result result = Dijkstra.dijkstra(graph, "A", "F");
        System.out.println(result.distances); // {A=0, C=2, B=3, E=4, D=5, F=7}
        System.out.println(result.paths);     // {F=[A, C, B, E, D, F], ...}
    }
}
```

---

## API Reference

All methods in FuncBox are **static**. You never need to create an instance — just call them directly on the class.

---

### Misc — Mathematics & String Utilities

**Import:**
```java
import funcBox.Misc;
```

---

#### isPrime

```java
boolean Misc.isPrime(int num)
```

Checks whether a given integer is a prime number.

**Parameters:**

| Parameter | Type  | Description              |
|-----------|-------|--------------------------|
| `num`     | `int` | The number to check.     |

**Returns:** `true` if the number is prime, `false` otherwise.

**Examples:**

```java
Misc.isPrime(2);   // true
Misc.isPrime(7);   // true
Misc.isPrime(1);   // false
Misc.isPrime(10);  // false
Misc.isPrime(97);  // true
```

---

#### primes

```java
List<Integer> Misc.primes(int start, int limit)
```

Generates all prime numbers in the range `[start, limit]` (both ends inclusive) using the **Sieve of Eratosthenes** algorithm.

**Parameters:**

| Parameter | Type  | Description                                 |
|-----------|-------|---------------------------------------------|
| `start`   | `int` | Lower bound of the range. Must be `≥ 2`.    |
| `limit`   | `int` | Upper bound of the range. Must be `≥ 2`.    |

**Returns:** `List<Integer>` containing all prime numbers in the given range, in ascending order.

**Throws:** `IllegalArgumentException` if either `start` or `limit` is less than `2`.

**Examples:**

```java
Misc.primes(2, 10);   // [2, 3, 5, 7]
Misc.primes(10, 30);  // [11, 13, 17, 19, 23, 29]
Misc.primes(2, 2);    // [2]
```

> ⚠️ **Note:** Both `start` and `limit` must be at least `2`. Passing values below `2` will throw an `IllegalArgumentException`.

---

#### fibonacci

```java
int Misc.fibonacci(int num)
```

Returns the Fibonacci number at the given index using an iterative approach. The sequence starts at index `0` with value `0`.

**Special return values:**

| Input condition | Returns |
|-----------------|---------|
| `num < 0` | `-1` (negative index is not valid) |
| Result exceeds `Integer.MAX_VALUE` | `-1` (overflow guard) |

**Fibonacci Sequence (reference):**
```
Index:  0  1  2  3  4  5  6   7   8   9   10
Value:  0  1  1  2  3  5  8  13  21  34   55
```

**Parameters:**

| Parameter | Type  | Description                          |
|-----------|-------|--------------------------------------|
| `num`     | `int` | The index in the Fibonacci sequence. |

**Returns:** `int` — the Fibonacci value at position `num`, or `-1` if `num` is negative or the result overflows `Integer.MAX_VALUE`.

**Examples:**

```java
Misc.fibonacci(0);   // 0
Misc.fibonacci(1);   // 1
Misc.fibonacci(5);   // 5
Misc.fibonacci(10);  // 55
Misc.fibonacci(46);  // 1836311903  (last valid int-range Fibonacci)
Misc.fibonacci(47);  // -1          (overflows Integer.MAX_VALUE)
Misc.fibonacci(-1);  // -1          (negative index)
```

---

#### getFactors

```java
String Misc.getFactors(int num)
```

Returns all factors of the given number, **excluding the number itself**. For prime numbers, only `[1]` is returned since primes have no other factors besides `1` and themselves.

**Parameters:**

| Parameter | Type  | Description                        |
|-----------|-------|------------------------------------|
| `num`     | `int` | The number to factorize.           |

**Returns:** `String` representation of the factor list (e.g., `[1, 2, 3, 4, 6]`).

**Examples:**

```java
Misc.getFactors(12);  // [1, 2, 3, 4, 6]
Misc.getFactors(28);  // [1, 2, 4, 7, 14]
Misc.getFactors(7);   // [1]        (7 is prime)
Misc.getFactors(1);   // [1]
Misc.getFactors(100); // [1, 2, 4, 5, 10, 20, 25, 50]
```

---

#### isPalindrome

```java
boolean Misc.isPalindrome(String val)
```

Checks whether a string reads the same forwards and backwards. The comparison is **case-insensitive**, so `"Racecar"` and `"racecar"` both return `true`.

**Parameters:**

| Parameter | Type     | Description              |
|-----------|----------|--------------------------|
| `val`     | `String` | The string to evaluate.  |

**Returns:** `true` if the string is a palindrome, `false` otherwise.

**Examples:**

```java
Misc.isPalindrome("madam");    // true
Misc.isPalindrome("Racecar");  // true  (case-insensitive)
Misc.isPalindrome("level");    // true
Misc.isPalindrome("Hello");    // false
Misc.isPalindrome("a");        // true  (single character)
Misc.isPalindrome("");         // false (empty string)
```

---

#### splitPrimeComposite

```java
List<List<Integer>> Misc.splitPrimeComposite(List<Integer> numbers)
```

Partitions a list of integers into two sublists — **primes** and **composites**. Numbers `≤ 1` are ignored and will not appear in either output list.

**Parameters:**

| Parameter | Type            | Description                         |
|-----------|-----------------|-------------------------------------|
| `numbers` | `List<Integer>` | The list of integers to partition.  |

**Returns:** A `List<List<Integer>>` with exactly two elements:

| Index | Contents    |
|-------|-------------|
| `0`   | Prime numbers from the input list    |
| `1`   | Composite numbers from the input list |

**Examples:**

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
List<List<Integer>> result = Misc.splitPrimeComposite(nums);

result.get(0);  // [2, 3, 5, 7]       — Primes
result.get(1);  // [4, 6, 8, 9, 10]   — Composites
// Note: 1 is excluded as it is neither prime nor composite

// Another example
List<Integer> mixed = List.of(13, 15, 17, 18, 19, 20, 23);
List<List<Integer>> split = Misc.splitPrimeComposite(mixed);

split.get(0);  // [13, 17, 19, 23]  — Primes
split.get(1);  // [15, 18, 20]      — Composites
```

---

### DataUtil — Safe Data Navigation

**Import:**
```java
import funcBox.DataUtil;
```

Safely traverses nested JSON strings or object graphs (maps, lists, arrays) without crashes. Guards against nulls, malformed JSON, missing keys, and out-of-bounds indexes.

---

#### safeGet

```java
Object DataUtil.safeGet(Object source, Object path)
Object DataUtil.safeGet(Object source, Object path, boolean returnLastSeen)
Object DataUtil.safeGet(Object source, Object path, Object defaultValue, boolean returnLastSeen)
```

**Path Format Guide:**

| Use Case | Path Syntax | Example |
|----------|------------|---------|
| **Access Map keys** (object properties) | `key.subkey.property` | `"user.profile.city"` |
| **Access List/Array elements** | Use **numeric index** for lists/arrays | `"employees.0.name"` (1st employee) |
| **Mix keys and indexes** | Alternate between names and numbers | `"data.0.items.2"` (1st item, 3rd sub-item) |

**Parameters:**

| Param | Type | Purpose |
|-------|------|---------|
| `source` | `String` or `Object` | JSON string or Map/List/array object |
| `path` | `String` | Dot-delimited path (e.g., `"a.b.0.c"`) |
| `defaultValue` | `Object` | Fallback when path fails; defaults to `null` |
| `returnLastSeen` | `boolean` | If `true`, return deepest valid node on failure |

**Quick Examples:**

```java
String json = """{"users": [{"name": "Alice", "age": 30}]}""";

// Key-based lookup
String name = (String) DataUtil.safeGet(json, "users.0.name");  // "Alice"

// Missing key — returns null
String missing = (String) DataUtil.safeGet(json, "users.0.email");  // null

// With default fallback
String city = (String) DataUtil.safeGet(json, "users.0.city", "Unknown");  // "Unknown"

// Last-seen node (get parent Map on failure)
Object lastNode = DataUtil.safeGet(json, "users.0.invalid", true);
// Returns the user Map {name: "Alice", age: 30}
```

**Path Rules:**
- 📌 **Names** = object properties (map keys) → use lowercase/camelCase
- 🔢 **Numbers** = array/list positions → use 0-based index (0, 1, 2, ...)
- ❌ Returns `defaultValue`/`null` if any key is missing or index out of bounds
- ✅ Safe with malformed JSON — never throws exceptions

---

### Dijkstra — Graph Algorithms

**Import:**
```java
import funcBox.dijkstra.Dijkstra;
import funcBox.dijkstra.Result;
```

The `Dijkstra` class implements **Dijkstra's shortest path algorithm** for weighted directed graphs represented as adjacency maps.

**Graph Format:**

The graph is represented as `Map<String, Map<String, Integer>>` where:
- The **outer key** is the name of a node.
- The **inner map** maps each **neighboring node** to its **edge weight** (must be a non-negative integer).

```java
Map<String, Map<String, Integer>> graph = new HashMap<>();
graph.put("A", Map.of("B", 4, "C", 2));  // A connects to B (cost 4) and C (cost 2)
graph.put("B", Map.of("D", 5));           // B connects to D (cost 5)
graph.put("C", Map.of("B", 1));           // C connects to B (cost 1)
graph.put("D", Map.of());                 // D has no outgoing edges
```

**The `Result` Object:**

| Field              | Type                       | Description                                      |
|--------------------|----------------------------|--------------------------------------------------|
| `result.distances` | `Map<String, Integer>`     | Shortest distance from `startNode` to each node |
| `result.paths`     | `Map<String, List<String>>`| Shortest path (as a list of nodes) to each node |

---

#### dijkstra (single source)

```java
Result Dijkstra.dijkstra(Map<String, Map<String, Integer>> graph, String startNode)
```

Finds the shortest paths from `startNode` to **all reachable nodes** in the graph. Returns paths for all nodes reachable from the start.

**Parameters:**

| Parameter   | Type                                  | Description                         |
|-------------|---------------------------------------|-------------------------------------|
| `graph`     | `Map<String, Map<String, Integer>>`   | The weighted directed graph.        |
| `startNode` | `String`                              | The node to start pathfinding from. |

**Returns:** A `Result` object containing distances and paths to every reachable node.

**Throws:** `IllegalArgumentException` if `startNode` does not exist in the graph.

**Example:**

```java
Map<String, Map<String, Integer>> graph = new HashMap<>();
graph.put("A", Map.of("B", 4, "C", 2));
graph.put("B", Map.of("D", 5, "E", 1));
graph.put("C", Map.of("B", 1, "E", 3));
graph.put("D", Map.of("F", 2));
graph.put("E", Map.of("D", 1, "F", 4));
graph.put("F", Map.of());

Result result = Dijkstra.dijkstra(graph, "A");

System.out.println(result.distances);
// {A=0, C=2, B=3, E=4, D=5, F=7}

System.out.println(result.paths);
// {A=[A], C=[A, C], B=[A, C, B], E=[A, C, B, E], D=[A, C, B, E, D], F=[A, C, B, E, D, F]}
```

---

#### dijkstra (source to target)

```java
Result Dijkstra.dijkstra(Map<String, Map<String, Integer>> graph, String startNode, String endNode)
```

Finds the shortest path from `startNode` to a **specific target node** (`endNode`). The algorithm stops and returns as soon as the target node is reached.

**Parameters:**

| Parameter   | Type                                  | Description                           |
|-------------|---------------------------------------|---------------------------------------|
| `graph`     | `Map<String, Map<String, Integer>>`   | The weighted directed graph.          |
| `startNode` | `String`                              | The node to start pathfinding from.   |
| `endNode`   | `String`                              | The target node to reach.             |

**Returns:** A `Result` object. The `distances` and `paths` maps will contain the entry for `endNode` with the shortest distance and the exact route taken.

**Throws:** `IllegalArgumentException` if either `startNode` or `endNode` does not exist in the graph.

**Example:**

```java
Map<String, Map<String, Integer>> graph = new HashMap<>();
graph.put("A", Map.of("B", 4, "C", 2));
graph.put("B", Map.of("D", 5, "E", 1));
graph.put("C", Map.of("B", 1, "E", 3));
graph.put("D", Map.of("F", 2));
graph.put("E", Map.of("D", 1, "F", 4));
graph.put("F", Map.of());

Result result = Dijkstra.dijkstra(graph, "A", "F");

System.out.println(result.distances);
// {A=0, C=2, B=3, E=4, D=5, F=7}

System.out.println(result.paths);
// {A=[A], C=[A, C], B=[A, C, B], E=[A, C, B, E], D=[A, C, B, E, D], F=[A, C, B, E, D, F]}

// Access just the result for the end node:
System.out.println(result.distances.get("F"));   // 7
System.out.println(result.paths.get("F"));        // [A, C, B, E, D, F]
```

> **Path explained:** A → C (cost 2) → B (cost +1=3) → E (cost +1=4) → D (cost +1=5) → F (cost +2=7). Total: **7**.

---

## Error Handling

FuncBox throws standard Java exceptions for invalid inputs. It is good practice to handle them explicitly.

| Method            | Exception                  | Cause                                              |
|-------------------|----------------------------|----------------------------------------------------|
| `primes()`        | `IllegalArgumentException` | `start` or `limit` is less than `2`                |
| `dijkstra()`      | `IllegalArgumentException` | `startNode` or `endNode` not found in graph        |

**Example with error handling:**

```java
try {
    List<Integer> result = Misc.primes(0, 50); // throws — 0 < 2
} catch (IllegalArgumentException e) {
    System.out.println("Invalid input: " + e.getMessage());
}

try {
    Result r = Dijkstra.dijkstra(graph, "Z", "F"); // throws — "Z" not in graph
} catch (IllegalArgumentException e) {
    System.out.println("Node not found: " + e.getMessage());
}
```

---

## Contributing

Contributions, bug reports, and feature requests are welcome!

1. **Fork** the repository
2. **Create** your feature branch: `git checkout -b feature/my-feature`
3. **Commit** your changes: `git commit -m 'Add my feature'`
4. **Push** to the branch: `git push origin feature/my-feature`
5. **Open a Pull Request** on GitHub

Please check the [issues page](https://github.com/funcBox-i3/funcBox-java/issues) for open tasks before starting.

---

## License

This project is licensed under the **MIT License** — see [LICENSE.txt](LICENSE.txt) for full details.

---

> **Disclaimer:** FuncBox is provided as-is for general utility use. The developer is not responsible for issues arising from misuse or improper integration of the library.

---

<p align="center">
  Made with ☕ · <a href="https://central.sonatype.com/artifact/io.github.funcbox-i3/funcBox">Maven Central</a> · <a href="https://github.com/funcBox-i3/funcBox-java/issues">Report an Issue</a>
</p>