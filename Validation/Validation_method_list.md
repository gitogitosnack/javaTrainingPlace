## 📄Jakarta Validationで業務によく使うアノテーション一覧

**_概要_**
入力チェック（バリデーション）で**業務上よく使われるアノテーション**を、機能ごとに一覧で提供します。

> 🏢 **業務シナリオ**：会員登録フォームで、名前・メールアドレス・年齢・パスワードの入力チェックを行う。

---

## 1. ✅ 基本制約アノテーション

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@NotNull` | `null`を許容しない（空文字はチェックしない点に注意）。 |
| 🔥 **よく使う** | `@NotBlank` | `null`・空文字・空白のみを許容しない（`String`専用）。 |
| 💡 **たまに使う** | `@NotEmpty` | `null`・空コレクション/空文字列を許容しない（空白のみはチェックしない）。 |
| 🔥 **よく使う** | `@Size(min = 1, max = 100)` | 文字列長・コレクションサイズの範囲を指定する。 |
| 💡 **たまに使う** | `@Min(0)` / `@Max(150)` | 数値の最小・最大値を指定する。 |
| 🔥 **よく使う** | `@Pattern(regexp = "...")` | 正規表現による形式チェック（郵便番号・電話番号等）。 |
| 🔥 **よく使う** | `@Email` | メールアドレス形式を検証する。 |
| 💡 **たまに使う** | `@Past` / `@Future` | 日付が過去／未来であることを検証する（生年月日、予約日等）。 |

---

## 2. 🚦 発動トリガー

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Valid` | 対象オブジェクトの制約チェックを実行する（Bean Validation標準）。 |
| 💡 **たまに使う** | `@Validated`（Spring独自） | `@Valid`に加え、**検証グループ**の指定や、クラスレベルでのメソッド引数検証（`@Validated`をクラスに付与）に対応する。 |

---

## 3. 🚨 エラー取得

| 優先度 | クラス/例外 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `BindingResult` | `@Valid`の直後の引数に追加すると、**例外を発生させずに**エラー内容を受け取れる（画面遷移用フォーム等）。 |
| 🔥 **よく使う** | `MethodArgumentNotValidException` | `BindingResult`を受け取らない場合、バリデーション違反時に**自動で送出される例外**（REST APIで`@ExceptionHandler`により捕捉することが多い）。 |

---

## 4. 🛠️ カスタムバリデータの作成

| 優先度 | 要素 | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `@Constraint(validatedBy = ...)` | 自作の制約アノテーションを定義する際の起点。 |
| 💡 **たまに使う** | `ConstraintValidator<A, T>`の実装 | `isValid()`メソッドに独自の検証ロジックを書く。 |

---

## 🚀 具体的なコード例

### 1. `BindingResult` によるエラー内容の取得

- **目的:** バリデーションエラー時に例外を発生させず、エラーメッセージを画面へ返す。

```java
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
class RegistrationController {

    @PostMapping("/register")
    public String register(@Valid @RequestBody UserRegistrationRequest request, BindingResult result) {
        if (result.hasErrors()) {
            // 発生したすべてのエラーメッセージを連結して返す
            String errors = result.getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce("", (a, b) -> a + b + "\n");
            return "入力エラー:\n" + errors;
        }
        return "登録完了";
    }
}
```

### 2. カスタムバリデータの実装（パスワード形式チェック）

```java
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.annotation.*;

@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@interface StrongPassword {
    String message() default "パスワードは8文字以上で、数字を含める必要があります";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}

class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return value.length() >= 8 && value.chars().anyMatch(Character::isDigit);
    }
}
```
