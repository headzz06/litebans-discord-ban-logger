package me.headzz.discordbanannouncer.utility;

public enum WebhookColors {
    RED("16711680"), GREEN("65331");

    private final String hexCode;

    WebhookColors(final String hexCode) {
        this.hexCode = hexCode;
    }

    public String getHexCode() {
        return this.hexCode;
    }
}