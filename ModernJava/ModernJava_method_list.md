## 📄モダンJava機能で使用する構文・インターフェース一覧

**_概要_**
`record`・`switch`式・ラムダ式・型推論など、モダンJavaの構文を一覧で提供します。

---

## 1. 📦 record関連

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `record Name(型 field1, 型 field2) {}` | 不変データクラスを定義する。`getter`（`field1()`のようにフィールド名そのままのメソッド）・`equals`・`hashCode`・`toString`が自動生成される。 |
| 💡 **たまに使う** | コンパクトコンストラクタ | `record Name(...) { Name { /* バリデーション */ } }`の形式で、生成時の検証処理を追加できる。 |
| 💡 **たまに使う** | 独自メソッドの追加 | `record`本体に通常のメソッドも定義できる（計算プロパティ等）。 |

---

## 2. 🔀 switch式・パターンマッチング

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `switch (x) { case A -> ...; }` | `->`構文の`switch`式。**breakが不要**で、値を返せる。 |
| 💡 **たまに使う** | `yield`（ブロック内での戻り値） | `case`の処理が複数行に渡る場合、`{ }`ブロック内で`yield`を使って値を返す。 |
| 🔥 **よく使う** | `if (obj instanceof String s)` | 型チェックと同時に、キャスト済みの変数`s`を得る（Java 16以降）。 |
| 💡 **たまに使う** | `switch`のパターンマッチング（`case String s ->`） | `switch`式の中で型に応じた分岐と変数束縛を同時に行う（Java 21以降）。 |

---

## 3. λ ラムダ式・関数型インターフェース

| 優先度 | インターフェース | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `Function<T, R>` | 引数を受け取り、値を返す（`apply()`）。`map()`の引数等で頻出。 |
| 🔥 **よく使う** | `Predicate<T>` | 引数を受け取り、`boolean`を返す（`test()`）。`filter()`の引数等で頻出。 |
| 🔥 **よく使う** | `Consumer<T>` | 引数を受け取り、何も返さない（`accept()`）。`forEach()`の引数等で頻出。 |
| 💡 **たまに使う** | `Supplier<T>` | 引数を取らず、値を返す（`get()`）。`orElseGet()`等の遅延評価で使われる。 |
| 💡 **たまに使う** | メソッド参照（`ClassName::methodName`） | 既存メソッドをそのままラムダとして渡す簡潔な記法。 |

---

## 4. 🔤 var（ローカル変数型推論）

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `var list = new ArrayList<String>();` | 右辺から型を推論し、宣言を簡潔にする（Java 10以降）。**フィールドや引数には使えない**。 |

---

## 🚀 具体的なコード例

### 1. `record` のコンパクトコンストラクタによるバリデーション

```java
public class RecordValidationExample {
    record Money(int amount) {
        Money { // コンパクトコンストラクタ
            if (amount < 0) {
                throw new IllegalArgumentException("金額は0以上である必要があります");
            }
        }
    }

    public static void main(String[] args) {
        Money money = new Money(1000); // OK
        // Money invalid = new Money(-1); // 実行時に例外
    }
}
```

### 2. switch式のパターンマッチング（Java 21以降）

```java
public class SwitchPatternExample {
    sealed interface Shape permits Circle, Square {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}

    static double area(Shape shape) {
        // 型に応じて分岐し、フィールドを直接取り出せる
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Square s -> s.side() * s.side();
        };
    }

    public static void main(String[] args) {
        System.out.println(area(new Circle(2))); // 12.56...
        System.out.println(area(new Square(3))); // 9.0
    }
}
```

### 3. 匿名クラスとラムダ式の比較

```java
import java.util.Comparator;

public class LambdaVsAnonymousExample {
    public static void main(String[] args) {
        // 匿名クラス（旧来の書き方）
        Comparator<String> byLengthOld = new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return Integer.compare(a.length(), b.length());
            }
        };

        // ラムダ式（同じ処理を簡潔に）
        Comparator<String> byLengthNew = (a, b) -> Integer.compare(a.length(), b.length());
    }
}
```
