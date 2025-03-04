package fr.elowyr.gambling.util.command;

import fr.elowyr.gambling.Gambling;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ACommand implements CommandExecutor {

    private final String commandName;
    private final String permission;
    private final boolean consoleCanExecute;
    private final Gambling gambling;

    public ACommand(Gambling gambling, String commandName, String permission, boolean consoleCanExecute) {
        this.permission = permission;
        this.commandName = commandName;
        this.consoleCanExecute = consoleCanExecute;
        this.gambling = gambling;
        gambling.getCommand(commandName).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getLabel().equalsIgnoreCase(commandName))
            return true;

        if (!consoleCanExecute && !(sender instanceof Player)) {
            sender.sendMessage(gambling.getConfigManager().getString("NOT-PLAYER"));
            return true;
        }

        if (!sender.hasPermission(permission)) {
            sender.sendMessage(gambling.getConfigManager().getString("NO-PERMISSION"));
            return true;
        }

        return execute(sender, args);
    }

    public abstract boolean execute(CommandSender sender, String[] args);
}
