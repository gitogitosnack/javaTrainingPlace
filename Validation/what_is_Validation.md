# Jakarta Validation（Bean Validation）とは？

Jakarta Validation（旧称：Bean Validation）は、DTOやEntityの**フィールドに制約アノテーションを付けるだけで、入力チェック（バリデーション）を宣言的に実現**する仕組みです。

Spring MVCでは`@Valid`/`@Validated`と組み合わせることで、コントローラーに届く前に**自動的に検証**が行われます。既存コードで「なぜかここでバリデーションエラーが発生している」場合、多くはDTOクラスに付与された制約アノテーションが原因です。

### 🏭 イメージ：「入口の検問」

1. **制約の宣言（検問のルール表）**
   - DTOのフィールドに`@NotBlank`や`@Size`のようなアノテーションで**チェックルール**を定義する。
2. **検問の実行（`@Valid`）**
   - コントローラーの引数に`@Valid`を付けると、リクエストがメソッド本体に届く**前**に自動でチェックが走る。
3. **違反時の対応（エラーの受け渡し）**
   - チェックに違反すると、`MethodArgumentNotValidException`（またはBindingResultへのエラー格納）として通知される。

---

## ✨ 主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **宣言的な入力チェック** | `if`文でのチェックを書かず、アノテーションだけでルールを表現できる。 |
| **Spring MVCとの統合** | `@RequestBody`に`@Valid`を付けるだけで、リクエスト受信時に自動検証される。 |
| **グループ化** | 登録時と更新時で異なるルールを適用する等、**検証グループ**による使い分けが可能。 |
| **カスタムバリデータ** | 独自の複雑な検証ロジックを、自作アノテーション＋`ConstraintValidator`で実装できる。 |

---

## 📝 代表的なアノテーション

| 種類 | アノテーション | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **必須チェック** | `@NotNull` / `@NotBlank` / `@NotEmpty` | `null`／空文字／空コレクションを許容しない。 | |
| **範囲・桁数** | `@Size(min, max)` | 文字列長・コレクションサイズの範囲を指定する。 | |
| **数値範囲** | `@Min` / `@Max` | 数値の最小・最大値を指定する。 | |
| **形式** | `@Pattern(regexp)` / `@Email` | 正規表現・メールアドレス形式を検証する。 | |

### 🛠️ コード例

```java
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;

class UserRegistrationRequest {
    @NotBlank(message = "名前は必須です")
    private String name;

    @Email(message = "メールアドレスの形式が不正です")
    @NotBlank
    private String email;

    @Min(value = 0, message = "年齢は0以上で入力してください")
    private int age;

    // getter/setter省略
}

@RestController
class UserController {

    @PostMapping("/users")
    public String register(@Valid @RequestBody UserRegistrationRequest request) {
        // ここに到達する時点で、すべての制約チェックを通過済み
        return "登録完了: " + request;
    }
}
```

コントローラーのメソッド引数に`@Valid`が付いているのを見つけたら、そのDTOクラスの定義を確認することで、**どんな入力チェックが行われているか**を素早く把握できます。
