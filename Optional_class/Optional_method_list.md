## 📄Optionalで使用するメソッド一覧

**_概要_**
`Optional`のメソッドは、大きく以下の4つのカテゴリーに分類されます。

1. **生成系メソッド**: `Optional`インスタンスを作成します。
2. **判定系メソッド**: 値の有無を確認します。
3. **変換系メソッド**: 値がある場合に変換・絞り込みを行います。
4. **取得系メソッド**: 中身の値、または代替値を取り出します。

---

## 1. 🏭 生成系メソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `Optional.ofNullable(T value)` | `null`かもしれない値を安全にラップする。既存メソッドを`Optional`対応させる際の基本形。 |
| 💡 **たまに使う** | `Optional.of(T value)` | **`null`ではないと確信できる**値をラップする（`null`を渡すと即座に`NullPointerException`）。 |
| 💡 **たまに使う** | `Optional.empty()` | 値が無い`Optional`を明示的に生成する。 |

---

## 2. 🔍 判定系メソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `isPresent()` | 値が**存在するか**を`boolean`で返す。 |
| 💡 **たまに使う** | `isEmpty()` | 値が**存在しないか**を`boolean`で返す（Java 11以降）。 |
| 🔥 **よく使う** | `ifPresent(Consumer<T> action)` | 値がある場合のみ**処理を実行**する（無い場合は何もしない）。 |
| 💡 **たまに使う** | `ifPresentOrElse(Consumer action, Runnable emptyAction)` | 値がある場合とない場合、**両方の処理**を書ける（Java 9以降）。 |

---

## 3. ⚙️ 変換系メソッド（中間操作）

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `map(Function<T,R> mapper)` | 値がある場合に**別の値へ変換**する。 |
| 💡 **たまに使う** | `flatMap(Function<T, Optional<R>> mapper)` | 変換結果が**`Optional`を返すメソッド**の場合に、二重の`Optional`（`Optional<Optional<T>>`）を防ぐ。 |
| 💡 **たまに使う** | `filter(Predicate<T> predicate)` | 条件に合わない場合は**空の`Optional`**に変える。 |

---

## 4. 🎯 取得系メソッド（終端操作）

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| ☠️ **使わない** | `get()` | 値を直接取得する。**値が無いと例外**になるため、事前チェック無しでの使用は避ける。 |
| 🔥 **よく使う** | `orElse(T other)` | 値が無ければ**固定のデフォルト値**を返す。 |
| 🔥 **よく使う** | `orElseGet(Supplier<T> supplier)` | 値が無ければ**関数を実行して**代替値を生成する（重い処理はこちらで遅延評価する）。 |
| 🔥 **よく使う** | `orElseThrow(Supplier<X> exceptionSupplier)` | 値が無ければ**指定した例外**を送出する。業務ロジックの「見つからなければエラー」の定石。 |

---

## 🚀 具体的なコード例

### 1. `flatMap` によるネスト解消

- **目的:** ユーザーが持つ「住所（Optional）」からさらに「郵便番号」を安全に取り出す。

```java
import java.util.Optional;

public class FlatMapExample {
    record Address(String zipCode) {}
    record User(String name, Optional<Address> address) {}

    public static void main(String[] args) {
        User user = new User("Taro", Optional.of(new Address("123-4567")));

        String zip = user.address()
            .flatMap(a -> Optional.ofNullable(a.zipCode())) // ネストしたOptionalを平坦化
            .orElse("不明");

        System.out.println(zip); // 123-4567
    }
}
```

### 2. `orElseThrow` による業務エラー処理

```java
import java.util.Optional;

public class OrElseThrowExample {
    record Product(Long id, String name) {}

    Optional<Product> findProduct(Long id) {
        return id == 1L ? Optional.of(new Product(1L, "ノートPC")) : Optional.empty();
    }

    public void purchase(Long id) {
        Product product = findProduct(id)
            .orElseThrow(() -> new IllegalArgumentException("商品ID:" + id + " は存在しません"));

        System.out.println(product.name() + " を購入しました");
    }
}
```
