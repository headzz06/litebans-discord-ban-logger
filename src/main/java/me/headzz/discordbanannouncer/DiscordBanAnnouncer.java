package me.headzz.discordbanannouncer;

import litebans.api.Entry;
import litebans.api.Events;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.awt.Color;
import java.util.Objects;
import java.util.UUID;

public final class DiscordBanAnnouncer extends JavaPlugin implements Listener {

    JDA jda;

    TextChannel textChannel;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        try {
            jda = JDABuilder.createDefault(getConfig().getString("BotToken"))
                    .setActivity(Activity.listening("for punishments"))
                    .build();
        }catch (Exception e){
            e.printStackTrace();
            getLogger().severe("Cannot connect to Discord Bot using this BotToken! Disabling!");
            getServer().getPluginManager().disablePlugin(this);
        }

        registerEvents();
        getServer().getPluginManager().registerEvents(this, this);

        try {
            textChannel = jda.getTextChannelById(getConfig().getString("ChannelID"));
        }catch (Exception e){
            e.printStackTrace();
            getLogger().severe("Invalid Channel ID! Disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
        }

    }

    @Override
    public void onDisable() {
        jda.shutdown();
    }

    public void registerEvents(){
        Events.get().register(new Events.Listener(){
            @Override
            public void entryAdded(Entry entry){
                EmbedBuilder eb = new EmbedBuilder();
                String playername = getName(entry.getUuid());
                eb.setImage("https://minotar.net/helm/"+playername+"/100.png");
                if (entry.getType().equals("ban")){
                    eb.setTitle("New Ban").addField("Username", playername, false)
                            .addField("Punished by", entry.getExecutorName(), true)
                            .addField("Duration",entry.getDurationString(),true)
                            .addField("Reason",entry.getReason(), true)
                            .setColor(Color.red);
                    textChannel.sendMessageEmbeds(eb.build()).queue();
                }else if (entry.getType().equals("warn")){
                    eb.setTitle("New Warn").addField("Username", playername, false)
                            .addField("Punished by", entry.getExecutorName(), true)
                            .addField("Reason",entry.getReason(), true)
                            .setColor(Color.red);
                    textChannel.sendMessageEmbeds(eb.build()).queue();

                }else if (entry.getType().equals("mute")){
                    eb.setTitle("New Mute").addField("Username", playername, false)
                            .addField("Punished by", entry.getExecutorName(), true)
                            .addField("Duration",entry.getDurationString(),true)
                            .addField("Reason",entry.getReason(), true)
                            .setColor(Color.red);
                    textChannel.sendMessageEmbeds(eb.build()).queue();
                }else if (entry.getType().equals("kick")){
                    eb.setTitle("New Kick").addField("Username", playername, false)
                            .addField("Punished by", entry.getExecutorName(), true)
                            .addField("Reason",entry.getReason(), true)
                                    .setColor(Color.red);
                    textChannel.sendMessageEmbeds(eb.build()).queue();
                }
            }
            @Override
            public void entryRemoved(Entry entry){
                EmbedBuilder eb = new EmbedBuilder();
                String playername = getName(entry.getUuid());
                eb.setImage("https://minotar.net/helm/"+playername+"/100.png");
                if (entry.getType().equals("ban")) {
                    eb.setTitle("New Unban").addField("Username",playername , false)
                            .addField("Removed by", Objects.requireNonNull(entry.getRemovedByName()), true)
                            .addField("Reason", entry.getReason(), true)
                            .setColor(Color.green);
                    textChannel.sendMessageEmbeds(eb.build()).queue();
                }else if (entry.getType().equals("mute")){
                    eb.setTitle("New Unmute").addField("Username", playername, false)
                            .addField("Removed by", Objects.requireNonNull(entry.getRemovedByName()), true)
                            .addField("Reason", entry.getReason(), true)
                            .setColor(Color.green);
                    textChannel.sendMessageEmbeds(eb.build()).queue();
                }else if (entry.getType().equals("warn")){
                    eb.setTitle("New Unwarn").addField("Username", playername, false)
                            .addField("Removed by", Objects.requireNonNull(entry.getRemovedByName()),true)
                            .addField("Reason", entry.getReason(), true)
                            .setColor(Color.green);
                    textChannel.sendMessageEmbeds(eb.build()).queue();
                }
            }
        });
    }

    public String getName(String s){
      return (getServer().getOfflinePlayer(UUID.fromString(s)).getName());
    }

}
