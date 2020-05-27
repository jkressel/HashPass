package eu.japk.hashpass;

public class CustomCharacters {

    private char[] chars;

    public CustomCharacters(String custom){
        chars = custom.toCharArray();
    }

    public int getCharacter(int pos){
        if(pos < chars.length && pos >= 0){
            return chars[pos];
        }
        return -1;

    }

    public int getNumberOfChars(){
        return chars.length;
    }
}
