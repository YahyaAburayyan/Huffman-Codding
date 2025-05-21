package application;
	
import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;


public class Main extends Application {
	private File choosedFile;
	private HuffmanDecompress decompress;
	private HuffmanCompress compress;
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			root.setStyle("-fx-background-color: #1b232a;");
			welcomeScreen(root);
			Scene scene = new Scene(root,800,800);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// welcome screen
	private void welcomeScreen(BorderPane root) {
	    VBox vbox = new VBox(20);
	    vbox.setAlignment(Pos.CENTER);
	    vbox.setPadding(new Insets(40));

	    Label welcome = new Label("Welcome to Huffman Compression & Decompression");
	    welcome.setStyle("-fx-text-fill: #f94000");
	    welcome.setFont(Font.font("Arial", FontWeight.BOLD, 26));
	    welcome.setTextAlignment(TextAlignment.CENTER);

	    Button selectFileBtn = createButton("Select File");
	    selectFileBtn.setFont(Font.font(16));

	    // File chooser logic
	    selectFileBtn.setOnAction(e -> {
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Select a File to Compress/Decompress");

	        // Optional: set extension filters (for specific file types)
	        fileChooser.getExtensionFilters().addAll(
	            new FileChooser.ExtensionFilter("All Files", "*.*")
	        );

	        File selectedFile = fileChooser.showOpenDialog(null);
	        if (selectedFile != null) {
	            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
	            // Proceed with compression or decompression logic here
	            choosedFile = selectedFile;
	            chooseOpperation(root, selectedFile);
	        }
	    });

	    vbox.getChildren().addAll(welcome, selectFileBtn);
	    root.setTop(null);
	    root.setCenter(vbox);
	}

	// after selecting a file choose compress or decompress 
	private void chooseOpperation(BorderPane root, File selectedFile) {
	    // Top label
	    Label label = new Label("Choose Operation to perform on the selected file");
	    label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    label.setTextFill(javafx.scene.paint.Color.web("#f94000"));
	    label.setPadding(new Insets(20));
	    BorderPane.setAlignment(label, Pos.CENTER);
	    
	    Label fileNameLabel = new Label("Selected File: " + selectedFile.getName());
	    fileNameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
	    fileNameLabel.setTextFill(javafx.scene.paint.Color.web("#f0f0f0"));
	    fileNameLabel.setPadding(new Insets(0, 0, 20, 0));
	    
	    VBox topBox = new VBox(label, fileNameLabel);
	    topBox.setAlignment(Pos.CENTER);
	    root.setTop(topBox);
	    
	    Image compImage = new Image("images/compress.png");
	    Image decompImage = new Image("images/decompress.png");

	    ImageView compImg = new ImageView(compImage);
	    ImageView decompImg = new ImageView(decompImage);


	    // Set size for images
	    compImg.setFitWidth(300);
	    compImg.setFitHeight(300);
	    decompImg.setFitWidth(300);
	    decompImg.setFitHeight(300);

	    // Set hover cursor and style
	    compImg.setStyle("-fx-cursor: hand;");
	    decompImg.setStyle("-fx-cursor: hand;");
	    
	    // Hover effects for compImg
	    compImg.setOnMouseEntered(e -> compImg.setScaleX(1.1));
	    compImg.setOnMouseEntered(e -> compImg.setScaleY(1.1));
	    compImg.setOnMouseExited(e -> compImg.setScaleX(1.0));
	    compImg.setOnMouseExited(e -> compImg.setScaleY(1.0));

	    // Hover effects for decompImg
	    decompImg.setOnMouseEntered(e -> decompImg.setScaleX(1.1));
	    decompImg.setOnMouseEntered(e -> decompImg.setScaleY(1.1));
	    decompImg.setOnMouseExited(e -> decompImg.setScaleX(1.0));
	    decompImg.setOnMouseExited(e -> decompImg.setScaleY(1.0));


	    // Click handlers
	    compImg.setOnMouseClicked(e -> {
	        System.out.println("Compressing: " + selectedFile.getName());
	        try {
	        	this.compress = new HuffmanCompress(selectedFile); 
	        	showCompressionDetails(root);
	        }catch (Exception ex) {
	        	Alert alert = new Alert(Alert.AlertType.ERROR);
	            alert.setTitle("Invalid File");
	            alert.setHeaderText("Cannot Decompress File");
	            alert.setContentText("The selected file has error.");
	            alert.show();
	            welcomeScreen(root);
			}
	        
	    });

	    decompImg.setOnMouseClicked(e -> {
	        System.out.println("Decompressing: " + selectedFile.getName());
	        // Call your decompression method here, e.g.:
	        try {
	        	this.decompress = new HuffmanDecompress(selectedFile);
	        	showDecompressionDetails(root);
	        }catch (Exception ex) {
	        	Alert alert = new Alert(Alert.AlertType.ERROR);
	            alert.setTitle("Invalid File");
	            alert.setHeaderText("Cannot Decompress File");
	            alert.setContentText("The selected file is not in a valid Huffman format.");
	            alert.show();
	            welcomeScreen(root);
			}
	        
	    });

	    // Place images side-by-side
	    HBox imageBox = new HBox(60); // spacing between images
	    imageBox.setAlignment(Pos.CENTER);
	    imageBox.setPadding(new Insets(40));
	    imageBox.getChildren().addAll(compImg, decompImg);

	    // Set center content
	    root.setCenter(imageBox);
	}

