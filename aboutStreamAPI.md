はい、Javaで配列やコレクションの処理を**Stream API**で行う際、`filter`以外にも多くの便利な処理があります。これらの処理は「**中間操作**」と「**終端操作**」の2種類に大きく分けられます。

-----

## 1\. 中間操作（`filter`の仲間）✨

中間操作はStreamを受け取ってStreamを返すため、**パイプラインとして複数つなげることが可能**です。`filter`は要素を絞り込む操作です。

| API（メソッド） | 説明 |
| :--- | :--- |
| `map(Function)` | **要素の変換**を行います。ストリーム内の各要素に指定されたラムダ式を適用し、結果を新しいストリームとして返します。型が変わることもあります。 |
| `flatMap(Function)` | **要素の分解・増幅**を行います。各要素をストリームに変換し、それらのストリームをすべて結合した一つのストリームを返します。例えば、リストのリストを一つのリストに平坦化するのに使われます。 |
| `distinct()` | **重複の除去**を行います。`equals`メソッドに基づき、一意な要素のみを含むストリームを返します。 |
| `limit(n)` | **要素の制限**を行います。ストリームの先頭から指定された数 *n* までの要素のみを含むストリームを返します。 |
| `skip(n)` | **要素のスキップ**を行います。ストリームの最初の *n* 個の要素を破棄し、残りの要素を含むストリームを返します。 |
| `sorted()` / `sorted(Comparator)` | **要素のソート**を行います。自然順序または指定された`Comparator`に基づいてソートされたストリームを返します。 |
| `peek(Consumer)` | **要素の確認**（デバッグなど）を行います。ストリームの要素に対する処理を実行しますが、ストリーム自体には変化は発生せず、同じストリームを返します。 |

-----

## 2\. 終端操作（Streamを閉じる処理）🔚

終端操作はStreamを受け取り、**最終的な結果（プリミティブ値、コレクション、副作用など）を返す**ため、**パイプラインの最後に一度だけ実行**され、Streamの処理を終了させます。

| API（メソッド） | 説明 |
| :--- | :--- |
| `forEach(Consumer)` | **各要素に対する処理**を行います。ストリーム内の各要素に指定されたラムダ式（戻り値なし）を実行します。 |
| `collect(Collector)` | **結果の収集**を行います。ストリームの要素を`List`、`Set`、`Map`などの**コレクションに変換**したり、文字列に結合したりします。最もよく使われる終端操作の一つです。 |
| `reduce(BinaryOperator)` / `reduce(identity, accumulator)` | **要素の集約**を行います。ストリームの要素を結合し、一つの結果にまとめます（例：合計値の計算）。 |
| `min(Comparator)` / `max(Comparator)` | **最小値/最大値の取得**を行います。指定された`Comparator`に基づき、ストリーム内の最小要素または最大要素を返します。結果は`Optional`でラップされます。 |
| `count()` | **要素数の取得**を行います。ストリームの要素数を`long`で返します。 |
| `anyMatch(Predicate)` / `allMatch(Predicate)` / `noneMatch(Predicate)` | **条件の一致判定**を行います。ストリーム内のいずれかの要素/全ての要素/要素なしが指定された条件に一致するかを`boolean`で返します。 |
| `findFirst()` / `findAny()` | **要素の検索**を行います。ストリームの最初の要素、または任意の要素を`Optional`で返します。 |

-----

これらの操作を組み合わせることで、従来の`for`ループなどを使うよりも**簡潔で宣言的**なコードで配列やコレクションの処理を記述できます。

この動画では、Java Stream APIの基本的な概念と、`filter`や`map`などの基本的な中間操作について分かりやすく解説されています。

[ストリームとは何かをわかりやすく解説！【Java応用講座】1-1 ストリームの基本](https://www.google.com/search?q=https://www.youtube.com/watch%3Fv%3DmJYU9eVtW05)


はい、Java Stream API の中間操作と終端操作について、具体的なコード例をいくつか紹介します。

-----

## 🚀 具体的なコード例 (Java Stream API)

### 1\. `filter` と `map` の組み合わせ (中間操作)

  * **目的:** 偶数のみを抽出し（`filter`）、その偶数を2倍に変換する（`map`）。

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

-----

### 2\. `distinct` と `sorted` (中間操作)

  * **目的:** 重複する要素を除去し（`distinct`）、降順にソートする（`sorted`）。

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

-----

### 3\. `reduce` (終端操作)

  * **目的:** ストリームの全要素を合計する（集約）。

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

-----

### 4\. `anyMatch` (終端操作)

  * **目的:** 特定の条件を満たす要素が一つでもあるかを確認する。

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

これらの例が、`filter`以外の操作や、中間操作・終端操作の組み合わせ方について理解を深める助けになれば幸いです。

他に特定の操作や、配列に対するStreamの使い方など、知りたい具体的な処理があればお気軽にお尋ねください。