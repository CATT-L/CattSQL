import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ### 数据库类封装
 *
 * 通过HashMap传参和返回，可以更加方便使用
 *
 * 真的炒鸡方便 QAQ
 *
 *
 *
 *
 * @author CATT-L
 * Created by CATT-L on 12/17/2017.
 *
 * ---
 * 2017-12-18 12:17:45
 * 增加了`更新记录`和`删除记录`
 *
 *      更新
 *          update `table` set `k1`='v1',`k2`='v2' where (`k`='v');
 *          update `table` set `k1`='v1',`k2`='v2' where (`wk1`='wv2' and `wk2`='wv2');
 *
 *      删除
 *          delete from `table` where (`k`='v');
 *          delete from `table` where (`k1`='v1' or `k2`='v2' or `k3`='v3');
 *
 * 今天天气好冷啊！ 10℃ !!! 冷死了冷死了 QAQ
 *
 * ---
 * 2017-12-17 17:26:12
 * 搞定了最基本的 `连接数据库`, 以及 `查找记录` 和 `插入记录`
 * 目前能实现如下语句
 *
 *      查询
 *          select * from `table`;
 *          select * from `table` limit offset,num;
 *          select * from `table` where `key`='value';
 *          select * from `table` where `key`='value' limit 0,1;
 *          select * from `table` where `key`='value' limit offset,num;
 *
 *      插入
 *          insert into `table` (`k1`,`k2`,`k3`) values('v1','v2','v3');
 *          insert into `table` (`k1`,`k2`,`k3`) values('v11','v12','v13'),('v21','v22','v23'),('v31','v32','v33');
 *
 * 呃... 目前就这么多 吃饭吃饭！ (๑و•̀ω•́)و
 *
 *
 */

public class CattSQL {

    /**
     * 静态常量
     *
     * 供外部获取的键名 为了防止打错字
     * 可以在其它类里通过常量的方式引用.
     *
     */
    public static String KEY_HOST = "host";
    public static String KEY_PORT = "port";
    public static String KEY_NAME = "name";
    public static String KEY_USER = "user";
    public static String KEY_PASS = "pass";

    /**
     * 私有成员 记录数据库基本信息
     *
     *      dbHost      主机地址
     *      dbPort      端口
     *      dbName      数据库名
     *      dbUser      用户名
     *      dbPass      密码
     *
     *      sql         上一条成功执行的SQL语句
     *      conn        数据库连接对象
     *
     */
    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUser;
    private String dbPass;
    private String sql = "";
    private Connection conn = null;

    /**
     * 构造函数
     * @param data      Map类型 键名键值都是字符串形式
     *
     *                  必须包含如下数据
     *                  KEY_NAME,数据库名
     *
     *                  可缺省                     缺省值
     *                  KEY_HOST,主机地址          127.0.0.1
     *                  KEY_PORT,端口号            3306
     *                  KEY_USER,用户名            root
     *                  KEY_PASS,连接密码          无密码
     */
    public CattSQL(Map<String, String> data) throws CattSqlException{
        this(
                data.get(KEY_HOST),
                data.get(KEY_PORT),
                data.get(KEY_NAME),
                data.get(KEY_USER),
                data.get(KEY_PASS)
                );
    }

    /**
     * 构造函数
     * @param host      主机地址 可以是IP也可以是域名
     * @param port      端口号 字符串格式
     * @param name      数据库名称
     * @param user      用户名
     * @param pass      密码
     */
    public CattSQL(String host, String port, String name, String user, String pass) throws CattSqlException{

        this.dbHost = host;
        this.dbPort = port;
        this.dbName = name;
        this.dbUser = user;
        this.dbPass = pass;

        // 传参错误 抛出异常
        paramCheck();

        // 开始连接
        loadDatabaseDriver();
        connect();
    }

    /**
     * 检查参数正确性
     *
     * @throws CattSqlException     传参错误异常
     */
    private void paramCheck() throws CattSqlException{

        // 地址缺省 127.0.0.1
        if(this.dbHost == null || this.dbHost.length() == 0) this.dbHost = "127.0.0.1";

        // 端口缺省 3306
        if(this.dbPort == null || this.dbPort.length() == 0) this.dbPort = "3306";

        // 数据库名不能为空啊！
        if(this.dbName == null || this.dbName.length() == 0) throw new CattSqlException("数据库名不能为空");

        // 用户名缺省 root
        if(this.dbUser == null || this.dbUser.length() == 0) this.dbUser = "root";

        // 密码缺省 空
        if(this.dbPass == null) this.dbPass = "";
    }

