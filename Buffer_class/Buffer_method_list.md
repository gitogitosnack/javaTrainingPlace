## 📄 業務でよく使うBuffered系メソッド一覧

**_概要_**
業務では、`BufferedReader`/`BufferedWriter`は主に**「ローカルのテキストファイル（CSV・ログなど）を1行ずつ読み込んで処理し、結果を別のファイルへ出力する」**というシナリオで使用します。
ここでは、その業務シナリオを想定しながら、**実務でよく使うメソッド**を一覧で紹介します。

---

## 1. 📖 読み込み系メソッド（`BufferedReader`）

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `readLine()` | ファイルから**1行**を`String`として読み込みます。**ファイルの終端に達すると`null`を返す**ため、`while`ループの終了条件によく使われます。 |
| 💡 **たまに使う** | `lines()` | ファイルの全行を**`Stream<String>`**として取得します。Stream APIの`filter`や`map`と組み合わせて、行単位のフィルタリング・加工がしやすくなります。 |
| ☠️ **使わない** | `read()` | 1**文字**だけ読み込みます（`int`で返る）。業務での行単位処理には不向きで、ほぼ使いません。 |

## 2. ✍️ 書き込み系メソッド（`BufferedWriter`）

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `write(String str)` | 指定した文字列をバッファに書き込みます。**改行は自動で入らない**ため、`newLine()`と組み合わせるのが基本です。 |
| 🔥 **よく使う** | `newLine()` | OS標準の改行コードを書き込みます。`write()`の後に呼び出し、**1行分の出力**を完成させます。 |
| 💡 **たまに使う** | `flush()` | バッファの内容を**強制的にディスクへ書き出し**ます。ログをリアルタイムで確認したい場合など、`close()`前に途中経過を反映させたい時に使用します。 |

## 3. 🔒 リソース管理

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `close()`（try-with-resourcesでの暗黙呼び出し） | ストリームを閉じ、保持していたリソース（ファイルハンドル）を解放します。**明示的に呼ぶのではなく、`try-with-resources`構文に任せるのが業務での基本**です。 |
| 🔥 **よく使う** | `new BufferedReader(new FileReader(path))` | `FileReader`（文字読み込みストリーム）を`BufferedReader`で**ラップ**する、最も基本的な生成方法です。文字コードを指定したい場合は`InputStreamReader`と組み合わせます。 |

---

## 🚀 具体的なコード例（業務シナリオ）

### 1\. ローカルのCSVファイルを1行ずつ読み込んで処理する

- **目的:** ローカルに置かれた`data.csv`を1行ずつ読み込み、カンマ区切りで分割して内容を確認する（`readLine()`）。

<!-- end list -->

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CsvReadExample {
    public static void main(String[] args) {
        String path = "data.csv";

        // try-with-resources: ブロックを抜けると自動的にclose()される
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            int lineNumber = 0;

            // readLine(): 1行読み込み、終端でnullを返す
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] columns = line.split(",");
                System.out.println(lineNumber + "行目: " + String.join(" / ", columns));
            }
        } catch (IOException e) {
            System.err.println("ファイル読み込みでエラーが発生しました: " + e.getMessage());
        }
    }
}
```

---

### 2\. Stream APIと組み合わせて行を絞り込む（`lines()`）

- **目的:** ログファイル`app.log`から、"ERROR"を含む行だけを抽出する。

<!-- end list -->

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class LogFilterExample {
    public static void main(String[] args) {
        String path = "app.log";

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            // lines(): 全行をStream<String>として取得し、filterでERROR行のみ抽出
            List<String> errorLines = br.lines()
                .filter(line -> line.contains("ERROR"))
                .collect(Collectors.toList());

            errorLines.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("ログ読み込みでエラーが発生しました: " + e.getMessage());
        }
    }
}
```

---

### 3\. 処理結果をテキストファイルへ出力する

- **目的:** 処理結果のリストを、1行ずつ`result.txt`へ書き込む（`write()` + `newLine()`）。

<!-- end list -->

```java
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ResultWriteExample {
    public static void main(String[] args) {
        List<String> results = Arrays.asList("処理完了: 田中", "処理完了: 鈴木", "処理完了: 佐藤");
        String outputPath = "result.txt";

        // 第2引数にtrueを渡すと、既存ファイルへの追記モードになる
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            for (String result : results) {
                bw.write(result);   // write(): 文字列を書き込む（改行は自動で入らない）
                bw.newLine();       // newLine(): 改行を書き込む
            }
            System.out.println("書き込みが完了しました: " + outputPath);
        } catch (IOException e) {
            System.err.println("ファイル書き込みでエラーが発生しました: " + e.getMessage());
        }
    }
}
```

---

### 4\. 読み込みと書き込みを組み合わせる（CSVを加工して別ファイルへ出力）

- **目的:** `data.csv`を読み込み、各行の先頭に連番を付けて`data_numbered.csv`へ出力する。

<!-- end list -->

```java
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CsvTransformExample {
    public static void main(String[] args) {
        String inputPath = "data.csv";
        String outputPath = "data_numbered.csv";

        // 2つのtry-with-resourcesリソースをまとめて管理
        try (BufferedReader br = new BufferedReader(new FileReader(inputPath));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {

            String line;
            int no = 1;
            while ((line = br.readLine()) != null) {
                bw.write(no + "," + line);
                bw.newLine();
                no++;
            }
            System.out.println("加工済みファイルを出力しました: " + outputPath);
        } catch (IOException e) {
            System.err.println("処理中にエラーが発生しました: " + e.getMessage());
        }
    }
}
```

## 以下、参考動画のリンク

[ファイル入出力(BufferedReader/BufferedWriter)をわかりやすく解説！【Java応用講座】](https://www.google.com/search?q=java+BufferedReader+BufferedWriter+tutorial)
