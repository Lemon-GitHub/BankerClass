import java.util.Arrays;
import java.util.Scanner;

public class BankerClass {
    private Scanner in = new Scanner(System.in);
    private int[] Available;//可用资源
    //private int[][] Max;//进程最大需求量
    private int[][] Allocation;//进程已占有资源数
    private int[][] Need;//进程还需资源数
    private int[][] Request;//进程请求数
    private int[] Work;//试分配
    private int[] temp;//进程执行顺序

    private int num = 0;//进程编号


    public BankerClass(int proc,int sour) {
        Available = new int[sour];//可用资源
        //Max = new int[proc][sour];//进程最大需求量
        Allocation = new int[proc][sour];//进程已占有资源数
        Need = new int[proc][sour];//进程还需资源数
        Request = new int[proc][sour];//进程请求数
        Work = new int[sour];//试分配
        temp = new int[proc];//进程执行顺序
    }

    // 启动
    public void start(int proc,int sour){//设置各初始系统变量，并判断是否处于安全状态。
        setAvailable(sour);
        setNeed(proc, sour);
        setAllocation(proc,sour);
        printSystemVariable(proc,sour);
        SecurityAlgorithm(proc,sour);
    }

    // Available
    public void setAvailable(int sour){
        System.out.println("请设置可用资源的总数：");
        for (int i = 0; i < sour; i++) {
            Available[i] = in.nextInt();
        }
    }

    // Need
    public void setNeed(int proc,int sour) {
        System.out.println("请设置各进程的尚需需求矩阵Need：");
        for (int i = 0; i < proc; i++) {
            for (int j = 0; j < sour; j++) {
                Need[i][j] = in.nextInt();
            }
        }
    }



    // Allocation
    public void setAllocation(int proc,int sour) {
        System.out.println("请设置各进程分配矩阵Allocation：");
        for (int i = 0; i < proc; i++) {
            for (int j = 0; j < sour; j++) {
                Allocation[i][j] = in.nextInt();
            }
        }
    }

    // print
    public void printSystemVariable(int proc,int sour){
        System.out.println("此时资源分配量如下：");
        System.out.println("进程  " + "  Allocation "+"   Need  "+"    Available ");

        for(int i = 0;i < proc;i++){
            System.out.print("P"+i+"  ");

            System.out.print("|  ");

            for(int j=0;j < sour;j++){
                System.out.print(Allocation[i][j]+"  ");
            }

            System.out.print("|  ");

            for(int j=0;j<sour;j++){
                System.out.print(Need[i][j]+"  ");
            }

            System.out.print("|  ");

            if(i==0){
                for(int j=0;j<sour;j++){
                    System.out.print(Available[j]+"  ");
                }
            }
            System.out.println();
        }
    }

    // request
    public void setRequest(int proc,int sour) {

        System.out.println("请输入请求资源的进程编号：");
        num= in.nextInt(); //设置全局变量进程编号num
        System.out.println("请输入请求各资源的数量：");
        for (int j = 0; j < sour; j++) {
            Request[num][j] = in.nextInt();
        }

        String str = Arrays.toString(Request[num]);
        System.out.println("即进程P" + num + "对各资源请求Request：(" +
                str+")");
        BankerAlgorithm(proc,sour);
    }

    // 银行家算法
    public void BankerAlgorithm(int proc,int sour) {
        boolean T = true;
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        for(int i = 0;i < sour;i++){
            if(Request[num][i] <= Need[num][i]){//判断Request是否小于Need  1⃣
                count1++;
            }
        }

        for(int i = 0;i < sour;i++){//判断Request是否小于Available  2⃣
            if(Request[num][i] <= Available[i]){
                count2++;
            }
        }

        if (count1 == sour) {
            if(count2 == sour) {
                //T = true 时，改变数据 条件1和2成立 修改Available Allocation Need的值  3⃣
                for (int i = 0; i < sour; i++) {
                    Available[i] -= Request[num][i];
                    Allocation[num][i] += Request[num][i];
                    Need[num][i] -= Request[num][i];
                }
            } else {
                System.out.println("当前没有足够的资源可分配");
                T = false;
            }
        }else {
            System.out.println("进程P" + num + "请求已经超出最大需求量Need.");
            T = false;
        }

        if(T==true){ // 安全性算法  4⃣
            printSystemVariable(proc,sour);
            System.out.println("现在进入安全算法：");
            boolean ret = SecurityAlgorithm(proc,sour);

            // 申请资源后，系统进入死锁状态，恢复资源矩阵状态。
            if(ret == false){
                System.out.println("进程" + num + "申请资源后，系统进入死锁状态，分配失败!");
                for (int i = 0; i < sour; i++) {
                    Available[i] += Request[num][i];
                    Allocation[num][i] -= Request[num][i];
                    Need[num][i] += Request[num][i];
                }
                printSystemVariable(proc, sour);
            }

            else{
                // 所有进程的Need都满足的情况->打印
                for(int i = 0;i < sour;i++){
                    if(Need[num][i] == 0){
                        count3++;
                    }
                }
                if(count3 == sour){
                    for (int i = 0; i < sour; i++) {
                        Available[i] += Allocation[num][i];
                    }
                    printSystemVariable(proc,sour); // 打印资源分配情况
                }
            }
        }
    }

