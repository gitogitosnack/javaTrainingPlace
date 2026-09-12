## 📄JSON/XML処理（Jackson/Gson）で業務によく使うもの一覧

**_概要_**
JSON変換ライブラリで**業務上よく使われるアノテーション・メソッド**を、機能ごとに一覧で提供します。

---

## 1. 🏷️ Jacksonアノテーション系

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@JsonProperty("json_key")` | JSONのキー名とJavaのフィールド名が異なる場合に対応付ける（スネークケース⇔キャメルケース変換等）。 |
| 🔥 **よく使う** | `@JsonIgnore` | このフィールドをJSON出力・入力の対象外にする（パスワード等の機密情報）。 |
| 💡 **たまに使う** | `@JsonInclude(JsonInclude.Include.NON_NULL)` | 値が`null`のフィールドをJSON出力から除外する。 |
| 💡 **たまに使う** | `@JsonFormat(pattern = "...")` | 日付・時刻フィールドの入出力フォーマットを指定する。 |
| 💡 **たまに使う** | `@JsonCreator` / `@JsonProperty`（コンストラクタ引数） | イミュータブルなクラス（`final`フィールドのみ）をデシリアライズする際のコンストラクタを指定する。 |
| ☠️ **使わない** | `@JsonAlias({"old_key"})` | 複数のJSONキー名を1つのフィールドで受け取れるようにする（互換性維持等の特殊用途）。 |

---

## 2. ⚙️ ObjectMapperのメソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `writeValueAsString(Object obj)` | JavaオブジェクトをJSON文字列に変換する。 |
| 🔥 **よく使う** | `readValue(String json, Class<T> clazz)` | JSON文字列をJavaオブジェクトに変換する。 |
| 💡 **たまに使う** | `readValue(String json, TypeReference<T>)` | `List<UserDto>`のようなジェネリクスを含む型に変換する際に使う。 |
| 💡 **たまに使う** | `convertValue(Object fromValue, Class<T> toClass)` | `Map`⇔独自クラスなど、Javaオブジェクト同士を変換する。 |
| 💡 **たまに使う** | `mapper.configure(feature, boolean)` | 未知プロパティを許容する等の**変換ルール全体の設定**を変更する。 |

---

## 3. 📦 Gson系（代替ライブラリ）

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 💡 **たまに使う** | `gson.toJson(Object obj)` | JavaオブジェクトをJSON文字列に変換する（Jacksonの`writeValueAsString`に相当）。 |
| 💡 **たまに使う** | `gson.fromJson(String json, Class<T>)` | JSON文字列をJavaオブジェクトに変換する（Jacksonの`readValue`に相当）。 |

---

## 🚀 具体的なコード例

### 1. ネストしたオブジェクト・配列のマッピング

- **目的:** 注文（`Order`）が複数の商品明細（`Item`）を持つJSONをパースする。

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class NestedJsonExample {
    record Item(String name, int quantity) {}
    record Order(String orderId, List<Item> items) {}

    public static void main(String[] args) throws Exception {
        String json = """
            {
              "orderId": "A001",
              "items": [
                {"name": "ペン", "quantity": 3},
                {"name": "ノート", "quantity": 1}
              ]
            }
            """;

        ObjectMapper mapper = new ObjectMapper();
        Order order = mapper.readValue(json, Order.class);

        System.out.println(order.orderId());       // A001
        System.out.println(order.items().size());  // 2
    }
}
```

### 2. 未知のプロパティを無視する設定

- **目的:** 外部APIのレスポンスに、こちらで定義していない項目が含まれていてもエラーにしない。

```java
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UnknownPropertyExample {
    record UserDto(String name) {}

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // 未知のフィールドがあってもエラーにしない
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String json = "{\"name\":\"Taro\", \"unknownField\":\"xxx\"}";
        UserDto user = mapper.readValue(json, UserDto.class);

        System.out.println(user.name()); // Taro
    }
}
```
