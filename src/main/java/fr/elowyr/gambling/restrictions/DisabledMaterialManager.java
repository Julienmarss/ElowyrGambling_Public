package fr.elowyr.gambling.restrictions;

import fr.elowyr.gambling.Gambling;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class DisabledMaterialManager {

    private Gambling gambling;
    private List<Material> materialList;

    public DisabledMaterialManager(Gambling gambling) {
        this.gambling = gambling;
        this.materialList = new ArrayList<>();
        this.gambling.getConfigManager().getStringList("DISABLED-ITEM").forEach(line -> {
            materialList.add(Material.getMaterial(line));
        });
    }

    public List<Material> getMaterialList() {
        return materialList;
    }
}
