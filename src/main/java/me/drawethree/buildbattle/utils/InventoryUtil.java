package me.drawethree.buildbattle.utils;

public class InventoryUtil {

    public static int getInventorySize(int listAmount) {
        int size = 9;
        while (listAmount > size) {
            if (size == 54) {
                break;
            }
            size += 9;
        }
        return size;
    }
}
