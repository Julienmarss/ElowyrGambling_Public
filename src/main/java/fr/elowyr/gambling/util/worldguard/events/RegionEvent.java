package fr.elowyr.gambling.util.worldguard.events;

import com.sk89q.worldguard.protection.regions.*;
import fr.elowyr.gambling.util.worldguard.movement.MovementWay;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public abstract class RegionEvent extends PlayerEvent
{
    private static final HandlerList handlerList;
    private ProtectedRegion region;
    private MovementWay movement;
    public PlayerEvent parentEvent;
    
    public RegionEvent(final ProtectedRegion region, final Player player, final MovementWay movement, final PlayerEvent parent) {
        super(player);
        this.region = region;
        this.movement = movement;
        this.parentEvent = parent;
    }
    
    public HandlerList getHandlers() {
        return RegionEvent.handlerList;
    }
    
    public ProtectedRegion getRegion() {
        return this.region;
    }
    
    public static HandlerList getHandlerList() {
        return RegionEvent.handlerList;
    }
    
    public MovementWay getMovementWay() {
        return this.movement;
    }
    
    public PlayerEvent getParentEvent() {
        return this.parentEvent;
    }
    
    static {
        handlerList = new HandlerList();
    }
}
