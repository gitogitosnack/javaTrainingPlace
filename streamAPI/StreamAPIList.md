Java Stream APIで使用される**メソッドの全て**を、機能ごとに一覧で提供します。

Stream APIのメソッドは、大きく以下の3つのカテゴリーに分類されます。

1.  **ストリーム生成メソッド**: ストリームを作成します。
2.  **中間操作メソッド**: データを加工・変換・フィルタリングし、**新しいストリーム**を返します（遅延実行されます）。
3.  **終端操作メソッド**: ストリーム処理を**実行**し、結果（コレクション、値、副作用など）を返します。

---

## 1. 🏭 ストリーム生成メソッド

| メソッド | 説明 |
| :--- | :--- |
| `stream()` | **`Collection`**（`List`, `Set`など）から**順次ストリーム**を生成します。 |
| `parallelStream()` | **`Collection`**から**並列ストリーム**を生成します。 |
| `Arrays.stream(array)` | **配列**からストリームを生成します。 |
| `Stream.of(T... values)` | 指定された**要素**からストリームを生成します。 |
| `Stream.empty()` | 要素を含まない**空のストリーム**を生成します。 |
| `Stream.generate(Supplier<T> s)` | 指定された`Supplier`を使って**無限ストリーム**を生成します。 |
| `Stream.iterate(T seed, UnaryOperator<T> f)` | **初期値**と**適用関数**を指定して**無限ストリーム**を生成します。 |
| `IntStream.range(int start, int end)` | `start`（含む）から`end`（含まない）までの**整数シーケンス**を生成します。 |
| `IntStream.rangeClosed(int start, int end)` | `start`から`end`まで**両方を含む**整数シーケンスを生成します。 |

---

## 2. ⚙️ 中間操作メソッド (Intermediate Operations)

中間操作はメソッドチェーンで繋げることができ、**遅延実行**されます。

### 🔄 変換・加工

| メソッド | 説明 |
| :--- | :--- |
| `map(Function<T, R> mapper)` | 各要素を、指定された関数（`mapper`）で**別の型や値に変換**します。 |
| `flatMap(Function<T, Stream<R>> mapper)` | 要素を**ストリームに変換**し、そのストリームの要素を**単一ストリームに平坦化**します（ネストされた構造を解消）。 |
| `mapToInt / mapToLong / mapToDouble` | 要素をそれぞれプリミティブ型の`IntStream`、`LongStream`、`DoubleStream`に変換します。 |

### 🔍 フィルタリング・選別

| メソッド | 説明 |
| :--- | :--- |
| `filter(Predicate<T> predicate)` | 指定された条件（`predicate`）に**合致する要素のみ**を抽出します。 |
| `distinct()` | ストリーム内の**重複する要素を取り除きます**（`Object.equals(Object)`に基づいて比較）。 |

### 📏 制限・スキップ

| メソッド | 説明 | |
| :--- | :--- | :--- |
| `limit(long maxSize)` | ストリームの要素を**先頭から指定された数**に制限します。 |
| `skip(long n)` | ストリームの要素の**先頭から指定された数**をスキップします。 |

### 🔀 並び替え・順序

| メソッド | 説明 |
| :--- | :--- |
| `sorted()` | 要素の**自然順序**（`Comparable`）でソートします。 |
| `sorted(Comparator<T> comparator)` | 指定された`Comparator`でソートします。 |
| `peek(Consumer<T> action)` | ストリームの各要素が処理される際に、**副作用を伴う操作**（デバッグやログ出力など）を実行します。ストリーム自体は変更しません。 |

---

## 3. 🎯 終端操作メソッド (Terminal Operations)

終端操作は**一度だけ**実行でき、パイプラインを終了させます。

### 📊 収集・集約

| メソッド | 説明 |
| :--- | :--- |
| `collect(Collector<T, A, R> collector)` | 処理結果を**`List`、`Set`、`Map`**などの**新しいコレクションに集約**します。 (`Collectors.toList()`, `Collectors.toMap()`などを使用) |
| `toList()` | (Java 16以降) 処理結果を**不変の`List`**に集約します。 |
| `toArray()` | 要素を**配列**として返します。 |
| `reduce(T identity, BinaryOperator<T> accumulator)` | ストリームの要素を**結合**し、単一の結果にまとめます。 |
| `count()` | ストリーム内の**要素の総数**を返します。 |
| `min(Comparator<T> comparator)` | 指定された`Comparator`で**最小の要素**を返します（`Optional`でラップ）。 |
| `max(Comparator<T> comparator)` | 指定された`Comparator`で**最大の要素**を返します（`Optional`でラップ）。 |

### 🔎 検索・マッチング

| メソッド | 説明 |
| :--- | :--- |
| `findFirst()` | ストリーム内の**最初の要素**を返します（`Optional`でラップ）。 |
| `findAny()` | ストリーム内の**任意の要素**を返します（`Optional`でラップ。並列ストリームで効率的）。 |
| `anyMatch(Predicate<T> predicate)` | **いずれかの要素**が条件に合致するかどうかを返します（`boolean`）。 |
| `allMatch(Predicate<T> predicate)` | **すべての要素**が条件に合致するかどうかを返します（`boolean`）。 |
| `noneMatch(Predicate<T> predicate)` | **どの要素も**条件に合致しないかどうかを返します（`boolean`）。 |

### 💥 副作用（出力・処理実行）

| メソッド | 説明 |
| :--- | :--- |
| `forEach(Consumer<T> action)` | ストリーム内の各要素に対して**指定された操作を実行**します（戻り値なし）。 |
| `forEachOrdered(Consumer<T> action)` | **ストリームの定義された順序**で、各要素に対して操作を実行します。 |

### 📦 プリミティブ型ストリーム（`IntStream`, `LongStream`, `DoubleStream`）の特別な終端操作

| メソッド | 説明 |
| :--- | :--- |
| `sum()` | 全要素の**合計**を計算します。 |
| `average()` | 全要素の**平均**を計算します（`OptionalDouble`でラップ）。 |
| `summaryStatistics()` | `count`, `sum`, `min`, `max`, `average`を**一度に集計**した統計情報を返します。 |

これらのメソッドを組み合わせて、JavaのStream APIによる強力なデータ処理パイプラインを構築します。