import java.io.File;
import java.io.Writer;
import java.util.*;
import java.util.Scanner;
import java.io.FileReader;
import java.io.FileWriter;
public class cipher_2{
    enum commandType{
        ENCRYPT,
        DECRYPT,
        TEST,
        NONE
    }
    public static void main(String args[]){

        String plainString = null;//plain string of all lowercase letters
        String cipherString = null;//cipher string of all lowercase letters
        if(args.length >= 1){//checks if there are enough arguments to fill the plain and cipher strings
            plainString = args[0];
            cipherString = args[1];
        }
        else {
            System.out.println("Please add arguments into the CLI");//error message if there are not enough arguments
            System.exit(0);//stops the code from running any farther
        }

        char invalid_char = '0';//what all characters that are not valid will be mapped to
        Scanner scan = new Scanner(System.in);
        System.out.println("Please input a command; 'e' to encrypt, 'd' to decrypt, and 't' to test.");
        commandType cmd = commandType.NONE;
        String userInput = getInput(scan);//gets the command that tells the program what it should do
        cmd = processInput(userInput);//changes the user input to a commandType
        if (cmd == commandType.TEST){
            doUnitTests();
        }
        else if (cmd == commandType.NONE){
            System.out.println("Sorry that was an invalid input.");
        }
        //builds the arrays if the user does not input test
        if(plainString.length() != cipherString.length()){//checks to make sure the two arguments have the same # of chars
            System.out.println("Please check that the plain and cipher strings are the same length");

        }
        else {
            char[] cipherArray = buildEncryptionArray(cipherString, plainString, invalid_char);
            char[] plainArray = buildPlainArray(cipherString, plainString, invalid_char);

            if (cmd == commandType.ENCRYPT) {
                getFiles("encoding", cipherArray, scan, invalid_char);
            } else if (cmd == commandType.DECRYPT) {
                getFiles("decoding", plainArray, scan, invalid_char);
            }
        }
    }

    //gets input and output file names from the user
    public static void getFiles(String function, char[] keyArray, Scanner scan, char invalid_char){
        System.out.println("Please input the input filename");
        String inputFile = getInput(scan);
        System.out.println("Please input the output filename");
        String outputFile = getInput(scan);
        if(!map(keyArray, inputFile, outputFile, invalid_char)){
            System.out.println(function +  " Failed");
        }
        else{
            System.out.println(function + " Completed");
        }
    }

    //builds the encryption array
    public static char[] buildEncryptionArray(String cipherStr, String plainStr, char invalid_char){
        char cipher[] = new char[256];
        Arrays.fill(cipher, invalid_char);//fills array with invalid chars
        for (int i = 0; i < plainStr.length(); i++){
            cipher[(int)plainStr.charAt(i)] = cipherStr.charAt(i);
        }
        return cipher;
    }
    //builds the plain array for decryption
    public static char[] buildPlainArray(String cipherStr, String plainStr, char invalid_char){
        char plain[] = new char[256];
        Arrays.fill(plain, invalid_char);//fills the array with invalid chars
        for (int i = 0; i < plainStr.length(); i++){
            plain[(int)cipherStr.charAt(i)] = plainStr.charAt(i);
        }
        return plain;
    }

