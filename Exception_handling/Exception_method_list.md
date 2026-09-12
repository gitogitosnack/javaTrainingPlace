## 📄例外処理で使用する構文・メソッド一覧

**_概要_**
例外処理に関わる構文・メソッドを、捕捉・送出・情報取得・設計方針の4カテゴリーで一覧化します。

---

## 1. 🛡️ 例外の捕捉

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `try { } catch (XxxException e) { }` | 指定した例外型を捕捉する。 |
| 🔥 **よく使う** | `finally { }` | 例外の有無にかかわらず**必ず実行**される後処理（リソース解放等）。 |
| 🔥 **よく使う** | `try-with-resources` | `AutoCloseable`を実装したリソースを、ブロック終了時に**自動クローズ**する。 |
| 💡 **たまに使う** | `catch (IOException \| SQLException e)` | **複数の例外型**をまとめて1つの`catch`で捕捉する（マルチキャッチ）。 |
| ☠️ **使わない** | `catch (Exception e) { }`（空実装） | 例外を握りつぶす**アンチパターン**。原因の追跡が不可能になるため避ける。 |

---

## 2. 🚀 例外の生成・送出

| 優先度 | 構文 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `throw new XxxException("message")` | 明示的に例外を発生させる。 |
| 🔥 **よく使う** | `throws XxxException`（メソッド宣言） | チェック例外を**呼び出し元に処理を委譲**することを宣言する。 |
| 🔥 **よく使う** | `class MyException extends RuntimeException` | 独自の非チェック例外を定義する（業務では最も一般的）。 |
| 💡 **たまに使う** | `class MyException extends Exception` | 独自のチェック例外を定義する（呼び出し元に確実な対応を強制したい場合）。 |

---

## 3. 🔎 例外情報の取得

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `e.getMessage()` | 例外に設定されたメッセージを取得する。 |
| 🔥 **よく使う** | `e.getCause()` | この例外の**原因となった元の例外**を取得する。 |
| 💡 **たまに使う** | `e.printStackTrace()` | スタックトレースを標準エラー出力に表示する（本番コードではロガー経由が望ましい）。 |
| 💡 **たまに使う** | `e.getStackTrace()` | スタックトレースを配列として取得する（ログ整形等）。 |

---

## 4. 📐 設計方針（現場での使い分け）

| 優先度 | 方針 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | 独自例外は`RuntimeException`継承 | `throws`の宣言が連鎖せず、呼び出し側のコードが煩雑にならない。Spring等のフレームワークもこの方針が主流。 |
| 💡 **たまに使う** | 独自例外は`Exception`継承 | 「必ず対応してほしい」回復可能なエラーに限定して使う。 |

---

## 🚀 具体的なコード例

### 1. `try-with-resources` によるリソース自動クローズ

- **目的:** ファイル読み込み時に、例外発生時も確実にリソースをクローズする。

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryWithResourcesExample {
    public static void main(String[] args) {
        // 括弧内で宣言したリソースは、ブロック終了時に自動でclose()される
        try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("ファイル読み込みに失敗しました: " + e.getMessage());
        }
    }
}
```

### 2. 例外の連鎖（原因の保持）

```java
public class ChainedExceptionExample {
    static class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    void fetchData() {
        try {
            // DBアクセスなどを想定
            throw new IllegalStateException("コネクションタイムアウト");
        } catch (IllegalStateException e) {
            // 低レイヤーの例外情報を保持したまま、業務的な例外に変換
            throw new DataAccessException("データ取得に失敗しました", e);
        }
    }
}
```
