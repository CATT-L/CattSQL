# JDBC 数据库类封装
  
把Java的JDBC封装起来, 更加方便调用  

真的炒鸡方便 QAQ  


### 使用方法

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

在创建对象的同时, 它会自动对数据库进行连接, 不需要手动`connect()`一遍。  
如果连接失败或是参数传递不合法, 会抛出异常。

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

* 多条查询
	```java
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
	 List<Map<String, Object>> select(String table, String k, String v)
	```

* 多条查询2
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
	List<Map<String, Object>> select(String table, int num, int offset, String k, String v)
	```




### 更新记录

---
2017-12-18 12:22:43  
把 `更新记录` 和 `删除记录` 补充完了  
也就是更新了下面的语句  

> * 更新
>	```java
>     update `table` set `k1`='v1',`k2`='v2' where (`k`='v');
>     update `table` set `k1`='v1',`k2`='v2' where (`wk1`='wv2' and `wk2`='wv2');
>	```
> * 删除
>	```java
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
