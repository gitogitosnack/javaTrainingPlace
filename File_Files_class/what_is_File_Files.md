# File / Files / Pathとは？

Javaでローカルのファイルやフォルダ（ディレクトリ）を操作する際には、大きく分けて**2つのAPI**が存在します。

1.  **`java.io.File`**（Java 1.0〜の**旧世代API**）
2.  **`java.nio.file.Files` / `java.nio.file.Path`**（Java 7〜の**新世代API**、通称**NIO.2**）

どちらも「ファイルの存在確認」「フォルダ作成」「ファイル一覧取得」など同じような操作を目的としていますが、内部の設計思想と使い勝手が大きく異なります。

---

### 🏚️ イメージ：「`File`は旧道具、`Files`/`Path`は新道具」

| 観点 | `java.io.File` | `java.nio.file.Files` / `Path` |
| :--- | :--- | :--- |
| **登場時期** | Java 1.0（かなり古い） | Java 7（NIO.2として登場） |
| **エラー処理** | 失敗しても`boolean`（`true`/`false`）を返すだけで、**原因が分からない** | 失敗すると**具体的な例外**（`IOException`など）を投げるので原因を特定しやすい |
| **機能の豊富さ** | 最低限のファイル操作のみ | コピー・移動・シンボリックリンク・属性取得など**高機能** |
| **Stream API連携** | 不可（`listFiles()`は配列を返す） | 可能（`Files.list()`や`Files.walk()`は`Stream`を返す） |
| **設計** | すべての操作が`File`クラスのメソッドに集約 | パス表現（`Path`）と操作（`Files`）が**分離**されている |

---

## ✨ 使い分けの指針

**結論：これから新しくコードを書くなら、基本的に`Files` / `Path`（NIO.2）を使う。**

- ✅ **`Files` / `Path`を使うべき場面**
  - 新規に業務ロジックを実装するとき（**基本はこちら一択**）
  - ファイルコピー・移動、フォルダの再帰的な探索を行いたいとき
  - 例外の原因をしっかり捕捉してログに残したいとき
  - Stream APIと組み合わせてファイル一覧をフィルタリング・加工したいとき

- ⚠️ **`File`を使う場面**
  - 既存コード（レガシーAPI）が`File`型を要求しているとき（**やむを得ず**）
  - `File`オブジェクトを引数に取る古いライブラリと連携するとき

> 💡 `Path`と`File`は相互変換が可能です（`file.toPath()` / `path.toFile()`）。既存コードが`File`前提でも、内部処理だけ`Files`のメソッドを使う、という組み合わせもよく行われます。

---

## 🚀 代表的な使い方のコード例

### 1. `File`（旧API）でフォルダの存在確認と作成

```java
import java.io.File;

public class FileExample {
    public static void main(String[] args) {
        File dir = new File("C:/work/output");

        // フォルダが存在しなければ作成（真偽値でしか結果が分からない）
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println("フォルダ作成: " + created);
        } else {
            System.out.println("既にフォルダが存在します: " + dir.getAbsolutePath());
        }
    }
}
```

### 2. `Files` / `Path`（新API）でフォルダの存在確認と作成

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesExample {
    public static void main(String[] args) {
        Path dir = Paths.get("C:/work/output");

        try {
            if (!Files.exists(dir)) {
                // 中間フォルダが無くても一気に作成できる
                Files.createDirectories(dir);
                System.out.println("フォルダを作成しました: " + dir.toAbsolutePath());
            } else {
                System.out.println("既にフォルダが存在します: " + dir.toAbsolutePath());
            }
        } catch (IOException e) {
            // なぜ失敗したか（権限不足、パス不正など）が例外として明確に分かる
            System.err.println("フォルダ作成に失敗しました: " + e.getMessage());
        }
    }
}
```

同じ「フォルダ作成」でも、`Files`側は**失敗理由を例外で受け取れる**点が大きな違いです。

業務でローカルフォルダ操作（ファイル一覧取得・コピー・削除など）を行う具体的なメソッドと実例は、[`File_Files_method_list.md`](./File_Files_method_list.md)にまとめています。
