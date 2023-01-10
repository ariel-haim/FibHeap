import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


public class t {
    static final PrintStream stream = System.out;
    static void printIndentPrefix(ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        for (int i = 0; i < size - 1; ++i) {
            stream.format("%c   ", hasNexts.get(i).booleanValue() ? '│' : ' ');
        }
    }

    static void printIndent(FibonacciHeap.HeapNode heapNode, ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        printIndentPrefix(hasNexts);

        stream.format("%c── %s\n",
                hasNexts.get(size - 1) ? '├' : '╰',
                heapNode == null ? "(null)" : String.valueOf(heapNode.getKey())
        );
    }

    static String repeatString(String s,int count){
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < count; i++) {
            r.append(s);
        }
        return r.toString();
    }

    static void printIndentVerbose(FibonacciHeap.HeapNode heapNode, ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        if (heapNode == null) {
            printIndentPrefix(hasNexts);
            stream.format("%c── %s\n", hasNexts.get(size - 1) ? '├' : '╰', "(null)");
            return;
        }

        Function<Supplier<FibonacciHeap.HeapNode>, String> keyify = (f) -> {
            FibonacciHeap.HeapNode node = f.get();
            return node == null ? "(null)" : String.valueOf(node.getKey());
        };
        String title  = String.format(" Key: %d ", heapNode.getKey());
        List<String> content =  Arrays.asList(
                String.format(" Rank: %d ", heapNode.getRank()),
                String.format(" Marked: %b ", heapNode.isMarked()),
                String.format(" Parent: %s ", keyify.apply(heapNode::getParent)),
                String.format(" Next: %s ", keyify.apply(heapNode::getNext)),
                String.format(" Prev: %s ", keyify.apply(heapNode::getPrevious)),
                String.format(" Child: %s", keyify.apply(heapNode::getChild))
        );

        /* Print details in box */
        int length = Math.max(
                title.length(),
                content.stream().map(String::length).max(Integer::compareTo).get()
        );
        String line = repeatString("─", length);
        String padded = String.format("%%-%ds", length);
        boolean hasNext = hasNexts.get(size - 1);

        //print header row
        printIndentPrefix(hasNexts);
        stream.format("%c── ╭%s╮%n", hasNext ? '├' : '╰', line);

        //print title row
        printIndentPrefix(hasNexts);
        stream.format("%c   │" + padded + "│%n", hasNext ? '│' : ' ', title);

        // print separator
        printIndentPrefix(hasNexts);
        stream.format("%c   ├%s┤%n", hasNext ? '│' : ' ', line);

        // print content
        for (String data : content) {
            printIndentPrefix(hasNexts);
            stream.format("%c   │" + padded + "│%n", hasNext ? '│' : ' ', data);
        }

        // print footer
        printIndentPrefix(hasNexts);
        stream.format("%c   ╰%s╯%n", hasNext ? '│' : ' ', line);
    }

    static void printHeapNode(FibonacciHeap.HeapNode heapNode, FibonacciHeap.HeapNode until, ArrayList<Boolean> hasNexts, boolean verbose) {
        if (heapNode == null || heapNode == until) {
            return;
        }
        hasNexts.set(
                hasNexts.size() - 1,
                heapNode.getNext() != null && heapNode.getNext() != heapNode && heapNode.getNext() != until
        );
        if (verbose) {
            printIndentVerbose(heapNode, hasNexts);
        } else {
            printIndent(heapNode, hasNexts);
        }

        hasNexts.add(false);
        printHeapNode(heapNode.getChild(), null, hasNexts, verbose);
        hasNexts.remove(hasNexts.size() - 1);

        until = until == null ? heapNode : until;
        printHeapNode(heapNode.getNext(), until, hasNexts, verbose);
    }

    public static void print(FibonacciHeap heap, boolean verbose) {
        if (heap == null) {
            stream.println("(null)");
            return;
        } else if (heap.isEmpty()) {
            stream.println("(empty)");
            return;
        }

        stream.println("╮");
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(false);
        printHeapNode(heap.getFirst(), null, list, verbose);
    }

    public static void demo() {
        /* Build an example */
        FibonacciHeap heap = new FibonacciHeap();

        FibonacciHeap.HeapNode n0 = heap.insert(20);
        heap.insert(8);
        heap.insert(3);
        heap.insert(100);
        FibonacciHeap.HeapNode n1 = heap.insert(15);
        heap.insert(18);
        heap.insert(1);
        heap.insert(2);
        heap.insert(7);
        heap.deleteMin();
        FibonacciHeap.HeapNode n2 = heap.insert(500);

        stream.println("Printing in regular mode:");
        t.print(heap, false);
        heap.deleteMin();
        t.print(heap, false);

        FibonacciHeap heap2 = new FibonacciHeap();
        heap2.meld(heap);
        t.print(heap2, false);
        System.out.println(Arrays.toString(heap2.countersRep()));
        System.out.println(heap2.potential());
        heap2.decreaseKey(n0, 1);
        t.print(heap2, false);
        heap2.decreaseKey(n0, 13);
        t.print(heap2, false);
        heap2.delete(n1);
        t.print(heap2, false);
        System.out.println(heap2.potential());
        heap2.delete(n2);
        t.print(heap2, false);
        FibonacciHeap heapy = new FibonacciHeap();
        FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[7];
        for (int i=0; i<7; i++){
            FibonacciHeap.HeapNode node = heapy.insert(i);
            nodes[i] = node;
        }
        heapy.delete(nodes[0]);
        System.out.println();
        System.out.println(Arrays.toString(heap2.countersRep()));
        System.out.println(heap2.potential());
        System.out.println(FibonacciHeap.getCutsNum());
        System.out.println(heap2.getMin().isMarked());
        heap2.delete(n0);
        t.print(heap2, false);
        heap2.delete(heap2.getMin());
        t.print(heap2, false);

        FibonacciHeap heap3 = new FibonacciHeap();
        FibonacciHeap.HeapNode t0 = heap3.insert(20);
        heap3.delete(t0);
        t.print(heap3, false);
        FibonacciHeap.HeapNode t1 = heap3.insert(20);
        heap3.meld(heap2);
        t.print(heap3, false);
    }

    public static void main(String[] args) {
        demo();
    }

    private static void q1() {
        long startTime = System.nanoTime();

        FibonacciHeap heap = new FibonacciHeap();
        int k = 20;
        int m = 1 << k;
        List<FibonacciHeap.HeapNode> array = new ArrayList<>();
        //FibonacciHeap.HeapNode extra = null;

        for (int i = m-1; i >= -1; i--) {
            FibonacciHeap.HeapNode node = heap.insert(i);
            if( ((m-i+1) & (m-i)) == 0) { //((m-i) & (m-i-1)) == 0 && m-i!=1 //for m-2^i keys
                array.add(node);
            }
            //if(i == m-2) {
            //    extra = node;
            //}
        }
        heap.deleteMin();
        //t.print(heap, false);
        for (int i = array.size()-1; i >= 0; i--) {
            heap.decreaseKey(array.get(i), m+1);
        }
        //heap.decreaseKey(extra, m+1);
        stream.println("Marked: "+ heap.getMarkedNum());
        stream.println("Tress: "+ heap.getTreesNum());
        stream.println("Potential: "+ heap.potential());
        stream.println(FibonacciHeap.getLinksNum());
        stream.println(FibonacciHeap.getCutsNum());

        //stream.println("Printing in regular mode:");
        //t.print(heap, false);

        long endTime = System.nanoTime();
        System.out.println("That took " + (endTime - startTime)/1000 + " milliseconds");
    }

    private static void q2() {
        long startTime = System.nanoTime();

        FibonacciHeap heap = new FibonacciHeap();
        int k = 14;
        int m = (int) (Math.pow(3, k) - 1);

        for (int i = 0; i <= m; i++) {
            heap.insert(i);
        }
        for (int i = 1; i <= 3*(m/4); i++) {
            heap.deleteMin();
        }
        stream.println("Marked: "+ heap.getMarkedNum());
        stream.println("Trees: "+ heap.getTreesNum());
        stream.println("Potential: "+ heap.potential());
        stream.println(FibonacciHeap.getLinksNum());
        stream.println(FibonacciHeap.getCutsNum());

        //stream.println("Printing in regular mode:");
        //t.print(heap, false);

        long endTime = System.nanoTime();
        System.out.println("That took " + (endTime - startTime)/1000 + " microseconds");
    }

}