# Spring Securityとは？

Spring Securityは、Springアプリケーションに対して**認証（Authentication）**と**認可（Authorization）**の仕組みを提供するセキュリティフレームワークです。

- **認証（Authentication）**：「あなたは誰か」を確認する処理（ログイン処理など）
- **認可（Authorization）**：「あなたは何ができるか」を確認する処理（管理者だけがアクセスできる画面、など）

自前でログイン機能やアクセス制御を実装しようとすると、パスワードの安全な保存、CSRF対策、セッション管理など考慮すべき点が非常に多くなりますが、Spring Securityを使うことで、これらを**標準機能として安全かつ宣言的に**実装できます。

### 🏭 イメージ：「建物の警備員とゲート」

Spring Securityが行っていることは、建物のセキュリティに例えるとわかりやすくなります。

1.  **フィルターチェーン（警備員）**
    - すべてのリクエストは、Spring Securityが用意した**フィルターチェーン（`SecurityFilterChain`）**を通過します。
    - 警備員が入館者をチェックするように、リクエストごとに「ログイン済みか」「アクセス権限があるか」を確認します。

2.  **認証（受付での身分確認）**
    - `AuthenticationManager`が中心となり、ユーザー名・パスワードなどの認証情報を検証します。
    - `UserDetailsService`がDBなどからユーザー情報を取得し、`PasswordEncoder`がパスワードを安全に照合します。

3.  **認可（フロアごとの入室制限）**
    - 認証が済んだユーザーに対して、URLやメソッド単位で「どのロールならアクセスできるか」を制御します。
    - 例: `/admin/**`は`ADMIN`ロールのみ、`/user/**`はログイン済みなら誰でも、など。

---

## ✨ Spring Securityの主な特徴

| 特徴                 | 説明                                                                                                       |
| :------------------- | :--------------------------------------------------------------------------------------------------------- |
| **宣言的な設定**     | `HttpSecurity`のAPIをメソッドチェーンで繋げ、「どのURLに誰がアクセスできるか」を宣言的に記述できます。      |
| **標準的なセキュリティ対策** | CSRF対策、セッション固定攻撃対策、クリックジャッキング対策などがデフォルトで有効になっています。            |
| **柔軟な認証方式**   | フォームログイン、Basic認証、JWT、OAuth2など、様々な認証方式に対応できます。                                |
| **パスワードの安全な管理** | `PasswordEncoder`（`BCryptPasswordEncoder`など）により、パスワードをハッシュ化して安全に保存・照合できます。 |
| **メソッド単位の制御** | `@PreAuthorize`などのアノテーションで、Controller/Serviceのメソッド単位でも権限チェックができます。          |

---

## 📝 代表的なコンポーネント（処理）

| 種類         | クラス・メソッド                  | 説明                                                              |
| :----------- | :--------------------------------- | :------------------------------------------------------------------ |
| **設定**     | `SecurityFilterChain`              | どのURLにどの認可ルールを適用するかを定義するBean。                 |
| **設定**     | `HttpSecurity.authorizeHttpRequests()` | URLパターンごとにアクセス制御ルールを設定する。                     |
| **認証**     | `UserDetailsService`               | ユーザー名からユーザー情報（パスワード・ロール等）を取得する。       |
| **認証**     | `PasswordEncoder`                  | パスワードのハッシュ化・照合を行う。                                 |
| **認可**     | `@PreAuthorize`                    | メソッド呼び出し前にロール・権限をチェックする。                    |

### 🛠️ コード例（Security設定の基本形）

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // パスワードのハッシュ化に使用するEncoderをBean登録
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // URLごとのアクセス制御ルールを定義
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**").permitAll() // 誰でもアクセス可
                .requestMatchers("/admin/**").hasRole("ADMIN")    // ADMINロールのみ
                .anyRequest().authenticated()                     // それ以外はログイン必須
            )
            // フォームログインを有効化
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
            );

        return http.build();
    }
}
```

Spring Securityは、現代のSpring Bootアプリケーションにおいて、認証・認可機能を安全かつ効率的に実装するために必須の機能となっています。

他に、JWT認証やOAuth2連携など、Spring Securityについて詳しく知りたい点はありますか？
