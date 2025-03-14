package org.FrostFizzie.DFDevExplorer.mixin.client;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static org.FrostFizzie.DFDevExplorer.client.DFDevExplorerClient.client;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class MixinClientCommonNetworkHandler {
    @Redirect(method = "onPacketException", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;disconnect(Lnet/minecraft/network/DisconnectionInfo;)V"))
    private void onPacketException(ClientConnection connection, DisconnectionInfo disconnectionInfo) {
        ToastManager toastManager = client.getToastManager();
        Text title = Text.translatable("DFDevExplorer.toast.packetException.title");
        Text description = Text.translatable("DFDevExplorer.toast.packetException.description");
        SystemToast toast = new SystemToast(SystemToast.Type.UNSECURE_SERVER_WARNING, title, description);
        toastManager.add(toast);
    }
}
