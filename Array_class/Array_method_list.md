## 📄Arraysクラスで業務によく使うメソッド一覧

**_概要_**
`java.util.Arrays`クラスのメソッドのうち、**実務で頻出するもの**を、具体的な業務シナリオに沿って一覧で提供します。

業務では主に以下のようなシーンで`Arrays`クラスを使います。

1.  **CSV等から読み込んだ数値配列のソート・検索**
2.  **配列の比較・重複チェック**
3.  **配列の一部コピー・デフォルト値での初期化**
4.  **配列 ⇔ `List` の相互変換**

---

## 1. 🔀 ソート・検索系

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `Arrays.sort(array)` | 配列を**昇順にソート**します（プリミティブ型はデュアルピボットクイックソート）。 |
| 💡 **たまに使う** | `Arrays.sort(array, Comparator<T>)` | オブジェクト配列を、指定した`Comparator`で**任意の順序**にソートします。 |
| 🔥 **よく使う** | `Arrays.binarySearch(array, key)` | **ソート済み配列**に対して二分探索を行い、要素の位置を返します（未ソートだと結果が不定）。 |

## 2. 🔍 比較・重複チェック系

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `Arrays.equals(a, b)` | 2つの配列の**要素・順序が完全に一致するか**を判定します（`==`は参照比較になるため配列比較には使えない）。 |
| 💡 **たまに使う** | `Arrays.deepEquals(a, b)` | **配列の配列（多次元配列）**同士を、中身まで再帰的に比較します。 |

## 3. 📦 初期化・コピー系

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `Arrays.fill(array, value)` | 配列の**全要素を指定した値で埋めます**（デフォルト値での初期化に使用）。 |
| 🔥 **よく使う** | `Arrays.copyOf(array, newLength)` | 元の配列を**新しいサイズ**でコピーします（拡張時は末尾がデフォルト値で埋まる）。 |
| 💡 **たまに使う** | `Arrays.copyOfRange(array, from, to)` | 配列の**指定範囲だけ**を切り出してコピーします（`to`は含まない）。 |

## 4. 🔄 変換・出力系

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `Arrays.asList(array)` | 配列を**固定長の`List`ビュー**に変換します（要素の追加・削除は不可。サイズ変更するなら`new ArrayList<>(Arrays.asList(...))`）。 |
| 🔥 **よく使う** | `Arrays.toString(array)` | 配列の中身を**人間が読める文字列**に変換します（デバッグ・ログ出力用）。 |
| 💡 **たまに使う** | `Arrays.deepToString(array)` | **多次元配列**の中身を、ネストした形で読める文字列に変換します。 |
| 💡 **たまに使う** | `Arrays.stream(array)` | 配列から**Stream**を生成します（合計・平均・フィルタなど、Stream APIの処理に繋げたい場合）。 |

---

## 🚀 業務シナリオ別コード例

### 1\. CSVから読み込んだ数値配列をソート・検索する

- **目的:** CSVから読み込んだ売上金額の配列を昇順ソートし、特定の金額が存在するか二分探索で調べる。

```java
import java.util.Arrays;

public class SortAndSearchExample {
    public static void main(String[] args) {
        // CSVパース等で得られた売上金額の配列を想定
        int[] salesAmounts = {32000, 15000, 87000, 45000, 6000};

        // sort: 二分探索を行うために事前に昇順ソートしておく
        Arrays.sort(salesAmounts);
        System.out.println("ソート後: " + Arrays.toString(salesAmounts));
        // ソート後: [6000, 15000, 32000, 45000, 87000]

        // binarySearch: 45000がどの位置にあるか検索（見つからなければ負の値が返る）
        int index = Arrays.binarySearch(salesAmounts, 45000);
        System.out.println("45000のインデックス: " + index); // 3
    }
}
```

---

### 2\. DBから取得したレコード配列の重複・一致チェック

- **目的:** 2つの処理で取得した配列の内容が一致しているか（≒処理結果が同一か）を検証する。

```java
import java.util.Arrays;

public class EqualsCheckExample {
    public static void main(String[] args) {
        // 例: バッチ処理前後で取得したユーザーIDの配列を比較する
        String[] beforeIds = {"U001", "U002", "U003"};
        String[] afterIds  = {"U001", "U002", "U003"};

        // equals: 中身と順序が一致しているかを判定（==では参照比較になり常にfalseになりがち）
        boolean isSame = Arrays.equals(beforeIds, afterIds);
        System.out.println("処理前後でIDが一致するか: " + isSame); // true
    }
}
```

---

### 3\. 配列の一部をコピーしてデフォルト値で初期化する

- **目的:** 固定長のステータス配列を「未処理」で初期化し、既存配列の一部だけを新しい配列にコピーする。

```java
import java.util.Arrays;

public class FillAndCopyExample {
    public static void main(String[] args) {
        // 10件分の処理ステータス管理配列を「未処理(0)」で初期化
        int[] statusFlags = new int[10];
        Arrays.fill(statusFlags, 0); // fill: 全要素を0（未処理）で埋める
        System.out.println("初期化後: " + Arrays.toString(statusFlags));

        // 既存の処理結果配列から、先頭5件だけを新しい配列として切り出す
        int[] fullResults = {1, 1, 0, 1, 0, 1, 1, 0, 1, 1};
        int[] firstFive = Arrays.copyOfRange(fullResults, 0, 5); // copyOfRange: 0〜4番目だけコピー
        System.out.println("先頭5件: " + Arrays.toString(firstFive)); // [1, 1, 0, 1, 0]
    }
}
```

---

### 4\. 配列 ⇔ `List` の相互変換

- **目的:** DBやAPIから受け取った配列を、業務ロジックで扱いやすい`List`に変換する（逆変換も含む）。

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayListConvertExample {
    public static void main(String[] args) {
        String[] departmentArray = {"営業部", "開発部", "人事部"};

        // asList: 配列をListビューに変換（このままだとadd/removeで例外が発生する）
        List<String> fixedList = Arrays.asList(departmentArray);

        // サイズ変更可能なListとして使いたい場合はArrayListでラップする
        List<String> mutableList = new ArrayList<>(fixedList);
        mutableList.add("経理部"); // ArrayListならadd可能
        System.out.println("部署一覧: " + mutableList);
        // 部署一覧: [営業部, 開発部, 人事部, 経理部]

        // stream: 配列からStreamを生成して合計等の集計処理に繋げる
        int[] memberCounts = {12, 8, 5};
        int total = Arrays.stream(memberCounts).sum();
        System.out.println("合計人数: " + total); // 25
    }
}
```
