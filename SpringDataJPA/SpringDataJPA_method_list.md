## 📄SpringDataJPAで業務によく使うアノテーション・メソッド一覧

**_概要_**
Spring Data JPAで**業務上よく使われるアノテーション・メソッド**を、機能ごとに一覧で提供します。

> 🏢 **業務シナリオ**：会員（`Member`）が複数の注文（`Order`）を持つ、1対多の関係を扱う。

---

## 1. 🏗️ エンティティ定義系アノテーション

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Entity` | クラスをJPAの管理対象エンティティにする。 |
| 💡 **たまに使う** | `@Table(name = "テーブル名")` | クラス名とテーブル名が異なる場合に対応付けを明示する。 |
| 🔥 **よく使う** | `@Id` | 主キー項目を指定する。 |
| 🔥 **よく使う** | `@GeneratedValue(strategy = GenerationType.IDENTITY)` | 主キーの**自動採番方式**を指定する。 |
| 💡 **たまに使う** | `@Column(name, nullable, unique, length)` | カラムの詳細な属性（NULL許可・一意制約・桁数）を指定する。 |
| 💡 **たまに使う** | `@OneToMany(mappedBy = "member")` | 1対多の関連を定義する（多側のフィールド名を指定）。 |
| 💡 **たまに使う** | `@ManyToOne` / `@JoinColumn(name = "member_id")` | 多対1の関連と、外部キーのカラム名を定義する。 |

---

## 2. 🗄️ リポジトリ標準メソッド（`JpaRepository`継承で自動提供）

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `save(entity)` | 新規登録・更新（主キーの有無で自動判定）を行う。 |
| 🔥 **よく使う** | `findById(id)` | 主キーで1件検索する（戻り値は`Optional`）。 |
| 🔥 **よく使う** | `findAll()` | 全件検索する。 |
| 💡 **たまに使う** | `deleteById(id)` | 主キー指定で削除する。 |
| 💡 **たまに使う** | `existsById(id)` | 存在確認のみ行う（`findById`より効率的）。 |
| 💡 **たまに使う** | `count()` | 件数を取得する。 |

---

## 3. 🔍 クエリメソッド（命名規則による自動SQL生成）

| 優先度 | メソッド名の例 | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `findByEmail(String email)` | `WHERE email = ?`に相当するSQLを自動生成する。 |
| 💡 **たまに使う** | `findByStatusAndCreatedAtAfter(...)` | `AND`条件を**メソッド名の連結**で表現する。 |
| 💡 **たまに使う** | `findByNameOrderByCreatedAtDesc(...)` | 検索条件とソート順を同時に指定する。 |
| 💡 **たまに使う** | `existsByEmail(String email)` | 存在確認のみを行う（`boolean`が返る）。 |

---

## 4. 🛠️ カスタムクエリ

| 優先度 | アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@Query("SELECT ... FROM ... WHERE ...")` | メソッド名では表現しきれない**複雑な条件**をJPQLで直接記述する。 |
| 💡 **たまに使う** | `@Param("name")` | `@Query`内のプレースホルダーとメソッド引数を紐付ける。 |
| 💡 **たまに使う** | `@Modifying` | `UPDATE`/`DELETE`文を実行する`@Query`に必須で付与する。 |

---

## 🚀 具体的なコード例

### 1. 命名規則によるクエリメソッド

```java
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // WHERE member_id = ? AND status = ? に相当するSQLが自動生成される
    List<OrderEntity> findByMemberIdAndStatus(Long memberId, String status);
}
```

### 2. `@Query` によるカスタムクエリと `@Modifying`

- **目的:** 特定期間の注文を集計対象外にする（ステータス一括更新）。

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query("SELECT o FROM OrderEntity o WHERE o.member.id = :memberId ORDER BY o.createdAt DESC")
    List<OrderEntity> findRecentOrdersByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = 'CANCELLED' WHERE o.id = :orderId")
    void cancelOrder(@Param("orderId") Long orderId);
}
```
