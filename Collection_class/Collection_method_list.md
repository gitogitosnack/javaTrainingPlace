## 📄List / Map / Setで使用するメソッド一覧

**_概要_**
業務システムの改修でよく登場する`List`・`Map`・`Set`・`Collections`のメソッドを、機能ごとに一覧で提供します。

---

## 1. 📚 List系メソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `add(E e)` / `add(int index, E e)` | 要素を**末尾**または**指定位置**に追加する。 |
| 🔥 **よく使う** | `get(int index)` | 指定位置の要素を取得する。 |
| 🔥 **よく使う** | `size()` | 要素数を返す。 |
| 🔥 **よく使う** | `contains(Object o)` | 指定要素が含まれるか判定する。 |
| 💡 **たまに使う** | `remove(int index)` / `remove(Object o)` | 指定位置・指定要素を削除する（**オーバーロードの違いに注意**）。 |
| 💡 **たまに使う** | `indexOf(Object o)` | 指定要素の**最初の位置**を返す（見つからなければ`-1`）。 |
| 💡 **たまに使う** | `sort(Comparator<T> c)` | リストを**破壊的に**ソートする。 |
| 🔥 **よく使う** | `stream()` | Stream APIへの入口。 |

---

## 2. 🗂️ Map系メソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `put(K key, V value)` | キーと値を登録する（既存キーは**上書き**）。 |
| 🔥 **よく使う** | `get(Object key)` | キーに対応する値を取得する（無ければ`null`）。 |
| 🔥 **よく使う** | `getOrDefault(Object key, V default)` | キーが無い場合に**デフォルト値**を返す。 |
| 🔥 **よく使う** | `containsKey(Object key)` | キーの存在を確認する。 |
| 💡 **たまに使う** | `computeIfAbsent(K key, Function<K,V> f)` | キーが無いときだけ値を**計算して格納**する（初期化処理の定石）。 |
| 💡 **たまに使う** | `merge(K key, V value, BiFunction f)` | 既存値と新しい値を**関数で結合**して更新する（集計処理の定石）。 |
| 🔥 **よく使う** | `keySet()` / `values()` / `entrySet()` | それぞれ**キー集合**・**値集合**・**キーと値のペア集合**を返す。 |
| 🔥 **よく使う** | `forEach(BiConsumer<K,V> action)` | 全エントリに対して処理を実行する。 |

---

## 3. 🧺 Set系メソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `add(E e)` | 要素を追加する（既存なら**無視**され`false`を返す）。 |
| 🔥 **よく使う** | `contains(Object o)` | 要素の存在を確認する。 |
| 💡 **たまに使う** | `retainAll(Collection c)` | **共通部分（積集合）**のみ残す。 |
| 💡 **たまに使う** | `removeAll(Collection c)` | 指定コレクションに含まれる要素を**すべて削除**する（差集合）。 |

---

## 4. 🛠️ Collectionsユーティリティクラス

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `Collections.emptyList()` / `emptyMap()` | **不変の空コレクション**を返す（`null`の代わりに使う）。 |
| 🔥 **よく使う** | `Collections.unmodifiableList(list)` | **変更不可なビュー**を生成する（外部への公開用）。 |
| 💡 **たまに使う** | `Collections.sort(list)` | リストをソートする（`list.sort()`と同等）。 |
| ☠️ **使わない** | `Collections.synchronizedList(list)` | スレッドセーフな**同期化ラッパー**（現代では`ConcurrentHashMap`等を使う方が多い）。 |

---

## 🚀 具体的なコード例

### 1. `computeIfAbsent` によるグルーピング

- **目的:** 社員リストを部署ごとにグルーピングする。

```java
import java.util.*;

public class GroupingExample {
    record Employee(String name, String department) {}

    public static void main(String[] args) {
        List<Employee> employees = List.of(
            new Employee("Taro", "Sales"),
            new Employee("Hanako", "Dev"),
            new Employee("Jiro", "Sales")
        );

        Map<String, List<Employee>> byDept = new HashMap<>();
        for (Employee e : employees) {
            // 部署キーが無ければ新しいListを作って登録し、そこに追加する
            byDept.computeIfAbsent(e.department(), k -> new ArrayList<>()).add(e);
        }

        System.out.println(byDept);
    }
}
```

### 2. `getOrDefault` によるカウント集計

```java
import java.util.HashMap;
import java.util.Map;

public class CountExample {
    public static void main(String[] args) {
        String[] statuses = {"OK", "NG", "OK", "OK", "NG"};
        Map<String, Integer> count = new HashMap<>();

        for (String status : statuses) {
            count.put(status, count.getOrDefault(status, 0) + 1);
        }

        System.out.println(count); // {NG=2, OK=3}
    }
}
```