    //encrypts or decrypts the input array using the key array then writes that to the output file
    public static boolean map(char[] keyArray , String inputFile , String outputFile, char invalid_char){
        boolean completed = false;
        try {
            // File reader to get the input.
            File targetFile = new File(inputFile);
            FileReader reader = new FileReader(targetFile);
            //file writer to print to the output file
            File outFile = new File(outputFile);
            FileWriter writer = new FileWriter(outFile);

            char[] cbuf = new char[100]; // Buffer to store characters from file
            while (-1 != (reader.read(cbuf)))//check if there are characters left in the file being read
            {
                char[] outbuf = new char[100];//output buffer that will be printed
                //encrypt the buffer of characters
                int validChars = encryptCharArray(cbuf, outbuf, keyArray, invalid_char);
                //writes all VALID information to the output file
                if(!writeTo(outputFile,outbuf,validChars, writer)){//calls the function to write to the output file
                    //if the function fails the loop is broken and the error message is printed
                    System.out.println("writing failed");
                    completed = false;
                    break;
                }
                completed = true;
                Arrays.fill(cbuf, invalid_char);//fills the read buffer with invalid characters in case
                // it is not filled by the file reader, prevents the writer from writing the same thing twice
            }
            // close the reader resource

            reader.close();

        }
        catch (java.io.IOException ex) {
            System.out.println("IOException thrown");
            completed = false;
        }
        return completed;
    }
    //gets a string input from the user using a scanner passed in from main
    public static String getInput(Scanner scan){
        String output = null;
        output = scan.next();
        return output;
    }
    //processes an input to return a commandType enum
    public static commandType processInput(String input){
        commandType cmd = commandType.NONE;
        if(input.equals("e")){
            cmd = commandType.ENCRYPT;
        }
        else if (input.equals("d")){
            cmd = commandType.DECRYPT;
        }
        else if (input.equals("t")){
            cmd = commandType.TEST;
        }
        else{
            cmd = commandType.NONE;
        }
        return cmd;
    }
    public static boolean writeTo(String fileName, char[] outbuf, int validChars, Writer writer){
        boolean done = false;
        try{
            //prints all the valid characters to the output file
            writer.write(outbuf,0,validChars);
            //moves to the next line
            writer.write("\n");
            //pushes out any buffer that was not printed
            writer.flush();
            done = true;
        }
        catch(java.io.IOException ex){
            done = false;
        }
        return done;
    }
    public static int encryptCharArray(char[] cbuf, char outbuf[], char[] key, char invalid_char){
        int tgtIndex = 0;
        for (int i = 0; i < cbuf.length; i++){
            if(key[(int)Character.toLowerCase(cbuf[i])] != invalid_char){//checks if the char is valid
                char currentChar = Character.toLowerCase(cbuf[i]);//changes to the lowercase version of the char
                outbuf[tgtIndex] = key[(int)currentChar];//maps the char to the encrypted or decrypted char
                tgtIndex++;//moves the target index to only account for valid chars
            }
        }
        return tgtIndex;
    }

    //unit tests
    public static void doUnitTests(){
        int successCount = 0;
        int failureCount = 0;
        int testCount = 0;
        char invalidChar = '0';

        //Cipher and plain strings for use in unit tests
        String cipherString = "bcdefghijklmnopqrstuvwxyza";
        String plainString = "abcdefghijklmnopqrstuvwxyz";


        char[] encryptionArray = buildEncryptionArray(cipherString, plainString, invalidChar);
        char[] plainArray = buildPlainArray(cipherString, plainString, invalidChar);

        for (int i = 0; i < plainString.length(); i++){
            testCount++;
            if(encryptionArray[plainString.charAt(i)] != cipherString.charAt(i) || plainArray[cipherString.charAt(i)] != plainString.charAt(i)){
                System.out.println("Building encryption array test failed at index " + i);
                failureCount++;
            }
            else{
                successCount++;
            }

        }

        char[] testArray = {'a', 'b', '1', 'z', '.', 'f'};
        char[] returnedArray = new char[testArray.length];

        char[] expectedEncryptedArray = {'b','c','a','g'};
        if(4 != encryptCharArray(testArray, returnedArray, encryptionArray, invalidChar)){
            failureCount++;
            testCount++;
            System.out.println("Characters encrypted returned incorrect amount");

        }
        else{
            successCount++;
            testCount++;
            for (int a = 0; a < expectedEncryptedArray.length; a++){
                if(returnedArray[a] != expectedEncryptedArray[a]){
                    failureCount++;
                    testCount++;
                }
                else{
                    testCount++;
                    successCount++;
                }
            }
        }



        char[] expectedDecryptedArray = {'z', 'a', 'y', 'e'};
        if(4 != encryptCharArray(testArray, returnedArray, plainArray, invalidChar)){
            failureCount++;
            testCount++;
            System.out.println("Characters encrypted returned incorrect amount");

        }
        else{
            successCount++;
            testCount++;
            for (int b = 0; b < expectedDecryptedArray.length; b++){
                if(returnedArray[b] != expectedDecryptedArray[b]){
                    failureCount++;
                    testCount++;
                }
                else{
                    testCount++;
                    successCount++;
                }
            }

        }


        String[] testInput = {"E", "e", "1", "d", "t", "a", "ewr", "test"};
        commandType[] expectedOutput = {commandType.NONE, commandType.ENCRYPT, commandType.NONE, commandType.DECRYPT, commandType.TEST, commandType.NONE, commandType.NONE, commandType.NONE};

        //tests process input function
        for (int z = 0; z < testInput.length; z++){
            testCount++;
            if(expectedOutput[z] != processInput(testInput[z])){
                failureCount++;
                System.out.println("process input failed with input " + testInput[z]);
            }
            else{
                successCount++;
            }

        }



        System.out.println(successCount + " tests succeeded of " + testCount + " tests run.");
        System.out.println(failureCount + " tests failed of " + testCount + " tests run.");
    }


}
