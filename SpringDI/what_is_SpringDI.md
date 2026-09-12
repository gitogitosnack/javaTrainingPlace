# DI（依存性注入）とアノテーション、Beanのスコープ・ライフサイクルとは？

**DI（Dependency Injection：依存性注入）**は、あるクラスが必要とする別のクラス（依存オブジェクト）を、**自分でnewせずに外部（Springコンテナ）から受け取る**設計手法です。

これにより、クラス同士が疎結合になり、テストのしやすさ・差し替えのしやすさが向上します。既存のSpringアプリケーションでは、**どのクラスがコンテナ管理下にあり、どこで何が注入されているか**を追えることが、コードリーディングの第一歩になります。

### 🏭 イメージ：「部品を自動で組み立てる工場」

1. **部品の登録（`@Component`等）**
   - `@Service`や`@Repository`を付けたクラスは、Springコンテナに**「部品（Bean）」として登録**される。
2. **組み立て（DIコンテナによる注入）**
   - `@Autowired`やコンストラクタ引数を通じて、必要な部品が**自動的に組み込まれる**。
3. **部品の生存期間（スコープ・ライフサイクル）**
   - 部品は基本的に**1つだけ生成され使い回される（シングルトン）**が、リクエストごとに作り直す設定も可能。生成直後・破棄直前に処理を挟むこともできる。

---

## ✨ 主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **ステレオタイプアノテーション** | `@Component`（汎用）、`@Service`（業務ロジック）、`@Repository`（永続化層）、`@Controller`/`@RestController`（画面/API層）で役割を明示する。 |
| **コンストラクタインジェクション推奨** | フィールドインジェクション（`@Autowired`をフィールドに付与）より、**不変性が保て、テストしやすい**ため現在の主流。 |
| **Beanスコープ** | `singleton`（デフォルト、アプリ内で1つ）と`prototype`（取得のたびに新規生成）が代表的。 |
| **ライフサイクルコールバック** | `@PostConstruct`（初期化直後）、`@PreDestroy`（破棄直前）で処理をフックできる。 |

---

## 📝 代表的なアノテーション

| 種類 | アノテーション | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **登録** | `@Service` | 業務ロジッククラスをBean登録する。 | |
| **注入** | コンストラクタ引数 | Spring 4.3以降は**`@Autowired`省略可**（コンストラクタが1つの場合）。 | |
| **区別** | `@Qualifier("name")` | 同じ型のBeanが複数ある場合に**注入対象を指定**する。 | |
| **スコープ** | `@Scope("prototype")` | Beanのスコープを変更する。 | |

### 🛠️ コード例（コンストラクタインジェクション）

```java
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
class UserRepository {
    String findNameById(Long id) {
        return "Taro"; // 実際はDBアクセス
    }
}

@Service
class UserService {
    private final UserRepository userRepository; // final = 不変

    // コンストラクタが1つだけなら @Autowired は省略可能
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String greet(Long id) {
        return "こんにちは、" + userRepository.findNameById(id) + "さん";
    }
}
```

`UserService`は`UserRepository`を**自分でnewしていない**点に注目してください。テスト時にはモックの`UserRepository`を差し替えて注入できます。
