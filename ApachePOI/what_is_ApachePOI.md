# Apache POIとは？

Apache POI（アパッチ・ポイ）は、**Java**から**Microsoft Office形式のファイル**（Excel: `.xlsx`/`.xls`、Word: `.docx`/`.doc`など）を**読み書きするためのライブラリ**です。

業務システムでは「一覧データをExcelでダウンロードしたい」「アップロードされたExcelファイルを読み込んでDBに登録したい」といった要件が非常に多く、Apache POIはその**帳票出力・データ取込機能の定番ライブラリ**として広く使われています。

### 🏭 イメージ：「Excelファイルをプログラムで操作するリモコン」

Excelを手作業で開かなくても、Javaのコードだけで**シートの作成・セルへの値の書き込み・既存ファイルの読み込み**などが行えます。

---

## ✨ Apache POIが業務でよく使われる理由

| 用途 | 説明 |
| :--- | :--- |
| **帳票出力** | DBから取得した一覧データをExcelファイルとして出力し、ユーザーにダウンロードさせる。 |
| **データ取込（インポート）** | ユーザーがアップロードしたExcelファイルを読み込み、内容をパースしてDBに登録する。 |
| **既存テンプレートへの差し込み** | あらかじめ用意したExcelテンプレート（罫線・書式設定済み）に値だけを差し込んで出力する。 |

---

## 🧱 主要なクラス構成（階層構造）

Apache POIのExcel操作（`org.apache.poi.ss.usermodel`パッケージ）は、以下の**階層構造**でオブジェクトを扱います。

```
Workbook（ブック全体）
 └─ Sheet（シート）
     └─ Row（行）
         └─ Cell（セル）
```

| クラス | 役割 |
| :--- | :--- |
| `Workbook` | Excelファイル**そのもの**を表すインターフェース。 |
| `Sheet` | ブック内の**1枚のシート**を表す。 |
| `Row` | シート内の**1行**を表す。 |
| `Cell` | 行内の**1つのセル**を表す。実際に値を読み書きする最小単位。 |

### 📦 `XSSFWorkbook` と `HSSFWorkbook` の違い

| クラス | 対応拡張子 | 説明 |
| :--- | :--- | :--- |
| `XSSFWorkbook` | `.xlsx`（Excel 2007以降） | 🔥 **よく使う**。現在の業務では基本的にこちらを使用する。 |
| `HSSFWorkbook` | `.xls`（Excel 97-2003） | ☠️ **使わない**。旧形式との互換性が必要な場合のみ使用。 |

`Workbook`インターフェースを実装しているため、生成時のクラスを切り替えるだけで、それ以降のコード（`Sheet`や`Cell`の操作）は共通で書ける。

---

## 📥 Maven依存関係

Excel操作（`.xlsx`）を行うには、`poi`と`poi-ooxml`の2つが必要。

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi</artifactId>
        <version>5.2.5</version>
    </dependency>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.5</version>
    </dependency>
</dependencies>
```

---

## 🚀 代表的な使い方（最小サンプル）

- **目的:** 新しいExcelファイルを作成し、シートに値を書き込んで保存する。

```java
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ApachePoiMinimalExample {
    public static void main(String[] args) throws IOException {
        // try-with-resourcesでWorkbookを自動クローズ
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("サンプルシート");

            Row row = sheet.createRow(0); // 0行目を作成
            row.createCell(0).setCellValue("氏名");
            row.createCell(1).setCellValue("年齢");

            try (FileOutputStream fos = new FileOutputStream("sample.xlsx")) {
                workbook.write(fos); // ファイルへ書き出し
            }
        }

        System.out.println("Excelファイルを作成しました。");
    }
}
```

具体的な業務シナリオ（帳票出力・データ取込）に沿ったメソッドの使い方は `ApachePOI_method_list.md` を参照。
