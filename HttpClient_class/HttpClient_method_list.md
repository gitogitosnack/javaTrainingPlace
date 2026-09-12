## 📄HTTPクライアント処理で業務によく使うメソッド一覧

**_概要_**
`RestTemplate`・`WebClient`・Java標準`HttpClient`のメソッドを一覧で提供します。

---

## 1. 📮 RestTemplate系（同期・レガシー）

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `getForObject(url, Class)` | GETリクエストを送り、レスポンスボディを指定クラスに変換する。 |
| 💡 **たまに使う** | `postForObject(url, request, Class)` | POSTリクエストを送信する。 |
| 💡 **たまに使う** | `exchange(url, method, entity, Class)` | ヘッダー等を細かく指定できる汎用メソッド。 |
| ☠️ **使わない** | 新規実装での`RestTemplate`利用 | 非推奨（メンテナンスモード）のため、新規開発では`WebClient`を使う。 |

---

## 2. 🌊 WebClient系（非同期・推奨）

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `get()` / `post()` | HTTPメソッドを指定してリクエストを開始する。 |
| 🔥 **よく使う** | `uri(String url, Object... vars)` | リクエスト先URL（パスパラメータ埋め込み可）を指定する。 |
| 🔥 **よく使う** | `retrieve()` | レスポンスの取得を開始する（エラー時は例外を投げる標準的な経路）。 |
| 🔥 **よく使う** | `bodyToMono(Class<T>)` | レスポンスボディを単一の結果（`Mono`）に変換する。 |
| 💡 **たまに使う** | `bodyToFlux(Class<T>)` | レスポンスボディを複数件の結果（`Flux`）に変換する。 |
| 💡 **たまに使う** | `onStatus(predicate, handler)` | 特定のステータスコード（4xx/5xx等）に応じたエラーハンドリングを定義する。 |
| ☠️ **使わない** | `.block()` の多用 | 非同期の利点を捨てて同期的に待つため、リアクティブなアプリでは多用を避ける。 |

---

## 3. 📡 Java標準HttpClient系

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `HttpClient.newHttpClient()` | クライアントインスタンスを生成する。 |
| 💡 **たまに使う** | `HttpRequest.newBuilder().uri(...).build()` | リクエストを組み立てる。 |
| 💡 **たまに使う** | `client.send(request, BodyHandlers.ofString())` | 同期的にリクエストを送信する。 |
| ☠️ **使わない** | `client.sendAsync(...)` | 非同期送信（`CompletableFuture`を返す）。特殊な要件でのみ使用。 |

---

## 🚀 具体的なコード例

### 1. `WebClient` のエラーハンドリング

- **目的:** 外部APIが4xx/5xxエラーを返した場合に、独自の例外に変換する。

```java
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class WebClientErrorHandlingExample {
    static class ExternalApiException extends RuntimeException {
        public ExternalApiException(String message) { super(message); }
    }

    public String callExternalApi() {
        WebClient client = WebClient.create("https://api.example.com");

        return client.get()
            .uri("/resource")
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> Mono.error(new ExternalApiException("外部API呼び出しに失敗しました")))
            .bodyToMono(String.class)
            .block();
    }
}
```

### 2. Java標準`HttpClient`による同期GETリクエスト

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StandardHttpClientExample {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.example.com/users/1"))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("ステータス: " + response.statusCode());
        System.out.println("ボディ: " + response.body());
    }
}
```
