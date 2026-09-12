## 📄recordで使用する構文一覧

**_概要_**
`record`に関する構文を、基本定義・コンストラクタ・拡張の3カテゴリーで一覧化します。

---

## 1. 📦 基本定義

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `record Name(型 field1, 型 field2) {}` | 不変データクラスを定義する。`getter`（`field1()`）・`equals`・`hashCode`・`toString`が自動生成される。 |
| 🔥 **よく使う** | `instance.field1()` | フィールドへのアクセサ。通常のクラスの`getField1()`とは**命名規則が異なる**点に注意。 |
| 💡 **たまに使う** | `record`のインターフェース実装 | `record Name(...) implements SomeInterface {}`のように、インターフェースを実装できる（クラスの継承はできない）。 |

---

## 2. 🏗️ コンストラクタ

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | コンパクトコンストラクタ | `record Name(...) { Name { /* 検証 */ } }`の形式で、生成時の**バリデーション**を追加する。引数の再代入も可能。 |
| 💡 **たまに使う** | 通常のコンストラクタ（正規コンストラクタ）のオーバーライド | 全フィールドを引数に取る形をそのまま書き、独自処理を追加する（コンパクトコンストラクタより冗長）。 |
| 💡 **たまに使う** | 追加のコンストラクタ（オーバーロード） | 別の引数構成のコンストラクタを追加し、内部で正規コンストラクタ（`this(...)`）を呼び出す。 |

---

## 3. 🔧 拡張（静的メンバー・独自メソッド）

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | 独自メソッドの追加 | `record`本体に、フィールドを使った計算プロパティ等の通常メソッドを追加できる。 |
| 💡 **たまに使う** | 静的メソッド・静的フィールド | `record`にも`static`な生成用ファクトリメソッド（`of(...)`等）を定義できる。 |

---

## 🚀 具体的なコード例

### 1. コンパクトコンストラクタによるバリデーション

- **目的:** 不正な値（マイナスの金額）でインスタンスが生成されないようにする。

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
        // Money invalid = new Money(-1); // 実行時にIllegalArgumentException
    }
}
```

### 2. 独自メソッド・静的ファクトリメソッドの追加

- **目的:** 座標間の距離計算メソッドと、原点を生成する静的メソッドを追加する。

```java
public class RecordMethodExample {
    record Point(double x, double y) {
        // 静的ファクトリメソッド
        static Point origin() {
            return new Point(0, 0);
        }

        // 独自の計算メソッド（フィールドの値を使った派生プロパティ）
        double distanceFromOrigin() {
            return Math.sqrt(x * x + y * y);
        }
    }

    public static void main(String[] args) {
        Point p = new Point(3, 4);
        System.out.println(p.distanceFromOrigin()); // 5.0
        System.out.println(Point.origin());          // Point[x=0.0, y=0.0]
    }
}
```

### 3. インターフェースの実装

```java
public class RecordInterfaceExample {
    interface Shape {
        double area();
    }

    record Circle(double radius) implements Shape {
        @Override
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    public static void main(String[] args) {
        Shape shape = new Circle(2);
        System.out.println(shape.area()); // 12.56...
    }
}
```
