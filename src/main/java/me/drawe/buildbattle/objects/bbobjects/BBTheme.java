package me.drawe.buildbattle.objects.bbobjects;

public class BBTheme {

    private String name;
    private int votes;
    private int slotInInventory;
    private int percentage;

    public BBTheme(String name, int votes, int slotInInventory) {
        this.name = name;
        this.votes = votes;
        this.slotInInventory = slotInInventory;
    }

    public BBTheme(String name) {
        this.name = name;
        this.votes = -1;
        this.slotInInventory = -1;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getName() {
        return name;
    }

    public int getSlotInInventory() {
        return slotInInventory;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
