## 📄ラムダ式・匿名クラスで使用する構文・インターフェース一覧

**_概要_**
ラムダ式・匿名クラス・標準の関数型インターフェースを、構文・インターフェース・関連構文の3カテゴリーで一覧化します。

---

## 1. ✍️ 記法の比較

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| ☠️ **使わない** | 匿名クラス（`new Interface() { ... }`） | フィールドを持たせたい・複数の抽象メソッドを実装したい等の特殊な場合を除き、ラムダ式に置き換えられることが多い。 |
| 🔥 **よく使う** | ラムダ式（`(引数) -> 処理`） | 関数型インターフェースの実装として最も簡潔で頻用される記法。 |
| 🔥 **よく使う** | メソッド参照（`ClassName::methodName`） | 既存メソッドをそのまま渡す、ラムダ式よりさらに簡潔な記法。 |
| 💡 **たまに使う** | コンストラクタ参照（`ClassName::new`） | コンストラクタをそのまま関数型インターフェースとして渡す。 |

---

## 2. 🔧 標準の関数型インターフェース（`java.util.function`）

| 優先度 | インターフェース | 抽象メソッド | 説明 |
| :--- | :--- | :--- | :--- |
| 🔥 **よく使う** | `Function<T, R>` | `R apply(T t)` | 引数を受け取り、値を返す。`map()`の引数等で頻出。 |
| 🔥 **よく使う** | `Predicate<T>` | `boolean test(T t)` | 引数を受け取り、`boolean`を返す。`filter()`の引数等で頻出。 |
| 🔥 **よく使う** | `Consumer<T>` | `void accept(T t)` | 引数を受け取り、何も返さない。`forEach()`の引数等で頻出。 |
| 💡 **たまに使う** | `Supplier<T>` | `T get()` | 引数を取らず、値を返す。`orElseGet()`等の遅延評価で使われる。 |
| 💡 **たまに使う** | `Runnable` | `void run()` | 引数も戻り値もない処理。スレッド実行やイベントハンドラでよく使われる。 |
| 💡 **たまに使う** | `Comparator<T>` | `int compare(T a, T b)` | 2つの値の大小関係を返す。ソート処理の比較ロジックに使われる。 |
| ☠️ **使わない** | `BiFunction<T, U, R>` | `R apply(T t, U u)` | 引数を2つ取るバージョンの`Function`（`Map.merge()`等の一部APIで使用）。 |

---

## 3. 🔤 関連構文（var との併用）

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `var list = new ArrayList<String>();` | ローカル変数の型推論（Java 10以降）。ラムダ式と組み合わせて`var`でコレクションを宣言することも多い。 |

---

## 🚀 具体的なコード例

### 1. メソッド参照によるさらなる簡潔化

```java
import java.util.List;
import java.util.function.Function;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> names = List.of("taro", "hanako");

        // ラムダ式
        Function<String, String> toUpperLambda = s -> s.toUpperCase();

        // メソッド参照（既存メソッドをそのまま渡す）
        Function<String, String> toUpperRef = String::toUpperCase;

        names.forEach(name -> System.out.println(toUpperRef.apply(name)));
        // 出力: TARO / HANAKO
    }
}
```

### 2. 匿名クラスでなければならないケース（独自フィールドを持つ場合）

- **目的:** カウント機能を持つ`Runnable`を作る（ラムダ式では外部の状態を保持できないため匿名クラスが必要）。

```java
public class AnonymousClassNeededExample {
    interface Counter {
        void increment();
        int getCount();
    }

    public static void main(String[] args) {
        // ラムダ式は独自フィールドを持てないため、状態を持つ実装には匿名クラス（またはローカルクラス）を使う
        Counter counter = new Counter() {
            private int count = 0; // 独自フィールド

            @Override
            public void increment() {
                count++;
            }

            @Override
            public int getCount() {
                return count;
            }
        };

        counter.increment();
        counter.increment();
        System.out.println(counter.getCount()); // 2
    }
}
```

### 3. `Predicate` を使ったフィルタリング

```java
import java.util.List;
import java.util.function.Predicate;

public class PredicateExample {
    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);

        Predicate<Integer> isEven = n -> n % 2 == 0;

        numbers.stream()
            .filter(isEven)
            .forEach(System.out::println); // 2, 4, 6
    }
}
```
