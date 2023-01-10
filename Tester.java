
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
        System.out.println(heap.findMin().getKey());
    }

}
