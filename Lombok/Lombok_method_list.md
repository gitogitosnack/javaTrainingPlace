## 📄Lombokで業務によく使うアノテーション一覧

**_概要_**
Lombokで**業務上よく使われるアノテーション**を、機能ごとに一覧で提供します。

---

## 1. 🔧 フィールドアクセス系

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Getter` | 全フィールド分の`getXxx()`を生成する（クラスまたはフィールド単位で付与可）。 |
| 💡 **たまに使う** | `@Setter` | 全フィールド分の`setXxx()`を生成する。**不変性を重視する設計では意図的に使わないこともある**。 |

---

## 2. 🏗️ コンストラクタ系

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@RequiredArgsConstructor` | `final`フィールド（および`@NonNull`フィールド）のみを引数に取るコンストラクタを生成する。**Spring DIのコンストラクタインジェクションで頻用**。 |
| 💡 **たまに使う** | `@NoArgsConstructor` | 引数なしコンストラクタを生成する（JPAエンティティ等、フレームワークの要求で必要になることが多い）。 |
| 💡 **たまに使う** | `@AllArgsConstructor` | 全フィールドを引数に取るコンストラクタを生成する。 |

---

## 3. 📦 複合アノテーション

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Data` | `@Getter`+`@Setter`+`@ToString`+`@EqualsAndHashCode`+`@RequiredArgsConstructor`相当をまとめて付与する。DTOでよく使われる。 |
| 💡 **たまに使う** | `@Value` | **不変クラス**を作る（全フィールド`final`、`setter`なし、`@Getter`のみ）。値オブジェクトに適する。 |

---

## 4. 🏭 生成パターン系

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Builder` | Builderパターンによるインスタンス生成コードを自動生成する。引数の多いコンストラクタの可読性を上げる。 |
| ☠️ **使わない** | `@SuperBuilder` | 継承関係のあるクラス階層で`@Builder`を使う場合の拡張版（特殊な設計でのみ必要）。 |

---

## 5. 📝 ログ・その他

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Slf4j` | クラスに`private static final Logger log = ...`を自動生成する。 |
| 💡 **たまに使う** | `@EqualsAndHashCode` | `equals()`/`hashCode()`を生成する（`@Data`に含まれるため単独使用は限定的）。 |
| 💡 **たまに使う** | `@ToString` | `toString()`を生成する。パスワード等を除外したい場合は`@ToString.Exclude`をフィールドに付与する。 |

---

## 🚀 具体的なコード例

### 1. `@Builder` によるインスタンス生成

- **目的:** 引数が多いオブジェクトを、可読性高く生成する。

```java
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class OrderRequest {
    private final String productName;
    private final int quantity;
    private final String shippingAddress;
}

public class BuilderExample {
    public static void main(String[] args) {
        OrderRequest request = OrderRequest.builder()
            .productName("ノートPC")
            .quantity(2)
            .shippingAddress("東京都渋谷区")
            .build();

        System.out.println(request.getProductName()); // ノートPC
    }
}
```

### 2. `@RequiredArgsConstructor` によるDIコード削減

```java
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor // final フィールドを引数に取るコンストラクタを自動生成
@Service
class PaymentService {
    private final PaymentGatewayDummy paymentGateway; // コンストラクタを書かなくてもDIされる

    public void pay(int amount) {
        log.info("決済処理を開始します: {}円", amount);
        paymentGateway.charge(amount);
    }
}
```
