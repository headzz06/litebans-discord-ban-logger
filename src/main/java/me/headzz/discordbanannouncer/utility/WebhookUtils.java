package me.headzz.discordbanannouncer.utility;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public final class WebhookUtils {
    private final String url;

    public WebhookUtils(final String url) {
        this.url = url;
    }

    public void execute(final String content) {
        try {
            final URL url = new URL(this.url);
            final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Java-DiscordWebhook");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            final OutputStream stream = connection.getOutputStream();
            stream.write(content.getBytes());
            stream.flush();
            stream.close();

            connection.getInputStream().close();
            connection.disconnect();
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    public static final WebhookUtils newWebhookUtils(final String url) {
        return new WebhookUtils(url);
    }
}