# Optionalクラスとは？

`Optional<T>`は、Java 8で導入された**「値があるかもしれないし、無いかもしれない」を表現する箱**です。

戻り値が`null`になり得るメソッドを`Optional`で包むことで、呼び出し側に「**この値は無いこともある**」ということを**型で明示**でき、`NullPointerException`（NPE）を防ぐ設計パターンとしてよく使われます。

### 🏭 イメージ：「中身があるかもしれない箱」

1. **箱を開ける前に「中身の有無」を確認できる**
   - `isPresent()` / `isEmpty()`で判定できる。
2. **中身がある場合だけ処理を実行できる**
   - `ifPresent()`や`map()`で、値がある時だけ処理を繋げられる。
3. **中身が無い場合の代替策を用意できる**
   - `orElse()`や`orElseThrow()`で、無い場合のデフォルト値や例外を宣言的に書ける。

---

## ✨ Optionalの主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **NPE対策** | `null`を直接扱う代わりに`Optional`を返すことで、呼び出し側に値の欠如を意識させる。 |
| **宣言的な分岐** | `if (obj != null)`のようなネストしたnullチェックを、メソッドチェーンで平坦に書ける。 |
| **メソッドチェーン** | `map()` / `flatMap()` / `filter()`を繋げて、値がある場合の変換処理を記述できる。 |
| **フィールドには使わない** | `Optional`は**戻り値専用**の設計思想であり、フィールドや引数の型として使うのは非推奨とされる。 |

---

## 📝 代表的なメソッド

| 種類 | メソッド | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **生成** | `Optional.ofNullable(value)` | `null`かもしれない値をラップする。 | DB検索結果など |
| **判定** | `isPresent()` | 値があるかどうかを`boolean`で返す。 | |
| **変換** | `map(Function f)` | 値がある場合のみ**変換**する。 | `opt.map(User::getName)` |
| **取得** | `orElseThrow(Supplier ex)` | 値が無ければ**例外を送出**する。 | `orElseThrow(() -> new NotFoundException())` |

### 🛠️ コード例

```java
import java.util.Optional;

public class OptionalExample {
    record User(Long id, String name) {}

    Optional<User> findById(Long id) {
        // DB検索を想定。見つからなければ空のOptionalを返す
        return id == 1L ? Optional.of(new User(1L, "Taro")) : Optional.empty();
    }

    public void run() {
        String name = findById(1L)
            .map(User::name)                 // 値があれば名前を取り出す
            .orElse("名無しさん");            // 無ければデフォルト値

        System.out.println(name); // Taro

        // 見つからない場合は例外を送出する典型パターン
        User user = findById(99L)
            .orElseThrow(() -> new IllegalStateException("ユーザーが見つかりません"));
    }
}
```

既存コードで`Optional`のメソッドチェーンが登場したら、「**値がある場合の処理**」と「**無い場合のフォールバック**」の2本の流れとして読み解くのがコツです。
