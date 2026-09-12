# モダンJava機能（record / switch式 / ラムダ式）とは？

Java 14以降、ボイラープレートコードを削減し、分岐処理をより簡潔に書くための新機能が次々と追加されています。既存コードが古いJavaバージョンを前提にしている場合、これらの新機能を知らないと**逆に「なぜ最新のコードだけ書き方が違うのか」**が分からなくなります。

### 🏭 主な新機能の全体像

1. **`record`（レコード型）**
   - `getter`・`equals`・`hashCode`・`toString`を自動生成する**不変データ保持クラス**。DTOやValue Objectに最適。
2. **`switch`式＆パターンマッチング**
   - 従来の`switch`文と違い、**値を返せる**・**breakが不要**・**型に応じた分岐（`instanceof`パターンマッチング）**ができる。
3. **ラムダ式・匿名クラス**
   - 実装クラスをわざわざ定義せずに、**その場で処理を渡す**ための簡潔な記法。

---

## ✨ 主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **`record`の不変性** | フィールドは自動的に`private final`になり、`setter`は生成されない。 |
| **`switch`式の網羅性チェック** | `enum`や`sealed`型を対象にすると、コンパイラが**全パターンの網羅**をチェックしてくれる。 |
| **`instanceof`パターンマッチング** | キャストを別途書かずに、型チェックと変数への束縛を同時に行える。 |
| **ラムダ式は匿名クラスの簡略記法** | `Runnable`や`Comparator`のような**関数型インターフェース**を実装する場合に、クラス定義を省略できる。 |

---

## 📝 代表的な構文

| 種類 | 構文 | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **record** | `record Point(int x, int y) {}` | 不変データクラスを1行で定義する。 | |
| **switch式** | `String s = switch (x) { case 1 -> "A"; default -> "B"; };` | 値を返す新しい`switch`。 | |
| **パターンマッチング** | `if (obj instanceof String s) { ... }` | 型チェックと変数代入を同時に行う。 | |
| **ラムダ式** | `(a, b) -> a + b` | 引数と処理本体だけを書く関数型インターフェースの実装。 | |

### 🛠️ コード例

```java
public class ModernJavaExample {
    record Point(int x, int y) {} // getter/equals/hashCode/toStringが自動生成される

    public static void main(String[] args) {
        Point p = new Point(1, 2);
        System.out.println(p); // Point[x=1, y=2] (自動生成されたtoString)

        Object obj = "Hello";
        // instanceofパターンマッチング：型チェックとキャストを同時に行う
        if (obj instanceof String s && s.length() > 3) {
            System.out.println(s.toUpperCase()); // HELLO
        }

        int day = 3;
        // switch式：値を返し、breakが不要
        String dayName = switch (day) {
            case 1 -> "月";
            case 2 -> "火";
            case 3 -> "水";
            default -> "不明";
        };
        System.out.println(dayName); // 水
    }
}
```

既存コードで匿名クラス（`new Runnable() { ... }`のような書き方）を見つけたら、それを**ラムダ式に置き換えられないか**を考えると、モダンな書き方への理解が深まります。