    // 安全性算法
    public boolean SecurityAlgorithm(int proc,int sour) {

        for (int i = 0; i < sour; i++) {//设置工作向量
            Work[i] = Available[i];
        }

        boolean[] Finish = new boolean[proc];//初始化Finish
        for (int i = 0; i < proc; i++) {
            Finish[i] = false;
        }

        boolean ret = false;
        int count2 = 0;//计数标志
        int circle = 0;
        int count = 0;//完成进程数
        int[] S = new int[proc];//安全序列
        boolean flag = true;

        System.out.println("试分配:");
        while (count < proc) {
            if (flag) {
                System.out.println("进程  " + "   Work  " + "   Allocation " + "    Need  " + "     Work+Allocation "+"  Finish");
                flag = false;
            }

            for (int i = 0; i < proc; i++) { //遍历进程
                count2 = 0;
                for (int n = 0; n < sour; n++) {

                    if (Finish[i] == false && Need[i][n] <= Work[n]) {//判断进程是否已试分配成功，
                        // 若没有分配，且资源需求数小于可用资源数，输出
                        count2++;
                        if (count2 == sour) {
                            System.out.print("P" + i + "  ");

                            for (int m = 0; m < sour; m++) {
                                System.out.print(Work[m] + "  ");
                            }

                            System.out.print("|  ");

                            for (int j = 0; j < sour; j++) {
                                Work[j] += Allocation[i][j];
                            }

                            Finish[i] = true;//当前进程能满足时，设为true
                            temp[count] = i;
                            count++;//满足，进程数加1

                            for (int j = 0; j < sour; j++) {
                                System.out.print(Allocation[i][j] + "  ");
                            }

                            System.out.print("|  ");

                            for (int j = 0; j < sour; j++) {
                                System.out.print(Need[i][j] + "  ");
                            }

                            System.out.print("|  ");

                            for (int j = 0; j < sour; j++) {
                                System.out.print(Work[j] + "  ");
                            }

                            System.out.print("\t"+" |  ");


                            System.out.print("\t"+Finish[i]);

                            System.out.println();
                        }
                    }
                }
                 }
            circle++;

            if (count == proc) {
                ret = true;
                System.out.println("系统是安全的");
                System.out.print("此时存在一个安全序列：");
                for (int i = 0; i < proc; i++) {
                    System.out.print("P" + temp[i]);
                    if (i < proc - 1) {
                        System.out.print("->");
                    }
                }
                System.out.println();
                break;
            }
            if (count < circle) {
                count = proc;
                ret = false;
                for (int i = 0; i < proc; i++) {
                    if (Finish[i] == false) {
                        System.out.println("当前系统处于不安全状态,故不存在安全序列");
                        break;
                    }
                }
            }
        }
        //System.out.println(circle);
        return ret;
    }

}



// 测试用例
// 进程数 5
// 资源数 4
// 资源总数 3 12 14 14
// available 1 6 2 2
// Max 0 0 4 4 2 7 5 0 3 6 10 10 0 9 8 4 0 6 6 10
// AL 0 0 3 2 1 0 0 0 1 3 5 4 0 3 3 2 0 0 1 4
// Need 0 0 1 2 1 7 5 0 2 3 5 6 0 6 5 2 0 6 5 6

// request     p2      1 2 2 2

// 1. request > Need
// 2. request  > available
// 3. 试分配 找到安全序列 找不到安全序列


// 测试用例
// 请输入进程个数：
// 5
// 请输入资源种类数：
// 3
// 请设置可用资源的总数：
// 3 3 2
// 请设置各进程的尚需需求矩阵Need：
// 7 4 3 1 2 2 6 0 0 0 1 1 4 3 1
// 请设置各进程分配矩阵Allocation：
// 0 1 0 2 0 0 3 0 2 2 1 1 0 0 2

