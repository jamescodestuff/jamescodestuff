package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which, when used together, perform the entire
 * Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte and NOT
     * as characters of 1 and 0 which take up 8 bits each
     * 
     * @param filename  The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding - 1; i++)
            pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                System.exit(1);
            }

            if (c == '1')
                currentByte += 1 << (7 - byteIndex);
            byteIndex++;

            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }

        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        } catch (Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";

        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();

            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1')
                    return bitString.substring(i + 1);
            }

            return bitString.substring(8);
        } catch (Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /**
     * Reads a given text file character by character, and returns an arraylist of
     * CharFreq objects with frequency > 0, sorted by frequency
     * 
     * @param filename The text file to read from
     * @return Arraylist of CharFreq objects, sorted by frequency
     */
    public static ArrayList<CharFreq> makeSortedList(String filename) {
        StdIn.setFile(filename);
        ArrayList<CharFreq> list = new ArrayList<CharFreq>();
        int[] freq = new int[128];
        int count = 0;
        char x = '-';
        while (StdIn.hasNextChar()) {
            x = StdIn.readChar();
            int str = (int) x;
            freq[str] += 1;
            count++; // check # of characters in the input
        }
        for (int i = 0; i < 128; i++) {
            if (freq[i] != 0) {
                list.add(new CharFreq((char) i, freq[i] / (count * 1.0)));
            }
        }
        if (list.size() == 1) {
            if (x == 127) {
                CharFreq b = new CharFreq((char) (0), 0.0);
                list.add(b);
            }
            CharFreq b = new CharFreq((char) (x + 1), 0.0);
            list.add(b);
        }
        Collections.sort(list);
        return list;
    }

    /**
     * Uses a given sorted arraylist of CharFreq objects to build a huffman coding
     * tree
     * 
     * @param sortedList The arraylist of CharFreq objects to build the tree from
     * @return A TreeNode representing the root of the huffman coding tree
     */
    public static TreeNode makeTree(ArrayList<CharFreq> sortedList) {
        Queue<TreeNode> source = new Queue<TreeNode>();
        Queue<TreeNode> target = new Queue<TreeNode>();
        TreeNode root = new TreeNode();
        TreeNode first; // smaller
        TreeNode second;
        CharFreq sum;
        while (sortedList.size() != 0) {
            source.enqueue(new TreeNode(sortedList.remove(0), null, null));
        }
        first = source.dequeue();
        second = source.dequeue();
        sum = new CharFreq(null, first.getData().getProbOccurrence() + second.getData().getProbOccurrence());
        TreeNode sumNode = new TreeNode(sum, first, second);
        target.enqueue(sumNode);

        while (!source.isEmpty() || target.size() > 1) {

            if (!source.isEmpty()) {
                if (target.size() != 0) {
                    if (source.peek().getData().getProbOccurrence() <= target.peek().getData().getProbOccurrence()) {
                        first = source.dequeue();
                    } else {
                        first = target.dequeue();
                    }
                } else {
                    if (!source.isEmpty()) {
                        first = source.dequeue();
                    }
                }
            } else {
                if (target.size() > 0) {
                    first = target.dequeue();
                }
            }

            if (!source.isEmpty()) {
                if (target.size() != 0) {
                    if (source.peek().getData().getProbOccurrence() <= target.peek().getData().getProbOccurrence()) {
                        second = source.dequeue();
                    } else {
                        second = target.dequeue();
                    }
                } else {
                    if (!source.isEmpty()) {
                        second = source.dequeue();
                    }
                }
            } else {
                if (target.size() > 0) {
                    second = target.dequeue();
                }
            }

            // if (!source.isEmpty() && target.size() > 1
            // && source.peek().getData().getProbOccurrence() <
            // target.peek().getData().getProbOccurrence()) {
            // second = source.dequeue();
            // } else if (!source.isEmpty() && target.size() > 1
            // && source.peek().getData().getProbOccurrence() >
            // target.peek().getData().getProbOccurrence()) {
            // second = target.dequeue();
            // } else if (!source.isEmpty() && target.size() > 1
            // && source.peek().getData().getProbOccurrence() ==
            // target.peek().getData().getProbOccurrence()) {
            // second = source.dequeue();
            // } else {
            // second = target.dequeue();
            // }

            double s = first.getData().getProbOccurrence() + second.getData().getProbOccurrence();
            sum = new CharFreq(null, s);
            sumNode = new TreeNode(sum, first, second);
            target.enqueue(sumNode);
        }
        root = target.peek();
        return root;
    }

    /**
     * Uses a given huffman coding tree to create a string array of size 128, where
     * each index in the array contains that ASCII character's bitstring encoding.
     * Characters not present in the huffman coding tree should have their spots in
     * the array left null
     * 
     * @param root The root of the given huffman coding tree
     * @return Array of strings containing only 1's and 0's representing character
     *         encodings
     */

    public static String[] makeEncodings(TreeNode root) {
        String[] end = new String[128];
        String temp = "";
        helper(root, temp, end);
        return end;
        /* Your code goes here */
        // if (root != null && root.getLeft() == null && root.getRight() == null) {
        // encode[(int) root.getData().getCharacter()] = msg;
        // msg = msg.substring(0, msg.length() - 1);
        // }
        // if (root == null) {
        // msg = msg.substring(0, msg.length() - 1);
        // return encode;
        // }
        // msg += '0';
        // makeEncodings(root.getLeft());
        // msg += '1';
        // makeEncodings(root.getRight());
        // return encode; // Delete this line
    }

    private static void helper(TreeNode root, String text, String[] strary) {
        if (root.getData().getCharacter() != null) {
            strary[(int) root.getData().getCharacter()] = text;
        } else if (root.getData().getCharacter() == null) {
            String leftTemp = text + "0";
            String rightTemp = text + "1";
            helper(root.getLeft(), leftTemp, strary);
            helper(root.getRight(), rightTemp, strary);
        }
    }

    /**
     * Using a given string array of encodings, a given text file, and a file name
     * to encode into, this method makes use of the writeBitString method to write
     * the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodings   The array containing binary string encodings for each
     *                    ASCII character
     * @param textFile    The text file which is to be encoded
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public static void encodeFromArray(String[] encodings, String textFile, String encodedFile) {
        StdIn.setFile(textFile);
        /* Your code goes here */
        String coded = "";
        while (StdIn.hasNextChar()) {
            coded = coded + encodings[(int) StdIn.readChar()];
        }
        writeBitString(encodedFile, coded);
    }

    /**
     * Using a given encoded file name and a huffman coding tree, this method makes
     * use of the readBitString method to convert the file into a bit string, then
     * decodes the bit string using the tree, and writes it to a file.
     * 
     * @param encodedFile The file which contains the encoded text we want to decode
     * @param root        The root of your Huffman Coding tree
     * @param decodedFile The file which you want to decode into
     */
    public static void decode(String encodedFile, TreeNode root, String decodedFile) {
        StdOut.setFile(decodedFile);
        String str = readBitString(encodedFile); // 0s and 1s
        // String temp = "";
        // int count = 0;
        String output = "";
        TreeNode ptr = root;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '0') {
                ptr = ptr.getLeft();
                if (ptr.getLeft() == null && ptr.getRight() == null) {
                    output = output + ptr.getData().getCharacter();
                    ptr = root;
                }
            } else if (str.charAt(i) == '1') {
                ptr = ptr.getRight();
                if (ptr.getLeft() == null && ptr.getRight() == null) {
                    output = output + ptr.getData().getCharacter();
                    ptr = root;
                }
            }
        }
        StdOut.print(output);
        /* Your code goes here */
        // if (root == null)
        // return;
        // while (count < msg.length()) {
        // temp = msg.substring(count, count + 1);
        // if (temp.equals("0") && ptr.getLeft() != null) {
        // ptr = ptr.getLeft();
        // if (ptr.getLeft() == null && ptr.getRight() == null) {
        // output += ptr.getData().getCharacter();
        // ptr = root;
        // }
        // } else if (temp.equals("1") && ptr.getRight() != null) {
        // ptr = ptr.getRight();
        // if (ptr.getLeft() == null && ptr.getRight() == null) {
        // output += ptr.getData().getCharacter();
        // ptr = root;
        // }
        // }
        // count++;
        // }
    }
}
