package com.visinhand.client;

import com.visinhand.VisInHand;
import java.util.Set;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.client.data.CloseKeyboardContext;
import org.vivecraft.api.client.data.OpenKeyboardContext;

/**
 * CC: Tweaked computers read keys straight from the screen rather than from a text box, so
 * Vivecraft never shows its keyboard for them. In VR, open it with any computer, turtle or
 * pocket computer terminal and close it again when the terminal closes.
 *
 * <p>Screens are matched by class name, so CC: Tweaked is not needed to compile.
 */
public final class ComputerKeyboard {
    private static final Set<String> COMPUTER_SCREENS = Set.of(
            "dan200.computercraft.client.gui.AbstractComputerScreen",
            "dan200.computercraft.client.gui.NoTermComputerScreen");

    private static boolean openedByUs;

    private ComputerKeyboard() {}

    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (isComputerScreen(event.getScreen()) && ClientVR.isActive()) {
            try {
                openedByUs = VRClientAPI.instance().openKeyboard(OpenKeyboardContext.FORCE);
            } catch (RuntimeException | LinkageError e) {
                VisInHand.LOGGER.debug("Could not open the VR keyboard", e);
            }
        }
    }

    public static void onScreenClosing(ScreenEvent.Closing event) {
        if (openedByUs && isComputerScreen(event.getScreen())) {
            openedByUs = false;
            try {
                VRClientAPI.instance().closeKeyboard(CloseKeyboardContext.FORCE);
            } catch (RuntimeException | LinkageError e) {
                VisInHand.LOGGER.debug("Could not close the VR keyboard", e);
            }
        }
    }

    static boolean isComputerScreen(Screen screen) {
        for (Class<?> type = screen.getClass(); type != null && type != Screen.class; type = type.getSuperclass()) {
            if (COMPUTER_SCREENS.contains(type.getName())) {
                return true;
            }
        }
        return false;
    }
}
