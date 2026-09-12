# ⏰クラス設計（ProcessStopwatch）

役割ごとに以下の4つのメソッドを用意します。

- start() : 計測開始
- stop() : 計測終了
- printLog() : ログ（標準出力やSLF4Jなど）にかかった時間を出力
- getData() / sendToScreen() : 画面側へ渡すためのデータを取得（または通知）

## 🌟具体的なProcessStopwatchクラス

```java
package com.example.util;

import java.time.Duration;
import java.time.Instant;

/**
 * 処理時間の計測とログ・画面へのデータ受け渡しを行うクラス
 */
public class ProcessStopwatch {

    private final String taskName;
    private Instant startTime;
    private Instant endTime;
    private boolean isStopped = false;

    // コンストラクタ
    public ProcessStopwatch(String taskName) {
        this.taskName = taskName;
    }

    // ==========================================
    // 1. スタート用メソッド
    // ==========================================
    public void start() {
        this.startTime = Instant.now();
        this.isStopped = false;
    }

    // ==========================================
    // 2. 終わり用メソッド
    // ==========================================
    public void stop() {
        this.endTime = Instant.now();
        this.isStopped = true;
    }

    /**
     * 経過時間（ミリ秒）を算出する内部ヘルパー
     */
    public long getElapsedMillis() {
        if (startTime == null) {
            return 0;
        }
        // stop()が呼ばれていなければ「今この瞬間」までの時間を計算
        Instant end = isStopped ? this.endTime : Instant.now();
        return Duration.between(startTime, end).toMillis();
    }

    // ==========================================
    // 3. ログに処理にかかった時間を出力するメソッド
    // ==========================================
    public void printLog() {
        long millis = getElapsedMillis();
        // コンソール出力（※必要に応じて Logger.info(...) 等に変更可能）
        System.out.println(String.format("[LOG] タスク「%s」の処理時間: %d ms", taskName, millis));
    }

    // ==========================================
    // 4. 画面にデータを受け渡すためのメソッド
    // ==========================================
    /**
     * 画面側で必要なデータ（タスク名と経過ミリ秒）をまとめて取得します
     */
    public ResultData getData() {
        return new ResultData(this.taskName, getElapsedMillis());
    }

    /**
     * 画面側のログ表示エリアなどに直接メッセージテキストを渡します
     */
    public String getFormattedScreenMessage() {
        return String.format("【処理完了】%s (所要時間: %d ms)", taskName, getElapsedMillis());
    }

    // ==========================================
    // 画面受け渡し用の内部データクラス (Java 8対応)
    // ==========================================
    public static class ResultData {
        private final String taskName;
        private final long elapsedMillis;

        public ResultData(String taskName, long elapsedMillis) {
            this.taskName = taskName;
            this.elapsedMillis = elapsedMillis;
        }

        public String getTaskName() {
            return taskName;
        }

        public long getElapsedMillis() {
            return elapsedMillis;
        }
    }
}
```

## 🌟使い方・データ受け渡し方

データを渡された画面側のコードでは、getTaskName() や getElapsedMillis() などの普通のゲッターメソッドを使って値を取り出せます。

```java
public void doBusinessProcess(MyLogScreenView screen) {
    // インスタンス作成
    ProcessStopwatch timer = new ProcessStopwatch("CSV出力処理");

    // 1. スタート
    timer.start();

    // 実際の処理
    exportCsv();

    // 2. 終わり
    timer.stop();

    // 3. ログに出力
    timer.printLog();

    // 4. 画面にデータを渡す（オブジェクトで渡す場合）
    ProcessStopwatch.ResultData data = timer.getData();
    screen.updateTable(data.getTaskName(), data.getElapsedMillis());

    // 4. 画面にデータを渡す（整形された文字列で渡す場合）
    screen.appendLogText(timer.getFormattedScreenMessage());
}
```
