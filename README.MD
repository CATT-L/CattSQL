# JDBC 数据库类封装
  
把Java的JDBC封装起来, 更加方便调用  

真的炒鸡方便 QAQ  

目前能够实现以下数据库语句  
```sql
// 陈列表与表删除
show tables;
drop table `table`;

// 查询
select * from `table`;
select * from `table` limit offset,num;
select * from `table` where `key`='value';
select * from `table` where `key`='value' limit 0,1;
select * from `table` where `key`='value' limit offset,num;

// 插入
insert into `table` (`k1`,`k2`,`k3`) values('v1','v2','v3');
insert into `table` (`k1`,`k2`,`k3`) values('v11','v12','v13'),('v21','v22','v23'),('v31','v32','v33');

// 更新
update `table` set `k1`='v1',`k2`='v2' where (`k`='v');
update `table` set `k1`='v1',`k2`='v2' where (`wk1`='wv2' and `wk2`='wv2');

// 删除
delete from `table` where (`k`='v');
delete from `table` where (`k1`='v1' or `k2`='v2' or `k3`='v3');

```


### 使用说明

在`TestMain.java`中写了示例代码.  

###### 静态常量

供外部获取键名, 防止打错字  

`KEY_HOST` 主机地址  
`KEY_PORT` 主机端口  
`KEY_NAME` 数据库名  
`KEY_USER` 用户名称  
`KEY_PASS` 用户密码  

###### 构造函数

你可以通过两种方法创建对象  

```java
String host = "localhost";
String port = "3306";
String name = "test";
String user = "root";
String pass = "";

CattSQL db;

// 第一种方法 直接传递变量
db = new CattSQL(host, port, name, user, pass);

// 第二种方法 通过Map传参
Map<String, String> dbConf = new HashMap<>();

/**
 * 除了数据库名 其它参数都可以缺省
 *
 * 缺省默认值如下
 * host 127.0.0.1
 * port 3306
 * user root
 * pass "" 空字符串
 * 
 */
// dbConf.put(CattSQL.KEY_HOST, host);
// dbConf.put(CattSQL.KEY_PORT, port);
dbConf.put(CattSQL.KEY_NAME, name);
// dbConf.put(CattSQL.KEY_USER, user);
// dbConf.put(CattSQL.KEY_PASS, pass);

db = new CattSQL(dbConf);

```

###### 连接与释放
在创建对象的同时, 它会自动对数据库进行连接, 不需要手动`connect()`一遍。  
如果连接失败或是参数传递不合法, 会抛出异常。  

在不需要使用数据库的时候，你可以通过`db.release()`释放数据库，在释放之后依旧可以进行各种数据库操作，因为它操作的时候会自动连接 QAQ  

<s>那这个`release()`真是毫无意义啊！</s>


###### 获取连接对象
有时候下面这些基本的`增 删 改 查`并不能满足你的需求, 可以通过`db.getConn()`获取一个**新的**连接对象, 直接执行数据库语句, 就像用JDBC一样.  
因为是新的`Connection`对象,　因此用完后记得关闭资源.  

###### 获取表列表和删除表

通过`db.getTables()`可以获取当前数据库的表名列表, 用字符串列表返回 `List<String>`.  
通过`db.dropTable(String table)`能删除指定表名的表, 注意确认该表存在，否则会抛出异常.  

###### 查看上一条SQL语句( 成功执行 )

使用`db.lastSQL()`可以获取上一条**成功执行**的SQL语句。  
如果需要做数据库操作日志的话, 这个功能很实用。  

但是要注意, 使用`db.getConn()`获取的对象, 在外部执行的sql将不会被记录。

###### 查询记录

* 单条查询  
	```java
	/**
	 * select * table where k=v limit 1;
	 *
	 * @param table     表名
	 * @param k         键名
	 * @param v         键值
	 * @return          单条数据 键值对
	 * @throws CattSqlException     表名键名为空或查询异常
	 */
	Map<String, Object> selectOne(String table, String k, String v)
	```

* 多条查询(符合条件的所有数据)
	```java
	/**
	 * 数据库查询 符合条件的全部
	 *
	 * select * from table where k=v;
	 *
	 * @param table     表名
	 * @param k         key
	 * @param v         value
	 * @return          结果数组 元素是字符串键值对
	 */
	 List<Map<String, String>> select(String table, String k, String v)
	```

