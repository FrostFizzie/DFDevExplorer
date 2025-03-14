package org.FrostFizzie.DFDevExplorer.Features;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public interface Feature {
    default void activate() {}
    default void tick() {}
    default void tooltip(ItemStack item, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> list) {}
    default boolean isActive() { return true; }
    default void hudRender(DrawContext draw, RenderTickCounter tickCounter) {}
    default void handlePacket(Packet<?> packet, PacketListener listener, CallbackInfo callback) {}
    default void sendPacket(Packet<?> packet, CallbackInfo callback) {}
    default void worldRenderLast(WorldRenderContext event) {}
    default void clientStop(MinecraftClient client) {}
    default String handleChatMessage(String message) { return message; }
    default void inventorySlotClicked(ScreenHandler handler, int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {

    }
}


