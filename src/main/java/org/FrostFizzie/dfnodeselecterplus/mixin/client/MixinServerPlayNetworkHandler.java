package org.FrostFizzie.dfnodeselecterplus.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.FrostFizzie.dfnodeselecterplus.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.FrostFizzie.dfnodeselecterplus.client.DfnodeselecterplusClient.*;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {
    @Inject(method = "sendPacket", at = @At(value = "HEAD"), cancellable = true)
    public void onClickSlot(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ClickSlotC2SPacket) {
            ClickSlotC2SPacket clickSlotC2SPacket = (ClickSlotC2SPacket) packet;
            ScreenHandler screenHandler = client.player.currentScreenHandler;
            if (screenHandler instanceof GenericContainerScreenHandler && clickSlotC2SPacket.getSlot() == 8 && inventoryName.getString().equals("Select Node")) {
                if (!NodesList.stream()
                        .anyMatch(node -> ItemStack.areEqual(node.getCompleteStack(), screenHandler.getSlot(clickSlotC2SPacket.getSlot()).getStack()))) {
                    return;
                }
                ci.cancel();
            }
        }
    }
}