## 📄デザインパターンの構造・コード例一覧

**_概要_**
実務コードのリーディングでよく遭遇する4つのGoFデザインパターンについて、**構造の特徴**と**コード例**を一覧で提供します。

---

## 1. 🏭 Factoryパターン

| 優先度 | 要素 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `static XxxFactory createXxx(...)` | 引数（種別コードなど）に応じて、生成するインスタンスを**内部で切り替える**静的メソッド。 |
| 💡 **たまに使う** | Factoryインターフェース＋複数実装 | Factory自体をインターフェース化し、DIコンテナで差し替え可能にする発展形。 |

### 🚀 コード例

```java
interface PaymentMethod {
    void pay(int amount);
}

class CreditCardPayment implements PaymentMethod {
    public void pay(int amount) { System.out.println(amount + "円をクレジットカードで決済"); }
}

class BankTransferPayment implements PaymentMethod {
    public void pay(int amount) { System.out.println(amount + "円を銀行振込で決済"); }
}

class PaymentFactory {
    // 種別コードに応じて、生成するインスタンスを切り替える（呼び出し側はnewを意識しない）
    static PaymentMethod create(String type) {
        return switch (type) {
            case "CARD" -> new CreditCardPayment();
            case "BANK" -> new BankTransferPayment();
            default -> throw new IllegalArgumentException("未対応の決済方法: " + type);
        };
    }
}
```

---

## 2. 🎯 Strategyパターン

| 優先度 | 要素 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | 処理を表すインターフェース＋複数実装 | 「アルゴリズム」を1つのインターフェースとして定義し、複数のクラス（またはラムダ）で実装する。 |
| 🔥 **よく使う** | ラムダ式によるStrategy | Java8以降、関数型インターフェースとラムダ式で**クラスを作らず**実装できる。 |

### 🚀 コード例

`what_is_DesignPatterns.md`の割引計算の例を参照。`if-else`の分岐をインターフェースの実装差し替えに置き換えるのがポイントです。

---

## 3. 📐 Template Methodパターン

| 優先度 | 要素 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | 親クラスの`final`メソッド（処理フロー） | 処理の**順序**を固定し、子クラスでの上書きを防ぐ。 |
| 🔥 **よく使う** | `abstract`メソッド（個別処理） | 子クラスに実装を強制する部分。バッチ処理の「前処理・本処理・後処理」構成でよく使われる。 |

### 🚀 コード例（バッチ処理の骨格）

```java
abstract class BatchJobTemplate {
    // 処理の流れ（骨格）は固定し、子クラスでの変更を許さない
    public final void execute() {
        setUp();
        process();
        tearDown();
    }

    protected void setUp() { System.out.println("共通の前処理"); }
    protected abstract void process(); // 子クラスごとに異なる本処理
    protected void tearDown() { System.out.println("共通の後処理"); }
}

class UserImportBatch extends BatchJobTemplate {
    @Override
    protected void process() {
        System.out.println("ユーザーCSVを取り込み中...");
    }
}
```

---

## 4. 🔌 Adapter / Facadeパターン

| 優先度 | 要素 | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | Adapter：既存クラスをラップする実装 | 外部ライブラリのインターフェースと、自社の共通インターフェースの**橋渡し役**。 |
| 💡 **たまに使う** | Facade：複数クラスをまとめた単一の入口クラス | 呼び出し側が意識すべき手順を1メソッドに集約し、内部の複雑さを隠す。 |

### 🚀 コード例（Facade：複数処理をまとめた注文確定処理）

```java
class OrderFacade {
    private final StockServiceDummy stockService = new StockServiceDummy();
    private final PaymentServiceDummy paymentService = new PaymentServiceDummy();
    private final NotificationServiceDummy notificationService = new NotificationServiceDummy();

    // 呼び出し側は内部の3ステップを意識せず、これだけ呼べばよい
    public void completeOrder(Long productId, int amount) {
        stockService.decreaseStock(productId);
        paymentService.charge(amount);
        notificationService.sendCompletionMail();
    }
}

class StockServiceDummy { void decreaseStock(Long id) {} }
class PaymentServiceDummy { void charge(int amount) {} }
class NotificationServiceDummy { void sendCompletionMail() {} }
```
