package view;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import model.Encryption;

public class SaveFile {

    public static void saveGame(int level){
        PrintWriter writer= null;
        try {
            
            writer = new PrintWriter("saves/savefile.sav", "UTF-8");
            String encoded = Encryption.encode(level);
            writer.printf(encoded);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.err.println("Couldn't save file");
            e.printStackTrace();
        } finally {
            if(writer != null) writer.close();
        }
    }
    
    public static void corruptSave(){
        PrintWriter writer= null;
        try {
            
            writer = new PrintWriter("saves/savefile.sav", "UTF-8");
            writer.printf("poop :D");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.err.println("Couldn't save file");
            e.printStackTrace();
        } finally {
            if(writer != null) writer.close();
        }
    }
    
    public static int loadGame() throws NoSaveException{
        String readContent;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("saves/savefile.sav"));
            readContent = br.readLine();
            int level = Encryption.decode(readContent);
            if(level == -1){
                throw new NoSaveException();
            }
            return level;
        } catch (IOException e) {
            throw new NoSaveException();
        } catch (NumberFormatException e){
            throw new NoSaveException();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
