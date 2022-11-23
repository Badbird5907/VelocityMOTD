package dev.badbird.velocitymotd.object;

import com.velocitypowered.api.proxy.server.ServerPing;
import lombok.Data;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class MOTDConfig {
    private List<String> lines = Arrays.asList("<green>Line1", "<red>Line2");
    private int maxPlayers = 69;
    private boolean maintenance = false;
    private boolean countdown = true;
    private boolean center = true;
    private String targetDateFormat = "MM/dd/yyyy HH:mm";
    private String targetDate = "01/01/2021 00:00";
    private String timezone = "EST";
    private List<String> maintenanceLines = Arrays.asList("<red><bold>MAINTENANCE</bold></red>", "<red>The server is currently under maintenance.");
    private List<String> countdownLines = Arrays.asList("<gold><bold>SERVER RELEASE</bold></gold>", "<gold>%days%d %hours%h %minutes%m %seconds%s</gold>");
    private String serverVersion = "DEFAULT";
    private int protocolVersion = 4; // 1.7.5+, velocity default
    private transient long countdownTime = -1;
    private List<String> samplePlayers = Arrays.asList("&cHi", "&bHello");

    public void init() {
        // convert targetDate to countdownTime, in unix epoch milliseconds, from targetDateFormat, targetDate, timezone
        if (countdown) {
            SimpleDateFormat format = new SimpleDateFormat(targetDateFormat);
            format.setTimeZone(java.util.TimeZone.getTimeZone(timezone));
            try {
                countdownTime = format.parse(targetDate).getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getActualLines() {
        List<String> lines = getRawLines();
        return lines;
    }


    private static final UUID ZERO_ID = new UUID(0, 0);

    public ServerPing.SamplePlayer[] getSamplePlayers() {
        List<ServerPing.SamplePlayer> list = new ArrayList<>();
        for (String samplePlayer : samplePlayers) {
            list.add(new ServerPing.SamplePlayer(translate(samplePlayer), ZERO_ID));
        }
        return list.toArray(new ServerPing.SamplePlayer[0]);
    }

    public Component getMOTD() {
        Component component = Component.text("");
        for (String actualLine : getActualLines()) {
            component = component.append(MiniMessage.miniMessage().deserialize(actualLine))
                    .append(Component.newline());
        }
        return component;
    }

    public List<String> getRawLines() {
        if (maintenance) {
            return maintenanceLines;
        } else if (countdown && countdownTime > System.currentTimeMillis()) {
            return getCountdownLinesFormatted();
        } else {
            return lines;
        }
    }

    public List<String> getCountdownLinesFormatted() {
        long days = (countdownTime - System.currentTimeMillis()) / 86400000;
        long hours = (countdownTime - System.currentTimeMillis()) / 3600000 % 24;
        long minutes = (countdownTime - System.currentTimeMillis()) / 60000 % 60;
        long seconds = (countdownTime - System.currentTimeMillis()) / 1000 % 60;
        return countdownLines.stream().map(line -> line.replace("%days%", String.valueOf(days)).replace("%hours%", String.valueOf(hours)).replace("%minutes%", String.valueOf(minutes)).replace("%seconds%", String.valueOf(seconds))).collect(Collectors.toList());
    }

    public String translate(String in) {
        return in.replace("&", "\u00A7");
    }
}
