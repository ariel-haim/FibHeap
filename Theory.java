public class Theory {
    public static void main(String[] args) {
//        Q1();
        Q2();

    }

    private static void Q1() {
        int m = (int) Math.pow(2,20);
        int log2 = (int) (Math.log(m) / Math.log(2));
        System.out.println("Started M=2^"+log2);
        long startTime = System.nanoTime();
        Q1_run(m);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
        System.out.println("Ended M=2^"+log2);
        System.out.println("Duration: "+duration);
    }

    public static void Q1_run(int m){
        FibonacciHeap heap = new FibonacciHeap();
        FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[m];
        for(int i=m-1; i>=-1; i--){
            FibonacciHeap.HeapNode node = heap.insert(i);
            if (i != -1){
                nodes[i] = node;
            }
        }
        heap.deleteMin();
        int log2 = (int) (Math.log(m) / Math.log(2));
        for (int i=log2; i>=1; i--){
            int index = m-(int)Math.pow(2, i) + 1;
            heap.decreaseKey(nodes[index], m+1);
        }
        heap.decreaseKey(nodes[m-2], m+1);
    System.out.println("links: " + FibonacciHeap.getLinksNum());
    System.out.println("cuts: " + FibonacciHeap.getCutsNum());
    System.out.println("potential: " + heap.potential());
    }

    private static void Q2() {
        int i = 14;
        int m = (int) Math.pow(3,i) -1;
        m=20;
        System.out.println("Started M=3^"+i+"-1");
        long startTime = System.nanoTime();
        Q2_run(m);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
        System.out.println("Ended M=3^"+i+"-1");
        System.out.println("Duration: "+duration);
    }

    public static void Q2_run(int m){
        FibonacciHeap heap = new FibonacciHeap();
        for (int k=0; k<=m; k++){
            heap.insert(k);
        }
        int res = (int) (0.75*m);
        for (int k=0; k<=res; k++){
            heap.deleteMin();
            System.out.println(heap.getLinksNum());
            t.print(heap,false);
        }
        System.out.println("links: " + FibonacciHeap.getLinksNum());
        System.out.println("cuts: " + FibonacciHeap.getCutsNum());
        System.out.println("potential: " + heap.potential());
    }
}


