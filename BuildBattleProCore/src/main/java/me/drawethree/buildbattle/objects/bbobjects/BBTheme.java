package me.drawethree.buildbattle.objects.bbobjects;

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

    public boolean isSuperVoteSlotClicked(int clickedSlot) {
        return clickedSlot == slotInInventory + 8;
    }
}
