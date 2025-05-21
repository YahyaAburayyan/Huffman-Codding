# 📦 Huffman Coding Project – File Compression & Decompression

## 📘 Overview

This project implements **Huffman Coding**, a lossless data compression algorithm. The core idea is to assign **shorter codes to more frequent bytes** and **longer codes to less frequent ones**, reducing overall file size without losing any data.

Your implementation includes:
- Frequency analysis of input data
- Construction of a Huffman Tree using a custom **priority queue**
- Generation of **prefix-free binary codes**
- File compression and decompression using your custom binary encoding
- A verification step to ensure **the uncompressed output matches the original**

---

## 🎯 Objectives

- Compress any file using the **Huffman coding algorithm**
- Store the compressed result in a binary file
- Uncompress the binary file to retrieve the original file
- Display Huffman encoding table and header information

---

## 🛠️ Features

✅ Reads any file type (text, binary, image, etc.)  
✅ Constructs a binary Huffman Tree  
✅ Implements a Priority Queue (Min-Heap) for tree building  
✅ Encodes and writes compressed data to a `.huff` file  
✅ Decodes the `.huff` file to restore the original  
✅ Verifies that the original and decompressed file are identical  
✅ Displays Huffman Code Table and Binary Header  

---

## 🧠 How Huffman Coding Works

1. **Frequency Count**: Count how many times each byte occurs.
2. **Tree Construction**: Build a **binary tree** from the lowest frequency characters upward using a priority queue.
3. **Code Assignment**: Generate **unique prefix codes** from the tree (e.g., `A → 10`, `B → 011`).
4. **Encoding**: Convert original bytes into a compressed bitstream.
5. **Decoding**: Use the tree to decode the compressed file back to its original form.

---
## User Interface
### Welcom Page
![Screenshot 2025-05-21 222007](https://github.com/user-attachments/assets/32b24715-4978-4b45-9abc-67a66fd18509)

---
### Choose opperation Page
![Screenshot 2025-05-21 222023](https://github.com/user-attachments/assets/c745b01e-9157-4821-b7c1-df4fa3d21a7b)

---
### After Choosing Compress 
![Screenshot 2025-05-21 222032](https://github.com/user-attachments/assets/7925aa0d-71dd-4eac-b6d3-fc463e370d91)

---
### Show Huffman Codes table for each byte in the input file
![Screenshot 2025-05-21 222042](https://github.com/user-attachments/assets/d7b1e53d-441a-4acb-a8ae-25109958f7df)

---
### Show the Header for the Compressed file 
![Screenshot 2025-05-21 222049](https://github.com/user-attachments/assets/87348bd7-01db-46db-9902-7c09656cbd0e)

---

## Same pages for the Decompress Opperation

