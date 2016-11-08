package io.github.volyx;


public class Req {

    private final String username;
    private final String display_name;
    private final String text;

    public Req(){
        username = null;
        display_name = null;
        text = null;
    }

    public Req(String username, String display_name, String text) {
        this.username = username;
        this.display_name = display_name;
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getText() {
        return text;
    }
}
