## 📄DI・アノテーション・Beanライフサイクルで業務によく使うもの一覧

**_概要_**
Spring DIコンテナ周りで**業務上よく使われるアノテーション**を、機能ごとに一覧で提供します。

> 🏢 **業務シナリオ**：注文サービスが、在庫確認とメール通知の2つの外部サービスに依存している。

---

## 1. 🏷️ ステレオタイプアノテーション（Bean登録）

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Component` | 汎用的なコンポーネントであることを示す（他の3つの親アノテーション）。 |
| 🔥 **よく使う** | `@Service` | 業務ロジック層のクラスであることを示す。 |
| 🔥 **よく使う** | `@Repository` | データアクセス層のクラスであることを示す（DBアクセス例外の変換機能も持つ）。 |
| 🔥 **よく使う** | `@RestController` | REST APIのエンドポイントを提供するクラスであることを示す（`@Controller`+`@ResponseBody`）。 |
| 💡 **たまに使う** | `@Configuration` | Bean定義用の設定クラスであることを示す。 |

---

## 2. 🔌 インジェクション方法

| 優先度 | 方法 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | コンストラクタインジェクション | フィールドを`final`にでき、**不変性とテスト容易性**が高い。現在の推奨方式。 |
| ☠️ **使わない** | フィールドインジェクション（`@Autowired`をフィールドに付与） | 手軽だが、`final`にできずテストで差し替えにくいため非推奨。 |
| 💡 **たまに使う** | `@Qualifier("beanName")` | 同じ型のBeanが複数存在する場合に**注入対象を明示**する。 |
| 💡 **たまに使う** | `@Primary` | 同じ型のBeanが複数ある場合の**デフォルト候補**を指定する。 |

---

## 3. ⏳ Beanのスコープ

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Scope("singleton")`（デフォルト） | アプリケーション全体で**1つのインスタンス**を共有する。明示しなくてもこれが既定値。 |
| 💡 **たまに使う** | `@Scope("prototype")` | 取得の**たびに新しいインスタンス**を生成する。状態を持つBeanで使う。 |
| ☠️ **使わない** | `@Scope("request")` / `@Scope("session")` | Webリクエスト・セッション単位のスコープ（特殊な用途に限定）。 |

---

## 4. 🔄 ライフサイクルコールバック

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `@PostConstruct` | Bean生成・DI完了**直後**に実行される初期化処理を定義する。 |
| 💡 **たまに使う** | `@PreDestroy` | コンテナ破棄**直前**に実行されるクリーンアップ処理を定義する。 |

---

## 🚀 具体的なコード例

### 1. `@Qualifier` による注入先の明示

- **目的:** 通知方法（メール／SMS）を実装した複数のBeanから、特定の実装を注入する。

```java
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

interface Notifier {
    void send(String message);
}

@Component("emailNotifier")
class EmailNotifier implements Notifier {
    public void send(String message) {
        System.out.println("メール送信: " + message);
    }
}

@Component("smsNotifier")
class SmsNotifier implements Notifier {
    public void send(String message) {
        System.out.println("SMS送信: " + message);
    }
}

@Service
class OrderNotificationService {
    private final Notifier notifier;

    // 同じNotifier型が複数あるため、@Qualifierでどちらを使うか明示する
    public OrderNotificationService(@Qualifier("emailNotifier") Notifier notifier) {
        this.notifier = notifier;
    }

    public void notifyOrderCompleted(String orderId) {
        notifier.send("注文" + orderId + "が完了しました");
    }
}
```

### 2. `@PostConstruct` による初期化処理

```java
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
class CacheWarmUpService {
    @PostConstruct
    public void init() {
        // DIが完了した直後に、キャッシュの事前読み込みなどを行う
        System.out.println("キャッシュを初期化しました");
    }
}
```
