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

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Features {
    public static final HashMap<Class<?>, Feature> features = new HashMap<>();
    public static Text inventoryName = null;
    public static void init() {
        addFeature(new NodeSwitcherFeature());

        features.values().forEach(Feature::activate);
    }
    public static void addFeature(Feature feature) {
        features.put(feature.getClass(), feature);
    }

    public static Stream<Feature> features() {
        return features.values().stream().filter(Feature::isActive);
    }

    public static Feature getFeature(Class<?> clazz) {
        return features.get(clazz);
    }
    public static void tick() {
        features().forEach(Feature::tick);
    }
    public static void tooltip(ItemStack item, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> list) {
        features().forEach(feature -> feature.tooltip(item, tooltipContext, tooltipType, list));
    }
    public static void hudRender(DrawContext draw, RenderTickCounter counter) {
        features().forEach(feature -> feature.hudRender(draw, counter));
    }
    public static void handlePacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        features().forEach(feature -> feature.handlePacket(packet, listener, ci));
    }
    public static void sendPacket(Packet<?> packet, CallbackInfo ci) {
        features().forEach(feature -> feature.sendPacket(packet, ci));
    }
    public static void worldRenderLast(WorldRenderContext event) {
        features().forEach(feature -> feature.worldRenderLast(event));
    }
    public static void clientStop(MinecraftClient client) {
        features().forEach(feature -> feature.clientStop(client));
    }
    public static String handleChatMessage(String message) {
            for (Feature feature : features().toList()) {
                message = feature.handleChatMessage(message);
            }
        return message;
    }
    public static void onSlotClick(ScreenHandler handler, int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        for (Feature feature : features().toList()) {
            feature.inventorySlotClicked(handler, slotIndex, button, actionType, player, ci);
        }
    }
}
