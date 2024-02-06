package com.metype.hidenseek.Game;

import org.bukkit.Material;

public class GameProperties {
    public boolean invisibleSeekers = false, forceHidersInBounds = true, forceSeekersInBounds = false,
            autoNewGame = true, allowElytra = false, allowEnderPearls = false, allowChorusFruit = false,
            allowShootBows = false, allowThrowSnowballs = true, allowThrowEggs = true, allowDamage = false,
            allowTeleport = true, allowOpeningContainers = false, projectileTagEnabled = false,
            meleeTagEnabled = true;
    public int gameLength = 300, seekerSpeedStrength = 2, outOfBoundsTime = 10, startingSeekers = 1,
    autoNewGameStartTime = 30, minHeightBounds = 0, maxHeightBounds = 64, hideTime = 30;
    public String gameName = "";
    public Material displayMaterial = Material.GOLD_BLOCK;

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof GameProperties props)) return false;
        // This was written at 2 am, and is probably *not* how you should do this :D
        return props.invisibleSeekers == invisibleSeekers &&
                props.forceHidersInBounds == forceHidersInBounds &&
                props.forceSeekersInBounds == forceSeekersInBounds &&
                props.autoNewGame == autoNewGame &&
                props.allowElytra == allowElytra &&
                props.allowEnderPearls == allowEnderPearls &&
                props.allowChorusFruit == allowChorusFruit &&
                props.allowTeleport == allowTeleport &&
                props.allowShootBows == allowShootBows &&
                props.allowThrowSnowballs == allowThrowSnowballs &&
                props.allowThrowEggs == allowThrowEggs &&
                props.allowOpeningContainers == allowOpeningContainers &&
                props.allowDamage == allowDamage &&
                props.projectileTagEnabled == projectileTagEnabled &&
                props.meleeTagEnabled == meleeTagEnabled &&
                props.gameLength == gameLength &&
                props.seekerSpeedStrength == seekerSpeedStrength &&
                props.outOfBoundsTime == outOfBoundsTime &&
                props.startingSeekers == startingSeekers &&
                props.autoNewGameStartTime == autoNewGameStartTime &&
                props.minHeightBounds == minHeightBounds &&
                props.maxHeightBounds == maxHeightBounds &&
                props.hideTime == hideTime &&
                props.gameName.equals(gameName) &&
                displayMaterial.equals(props.displayMaterial);
    }
}
