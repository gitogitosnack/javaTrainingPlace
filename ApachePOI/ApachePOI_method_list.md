## 📄Apache POIで業務上よく使うメソッド一覧

**_概要_**
Apache POIの全APIを網羅するのではなく、**業務で実際によく使うメソッド**を、代表的な2つの業務シナリオに沿って一覧で提供します。

1.  **帳票出力シナリオ**: DBから取得した一覧データをExcelファイルとして書き出す。
2.  **データ取込シナリオ**: ユーザーがアップロードしたExcelファイルを読み込んでデータを取得する。

---

## 1. 📝 Excel書き込み（帳票出力）で使うメソッド

| 優先度            | メソッド                             | 説明                                                                                                                                                                                   |
| :---------------- | :----------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 🔥 **よく使う**   | `WorkbookFactory.create(true)`       | 新規に**空のExcelブック**を作成します。引数`true`で`.xlsx`形式（内部的に`XSSFWorkbook`）を生成。戻り値は共通インターフェース`Workbook`型なので、呼び出し側は具象クラスを意識しません。 |
| 🔥 **よく使う**   | `workbook.createSheet(String name)`  | 指定した**名前のシート**をブックに追加します。                                                                                                                                         |
| 🔥 **よく使う**   | `sheet.createRow(int rownum)`        | シートに**指定した行番号（0始まり）の行**を作成します。                                                                                                                                |
| 🔥 **よく使う**   | `row.createCell(int column)`         | 行に**指定した列番号（0始まり）のセル**を作成します。                                                                                                                                  |
| 🔥 **よく使う**   | `cell.setCellValue(...)`             | セルに**値を設定**します（`String`, `double`, `boolean`, `Date`, `Calendar`, `LocalDate`, `LocalDateTime`, `RichTextString`などをオーバーロードで受け付ける）。                        |
| 💡 **たまに使う** | `cell.setCellFormula(String formula)` | セルに**数式**（例: `"A1+B1"`、先頭の`=`は不要）を設定します。計算結果を画面で確認するには、Excelで開いた際の自動計算に任せるか、`FormulaEvaluator`で評価する必要があります。            |
| 💡 **たまに使う** | `workbook.createCellStyle()`         | **セルの書式**（太字、罫線、背景色、日付フォーマットなど）を定義する`CellStyle`を作成します。                                                                                          |
| 💡 **たまに使う** | `cell.setCellStyle(CellStyle style)` | セルに**書式を適用**します（ヘッダー行を太字にするなど）。                                                                                                                             |
| 💡 **たまに使う** | `sheet.autoSizeColumn(int column)`   | 列幅を**内容に合わせて自動調整**します。                                                                                                                                               |
| 🔥 **よく使う**   | `workbook.write(OutputStream out)`   | 組み立てた内容を**実際のファイル（ストリーム）に書き出し**ます。                                                                                                                       |
| 💡 **たまに使う** | `Files.newOutputStream(Path path)`   | `java.nio.file.Files`が提供する、指定した**パスへの`OutputStream`を取得**するメソッド。`new FileOutputStream(String)`よりモダンな書き方で、`workbook.write(...)`にそのまま渡せます。   |
| 🔥 **よく使う**   | `workbook.close()`                   | 使用したリソースを**解放**します（`try-with-resources`推奨）。                                                                                                                         |

> 📌 **モダンな書き方:**
> - `new XSSFWorkbook()`のように具象クラスを直接`new`するのではなく、`WorkbookFactory.create(...)`から共通インターフェース`Workbook`を受け取るのが現代的なスタイルです。書き込み先を`.xls`に変えたくなった場合も、呼び出し元のコードをほぼ変えずに済みます。
> - ファイル出力にも`new FileOutputStream(String)`ではなく、`java.nio.file.Files.newOutputStream(Path)`を使うのが現代的です。`Path`ベースのAPIと統一的に扱え、発生する例外も`NoSuchFileException`など具体的なサブクラスになるため原因を特定しやすくなります。

---

## 2. 📖 Excel読み込み（データ取込）で使うメソッド

