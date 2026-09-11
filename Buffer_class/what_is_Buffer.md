# Buffered系クラス（BufferedReader / BufferedWriter）とは？

Java標準の入出力（I/O）APIには、`FileReader`や`FileWriter`のように**ファイルへ直接1文字ずつアクセスするクラス**があります。しかし、これらをそのまま使うと、**ディスクアクセス（システムコール）が発生する回数が非常に多くなり**、処理が遅くなってしまいます。

`BufferedReader` / `BufferedWriter`は、この問題を解決するための**ラッパークラス（デコレーター）**です。内部に**メモリ上のバッファ（作業領域）**を持ち、まとめてデータを読み書きすることで、**ディスクアクセスの回数を大幅に削減**し、I/O処理を**高速化**します。

### 🏭 イメージ：「まとめ買い」

- `FileReader`だけで1文字ずつ読む ＝ コンビニに1個ずつお菓子を買いに行く（**行き来の回数が多くて非効率**）
- `BufferedReader`で読む ＝ 一度にまとめて大量に買ってきて、家の棚（バッファ）から必要な分だけ取り出す（**効率的**）

---

## 🔗 FileReader/FileWriterとの違い

| 項目 | `FileReader` / `FileWriter` | `BufferedReader` / `BufferedWriter` |
| :--- | :--- | :--- |
| アクセス単位 | 1文字ずつファイルへ直接アクセス | 内部バッファ単位でまとめてアクセス |
| 速度 | 遅い（特に大きいファイルで顕著） | 速い（ディスクアクセス回数を削減） |
| 便利メソッド | ほぼなし | `readLine()`（1行読み込み）、`newLine()`（改行）など |
| 使い方 | 単体で使うことも可能 | **他のReader/Writerをラップして使う**のが基本 |

このように、`BufferedReader`/`BufferedWriter`は**単体では使わず**、`FileReader`や`FileWriter`など**元となるストリームを「包む（ラップする）」**形で使用します。

```java
// FileReaderを、BufferedReaderでラップする
BufferedReader br = new BufferedReader(new FileReader("sample.txt"));
```

---

## ⚠️ try-with-resourcesによるリソース管理の重要性

`BufferedReader`や`BufferedWriter`は、ファイルというOSレベルのリソース（ハンドル）を保持しています。**`close()`を呼び忘れると、リソースリーク**（ファイルが開いたままになる、書き込んだ内容がバッファに残ったままディスクに反映されない等）の原因になります。

そのため、Java 7以降は**try-with-resources構文**を使い、**ブロックを抜けるときに自動的に`close()`を呼び出す**書き方が推奨されます。

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryWithResourcesExample {
    public static void main(String[] args) {
        // try()内で生成したリソースは、tryブロックを抜けると自動的にclose()される
        try (BufferedReader br = new BufferedReader(new FileReader("sample.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ここでは既にbrはclose()されている
    }
}
```

---

## ✨ 主な特徴まとめ

| 特徴 | 説明 |
| :--- | :--- |
| **ラッパークラス** | `FileReader`/`FileWriter`など、元となるストリームを包んで機能を拡張する。 |
| **バッファリング** | 内部にメモリ上のバッファを持ち、ディスクアクセスの回数を削減して高速化する。 |
| **行単位の操作** | `readLine()`（読み込み）、`newLine()`（改行書き込み）など、行単位の便利メソッドを提供する。 |
| **要クローズ** | ファイルというリソースを保持するため、`try-with-resources`で確実にクローズする必要がある。 |

バイナリデータ（画像ファイルなど）を扱う場合は、文字用の`BufferedReader`/`BufferedWriter`ではなく、バイト列用の`BufferedInputStream`/`BufferedOutputStream`を使用します。考え方（バッファリングによる高速化、要クローズ）は共通です。

他に、Buffered系クラスの具体的な使い方など、詳しく知りたい点はありますか？
