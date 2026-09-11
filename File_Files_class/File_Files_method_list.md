## 📄File・Files・Pathで業務でよく使うメソッド一覧

**_概要_**
「**ローカルのフォルダ操作をしたい**」という業務シナリオを軸に、実務でよく使う`File`・`Files`・`Path`のメソッドを一覧で提供します。
想定シナリオは以下の通りです。

1.  **フォルダの存在確認・作成**: 出力先フォルダが無ければ作る。
2.  **フォルダ内のファイル一覧取得**: 対象フォルダ配下のファイルを探索する。
3.  **ファイルの絞り込み**: 特定の拡張子・条件のファイルだけを抽出する。
4.  **ファイルのコピー・移動・削除**: バックアップや後片付けを行う。
5.  **ファイルの中身の読み書き**: テキストファイルを読み込む・書き出す。

---

## 1. 📁 フォルダの存在確認・作成

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `Files.exists(Path path)` | 指定した**パスが存在するか**を確認します（`boolean`）。 |
| 🔥 **よく使う** | `Files.createDirectories(Path dir)` | **中間フォルダも含めて**一括でフォルダを作成します（既に存在する場合は何もしない）。 |
| 💡 **たまに使う** | `Paths.get(String first, String... more)` | 文字列から`Path`オブジェクトを生成します（`Path.of()`でも同様）。 |
| 💡 **たまに使う** | `File.mkdirs()` | （旧API）中間フォルダも含めてフォルダを作成します。作成失敗時は`false`が返るのみで原因不明。 |
| 💡 **たまに使う** | `File.exists()` | （旧API）ファイル・フォルダが存在するかを確認します。 |

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateOutputFolder {
    public static void main(String[] args) {
        Path outputDir = Paths.get("C:/work/report/2026-09");

        try {
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir); // 中間フォルダ(report, 2026-09)もまとめて作成
                System.out.println("出力フォルダを作成しました: " + outputDir);
            }
        } catch (IOException e) {
            System.err.println("フォルダ作成に失敗しました: " + e.getMessage());
        }
    }
}
```

---

## 2. 🔍 フォルダ内のファイル一覧取得・探索

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `Files.list(Path dir)` | 指定フォルダ**直下**のファイル・フォルダを`Stream<Path>`として取得します（サブフォルダの中身は含まない）。 |
| 💡 **たまに使う** | `Files.walk(Path start)` | 指定フォルダを**サブフォルダも含めて再帰的に**探索し、`Stream<Path>`として取得します。 |
| 💡 **たまに使う** | `Files.isDirectory(Path path)` | 指定パスが**フォルダかどうか**を判定します。 |
| 💡 **たまに使う** | `Files.isRegularFile(Path path)` | 指定パスが**通常のファイルかどうか**を判定します。 |
| ☠️ **使わない** | `File.listFiles()` | （旧API）フォルダ直下の一覧を`File[]`（配列）で取得します。Stream連携できないため新規実装では非推奨。 |

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListFilesExample {
    public static void main(String[] args) {
        Path targetDir = Paths.get("C:/work/report");

        // フォルダ直下のファイルのみ一覧化（サブフォルダは含まない）
        try (Stream<Path> stream = Files.list(targetDir)) {
            List<String> fileNames = stream
                .filter(Files::isRegularFile) // ファイルのみ抽出（フォルダは除外）
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());

            fileNames.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("一覧取得に失敗しました: " + e.getMessage());
        }
    }
}
```

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class WalkSubFoldersExample {
    public static void main(String[] args) {
        Path targetDir = Paths.get("C:/work/report");

        // サブフォルダも含めて再帰的に探索
        try (Stream<Path> stream = Files.walk(targetDir)) {
            stream.filter(Files::isRegularFile)
                  .forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("探索に失敗しました: " + e.getMessage());
        }
    }
}
```

---

## 3. 🎯 特定拡張子のファイルだけを抽出

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `Path.toString()` / `Path.getFileName()` | パス文字列やファイル名を取得し、`endsWith()`などで拡張子判定に使います。 |
| 🔥 **よく使う** | `Stream.filter(Predicate)` | Stream APIの`filter`を使い、**拡張子が一致するファイルのみ**を絞り込みます。 |

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterByExtensionExample {
    public static void main(String[] args) {
        Path targetDir = Paths.get("C:/work/report");

        try (Stream<Path> stream = Files.list(targetDir)) {
            // .xlsxファイルのみを抽出
            List<Path> excelFiles = stream
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".xlsx"))
                .collect(Collectors.toList());

            excelFiles.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("抽出に失敗しました: " + e.getMessage());
        }
    }
}
```