| 優先度            | メソッド                                                                       | 説明                                                                                                                                                                                         |
| :---------------- | :----------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 🔥 **よく使う**   | `WorkbookFactory.create(File file)` / `WorkbookFactory.create(InputStream is)` | アップロードされたExcelファイルを読み込みます。ファイルの中身（シグネチャ）を見て**`.xlsx`と`.xls`を自動判別**し、適切な`Workbook`実装を返してくれるため、拡張子を気にする必要がありません。 |
| 💡 **たまに使う** | `Files.newInputStream(Path path)`                                              | `java.nio.file.Files`が提供する、指定した**パスからの`InputStream`を取得**するメソッド。`new FileInputStream(String)`よりモダンな書き方で、`WorkbookFactory.create(InputStream is)`にそのまま渡せます。 |
| 🔥 **よく使う**   | `workbook.getSheetAt(int index)`                                               | 指定した**インデックスのシート**を取得します（1枚目は`0`）。                                                                                                                                 |
| 🔥 **よく使う**   | `sheet.getLastRowNum()`                                                        | シート内の**最後の行番号**を取得します（ループの終端判定に使用）。                                                                                                                           |
| 🔥 **よく使う**   | `sheet.getRow(int rownum)`                                                     | シートから**指定した行番号（0始まり）の行**を取得します。データが無い行は`null`が返るため`null`チェックが必要。                                                                              |
| 🔥 **よく使う**   | `row.getCell(int column)`                                                      | 行から**指定した列のセル**を取得します。                                                                                                                                                     |
| 🔥 **よく使う**   | `cell.getCellType()`                                                           | セルの**データ型**（文字列/数値/真偽値/空など）を判定します。値取得前の分岐に必須。判定できる型の一覧は[Appendix: CellTypeの種類](#appendix-celltypeの種類)を参照。                          |
| 🔥 **よく使う**   | `cell.getStringCellValue()`                                                    | セルの値を**文字列として**取得します。                                                                                                                                                       |
| 💡 **たまに使う** | `cell.getRichStringCellValue()`                                                | セルの値を`RichTextString`として取得します。`getStringCellValue()`と異なり、**1セル内で部分的に異なるフォント/色などの書式**を保持したまま値を扱いたい場合に使用します。                     |
| 🔥 **よく使う**   | `cell.getNumericCellValue()`                                                   | セルの値を**数値（double）として**取得します。                                                                                                                                               |
| 💡 **たまに使う** | `cell.getDateCellValue()`                                                      | セルの値を**日付（`java.util.Date`）として**取得します（書式が日付設定されている場合）。                                                                                                     |
| 💡 **たまに使う** | `cell.getLocalDateTimeCellValue()`                                             | セルの値を**`java.time.LocalDateTime`として**取得します。`getDateCellValue()`の`java.time`版で、現代のJavaコードでは`Date`より扱いやすくこちらが推奨されます。                              |
| 💡 **たまに使う** | `cell.getCellFormula()`                                                        | セルに入力された**数式の文字列**（例: `"A1+B1"`）を取得します。`cell.getCellType()`が`FORMULA`の場合に使用。計算結果自体は`get〇〇CellValue()`系で別途取得します。                          |
| 💡 **たまに使う** | `workbook.getCreationHelper().createFormulaEvaluator()` | 数式を**その場で再計算するための`FormulaEvaluator`**を取得します。Excel側の最終保存時の計算結果（キャッシュ値）に頼らず、確実に最新の計算結果を得たい場合に使用します。                     |
| 💡 **たまに使う** | `formulaEvaluator.evaluateInCell(Cell cell)`                                   | 指定セルの数式を**評価して結果の値でセルを上書き**します（セルの型もFORMULAから結果の型に変わる）。評価後は通常の`get〇〇CellValue()`で結果を取得できます。                                 |
| 💡 **たまに使う** | `formulaEvaluator.evaluate(Cell cell)`                                         | 指定セルの数式を評価し、**結果を`CellValue`として返します**（元のセルは変更しない）。`cellValue.getNumberValue()`/`getStringValue()`/`getBooleanValue()`で結果を取り出します。               |
| 💡 **たまに使う** | `cell.getErrorCellValue()`                                                     | セルの値を**エラーコード（byte、例: `7`）として**取得します。`cell.getCellType()`が`ERROR`の場合に使用。コードのままでは分かりにくいため、`FormulaError.forInt(cell.getErrorCellValue()).getText()`で`#DIV/0!`や`#VALUE!`などの**表示用エラー文字列**に変換できます。 |
| 💡 **たまに使う** | `sheet.iterator()` / 拡張for文                                                 | シートの**全行を順番に走査**します。                                                                                                                                                         |
| ☠️ **使わない**   | `new XSSFWorkbook(...)` / `new HSSFWorkbook(...)` の直接指定                   | 形式を決め打ちすると、想定と異なる形式（`.xls`しか来ないはずが`.xlsx`が来た、等）のファイルで例外になる。判別は`WorkbookFactory`に任せるのが現代的な書き方。                                 |

> 📌 **モダンな書き方:**
> - ユーザーがアップロードするファイルは`.xlsx`か`.xls`か事前にわからないことが多いため、読み込み側では特に`WorkbookFactory.create(...)`の恩恵が大きいです。ファイルシグネチャから形式を自動判別してくれるので、`if`文で拡張子を判定して`XSSFWorkbook`/`HSSFWorkbook`を出し分ける処理が不要になります。
> - ローカルファイルの読み込みにも`new FileInputStream(String)`ではなく、`java.nio.file.Files.newInputStream(Path)`を使うのが現代的です。`Path`ベースのAPIと統一的に扱え、発生する例外も具体的なサブクラスになるため原因を特定しやすくなります。

> 🧮 **数式セルの計算結果を取得する2つの方法:**
> 1. **キャッシュ値をそのまま読む**: `cell.getCellType()`が`FORMULA`のとき、`cell.getCachedFormulaResultType()`で結果の型を判定し、対応する`get〇〇CellValue()`を呼ぶ。Excelが最後に保存した時点の計算結果を読むだけなので手軽だが、値が古い可能性がある。
> 2. **`FormulaEvaluator`で再計算する**: `workbook.getCreationHelper().createFormulaEvaluator()`で評価器を取得し、`evaluate(cell)`または`evaluateInCell(cell)`で数式を実際に計算し直す。Javaプログラムでセルの値を書き換えた直後など、確実に最新の結果が欲しい場合はこちらを使う。
> ⚠️ 利用には`poi`本体に加えて`poi-ooxml`（`.xlsx`用）も依存関係に含めておく必要があります（`.xls`のみ対応で良ければ`poi`だけでも可）。

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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

        // 引数 true = .xlsx形式の空ブックを作成（false なら .xls）
        try (Workbook workbook = WorkbookFactory.create(true)) {
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

            // Files.newOutputStream(Path)でモダンにストリームを取得
            try (OutputStream os = Files.newOutputStream(Path.of("employee_list.xlsx"))) {
                workbook.write(os); // ファイルへ書き出し
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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImportEmployeeListExample {
    public static void main(String[] args) throws IOException {
        // Files.newInputStream(Path)でモダンにストリームを取得し、
        // .xlsx / .xls を自動判別してブックを読み込む
        try (InputStream is = Files.newInputStream(Path.of("employee_list.xlsx"));
             Workbook workbook = WorkbookFactory.create(is)) {

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

---

## Appendix: CellTypeの種類

`cell.getCellType()`は`org.apache.poi.ss.usermodel.CellType`列挙型を返します。分岐処理（`switch`文など）で使う際の一覧は以下の通りです。

| 定数 | 説明 |
| :--- | :--- |
| `STRING` | 文字列セル。`cell.getStringCellValue()`で値を取得する。 |
| `NUMERIC` | 数値セル。`cell.getNumericCellValue()`で値を取得する。**日付**もこの型に含まれるため、`DateUtil.isCellDateFormatted(cell)`で日付かどうかを判定し、日付なら`cell.getDateCellValue()`を使う。 |
| `BOOLEAN` | 真偽値セル。`cell.getBooleanCellValue()`で値を取得する。 |
| `FORMULA` | 数式が入力されたセル。`cell.getCellFormula()`で数式文字列を取得できる。計算結果はキャッシュ値（`cell.getCachedFormulaResultType()`＋`get〇〇CellValue()`）か、`FormulaEvaluator`での再計算のいずれかで取得する。詳細は上の「数式セルの計算結果を取得する2つの方法」を参照。 |
| `BLANK` | 空セル（値未入力）。 |
| `ERROR` | Excel上でエラー値（`#DIV/0!`など）が入っているセル。`cell.getErrorCellValue()`でエラーコード（byte）を取得できる。`FormulaError.forInt(...)`.`getText()`で表示用文字列に変換可能。 |
| `_NONE` | 非推奨。通常のコードでは使用しない。 |

---

## 以下、公式ドキュメントのリンク

[Apache POI - the Java API for Microsoft Documents](https://poi.apache.org/)
