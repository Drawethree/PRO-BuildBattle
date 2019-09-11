package me.drawe.buildbattle.hooks;

public class BBHookPlaceholderAPI extends BBHook {

    public BBHookPlaceholderAPI() {
        super("PlaceholderAPI");
    }

    @Override
    protected void runHookAction() {
        //We are no longer registering placeholders locally. Extension was created.
    }
}
