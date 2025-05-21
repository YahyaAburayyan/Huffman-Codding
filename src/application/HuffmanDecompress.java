package application;

import java.io.*;

public class HuffmanDecompress {
    private BinaryTreeNode[] frequancies = new BinaryTreeNode[256];
    private File inputFile;
    private File decompressedFile;
    private long lengthOfCompressedFile;
    private long lengthOfFileAfterDecompression;
    private String header = "";
    private String orginalFileName;
    private static final int BUFFER_SIZE = 8192;

    public HuffmanDecompress(File input) {
        System.out.println("DECOMPRESS STARTED-------------------------------------------------");
        this.inputFile = input;
        this.lengthOfCompressedFile = input.length();
        System.out.println("The file size before decomp = " + lengthOfCompressedFile);
        
        try (FileInputStream in = new FileInputStream(input)) {
            
            // Read extension length , first byte in the compressed file
            int extLength = in.read();
            if (extLength == -1) {
                throw new IllegalArgumentException("Invalid file format: EOF reading extension length");
            }
            header += "Extinsion Length = " + byteToBinaryString((byte) extLength) + "\n";
            // Read extension , # of bytes as the length above
            byte[] fileExtension = new byte[extLength];
            if (in.read(fileExtension) != extLength) {
                throw new IllegalArgumentException("Invalid file format: EOF reading extension");
            }
            String fileExt = new String(fileExtension);
            header += "Extinsion = ";
            for(byte b: fileExtension) {
            	header += byteToBinaryString(b);
            }
            header += "\n";
            System.out.println("Original file Extension is = " + fileExt);

            // Read Huffman tree actual length before padding (4 bytes)
            int treeLength = 0;
            header += "Tree Length = ";
            for (int i = 0; i < 4; i++) {
                int b = in.read();
                if (b == -1) {
                    throw new IOException("Invalid file format: EOF reading tree length");
                }
                // left shift by 8 to make space for the new byte
                // or to merge the new byte to the full result
                treeLength = (treeLength << 8) | b; 
                header += byteToBinaryString((byte) b) + " ";
            }
            header += "\n";
            System.out.println("The actual length of huff tree = " + treeLength);

            // Read Huffman tree
            int numberOfHuffmanCodesBytes;
            if(treeLength % 8 == 0) {
            	numberOfHuffmanCodesBytes = treeLength / 8;
            }else {
            	numberOfHuffmanCodesBytes = treeLength / 8 + 1;
            }
            byte[] treeBuffer = new byte[numberOfHuffmanCodesBytes];
            // if the input stream didn't read the same number of bytes then something wrong in the file
            if (in.read(treeBuffer) != numberOfHuffmanCodesBytes) {
                throw new IllegalArgumentException("Invalid file format: EOF reading Huffman tree");
            }
            // getting the tree representation
            StringBuilder huffTree = new StringBuilder();
            for (int i = 0; i < treeBuffer.length; i++) {
                huffTree.append(byteToBinaryString(treeBuffer[i]));
            }
            // set the tree representation to the actual length
            header += "Tree Represintation = " + huffTree.toString() + "\n" ;
            huffTree.setLength(treeLength);

            // Build Huffman tree
            BinaryTreeNode root = bulidHuffTree(huffTree.toString());
            BinaryTreeNode.findHuffCodesFromPath(root);
            if (root.getLeft() == null && root.getRight() == null) { // only one node in the tree, the whole file is the same byte
                root.setHuffCode("1");
            }

            // Create output file
            String outputFileName = inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.')) +"_Decompressed" + "." + fileExt;
            System.out.println("The output file is = " + outputFileName);

            // start writing the decompressed file
            try (FileOutputStream out = new FileOutputStream(outputFileName)) {
                byte[] outBuffer = new byte[BUFFER_SIZE];
                int outBufferPos = 0;
                BinaryTreeNode current = root;
                // Read all remaining data
                byte[] compressedData = in.readAllBytes();
                if (compressedData.length == 0) {
                    throw new IllegalArgumentException("Invalid file format: No compressed data");
                }
                
                // Get number of valid bits from last byte
                int validBitsInLastByte = compressedData[compressedData.length - 1] & 0xFF;
                int processBytes = compressedData.length - 1; // Exclude last byte, it's not data
                
                // Process all complete bytes
                for (int byteIndex = 0; byteIndex<processBytes; byteIndex++) {
                    int currentByte = compressedData[byteIndex] & 0xFF;
                    int bitsToProcess = 8;
                    
                    // For the last data byte, use the number of valid bits
                    if (byteIndex == processBytes - 1) { // last byte of data
                    	if(validBitsInLastByte == 0) {
                    		bitsToProcess = 0;
                    	}else {
                    		bitsToProcess = 8 - validBitsInLastByte;
                    	}
                    }
                    String compressedByte = byteToBinaryString((byte) currentByte); 
                    for (int bit = 0; bit < bitsToProcess; bit++) {
                        // search for the data in huffman tree based on the huffman code
                        if (compressedByte.charAt(bit) == '0') {
                            current = current.getLeft();
                        } else {
                            current = current.getRight();
                        }
                        
                        // If we reached a leaf, output the byte
                        if (current.isLeaf()) {
                            outBuffer[outBufferPos++] = current.getData();
                            frequancies[current.getData() & 0xFF] = current; // record the byte , for the huff table printing for the user
                            // if the buffer gets full then write on the output file
                            if (outBufferPos == BUFFER_SIZE) {
                            	out.write(outBuffer, 0, outBufferPos);
                                outBufferPos = 0;
                            }
                            // we have find a matching huffman code so reset for the next 
                            current = root;
                        }
                    }
                }
                
                // Write any remaining bytes
                if (outBufferPos > 0) {
                	out.write(outBuffer, 0, outBufferPos);
                }
            }

            File newFile = new File(outputFileName);
            this.decompressedFile = newFile;
            this.lengthOfFileAfterDecompression = newFile.length();
            System.out.println("File size after decomp = " + newFile.length());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BinaryTreeNode bulidHuffTree(String huffTree) {
        BinaryTreeNode[] nodes = new BinaryTreeNode[256];
        int nodeCount = 0;
        int i = 0;
        
        while (i < huffTree.length()) {
            char isLeaf = huffTree.charAt(i);
            if (isLeaf == '0') { // leaf node 
                if (i + 8 >= huffTree.length()) {
                    throw new IllegalArgumentException("Invalid format, not enough bits for a byte");
                }
                String byteReaded = huffTree.substring(i + 1, i + 9); // read the next 8 bits after the 0
                byte data = (byte) Integer.parseInt(byteReaded, 2); // those 8 bits is the data this leaf node represent
                BinaryTreeNode leaf = new BinaryTreeNode(data, 0); // we don't need the frequency
                nodes[nodeCount++] = leaf;
                i += 9; // shift 8 bits to the next node
            } else if (isLeaf == '1') { // internal node
                if (nodeCount < 2) {
                    throw new IllegalArgumentException("There are not enough nodes");
                }
                BinaryTreeNode right = nodes[--nodeCount];
                BinaryTreeNode left = nodes[--nodeCount];
                BinaryTreeNode internal = new BinaryTreeNode(left, right);
                nodes[nodeCount++] = internal;
                i++;
            } else {
                throw new IllegalArgumentException("Invalid format in Huffman tree");
            }
        }
        // should be one node left which is the root
        if (nodeCount != 1) {
            throw new IllegalArgumentException("Invalid Huffman tree structure");
        }
        return nodes[0];
    }

    // convert a byte to it's 8 bit binary representation 
    public static String byteToBinaryString(byte b) {
        StringBuilder binaryString = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            int bit = (b >> i) & 1;
            binaryString.append(bit);
        }
        return binaryString.toString();
    }

    // Setters and Getters
    public BinaryTreeNode[] getFrequancies() {
        return frequancies;
    }

    public void setFrequancies(BinaryTreeNode[] frequancies) {
        this.frequancies = frequancies;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public long getLengthOfCompressedFile() {
        return lengthOfCompressedFile;
    }

    public void setLengthOfCompressedFile(long lengthOfCompressedFile) {
        this.lengthOfCompressedFile = lengthOfCompressedFile;
    }

    public long getLengthOfFileAfterDecompression() {
        return lengthOfFileAfterDecompression;
    }

    public void setLengthOfFileAfterDecompression(long lengthOfFileAfterDecompression) {
        this.lengthOfFileAfterDecompression = lengthOfFileAfterDecompression;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getOrginalFileName() {
        return orginalFileName;
    }

    public void setOrginalFileName(String orginalFileName) {
        this.orginalFileName = orginalFileName;
    }

	public File getDecompressedFile() {
		return decompressedFile;
	}

	public void setDecompressedFile(File decompressedFile) {
		this.decompressedFile = decompressedFile;
	}
	
	
}
