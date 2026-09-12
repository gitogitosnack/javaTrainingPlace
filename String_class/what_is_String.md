# String / StringBuilder とは？

`String`はJavaの**文字列を表す不変（immutable）クラス**です。一方`StringBuilder`は**可変（mutable）**な文字列を扱うクラスで、大量の文字列連結を行う場面で使い分けます。

既存コードのロジック解析では、この**不変・可変の違い**を理解しているかどうかで、「なぜここで`StringBuilder`が使われているのか」「なぜループの中で`+=`をしていないのか」といった設計意図が読み取れるようになります。

### 🏭 イメージ：「一枚岩の石板 vs 継ぎ足せる粘土」

1. **`String`（石板）**
   - 一度刻んだ文字は変更できない。`str += "a"`は、**新しい石板を作り直している**のと同じ（元の`str`は変更されない）。
2. **`StringBuilder`（粘土）**
   - 内部バッファに直接**継ぎ足していく**ため、大量連結でも新しいオブジェクトを都度作らず高速。

---

## ✨ 主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **`String`の不変性** | 生成後は内容を変更できない。文字列連結（`+`）のたびに**新しいオブジェクトが生成**される。 |
| **`StringBuilder`の可変性** | `append()`などで**同一インスタンスを直接変更**する。ループでの連結に強い。 |
| **スレッドセーフ性** | `StringBuilder`は非同期。同期版が必要な場合は`StringBuffer`（現在はほぼ使われない）。 |
| **文字列プール** | リテラルで作った`String`（`"abc"`）は**文字列プール**で再利用され、`new String("abc")`は毎回新規生成される。 |

---

## 📝 代表的なメソッド

| 種類 | メソッド | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **String** | `equals(Object o)` | 内容が等しいか比較する（`==`は**参照比較**なので使わない）。 | `"abc".equals(str)` |
| **String** | `String.join(delimiter, elements)` | 複数要素を区切り文字で連結する。 | `String.join(",", list)` |
| **String** | `formatted(Object... args)` | フォーマット済み文字列を生成する（Java 15以降）。 | `"%s円".formatted(1000)` |
| **StringBuilder** | `append(x)` | 末尾に追加する（メソッドチェーン可）。 | `sb.append("a").append("b")` |

### 🛠️ コード例

```java
public class StringBuilderExample {
    public static void main(String[] args) {
        // NG例：ループ内でStringを+=すると、毎回新しいオブジェクトが生成され非効率
        // String bad = "";
        // for (int i = 0; i < 10000; i++) { bad += i; }

        // OK例：StringBuilderで同一バッファに継ぎ足す
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i).append(",");
        }
        // 末尾の余分なカンマを削除
        sb.setLength(sb.length() - 1);

        System.out.println(sb.toString()); // 0,1,2,3,4
    }
}
```

大量データを扱うバッチ処理などで文字列を組み立てる箇所は、まず`StringBuilder`が使われているか、非効率な`+=`連結になっていないかを確認するのが改修のポイントです。
