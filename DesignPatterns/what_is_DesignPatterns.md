# GoFデザインパターン（実務頻出）とは？

デザインパターンは、**過去のソフトウェア設計者たちが発見した「よくある問題への定石的な解決策」**をカタログ化したものです。既存コードの中にこれらの「型」を見分けられると、クラス構成の意図を素早く理解でき、修正時も**その設計パターンの流儀に沿って**変更を加えられます。

### 🏭 イメージ：「料理のレシピ集」

同じ食材（クラス・インターフェース）でも、レシピ（パターン）によって全く違う役割分担になります。パターン名を知っていれば、コードを見た瞬間に「あ、これは〇〇パターンだ」と**設計意図を逆算**できます。

---

## ✨ 実務で頻出する4つのパターン

| パターン | 目的 | 見分け方（識別ポイント） |
| :--- | :--- | :--- |
| **Factory（ファクトリ）** | オブジェクトの**生成方法を隠蔽**し、呼び出し側は「何が生成されるか」を意識しなくてよくする。 | `createXxx()` / `newInstance()`のようなメソッドがあり、内部で`if`や`switch`によって**生成するクラスを分岐**している。 |
| **Strategy（ストラテジー）** | アルゴリズム（処理内容）を**インターフェースとして切り出し**、実行時に差し替え可能にする。 | 同じインターフェースを実装した**複数のクラス**があり、呼び出し側は`if-else`を書かずに委譲している。 |
| **Template Method（テンプレートメソッド）** | 処理の**大まかな流れ（骨格）を親クラスで固定**し、詳細部分だけを子クラスに実装させる。 | 親クラスに`final`な処理フローのメソッドがあり、中で呼び出される個別処理は`abstract`（または未実装）になっている。 |
| **Adapter / Facade（アダプター/ファサード）** | **Adapter**: 互換性のないインターフェース同士を繋ぐ変換器。**Facade**: 複雑な内部処理を**シンプルな窓口**にまとめる。 | Adapterは「外部ライブラリのクラスをラップして自社インターフェースに合わせている」箇所、Facadeは「複数のクラスへの呼び出しを1つのメソッドにまとめている」箇所。 |

---

## 📝 コード例：Strategyパターン（ラムダ式による簡潔な実装）

Java 8以降は、Strategyパターンを**インターフェース＋ラムダ式**で簡潔に書けるようになりました。

```java
import java.util.function.Function;

// 「割引計算」というアルゴリズムを切り出したインターフェース
interface DiscountStrategy {
    int applyDiscount(int price);
}

public class StrategyExample {
    public static void main(String[] args) {
        // 通常会員：割引なし
        DiscountStrategy normalMember = price -> price;
        // ゴールド会員：10%割引
        DiscountStrategy goldMember = price -> (int) (price * 0.9);

        System.out.println(checkout(1000, normalMember)); // 1000
        System.out.println(checkout(1000, goldMember));   // 900
    }

    // 呼び出し側は「どの割引ロジックか」をif文で分岐せず、Strategyを受け取るだけ
    static int checkout(int price, DiscountStrategy strategy) {
        return strategy.applyDiscount(price);
    }
}
```

既存コードで「巨大な`if-else`／`switch`の分岐」を見つけたら、それが**Strategyパターンで置き換えられそうな箇所**かどうかを考えると、改修時の設計改善につながります。
