package fr.iglee42.createqualityoflife.utils;

import net.minecraft.network.FriendlyByteBuf;

public class Utils {

    public static void saveStringToBuffer(FriendlyByteBuf buf , String s){
        buf.writeInt(s.length());
        for (char c : s.toCharArray()){
            buf.writeChar(c);
        }
    }

    public static String readStringFromBuffer(FriendlyByteBuf buf){
        int length = buf.readInt();
        char[] word = new char[length];
        for (int i = 0; i < length; i++){
            word[i] = buf.readChar();
        }
        return String.valueOf(word);
    }
}
