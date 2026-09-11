## 📄StreamAPIで使用するメソッド一覧

**_概要_**  
Java Stream APIで使用される**メソッドの全て**を、機能ごとに一覧で提供します。  
Stream APIのメソッドは、大きく以下の3つのカテゴリーに分類されます。

1.  **ストリーム生成メソッド**: ストリームを作成します。
2.  **中間操作メソッド**: データを加工・変換・フィルタリングし、**新しいストリーム**を返します（遅延実行されます）。
3.  **終端操作メソッド**: ストリーム処理を**実行**し、結果（コレクション、値、副作用など）を返します。

---

## 1. 🏭 ストリーム生成メソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `stream()` | **`Collection`**（`List`, `Set`など）から**順次ストリーム**を生成します。 |
| ☠️ **使わない** | `parallelStream()` | **`Collection`**から**並列ストリーム**を生成します。（※スレッド枯渇やバグの要因となるため実務では原則避ける） |
| 💡 **たまに使う** | `Arrays.stream(array)` | **配列**からストリームを生成します。 |
| 💡 **たまに使う** | `Stream.of(T... values)` | 指定された**要素**からストリームを生成します。 |
| ☠️ **使わない** | `Stream.empty()` | 要素を含まない**空のストリーム**を生成します。 |
| ☠️ **使わない** | `Stream.generate(Supplier<T> s)` | 指定された`Supplier`を使って**無限ストリーム**を生成します。 |
| ☠️ **使わない** | `Stream.iterate(T seed, UnaryOperator<T> f)` | **初期値**と**適用関数**を指定して**無限ストリーム**を生成します。 |
| 💡 **たまに使う** | `IntStream.range(int start, int end)` | `start`（含む）から`end`（含まない）までの**整数シーケンス**を生成します。 |
| 💡 **たまに使う** | `IntStream.rangeClosed(int start, int end)` | `start`から`end`まで**両方を含む**整数シーケンスを生成します。 |

---

## 2. ⚙️ 中間操作メソッド (Intermediate Operations)

中間操作はメソッドチェーンで繋げることができ、**遅延実行**されます。

### 🔄 変換・加工

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `map(Function<T, R> mapper)` | 各要素を、指定された関数（`mapper`）または、ラムダ式を使用して**別の型や値に変換**します。 |
| 🔥 **よく使う** | `flatMap(Function<T, Stream<R>> mapper)` | 要素を**ストリームに変換**し、そのストリームの要素を**単一ストリームに平坦化**します（ネストされた構造を解消）。 |
| 💡 **たまに使う** | `mapToInt / mapToLong / mapToDouble` | 要素をそれぞれプリミティブ型の`IntStream`、`LongStream`、`DoubleStream`に変換します。 |

### 🔍 フィルタリング・選別

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `filter(Predicate<T> predicate)` | 指定された条件（`predicate`）に**合致する要素のみ**を抽出します。 |
| 🔥 **よく使う** | `distinct()` | ストリーム内の**重複する要素を取り除きます**（`Object.equals(Object)`に基づいて比較）。 |

### 📏 制限・スキップ

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `limit(long maxSize)` | ストリームの要素を**先頭から指定された数**に制限します。 |
| 💡 **たまに使う** | `skip(long n)` | ストリームの要素の**先頭から指定された数**をスキップします。 |

### 🔀 並び替え・順序

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `sorted()` | 要素の**自然順序**（`Comparable`）でソートします。 |
| 🔥 **よく使う** | `sorted(Comparator<T> comparator)` | 指定された`Comparator`でソートします。 |
| 💡 **たまに使う** | `peek(Consumer<T> action)` | ストリームの各要素が処理される際に、**副作用を伴う操作**（デバッグやログ出力など）を実行します。ストリーム自体は変更しません。 |

---

## 3. 🎯 終端操作メソッド (Terminal Operations)

終端操作は**一度だけ**実行でき、パイプラインを終了させます。

### 📊 収集・集約

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `collect(Collector<T, A, R> collector)` | 処理結果を**`List`、`Set`、`Map`**などの**新しいコレクションに変換・収集**します。 (`Collectors.toList()`, `Collectors.toMap()`などを使用) |
| 🔥 **よく使う** | `toList()` | (Java 16以降) 処理結果を**不変の`List`**に変換します。 |
| ☠️ **使わない** | `toArray()` | 要素を**配列**に変換し、配列型として返します。 |
| ☠️ **使わない** | `reduce(T identity, BinaryOperator<T> accumulator)` | ストリームの要素を**結合**（ギュッと1つにする）し、その単一の結果を返す。 |
| 💡 **たまに使う** | `count()` | ストリーム内の**要素の総数**を返します。 |
| 💡 **たまに使う** | `min(Comparator<T> comparator)` | 指定された`Comparator`で**最小の要素**を返します（`Optional`でラップ）。 |
| 💡 **たまに使う** | `max(Comparator<T> comparator)` | 指定された`Comparator`で**最大の要素**を返します（`Optional`でラップ）。 |

