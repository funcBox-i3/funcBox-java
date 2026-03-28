<div align="center">

# 📦 FuncBox

**A streamlined, high-performance Java utility library for mathematics, strings, and graph algorithms.**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-11+-ED8B00.svg?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.1.0-blue.svg?logo=apachemaven)](https://search.maven.org/artifact/io.github.funcbox-i3/funcBox)

</div>

---

## Installation

Integrate FuncBox into your project using your preferred build tool.

### Maven

```xml
<dependency>
    <groupId>io.github.funcbox-i3</groupId>
    <artifactId>funcBox</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'io.github.funcbox-i3:funcBox:1.1.0'
```

---

## Quick Start

```java
import funcBox.Misc;
import funcBox.dijkstra.Dijkstra;
import funcBox.dijkstra.Result;

// Check if a number is prime
System.out.println(Misc.isPrime(17));           // true

// Get all primes in a range
System.out.println(Misc.primes(10, 30));        // [11, 13, 17, 19, 23, 29]

// Find the shortest path in a graph
Result r = Dijkstra.dijkstra(graph, "A", "F");
System.out.println(r.distances);
System.out.println(r.paths);
```

---

## API Reference

### `Misc` — Mathematics & Strings

All methods are **static** and can be called directly without instantiation.

| Method | Description |
|--------|-------------|
| [`isPrime`](#misc-isprime) | Check if a number is prime |
| [`primes`](#misc-primes) | Get all primes in a range |
| [`fibonacci`](#misc-fibonacci) | Get the nth Fibonacci number |
| [`getFactors`](#misc-getfactors) | Get all factors of a number |
| [`isPalindrome`](#misc-ispalindrome) | Check if a string is a palindrome |
| [`splitPrimeComposite`](#misc-splitprimecomposite) | Split numbers into primes and composites |

---

<a id="misc-isprime"></a>

#### ✨ `Misc.isPrime`

```java
boolean Misc.isPrime(int num)
```

Checks whether a given number is prime.

| Parameter | Type  | Description          |
|-----------|-------|----------------------|
| `num`     | `int` | The number to check. |

**Returns:** `true` if prime, `false` otherwise.

```java
Misc.isPrime(7);   // true
Misc.isPrime(10);  // false
```

---

<a id="misc-primes"></a>

#### ✨ `Misc.primes`

```java
List<Integer> Misc.primes(int start, int limit)
```

Generates all prime numbers within a given range using the Sieve of Eratosthenes.

| Parameter | Type  | Description                        |
|-----------|-------|------------------------------------|
| `start`   | `int` | Lower bound, inclusive (min: `2`). |
| `limit`   | `int` | Upper bound, inclusive (min: `2`). |

**Returns:** `List<Integer>` of all primes between `start` and `limit`.

> [!NOTE]
> Both `start` and `limit` must be at least `2`, otherwise an `IllegalArgumentException` is thrown.

```java
Misc.primes(2, 10);   // [2, 3, 5, 7]
Misc.primes(10, 20);  // [11, 13, 17, 19]
```

---

<a id="misc-fibonacci"></a>

#### ✨ `Misc.fibonacci`

```java
Integer Misc.fibonacci(int num)
```

Calculates the Fibonacci number at the specified index.

| Parameter | Type  | Description                          |
|-----------|-------|--------------------------------------|
| `num`     | `int` | The index in the Fibonacci sequence. |

**Returns:** The Fibonacci value at position `num`.

```java
Misc.fibonacci(0);   // 0
Misc.fibonacci(5);   // 5
Misc.fibonacci(10);  // 55
```

---

<a id="misc-getfactors"></a>

#### ✨ `Misc.getFactors`

```java
String Misc.getFactors(int num)
```

Returns all factors of a number, excluding the number itself.

| Parameter | Type  | Description                     |
|-----------|-------|---------------------------------|
| `num`     | `int` | The number to find factors for. |

**Returns:** A `String` representation of the factor list. Returns `[1]` for prime numbers.

```java
Misc.getFactors(12);  // [1, 2, 3, 4, 6]
Misc.getFactors(7);   // [1]
Misc.getFactors(28);  // [1, 2, 4, 7, 14]
```

---

<a id="misc-ispalindrome"></a>

#### ✨ `Misc.isPalindrome`

```java
boolean Misc.isPalindrome(String val)
```

Checks whether a string reads the same forward and backward. The comparison is case-insensitive.

| Parameter | Type     | Description             |
|-----------|----------|-------------------------|
| `val`     | `String` | The string to evaluate. |

**Returns:** `true` if the string is a palindrome, `false` otherwise.

```java
Misc.isPalindrome("Racecar");  // true
Misc.isPalindrome("madam");    // true
Misc.isPalindrome("Hello");    // false
```

---

<a id="misc-splitprimecomposite"></a>

#### ✨ `Misc.splitPrimeComposite`

```java
List<List<Integer>> Misc.splitPrimeComposite(List<Integer> numbers)
```

Partitions a list of integers into primes and composites. Numbers less than or equal to `1` are ignored.

| Parameter | Type            | Description                      |
|-----------|-----------------|----------------------------------|
| `numbers` | `List<Integer>` | The list of integers to analyze. |

**Returns:** A `List` containing two sublists:

| Index | Contents   |
|-------|------------|
| `0`   | Primes     |
| `1`   | Composites |

```java
List<Integer> nums = List.of(2, 3, 4, 5, 6, 7, 8, 9, 10);
List<List<Integer>> result = Misc.splitPrimeComposite(nums);

result.get(0);  // [2, 3, 5, 7]      — Primes
result.get(1);  // [4, 6, 8, 9, 10]  — Composites
```

---

### `Dijkstra` — Graph Algorithms

| Method | Description |
|--------|-------------|
| [`dijkstra`](#dijkstra-dijkstra) | Find shortest paths in a weighted graph |

---

<a id="dijkstra-dijkstra"></a>

#### ✨ `Dijkstra.dijkstra`

```java
Result Dijkstra.dijkstra(Map<String, Map<String, Integer>> graph, String startNode)
Result Dijkstra.dijkstra(Map<String, Map<String, Integer>> graph, String startNode, String endNode)
```

Computes shortest paths using Dijkstra's algorithm. Two overloads are available:

- **Without `endNode`** — Returns the path to the farthest reachable node from `startNode`.
- **With `endNode`** — Returns the exact shortest path from `startNode` to `endNode`.

| Parameter   | Type                                | Description                                                 |
|-------------|-------------------------------------|-------------------------------------------------------------|
| `graph`     | `Map<String, Map<String, Integer>>` | Adjacency map: `Node -> { Neighbor -> Edge Weight }`.       |
| `startNode` | `String`                            | The starting node for pathfinding.                          |
| `endNode`   | `String` *(optional)*               | Target node. If specified, only the path to it is returned. |

**Returns:** A `Result` object with:

| Field               | Type                          | Description                          |
|---------------------|-------------------------------|--------------------------------------|
| `result.distances`  | `Map<String, Integer>`        | Shortest distance to each node.      |
| `result.paths`      | `Map<String, List<String>>`   | Shortest path (as node list) to each node. |

> [!NOTE]
> Both `startNode` and `endNode` (if provided) must exist as keys in the graph, otherwise an `IllegalArgumentException` is thrown.

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
```

---

## Contributing

Contributions, issues, and feature requests are welcome.

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

See the [issues page](https://github.com/funcBox-i3/funcBox-java/issues) for open tasks.

---

## Disclaimer

FuncBox provides utility functions for general use. The developer is not responsible for any issues caused by improper use or abuse of the library.

---

## License

This project is licensed under the MIT [License](LICENSE.txt).