* 多条查询(符合条件限定条数和开始位置)
	```java
	/**
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
	List<Map<String, String>> select(String table, int num, int offset, String k, String v)
	```

* 无条件查询(取得所有)
	```java
	/**
	 * select * from table;
	 *
     * @param table         表名
     * @return              结果列表
	 */
	List<Map<String, String>> select(String table)
	```

* 无条件查询(限定条数和偏移)
	```java
	/**
	 * select * from table;
	 *
     * @param table         表名
     * @param num           数量
     * @param offset        偏移
     * @return              结果列表
	 */
	 List<Map<String, String>> select(String table, int num, int offset)
	```


###### 插入记录

* 插入记录(单条插入)
	```java
    /**
     * insert into `table` (`k1`,`k2`,`k3`) values('v1','v2','v3');
     *
     * @param table         表名
     * @param data          键值对
     * @return              返回主键    通常是自增ID 为了保险起见还是用String形式返回
     *
     * @throws CattSqlException     抛出异常
     */
     String insert(String table, Map<String, String> data)
	```

* 插入记录(一次多条)
	```java
    /**
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
     List<String> insert(String table, String[][] list)
	```

###### 更新记录

* 单条件选择更新
	```java
    /**
     * update `table` set `k2`='v2',`k3`='v3' where (`k1`='v1');
     *
     * @param table         表名
     * @param k             键名
     * @param v             键值 缺省为空字符串
     * @param data          更新键值对 缺省则不更新
     * @return              返回受影响条数
     *
     * @throws CattSqlException     表名键名为空异常 数据库插入异常
     */
     int update(String table, String k, String v, Map<String, String> data)
	```

* 多条件选择更新
	```java
    /**
     * update `table` set `k1`='v1',`k2`='v2' where (`wk1`='wv2' and `wk2`='wv2');
     *
     * @param table         表名
     * @param where         条件 键值对
     * @param data          更新 键值对
     * @return              受影响条数
     * @throws CattSqlException     表名 条件为空异常, 数据库插入失败
     */
     int update(String table, Map<String, String> where, Map<String, String> data)
	```

###### 删除记录

* 单条删除
	```java
    /**
     * delete from `tt` where (`k`='v');
     *
     * @param table         表名
     * @param k             键名
     * @param v             键值
     * @return              受影响条数
     *
     * @throws CattSqlException     异常
     */
     int delete(String table, String k, String v)
	```

* 多条删除
	```java
    /**
     * delete from `table` where (`k1`='v1' or `k2`='v2' or `k3`='v3');
     *
     * @param table     表名
     * @param k         键名
     * @param v         键值数组
     * @return          受影响条数
     *
     * @throws CattSqlException     表名为空, 键名为空, 执行异常
     */
     int delete(String table, String k, String[] v)
	```


### 更新日志


---
2017-12-19 14:13:05
增加了`列出表`和`删除表`  

```sql
show tables;
drop table `table`;
```

---
2017-12-18 12:22:43  
把 `更新记录` 和 `删除记录` 补充完了  
也就是更新了下面的语句  

> * 更新
>	```sql
>     update `table` set `k1`='v1',`k2`='v2' where (`k`='v');
>     update `table` set `k1`='v1',`k2`='v2' where (`wk1`='wv2' and `wk2`='wv2');
>	```
> * 删除
>	```sql
>     delete from `table` where (`k`='v');
>     delete from `table` where (`k1`='v1' or `k2`='v2' or `k3`='v3')
>	```

今天天气好冷啊！ 10℃ !!! 冷死了冷死了 QAQ  

---
2017-12-17 17:26:12  
搞定了最基本的 `连接数据库`, 以及 `查找记录` 和 `插入记录`  
目前能实现如下语句  

> * 查询
>	```sql
>     select * from `table`;
>     select * from `table` limit offset,num;
>     select * from `table` where `key`='value';
>     select * from `table` where `key`='value' limit 0,1;
>     select * from `table` where `key`='value' limit offset,num;
>	```
> * 插入
>	```sql
>     insert into `table` (`k1`,`k2`,`k3`) values('v1','v2','v3');
>     insert into `table` (`k1`,`k2`,`k3`) values('v11','v12','v13'),('v21','v22','v23'),('v31','v32','v33');
>	```

呃... 目前就这么多 吃饭吃饭！ (๑و•̀ω•́)و  
