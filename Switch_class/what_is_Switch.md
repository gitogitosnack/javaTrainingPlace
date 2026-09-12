# switch式・パターンマッチングとは？

`switch`式は、Java 14で正式導入された**値を返せる新しい`switch`構文**です。従来の`switch`文（`case`ごとに`break`が必要）とは異なり、`->`構文を使うことで**breakが不要**になり、`switch`自体が**式として値を返せる**ようになりました。

さらにJava 16以降の`instanceof`パターンマッチング、Java 21の`switch`パターンマッチングにより、**型に応じた分岐処理をキャストなしで簡潔に書ける**ようになっています。

### 🏭 イメージ：「値を返す自動販売機」

1. **従来の`switch`文（レバー式の機械）**
   - `case`ごとに処理を書き、`break`を忘れると**意図せず次の`case`に処理が流れ落ちる（フォールスルー）**バグの温床だった。
2. **`switch`式（ボタンを押すと商品が出てくる自販機）**
   - `case 値 -> 結果;`の形で、**その場で値を返す**。`break`が不要でフォールスルーの心配がない。
3. **パターンマッチング（中身を見て仕分ける機械）**
   - `instanceof`や`switch`の`case`で、**型チェックとキャスト・変数への束縛を同時に**行える。

---

## ✨ 主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **`break`が不要** | `->`構文はフォールスルーが起きず、各`case`は独立して評価される。 |
| **式として値を返せる** | `switch`の結果を直接変数に代入できる（`String s = switch (x) { ... };`）。 |
| **網羅性チェック** | `enum`や`sealed`インターフェースを対象にすると、コンパイラが**全パターンを網羅しているか**をチェックしてくれる。 |
| **`instanceof`パターンマッチング** | `if (obj instanceof String s)`のように、型チェックと同時にキャスト済み変数を得られる。 |
| **`yield`によるブロック内の戻り値** | `case`の処理が複数行になる場合、`{ }`ブロック内で`yield`を使って値を返す。 |

---

## 📝 代表的な構文

| 種類 | 構文 | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **switch式** | `String s = switch (x) { case 1 -> "A"; default -> "B"; };` | 値を返す新しい`switch`。 | |
| **複数行のcase** | `case 1 -> { ...; yield 値; }` | ブロック内で処理を行い、`yield`で値を返す。 | |
| **パターンマッチング** | `if (obj instanceof String s) { ... }` | 型チェックと変数代入を同時に行う。 | |
| **switchパターンマッチング** | `case String s -> ...;` | `switch`式の中で型に応じた分岐を行う（Java 21以降）。 | |

### 🛠️ コード例

```java
public class SwitchExample {
    public static void main(String[] args) {
        int day = 3;

        // switch式：値を返し、breakが不要
        String dayName = switch (day) {
            case 1 -> "月";
            case 2 -> "火";
            case 3 -> "水";
            default -> "不明";
        };
        System.out.println(dayName); // 水

        Object obj = "Hello";
        // instanceofパターンマッチング：型チェックとキャストを同時に行う
        if (obj instanceof String s && s.length() > 3) {
            System.out.println(s.toUpperCase()); // HELLO
        }
    }
}
```

既存コードで`switch`文に`break`が並んでいる場合、フォールスルーを意図していないなら`->`構文の`switch`式に書き換えることで、意図しないフォールスルーバグを防げます。
