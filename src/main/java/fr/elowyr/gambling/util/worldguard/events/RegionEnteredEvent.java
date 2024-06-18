package fr.elowyr.gambling.util.worldguard.events;

import com.sk89q.worldguard.protection.regions.*;
import fr.elowyr.gambling.util.worldguard.movement.MovementWay;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;

public class RegionEnteredEvent extends RegionEvent
{
    public RegionEnteredEvent(final ProtectedRegion region, final Player player, final MovementWay movement, final PlayerEvent parent) {
        super(region, player, movement, parent);
    }
}