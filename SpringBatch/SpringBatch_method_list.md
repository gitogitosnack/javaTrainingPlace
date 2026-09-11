## 📄Spring Batchで業務によく使うクラス・アノテーション一覧

**_概要_**
夜間バッチで**CSVファイルを読み込み、加工した上でDBに一括登録する**という、実務で頻出のシナリオを想定し、そこで使われる**クラス・アノテーション**を一覧で提供します。
登場するコンポーネントは、大きく以下の3つのフェーズに分類されます。

1.  **設定・起動フェーズ**: バッチ処理を有効化し、Jobを起動します。
2.  **Job/Step構築フェーズ**: 処理の流れ（Job・Step）を宣言的に組み立てます。
3.  **データ入出力フェーズ**: CSV読込・加工・DB書込を担う`ItemReader`/`ItemProcessor`/`ItemWriter`を実装します。

---

## 1. ⚙️ 設定・起動フェーズ

| 優先度 | クラス／アノテーション | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `@EnableBatchProcessing` | Spring Bootアプリで**Spring Batch機能を有効化**します。（Spring Boot 3系では自動設定される場合もあるため明示は状況次第） |
| 🔥 **よく使う** | `JobLauncher` | 構築した`Job`を**実行（起動）**するためのインターフェース。`run(job, jobParameters)`で起動します。 |
| 🔥 **よく使う** | `JobParameters` / `JobParametersBuilder` | Jobに渡す**実行時パラメータ**（対象日付、対象ファイルパスなど）を保持・生成します。 |
| 💡 **たまに使う** | `JobRepository` | Job/Stepの**実行履歴・状態をDBに永続化**するためのリポジトリ。Builder系APIの引数として渡されます。 |

---

## 2. 🏗️ Job/Step構築フェーズ

| 優先度 | クラス | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `JobBuilder` | `Step`を組み合わせて**1つのJobを構築**します。（`.start(step).next(step2)`のように連結） |
| 🔥 **よく使う** | `StepBuilder` | `chunk()`で**チャンクサイズ**を指定し、`reader`/`processor`/`writer`を組み合わせて**1つのStepを構築**します。 |
| 💡 **たまに使う** | `PlatformTransactionManager` | チャンク単位の**トランザクション制御**に使用します。`StepBuilder.chunk(size, transactionManager)`に渡します。 |
| ☠️ **使わない** | `JobExecutionListener` | Job開始前・終了後に**フック処理**（通知送信やログ出力など）を挟みたい場合にのみ使用します。 |

---

## 3. 📥📤 データ入出力フェーズ（CSV → DB取込のコア部分）

### 🔍 読込（Read）

| 優先度 | クラス | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `FlatFileItemReader<T>` | **CSV/固定長ファイル**を1行（1レコード）ずつ読み込み、指定した型にマッピングします。 |
| 🔥 **よく使う** | `DelimitedLineTokenizer` | CSVの**区切り文字（カンマなど）でフィールドを分割**します。`FlatFileItemReader`の内部で使用します。 |
| 💡 **たまに使う** | `BeanWrapperFieldSetMapper<T>` | 分割したフィールドを**JavaBean（DTO）にマッピング**します。 |

### 🔄 加工（Process）

| 優先度 | クラス | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `ItemProcessor<I, O>` | 読み込んだ1件のデータを**加工・変換・バリデーション**します（`process(item)`をラムダ実装）。`null`を返すとその件は**スキップ**されます。 |

### 📝 書込（Write）

| 優先度 | クラス | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `JdbcBatchItemWriter<T>` | JDBCの**バッチ更新（`addBatch`）**を利用して、チャンク単位で**一括INSERT/UPDATE**します。 |
| 💡 **たまに使う** | `RepositoryItemWriter<T>` | Spring Data JPAの**リポジトリ経由**でチャンク単位の一括保存を行います。 |
| 💡 **たまに使う** | `.chunk(size, transactionManager)` | Step構築時に、**何件ごとにread→process→writeをまとめてコミットするか**を指定します。 |

---

## 🚀 具体的なコード例（CSV取込バッチ）

### 1\. CSVの1行を表すDTO

- **目的:** CSVの各行（社員コード, 氏名, 給与）を保持するデータクラスを定義する。

```java
public class EmployeeCsvRecord {
    private String employeeCode;
    private String name;
    private int salary;

    // getter/setterは省略
}
```

---

### 2\. `FlatFileItemReader` によるCSV読込

- **目的:** `employees.csv`を1行ずつ読み込み、`EmployeeCsvRecord`にマッピングする（読込フェーズ）。

```java
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ClassPathResource;

public FlatFileItemReader<EmployeeCsvRecord> employeeItemReader() {
    return new FlatFileItemReaderBuilder<EmployeeCsvRecord>()
        .name("employeeItemReader")
        .resource(new ClassPathResource("employees.csv"))
        .delimited() // カンマ区切りとして解釈
        .names("employeeCode", "name", "salary") // CSV列とフィールドの対応
        .targetType(EmployeeCsvRecord.class) // マッピング先のDTO
        .linesToSkip(1) // ヘッダー行をスキップ
        .build();
}
```

---

### 3\. `ItemProcessor` による加工（バリデーション＆変換）

- **目的:** 給与が0円以下の不正データは除外し（`null`を返してスキップ）、氏名の前後空白をトリムする（加工フェーズ）。

```java
import org.springframework.batch.item.ItemProcessor;

public ItemProcessor<EmployeeCsvRecord, EmployeeCsvRecord> employeeItemProcessor() {
    return record -> {
        if (record.getSalary() <= 0) {
            return null; // nullを返すとこの1件はスキップされる
        }
        record.setName(record.getName().trim());
        return record;
    };
}
```

---

### 4\. `JdbcBatchItemWriter` によるDB一括登録

- **目的:** 加工済みデータを、チャンク単位でまとめてDBに一括INSERTする（書込フェーズ）。

```java
import javax.sql.DataSource;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;

public JdbcBatchItemWriter<EmployeeCsvRecord> employeeItemWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<EmployeeCsvRecord>()
        .dataSource(dataSource)
        .sql("INSERT INTO employees (employee_code, name, salary) "
           + "VALUES (:employeeCode, :name, :salary)")
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .build();
}
```

---

### 5\. Job/Stepとして組み立てる（一連の流れを通しで実行）

- **目的:** 上記のreader/processor/writerを`chunk(100, ...)`で結合し、100件ごとにコミットしながらCSV→DB取込を実行する。

```java
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EmployeeCsvImportJobConfig {

    @Bean
    public Step importEmployeeStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    DataSource dataSource) {
        return new StepBuilder("importEmployeeStep", jobRepository)
                .<EmployeeCsvRecord, EmployeeCsvRecord>chunk(100, transactionManager) // 100件ごとにread→process→writeをまとめてコミット
                .reader(employeeItemReader())
                .processor(employeeItemProcessor())
                .writer(employeeItemWriter(dataSource))
                .build();
    }

    @Bean
    public Job importEmployeeJob(JobRepository jobRepository, Step importEmployeeStep) {
        return new JobBuilder("importEmployeeJob", jobRepository)
                .start(importEmployeeStep) // Stepを1つだけ持つシンプルなJob
                .build();
    }
}
```

- 実行時は、`JobLauncher.run(importEmployeeJob, new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters())` のように`JobParameters`を付けて起動する（同一パラメータでの再実行を防ぐため、実行時刻などを一意な値として渡すのが定石）。

## 以下、参考リンク

[Spring Batch 公式リファレンス](https://docs.spring.io/spring-batch/reference/)
