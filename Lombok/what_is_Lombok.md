# Lombokとは？

Lombokは、`getter`/`setter`/コンストラクタ/`toString`といった**定型的なボイラープレートコード**を、アノテーションを付けるだけで**コンパイル時に自動生成**してくれるライブラリです。

既存コードでフィールドしか書かれていないのに`getXxx()`や`setXxx()`が呼び出せている場合、多くは**Lombokが裏で生成している**ためです。ソースコード上に実体が無いので、初見では戸惑いやすいポイントです。

### 🏭 イメージ：「代筆してくれる秘書」

1. **フィールドだけ用意する**
   - 自分は必要なデータ項目（フィールド）だけを定義する。
2. **アノテーションで「これを書いて」と指示する**
   - `@Getter`と書けば、秘書（Lombok）が全フィールド分の`getXxx()`を代筆してくれる。
3. **コンパイル時に実際のコードが生成される**
   - 実行時ではなく**コンパイル時**にコードが埋め込まれるため、パフォーマンスへの影響はない。

---

## ✨ 主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **コード量の削減** | `getter`/`setter`/コンストラクタ等の定型コードを書かずに済む。 |
| **コンパイル時生成** | アノテーションプロセッサとして動作し、`.class`ファイルには通常のメソッドとして出力される。 |
| **IDE連携が必要** | IDEにLombokプラグインが入っていないと、生成されるはずのメソッドが**エディタ上で認識されず赤線が出る**ことがある。 |
| **可読性とのトレードオフ** | 便利な反面、**ソースコードに書かれていない処理**が実行されるため、初見のコードリーディングでは注意が必要。 |

---

## 📝 代表的なアノテーション

| 種類 | アノテーション | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **アクセサ** | `@Getter` / `@Setter` | 全フィールド分の`getXxx`/`setXxx`を生成する。 | |
| **コンストラクタ** | `@RequiredArgsConstructor` | `final`フィールドのみを引数に取るコンストラクタを生成する（**DIで多用**）。 | |
| **複合** | `@Data` | `@Getter`+`@Setter`+`@ToString`+`@EqualsAndHashCode`をまとめて付与する。 | |
| **生成パターン** | `@Builder` | Builderパターンのコードを自動生成する。 | |

### 🛠️ コード例

```java
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
class User {
    private final Long id;
    private final String name;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    // @Getter により getId(), getName() が自動生成される（ソース上には存在しない）
}

@RequiredArgsConstructor // final フィールドを引数に取るコンストラクタを自動生成
@Service
class UserService {
    private final UserRepositoryDummy userRepository; // コンストラクタが自動生成されDIが効く
}
```

改修時に「このクラスにはgetterが定義されていないはずなのにコンパイルが通る」と感じたら、まずクラス宣言の上に`@Getter`や`@Data`が付いていないか確認するのが定石です。
