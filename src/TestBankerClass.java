import java.util.Scanner;

public class TestBankerClass {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        boolean Choose = true;
        String C;
        System.out.println("请输入进程个数：");
        int proc = in.nextInt();
        System.out.println("请输入资源类数：");
        int resource = in.nextInt();
        BankerClass T = new BankerClass(proc,resource);
        T.start(proc,resource);
        while (Choose == true) {
            T.setRequest(proc,resource);
            System.out.println("您是否还要进行资源请求：y/n?");
            C = in.next();
            if (C.equals("n")) {
                Choose = false;
            }
            if(C.equals("y")){
                Choose = true;
            }
        }
    }

}
