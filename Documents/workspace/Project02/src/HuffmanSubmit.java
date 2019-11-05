import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
/**
 * 
 * @author sarahatterbury
 * CSC172
 * Project 02
 *
 */
// Import any package as required

class HuffmanNode{
	Integer freq;
	Character key;
	HuffmanNode left;
	HuffmanNode right;
	/**
	 * Constructor used for leaf nodes. Only holds frequency and character, does not have children
	 * @param keyVal the character being encoded (ascii binary representation)
	 * @param frequency the amount of times the character occurs in the input file
	 */
	public HuffmanNode(Character keyVal, Integer frequency){//leaf node
		freq = frequency;
		key = keyVal;
	}
	/**
	 * Constructor for internal nodes. Though it holds the possibility for a character value, it is always passed null for that value if this constructor is called. Has left and right children which can either be an internal node or a leaf node
	 * @param keyVal character being encoded, always null in this case
	 * @param frequency the amount of times the character occurs in the input file
	 * @param leftChild left child of the current node
	 * @param rightChild right child of the current node
	 */
	public HuffmanNode(Character keyVal, Integer frequency, HuffmanNode leftChild, HuffmanNode rightChild){//internal node
		freq = frequency;
		key = keyVal;
		left = leftChild;
		right = rightChild;
	}
	/**
	 * Checks whether the current node is a leaf node or an internal node by checking for null children below it.
	 * @return true when the node has no children, false when it has children
	 */
	public boolean isLeaf(){ 
		return (left == null) && (right == null); 
	}
}

class HuffmanComparator implements Comparator<HuffmanNode>{
	/**
	 * Comparator used in the priority queue construction to account for the hashmap being arranged in <Character, Integer> order, tells the priority queue to order itself in ascending order based on the Integer values rather than the characters as would be the default.
	 * @param node1 current node
	 * @param node2 node to be added
	 */
	@Override
	public int compare(HuffmanNode node1, HuffmanNode node2) {
		if(node1.freq > node2.freq){
			return -1;  
		}else if (node1.freq == node2.freq){
			return 0;
		}else{
			return 1;
		}
	}

}

	
public class HuffmanSubmit implements Huffman {
		
