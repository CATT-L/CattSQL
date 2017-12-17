### JDBC 数据库类封装

2017-12-17 17:26:12  
搞定了最基本的 `连接数据库`, 以及 `查找记录` 和 `插入记录`  
目前能实现如下语句  

> * 查询
>	```
>     select * from `table`;
>     select * from `table` limit offset,num;
>     select * from `table` where `key`='value';
>     select * from `table` where `key`='value' limit 0,1;
>     select * from `table` where `key`='value' limit offset,num;
>	```
> * 插入
>	```
>     insert into `table` (`k1`,`k2`,`k3`) values('v1','v2','v3');
>     insert into `table` (`k1`,`k2`,`k3`) values('v11','v12','v13'),('v21','v22','v23'),('v31','v32','v33');
>	```

呃... 目前就这么多 吃饭吃饭！ (๑و•̀ω•́)و  