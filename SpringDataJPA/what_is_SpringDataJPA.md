# Spring Data JPA / O/Rマッパーとは？

Spring Data JPAは、**Javaのオブジェクトとデータベースのテーブルを対応付け（O/Rマッピング）**、SQLをほとんど書かずにデータアクセス処理を実現するための仕組みです。内部的にはJPA（Java Persistence API）の実装である**Hibernate**が使われることが一般的です。

現場では他にも**MyBatis**（SQLを直接記述する派）や**Doma2**（コンパイル時にSQLを検証する国産ライブラリ）が使われることもありますが、いずれも「オブジェクトとDBのマッピング」という目的は共通しています。

### 🏭 イメージ：「話すだけでSQLを書いてくれる通訳」

1. **エンティティ定義（テーブルの設計図）**
   - `@Entity`を付けたクラスが、DBのテーブルに対応する。
2. **リポジトリインターフェース（御用聞き）**
   - `JpaRepository`を継承したインターフェースを定義するだけで、`save`・`findById`などの基本的なCRUD処理が**自動的に実装される**。
3. **メソッド名からSQLを自動生成**
   - `findByEmail(String email)`のように**メソッド名の規則**に従うだけで、対応するSQLが自動生成される（実装クラスを書く必要がない）。

---

## ✨ 主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **リポジトリの自動実装** | `JpaRepository<Entity, ID>`を継承するだけで、基本的なCRUDメソッドが使えるようになる。 |
| **クエリメソッド** | `findByXxx`のような**メソッド名の命名規則**に従うだけで、SQL（正確にはJPQL）が自動生成される。 |
| **エンティティとテーブルのマッピング** | `@Entity`, `@Table`, `@Column`, `@Id`などのアノテーションで、クラスとDBの対応関係を定義する。 |
| **カスタムクエリ** | 複雑な条件は`@Query`アノテーションでJPQL/ネイティブSQLを直接記述できる。 |

---

## 📝 代表的なアノテーション・メソッド

| 種類 | アノテーション/メソッド | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **エンティティ** | `@Entity` / `@Table(name="users")` | クラスをテーブルに対応付ける。 | |
| **主キー** | `@Id` / `@GeneratedValue` | 主キー項目と採番方式を指定する。 | |
| **リレーション** | `@ManyToOne` / `@OneToMany` | テーブル間の関連（多対1、1対多）を表現する。 | |
| **リポジトリ** | `findById(id)` / `save(entity)` | 標準で使える基本的なCRUDメソッド。 | |

### 🛠️ コード例

```java
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

@Entity
@Table(name = "users")
class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    private String name;

    // getter/setter省略（実務ではLombokの@Getter/@Setterを使うことが多い）
}

// インターフェースを定義するだけで実装が自動生成される
interface UserRepository extends JpaRepository<UserEntity, Long> {
    // メソッド名から "WHERE email = ?" のSQLが自動生成される
    UserEntity findByEmail(String email);
}
```

改修作業では、まず**エンティティクラスとテーブル定義の対応**、次に**リポジトリのメソッド名からどんなSQLが発行されているか**を読み解くのが基本の流れです。
