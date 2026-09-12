# HTTPクライアント処理とは？

外部Web API（他システムや外部サービス）を呼び出すための仕組みです。Spring/Javaには複数の選択肢があり、既存コードがどれを採用しているかによって**書き方も非同期・同期の性質も大きく異なります**。

### 🏭 イメージ：「郵便配達」

1. **リクエストの作成（手紙を書く）**
   - URL・HTTPメソッド・ヘッダー・ボディを組み立てる。
2. **送信と応答待ち（配達して返事を待つ）**
   - 同期的に応答を待つ方式と、非同期（応答を待たず別処理を続ける）方式がある。
3. **レスポンスの処理（返事を読む）**
   - ステータスコードの確認、ボディのJSONパース、エラー時のハンドリングを行う。

---

## ✨ 主な選択肢の比較

| クラス | 特徴 | 現在の位置づけ |
| :--- | :--- | :--- |
| **`RestTemplate`** | Spring製の**同期**HTTPクライアント。書き方がシンプルで歴史が長い。 | **非推奨（メンテナンスモード）**だが既存コードには大量に残っている。 |
| **`WebClient`**（Spring WebFlux） | **非同期・リアクティブ**対応のHTTPクライアント。`Mono`/`Flux`で結果を扱う。 | 現在の**推奨**。同期的に使うことも可能（`.block()`）。 |
| **`java.net.http.HttpClient`**（Java標準） | Java 11で導入された標準ライブラリ。フレームワーク非依存。 | Spring以外のプロジェクトや、軽量な用途で使われる。 |

---

## 📝 代表的なメソッド

| 種類 | メソッド | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **RestTemplate** | `getForObject(url, Class)` | GETリクエストを送り、指定クラスに変換して受け取る。 | |
| **WebClient** | `get().uri(url).retrieve().bodyToMono(Class)` | GETリクエストを送り、`Mono`（非同期の結果コンテナ）で受け取る。 | |
| **標準HttpClient** | `HttpClient.newHttpClient().send(request, handler)` | リクエストを送信し、同期的にレスポンスを受け取る。 | |

### 🛠️ コード例（WebClientでのGETリクエスト）

```java
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientExample {
    record UserDto(Long id, String name) {}

    public void fetchUser() {
        WebClient client = WebClient.create("https://api.example.com");

        UserDto user = client.get()
            .uri("/users/{id}", 1)
            .retrieve()                    // レスポンスの取得を開始
            .bodyToMono(UserDto.class)      // ボディをUserDtoに変換
            .block();                       // 同期的に結果を待つ（本来は非同期で扱うのが推奨）

        System.out.println(user.name());
    }
}
```

既存コードで`RestTemplate`が使われていても、**新規実装では`WebClient`が推奨されている**ことを知っておくと、改修方針の判断材料になります。