### 🔎 検索・マッチング

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `findFirst()` | ストリーム内の**最初の要素**を返します（`Optional`でラップ）。 |
| 💡 **たまに使う** | `findAny()` | ストリーム内の**任意の要素**を返します（`Optional`でラップ。並列ストリームで効率的）。 |
| 🔥 **よく使う** | `anyMatch(Predicate<T> predicate)` | **いずれかの要素**が条件に合致するかどうかを返します（`boolean`）。 |
| 💡 **たまに使う** | `allMatch(Predicate<T> predicate)` | **すべての要素**が条件に合致するかどうかを返します（`boolean`）。 |
| 💡 **たまに使う** | `noneMatch(Predicate<T> predicate)` | **どの要素も**条件に合致しないかどうかを返します（`boolean`）。 |

### 💥 副作用（出力・処理実行）

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `forEach(Consumer<T> action)` | ストリーム内の各要素に対して**指定された操作を実行**します（戻り値なし）。ラムダ式を使用する。 |
| ☠️ **使わない** | `forEachOrdered(Consumer<T> action)` | **ストリームの定義された順序**で、各要素に対して操作を実行します。 |

### 📦 プリミティブ型ストリーム（`IntStream`, `LongStream`, `DoubleStream`）の特別な終端操作

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `sum()` | 全要素の**合計**を計算します。 |
| 💡 **たまに使う** | `average()` | 全要素の**平均**を計算します（`OptionalDouble`でラップ）。 |
| ☠️ **使わない** | `summaryStatistics()` | `count`, `sum`, `min`, `max`, `average`を**一度に集計**した統計情報を返します。 |

## 🚀 具体的なコード例 (Java Stream API)

### 1\. `filter` と `map` の組み合わせ (中間操作)

- **目的:** 偶数のみを抽出し（`filter`）、その偶数を2倍に変換する（`map`）。

<!-- end list -->

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        // 偶数のみを抽出し、それらを2倍にしたリストを作成
        List<Integer> doubledEvens = numbers.stream()
            .filter(n -> n % 2 == 0) // filter: 偶数(n % 2 == 0)を抽出
            .map(n -> n * 2)         // map: 各要素を2倍に変換
            .collect(Collectors.toList()); // 終端操作: 結果をListに収集

        System.out.println("元のリスト: " + numbers);
        System.out.println("結果のリスト: " + doubledEvens); // [4, 8, 12]
    }
}
```

---

### 2\. `distinct` と `sorted` (中間操作)

- **目的:** 重複する要素を除去し（`distinct`）、降順にソートする（`sorted`）。

<!-- end list -->

```java
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DistinctAndSortExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Alice", "David", "Bob");

        // 重複を除去し、名前をアルファベット降順でソート
        List<String> uniqueSortedNames = names.stream()
            .distinct() // distinct: 重複要素を除去
            .sorted(Comparator.reverseOrder()) // sorted: 降順にソート
            .collect(Collectors.toList()); // 終端操作: Listに収集

        System.out.println("元のリスト: " + names);
        System.out.println("結果のリスト: " + uniqueSortedNames); // [David, Charlie, Bob, Alice]
    }
}
```

---

### 3\. `reduce` (終端操作)

- **目的:** ストリームの全要素を合計する（集約）。

<!-- end list -->

```java
import java.util.Arrays;
import java.util.List;

public class ReduceExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(10, 20, 30, 40);

        // reduce: 0を初期値として、全要素を足し合わせる
        int sum = numbers.stream()
            .reduce(0, (a, b) -> a + b); // (初期値, (現在までの合計, 次の要素) -> 新しい合計)

        System.out.println("合計: " + sum); // 100
    }
}
```

---

### 4\. `anyMatch` (終端操作)

- **目的:** 特定の条件を満たす要素が一つでもあるかを確認する。

<!-- end list -->

```java
import java.util.Arrays;
import java.util.List;

public class MatchExample {
    public static void main(String[] args) {
        List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry", "Date");

        // anyMatch: 'B'で始まる要素が一つでも存在するか
        boolean hasBStart = fruits.stream()
            .anyMatch(s -> s.startsWith("B")); // 終端操作: 結果はboolean

        // allMatch: 全ての要素が5文字以上か
        boolean allFiveLetters = fruits.stream()
            .allMatch(s -> s.length() >= 5); // 終端操作: 結果はboolean

        System.out.println("'B'で始まる要素があるか: " + hasBStart); // true
        System.out.println("全て5文字以上か: " + allFiveLetters); // false
    }
}
```

## 以下、参考動画のリンク

[ストリームとは何かをわかりやすく解説！【Java応用講座】1-1 ストリームの基本](https://www.google.com/search?q=https://www.youtube.com/watch%3Fv%3DmJYU9eVtW05)
