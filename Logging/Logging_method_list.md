## 📄SLF4J/Logbackで業務によく使うメソッド一覧

**_概要_**
ログ出力・例外ログ・MDCについて、業務でよく使われるメソッドを一覧で提供します。

---

## 1. 📝 ログ出力メソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `log.info("message {}", value)` | 通常運用で残しておきたい情報（処理開始・終了等）を出力する。 |
| 🔥 **よく使う** | `log.warn("message {}", value)` | 想定範囲内だが注意すべき事象（リトライ発生等）を出力する。 |
| 🔥 **よく使う** | `log.error("message {}", value)` | 異常事態・障害につながる事象を出力する。 |
| 💡 **たまに使う** | `log.debug("message {}", value)` | 開発・調査時のみ必要な詳細情報を出力する（本番では出力レベルを抑制することが多い）。 |
| ☠️ **使わない** | `log.trace(...)` | 極めて詳細な情報（フレームワーク内部処理等）。業務コードで使う場面は稀。 |
| ☠️ **使わない** | `"message " + value`（文字列連結） | プレースホルダー`{}`を使わないと、ログレベル無効時にも連結コストが発生する。 |

---

## 2. 💥 例外ログ

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `log.error("message", exception)` | メッセージと共に**スタックトレース全体**を出力する（第2引数に例外オブジェクトを渡す）。 |
| ☠️ **使わない** | `log.error("message: " + exception.getMessage())` | メッセージ文字列のみで、**スタックトレースが失われる**アンチパターン。 |

---

## 3. 🧵 MDC操作（トレース情報の付与）

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `MDC.put(String key, String value)` | 現在のスレッドに紐づくログコンテキストに値を登録する。 |
| 💡 **たまに使う** | `MDC.get(String key)` | 登録した値を取得する。 |
| 💡 **たまに使う** | `MDC.remove(String key)` / `MDC.clear()` | スレッド使い回し（コネクションプール等）による**情報の混線を防ぐため**、処理終了時に必ず削除する。 |

---

## 4. ⚡ パフォーマンス配慮

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `log.isDebugEnabled()` | DEBUGログが実際に出力される設定かを事前チェックし、**重い引数生成処理**を無駄に実行しないようにする。 |

---

## 🚀 具体的なコード例

### 1. `MDC` を使ったリクエスト単位のトレースID付与

- **目的:** Webリクエストごとに一意のIDを発行し、一連のログをまとめて追跡できるようにする。

```java
import org.slf4j.MDC;

import java.util.UUID;
import java.io.IOException;
import jakarta.servlet.*;

public class TraceIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId); // 以降のログ出力に自動的にtraceIdが含まれる
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("traceId"); // スレッドプールでの使い回しに備えて必ず削除する
        }
    }
}
```

### 2. 例外発生時のスタックトレース付きログ

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    void charge(int amount) {
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException("金額が不正です: " + amount);
            }
        } catch (IllegalArgumentException e) {
            // 第2引数に例外を渡すことで、スタックトレース全体がログに残る
            log.error("決済処理でエラーが発生しました", e);
            throw e;
        }
    }
}
```
