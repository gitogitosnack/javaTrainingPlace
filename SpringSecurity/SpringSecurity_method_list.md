## 📄SpringSecurityで業務によく使うクラス・メソッド一覧

**_概要_**  
Spring Securityで**業務上よく使われるクラス・メソッド**を、機能ごとに一覧で提供します。  
ここでは、以下の**業務シナリオ**を題材にして解説します。

> 🏢 **業務シナリオ**：社内システムにログイン機能を実装し、**管理者（ADMIN）は全ての画面、一般ユーザー（USER）は自分の画面のみ**にアクセスできるようにする。

Spring Securityの設定は、大きく以下の3つのカテゴリーに分類されます。

1.  **設定系メソッド**: どのURLに、どの権限が必要かを定義します。
2.  **認証系クラス**: ログイン時にユーザー情報を検証します。
3.  **認可系アノテーション**: メソッド単位で権限チェックを行います。

---

## 1. ⚙️ 設定系メソッド（`HttpSecurity`）

`SecurityFilterChain`の中で、`HttpSecurity`をメソッドチェーンで繋げてルールを定義します。

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `authorizeHttpRequests(customizer)` | URLパターンごとに**アクセス制御ルール**を設定する起点となるメソッド。 |
| 🔥 **よく使う** | `requestMatchers(String... patterns)` | 対象となる**URLパターン**を指定する（例: `/admin/**`）。 |
| 🔥 **よく使う** | `hasRole(String role)` | 指定したロールを持つユーザーのみ**アクセスを許可**する（内部的に`ROLE_`が付与される）。 |
| 💡 **たまに使う** | `hasAnyRole(String... roles)` | 指定した**複数ロールのいずれか**を持つユーザーにアクセスを許可する。 |
| 🔥 **よく使う** | `permitAll()` | ログイン不要で**誰でもアクセス可能**にする（ログイン画面等）。 |
| 🔥 **よく使う** | `authenticated()` | **ログイン済み**であればアクセス可能にする。 |
| ☠️ **使わない** | `denyAll()` | 全てのアクセスを**拒否**する（メンテナンス時等、業務では稀）。 |
| 🔥 **よく使う** | `formLogin(customizer)` | **フォームログイン**（ログイン画面）を有効化する。 |
| 💡 **たまに使う** | `logout(customizer)` | **ログアウト処理**のURLや遷移先を設定する。 |
| 💡 **たまに使う** | `csrf(customizer)` | **CSRF対策**の有効/無効を設定する（API開発時に無効化することがある）。 |

---

## 2. 🔑 認証系クラス（ログイン処理まわり）

ユーザーが入力したID・パスワードを検証するためのクラスです。

| 優先度 | クラス・メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `UserDetailsService.loadUserByUsername(String username)` | ユーザー名から**DB等の情報を取得**し、`UserDetails`として返す。ログイン処理の中核。 |
| 🔥 **よく使う** | `User.builder()` | `UserDetails`の実装を**簡単に構築**するビルダー（Spring Security標準）。 |
| 🔥 **よく使う** | `BCryptPasswordEncoder.encode(String rawPassword)` | パスワードを**ハッシュ化**する（会員登録時に使用）。 |
| 💡 **たまに使う** | `PasswordEncoder.matches(raw, encoded)` | 入力パスワードとハッシュ化済みパスワードが**一致するか照合**する（通常はSpring Securityが内部で自動実行）。 |

---

## 3. 🛡️ 認可系アノテーション（メソッド単位の権限チェック）

Controller/Serviceのメソッドに直接権限チェックを付与できます。

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@PreAuthorize("hasRole('ADMIN')")` | メソッド実行**前**にロールをチェックする。URL単位より**細かい制御**が可能。 |
| 💡 **たまに使う** | `@PreAuthorize("hasAuthority('USER_EDIT')")` | ロールではなく、より細かい**権限（Authority）単位**でチェックする。 |
| ☠️ **使わない** | `@Secured("ROLE_ADMIN")` | `@PreAuthorize`より古い形式のアノテーション（式が書けず柔軟性が低いため現在はあまり使わない）。 |

---

## 🚀 具体的なコード例（社内システムのログイン＋ロール別アクセス制御）

### 1\. ユーザー情報の取得（`UserDetailsService`の実装）

- **目的:** ログイン時にユーザー名からDB相当の情報を取得し、ロール（権限）を付与する。

<!-- end list -->

```java
import java.util.List;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class InHouseUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 本来はリポジトリ経由でDBからユーザーを取得する
        if ("admin".equals(username)) {
            return User.builder()
                .username("admin")
                .password("$2a$10$7EqJtq98hPqEX7fNZaFWoO...") // ハッシュ化済みパスワード
                .roles("ADMIN") // 内部的に ROLE_ADMIN として扱われる
                .build();
        } else if ("taro".equals(username)) {
            return User.builder()
                .username("taro")
                .password("$2a$10$92IXUNpkjO0rOQ5byMi.Ye...")
                .roles("USER") // ROLE_USER
                .build();
        }
        throw new UsernameNotFoundException("ユーザーが見つかりません: " + username);
    }
}
```

---

### 2\. URLごとのアクセス制御（`SecurityFilterChain`）

- **目的:** `/admin/**`はADMINロールのみ、`/user/**`はログイン済みなら誰でもアクセス可能にする。

<!-- end list -->

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // ログイン画面・静的リソースは誰でもアクセス可
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                // 管理者用画面はADMINロールのみ
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // ユーザー用画面はログイン済みなら誰でも（ADMINも含む）
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                // それ以外は全てログイン必須
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")           // 自作のログイン画面
                .defaultSuccessUrl("/home", true) // ログイン成功時の遷移先
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout") // ログアウト後の遷移先
            );

        return http.build();
    }
}
```

---

### 3\. メソッド単位でのアクセス制御（`@PreAuthorize`）

- **目的:** URL単位のチェックに加え、Serviceのメソッド単位でも管理者しか実行できない処理を保護する。

<!-- end list -->

```java
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {

    // ADMINロールを持つユーザーしか呼び出せない
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        // ユーザー削除処理
        System.out.println("ユーザーID: " + userId + " を削除しました");
    }
}
```

## 以下、参考動画のリンク

[Spring Security入門！認証・認可の仕組みをわかりやすく解説【Spring Boot】](https://www.google.com/search?q=https://www.youtube.com/watch%3Fv%3DherWDaEuqXI)
