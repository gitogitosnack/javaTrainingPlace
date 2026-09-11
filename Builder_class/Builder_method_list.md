## 📄Builderパターンで業務でよく使う手法一覧

**_概要_**
Builderパターンを実務で使う際によく登場する**手法**を、業務シナリオに沿って一覧で提供します。
ここでは以下のシナリオを題材にします。

> 🎯 **業務シナリオ**: ユーザー登録画面から送信された内容をもとに、`UserRegistrationRequest`という**フィールドの多いクラス**（氏名・年齢は必須、電話番号・自己紹介・会員ランクは任意）を安全に組み立てたい。

Builderパターンの実現方法は、大きく以下の2つに分類されます。

1.  **手動実装**: 自前でBuilderクラスを書く方法。ライブラリ不要でどこでも使えますが、記述量が多い。
2.  **Lombok**: アノテーションでBuilderクラスを自動生成する方法。実務では**こちらが主流**。

---

## 1. 🛠️ 手動実装

| 優先度 | 手法 | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `private`コンストラクタ + `static`内部Builderクラス | 対象クラスのコンストラクタを`private`にし、内部に`Builder`クラスを定義して組み立てを行わせます。（ライブラリを導入できない現場や、Builder特有の複雑なロジックを入れたい場合に使用） |
| 💡 **たまに使う** | 必須項目を`Builder`のコンストラクタ引数にする | **必須項目**は`builder(name, age)`のように**Builder生成時に渡す**ことで、設定し忘れを防ぎます。 |
| 🔥 **よく使う** | 各setterメソッドで`this`を返す（メソッドチェーン） | `.phoneNumber(xxx)`のように**自分自身(`this`)を返す**ことで、`.method().method()`と連続して呼び出せるようにします。 |

### 🚀 コード例（手動実装）

```java
public class UserRegistrationRequest {
    private final String name;         // 必須
    private final int age;             // 必須
    private final String phoneNumber;  // 任意
    private final String introduction; // 任意
    private final String rank;         // 任意（デフォルトあり）

    private UserRegistrationRequest(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.phoneNumber = builder.phoneNumber;
        this.introduction = builder.introduction;
        this.rank = builder.rank;
    }

    public static Builder builder(String name, int age) {
        return new Builder(name, age);
    }

    public static class Builder {
        private final String name;
        private final int age;
        private String phoneNumber = "未登録";
        private String introduction = "";
        private String rank = "一般会員"; // デフォルト値

        private Builder(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder introduction(String introduction) {
            this.introduction = introduction;
            return this;
        }

        public Builder rank(String rank) {
            this.rank = rank;
            return this;
        }

        public UserRegistrationRequest build() {
            return new UserRegistrationRequest(this);
        }
    }

    @Override
    public String toString() {
        return "UserRegistrationRequest{name=%s, age=%d, phoneNumber=%s, introduction=%s, rank=%s}"
            .formatted(name, age, phoneNumber, introduction, rank);
    }
}
```

```java
public class ManualBuilderExample {
    public static void main(String[] args) {
        // 必須項目（name, age）はbuilder()の引数で渡す
        // 任意項目（phoneNumber, rank）だけをメソッドチェーンで設定
        UserRegistrationRequest request = UserRegistrationRequest.builder("山田太郎", 28)
            .phoneNumber("090-1234-5678")
            .rank("プレミアム会員")
            .build(); // introductionは未設定なのでデフォルト値""のまま

        System.out.println(request);
        // 出力: UserRegistrationRequest{name=山田太郎, age=28, phoneNumber=090-1234-5678, introduction=, rank=プレミアム会員}
    }
}
```

---

## 2. 🍀 Lombokによる自動生成

Lombokを導入すると、アノテーションを付けるだけで上記のBuilderクラスを**自動生成**してくれるため、実務ではこちらが圧倒的によく使われます。

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Builder` | クラスに付与するだけで、`builder()`メソッドとBuilderクラス一式を**自動生成**します。 |
| 🔥 **よく使う** | `@Builder.Default` | フィールドに付与すると、**Builderで未設定の場合のデフォルト値**を指定できます（付けないとBuilder経由の生成では`null`になってしまう点に注意）。 |
| 💡 **たまに使う** | `@NonNull`（Builderのフィールドに付与） | **必須項目のnullチェック**を自動生成し、未設定のまま`build()`すると`NullPointerException`を発生させます。 |
| 💡 **たまに使う** | `@AllArgsConstructor(access = AccessLevel.PRIVATE)` | `@Builder`と組み合わせて使うことが多く、**全フィールドを受け取るコンストラクタ**を生成しつつ外部からの直接生成を禁止します。 |
| ☠️ **使わない** | Builderの手動オーバーライド | `toBuilder = true`など特殊オプションを使わない限り、生成されたコードを直接編集するのは**メンテナンス性を損なう**ため避けます。 |

### 🚀 コード例（Lombok使用）

```java
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@Builder
public class UserRegistrationRequest {

    @NonNull
    private String name; // 必須（未設定でbuild()するとNullPointerException）

    private int age; // 必須（int型はnullチェックできないため、業務ロジック側でバリデーション推奨）

    @Builder.Default
    private String phoneNumber = "未登録"; // 任意（デフォルト値あり）

    private String introduction; // 任意（未設定ならnull）

    @Builder.Default
    private String rank = "一般会員"; // 任意（デフォルト値あり）
}
```

```java
public class LombokBuilderExample {
    public static void main(String[] args) {
        // @Builderが自動生成したbuilder()から、必要な項目だけメソッドチェーンで設定
        UserRegistrationRequest request = UserRegistrationRequest.builder()
            .name("鈴木花子")
            .age(24)
            .phoneNumber("080-9876-5432")
            .build(); // introductionは未設定なのでnull、rankは@Builder.Defaultにより"一般会員"

        System.out.println(request);
        // 出力例: UserRegistrationRequest(name=鈴木花子, age=24, phoneNumber=080-9876-5432, introduction=null, rank=一般会員)

        // @NonNullを付けたフィールドを設定せずにbuild()すると例外が発生する
        try {
            UserRegistrationRequest.builder()
                .age(30)
                .build(); // nameを設定していない
        } catch (NullPointerException e) {
            System.out.println("例外発生: " + e.getMessage()); // 例外発生: name is marked non-null but is null
        }
    }
}
```

---

## 3. ⚖️ 手動実装とLombokの比較

| 観点 | 手動実装 | Lombok（`@Builder`） |
| :--- | :--- | :--- |
| 記述量 | 多い（Builderクラスを全て自分で書く） | 少ない（アノテーション1つ） |
| 必須項目の表現 | Builderのコンストラクタ引数で表現しやすい | `@NonNull`で表現するが**実行時例外**でしか検知できない |
| デフォルト値 | フィールド初期化で自由に設定可能 | `@Builder.Default`を**付け忘れるとnullになる**ため注意が必要 |
| 独自ロジックの追加 | `build()`内などに自由にバリデーション処理を追加しやすい | 生成コードのため、複雑な独自ロジックは書きにくい |
| 依存ライブラリ | 不要 | Lombokの導入が必須 |
| 実務での採用度 | 🔥低〜中（特殊要件がある場合） | 🔥🔥🔥高（DTO/Entityの定番） |

---

## 以下、参考動画のリンク

[Builderパターンとは何かをわかりやすく解説！【デザインパターン入門】](https://www.google.com/search?q=Builder+%E3%83%87%E3%82%B6%E3%82%A4%E3%83%B3%E3%83%91%E3%82%BF%E3%83%BC%E3%83%B3+java)
