package fr.elowyr.gambling.listener;

import fr.elowyr.gambling.Gambling;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandsListener implements Listener {

    private Gambling gambling;

    public CommandsListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void processCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();

        if (gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            gambling.getAllowedManager().getCommands().stream().filter(s -> !event.getMessage().startsWith(s)).forEach(s -> {
                event.setCancelled(true);
                player.sendMessage(this.gambling.getConfigManager().getString("COMMAND-DISABLED"));
            });
        }
    }
}
