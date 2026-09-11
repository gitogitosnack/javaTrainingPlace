## 📄Apache POIで業務上よく使うメソッド一覧

**_概要_**
Apache POIの全APIを網羅するのではなく、**業務で実際によく使うメソッド**を、代表的な2つの業務シナリオに沿って一覧で提供します。

1.  **帳票出力シナリオ**: DBから取得した一覧データをExcelファイルとして書き出す。
2.  **データ取込シナリオ**: ユーザーがアップロードしたExcelファイルを読み込んでデータを取得する。

---

## 1. 📝 Excel書き込み（帳票出力）で使うメソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `new XSSFWorkbook()` | 新規に**空のExcelブック**（`.xlsx`）を作成します。 |
| 🔥 **よく使う** | `workbook.createSheet(String name)` | 指定した**名前のシート**をブックに追加します。 |
| 🔥 **よく使う** | `sheet.createRow(int rownum)` | シートに**指定した行番号（0始まり）の行**を作成します。 |
| 🔥 **よく使う** | `row.createCell(int column)` | 行に**指定した列番号（0始まり）のセル**を作成します。 |
| 🔥 **よく使う** | `cell.setCellValue(...)` | セルに**値を設定**します（`String`, `double`, `boolean`, `Date`などをオーバーロードで受け付ける）。 |
| 💡 **たまに使う** | `workbook.createCellStyle()` | **セルの書式**（太字、罫線、背景色、日付フォーマットなど）を定義する`CellStyle`を作成します。 |
| 💡 **たまに使う** | `cell.setCellStyle(CellStyle style)` | セルに**書式を適用**します（ヘッダー行を太字にするなど）。 |
| 💡 **たまに使う** | `sheet.autoSizeColumn(int column)` | 列幅を**内容に合わせて自動調整**します。 |
| 🔥 **よく使う** | `workbook.write(OutputStream out)` | 組み立てた内容を**実際のファイル（ストリーム）に書き出し**ます。 |
| 🔥 **よく使う** | `workbook.close()` | 使用したリソースを**解放**します（`try-with-resources`推奨）。 |

---

## 2. 📖 Excel読み込み（データ取込）で使うメソッド

| 優先度 | メソッド | 説明 |
| :--- | :--- | :--- |
| 🔥 **よく使う** | `new XSSFWorkbook(InputStream is)` | アップロードされた`.xlsx`ファイルの**ストリームからブックを読み込み**ます。 |
| 🔥 **よく使う** | `workbook.getSheetAt(int index)` | 指定した**インデックスのシート**を取得します（1枚目は`0`）。 |
| 🔥 **よく使う** | `sheet.getLastRowNum()` | シート内の**最後の行番号**を取得します（ループの終端判定に使用）。 |
| 🔥 **よく使う** | `row.getCell(int column)` | 行から**指定した列のセル**を取得します。 |
| 🔥 **よく使う** | `cell.getCellType()` | セルの**データ型**（文字列/数値/真偽値/空など）を判定します。値取得前の分岐に必須。 |
| 🔥 **よく使う** | `cell.getStringCellValue()` | セルの値を**文字列として**取得します。 |
| 🔥 **よく使う** | `cell.getNumericCellValue()` | セルの値を**数値（double）として**取得します。 |
| 💡 **たまに使う** | `cell.getDateCellValue()` | セルの値を**日付（Date）として**取得します（書式が日付設定されている場合）。 |
| 💡 **たまに使う** | `sheet.iterator()` / 拡張for文 | シートの**全行を順番に走査**します。 |
| ☠️ **使わない** | `HSSFWorkbook`での読み込み | 旧形式`.xls`専用。業務では基本的に`.xlsx`（`XSSFWorkbook`）を使うため出番が少ない。 |

---

## 🚀 具体的なコード例（業務シナリオ）

### 1\. 帳票出力：一覧データをExcelファイルとして書き出す

- **目的:** DBから取得した社員一覧（想定）を、ヘッダー付きのExcelファイルとして出力する。

```java
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExportEmployeeListExample {

    // DBから取得したデータを想定した簡易レコード
    record Employee(String name, int age, String department) {}

    public static void main(String[] args) throws IOException {
        List<Employee> employees = Arrays.asList(
            new Employee("山田太郎", 34, "営業部"),
            new Employee("佐藤花子", 28, "開発部"),
            new Employee("鈴木一郎", 41, "総務部")
        );

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("社員一覧");

            // ヘッダー行のスタイル（太字）を用意
            CellStyle headerStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);

            // 1行目：ヘッダー
            Row headerRow = sheet.createRow(0);
            String[] headers = {"氏名", "年齢", "所属部署"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 2行目以降：データ本体
            int rowIndex = 1;
            for (Employee emp : employees) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(emp.name());
                row.createCell(1).setCellValue(emp.age()); // int -> double へ自動変換される
                row.createCell(2).setCellValue(emp.department());
            }

            // 列幅を内容に合わせて自動調整
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream("employee_list.xlsx")) {
                workbook.write(fos); // ファイルへ書き出し
            }
        }

        System.out.println("employee_list.xlsx を出力しました。");
    }
}
```

---

### 2\. データ取込：アップロードされたExcelファイルを読み込む

- **目的:** 上で出力した`employee_list.xlsx`を読み込み、1行ずつデータを取得する。
- **ポイント:** `getCellType()`でセルの型を判定してから、対応する`get〇〇CellValue()`を呼び出すのが定石。

```java
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ImportEmployeeListExample {
    public static void main(String[] args) throws IOException {
        try (FileInputStream fis = new FileInputStream("employee_list.xlsx");
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) { // ストリームからブックを読み込み

            Sheet sheet = workbook.getSheetAt(0); // 1枚目のシートを取得

            // 1行目（ヘッダー）はスキップし、2行目からデータを読む
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue; // 空行はスキップ

                String name = getCellValueAsString(row.getCell(0));
                String age = getCellValueAsString(row.getCell(1));
                String department = getCellValueAsString(row.getCell(2));

                System.out.println("氏名: " + name + " / 年齢: " + age + " / 部署: " + department);
            }
        }
    }

    // セルの型を判定し、文字列として値を取り出す共通メソッド
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue()); // 数値は文字列化
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case BLANK -> "";
            default -> "";
        };
    }
}
```

## 以下、公式ドキュメントのリンク

[Apache POI - the Java API for Microsoft Documents](https://poi.apache.org/)
