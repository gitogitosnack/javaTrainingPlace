import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamApiPractice{
    public static void main (String[] args){
        List<String> words = Arrays.asList(
            "apple"
            ,"banana"
            ,"cat"
            ,"dog"
            ,"egg"
        );

        // 処理の流れ：
        // 1. 文字列のリストからストリームを生成
        // 2. filter: 長さが3文字より長い単語のみを抽出 (Intermediate Operation)
        // 3. map: それらを全て大文字に変換 (Intermediate Operation)
        // 4. collect: 結果を新しいListに格納 (Terminal Operation)
        List<String> result = words.stream()
            .filter(w -> w.length() > 5) // ラムダ式（例: s -> s.toUpperCase()）
            .map(String::toUpperCase) // 名称: メソッド参照 (Method Reference) ラムダ式をさらに簡素化したもの。文法: ClassName::methodName。
            .collect(Collectors.toList());
        
        System.out.println(result);

    }
}