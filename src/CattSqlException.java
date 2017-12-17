/**
 * 自定义异常 没有什么特殊的功能 就 ... 区分一下默认的Exception而已
 *
 * Created by CATT-L on 12/17/2017.
 */
public class CattSqlException extends Exception{

    public CattSqlException(String e) {
        super(e);
    }
}