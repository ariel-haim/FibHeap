import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;
/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    private int size;
    private HeapNode first;
    private HeapNode min;
    private static int cutsNum = 0;
    private static int linksNum = 0;
    private int markedNum;
    private int treesNum;
    private static final double GOLDEN = ( 1 + Math.sqrt(5))/2;


    public FibonacciHeap(){ // note: do not remove empty constructor
        this.size = 0;
        this.first = null;
        this.min = null;
        markedNum = 0;
        treesNum = 0;
    }
   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty()
    {
    	return size == 0; // should be replaced by student code
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key)
    {
        size++;
        treesNum++;
    	HeapNode newNode = new HeapNode(key);
        if (this.size == 1){
            min = newNode;
            first = newNode;
            first.next = first;
            first.prev = first;
            return newNode;
        }
        else if (newNode.getKey() < min.getKey()){
            min = newNode;
        }
        saveNewRootFirst(newNode);

        return newNode;
    }

    /**
     *
     * @param newNode
     * puts the newly inserted node as the first HeapNode of the heap
     */
    private void saveNewRootFirst(HeapNode newNode){
        first.prev.next = newNode;
        newNode.prev = first.prev;
        first.prev = newNode;
        newNode.next = first;
        first = newNode;
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin()  // min is never marked (its a root) so no markedNum--
    {
        if (isEmpty()){
            return;
        }
        size--;
        treesNum = treesNum + min.rank - 1; // temporary before consolidating
        createRootsFromChildren(min);

        if (min == first){
            if (min.rank == 0) {
                first = first.next;
            }
            else {
                first = first.child;
            }
        }
        if (min.rank != 0) {
            min.child.prev.next = min.next;
            min.next.prev = min.child.prev;
            min.child.prev = min.prev;
            min.prev.next = min.child;
            min.child.parent = null;
        }

        if (min.rank == 0) {
            min.next.prev = min.prev;
            min.prev.next = min.next;
        }
        if (treesNum == 0){
            this.first = null;
            this.min = null;
            markedNum = 0;
            return;
        }
        successiveLinking();
        /// find new minimum
        updateMinimum();
     	
    }

    /**
     * commits succesive linking:
     * links between 2 trees with the same rank until we're left with one tree
     * called after deleting the minimum
     */

    private void successiveLinking() {
        int numOfRanks =(int) (Math.log(size) / Math.log(GOLDEN))+1;
        HeapNode[] rankList = new HeapNode[numOfRanks];
        HeapNode currTree = first;

        for (int i = 0; i < treesNum; i++) {
            HeapNode nextTree = currTree.next;
            while (rankList[currTree.rank] != null) {
                currTree = link(currTree, rankList[currTree.rank]);
                rankList[currTree.rank-1] = null;
            }
            rankList[currTree.rank] = currTree;
            currTree = nextTree;
        }
        /// now go over the array and link new trees + update treeNum WHEN DOING SO
        HeapNode node = null;
        HeapNode newNode = null;
        this.treesNum = 0;
        for (int i = 0 ; i<rankList.length; i++){
            if (rankList[i] != null) {
                this.treesNum++;
                newNode = rankList[i];
                if(node == null){
                    node = newNode;
                    node.prev = newNode;
                    node.next = newNode;
                    continue;
                }
                node.prev.next = newNode;
                newNode.prev = node.prev;
                node.prev = newNode;
                newNode.next = node;
            }
        }
        this.first = node;
        /// update ALL FIELDS so this.heap ===== newHeap /except size,min
    }

    /**
     * @pre: node1 and node2 have the same rank
     * @param node1
     * @param node2
     * links between node1 and node2, the node with the minimal key will be the new root
     * and the other one will be the child
     * @return node that links between node1 and node2
     */

    private HeapNode link(HeapNode node1, HeapNode node2) {
        HeapNode newRoot = node1;
        HeapNode newChild = node2;
        if (node2.getKey()<node1.getKey()) {
            newRoot = node2;
            newChild = node1;
        }
        if (newRoot.rank == 0) {
            newChild.next = newChild;
            newChild.prev = newChild;
            newRoot.child = newChild;
            newChild.parent = newRoot;
        }
        else {
            newChild.next = newRoot.child;
            newChild.prev = newRoot.child.prev;
            newRoot.child.prev.next = newChild;
            newRoot.child.prev = newChild;
            newRoot.child = newChild;
            newChild.parent = newRoot;
        }
        newRoot.rank++; // update rank of linked tree
        linksNum++;
        return newRoot;
    }

    /**
     * after deleteMin, we need to find the new minimal node from the roots
     */
    private void updateMinimum() {
        HeapNode curr_node = first;
        int minimum = first.getKey();
        for (int i=0; i < treesNum; i++) {
            if (curr_node.getKey() <= minimum) {
                minimum = curr_node.getKey();
                min = curr_node;
            }
            curr_node = curr_node.next;
        }
    }

    /**
     *
     * @param parent
     * adds the children of parent as new roots in the heap
     */
    private void createRootsFromChildren(HeapNode parent) {
        HeapNode curr_node = parent.child;
        for (int i=0; i<min.rank; i++) {
            if (curr_node.mark) {

                markedNum--;
            }
            curr_node.mark = false;
            curr_node.parent = null;
            curr_node = curr_node.next;
        }
    }

    /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2)
    {
        if (heap2.isEmpty()){
            return;
        }
        if (this.isEmpty()){
            this.size = heap2.size;
            this.first = heap2.first;
            this.min = heap2.min;
            markedNum = heap2.markedNum;
            treesNum = heap2.treesNum;
            return;
        }
        size += heap2.size;
        markedNum += heap2.markedNum;
        if (heap2.min.getKey() < min.getKey()) {
            min = heap2.min;
        }
        treesNum += heap2.treesNum;

        first.prev.next = heap2.first;
        heap2.first.prev.next = first;
        HeapNode tmp = first.prev;
        first.prev = heap2.first.prev;
        heap2.first.prev = tmp;

    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size()
    {
    	return size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * (Note: The size of of the array depends on the maximum order of a tree.)  
    * 
    */
    public int[] countersRep() {
        if (size==0){
            return new int[0];
        }
        int numOfRanks =(int) (Math.log(size) / Math.log(GOLDEN))+1;
    	int[] arr = new int[numOfRanks];
        HeapNode currNode = first;
        for (int i=0; i<treesNum; i++) {
            arr[currNode.rank]++;
            currNode = currNode.next;
        }
        int rlen = arr.length;
        while (arr[rlen-1] == 0) {
            --rlen;
        }
        arr = Arrays.copyOf(arr, rlen);
        return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) 
    {
    	this.decreaseKey(x, x.getKey()-min.getKey()+1);
        this.deleteMin(); // size is decreased here
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {
        x.key -= delta;
        if (x.getKey() < min.getKey()) { // update minimum
            min = x;
        }
        if (x.parent == null) { // avoid null pointer
            return;
        }
        if (x.getKey()>x.parent.getKey()) {
            return;
        }
        cascadingCuts(x); // do all cuts needed + update CUTSNUM and NUMTREES and MARKEDNUM
    }

    /**
     *
     * @param x: the node who's key was decreased
     *         after decreseaing the key of a node we need to cut the node from its parent
     *         if the node's key is smaller than its parent.
     *         if the parent is marked, then we cut its as well, otherwise we mark it and stop
     *         continue recursively
     */
    public void cascadingCuts(HeapNode x) {// do all cuts needed + update CUTSNUM and NUMTREES and MARKEDNUM
        HeapNode parent = x.parent;
        x.prev.next = x.next;
        x.next.prev = x.prev;
        if (x.parent.rank == 1) {
            x.parent.child = null;
        }
        else {
            x.parent.child = x.next;
        }
        x.parent = null;
        saveNewRootFirst(x);

        // updates
        if (x.mark) {
            markedNum--;
        }
        x.mark = false;
        cutsNum++;
        treesNum++;
        parent.rank--;
        if (parent.mark) {
            cascadingCuts(parent);
        }
        else {
            if (parent.parent != null) { // parent is not root
                parent.mark = true;
                markedNum++;
            }
        }
    }

   /**
    * public int nonMarked() 
    *
    * This function returns the current number of non-marked items in the heap
    */
    public int nonMarked() 
    {    
        return this.size - this.markedNum;
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
        return this.treesNum + 2*markedNum; // should be replaced by student code
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks()
    {    
    	return linksNum;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return cutsNum;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
      * we use a helper heap, and insert the children of the current minimal node, to the helper heap,
      * as one of them is definitely the next minimal node.
      * we do it k times, and each time the rank of the minimal node is deg(H) at most.
      * hence, the time complexity is as required.
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        if (k == 0){
            return new int[0];
        }
        int[] arr = new int[k];
        FibonacciHeap heapHelper = new FibonacciHeap();
        HeapNode newNode = heapHelper.insert(H.min.getKey());
        newNode.pointer = H.min;
        for (int i = 0; i < k; i++) {
            HeapNode nextMin = heapHelper.findMin().pointer;
            arr[i] = nextMin.getKey();
            heapHelper.deleteMin();

            int numChildren = nextMin.rank;
            if (numChildren != 0) {
                HeapNode curr_child = nextMin.child;
                for (int j = 0; j<numChildren; j++) {
                    HeapNode newChild = heapHelper.insert(curr_child.getKey());
                    newChild.pointer = curr_child;
                    curr_child = curr_child.next;
                }
            }
        }
        return arr;
    }

    /**
     *
     * @return the first node of the heap
     */
    public HeapNode getFirst() {
        return first;
    }

    /**
     *
     * @return the minimal node of the heap
     */
    public HeapNode getMin() {
        return min;
    }

    /**
     *
     * @return how many cuts were made through the program
     */
    public static int getCutsNum() {
        return cutsNum;
    }

    /**
     *
     * @return how many links were made through the program
     */
    public static int getLinksNum() {
        return linksNum;
    }

    /**
     *
     * @return how many nodes are marked
     */
    public int getMarkedNum() {
        return markedNum;
    }

    /**
     *
     * @return the number of trees in the heap
     */
    public int getTreesNum() {
        return treesNum;
    }


    /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
        public int rank;
        public boolean mark;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public HeapNode pointer;

    	public HeapNode(int key) {
    		this.key = key;
            this.rank = 0;
            this.mark = false;
            this.child = null;
            this.next = null;
            this.prev = null;
            this.parent = null;

    	}

        public HeapNode(int key, HeapNode originalNode) {
            this.key = key;
            this.rank = 0;
            this.mark = false;
            this.child = null;
            this.next = null;
            this.prev = null;
            this.parent = null;
            this.pointer = originalNode;

        }

        public int getRank() {
            return rank;
        }

        public boolean getMark() {
            return mark;
        }

        public boolean getMarked() {
            return mark;
        }

        public int getKey() {
    		return this.key;
    	}

        public boolean isMarked() {
            return mark;
        }

        public HeapNode getChild() {
            return child;
        }

        public HeapNode getNext() {
            return next;
        }

        public HeapNode getPrevious() {
            return prev;
        }
        public HeapNode getPrev() {
            return prev;
        }

        public HeapNode getParent() {
            return parent;
        }

        public HeapNode getPointer() {
            return pointer;
        }


    }
}
