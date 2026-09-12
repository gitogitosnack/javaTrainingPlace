## 📄switch式・パターンマッチングで使用する構文一覧

**_概要_**
`switch`式・`instanceof`パターンマッチングに関する構文を、基本構文・戻り値・パターンマッチングの3カテゴリーで一覧化します。

---

## 1. 🔀 switch式の基本構文

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `switch (x) { case A -> 結果; default -> 結果; }` | `->`構文の`switch`式。**breakが不要**で、値を返せる。 |
| 💡 **たまに使う** | `case A, B -> 結果;` | 複数の値を1つの`case`にまとめる（カンマ区切り）。 |
| ☠️ **使わない** | 従来の`switch`文（`case A: ... break;`） | フォールスルーのバグ要因になりやすいため、新規実装では`->`構文が推奨される。 |

---

## 2. 📤 戻り値の扱い

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `変数 = switch (x) { ... };` | `switch`の結果をそのまま変数に代入する。 |
| 💡 **たまに使う** | `yield`（ブロック内での戻り値） | `case`の処理が複数行に渡る場合、`{ }`ブロック内で`yield`を使って値を返す。 |
| 💡 **たまに使う** | `enum`/`sealed`型を対象にした網羅性チェック | `default`を書かなくても、コンパイラが全パターンの網羅を保証してくれる。 |

---

## 3. 🎯 パターンマッチング

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `if (obj instanceof String s)` | 型チェックと同時に、キャスト済みの変数`s`を得る（Java 16以降）。 |
| 💡 **たまに使う** | `case String s ->` | `switch`式の中で型に応じた分岐と変数束縛を同時に行う（Java 21以降）。 |
| 💡 **たまに使う** | `case String s when s.isEmpty() ->` | パターンマッチングに追加条件（ガード）を付ける（Java 21以降）。 |
| 💡 **たまに使う** | `record`パターン（`case Point(int x, int y) ->`） | `record`のフィールドを分解しながらマッチングする（Java 21以降）。 |

---

## 🚀 具体的なコード例

### 1. `yield` を使った複数行の case

- **目的:** 曜日に応じて、複数の処理を経てからメッセージを返す。

```java
public class YieldExample {
    public static void main(String[] args) {
        int day = 6;

        String message = switch (day) {
            case 6, 7 -> {
                String note = "休日";
                yield note + "なのでゆっくりしましょう";
            }
            default -> "平日なので仕事です";
        };

        System.out.println(message); // 休日なのでゆっくりしましょう
    }
}
```

### 2. switchパターンマッチングによる型別処理（Java 21以降）

- **目的:** `sealed interface`を使い、図形の種類ごとに面積計算を分岐する。網羅性もコンパイラがチェックしてくれる。

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
            // sealed型なので、これで全パターンを網羅していることをコンパイラが保証する
        };
    }

    public static void main(String[] args) {
        System.out.println(area(new Circle(2))); // 12.56...
        System.out.println(area(new Square(3))); // 9.0
    }
}
```

### 3. enumを使った網羅性チェック

```java
public class EnumSwitchExample {
    enum Status { NEW, IN_PROGRESS, DONE }

    static String label(Status status) {
        // Statusの全ての値をcaseで網羅しないとコンパイルエラーになる（defaultが無くてもOK）
        return switch (status) {
            case NEW -> "新規";
            case IN_PROGRESS -> "対応中";
            case DONE -> "完了";
        };
    }
}
```
