# Spring Batchとは？

Spring Batch（スプリングバッチ）は、**大量データの一括処理（バッチ処理）**を効率よく・堅牢に実装するためのSpringファミリーのフレームワークです。

「夜間に大量のCSVファイルをDBへ取り込む」「毎月末に集計処理を回す」といった、業務システムで頻出する**定期実行・大量データ処理**を、車輪の再発明をせずに実装できるのが最大の特徴です。

### 🏭 イメージ：「工場の生産ライン」

Spring Batchの処理は、以下の階層構造で構成されます。

1.  **Job（ジョブ）**
    - バッチ処理全体を表す**最上位の単位**です。1つ以上の`Step`から構成されます。
    - 例: 「日次売上データ取込ジョブ」

2.  **Step（ステップ）**
    - Jobを構成する**個々の処理単位**です。Jobは複数のStepを順番に（あるいは条件分岐しながら）実行します。
    - 例: 「CSV読込ステップ」→「集計ステップ」→「DB反映ステップ」

3.  **Chunk（チャンク）：Stepの中身**
    - Stepの処理方式の1つで、データを**一定件数（チャンクサイズ）ごとにまとめて処理**します。
    - `ItemReader`（読込）→ `ItemProcessor`（加工）→ `ItemWriter`（書込）の3段階で構成され、これを**チャンク単位でトランザクション管理**しながら繰り返します。

---

## ✨ Spring Batchの主な特徴

| 特徴                 | 説明                                                                                                                     |
| :------------------- | :------------------------------------------------------------------------------------------------------------------------- |
| **チャンク処理**     | 大量データを一定件数ずつ読込・加工・書込することで、**メモリ効率**よく大量データを処理できます。                          |
| **トランザクション管理** | チャンク単位で自動的にコミット/ロールバックされるため、**途中失敗時のデータ整合性**を保ちやすい設計になっています。       |
| **リトライ・スキップ** | 一時的なエラー（DB接続断など）は**自動リトライ**、不正データは**スキップして処理継続**、といった制御を設定だけで実現できます。 |
| **実行状態の管理**   | `JobRepository`が実行履歴・実行状態をDBに記録するため、**再実行（リスタート）**や実行結果の追跡が容易です。                |
| **宣言的な設定**     | `JobBuilder` / `StepBuilder`を使い、**「何を読み、どう加工し、どこに書くか」を宣言的に組み立てる**だけでバッチ処理を構築できます。 |

---

## 📝 主要コンポーネント

| 種類             | クラス／概念        | 説明                                                                 |
| :--------------- | :------------------- | :--------------------------------------------------------------------- |
| **実行単位**     | `Job`                 | バッチ処理全体を表す**最上位の実行単位**。                             |
| **実行単位**     | `Step`                | Jobを構成する**個々の処理ステップ**。                                  |
| **読込**         | `ItemReader<T>`       | データソース（CSV、DBなど）から**1件ずつデータを読み込む**。            |
| **加工**         | `ItemProcessor<I,O>`  | 読み込んだデータを**加工・変換・フィルタリング**する（省略可能）。        |
| **書込**         | `ItemWriter<T>`       | 加工済みデータを**チャンク単位でまとめて書き込む**（DB、ファイルなど）。 |
| **起動**         | `JobLauncher`         | Jobを**起動**する。パラメータ（`JobParameters`）を渡して実行する。      |
| **管理**         | `JobRepository`       | Job/Stepの**実行状態・履歴をDBに永続化**する。                          |

### 🛠️ コード例（Jobの基本構成）

```java
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SampleBatchConfig {

    // Stepの定義：チャンクサイズ10件ごとに read → process → write を繰り返す
    @Bean
    public Step sampleStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager) {
        return new StepBuilder("sampleStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(() -> null) // 実際はItemReaderを指定
                .writer(items -> {}) // 実際はItemWriterを指定
                .build();
    }

    // Jobの定義：Stepをつなげて1つのバッチ処理として組み立てる
    @Bean
    public Job sampleJob(JobRepository jobRepository, Step sampleStep) {
        return new JobBuilder("sampleJob", jobRepository)
                .start(sampleStep)
                .build();
    }
}
```

Spring Batchは、業務システムにおける**夜間バッチ・定期集計・大量データ移行**などを、堅牢かつ再利用可能な形で実装するために必須の機能となっています。

他に、Spring Batchの特定コンポーネントの使い方など、詳しく知りたい点はありますか？
