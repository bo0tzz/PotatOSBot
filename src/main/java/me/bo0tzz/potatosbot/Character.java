package me.bo0tzz.potatosbot;

/**
 * Created by bo0tzz on 3-5-2016.
 */
public enum Character {
    ANNOUNCER("Announcer"),
    CAROLINE("Caroline"),
    CAVE_JOHNSON("Cave Johnson"),
    CORE("Core"),
    DEFECTIVE_TURRET("Defective Turret"),
    GLADOS("GLaDOS"),
    TURRET("Turret"),
    WHEATLEY("Wheatley"),
    ALL("");

    private final String name;

    Character(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getEndpoint() {
        if (!name.equals(""))
            return name + "/";
        return "/";
    }
}
