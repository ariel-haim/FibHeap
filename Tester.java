
public class Tester {
    public static void main(String[] args) {
        FibonacciHeap heap = new FibonacciHeap();
        FibonacciHeap.HeapNode pointer = null;
        for (int i =0; i<100; i++) {
            pointer = heap.insert(i);
        }
        heap.insert(-200);
        System.out.println(heap.findMin().getKey());
        heap.deleteMin();
        System.out.println(heap.findMin().getKey());
        System.out.println(pointer.getKey());
        heap.decreaseKey(pointer,100);
        // heap.delete(pointer);
        System.out.println(heap.findMin().getKey());

        FibonacciHeap heap2 = new FibonacciHeap();
        FibonacciHeap.HeapNode pointer2 = null;
        for (int i =0; i<9; i++) {
            pointer = heap2.insert(i);
        }
        heap2.deleteMin();
        int[] arr = FibonacciHeap.kMin(heap2,7);
        int i = 0;
    }

}
