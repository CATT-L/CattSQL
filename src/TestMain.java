import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 测试数据库类用的程序入口
 *
 * Created by CATT-L on 12/17/2017.
 */
public class TestMain {

    public static void main(String[] args) throws Exception{

        Map<String, String> dbSet = new HashMap<>();

        dbSet.put("name", "test");

        CattSQL db = new CattSQL(dbSet);

        String table = "tt";
//        db.select(table);
//        db.select(table, 10, 0);
//        db.select(table, 10, 10);
//        db.select(table, "count", "2");
//        db.select(table, 10, 10, "count", "3");
//        db.selectOne(table, "pid", "25");


//        String[][] list = {
//                {"url", "count"},
//                {"urlTest", "1"},
//                {"urlTest", "1"},
//                {"urlTest", "1"},
//                {"urlTest", "1"},
//                {"urlsad", "2"},
//                {"asdasf", "5"}
//        };
//
//        List<Object> re = db.insert("song_img", list);
//        System.out.println(re);

        String[] delete = {
          "1","2","3"
        };

        int re = db.delete(table, null, "");
        System.out.println(re);


    }
}
