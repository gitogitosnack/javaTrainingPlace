## 📄String / StringBuilderで使用するメソッド一覧

**_概要_**
文字列操作でよく使われる`String`・`StringBuilder`・`Apache Commons StringUtils`のメソッドを一覧で提供します。

---

## 1. 🔤 String系メソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `equals(Object o)` | 内容の等価性を比較する。 |
| 💡 **たまに使う** | `equalsIgnoreCase(String s)` | 大文字小文字を区別せず比較する。 |
| 🔥 **よく使う** | `trim()` / `strip()` | 前後の空白を除去する（`strip()`はUnicode対応でJava11以降推奨）。 |
| 🔥 **よく使う** | `split(String regex)` | 正規表現で文字列を分割し配列で返す。 |
| 🔥 **よく使う** | `String.join(CharSequence delimiter, ...)` | 複数要素を区切り文字で連結する。 |
| 💡 **たまに使う** | `String.format(...)` / `formatted(...)` | プレースホルダーを使ったフォーマット済み文字列を生成する。 |
| 💡 **たまに使う** | `isBlank()` | 空文字または空白のみかを判定する（Java 11以降）。 |
| ☠️ **使わない** | `isEmpty()` | 長さ0かのみ判定（空白のみの文字列は`false`になる点に注意）。 |
| 💡 **たまに使う** | `repeat(int count)` | 文字列をn回繰り返す（Java 11以降）。 |
| 💡 **たまに使う** | `lines()` | 改行で分割したStreamを返す（Java 11以降）。 |
| 🔥 **よく使う** | `substring(int begin, int end)` | 部分文字列を取り出す。 |
| 🔥 **よく使う** | `replace(CharSequence target, CharSequence replacement)` | 文字列を置換する。 |

---

## 2. 🧱 StringBuilder系メソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `append(x)` | 末尾に追加する（メソッドチェーン可）。 |
| 💡 **たまに使う** | `insert(int offset, x)` | 指定位置に挿入する。 |
| 💡 **たまに使う** | `delete(int start, int end)` | 指定範囲を削除する。 |
| ☠️ **使わない** | `reverse()` | 文字列を反転する（業務で使う場面は稀）。 |
| 🔥 **よく使う** | `toString()` | `String`に変換する（最終的に必ず呼ぶ）。 |
| 💡 **たまに使う** | `setLength(int newLength)` | 長さを切り詰める（末尾の余分な区切り文字削除等）。 |

---

## 3. 🧰 文字列ユーティリティ（Apache Commons `StringUtils`）

現場では標準の`String`メソッドだけでなく、`null`安全なユーティリティが併用されます。

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `StringUtils.isEmpty(str)` / `isNotEmpty(str)` | `null`または空文字かを**NPEなしで**判定する。 |
| 🔥 **よく使う** | `StringUtils.isBlank(str)` / `isNotBlank(str)` | `null`・空文字・空白のみを**まとめて**判定する。 |
| 💡 **たまに使う** | `StringUtils.defaultIfBlank(str, defaultVal)` | 空白なら**デフォルト値**を返す。 |
| 💡 **たまに使う** | `StringUtils.isNumeric(str)` | 数字のみで構成されるか判定する。 |

---

## 🚀 具体的なコード例

### 1. `split` と `join` の組み合わせ

- **目的:** CSV形式の文字列を配列に分解し、別の区切り文字で再構成する。

```java
public class SplitJoinExample {
    public static void main(String[] args) {
        String csv = "田中,佐藤,鈴木";
        String[] names = csv.split(",");

        String tsv = String.join("\t", names);
        System.out.println(tsv); // 田中	佐藤	鈴木
    }
}
```

### 2. `StringUtils.isBlank` によるnull安全な入力チェック

```java
import org.apache.commons.lang3.StringUtils;

public class BlankCheckExample {
    public static void main(String[] args) {
        String input = null;

        // NG例: input.isEmpty() だとNullPointerExceptionが発生する
        if (StringUtils.isBlank(input)) {
            System.out.println("入力値が未設定です");
        }
    }
}
```
