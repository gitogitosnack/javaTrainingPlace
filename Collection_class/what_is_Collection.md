# List / Map / Set とは？

Javaの**コレクションフレームワーク**は、複数のデータをまとめて扱うための標準APIです。既存コードの解析では、配列よりも圧倒的に`List`・`Map`・`Set`が使われており、それぞれの**実装クラスの違い（挙動・計算量）**を知っているだけで読解速度が大きく変わります。

- **`List`**：順序があり、**重複を許す**（本棚に並んだ本）
- **`Set`**：順序を保証しない（実装による）、**重複を許さない**（重複のない集合の箱）
- **`Map`**：**キーと値のペア**で管理する（索引カード付きの辞書）

### 🏭 イメージ：「棚とラベル」

1. **`List`（本棚）**
   - インデックス（背番号）順にデータが並ぶ。同じ本（値）を何冊置いてもよい。
2. **`Set`（重複NGの箱）**
   - 同じ値を入れようとしても、既にあれば無視される。
3. **`Map`（索引カード付きの辞書）**
   - 「キー」というラベルを引くと、対応する「値」がすぐ取り出せる。

---

## ✨ 主な実装クラスと特徴

| 種類 | 実装クラス | 特徴 | 計算量の目安 |
| :--- | :--- | :--- | :--- |
| **List** | `ArrayList` | 内部は配列。**参照（get）が速い**。末尾追加は速いが、途中挿入・削除は遅い。 | `get`: O(1), 途中`add/remove`: O(n) |
| **List** | `LinkedList` | 内部は双方向連結リスト。**途中挿入・削除が速い**が、参照は遅い。 | `get`: O(n), 先頭/末尾`add/remove`: O(1) |
| **Map** | `HashMap` | ハッシュテーブル。**順序を保証しない**が高速。 | `get/put`: 平均O(1) |
| **Map** | `LinkedHashMap` | `HashMap`に**挿入順（またはアクセス順）**を保持する機能を追加。 | `get/put`: 平均O(1) |
| **Map** | `TreeMap` | キーを**自然順序（ソート済み）**で保持する。 | `get/put`: O(log n) |
| **Set** | `HashSet` | `HashMap`をベースにした実装。順序は保証されない。 | `add/contains`: 平均O(1) |
| **Set** | `LinkedHashSet` | 挿入順を保持する`HashSet`。 | `add/contains`: 平均O(1) |
| **Set** | `TreeSet` | 要素を自然順序で保持する。 | `add/contains`: O(log n) |

---

## 📝 代表的なメソッド

| 種類 | メソッド | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **Map** | `computeIfAbsent(key, function)` | キーが**存在しない場合のみ**値を計算して格納する。 | 集計用のリスト初期化に多用 |
| **Map** | `getOrDefault(key, default)` | キーが存在しなければ**デフォルト値**を返す。 | カウンターの初期値`0` |
| **Map** | `merge(key, value, function)` | キーの値を**既存値と結合**して更新する。 | 合計値の積み上げ |
| **Collections** | `unmodifiableList(list)` | **変更不可**なビューを生成する。 | 外部公開用のリスト |

### 🛠️ コード例

```java
import java.util.HashMap;
import java.util.Map;

public class WordCountExample {
    public static void main(String[] args) {
        String[] words = {"apple", "banana", "apple", "cat", "banana", "apple"};
        Map<String, Integer> counter = new HashMap<>();

        for (String word : words) {
            // キーが無ければ0、あれば現在値に+1する
            counter.merge(word, 1, Integer::sum);
        }

        System.out.println(counter);
        // 出力例: {banana=2, apple=3, cat=1}
    }
}
```

既存の業務コードでは`HashMap`や`ArrayList`が大半を占めますが、**なぜそのMap実装が選ばれているのか**（順序保証が必要か、ソートが必要か）を意識すると設計意図が読み取りやすくなります。
