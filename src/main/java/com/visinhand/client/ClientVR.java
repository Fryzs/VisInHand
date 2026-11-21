package com.visinhand.client;

import com.visinhand.VisInHand;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPart;

/** Client-side view of Vivecraft: is the local player in VR right now. */
public final class ClientVR {
    private ClientVR() {}

    public static boolean isActive() {
        try {
            return VRClientAPI.instance().isVRActive();
        } catch (RuntimeException | LinkageError e) {
            VisInHand.LOGGER.debug("Could not query Vivecraft VR state", e);
            return false;
        }
    }

    /** A short buzz in the main-hand controller, if VR is active. */
    public static void tap() {
        if (!isActive()) {
            return;
        }
        try {
            VRClientAPI.instance().triggerHapticPulse(VRBodyPart.MAIN_HAND, 0.05F);
        } catch (RuntimeException | LinkageError ignored) {
            // haptics are a nicety; never fail a click over them
        }
    }
}
