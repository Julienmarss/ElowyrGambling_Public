package fr.elowyr.gambling.util.worldguard.events;

import com.sk89q.worldguard.protection.regions.*;
import fr.elowyr.gambling.util.worldguard.movement.MovementWay;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class RegionLeaveEvent extends RegionEvent implements Cancellable
{
    private boolean cancelled;
    private boolean cancellable;
    
    public RegionLeaveEvent(final ProtectedRegion region, final Player player, final MovementWay movement, final PlayerEvent parent) {
        super(region, player, movement, parent);
        this.cancelled = false;
        this.cancellable = true;
        if (movement == MovementWay.SPAWN || movement == MovementWay.DISCONNECT) {
            this.cancellable = false;
        }
    }
    
    public void setCancelled(final boolean cancelled) {
        if (!this.cancellable) {
            return;
        }
        this.cancelled = cancelled;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public boolean isCancellable() {
        return this.cancellable;
    }
    
    protected void setCancellable(final boolean cancellable) {
        if (!(this.cancellable = cancellable)) {
            this.cancelled = false;
        }
    }
}