    /**
     * 加载数据库驱动类
     *
     * @throws CattSqlException     加载失败
     */
    private void loadDatabaseDriver() throws CattSqlException{
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new CattSqlException("加载数据库驱动失败！");
        }
    }

    /**
     * 连接数据库
     *
     * @throws CattSqlException     连接失败
     */
    private void connect() throws CattSqlException{

        String url = "jdbc:mysql://" + this.dbHost
                        + ":" + this.dbPort
                        + "/" + this.dbName;
        try {
            Connection conn = DriverManager.getConnection(url, this.dbUser, this.dbPass);
            this.conn = conn;

        } catch (SQLException e) {
            throw new CattSqlException("连接数据库失败");
        }
    }

    /**
     * 获取上一步sql语句
     * @return sql语句
     */
    public String lastSQL(){
        return this.sql;
    }

    /**
     * 多条查询
     *
     * 内部方法 能够实现
     * select * from table;
     * select * from table where k=v;
     * select * from table limit offset,num;
     * select * from table where k=v limit offset,num;
     *
     * 不可缺省
     * @param table     表名
     * @param num       取出数量    0 为所有
     * @param offset    偏移量     开始位置
     *
     * 可缺省
     * @param k         键名
     * @param v         键值
     * @return          键值对列表
     * @throws CattSqlException     表名为空或查询异常
     */
    public List<Map<String, Object>> select(String table, int num, int offset, String k, String v) throws CattSqlException{

        if(table == null || table.length() == 0) throw new CattSqlException("表名不能为空");

        // sql 构造
        String sql = "select * from `" + table + "`";

        if(k != null && k.length() > 0){
            sql += " where `" + k + "`='" + v + "'";
        }

        if(num > 0){
            // 偏移不能为负数
            if(offset < 0) offset = 0;
            String limit = "limit " + offset + "," + num;
            sql += " " + limit;
        }
        sql += ";";

        // System.out.println(sql);

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // 空值 返回空
            if(!rs.next()) return null;

            // 获取字段名
            int colNum = rs.getMetaData().getColumnCount();
            String[] colName = new String[colNum];
            for(int i = 0; i < colNum; i++){
                colName[i] = rs.getMetaData().getColumnName(i+1);
            }

            // 存放结果
            List<Map<String, Object>> list = new ArrayList<>();

            do{
                // 封装数据
                Map<String, Object> map = new HashMap<>();
                for(int i = 0; i < colNum; i++){
                    String _k = colName[i];
                    Object _v = rs.getObject(i+1);
                    map.put(_k, _v);
                }

                // 推入list
                list.add(map);

            } while (rs.next());

            rs.close();
            ps.close();

            // 记录sql
            this.sql = sql;

            return list;
        } catch (Exception e) {
            // 查询数据库失败
            throw new CattSqlException("数据库查询失败 语句: " + sql);
        }
    }

    /**
     * 数据库查询 单条
     *
     * select * table where k=v limit 1;
     *
     * @param table     表名
     * @param k         键名
     * @param v         键值
     * @return          单条数据 键值对
     * @throws CattSqlException     表名键名为空或查询异常
     */
    public Map<String, Object> selectOne(String table, String k, String v) throws CattSqlException{

        // 查询且仅查询一条数据
        List<Map<String, Object>> list = select(table, 1, 0, k, v);

        if(list != null) return list.get(0);
        else return null;
    }

    /**
     * 数据库查询 符合条件的全部
     *
     * select * from table where k=v;
     *
     * @param table     表名
     * @param k         key
     * @param v         value
     * @return          结果数组
     */
    public List<Map<String, Object>> select(String table, String k, String v) throws CattSqlException{

        return select(table, 0, 0, k, v);
    }

    /**
     * 数据库查询 无条件 限定条数
     *
     * select * from table limit offset,num;
     *
     * @param table         表名
     * @param num           数量
     * @param offset        偏移
     * @return              结果数组
     */
    public List<Map<String, Object>> select(String table, int num, int offset) throws CattSqlException{
        return select(table, num, offset, null, null);
    }

    /**
     * 取得所有数据
     *
     * select * from table;
     *
     * @param table     表名
     * @return          结果
     */
    public List<Map<String, Object>> select(String table) throws CattSqlException{
        return select(table, 0, 0, null, null);
    }

    /**
     * 插入记录 单条
     *
     * insert into `table` (`k1`,`k2`,`k3`) values('v1','v2','v3');
     *
     * @param table         表名
     * @param data          键值对
     * @return              返回主键    通常是自增ID 为了保险起见还是用String形式返回
     *
     * @throws CattSqlException     抛出异常
     */
    public String insert(String table, Map<String, String> data) throws CattSqlException{

        // 表名判空
        if(table == null || table.length() == 0) throw new CattSqlException("表名不能为空");

        int len = data.size();
        if(len == 0) return null;

        String kStr = "";
        String vStr = "";

        for (Map.Entry<String, String> entry : data.entrySet()){
            kStr += ",`"+entry.getKey()+"`";
            vStr += ",'"+entry.getValue()+"'";
        }
        String sql = "insert into `"+table+"` (";
        sql += kStr.substring(1);
        sql += ") values(";
        sql += vStr.substring(1);
        sql += ");";

        try{
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            String key = null;
            // 返回主键
            if(ps.executeUpdate() > 0){
                ResultSet rs = ps.getGeneratedKeys();

                if(rs.next()){
                    key = rs.getString(1);
                }
                rs.close();
            }
            ps.close();

            // 记录sql
            this.sql = sql;

            return key;

        } catch (Exception e){
            e.printStackTrace();
            throw new CattSqlException("数据插入失败 QAQ");
        }
    }

    /**
     * 插入记录 多条插入
     *
     * insert into `table` (`k1`,`k2`,`k3`) values('v11','v12','v13'),('v21','v22','v23'),('v31','v32','v33');
     *
     * @param table         表名
     * @param list          字符串数组
     *                          约定第 0 行为键名 (`k1`,`k2`,`k3`)
     *                          后面行均为键值 ('v11','v12','v13'),('v21','v22','v23'),('v31','v32','v33')
     *
     * @return              返回主键ID列表, 字符串类型
     *                          大概长这样 [27,28,29]
     *                          或这样 [子鼠,丑牛,寅虎]
     *                          取决于你的主键是什么
     *
     * @throws CattSqlException     抛出异常
     */
    public List<Object> insert(String table, String[][] list) throws CattSqlException{
        if(table == null || table.length() == 0) throw new CattSqlException("表名不能为空");

        // 数组空
        if(list == null) return null;

        int listSize = list.length;
        if(listSize <= 1) return null;

        int dataSize = list[0].length;

        String kStr = "";
        // 拼接键名字符串
        for(int i = 0; i < dataSize; i++){
            kStr += ",`" + list[0][i] + "`";
        }
        kStr = "(" + kStr.substring(1) + ")";

        String vStr = "";
        // 拼接键值字符串
        for(int i = 1; i < listSize; i++){
            String vTemp = "";
            for(int j = 0; j < dataSize; j++){
                vTemp += ",'" + list[i][j] + "'";
            }
            vStr += ",(" + vTemp.substring(1) + ")";
        }
        vStr = "value" + vStr.substring(1);

        String sql = "insert into `" + table + "`";
        sql += " " + kStr;
        sql += " " + vStr;
        sql += ";";

        try{
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            List<Object> key = null;

            // 记录主键
            if(ps.executeUpdate() > 0){
                key = new ArrayList<>();

                ResultSet rs = ps.getGeneratedKeys();
                while(rs.next()){
                    key.add(rs.getObject(1));
                }
                rs.close();
            }
            ps.close();

            this.sql = sql;

            return key;
        } catch (Exception e){
            e.printStackTrace();
            throw new CattSqlException("数据插入失败 QAQ");
        }
    }

    /**
     * 更新记录
     *
     * update `table` set `k2`='v2',`k3`='v3' where (`k1`='v1');
     * @param table         表名
     * @param k             键名
     * @param v             键值 缺省为空字符串
     * @param data          更新键值对 缺省则不更新
     * @return              返回受影响条数
     *
     * @throws CattSqlException     表名键名为空异常 数据库插入异常
     */
    public int update(String table, String k, String v, Map<String, String> data) throws CattSqlException{

        // 参数判空 表名 键名
        if(table == null || k == null || table.length() == 0 || k.length() == 0) throw new CattSqlException("表名和键名不能为空");

        // 没有数据被传入 不需要更新 返回 0 条受到影响
        if(data == null || data.size() == 0) return 0;

        // 键值缺省 空字符串
        if(v == null || v.length() == 0) v = "";

        Map<String, String> where = new HashMap<>();
        where.put(k, v);

        return update(table, where, data);
    }

    /**
     * 多条件更新
     *
     * update `table` set `k1`='v1',`k2`='v2' where (`wk1`='wv2' and `wk2`='wv2');
     *
     * @param table         表名
     * @param where         条件 键值对
     * @param data          更新 键值对
     * @return              受影响条数
     * @throws CattSqlException     表名 条件为空异常, 数据库插入失败
     */
    public int update(String table, Map<String, String> where, Map<String, String> data) throws CattSqlException{

        // 参数判空 表名 键名
        if(table == null || where == null || table.length() == 0 || where.size() == 0) throw new CattSqlException("表名和条件不能为空");

        // 没有数据被传入 不需要更新 返回 0 条受到影响
        if(data == null || data.size() == 0) return 0;


        // 构造更新段
        String nStr = "";
        for (Map.Entry<String, String> entry : data.entrySet()){

            String k = entry.getKey();
            String v = entry.getValue();

            // 键值为空 跳过添加
            if(k == null || k.length() == 0) continue;
            // 键名缺省 空字符串
            if(v == null) v = "";

            nStr += ",`" + k + "`='" + v + "'";
        }

        // 没有内容需要更新
        if(nStr.length() == 0) return 0;

        nStr = nStr.substring(1);

        // 构造条件段
        String wStr = "";
        for(Map.Entry<String, String> entry : where.entrySet()){
            String k = entry.getKey();
            String v = entry.getValue();

            // 键名为空 跳过此次添加
            if(k == null || k.length() == 0) continue;
            // 键值缺省空字符串
            if(v == null) v = "";

            wStr += " and `" + k + "`='" + v + "'";
        }

        if (wStr.length() == 0) throw new CattSqlException("条件不能为空");

        wStr = "where (" + wStr.substring(5) + ")";

        String sql = "update `"+table+"` set";
        sql += " " + nStr;
        sql += " " + wStr;
        sql += ";";

        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            int re = ps.executeUpdate();
            ps.close();

            // 记录sql
            this.sql = sql;
            return re;

        } catch(Exception e){
            e.printStackTrace();
            throw new CattSqlException("数据库更新失败了 QAQ");
        }
    }

    /**
     * 多条删除
     *
     * delete from `table` where (`k1`='v1' or `k2`='v2' or `k3`='v3');
     *
     * @param table     表名
     * @param k         键名
     * @param v         键值数组
     * @return          受影响条数
     *
     * @throws CattSqlException     表名为空, 键名为空, 执行异常
     */
    public int delete(String table, String k, String[] v) throws CattSqlException{

        if(table == null || table.length() == 0 || k == null || k.length() == 0) throw new CattSqlException("表名和键名不能为空");

        // 没有数据需要被删除
        if(v.length == 0) return 0;

        String wStr = "";
        for(int i = 0; i < v.length; i++){
            // 缺省 空字符
            if(v[i] == null) v[i] = "";
            wStr += " or `" + k + "`='" + v[i] + "'";
        }
        wStr = "where (" + wStr.substring(4) + ")";

        String sql = "delete from `" + table + "` " + wStr + ";";

        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            int re = ps.executeUpdate();
            ps.close();

            // 记录sql
            this.sql = sql;
            return re;

        } catch(Exception e){
            e.printStackTrace();
            throw new CattSqlException("数据库更新失败了 QAQ");
        }
    }

    /**
     * 单条删除
     *
     * delete from `tt` where (`k`='v');
     *
     * @param table         表名
     * @param k             键名
     * @param v             键值
     * @return              受影响条数
     *
     * @throws CattSqlException     异常
     */
    public int delete(String table, String k, String v) throws CattSqlException{

        String[] vrr = {v};
        return delete(table, k, vrr);
    }

}
