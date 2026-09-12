## 📄java.timeで使用するメソッド一覧

**_概要_**
`java.time`パッケージのメソッドを、生成・変換・計算・相互変換の4カテゴリーで一覧化します。

---

## 1. 🏭 生成系メソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `LocalDate.now()` / `LocalDateTime.now()` | 現在の日付・日時を取得する。 |
| 🔥 **よく使う** | `LocalDate.of(year, month, day)` | 指定した年月日でインスタンスを生成する。 |
| 🔥 **よく使う** | `LocalDate.parse(text, formatter)` | 文字列から日付を**解析（パース）**する。 |
| 💡 **たまに使う** | `Instant.now()` | UTC基準の現在時刻（エポック秒）を取得する。ログのタイムスタンプ等。 |

---

## 2. 🔄 変換系メソッド（フォーマット）

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `DateTimeFormatter.ofPattern(String pattern)` | 独自のフォーマットパターンを定義する。 |
| 🔥 **よく使う** | `date.format(formatter)` | 日付を**文字列に変換**する。 |
| 🔥 **よく使う** | `LocalDate.parse(text, formatter)` | 文字列を**日付オブジェクトに変換**する。 |

---

## 3. ➕ 計算系メソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `plusDays(n)` / `minusDays(n)` | 日数の加算・減算をした**新しいインスタンス**を返す。 |
| 💡 **たまに使う** | `plusMonths(n)` / `plusYears(n)` | 月・年単位の加算を行う。 |
| 🔥 **よく使う** | `isBefore(other)` / `isAfter(other)` | 前後関係を判定する。 |
| 💡 **たまに使う** | `Period.between(start, end)` | 2つの日付の**期間差（年・月・日）**を求める。 |
| 💡 **たまに使う** | `Duration.between(start, end)` | 2つの日時の**時間差（時・分・秒）**を求める。 |
| 💡 **たまに使う** | `ChronoUnit.DAYS.between(start, end)` | 指定単位での差分を数値として求める。 |

---

## 4. 🔁 旧API（Date/Calendar）との相互変換

改修対象のコードには旧APIが残っていることが多く、変換方法を知っておくと移行作業がスムーズになります。

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `Date.from(Instant instant)` | `java.time`から旧`Date`へ変換する。 |
| 💡 **たまに使う** | `date.toInstant()` | 旧`Date`から`Instant`へ変換する。 |

---

## 🚀 具体的なコード例

### 1. 文字列から日付への変換とバリデーション

- **目的:** 画面から受け取った文字列を`LocalDate`に変換する。

```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ParseExample {
    public static void main(String[] args) {
        String input = "2026/09/12";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        try {
            LocalDate date = LocalDate.parse(input, formatter);
            System.out.println("変換成功: " + date);
        } catch (DateTimeParseException e) {
            System.out.println("日付の形式が不正です: " + input);
        }
    }
}
```

### 2. `Period.between` による年齢計算

```java
import java.time.LocalDate;
import java.time.Period;

public class AgeCalculationExample {
    public static void main(String[] args) {
        LocalDate birthDate = LocalDate.of(1990, 5, 20);
        LocalDate today = LocalDate.now();

        int age = Period.between(birthDate, today).getYears();
        System.out.println("年齢: " + age + "歳");
    }
}
```
