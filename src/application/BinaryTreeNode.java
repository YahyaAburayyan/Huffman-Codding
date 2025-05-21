package application;


public class BinaryTreeNode {
	// Attributes
	private BinaryTreeNode left;
	private BinaryTreeNode right;
	private Byte data;
	private String huffCode;
	private int frequancy;
	
	
	// Constructors
	public BinaryTreeNode() {
		
	}
	
	public BinaryTreeNode(BinaryTreeNode left, BinaryTreeNode right) {
		this.left = left;
		this.right = right;
		this.frequancy = left.frequancy + right.frequancy;
	}
	
	public BinaryTreeNode(Byte data, int frequancy) {
		this.data = data;
		this.frequancy = frequancy;
	}
	
	public BinaryTreeNode(int frequancy) {
		this.frequancy = frequancy;
	}
	
	// methods 
	public String postOrder() {
		return postOrder(this);
	}
	private String postOrder(BinaryTreeNode current) {
		if(current != null) {
			return postOrder(current.left) + postOrder(current.right) + current.toString();
		}
		return "";
	}
	
	
	public static void findHuffCodesFromPath(BinaryTreeNode root ) {
		String huffCode = "";
		trackPath(root, huffCode);
	}
	private static void trackPath(BinaryTreeNode root, String huffCode) {
		if(root == null) { // empty tree
			return;
		}
		if(root.data == null) { // none leaf node , internal node
			trackPath(root.left, huffCode + "0" );
			trackPath(root.right, huffCode + "1" );
		}else {
			root.setHuffCode(huffCode); // leaf node assign huffCode to it
		}
	}
	
	
	
	// Setters and Getters
	public BinaryTreeNode getLeft() {
		return left;
	}

	public void setLeft(BinaryTreeNode left) {
		this.left = left;
	}

	public BinaryTreeNode getRight() {
		return right;
	}

	public void setRight(BinaryTreeNode right) {
		this.right = right;
	}

	public Byte getData() {
		return data;
	}

	public void setData(Byte data) {
		this.data = data;
	}

	public String getHuffCode() {
		return huffCode;
	}

	public void setHuffCode(String huffCode) {
		this.huffCode = huffCode;
	}

	public int getFrequancy() {
		return frequancy;
	}

	public void setFrequancy(int frequancy) {
		this.frequancy = frequancy;
	}
	
	public void incFrequancy() {
		this.frequancy++;
	}
	
	public boolean isLeaf() {
		return this.left == null && this.right == null;
	}
	
	
	public static String byteToBinaryString(byte b) {
	    StringBuilder binaryString = new StringBuilder();
	    for (int i = 7; i >= 0; i--) {
	        int bit = (b >> i) & 1;
	        binaryString.append(bit);
	    }
	    return binaryString.toString();
	}
	
	
	@Override
	public String toString() {
		if(data != null) { // leaf 
			//System.out.println((char)(data & 0xFF) + "-->" + byteToBinaryString(data));
			return "0" + byteToBinaryString(data);
		}else {
			//System.out.println("internal");
			return "1"; // none Leaf
		}
	}
	
}
