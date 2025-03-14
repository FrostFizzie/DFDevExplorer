package org.FrostFizzie.DFDevExplorer.Features;


import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.FrostFizzie.DFDevExplorer.Node;


import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static org.FrostFizzie.DFDevExplorer.Features.Features.inventoryName;
import static org.FrostFizzie.DFDevExplorer.client.DFDevExplorerClient.*;

public class NodeSwitcherFeature implements Feature {
    private final SoundEvent clickSound = SoundEvent.of(Identifier.of("minecraft", "block.wooden_button.click_on"));
    @Override
    public void handlePacket(Packet<?> packet, PacketListener listener, CallbackInfo callback) {
        if (!(listener instanceof ClientPlayNetworkHandler handler)) {
            return;
        }

        switch (packet) {
            case InventoryS2CPacket inventoryS2CPacket -> onInventory(inventoryS2CPacket, handler, callback);
            case OpenScreenS2CPacket openScreenS2CPacket -> onOpenScreen(openScreenS2CPacket, callback);
            case CloseScreenS2CPacket closeScreenS2CPacket -> onCloseScreen(closeScreenS2CPacket, callback);
            default -> {
            }
        }
    }

    @Override
    public void sendPacket(Packet<?> packet, CallbackInfo callback) {
        switch (packet) {
            case ClickSlotC2SPacket clickSlotC2SPacket -> onClickSlot(clickSlotC2SPacket, callback);
            default -> {
            }
        }
    }

    public void onInventory(InventoryS2CPacket packet, ClientPlayNetworkHandler handler, CallbackInfo callback) {
        if (inventoryName == null) {
            return;
        }
        if (packet.getContents().size() == 45 && Objects.equals(inventoryName.getString(), "Select Node") && Objects.requireNonNull(client.player).getScoreboard() != null) {
            List<ItemStack> stacks = packet.getContents();
            Node node = NodesList.stream().anyMatch(search -> search.getIP().equalsIgnoreCase("")) ? NodesList.stream().filter(search -> search.getIP().equalsIgnoreCase("")).findFirst().get() : NodesList.getFirst();
            stacks.set(8, node.getCompleteStack());
            callback.cancel();
            handler.onInventory(new InventoryS2CPacket(packet.getSyncId(), packet.getRevision(), (DefaultedList<ItemStack>) packet.getContents(), packet.getCursorStack()));
        }
    }

    public void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        inventoryName = packet.getName();
    }

    public void onCloseScreen(CloseScreenS2CPacket packet, CallbackInfo ci) {
        inventoryName = null;
    }

    public void onClickSlot(ClickSlotC2SPacket clickSlotC2SPacket, CallbackInfo ci) {
        if (inventoryName == null) {
            return;
        }
        ScreenHandler screenHandler = Objects.requireNonNull(client.player).currentScreenHandler;
        ItemStack stack = screenHandler.getSlot(clickSlotC2SPacket.getSlot()).getStack();
        if (NodesList.stream().noneMatch(node -> ItemStack.areEqual(node.getCompleteStack(), stack))) return;
        if (!(screenHandler instanceof GenericContainerScreenHandler && clickSlotC2SPacket.getSlot() == 8 && Objects.equals(inventoryName.getString(), "Select Node")))
            return;

        ci.cancel();
    }

    @Override
    public void inventorySlotClicked(ScreenHandler handler, int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!(handler instanceof GenericContainerScreenHandler) || slotIndex < 0 || slotIndex >= 45 || inventoryName == null || !Objects.equals(inventoryName.getString(), "Select Node")) {
            return;
        }

        ItemStack item = handler.slots.get(slotIndex).getStack();
        if (item.isEmpty()) return;

        try {
            Optional<Node> currentNodeOpt = getCurrentNode(item);
            if (currentNodeOpt.isEmpty()) return;

            Node currentNode = currentNodeOpt.get();

            if (isRightClick(actionType, button)) {
                handleRightClick(handler, slotIndex, currentNode, actionType, ci);
            } else {
                handleNodeSelection(player, currentNode, ci);
            }
        } catch (IndexOutOfBoundsException | NoSuchElementException ignored) {
        }
    }


    private Optional<Node> getCurrentNode(ItemStack item) {
        return NodesList.stream()
                .filter(node -> ItemStack.areEqual(node.getCompleteStack(), item))
                .findFirst();
    }


    private boolean isRightClick(SlotActionType actionType, int button) {
        return button == 1 && (actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE);
    }


    private void handleRightClick(ScreenHandler handler, int slotIndex, Node currentNode, SlotActionType actionType, CallbackInfo ci) {
        int direction = (actionType == SlotActionType.PICKUP) ? 1 : -1;
        int currentNodeIndex = NodesList.indexOf(currentNode);
        int newIndex = (currentNodeIndex + direction + NodesList.size()) % NodesList.size();

        ItemStack newNodeItem = NodesList.get(newIndex).getCompleteStack();
        if (!Objects.equals(handler.slots.get(slotIndex).getStack(), newNodeItem)) {
            handler.setStackInSlot(slotIndex, handler.getRevision(), newNodeItem);
        }

        ci.cancel();
        playClickSound();
    }


    private void handleNodeSelection(PlayerEntity player, Node currentNode, CallbackInfo ci) {
        String plainName = PlainTextComponentSerializer.plainText().serialize(component(currentNode.getName()));
        player.sendMessage(miniMessage("<green><bold>»</bold></green> <gray>Sending you to " + plainName + "...</gray>"), false);
        Objects.requireNonNull(client.getNetworkHandler()).sendCommand("server " + currentNode.getID());

        ci.cancel();
        closePlayerScreens();
    }


    private void playClickSound() {
        if (client.player != null) {
            client.player.playSound(clickSound, 2f, 1.74f);
        }
    }

    private void closePlayerScreens() {
        if (client.player != null) {
            client.player.closeHandledScreen();
            client.player.closeScreen();
        }
    }
}
