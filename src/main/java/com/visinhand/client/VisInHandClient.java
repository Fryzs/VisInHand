package com.visinhand.client;

import com.visinhand.VisInHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Client entry point. Each integration is registered only when its mod is installed, so classes
 * that touch Thaumaturge or CC: Tweaked are never loaded without them.
 */
@Mod(value = VisInHand.MOD_ID, dist = Dist.CLIENT)
public final class VisInHandClient {
    public VisInHandClient() {
        ModList mods = ModList.get();
        if (mods.isLoaded("thaumaturge")) {
            NeoForge.EVENT_BUS.addListener(VRFocusInput::onClientTick);
            VisInHand.LOGGER.info("Vis in Hand: Thaumaturge VR support on");
        }
        if (mods.isLoaded("computercraft")) {
            NeoForge.EVENT_BUS.addListener(ComputerKeyboard::onScreenInit);
            NeoForge.EVENT_BUS.addListener(ComputerKeyboard::onScreenClosing);
            VisInHand.LOGGER.info("Vis in Hand: CC: Tweaked VR keyboard on");
        }
        if (mods.isLoaded("ic2")) {
            // IC2 needs no client listener: its hooks are mixins (laser aim, stick-as-forward).
            VisInHand.LOGGER.info("Vis in Hand: Industrial Craft 2 VR support on");
        }
        if (DevSelfTest.ENABLED) {
            NeoForge.EVENT_BUS.addListener(DevSelfTest::onClientTick);
        }
    }
}
