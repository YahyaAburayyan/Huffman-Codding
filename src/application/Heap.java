package application;

public class Heap {
	// Attributes
	private BinaryTreeNode [] heap;
	private int size;
	private int maxSize;
	
	// constructor 
	public Heap(int size) {
		this.maxSize = size;
		this.size = 0;
		this.heap = new BinaryTreeNode[maxSize + 1];
	}
	
	
	// heapify method here 
	private void heapify(BinaryTreeNode [] heap, int i) {
		int left = 2*i;
		int right = 2*i+1;
		int smallest = i;
		if (2*i <= size && heap[left].getFrequancy() < heap[smallest].getFrequancy()) {
			smallest = left;
		}
		if(right <= size && heap[right].getFrequancy() < heap[smallest].getFrequancy()) {
			smallest = right;
		}
		if(smallest != i) {
			BinaryTreeNode temp = heap[i];
			heap[i] = heap[smallest];
			heap[smallest] = temp;
			heapify(heap, smallest);
		}
	}
		
	// insert 
	public void insert(BinaryTreeNode x) {
		size++;
		heap [size] = x;
		int current = size;
		while (current > 1 && heap[current].getFrequancy() < heap[current/2].getFrequancy()) {
			BinaryTreeNode temp = heap[current];
			heap[current] = heap[current/2];
			heap[current/2] = temp;	
			current = current/2;
		}
	}
	
	// extract min 
	public BinaryTreeNode extractMin() {
		BinaryTreeNode min = heap[1];
		heap[1] = heap[size];
		size--;
		heapify(heap, 1);
		return min;
		
	}
	
	public void print() {
		for(int i=1; i<=size ; i++) {
			System.out.println("index = " + i + " value = " + heap[i].getFrequancy());
		}
	}


	public BinaryTreeNode[] getHeap() {
		return heap;
	}


	public void setHeap(BinaryTreeNode[] heap) {
		this.heap = heap;
	}


	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	public int getMaxSize() {
		return maxSize;
	}


	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	
	
}
