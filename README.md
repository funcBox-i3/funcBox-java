# FuncBox Java Utility Library

**FuncBox** is a lightweight Java utility library with minimal dependencies that provides ready-to-use functions for mathematics, string operations, and graph algorithms — all in one clean package.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-11%2B-ED8B00.svg?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.1.0-blue.svg?logo=apachemaven)](https://search.maven.org/artifact/io.github.funcbox-i3/funcBox)


---

## Table of Contents

- [Install](#install)
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
    - [isAnagram](#isanagram)
    - [capitalizeEachWord](#capitalizeeachword)
    - [truncate](#truncate)
    - [clamp](#clamp)
    - [splitPrimeComposite](#splitprimecomposite)
  - [funcBox.dig — Safe JSON Navigation](#funcboxdig--safe-json-navigation)
    - [Dig](#dig)
    - [DigContext](#digcontext)
  - [Dijkstra — Graph Algorithms](#dijkstra--graph-algorithms)
    - [dijkstra (single source)](#dijkstra-single-source)
    - [dijkstra (source to target)](#dijkstra-source-to-target)
  - [Result](#result)
- [Error Handling](#error-handling)
- [Disclaimer](#disclaimer)
- [Contributing](#contributing)
- [License](#license)

---

## Install

### Requirements

| Requirement | Version |
|-------------|---------|
| Java | 11 or higher |
| Maven / Gradle | Any modern version |

### Installation

Add FuncBox to your project using your preferred build tool. The dependency lives on **Maven Central** so no extra repository configuration is required.

#### Maven

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

#### Gradle (Groovy)

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

#### Gradle (Kotlin DSL)

Open your `build.gradle.kts` and add:

```kotlin
dependencies {
    implementation("io.github.funcbox-i3:funcBox:1.1.0")
}
```

#### Manual JAR

If you are not using a build tool, download the JAR directly from [MVN Repository](https://mvnrepository.com/artifact/io.github.funcbox-i3/funcBox/1.1.0) and add it to your project's classpath.

---

## Quick Start

```java
import funcBox.Misc;
import funcBox.dig.Dig;
import funcBox.dig.DigContext;
import funcBox.dijkstra.Dijkstra;
import funcBox.dijkstra.Result;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        // Math Utilities
        System.out.println(Misc.isPrime(17));              // true
        System.out.println(Misc.primes(2, 20));            // [2, 3, 5, 7, 11, 13, 17, 19]
        System.out.println(Misc.fibonacci(10));            // 55
        System.out.println(Misc.getFactors(12));           // [1, 2, 3, 4, 6]

        // String Utilities
        System.out.println(Misc.isPalindrome("A man, a plan, a canal: Panama")); // true
        System.out.println(Misc.isAnagram("Listen", "Silent", false));          // true
        System.out.println(Misc.capitalizeEachWord("hello world"));              // Hello World
        System.out.println(Misc.truncate("abcdefghijklmnop", 10));               // abcdefghij
        System.out.println(Misc.clamp(150L, 0, 100));                            // 100

        // Split into primes and composites
        List<Integer> numbers = List.of(2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<List<Integer>> split = Misc.splitPrimeComposite(numbers);
        System.out.println(split.get(0)); // Primes:     [2, 3, 5, 7]
        System.out.println(split.get(1)); // Composites: [4, 6, 8, 9, 10]

        // Safe JSON Navigation
        String json = """
        {
          "user": {
            "name": "Zoro",
            "age": 19,
            "address": { "city": "London" }
          }
        }
        """;

        DigContext d = Dig.of(json);
        System.out.println(d.getString("user.name"));         // Zoro
        System.out.println(d.getInt("user.age"));             // 19
        System.out.println(d.scope("user.address").getString("city")); // London

        // Graph Shortest Path
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

#### **`isPrime`**

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

#### **`primes`**

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

#### **`fibonacci`**

```java
long Misc.fibonacci(int num)
```

Returns the Fibonacci number at the given index using an iterative approach.

**Valid index range:** `0..92`

**Parameters:**

| Parameter | Type  | Description                          |
|-----------|-------|--------------------------------------|
| `num`     | `int` | The index in the Fibonacci sequence. |

**Returns:** `long` — the Fibonacci value at position `num`.

**Throws:**

| Exception | Condition |
|-----------|-----------|
| `IllegalArgumentException` | `num < 0` |
| `ArithmeticException` | `num > 92` (result exceeds `long` capacity) |

**Examples:**

```java
Misc.fibonacci(0);   // 0
Misc.fibonacci(1);   // 1
Misc.fibonacci(10);  // 55
Misc.fibonacci(50);  // 12586269025
Misc.fibonacci(92);  // 7540113804746346429
```

---

#### **`getFactors`**

```java
String Misc.getFactors(int num)
```

Returns all factors of the given number, **including `1` but excluding the number itself**. For prime numbers, only `[1]` is returned since primes have no other factors besides `1` and themselves.

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

#### **`isPalindrome`**

```java
boolean Misc.isPalindrome(String val)
```

Checks whether a string is a valid palindrome using a two-pointer approach. A valid palindrome reads identically forward and backward, ignoring non-alphanumeric characters and treating uppercase and lowercase letters as equivalent.

**Parameters:**

| Parameter | Type     | Description              |
|-----------|----------|--------------------------|
| `val`     | `String` | The string to evaluate.  |

**Returns:** `true` if the string is a palindrome, `false` for null or non-palindromes.

**Examples:**

```java
Misc.isPalindrome("racecar");                           // true
Misc.isPalindrome("A man, a plan, a canal: Panama");   // true (spaces/punctuation ignored)
Misc.isPalindrome("hello");                            // false
Misc.isPalindrome("12321");                            // true
Misc.isPalindrome(null);                               // false
```

---

#### **`isAnagram`**

```java
boolean Misc.isAnagram(String str1, String str2, boolean caseSensitive)
```

Determines whether two strings are anagrams of each other. Two strings are anagrams if they contain the same characters with the same frequencies. Whitespace is **always ignored** during comparison. The `caseSensitive` flag controls whether casing matters.

**Parameters:**

| Parameter       | Type      | Description                                                                 |
|-----------------|-----------|-----------------------------------------------------------------------------|
| `str1`          | `String`  | The first string to compare.                                                |
| `str2`          | `String`  | The second string to compare.                                               |
| `caseSensitive` | `boolean` | `true` for case-sensitive; `false` for case-insensitive (treats `A` = `a`). |

**Returns:** `true` if the strings are anagrams, `false` otherwise (including `null` or empty inputs).

**Examples:**

```java
// Case-insensitive (default behavior)
Misc.isAnagram("Listen", "Silent", false);           // true
Misc.isAnagram("Triangle", "Integral", false);       // true
Misc.isAnagram("Hello", "World", false);             // false

// Case-sensitive
Misc.isAnagram("Listen", "silent", true);            // false  ('L' != 'l')
Misc.isAnagram("listen", "silent", true);            // true

// Whitespace is always ignored
Misc.isAnagram("a b c", "cba", false);               // true
Misc.isAnagram("rail safety", "fairy tales", false); // true

// Null / empty handling
Misc.isAnagram(null, "test", false);                 // false
Misc.isAnagram("", "", false);                       // false
```

---

#### **`splitPrimeComposite`**

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

#### **`capitalizeEachWord`**

```java
String Misc.capitalizeEachWord(String str)
```

Capitalizes the first character of each word in a string. Whitespace is normalized (multiple spaces collapsed to single spaces), and null/empty/whitespace-only input returns an empty string.

**Parameters:**

| Parameter | Type     | Description                                |
|-----------|----------|---------------------------------------------|
| `str`     | `String` | The input string to capitalize word-by-word |

**Returns:** The capitalized string, or empty string if input is null/empty/whitespace-only.

**Examples:**

```java
Misc.capitalizeEachWord("hello world");              // "Hello World"
Misc.capitalizeEachWord("a");                        // "A"
Misc.capitalizeEachWord("hello  WORLD");             // "Hello World"  (multiple spaces collapsed)
Misc.capitalizeEachWord("hello\tWORLD");             // "Hello World"  (tabs normalized)
Misc.capitalizeEachWord("123 abc");                  // "123 Abc"
Misc.capitalizeEachWord(null);                       // ""
Misc.capitalizeEachWord("   ");                      // ""  (whitespace-only)
```

---

#### **`truncate`**

```java
String Misc.truncate(String text, int maxLength)
```

Truncates a string to a maximum length. If the input length is less than or equal to `maxLength`, the original string is returned unchanged.

**Parameters:**

| Parameter   | Type     | Description                          |
|-------------|----------|--------------------------------------|
| `text`      | `String` | The input text, may be null          |
| `maxLength` | `int`    | The maximum allowed length           |

**Returns:** The truncated or original string; returns empty string for null input.

**Throws:** `IllegalArgumentException` if `maxLength < 0`.

**Examples:**

```java
Misc.truncate("abcdefghijklmnop", 10);   // "abcdefghij"
Misc.truncate("hello", 10);               // "hello"  (already shorter)
Misc.truncate(null, 5);                   // ""
Misc.truncate("test", -1);                // throws IllegalArgumentException
```

---

#### **`clamp`**

```java
int Misc.clamp(long value, int min, int max)
```

Clamps a value to the inclusive range `[min, max]`. If `value < min`, returns `min`. If `value > max`, returns `max`. Otherwise, returns the value casted to `int`.

**Parameters:**

| Parameter | Type  | Description                              |
|-----------|-------|------------------------------------------|
| `value`   | `long` | The value to clamp                       |
| `min`     | `int` | The minimum allowed value (inclusive)    |
| `max`     | `int` | The maximum allowed value (inclusive)    |

**Returns:** The clamped value as `int`.

**Throws:** `IllegalArgumentException` if `min > max`.

**Examples:**

```java
Misc.clamp(150L, 0, 100);   // 100  (clamped to max)
Misc.clamp(-10L, 0, 100);   // 0    (clamped to min)
Misc.clamp(50L, 0, 100);    // 50   (within range)
Misc.clamp(100, 100, 100);  // 100  (exact boundary)
```

---

**Import:**
```java
import funcBox.dig.Dig;
import funcBox.dig.DigContext;
```

Parse JSON once, then reuse a `DigContext` for repeated lookups over maps, lists, and arrays.

---

#### **`Dig`**

```java
DigContext Dig.of(String json)
DigContext Dig.of(Object source)
```

#### **`DigContext`**

```java
Object DigContext.get(Object path)
Object DigContext.get(Object path, Object defaultValue)
boolean DigContext.has(Object path)
DigContext DigContext.scope(Object path)
Map<String, Object> DigContext.getAll(String... paths)
String DigContext.getString(String path)
Integer DigContext.getInt(String path)
Long DigContext.getLong(String path)
Double DigContext.getDouble(String path)
Boolean DigContext.getBoolean(String path)
```

**Path Format Guide:**

| Use Case | Path Syntax | Example |
|----------|------------|---------|
| **Access Map keys** (object properties) | `key.subkey.property` | `"user.profile.city"` |
| **Access List/Array elements** | Use **numeric index** for lists/arrays | `"employees.0.name"` (1st employee) |
| **Mix keys and indexes** | Alternate between names and numbers | `"data.0.items.2"` (1st item, 3rd sub-item) |

**Quick Examples:**

```java
DigContext d = Dig.of(json);

String name = d.getString("users.0.name");
String city = (String) d.get("users.0.city", "Unknown");
DigContext user = d.scope("users.0");
Integer age = user.getInt("age");
```

**Path Rules:**
-  **Names** = object properties (map keys) → use lowercase/camelCase
-  **Numbers** = array/list positions → use 0-based index (0, 1, 2, ...)
-  Returns `defaultValue`/`null` if any key is missing or index is out of bounds
-  Safe with malformed JSON — never throws exceptions


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

#### **`dijkstra`** (single source)

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

#### **`dijkstra`** (source to target)

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

## Disclaimer

This library aims to provide stable APIs, but minor behavior adjustments may happen between releases. Review release notes before upgrading.

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

Made with care · [Maven Central](https://central.sonatype.com/artifact/io.github.funcbox-i3/funcBox) · [Report an Issue](https://github.com/funcBox-i3/funcBox-java/issues)
