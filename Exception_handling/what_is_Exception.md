# 例外処理（Exception Handling）とは？

例外処理は、プログラム実行中に発生した**予期しない事態（エラー）**を捕捉し、適切に対処するための仕組みです。既存コードの改修では、「どこで例外が発生し」「どう捕捉され」「どこまで伝播するのか」を追えるかどうかが、障害調査のスピードを大きく左右します。

### 🏭 イメージ：「エラーの伝言ゲーム」

1. **例外の発生（`throw`）**
   - 処理中に問題が起きると、例外オブジェクトが**投げられる**。
2. **例外の伝播（呼び出し元へ遡る）**
   - 捕捉（`catch`）されなければ、例外は**呼び出し元へ、さらにその呼び出し元へ**と遡っていく。
3. **例外の捕捉（`catch`）**
   - どこかの層で`catch`されると、そこで処理が止まり、エラーハンドリングが行われる。
4. **原因の連鎖（`cause`）**
   - 別の例外をラップして再送出する際、元の例外を**`cause`として保持**することで、根本原因を追跡できる。

---

## ✨ 例外の分類

| 分類 | 代表クラス | 特徴 |
| :--- | :--- | :--- |
| **チェック例外** | `Exception`（`RuntimeException`を除く） | コンパイラが**`throws`宣言または`try-catch`を強制**する。回復可能なエラー向け（例: `IOException`）。 |
| **非チェック例外** | `RuntimeException`とそのサブクラス | 宣言・捕捉が**強制されない**。プログラムのバグに起因することが多い（例: `NullPointerException`）。 |
| **エラー** | `Error`とそのサブクラス | JVMレベルの致命的な問題（例: `OutOfMemoryError`）。**アプリコードで捕捉すべきではない**。 |

業務ロジックで独自例外を作る際は、「呼び出し元に回復を強制したいか」で**チェック例外／非チェック例外（多くは`RuntimeException`継承）**を使い分けます。

---

## 📝 代表的な構文・クラス

| 種類 | 構文・クラス | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **捕捉** | `try-catch-finally` | 例外の捕捉と、必ず実行する後処理を書く。 | |
| **リソース管理** | `try-with-resources` | `AutoCloseable`実装のリソースを**自動でクローズ**する。 | `try (var is = new FileInputStream(...))` |
| **送出** | `throw new XxxException(...)` | 例外を明示的に発生させる。 | |
| **連鎖** | `new XxxException(msg, cause)` | 元の例外を**原因（cause）**として保持しつつ再送出する。 | |

### 🛠️ コード例

```java
public class OrderService {
    static class OrderProcessingException extends RuntimeException {
        public OrderProcessingException(String message, Throwable cause) {
            super(message, cause); // 元の例外をcauseとして保持
        }
    }

    void processOrder(String orderId) {
        try {
            // 何らかの外部連携処理（失敗する可能性がある）
            callExternalApi(orderId);
        } catch (RuntimeException e) {
            // 低レイヤーの例外を、業務的な意味を持つ例外に変換して再送出
            throw new OrderProcessingException("注文処理に失敗しました: " + orderId, e);
        }
    }

    void callExternalApi(String orderId) {
        throw new RuntimeException("外部APIタイムアウト");
    }
}
```

障害調査では、スタックトレースの**「Caused by:」以下**が根本原因であることが多いため、`cause`が正しく引き継がれているかを確認する習慣が重要です。