	// after selecting compression page
	private void showCompressionDetails(BorderPane root) {
	    root.setTop(null); // Clear top content

	    VBox detailsBox = new VBox(20);
	    detailsBox.setPadding(new Insets(30));
	    detailsBox.setAlignment(Pos.CENTER);

	    Label title = new Label("Your file was compressed successfully. Choose what to do:");
	    title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    title.setTextFill(javafx.scene.paint.Color.web("#f94000"));
	    
	    Label fileNameLabel = new Label("Compressed File: " + compress.getCompressedFile().getName());
	    fileNameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
	    fileNameLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);

	    // File sizes
	    long originalSize = compress.getLengthOfFile();// You should have stored this
	    long compressedSize = compress.getLengthOfFileAfterCompression();
	    
	    Label originalSizeLabel = new Label("Original Size: " + originalSize);
	    originalSizeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
	    originalSizeLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);

	    Label compressedSizeLabel = new Label("Compressed Size: " + compressedSize);
	    compressedSizeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
	    compressedSizeLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
	    
	    Button showHuffCodesBtn = createButton("Huffman Codes");
	    Button showHeaderBtn = createButton("Show Header");
	    Button backBtn = createButton("Back");

	    showHuffCodesBtn.setOnAction(e -> showHuffmanCodesTableForCompression(root, compress.getHuff().getFrequancies()));
	    showHeaderBtn.setOnAction(e -> showHeaderDetailsForCompression(root, compress.getHeader()));  // Assume getHeader() returns a String
	    backBtn.setOnAction(e -> {
	        choosedFile = null;
	        compress = null;
	        decompress = null;
	        welcomeScreen(root);
	    });

	    detailsBox.getChildren().addAll(title, fileNameLabel, originalSizeLabel, compressedSizeLabel,showHuffCodesBtn, showHeaderBtn, backBtn);
	    root.setCenter(detailsBox);
	}
	
	// huffman table for compression
	private void showHuffmanCodesTableForCompression(BorderPane root, BinaryTreeNode[] codes) {
	    VBox vbox = new VBox(20);
	    vbox.setPadding(new Insets(30));
	    vbox.setAlignment(Pos.CENTER);

	    Label title = new Label("Huffman Codes Table");
	    title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    title.setTextFill(javafx.scene.paint.Color.web("#f94000"));

	    TableView<BinaryTreeNode> table = new TableView<>();
	    table.setPrefWidth(250); // smaller width

	    TableColumn<BinaryTreeNode, String> charCol = new TableColumn<>("Byte");
	    TableColumn<BinaryTreeNode, String> codeCol = new TableColumn<>("Code");

	    charCol.setCellValueFactory(data -> {
	        Byte b = data.getValue().getData();
	        int unsigned = b & 0xFF;
	        String value;

	        if (unsigned >= 32 && unsigned <= 126) {
	            value =  (char) unsigned +"";
	        } else if (unsigned == 10) {
	            value = "\\n";
	        } else if (unsigned == 13) {
	            value = "\\r";
	        } else if (unsigned == 9) {
	            value = "\\t";
	        } else {
	            value = "byte : " + unsigned;
	        }

	        return new javafx.beans.property.SimpleStringProperty(value);
	    });

	    codeCol.setCellValueFactory(data -> 
	        new javafx.beans.property.SimpleStringProperty(data.getValue().getHuffCode())
	    );

	    // Set column widths and cell styling
	    charCol.setPrefWidth(100);
	    codeCol.setPrefWidth(150);

	    charCol.setStyle("-fx-font-size: 16px; -fx-alignment: CENTER;");
	    codeCol.setStyle("-fx-font-size: 16px; -fx-alignment: CENTER;");

	    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	    table.setFixedCellSize(30); // Larger cell height
	    table.setPrefHeight(400); // Adjust based on number of entries

	    table.getColumns().addAll(charCol, codeCol);

	    for (BinaryTreeNode node : codes) {
	        if (node != null && node.getFrequancy() > 0 && node.getData() != null) {
	            table.getItems().add(node);
	        }
	    }

	    Button backBtn = createButton("Back");
	    backBtn.setOnAction(e -> showCompressionDetails(root));

	    vbox.getChildren().addAll(title, table, backBtn);
	    root.setTop(null);
	    root.setCenter(vbox);
	}
	
		// show the header for the compression
		private void showHeaderDetailsForCompression(BorderPane root, String header) {
		    VBox vbox = new VBox(20);
		    vbox.setPadding(new Insets(30));
		    vbox.setAlignment(Pos.CENTER_LEFT);

		    Label title = new Label("Header Information");
		    title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		    title.setTextFill(javafx.scene.paint.Color.web("#f94000"));

		    Label headerLabel = new Label(header);
		    headerLabel.setFont(Font.font("Courier New", 14));
		    headerLabel.setWrapText(true);
		    headerLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);

		    Button backBtn = createButton("Back");
		    backBtn.setOnAction(e -> showCompressionDetails(root));

		    vbox.getChildren().addAll(title, headerLabel, backBtn);
		    root.setTop(null);
		    root.setCenter(vbox);
		}
	
	// after choosing decompression page 
	private void showDecompressionDetails(BorderPane root) {
	    root.setTop(null);

	    VBox detailsBox = new VBox(20);
	    detailsBox.setPadding(new Insets(30));
	    detailsBox.setAlignment(Pos.CENTER);

	    Label title = new Label("Your file was decompressed successfully. Choose what to do:");
	    title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    title.setTextFill(javafx.scene.paint.Color.web("#f94000"));

	    Label fileNameLabel = new Label("Decompressed File: " + decompress.getDecompressedFile().getName());
	    fileNameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
	    fileNameLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
	    
	    // File sizes
	    long compressedSize = decompress.getLengthOfCompressedFile();// Original compressed file
	    long decompressedSize = decompress.getLengthOfFileAfterDecompression(); // Result after decompression
	    
	    Label compressedSizeLabel = new Label("Compressed Size: " + compressedSize);
	    compressedSizeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
	    compressedSizeLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);

	    Label decompressedSizeLabel = new Label("Decompressed Size: " + decompressedSize);
	    decompressedSizeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
	    decompressedSizeLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);

	    Button showHuffCodesBtn = createButton("Huffman Codes");
	    Button showHeaderBtn = createButton("Show Header");
	    Button backBtn = createButton("Back");

	    showHuffCodesBtn.setOnAction(e -> showHuffmanCodesTableForDeCompression(root, decompress.getFrequancies()));
	    showHeaderBtn.setOnAction(e -> showHeaderDetailsForDeCompression(root, decompress.getHeader()));
	    backBtn.setOnAction(e -> {
	        choosedFile = null;
	        compress = null;
	        decompress = null;
	        welcomeScreen(root);
	    });

	    detailsBox.getChildren().addAll(title, fileNameLabel, compressedSizeLabel, decompressedSizeLabel,showHuffCodesBtn, showHeaderBtn, backBtn);
	    root.setCenter(detailsBox);
	}
	
	// huffman table for decompression
	private void showHuffmanCodesTableForDeCompression(BorderPane root, BinaryTreeNode[] codes) {
	    VBox vbox = new VBox(20);
	    vbox.setPadding(new Insets(30));
	    vbox.setAlignment(Pos.CENTER);

	    Label title = new Label("Huffman Codes Table");
	    title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    title.setTextFill(javafx.scene.paint.Color.web("#f94000"));

	    TableView<BinaryTreeNode> table = new TableView<>();
	    table.setPrefWidth(250); // smaller width

	    TableColumn<BinaryTreeNode, String> charCol = new TableColumn<>("Byte");
	    TableColumn<BinaryTreeNode, String> codeCol = new TableColumn<>("Code");

	    charCol.setCellValueFactory(data -> {
	        Byte b = data.getValue().getData();
	        int unsigned = b & 0xFF;
	        String value;

	        if (unsigned >= 32 && unsigned <= 126) {
	            value =  (char) unsigned +"";
	        } else if (unsigned == 10) {
	            value = "\\n";
	        } else if (unsigned == 13) {
	            value = "\\r";
	        } else if (unsigned == 9) {
	            value = "\\t";
	        } else {
	            value = "byte : " + unsigned;
	        }

	        return new javafx.beans.property.SimpleStringProperty(value);
	    });

	    codeCol.setCellValueFactory(data -> 
	        new javafx.beans.property.SimpleStringProperty(data.getValue().getHuffCode())
	    );

	    // Set column widths and cell styling
	    charCol.setPrefWidth(100);
	    codeCol.setPrefWidth(150);

	    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	    charCol.setStyle("-fx-font-size: 16px; -fx-alignment: CENTER;");
	    codeCol.setStyle("-fx-font-size: 16px; -fx-alignment: CENTER;");
	    
	    table.setFixedCellSize(30); // Larger cell height
	    table.setPrefHeight(400); // Adjust based on number of entries

	    table.getColumns().addAll(charCol, codeCol);

	    for (BinaryTreeNode node : codes) {
	        if (node != null && node.getData() != null) {
	            table.getItems().add(node);
	        }
	    }

	    Button backBtn = createButton("Back");
	    backBtn.setOnAction(e -> showDecompressionDetails(root));

	    vbox.getChildren().addAll(title, table, backBtn);
	    root.setTop(null);
	    root.setCenter(vbox);
	}


	
	
	// show the header for the decompression
	private void showHeaderDetailsForDeCompression(BorderPane root, String header) {
	    VBox vbox = new VBox(20);
	    vbox.setPadding(new Insets(30));
	    vbox.setAlignment(Pos.CENTER_LEFT);

	    Label title = new Label("Header Information");
	    title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    title.setTextFill(javafx.scene.paint.Color.web("#f94000"));

	    Label headerLabel = new Label(header);
	    headerLabel.setFont(Font.font("Courier New", 14));
	    headerLabel.setWrapText(true);
	    headerLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);

	    Button backBtn = createButton("Back");
	    backBtn.setOnAction(e -> showDecompressionDetails(root));

	    vbox.getChildren().addAll(title, headerLabel, backBtn);
	    root.setTop(null);
	    root.setCenter(vbox);
	}


	

	
	// initialize a button with given style
    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #f94000;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-padding: 12 24 12 24;" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-radius: 30;" +
                        "-fx-cursor: hand;");
        button.setPrefWidth(220);
        return button;
    }
	
	public static void main(String[] args) {
		launch(args);

	}
}
