## 📄SpringAOPで業務によく使うアノテーション一覧

**_概要_**
Spring AOPおよび`@Transactional`関連で**業務上よく使われるアノテーション**を、機能ごとに一覧で提供します。

> 🏢 **業務シナリオ**：注文確定処理で、在庫更新と注文レコード作成を**1つのトランザクション**にまとめ、失敗したら両方ロールバックする。また、全Serviceメソッドの呼び出しログを共通化する。

---

## 1. 🎭 Aspect定義系アノテーション

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Aspect` | このクラスが横断処理（Aspect）をまとめたクラスであることを示す。 |
| 🔥 **よく使う** | `@Pointcut("execution(...)")` | 適用対象のメソッドパターンを定義する（再利用可能）。 |
| 🔥 **よく使う** | `@Around("pointcut()")` | メソッドの**実行前後を丸ごと制御**する（最も柔軟で頻用される）。 |
| 💡 **たまに使う** | `@Before("pointcut()")` | メソッド実行**前**にのみ処理を挟む（引数チェック等）。 |
| 💡 **たまに使う** | `@AfterReturning(pointcut, returning)` | メソッドが**正常終了した場合のみ**、戻り値を受け取って処理する。 |
| 💡 **たまに使う** | `@AfterThrowing(pointcut, throwing)` | メソッドが**例外を投げた場合のみ**処理する（共通エラーログ等）。 |
| ☠️ **使わない** | `@After("pointcut()")` | 正常・異常にかかわらず**必ず**実行される（`finally`相当、使い分けが難しく`Around`で代用されがち）。 |

---

## 2. 💰 トランザクション制御系アノテーション

| 優先度 | アノテーション/属性 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Transactional` | このメソッドをトランザクション境界とする。デフォルトは非チェック例外でロールバック。 |
| 🔥 **よく使う** | `@Transactional(rollbackFor = Exception.class)` | **チェック例外でもロールバック**させたい場合に指定する。 |
| 💡 **たまに使う** | `@Transactional(propagation = Propagation.REQUIRES_NEW)` | 呼び出し元のトランザクションとは**別の新しいトランザクション**を開始する。 |
| 💡 **たまに使う** | `@Transactional(readOnly = true)` | 参照系処理であることを明示し、パフォーマンス最適化のヒントを与える。 |
| ☠️ **使わない** | `@Transactional(isolation = Isolation.SERIALIZABLE)` | トランザクション分離レベルを厳格化する（性能影響が大きく特殊な要件のみ）。 |

---

## 🚀 具体的なコード例

### 1. 例外発生時に共通ログを出す `@AfterThrowing`

```java
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
class ErrorLoggingAspect {

    @AfterThrowing(pointcut = "execution(* com.example.service..*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        System.out.println(joinPoint.getSignature() + " で例外発生: " + ex.getMessage());
    }
}
```

### 2. `@Transactional` による在庫更新と注文作成の一貫性保証

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class OrderService {
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;

    public OrderService(StockRepository stockRepository, OrderRepository orderRepository) {
        this.stockRepository = stockRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional // どちらかが失敗したら両方ロールバックされる
    public void placeOrder(Long productId, int quantity) {
        stockRepository.decreaseStock(productId, quantity); // 在庫を減らす
        orderRepository.createOrder(productId, quantity);   // 注文レコードを作成
        // ここで例外が発生すれば、上記2つの変更はロールバックされる
    }
}
```
