package com.visinhand;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

/**
 * VR compatibility for Vivecraft players. Every integration is optional and only does anything
 * when its mod is installed: Thaumaturge, Industrial Craft 2 and CC: Tweaked.
 */
@Mod(VisInHand.MOD_ID)
public final class VisInHand {
    public static final String MOD_ID = "visinhand";
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Patched classes the game would otherwise load only once a player casts, scans or fires.
     * Loading them at startup applies our mixins early, so a broken hook shows up in the log
     * right away instead of mid-game. Keyed by mod id; skipped when the mod is absent.
     */
    private static final Map<String, List<String>> LATE_TARGETS = Map.of(
            "thaumaturge", List.of(
                    "com.leclowndu93150.thaumaturge.api.spell.cast.SpellTarget",
                    "com.leclowndu93150.thaumaturge.content.research.scan.ScanRaycastHelper"),
            "ic2", List.of(
                    "ic2.core.item.tool.ItemToolMiningLaser"));

    public VisInHand(IEventBus modBus) {
        modBus.addListener(VisInHand::onCommonSetup);
        LOGGER.info("Vis in Hand loaded: VR support for Thaumaturge, Industrial Craft 2 and CC: Tweaked (whichever are installed)");
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        LATE_TARGETS.forEach((modId, classes) -> {
            if (!ModList.get().isLoaded(modId)) {
                return;
            }
            for (String target : classes) {
                try {
                    Class.forName(target, true, VisInHand.class.getClassLoader());
                } catch (ClassNotFoundException e) {
                    LOGGER.warn("{} class {} not found; this version may not be supported", modId, target);
                }
            }
        });
    }
}
