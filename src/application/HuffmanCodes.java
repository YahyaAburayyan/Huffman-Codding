package application;

public class HuffmanCodes {
	private BinaryTreeNode root;
	private String codes[] ;
	private Heap heap;
	private BinaryTreeNode [] frequancies;
	
	// Constructors 
	public HuffmanCodes() {
		
	}
	
	public HuffmanCodes (BinaryTreeNode [] frequancies) {
		codes = new String[256];
		heap = new Heap(256);
		this.frequancies  = frequancies;
		bulidHuffTree();
	}
	
	private void bulidHuffTree() {
		for(int i=0 ; i< frequancies.length ;i++) {
			if(frequancies[i].getFrequancy() > 0) {
				heap.insert(frequancies[i]);
			}
		}
		while(heap.getSize() > 1) { // Building the Huffman tree using the heap 
			BinaryTreeNode left = heap.extractMin();
			BinaryTreeNode right = heap.extractMin();
			BinaryTreeNode z = new BinaryTreeNode(left, right);
			heap.insert(z);
		}
		this.root = heap.extractMin();
		if(root.getLeft() == null && root.getRight() == null) { // only one character in the whole file
			root.setHuffCode("1");
		}else {
			String code = "";
			getCodesFromHuffTree(root, code);
		}
		System.out.println("Heap Size now = " + heap.getSize()); // just to see that the heap empty
		
	}
	
	private void getCodesFromHuffTree(BinaryTreeNode root, String code) {
		if(root == null) { // empty tree (Base case)
			return;
		}
		if(root.isLeaf()) { // we reached a leaf and we want to give it a code
			int data = root.getData();
			if(data < 0) {
				data += 256;
			}
			codes[data] = code;
			root.setHuffCode(code);
		}else {
			getCodesFromHuffTree(root.getLeft(), code +"0"); // Recursive calls
			getCodesFromHuffTree(root.getRight(), code +"1");
		}
		
	}
	
	public String postOrderRepresintationfHuffCodes() {
		return root.postOrder();
	}

	// Setters and Getters 
	public BinaryTreeNode getRoot() {
		return root;
	}

	public void setRoot(BinaryTreeNode root) {
		this.root = root;
	}

	public String[] getCodes() {
		return codes;
	}

	public void setCodes(String[] codes) {
		this.codes = codes;
	}

	public Heap getHeap() {
		return heap;
	}

	public void setHeap(Heap heap) {
		this.heap = heap;
	}

	public BinaryTreeNode[] getFrequancies() {
		return frequancies;
	}

	public void setFrequancies(BinaryTreeNode[] frequancies) {
		this.frequancies = frequancies;
	}
	
	
}
