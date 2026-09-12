# Spring AOP（アスペクト指向プログラミング）とは？

Spring AOPは、ログ出力・トランザクション管理・権限チェックといった**複数のクラスに横断的に現れる関心事（横断的関心事）**を、業務ロジック本体から**分離して一箇所にまとめる**ための仕組みです。

既存コードで「メソッドの中に処理が書かれていないのに、なぜかログが出る／トランザクションが効いている」という場合、**裏でAOPが動いている**ことがほとんどです。仕組みを知らないと、この「見えない処理」の存在に気づけず調査が難航します。

### 🏭 イメージ：「本編に割り込むナレーション」

1. **本編（ビジネスロジック）**
   - 各Serviceクラスに書かれた、本来の業務処理。
2. **ナレーション（Aspect＝横断処理）**
   - 「このメソッドが呼ばれる**前**に」「呼ばれた**後**に」「**例外が起きたら**」といったタイミングで割り込む処理。
3. **プロキシによる実現**
   - Springは対象クラスの**代理（プロキシ）**を作り、本来のメソッド呼び出しの前後にAspectの処理を挟み込む。

`@Transactional`もAOPの仕組みを使って実現されている代表例です。メソッド呼び出し前にトランザクションを開始し、正常終了ならコミット、例外発生ならロールバックします。

---

## ✨ 主な特徴

| 特徴 | 説明 |
| :--- | :--- |
| **横断的関心事の分離** | ログ・トランザクション・権限チェックなどを、業務ロジックから切り離して一元管理できる。 |
| **Advice（処理の種類）** | `Before`（実行前）、`After`（実行後）、`Around`（前後を包む）、`AfterReturning`（正常終了時）、`AfterThrowing`（例外時）。 |
| **Pointcut（適用対象の指定）** | 「どのクラスの、どのメソッドに」処理を適用するかを、パッケージ・アノテーション等で指定する。 |
| **プロキシベース** | デフォルトでは対象クラスの**プロキシ（代理オブジェクト）**経由で処理が挟まれるため、**同一クラス内のメソッド呼び出し（`this.method()`）にはAOPが効かない**という制約がある。 |

---

## 📝 代表的なアノテーション

| 種類 | アノテーション | 説明 | 例 |
| :--- | :--- | :--- | :--- |
| **定義** | `@Aspect` | このクラスがAspect（横断処理の集合）であることを示す。 | |
| **適用範囲** | `@Pointcut("execution(...)")` | 処理を適用する対象メソッドを定義する。 | |
| **処理** | `@Around("pointcut()")` | メソッドの前後を丸ごと囲む処理を定義する。 | |
| **トランザクション** | `@Transactional` | メソッドをトランザクション境界として扱う。 | |

### 🛠️ コード例（実行時間を計測するAspect）

```java
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
class ExecutionTimeAspect {

    // com.example.service パッケージ配下の全メソッドが対象
    @Pointcut("execution(* com.example.service..*(..))")
    void serviceMethods() {}

    @Around("serviceMethods()")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed(); // 本来のメソッドを実行
        long time = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " 実行時間: " + time + "ms");
        return result;
    }
}
```

`@Transactional`のロールバック挙動を追う際は、「デフォルトでは**非チェック例外（`RuntimeException`）でのみロールバック**され、チェック例外ではロールバックされない」という点が特によく引っかかる仕様です。
