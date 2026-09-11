# Builderパターンとは？

Builderパターンは、GoF（Gang of Four）のデザインパターンの1つで、**フィールドや引数の多い複雑なオブジェクト**を、**メソッドチェーンで安全かつ可読性高く組み立てる**ための設計手法です。

従来のコンストラクタでは、引数が増えるほど**「テレスコーピングコンストラクタ」**（引数の数だけコンストラクタをオーバーロードし続ける地獄）に陥りがちですが、Builderパターンを使うことで、**どの値が何を表しているか一目で分かる形**でオブジェクトを生成できます。

### 🏭 イメージ：「オーダーメイドの組み立て工程」

Builderパターンを使ったオブジェクト生成は、以下の流れで行われます。

1.  **Builderオブジェクトの取得**
    - 対象クラスの`builder()`メソッド（または`new XxxBuilder()`）から**組み立て役のBuilderオブジェクト**を取得します。

2.  **各項目の設定（メソッドチェーン）**
    - `.name("Taro")`, `.age(20)`のように、**必要な項目だけをメソッドチェーンで設定**していきます。
    - 引数の**順序を意識する必要がなく**、設定していない項目は**デフォルト値や任意（null許容）**として扱えます。

3.  **`build()`による生成**
    - 最後に`build()`メソッドを呼び出すことで、設定した内容をもとに**不変（イミュータブル）なオブジェクトが1つ生成**されます。
    - 生成後のオブジェクトは基本的に**フィールドの再代入ができない**ため、安全に扱えます。

---

## ✨ Builderパターンの主な特徴

| 特徴                 | 説明                                                                                                                                     |
| :------------------- | :----------------------------------------------------------------------------------------------------------------------------------------- |
| **可読性の向上**     | `new User("Taro", 20, null, "tokyo")`のような**意味不明な引数の羅列**を避け、`.name("Taro").age(20)`のように**何を設定しているか明確**になります。 |
| **必須・任意の区別** | コンストラクタで必須項目のみ受け取り、任意項目はBuilderのメソッドで個別に設定する、といった**柔軟な設計**が可能です。                        |
| **不変オブジェクト** | `build()`で生成した後はフィールドを変更できない**イミュータブルなオブジェクト**を作りやすく、**バグの少ない安全なコード**につながります。      |
| **段階的な構築**     | 一度に全ての値を渡す必要がなく、**条件によって設定する項目を出し分ける**など、柔軟な組み立てが可能です。                                       |
| **オーバーロード地獄の回避** | 引数の数や組み合わせごとにコンストラクタを量産する必要がなくなります。                                                                       |

---

## 📝 代表的な使い方（手動実装）

Builderパターンは、**対象クラスの内部にstaticなBuilderクラスを定義**し、コンストラクタをprivateにすることで実現するのが基本形です。

### 🛠️ コード例

```java
public class User {
    private final String name;   // 必須
    private final int age;       // 必須
    private final String address; // 任意
    private final String memo;    // 任意

    // コンストラクタはprivateにして、外部からの直接生成を禁止
    private User(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.address = builder.address;
        this.memo = builder.memo;
    }

    // Builderを取得するための入り口
    public static Builder builder(String name, int age) {
        return new Builder(name, age);
    }

    public static class Builder {
        private final String name;   // 必須はコンストラクタで受け取る
        private final int age;       // 必須はコンストラクタで受け取る
        private String address = ""; // 任意はデフォルト値を持たせる
        private String memo = "";    // 任意はデフォルト値を持たせる

        private Builder(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public Builder address(String address) {
            this.address = address;
            return this; // 自分自身を返すことでメソッドチェーンを実現
        }

        public Builder memo(String memo) {
            this.memo = memo;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Override
    public String toString() {
        return "User{name=%s, age=%d, address=%s, memo=%s}"
            .formatted(name, age, address, memo);
    }
}
```

```java
public class BuilderExample {
    public static void main(String[] args) {
        // 必須項目（name, age）はbuilder()の引数で渡し、
        // 任意項目（address, memo）だけをメソッドチェーンで設定する
        User user = User.builder("Taro", 20)
            .address("Tokyo")
            .build(); // memoは設定しないのでデフォルト値のまま

        System.out.println(user);
        // 出力: User{name=Taro, age=20, address=Tokyo, memo=}
    }
}
```

---

Builderパターンは、DTOやEntity、設定オブジェクトなど、**フィールド数の多いクラスを安全に組み立てたい場面**で非常によく使われます。実務ではLombokの`@Builder`アノテーションで自動生成するケースが多いため、詳細な使い分けは[[Builder_method_list]]を参照してください。
