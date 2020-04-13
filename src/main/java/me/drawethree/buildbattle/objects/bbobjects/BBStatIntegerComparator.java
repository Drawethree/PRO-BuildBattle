package me.drawethree.buildbattle.objects.bbobjects;

import java.util.Comparator;

public class BBStatIntegerComparator implements Comparator<BBPlayerStats> {

    private final BBStat stat;

    public BBStatIntegerComparator(BBStat stat) {
        this.stat = stat;
    }

    @Override
    public int compare(BBPlayerStats o1, BBPlayerStats o2) {
        return ((Integer) o1.getStat(stat)).compareTo((Integer) o2.getStat(stat));
    }
}
