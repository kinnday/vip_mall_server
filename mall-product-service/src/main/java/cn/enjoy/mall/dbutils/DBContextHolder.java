package cn.enjoy.mall.dbutils;

import java.util.Stack;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 往期课程咨询芊芊老师  QQ：2130753077 VIP课程咨询 依娜老师  QQ：2133576719
 * 类说明：数据源的选择
 */
public class DBContextHolder {
    /*保存系统中存在的数据源的标识符，然后通过该标识符定位到实际的数据源实体*/
//  fxc-本地线程 保留数据库实例（主库还是从库）
//  select*** --> salve; addXXX -->master; 只是这种没问题
//  特殊场景：
//  serviceAimpl: selectA updateB
//  serviceBimpl: selectB updateB
//  updateB -- master
//  selectA {updateB} -- slave  出现 service里面调用service 就会出问题
//    所以这里用了 Stack-栈！！！  先进后出
    private static final ThreadLocal<Stack<DBTypeEnum>> contextHolderStack
            = new ThreadLocal<Stack<DBTypeEnum>>(){
        @Override
        protected Stack<DBTypeEnum> initialValue() {
            return new Stack<DBTypeEnum>();
        }
    };


//    private static final ThreadLocal<DBTypeEnum> contextHolder
//            = new ThreadLocal<>();


    public static void set(DBTypeEnum dbType) {
//      Stack-栈！！！  先进后出
        contextHolderStack.get().push(dbType);
//        contextHolder.set(dbType);
    }

    public static void remove() {
        if(!contextHolderStack.get().empty()){
            contextHolderStack.get().pop();
        }
//        contextHolder.set(dbType);
    }

    public static DBTypeEnum get() {
        if(contextHolderStack.get().empty()){
            return DBTypeEnum.MASTER;
        }
        DBTypeEnum current = contextHolderStack.get().peek();
        System.out.println("当前数据库："+current);
        return current;
    }

    public static void master() {
        set(DBTypeEnum.MASTER);
        System.out.println("切换到master");
    }

    /*通过轮询选择从库*/
    public static void slave() {
        set(DBTypeEnum.SLAVE);//轮询
        System.out.println("切换到从库-----------------------");
    }
}
