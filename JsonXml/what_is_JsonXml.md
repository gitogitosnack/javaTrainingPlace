# JSON/XMLパースライブラリ（Jackson/Gson）とは？

JavaのオブジェクトとJSON（またはXML）形式のデータを**相互変換（シリアライズ／デシリアライズ）**するためのライブラリです。API連携やファイル入出力を扱うコードの大半で登場します。

Spring Bootは**Jackson**を標準で内蔵しており、`@RestController`が返すオブジェクトは自動的にJSONへ変換されています。**Gson**（Google製）も軽量な代替ライブラリとしてよく使われます。

### 🏭 イメージ：「翻訳家」

1. **シリアライズ（Javaオブジェクト → JSON）**
   - `ObjectMapper.writeValueAsString(obj)`で、Javaのオブジェクトを**JSON文字列**に変換する。
2. **デシリアライズ（JSON → Javaオブジェクト）**
   - `ObjectMapper.readValue(json, Class)`で、JSON文字列を**Javaのオブジェクト**に変換する。
3. **フィールドとJSONキーの対応付け**
   - デフォルトはフィールド名がそのままJSONキーになるが、`@JsonProperty`等で**名前を変えたり除外したり**できる。

---

## ✨ 主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **自動変換（Spring統合）** | `@RestController`のメソッドが返すオブジェクトは、Jacksonにより自動でJSONレスポンスに変換される。 |
| **アノテーションによるカスタマイズ** | `@JsonProperty`（キー名変更）、`@JsonIgnore`（除外）、`@JsonFormat`（日付形式）等で変換ルールを細かく制御できる。 |
| **未知のプロパティへの耐性** | 受信したJSONに未定義のフィールドが含まれていてもエラーにしない設定（`FAIL_ON_UNKNOWN_PROPERTIES`）が可能。 |
| **ネストしたオブジェクトの対応** | JSON内のオブジェクト・配列も、対応するJavaクラス・`List`にそのままマッピングできる。 |

---

## 📝 代表的なクラス・アノテーション

| 種類 | クラス/アノテーション | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **変換の起点** | `ObjectMapper` | JSON⇔Javaオブジェクトの変換を行う中心クラス。 | |
| **キー名変更** | `@JsonProperty("user_name")` | JSONのキー名とフィールド名を対応付ける。 | |
| **除外** | `@JsonIgnore` | このフィールドをJSONに含めない。 | |
| **日付形式** | `@JsonFormat(pattern="yyyy-MM-dd")` | 日付のフォーマットを指定する。 | |

### 🛠️ コード例

```java
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

class UserDto {
    @JsonProperty("user_name")
    private String userName;

    @JsonIgnore
    private String password; // JSONには出力されない

    public UserDto() {}
    public UserDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
}

public class JacksonExample {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        UserDto user = new UserDto("Taro", "secret");
        String json = mapper.writeValueAsString(user);
        System.out.println(json); // {"user_name":"Taro"}

        UserDto parsed = mapper.readValue("{\"user_name\":\"Hanako\"}", UserDto.class);
        System.out.println(parsed.getUserName()); // Hanako
    }
}
```

API連携のバグ調査では、まずDTOクラスの`@JsonProperty`や`@JsonIgnore`の付与状況を確認し、**実際にやり取りされるJSONの形**を把握するのが近道です。
