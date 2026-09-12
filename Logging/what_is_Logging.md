# ログライブラリ（SLF4J/Logback）とは？

**SLF4J（Simple Logging Facade for Java）**は、ログ出力のための**共通インターフェース（ファサード）**です。実際にログを出力する処理は**Logback**（Spring Bootのデフォルト実装）などが担当します。

障害調査やバグ再現において、ログはコードの中で**「実際に何が起きたか」を追跡する最重要の手がかり**です。ログレベルの意味とMDCの仕組みを理解しておくと、調査効率が大きく上がります。

### 🏭 イメージ：「航海日誌」

1. **ログレベル（記録の重要度）**
   - 些細な情報（`DEBUG`）から重大な異常（`ERROR`）まで、**重要度に応じて記録を分ける**。
2. **プレースホルダーによる効率的な記録**
   - `log.info("ユーザー{}が処理を開始", userId)`のように、**文字列連結より効率的**に変数を埋め込める。
3. **MDC（航海日誌の航海番号）**
   - リクエストごとに割り振ったID（トレースID）を`MDC`に詰めることで、**1つの処理の流れをログ上で追跡**できるようにする。

---

## ✨ 主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **ファサードパターン** | SLF4Jは「窓口」であり、実装（Logback、Log4j2等）を後から差し替えられる。 |
| **ログレベル** | `TRACE < DEBUG < INFO < WARN < ERROR`の順に重要度が上がる。設定したレベル以上のみ出力される。 |
| **プレースホルダー構文** | `{}`を使うことで、ログレベルが無効な場合の**文字列連結コストを回避**できる。 |
| **MDC（Mapped Diagnostic Context）** | スレッドローカルにキー・値を保存し、ログ出力フォーマットに自動的に含められる（リクエストの追跡に利用）。 |

---

## 📝 代表的なメソッド

| 種類 | メソッド | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **出力** | `log.info("message {}", value)` | INFOレベルでログを出力する。 | |
| **例外ログ** | `log.error("message", exception)` | エラーメッセージとスタックトレースを併せて出力する。 | |
| **MDC** | `MDC.put(key, value)` | リクエストスコープの値をログコンテキストに登録する。 | |
| **ガード** | `log.isDebugEnabled()` | DEBUGログが有効かを事前確認する（重い処理の無駄な実行を避ける）。 | |

### 🛠️ コード例

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    void processOrder(String orderId) {
        log.info("注文処理を開始します: orderId={}", orderId); // プレースホルダーで変数を埋め込む

        try {
            // 何らかの処理
        } catch (RuntimeException e) {
            // 第2引数に例外を渡すと、スタックトレースも出力される
            log.error("注文処理に失敗しました: orderId={}", orderId, e);
        }
    }
}
```

障害調査の第一歩は、まず`ERROR`ログの**スタックトレース（原因の連鎖）**を追い、次に同じ`orderId`やトレースIDを持つ`INFO`/`DEBUG`ログで**処理の前後関係**を追跡することです。
