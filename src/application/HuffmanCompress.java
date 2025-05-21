package application;

import java.io.*;
import java.lang.foreign.PaddingLayout;

public class HuffmanCompress {
    private BinaryTreeNode[] frequancies;
    private File inputFile;
    private File compressedFile;
    private String header = "";
    private HuffmanCodes huff;
    private static final int BUFFER_SIZE = 8192;
    private long lengthOfFile;
    private long lengthOfFileAfterCompression;
    private String[] huffCodes;
    private int headerSize;
    private String originalFileName;
    private String originalFileExtinsion;
    
    public HuffmanCompress(File input) {
    	System.out.println("COMPRESS STARTED-------------------------------------------------");
        this.inputFile = input;
        this.lengthOfFile = input.length();
        System.out.println("The file size before comp = " + lengthOfFile);
        
        try {
            // Get file extension
        	String fileName = input.getName();
            String fileExtension = "";
            int i = fileName.lastIndexOf('.');// to handle if there is multiple '.' in the file name
            if (i > 0) {
                fileExtension = fileName.substring(i + 1);
            }
            byte[] extensionBytes = fileExtension.getBytes();// file extension buffer
            
            // Build frequency table
            frequancies = new BinaryTreeNode[256];
            for (int j = 0; j < 256; j++) {
                frequancies[j] = new BinaryTreeNode((byte) j, 0);
            }
            
            // Count frequencies using buffered read
            try (FileInputStream in = new FileInputStream(input)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    for (int j = 0; j < bytesRead; j++) {
                        int index = buffer[j] & 0xFF; // to handle the unsigned 
                        frequancies[index].incFrequancy();
                    }
                }
            }
            // to check if the file is empty
            int sum =0;
            for(BinaryTreeNode n : frequancies) {
            	sum += n.getFrequancy();
            }
            if(sum == 0) {
            	frequancies[0].incFrequancy(); // just to make the huffman tree gets constructed
            }
            // Create Huffman codes
            huff = new HuffmanCodes(frequancies);
            huffCodes = huff.getCodes();
            
            // Create compressed file
            String compressedFileName = fileName.substring(0, i) + ".huf";
            try (FileOutputStream out = new FileOutputStream(compressedFileName)) {
                
                // Write extension length and extension
            	out.write(extensionBytes.length); // 1 byte
            	out.write(extensionBytes);// # of bytes as the length
                header += "Extinsion Lenght = " +byteToBinaryString((byte)extensionBytes.length) +"\n" + "Extinsion = ";
                for(byte b: extensionBytes) {
                	header += byteToBinaryString(b);
                }
                // Generate and write Huffman tree representation
                String treeRepresentation = huff.postOrderRepresintationfHuffCodes();
                
                // Write tree length (4 bytes) , big-endian format
                // >> : right shift to get the most significant bits every time
                // 0xFF : to ensure only 8 bits is kept for the write 
                header += "\n" +"Tree Length = ";
                int treeLength = treeRepresentation.length();
                byte b1 = (byte)((treeLength >> 24) & 0xFF);
                out.write(b1);
                header += byteToBinaryString(b1) + " ";
                byte b2 = (byte)((treeLength >> 16) & 0xFF);
                out.write(b2);
                header += byteToBinaryString(b2) + " ";
                byte b3 = (byte)((treeLength >> 8) & 0xFF);
                out.write(b3);
                header += byteToBinaryString(b3) + " ";
                byte b4 = (byte)(treeLength & 0xFF);
                out.write(b4);
                header += byteToBinaryString(b4);
                
                header += "\n";
                header += "Huffman Tree = " + treeRepresentation;
                // just to add the padding bits to the header that will be printed for the interface
                int pading = (8-(treeLength % 8)) % 8;
                for(int p=0; p< pading; p++) {
                	header += "0";
                }
                // Write tree representation
                int treeBytesLength; // bytes to be written on the header so they represent the huffman tree
                if(treeLength % 8 == 0) { // dont need padding
                	treeBytesLength = treeLength / 8;
                }else {// needs padding
                	treeBytesLength = treeLength / 8 + 1;
                }
                // buffer for the tree representation
                byte[] treeBytes = new byte[treeBytesLength];
                for (int j = 0; j < treeLength; j += 8) { // reads 1 byte each time from the tree representation 
                    int end = Math.min(j + 8, treeLength); // to handle if the last byte to read is not full 8 bits
                    String bits = treeRepresentation.substring(j, end);
                    bits = (bits + "00000000").substring(0, 8); // 
                    treeBytes[j / 8] = (byte) Integer.parseInt(bits, 2);// storing the readed byte in it's index
                }
                out.write(treeBytes);
                
                // Write compressed data
                try (FileInputStream in = new FileInputStream(input)) {
                    byte[] inBuffer = new byte[BUFFER_SIZE];
                    byte[] outBuffer = new byte[BUFFER_SIZE];
                    int outBufferPos = 0; // track how much in the outBuffer
                    int currentByte = 0; // byte to write
                    int bitsInCurrentByte = 0;// to count until we got 8 bits to write
                    int bytesRead; // number of bytes readed from input file , in inBuffer
                    
                    while ((bytesRead = in.read(inBuffer)) != -1) {
                        for (int j = 0; j < bytesRead; j++) {
                            String huffCode = huffCodes[inBuffer[j] & 0xFF];//0xFF : to treat the unsigned 
                            for(int k=0; k<huffCode.length(); k++) {
                            	char bit = huffCode.charAt(k);
                                currentByte = currentByte << 1; // to make space for the new bit
                                if(bit == '1') {
                                	currentByte = currentByte | 1;
                                }else {
                                	currentByte = currentByte | 0;
                                }
                                bitsInCurrentByte++;
                                if (bitsInCurrentByte == 8) { // if we have make a byte we add it to the buffer
                                    outBuffer[outBufferPos++] = (byte) currentByte;
                                    if (outBufferPos == BUFFER_SIZE) { // if the buffer gets full then write
                                        out.write(outBuffer, 0, outBufferPos);
                                        outBufferPos = 0;
                                    }
                                    // reset for new byte
                                    currentByte = 0;
                                    bitsInCurrentByte = 0;
                                }
                            }
                        }
                    }
                    
                    // Handle remaining bits
                    if (bitsInCurrentByte > 0) {
                        currentByte <<= (8 - bitsInCurrentByte); // to pad the last byte with zeros if it's not a full 8 bits
                        outBuffer[outBufferPos++] = (byte) currentByte;
                    }
                    
                    // Write remaining buffer
                    if (outBufferPos > 0) {
                        out.write(outBuffer, 0, outBufferPos);
                    }
                    
                    // Write a final byte in the compressed file tills how many valid bits in the last byte of compressed data
                    if(bitsInCurrentByte == 0) {
                    	out.write(0);
                    }else {
                    	out.write(8 - bitsInCurrentByte);
                    }
                }
            }
            // to record the size after compression
            File newFile = new File(compressedFileName);
            this.compressedFile = newFile;
            this.lengthOfFileAfterCompression = newFile.length();
            System.out.println("File size after comp = " + this.lengthOfFileAfterCompression);
            System.out.println(header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public static String byteToBinaryString(byte b) {
	    StringBuilder binaryString = new StringBuilder();
	    for (int i = 7; i >= 0; i--) {
	        int bit = (b >> i) & 1;
	        binaryString.append(bit);
	    }
	    return binaryString.toString();
	}
    
    // Getters and setters 
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

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getHeaderSize() {
		return headerSize;
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getOriginalFileExtinsion() {
		return originalFileExtinsion;
	}

	public void setOriginalFileExtinsion(String originalFileExtinsion) {
		this.originalFileExtinsion = originalFileExtinsion;
	}

	public String[] getHuffCodes() {
		return huffCodes;
	}

	public void setHuffCodes(String[] huffCodes) {
		this.huffCodes = huffCodes;
	}

	public File getCompressedFile() {
		return compressedFile;
	}

	public void setCompressedFile(File compressedFile) {
		this.compressedFile = compressedFile;
	}

	public HuffmanCodes getHuff() {
		return huff;
	}

	public void setHuff(HuffmanCodes huff) {
		this.huff = huff;
	}

	public long getLengthOfFile() {
		return lengthOfFile;
	}

	public void setLengthOfFile(long lengthOfFile) {
		this.lengthOfFile = lengthOfFile;
	}

	public long getLengthOfFileAfterCompression() {
		return lengthOfFileAfterCompression;
	}

	public void setLengthOfFileAfterCompression(long lengthOfFileAfterCompression) {
		this.lengthOfFileAfterCompression = lengthOfFileAfterCompression;
	}
	
	
	
}
