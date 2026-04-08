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
- [Quick Start](#quick-start)
- [API Reference](#api-reference)
  - [Misc — Mathematics & String Utilities](#misc--mathematics--string-utilities)
  - [funcBox.io — File & Resource Utilities](#funcboxio--file--resource-utilities)
  - [funcBox.dig — Safe JSON Navigation](#funcboxdig--safe-json-navigation)
  - [funcBox.http — Simplified Web Client](#funcboxhttp--simplified-web-client)
  - [Dijkstra — Graph Algorithms](#dijkstra--graph-algorithms)
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
        System.out.println(Misc.levenshteinDistance("hello", "hallo"));          // 1
        System.out.println(Misc.fuzzyMatchScore("hello", "hallo"));              // 0.8

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

**Available Functions:**

| Function | Description |
|----------|-------------|
| [`isPrime(int num)`](#isprime) | Check if a number is prime |
| [`primes(int start, int limit)`](#primes) | Generate all primes in a range using Sieve of Eratosthenes |
| [`fibonacci(int num)`](#fibonacci) | Get Fibonacci number at index (0-92) |
| [`getFactors(int num)`](#getfactors) | Get all factors of a number |
| [`isPalindrome(String val)`](#ispalindrome) | Check if string is a palindrome (ignores punctuation/case) |
| [`isAnagram(String str1, String str2, boolean caseSensitive)`](#isanagram) | Check if two strings are anagrams |
| [`capitalizeEachWord(String str)`](#capitalizeeachword) | Capitalize first letter of each word |
| [`truncate(String text, int maxLength)`](#truncate) | Truncate string to maximum length |
| [`clamp(long value, int min, int max)`](#clamp) | Clamp value to min/max range |
| [`splitPrimeComposite(List<Integer> numbers)`](#splitprimecomposite) | Split list into primes and composites |
| [`levenshteinDistance(String str1, String str2)`](#levenshteindistance) | Calculate minimum edit distance between two strings |
| [`levenshteinDistance(String target, String[] candidates)`](#levenshteindistance-array) | Calculate edit distances from target to multiple candidates |
| [`fuzzyMatchScore(String str1, String str2)`](#fuzzymatchscore) | Calculate normalized similarity score (0.0 to 1.0) |
| [`fuzzyMatchScore(String target, String[] candidates)`](#fuzzymatchscore-array) | Calculate similarity scores from target to multiple candidates |

---

### funcBox.io — File & Resource Utilities

**Import:**
```java
import funcBox.io.FileUtil;
```

**Available Functions:**

| Function | Description |
|----------|-------------|
| `loadResource(String path)` | Load a UTF-8 text resource from `src/main/resources` (JAR-safe) |
| `safeWrite(Path path, String content)` | Atomic write using temp-file + backup + rollback |
| `getMimeType(File file)` | Best-effort MIME detection (extension-free), returns `application/octet-stream` when unknown |

**Examples:**
```java
// 1) Loading a resource (inside JAR or in IDE)
String text = FileUtil.loadResource("funcbox_io_demo.txt");
System.out.println(text);

// 2) Safe write with backup
Path out = Path.of("output.txt");
FileUtil.safeWrite(out, "hello\n");

// 3) MIME detection
String mime = FileUtil.getMimeType(out.toFile());
System.out.println(mime);
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

#### getFactors

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

#### isPalindrome

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

#### isAnagram

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

#### capitalizeEachWord

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

#### truncate

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

#### clamp

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

#### levenshteinDistance

```java
int Misc.levenshteinDistance(String str1, String str2)
int[] Misc.levenshteinDistance(String target, String[] candidates)
```

Calculates the Levenshtein distance (edit distance) between two strings. The distance is the minimum number of single-character edits (insertions, deletions, or substitutions) required to change one string into the other. A distance of 0 means the strings are identical.

**Performance:** O(n × m) time, O(m) space using optimized 1D array approach.

**Guard-rails:**
- Null inputs are treated as empty strings
- Works with strings of any length

**Parameters (single comparison):**

| Parameter | Type     | Description |
|-----------|----------|-------------|
| `str1`    | `String` | First string (may be null) |
| `str2`    | `String` | Second string (may be null) |

**Returns:** Edit distance as integer (0 = identical, increases with difference).

**Parameters (array comparison):**

| Parameter    | Type       | Description |
|--------------|------------|-------------|
| `target`     | `String`   | Target string (may be null) |
| `candidates` | `String[]` | Array of candidate strings |

**Returns:** Array of distances corresponding to each candidate.

**Examples:**

```java
// Single comparison
Misc.levenshteinDistance("hello", "hello");  // 0 (identical)
Misc.levenshteinDistance("hello", "holl");   // 2 (1 substitution, 1 deletion)
Misc.levenshteinDistance("abc", "xyz");      // 3 (completely different)
Misc.levenshteinDistance("hello", null);     // 5 (null treated as empty)

// Multiple candidates
int[] distances = Misc.levenshteinDistance("hello", new String[]{
    "heat",   // 3 edits
    "help",   // 2 edits
    "groot",  // 5 edits
    "hell"    // 1 edit
});
// Result: [3, 2, 5, 1]
```

---

#### fuzzyMatchScore

```java
double Misc.fuzzyMatchScore(String str1, String str2)
double[] Misc.fuzzyMatchScore(String target, String[] candidates)
```

Calculates a normalized fuzzy match score between two strings, ranging from 0.0 to 1.0. A score of 1.0 means an exact match, while 0.0 means completely different strings.

**Score Calculation:** Based on Levenshtein distance normalized by the maximum string length.

**Parameters (single comparison):**

| Parameter | Type     | Description |
|-----------|----------|-------------|
| `str1`    | `String` | First string (may be null) |
| `str2`    | `String` | Second string (may be null) |

**Returns:** Similarity score between 0.0 and 1.0.

**Parameters (array comparison):**

| Parameter    | Type       | Description |
|--------------|------------|-------------|
| `target`     | `String`   | Target string (may be null) |
| `candidates` | `String[]` | Array of candidate strings |

**Returns:** Array of scores corresponding to each candidate.

**Examples:**

```java
// Single comparison
Misc.fuzzyMatchScore("hello", "hello");  // 1.0 (exact match)
Misc.fuzzyMatchScore("hello", "hallo");  // 0.8 (very similar)
Misc.fuzzyMatchScore("hello", "xyz");    // 0.0 (completely different)

// Multiple candidates
double[] scores = Misc.fuzzyMatchScore("hello", new String[]{
    "hebdfat",  // 0.286
    "helgp",    // 0.6
    "groot",    // 0.0
    "hell"      // 0.8
});
// Result: [0.286, 0.6, 0.0, 0.8]

// Finding best match
double[] scores = Misc.fuzzyMatchScore("algoritm", new String[]{
    "algorithm",
    "alternator",
    "algae"
});
// Highest score indicates the best match
```

---

### funcBox.dig — Safe JSON Navigation

**Import:**
```java
import funcBox.dig.Dig;
import funcBox.dig.DigContext;
```

Parse JSON once, then reuse a `DigContext` for repeated lookups over maps, lists, and arrays.
`Dig.of(String)` supports both JSON object roots (`{...}`) and JSON array roots (`[...]`).

**Available Methods:**

| Method | Description |
|--------|-------------|
| [`Dig.of(String json)`](#digofstring-json) | Create context from JSON string |
| [`Dig.of(Object source)`](#digofobject-source) | Create context from object/Map/List |
| [`get(Object path)`](#getobject-path) | Get value at path (returns null if missing) |
| [`get(Object path, Object defaultValue)`](#getobject-path-object-defaultvalue) | Get value with fallback |
| [`has(Object path)`](#hasobject-path) | Check if path exists |
| [`isEmpty()`](#isempty) | Check if context is empty |
| [`scope(Object path)`](#scopeobject-path) | Create child context at path |
| [`getAll(String... paths)`](#getallstring-paths) | Get multiple values at once |
| [`getString(String path)`](#getstringstring-path) | Get value as String |
| [`getInt(String path)`](#getintstring-path) | Get value as Integer |
| [`getLong(String path)`](#getlongstring-path) | Get value as Long |
| [`getDouble(String path)`](#getdoublestring-path) | Get value as Double |
| [`getBoolean(String path)`](#getbooleanstring-path) | Get value as Boolean |

---

#### Dig

Factory entry point for creating `DigContext` instances from JSON or object graphs.

```java
DigContext Dig.of(String json)
DigContext Dig.of(Object source)
```

**`Dig.of(String json)`**

Creates a `DigContext` from a raw JSON string.

**Parameters:**
| Parameter | Type     | Description         |
|-----------|----------|---------------------|
| `json`    | `String` | Raw JSON text       |

**Returns:** Immutable `DigContext` instance, or `EMPTY` if JSON is null/blank/invalid.

**Example:**
```java
String json = "{\"user\": {\"name\": \"Alice\", \"age\": 30}}";
DigContext d = Dig.of(json);
System.out.println(d.getString("user.name")); // Alice
```

**`Dig.of(Object source)`**

Creates a `DigContext` from JSON, an existing `DigContext`, or an object graph (Map/List/Array).

**Parameters:**
| Parameter | Type      | Description                                              |
|-----------|-----------|----------------------------------------------------------|
| `source`  | `Object`  | Raw JSON (String), `DigContext`, Map/List/Array, or null |

**Returns:** Normalized `DigContext` instance.

**Example:**
```java
Map<String, Object> data = Map.of("name", "Bob", "age", 25);
DigContext d = Dig.of(data);
System.out.println(d.getString("name")); // Bob
```

---

#### DigContext

Immutable context for safe navigation through JSON and object graphs using dot-path expressions.


---

##### get(Object path)

Resolves a value from the context using a dot path, list path, or object-array path.

```java
Object DigContext.get(Object path)
```

**Parameters:**
| Parameter | Type     | Description       |
|-----------|----------|-------------------|
| `path`    | `Object` | Path expression   |

**Returns:** Resolved value or `null` if not found.

**Example:**
```java
DigContext d = Dig.of("{\"user\": {\"profile\": {\"city\": \"NY\"}}}");
String city = (String) d.get("user.profile.city"); // "NY"
Object missing = d.get("user.email");              // null
```

---

##### get(Object path, Object defaultValue)

Resolves a value from the context with a fallback default.

```java
Object DigContext.get(Object path, Object defaultValue)
```

**Parameters:**
| Parameter      | Type     | Description                  |
|----------------|----------|------------------------------|
| `path`         | `Object` | Path expression              |
| `defaultValue` | `Object` | Fallback value when missing  |

**Returns:** Resolved value or `defaultValue` if not found.

**Example:**
```java
DigContext d = Dig.of("{\"count\": 5}");
Integer count = (Integer) d.get("count", 0);         // 5
Integer missing = (Integer) d.get("missing", 0);     // 0 (default)
```

---

##### has(Object path)

Checks whether a path exists in the current context.

```java
boolean DigContext.has(Object path)
```

**Parameters:**
| Parameter | Type     | Description       |
|-----------|----------|-------------------|
| `path`    | `Object` | Path expression   |

**Returns:** `true` if the path exists (even when value is null), `false` otherwise.

**Example:**
```java
DigContext d = Dig.of("{\"user\": {\"name\": null}}");
System.out.println(d.has("user.name"));  // true (exists even though null)
System.out.println(d.has("user.email")); // false (doesn't exist)
```

---

##### isEmpty()

Indicates whether this context contains usable data.

```java
boolean DigContext.isEmpty()
```

**Returns:** `true` when the context is empty (created from null/invalid JSON).

**Example:**
```java
DigContext valid = Dig.of("{\"x\": 1}");
DigContext empty = Dig.of(null);
System.out.println(valid.isEmpty());     // false
System.out.println(empty.isEmpty());     // true
System.out.println(empty == DigContext.EMPTY); // true
```

---

##### scope(Object path)

Re-roots the context to a nested node, creating a child context.

```java
DigContext DigContext.scope(Object path)
```

**Parameters:**
| Parameter | Type     | Description       |
|-----------|----------|-------------------|
| `path`    | `Object` | Path expression   |

**Returns:** Child `DigContext` rooted at the specified path, or `EMPTY` if path is missing.

**Example:**
```java
String json = "{\"user\": {\"profile\": {\"city\": \"NY\", \"zip\": 10001}}}";
DigContext d = Dig.of(json);
DigContext profile = d.scope("user.profile");
System.out.println(profile.getString("city")); // "NY"
System.out.println(profile.getString("zip"));  // 10001
```

---

##### getAll(String... paths)

Resolves multiple paths in one call.

```java
List<Object> DigContext.getAll(String... paths)
```

**Parameters:**
| Parameter | Type        | Description              |
|-----------|-------------|--------------------------|
| `paths`   | `String...` | One or more path strings |

**Returns:** Ordered list of resolved values (same order as input paths).

**Example:**
```java
DigContext d = Dig.of("{\"a\": 1, \"b\": 2, \"c\": 3}");
List<Object> values = d.getAll("a", "c", "b");
System.out.println(values); // [1, 3, 2]
```

---

##### getString(String path)

Resolves a path and converts the value to string when present.

```java
String DigContext.getString(String path)
```

**Parameters:**
| Parameter | Type     | Description |
|-----------|----------|-------------|
| `path`    | `String` | Dot path    |

**Returns:** String value or `null` if not found.

**Example:**
```java
DigContext d = Dig.of("{\"id\": 42, \"name\": \"Alice\"}");
System.out.println(d.getString("name")); // "Alice"
System.out.println(d.getString("id"));   // "42" (converted to string)
System.out.println(d.getString("missing")); // null
```

---

##### getInt(String path)

Resolves a path and converts to `Integer` when possible.

```java
Integer DigContext.getInt(String path)
```

**Parameters:**
| Parameter | Type     | Description |
|-----------|----------|-------------|
| `path`    | `String` | Dot path    |

**Returns:** Integer value or `null` if not found or cannot be parsed.

**Supported conversions:**
- Direct `Integer` values
- Other `Number` types (cast to int)
- Numeric `String` values (parsed)

**Example:**
```java
DigContext d = Dig.of("{\"count\": 42, \"price\": 19.99, \"quantity\": \"100\"}");
System.out.println(d.getInt("count"));     // 42
System.out.println(d.getInt("price"));     // 19 (truncated from double)
System.out.println(d.getInt("quantity"));  // 100 (parsed from string)
System.out.println(d.getInt("missing"));   // null
```

---

##### getLong(String path)

Resolves a path and converts to `Long` when possible.

```java
Long DigContext.getLong(String path)
```

**Parameters:**
| Parameter | Type     | Description |
|-----------|----------|-------------|
| `path`    | `String` | Dot path    |

**Returns:** Long value or `null` if not found or cannot be parsed.

**Supported conversions:**
- Direct `Long` values
- Other `Number` types (cast to long)
- Numeric `String` values (parsed)

**Example:**
```java
DigContext d = Dig.of("{\"timestamp\": 1609459200000, \"count\": \"9999999999\"}");
System.out.println(d.getLong("timestamp")); // 1609459200000
System.out.println(d.getLong("count"));     // 9999999999
System.out.println(d.getLong("missing"));   // null
```

---

##### getDouble(String path)

Resolves a path and converts to `Double` when possible.

```java
Double DigContext.getDouble(String path)
```

**Parameters:**
| Parameter | Type     | Description |
|-----------|----------|-------------|
| `path`    | `String` | Dot path    |

**Returns:** Double value or `null` if not found or cannot be parsed.

**Supported conversions:**
- Direct `Double` values
- Other `Number` types (cast to double)
- Numeric `String` values (parsed)

**Example:**
```java
DigContext d = Dig.of("{\"price\": 19.99, \"rating\": 4, \"tax\": \"0.08\"}");
System.out.println(d.getDouble("price"));  // 19.99
System.out.println(d.getDouble("rating")); // 4.0
System.out.println(d.getDouble("tax"));    // 0.08
System.out.println(d.getDouble("missing")); // null
```

---

##### getBoolean(String path)

Resolves a path and converts to `Boolean` when possible.

```java
Boolean DigContext.getBoolean(String path)
```

**Parameters:**
| Parameter | Type     | Description |
|-----------|----------|-------------|
| `path`    | `String` | Dot path    |

**Returns:** Boolean value or `null` if not found or cannot be parsed.

**Supported conversions:**
- Direct `Boolean` values
- Numeric values: `0.0` → false, non-zero → true
- String values: `"true"` (case-insensitive) → true, `"false"` → false

**Example:**
```java
DigContext d = Dig.of("{\"active\": true, \"deleted\": false, \"count\": 5, \"enabled\": \"true\"}");
System.out.println(d.getBoolean("active"));   // true
System.out.println(d.getBoolean("deleted"));  // false
System.out.println(d.getBoolean("count"));    // true (5 != 0)
System.out.println(d.getBoolean("enabled"));  // true (parsed from string)
System.out.println(d.getBoolean("missing"));  // null
```

---

**Path Format Guide:**

| Use Case | Path Syntax | Example |
|----------|------------|---------|
| **Access Map keys** (object properties) | `key.subkey.property` | `"user.profile.city"` |
| **Access List/Array elements** | Use **numeric index** for lists/arrays | `"employees.0.name"` (1st employee) |
| **Mix keys and indexes** | Alternate between names and numbers | `"data.0.items.2"` (1st item, 3rd sub-item) |


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

**Available Methods:**

| Method | Description |
|--------|-------------|
| [`dijkstra(Map graph, String startNode)`](#dijkstra-single-source) | Find shortest paths from start to all reachable nodes |
| [`dijkstra(Map graph, String startNode, String endNode)`](#dijkstra-source-to-target) | Find shortest path from start to specific target |

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

### funcBox.http — Simplified Web Client

**Import:**
```java
import funcBox.http.HttpBox;
```

`HttpBox` provides a simplified, fetch-like API for HTTP requests, wrapping Java 11+ `HttpClient` with minimal boilerplate. All methods are static and non-blocking with a 10-second timeout.

**Available Methods:**

| Method | Description |
|--------|-------------|
| [`get(String url)`](#get) | Send GET request, return response body |
| [`getJson(String url)`](#getjson) | Send GET request, return parsed JSON |
| [`post(String url, String body)`](#post) | Send POST request with String body |
| [`postJson(String url, JSONObject jsonObj)`](#postjson) | Send POST request with JSON object |
| [`put(String url, String body)`](#put) | Send PUT request with String body |
| [`delete(String url)`](#delete) | Send DELETE request |
| [`request(String method, String url, String body, String contentType)`](#request) | Generic request method |

---

#### get

```java
String HttpBox.get(String url)
```

Sends a GET request and returns the response body as a String.

**Parameters:**
| Parameter | Type     | Description   |
|-----------|----------|----------------|
| `url`     | `String` | The URL to fetch |

**Returns:** Response body as `String`, or `null` if status is not 2xx.

**Throws:** 
| Exception | Cause |
|-----------|-------|
| `IllegalArgumentException` | URL is null or blank |
| `HttpBoxException` | Network error occurs |

**Example:**
```java
String response = HttpBox.get("https://api.github.com/repos/funcBox-i3/funcBox-java");
if (response != null) {
    System.out.println(response);
} else {
    System.out.println("Request failed (non-2xx status)");
}
```

---

#### getJson

```java
JSONObject HttpBox.getJson(String url)
```

Sends a GET request and parses the response as JSON.

**Parameters:**
| Parameter | Type     | Description   |
|-----------|----------|----------------|
| `url`     | `String` | The URL to fetch |

**Returns:** Parsed `JSONObject`, or `null` if status is not 2xx.

**Throws:**
| Exception | Cause |
|-----------|-------|
| `IllegalArgumentException` | URL is null or blank |
| `HttpBoxException` | Network error occurs OR JSON parsing fails |

**Example:**
```java
JSONObject repo = HttpBox.getJson("https://api.github.com/repos/funcBox-i3/funcBox-java");
if (repo != null) {
    System.out.println("Stars: " + repo.getInt("stargazers_count"));
} else {
    System.out.println("Failed to fetch JSON");
}
```

---

#### post

```java
String HttpBox.post(String url, String body)
```

Sends a POST request with a String body and returns the response.

**Parameters:**
| Parameter | Type     | Description                |
|-----------|----------|----------------------------|
| `url`     | `String` | The URL to post to        |
| `body`    | `String` | Request body (can be null) |

**Returns:** Response body as `String`, or `null` if status is not 2xx.

**Throws:**
| Exception | Cause |
|-----------|-------|
| `IllegalArgumentException` | URL is null or blank |
| `HttpBoxException` | Network error occurs |

**Example:**
```java
String json = "{\"name\": \"Alice\", \"age\": 30}";
String response = HttpBox.post("https://api.example.com/users", json);
if (response != null) {
    System.out.println("Created: " + response);
}
```

---

#### postJson

```java
String HttpBox.postJson(String url, JSONObject jsonObj)
```

Sends a POST request with a JSON object and returns the response.

**Parameters:**
| Parameter | Type         | Description       |
|-----------|--------------|-------------------|
| `url`     | `String`     | The URL to post to |
| `jsonObj` | `JSONObject` | The JSON object to send |

**Returns:** Response body as `String`, or `null` if status is not 2xx.

**Throws:**
| Exception | Cause |
|-----------|-------|
| `IllegalArgumentException` | URL is null/blank OR jsonObj is null |
| `HttpBoxException` | Network error occurs |

**Example:**
```java
JSONObject payload = new JSONObject();
payload.put("name", "Bob");
payload.put("email", "bob@example.com");

String response = HttpBox.postJson("https://api.example.com/users", payload);
if (response != null) {
    System.out.println("Success: " + response);
}
```

---

#### **`put`**

```java
String HttpBox.put(String url, String body)
```

Sends a PUT request with a String body and returns the response.

**Parameters:**
| Parameter | Type     | Description                |
|-----------|----------|----------------------------|
| `url`     | `String` | The URL to put to         |
| `body`    | `String` | Request body (can be null) |

**Returns:** Response body as `String`, or `null` if status is not 2xx.

**Throws:**
| Exception | Cause |
|-----------|-------|
| `IllegalArgumentException` | URL is null or blank |
| `HttpBoxException` | Network error occurs |

**Example:**
```java
String json = "{\"id\": 123, \"status\": \"active\"}";
String response = HttpBox.put("https://api.example.com/users/123", json);
if (response != null) {
    System.out.println("Updated: " + response);
}
```

---

#### **`delete`**

```java
String HttpBox.delete(String url)
```

Sends a DELETE request and returns the response.

**Parameters:**
| Parameter | Type     | Description      |
|-----------|----------|-------------------|
| `url`     | `String` | The URL to delete |

**Returns:** Response body as `String`, or `null` if status is not 2xx.

**Throws:**
| Exception | Cause |
|-----------|-------|
| `IllegalArgumentException` | URL is null or blank |
| `HttpBoxException` | Network error occurs |

**Example:**
```java
String response = HttpBox.delete("https://api.example.com/users/123");
if (response != null) {
    System.out.println("Deleted successfully: " + response);
}
```

---

#### request

```java
String HttpBox.request(String method, String url, String body, String contentType)
```

Generic request method supporting custom HTTP methods. Use this for methods not covered by the convenience methods above.

**Parameters:**
| Parameter     | Type     | Description                           |
|---------------|----------|---------------------------------------|
| `method`      | `String` | HTTP method (GET, POST, PUT, DELETE, PATCH, etc.) |
| `url`         | `String` | The URL                               |
| `body`        | `String` | Request body (null if none)           |
| `contentType` | `String` | Content-Type header (null for none)   |

**Returns:** Response body as `String`, or `null` if status is not 2xx.

**Throws:**
| Exception | Cause |
|-----------|-------|
| `IllegalArgumentException` | URL is null or blank |
| `HttpBoxException` | Network error occurs |

**Example:**
```java
String response = HttpBox.request(
    "PATCH",
    "https://api.example.com/data/1",
    "{\"field\": \"value\"}",
    "application/json"
);
if (response != null) {
    System.out.println("Patched: " + response);
}
```

---

**Error Handling:**

| Behavior | Condition |
|----------|-----------|
| Returns `null` | Status code is not 2xx (< 200 or >= 300) |
| Throws `IllegalArgumentException` | URL is null or blank |
| Throws `HttpBoxException` | Network timeout, connection error, or parsing error |

**Default Behavior:**
- **Timeout:** 10 seconds per request
- **Content-Type:** `application/json` (for POST/PUT methods)
- **HTTP Version:** HTTP/1.1 (or higher, depending on client)

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