---

## 4. 📦 ファイルのコピー・移動・削除

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `Files.copy(Path src, Path dst, CopyOption... options)` | ファイルを**コピー**します。`StandardCopyOption.REPLACE_EXISTING`で上書き可能。 |
| 🔥 **よく使う** | `Files.delete(Path path)` | ファイル・空フォルダを**削除**します。存在しない場合は例外（`NoSuchFileException`）。 |
| 💡 **たまに使う** | `Files.deleteIfExists(Path path)` | 存在する場合のみ削除します（存在しなくても例外にならない）。 |
| 💡 **たまに使う** | `Files.move(Path src, Path dst, CopyOption... options)` | ファイルを**移動**（リネーム含む）します。 |
| ☠️ **使わない** | `File.renameTo(File dest)` | （旧API）ファイルの移動・リネームを行いますが、失敗理由が分からず環境依存の挙動もあるため非推奨。 |

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CopyAndDeleteExample {
    public static void main(String[] args) {
        Path src = Paths.get("C:/work/report/2026-09/summary.xlsx");
        Path backupDir = Paths.get("C:/work/backup");
        Path dst = backupDir.resolve(src.getFileName());

        try {
            Files.createDirectories(backupDir); // バックアップ先フォルダが無ければ作成

            // バックアップとしてコピー（既に同名ファイルがあれば上書き）
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("バックアップ完了: " + dst);

            // 元ファイルは不要になったので削除（存在しなくてもエラーにしない）
            Files.deleteIfExists(src);
            System.out.println("元ファイルを削除しました: " + src);
        } catch (IOException e) {
            System.err.println("コピー・削除に失敗しました: " + e.getMessage());
        }
    }
}
```

---

## 5. 📝 ファイルの中身の読み書き

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `Files.readAllLines(Path path)` | テキストファイルを**1行ずつのList**として読み込みます（小〜中サイズのファイル向け）。 |
| 🔥 **よく使う** | `Files.writeString(Path path, CharSequence content, OpenOption...)` | 文字列をファイルに**書き込み**ます（Java 11以降）。 |
| 💡 **たまに使う** | `Files.readString(Path path)` | ファイル全体を**1つの文字列**として読み込みます（Java 11以降）。 |
| 💡 **たまに使う** | `Files.lines(Path path)` | ファイルを1行ずつの`Stream<String>`として読み込みます（**大きいファイル向け**、遅延読み込み）。 |
| ☠️ **使わない** | `Files.write(Path path, byte[] bytes)` | バイト配列を直接書き込みます。文字列を扱う業務用途では`writeString`の方が簡潔。 |

```java
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ReadWriteFileExample {
    public static void main(String[] args) {
        Path logFile = Paths.get("C:/work/report/2026-09/result.log");

        try {
            // ファイルへの書き込み（追記モード、無ければ新規作成）
            Files.writeString(
                logFile,
                "処理が完了しました。\n",
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );

            // ファイルの読み込み（1行ずつList化）
            List<String> lines = Files.readAllLines(logFile, StandardCharsets.UTF_8);
            lines.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("読み書きに失敗しました: " + e.getMessage());
        }
    }
}
```

---

## 🚀 業務シナリオ通しコード例：フォルダ整理バッチ

- **目的:** 「出力フォルダを用意 → 対象フォルダのファイル一覧取得 → `.csv`ファイルだけをバックアップフォルダへコピー → コピー済みの元ファイルを削除」という一連の業務フローを1本にまとめる。

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FolderCleanupBatch {
    public static void main(String[] args) {
        Path targetDir = Paths.get("C:/work/report/2026-09");
        Path backupDir = Paths.get("C:/work/backup/2026-09");

        try {
            // 1. バックアップ先フォルダが無ければ作成
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
                System.out.println("バックアップフォルダを作成しました: " + backupDir);
            }

            // 2. 対象フォルダ内の.csvファイルのみを抽出
            List<Path> csvFiles;
            try (Stream<Path> stream = Files.list(targetDir)) {
                csvFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .collect(Collectors.toList());
            }

            // 3. 各csvファイルをバックアップフォルダへコピーし、元ファイルを削除
            for (Path src : csvFiles) {
                Path dst = backupDir.resolve(src.getFileName());
                Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(src);
                System.out.println("移動完了: " + src.getFileName());
            }

            System.out.println("フォルダ整理バッチが完了しました。対象件数: " + csvFiles.size());
        } catch (IOException e) {
            System.err.println("バッチ処理中にエラーが発生しました: " + e.getMessage());
        }
    }
}
```
