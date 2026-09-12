# java.time API（日付・時間）とは？

`java.time`パッケージは、Java 8で導入された**新しい日付・時間API**です。従来の`Date`や`Calendar`が抱えていた「可変（スレッドセーフでない）」「月が0始まり」といった問題点を解消し、**不変（immutable）で直感的**に扱えるようになっています。

既存の業務システムでは、新旧のAPIが混在していることも多く、**どのクラスがどの用途か**を見分けられると読解がスムーズになります。

### 🏭 イメージ：「用途別の時計」

1. **`LocalDate`（カレンダー）**
   - 日付のみ（年月日）を表す。時刻情報を持たない。
2. **`LocalTime`（腕時計）**
   - 時刻のみ（時分秒）を表す。日付情報を持たない。
3. **`LocalDateTime`（カレンダー付き腕時計）**
   - 日付＋時刻を表す。**タイムゾーンを持たない**。
4. **`ZonedDateTime`（世界時計）**
   - 日付＋時刻＋**タイムゾーン**を表す。
5. **`Instant`（世界標準の基準点）**
   - UTC基準の1点の時刻（エポック秒）を表す。ログのタイムスタンプなどに使われる。

---

## ✨ 主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **不変（immutable）** | すべての操作は**新しいインスタンス**を返す（`plusDays()`等は元のインスタンスを変更しない）。 |
| **スレッドセーフ** | 不変であるため、複数スレッドから安全に共有できる（`SimpleDateFormat`のような問題が起きない）。 |
| **直感的なAPI** | 月が1始まり、メソッド名が明確（`plusDays`, `isBefore`等）。 |
| **フォーマット/パースの分離** | `DateTimeFormatter`が文字列⇔日時オブジェクトの変換を担当する。 |

---

## 📝 代表的なメソッド

| 種類 | メソッド | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **生成** | `LocalDate.now()` / `LocalDateTime.now()` | 現在日時を取得する。 | |
| **変換** | `DateTimeFormatter.ofPattern(pattern)` | フォーマットパターンを定義する。 | `"yyyy/MM/dd"` |
| **計算** | `plusDays(n)` / `minusMonths(n)` | 日時を加算・減算した**新しいインスタンス**を返す。 | |
| **比較** | `isBefore()` / `isAfter()` | 日時の前後関係を判定する。 | |

### 🛠️ コード例

```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeExample {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(7); // 1週間後（新しいインスタンス）

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        System.out.println("本日: " + today.format(formatter));
        System.out.println("期限: " + deadline.format(formatter));

        if (deadline.isAfter(today)) {
            System.out.println("期限はまだ先です");
        }
    }
}
```

`java.time`は不変オブジェクトのため、`date.plusDays(1);`のように**戻り値を受け取らずに呼び出しても何も変わらない**という点は、既存コードのバグ調査でよく引っかかるポイントです。
