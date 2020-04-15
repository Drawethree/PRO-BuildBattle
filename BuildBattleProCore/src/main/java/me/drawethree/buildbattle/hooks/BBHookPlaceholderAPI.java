package me.drawethree.buildbattle.hooks;

import me.drawethree.buildbattle.BuildBattle;

public class BBHookPlaceholderAPI extends BBHook {

    public BBHookPlaceholderAPI() {
        super("PlaceholderAPI");
    }

    @Override
    protected void runHookAction(BuildBattle plugin) {
        //We are no longer registering placeholders locally. Extension was created.
    }
}
