package com.visinhand;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(VisInHand.MOD_ID)
public final class VisInHand {
    public static final String MOD_ID = "visinhand";
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Thaumaturge classes we patch that the game would otherwise load only once a player casts
     * or scans. Loading them at startup applies our mixins early, so a broken hook shows up in
     * the log right away instead of mid-game.
     */
    private static final String[] LATE_TARGETS = {
            "com.leclowndu93150.thaumaturge.api.spell.cast.SpellTarget",
            "com.leclowndu93150.thaumaturge.content.research.scan.ScanRaycastHelper",
    };

    public VisInHand(IEventBus modBus) {
        modBus.addListener(VisInHand::onCommonSetup);
        LOGGER.info("Vis in Hand loaded: Thaumaturge tools will aim with the VR controller");
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        for (String target : LATE_TARGETS) {
            try {
                Class.forName(target, true, VisInHand.class.getClassLoader());
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Thaumaturge class {} not found; this version may not be supported", target);
            }
        }
    }
}
