import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 测试数据库类用的程序入口
 *
 *
 * 注 这里的代码仅供参考
 *      并不能真实运行,　原因是你没有对应的数据库和表格啊 QAQ
 *
 * Created by CATT-L on 12/17/2017.
 */
public class TestMain {



    public static void main(String[] args) throws CattSqlException{

        // 创建一个Map用于存储数据库配置
        Map<String, String> dbSet = new HashMap<>();
        dbSet.put("name", "test");

        // 创建数据库操作对象
        CattSQL db = null;
        try {
            db = new CattSQL(dbSet);
        } catch (CattSqlException e) {
            e.printStackTrace();
        }

        if (db == null) return;

        // 获取数据库列表
        List<String> dbList = db.getTables();
        for (String table : dbList){
            System.out.println(table);
        }

        // 删除指定数据库
        String tableDrop = "QAQ";
        db.dropTable(tableDrop);

        // 获取sql语句
        String sql = db.lastSQL();
        System.out.println("上一条SQL语句: " + sql);

        // 执行sql语句 ==================================================
        sql = "CREATE TABLE `newTable` (" +
                "`id`  int NOT NULL AUTO_INCREMENT ," +
                "`k1`  int ZEROFILL NULL ," +
                "`k2`  varchar(255) NOT NULL ," +
                "`k3`  text NULL ," +
                "`k4`  varchar(40) NULL DEFAULT 'default varchar' ," +
                "PRIMARY KEY (`id`)" +
                ")" +
                ";";
        Connection conn = db.getConn();

        try {
            // 执行SQL语句
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            // 尝试关闭Connection对象
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        // 查询 ==================================================
        String table = "owo";

        // 注 偏移量 offset
        // 偏移 0 表示 从1开始
        // 偏移 5 表示 从6开始
        // 以此类推

        List<Map<String, String>> reList;
        // 取出表内所有
        reList = db.select(table);
        // 取出表内10条, 从第11条开始
        reList = db.select(table, 10, 10);
        // 取出表内count==2的所有
        reList = db.select(table, "count", "2");
        // 取出表内count==2的第3条到第5条 (2条, 从第3条开始)
        reList = db.select(table, 2, 2, "count", "2");

        Map<String, String> reMap;
        // 去除符合条件的单条数据
        reMap = db.selectOne(table, "id", "666");

        // 插入 ==================================================

        // 单条插入
        Map<String, String> dataInsertOne = new HashMap<>();
        dataInsertOne.put("k1", "v1");
        dataInsertOne.put("k2", "v2");
        dataInsertOne.put("k3", "v3");

        String insertID = db.insert(table, dataInsertOne);
        System.out.println("返回ID " + insertID);

        // 多条插入
        String[][] dataInsertList = {
                {"k1", "k2", "k3"},
                {"v11", "v12", "v13"},
                {"v21", "v22", "v23"},
                {"v31", "v32", "v33"},
                {"v41", "v42", "v43"}
        };

        List<String> insertIDList = db.insert(table, dataInsertList);
        System.out.println("返回ID " + insertIDList);


        // 更新 ==================================================
        int num;
        Map<String, String> dataUpdate = new HashMap<>();
        dataUpdate.put("k1", "v1");
        dataUpdate.put("k2", "v2");

        // 单条件选择更新
        num = db.update(table, "key", "value", dataUpdate);
        System.out.println(num + " 条记录被更新");

        // 多条件选择更新
        Map<String, String> whereUpdate = new HashMap<>();
        whereUpdate.put("where1", "v1");
        whereUpdate.put("where2", "v2");

        num = db.update(table, whereUpdate, dataUpdate);
        System.out.println(num + " 条记录被更新");

        // 删除 ==================================================
        num = db.delete(table, "k", "v");
        System.out.println("删除了 " + num + " 条");

        String[] delIDArr = {"1","2","3","4"};
        num = db.delete(table, "id", delIDArr);

        System.out.println("删除了 " + num + " 条"); // 不出意外是4条

        // 释放 ==================================================
        db.release();
    }
}