	HashMap<Character, Integer> freq = new HashMap<>();
	HashMap<Character, String> codes = new HashMap<>();
		// Feel free to add more methods and variables as required. 
	/**
	 * Calls each of the helper functions to create a frequency table, a priority queue, huffman tree, and the subsequent encodings. Then,
	 * using the codes hashmap created in createCode, each character of the input file is read and the appropriate true or false binary value is written to our encoded file
	 * based on the corresponding element in the coded String.
	 *@param inputFile the file to be encoded
	 *@param outputFile the file where the encoding will be written
	 *@param freqFile the name of the frequency file we will create in frequency()
	 */
	public void encode(String inputFile, String outputFile, String freqFile){
		try {
			frequency(inputFile, freqFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PriorityQueue<HuffmanNode> queue = makePriorityQueue();
		HuffmanNode root = queue.peek();
		createCode(root, "");
		printTree(root);
		BinaryIn bIn = new BinaryIn(inputFile);
		BinaryOut out = new BinaryOut(outputFile);
		while(!bIn.isEmpty()){
			   char c = bIn.readChar();
			   String coded = codes.get(c);
			   char[] input = coded.toCharArray();
			   for(char i : input){
				   if(i =='0'){
					   out.write(true);
				   }else if(i == '1'){
					   out.write(false);
				   }
			   }
		}
		out.flush();
	}
	/**
	 * Method to check the structure of the tree, only prints when the node is a leaf to avoid errors though this obscures the tree structure slightly
	 * @param root current pointer, points to the root of the tree at first then is moved recursively through
	 */
	private void printTree(HuffmanNode root) {
		if(root.isLeaf()){
			System.out.println(root.key + ":" + root.freq);
		}
		if(root.left != null)
			printTree(root.left);
			
		if(root.right != null)
			printTree(root.right);
			
	}
	/**
	 * The method used to actually build the encoded values for each character. Uses the tree structure and adds a one or a zero to the final string based on the direction the pointer can move in the tree. Adds this value to a hashmap along with the character it corresponds to 
	 * @param root pointer for the current location in the tree. starts at the root and is moved through depending on tree structure
	 * @param binary the final binary encoding for the character
	 */
	private void createCode(HuffmanNode root, String binary) {
		if(root.isLeaf()){
			codes.put(root.key, binary);//if the current is a leaf, add the binary string as an encoding
		}else{
			createCode(root.left, binary + "0");//if can go left, add 0 to encoding
			createCode(root.right, binary + "1");//if can go right, add 1 to encoding
		}
	}

	/**
	 * Makes an empty priority queue of huffman nodes using the previously defined comparator. Each entry in our frequency hashmap is made into a leaf node and added to the priority queue. 
	 * Then, the smallest two values from the queue are summed and a new node is created holding their summed frequency. This new node is initialized with no character, and the two smallest nodes become 
	 * its children. This is repeated until the huffman tree is fully created for our inputs.
	 * @return priorityqueue created by this method is returned for use in encoding and decoding
	 */
	private PriorityQueue<HuffmanNode> makePriorityQueue() {
		PriorityQueue<HuffmanNode> huffQueue = new PriorityQueue<>(1, new HuffmanComparator());
		for(HashMap.Entry<Character, Integer> entry : freq.entrySet()){
			HuffmanNode huff = new HuffmanNode(entry.getKey(), entry.getValue());
			huffQueue.offer(huff);
		}
		while(huffQueue.size() > 1){
			HuffmanNode node1 = huffQueue.poll();
			HuffmanNode node2 = huffQueue.poll();
			
			int newFreq = node1.freq + node2.freq;
			HuffmanNode newNode = new HuffmanNode(null, newFreq, node1, node2);
			huffQueue.offer(newNode);
			
		}
		return huffQueue;
	}
	/**
	 * Builds the frequency file. Creates a hashmap of frequencies based on the amount of times the character occurs in the file. Writes this hashmap to the frequency file name specified in the call. 
	 * @param inputFile The original uncoded file to be read
	 * @param freqFile the name of the file where the frequencies will be written
	 * @throws IOException from writing to a file
	 */
	private void frequency(String inputFile, String freqFile) throws IOException {
		   BinaryIn bIn = new BinaryIn(inputFile);
		   while(!bIn.isEmpty()){
			   char c = bIn.readChar(); 
			   if(freq.containsKey(c)){
				   freq.replace(c, freq.get(c)+1);
			   }else{
				   freq.put(c, 1);
			   }
		   }
		   BufferedWriter freqF = new BufferedWriter(new FileWriter(freqFile));
		   freqF.flush();
		   for (HashMap.Entry<Character, Integer> entry : freq.entrySet()) {
			   String keyValue = "";
			   keyValue = Integer.toBinaryString(entry.getKey());
			   freqF.write(keyValue + ":" + entry.getValue());
			   freqF.newLine();
			}
		   freqF.close();
			
		}

	/**
	 * Reads from the given frequency file to create a new frequency hashmap. Then, it builds another huffman tree and discerns the encoding keys based on the frequency file.
	 * Writes to the binary output file based on both the tree and the encoded input file. Based on the boolean values read from the encoded file, the pointer moves left or right 
	 * through the tree. The output file is written to when the tree reaches a leaf.
	 *@param inputFile the encoded file to be decoded
	 *@param outputFile the file where the decoding will be written
	 *@param freqFile the name of the frequency file we will read to determine frequencies
	 */
	public void decode(String inputFile, String outputFile, String freqFile){
		BinaryIn bIn = new BinaryIn(inputFile);//encoded
		BinaryOut out = new BinaryOut(outputFile);//decoded
		Scanner scanner = null;
		HashMap<Character, Integer> temporaryMap = new HashMap<>();
		try {
			scanner = new Scanner(new File(freqFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(scanner.hasNext()){
			String string = scanner.nextLine();
			String[] temp = string.split(":"); 
			temporaryMap.put((char) Integer.parseInt(temp[0], 2), Integer.parseInt(temp[1]));
		}
		scanner.close();
		freq = temporaryMap;
		PriorityQueue<HuffmanNode> queue = makePriorityQueue();
		HuffmanNode root = queue.peek();
		createCode(root, "");
		printTree(root);
		while(!bIn.isEmpty()){
			boolean bool = bIn.readBoolean();
			if(root.isLeaf()){
				out.write(root.key);
				root = queue.peek();
			}
			if(bool){//if it's true(0), go left; covered concept in workshop
				root = root.left;
			}else{//if it's false(1), go right
				root = root.right;
			}
		}
		out.flush();
		out.close();
	}
	
	
	public static void main(String[] args) {
	   Huffman  huffman = new HuffmanSubmit();
	   huffman.encode("alice30.txt", "alice30.enc", "freq.txt");
	   huffman.decode("alice30.enc", "alice30_dec.txt", "freq.txt");
	   //huffman.encode("ur.jpg", "ur.enc", "freq.txt");
	  // huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
		// After decoding, both ur.jpg and ur_dec.jpg should be the same. 
		// On linux and mac, you can use `diff' command to check if they are the same. 
	}
	
}
//how to iterate through a hashmap from here: https://javatutorial.net/java-iterate-hashmap-example
//parseInt from base 2 to base 10 from here: https://docs.oracle.com/javase/7/docs/api/java/lang/Integer.html#parseInt(java.lang.String,%20int)

