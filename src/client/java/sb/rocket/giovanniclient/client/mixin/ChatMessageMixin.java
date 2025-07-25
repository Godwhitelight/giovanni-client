package sb.rocket.giovanniclient.client.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import sb.rocket.giovanniclient.client.config.ConfigManager;
import sb.rocket.giovanniclient.client.features.fun.FunConfig;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatMessageMixin {
    @Unique
    private final FunConfig fc = ConfigManager.getConfig().fc;

    /**
     * This @ModifyVariable targets the 'userInput' String argument of the 'sendChatMessage' method.
     * By injecting at the HEAD of the method and specifying 'argsOnly = true',
     * we ensure that the 'userInput' variable is modified as soon as the method begins execution.
     * This means both the MessageBody (for signing) and the ChatMessageC2SPacket (for sending)
     * will receive the modified message, resolving signature mismatches.
     */
    @ModifyVariable(
            method = "sendChatMessage(Ljava/lang/String;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private String modifySentChatMessage(String userInput) {
        if (fc.FAKE_IRONMAN_TOGGLE && !userInput.startsWith("/")) {
            return "♲: " + userInput;
        }

        if (fc.TROLL_FEATURES && userInput.equals("Help Wizardman!"))
            return "Help Giovanni!";

        return userInput;
    }
}