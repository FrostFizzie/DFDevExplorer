package org.FrostFizzie.dfnodeselecterplus.mixin.client;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static org.FrostFizzie.dfnodeselecterplus.client.DfnodeselecterplusClient.*;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {
    @Inject(method = "sendPacket", at = @At(value = "HEAD"), cancellable = true)
    public void onClickSlot(Packet<?> packet, CallbackInfo ci) {
        if (inventoryName == null) {
            return;
        }
        if (packet instanceof ClickSlotC2SPacket clickSlotC2SPacket) {
            ScreenHandler screenHandler = Objects.requireNonNull(client.player).currentScreenHandler;
            if (screenHandler instanceof GenericContainerScreenHandler && clickSlotC2SPacket.getSlot() == 8 && inventoryName.getString().equals("Select Node")) {
                if (NodesList.stream()
                        .noneMatch(node -> ItemStack.areEqual(node.getCompleteStack(), screenHandler.getSlot(clickSlotC2SPacket.getSlot()).getStack()))) {
                    return;
                }
                ci.cancel();
            }
        }
    }
}