package org.FrostFizzie.dfnodeselecterplus.mixin.client;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.FrostFizzie.dfnodeselecterplus.Node;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static org.FrostFizzie.dfnodeselecterplus.client.DfnodeselecterplusClient.*;

@Mixin(ScreenHandler.class)
public abstract class MixinScreenHandler {

    @Shadow @Final public DefaultedList<Slot> slots;

    @Shadow public abstract int getRevision();

    @Shadow public abstract void setStackInSlot(int slot, int revision, ItemStack stack);

    @Unique
    private final SoundEvent clickSound = SoundEvent.of(Identifier.of("minecraft", "block.wooden_button.click_on"));

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!((Object) this instanceof GenericContainerScreenHandler) || slotIndex < 0 || slotIndex >= 45 || inventoryName == null || !inventoryName.getString().equals("Select Node")) {
            return;
        }

        ItemStack item = this.slots.get(slotIndex).getStack();
        if (item.isEmpty()) return;

        try {
            Optional<Node> currentNodeOpt = getCurrentNode(item);
            if (currentNodeOpt.isEmpty()) return;

            Node currentNode = currentNodeOpt.get();

            if (isRightClick(actionType, button)) {
                handleRightClick(slotIndex, currentNode, actionType, ci);
            } else {
                handleNodeSelection(player, currentNode, ci);
            }
        } catch (IndexOutOfBoundsException | NoSuchElementException ignored) {
        }
    }
    @Unique
    private Optional<Node> getCurrentNode(ItemStack item) {
        return NodesList.stream()
                .filter(node -> ItemStack.areEqual(node.getCompleteStack(), item))
                .findFirst();
    }

    @Unique
    private boolean isRightClick(SlotActionType actionType, int button) {
        return button == 1 && (actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE);
    }

    @Unique
    private void handleRightClick(int slotIndex, Node currentNode, SlotActionType actionType, CallbackInfo ci) {
        int direction = (actionType == SlotActionType.PICKUP) ? 1 : -1;
        int currentNodeIndex = NodesList.indexOf(currentNode);
        int newIndex = (currentNodeIndex + direction + NodesList.size()) % NodesList.size();

        ItemStack newNodeItem = NodesList.get(newIndex).getCompleteStack();
        if (!this.slots.get(slotIndex).getStack().equals(newNodeItem)) {
            this.setStackInSlot(slotIndex, this.getRevision(), newNodeItem);
        }

        ci.cancel();
        playClickSound();
    }

    @Unique
    private void handleNodeSelection(PlayerEntity player, Node currentNode, CallbackInfo ci) {
        String plainName = PlainTextComponentSerializer.plainText().serialize(component(currentNode.getName()));
        player.sendMessage(miniMessage("<green><bold>»</bold></green> <gray>Sending you to " + plainName + "...</gray>"), false);
        Objects.requireNonNull(client.getNetworkHandler()).sendCommand("server " + currentNode.getID());

        ci.cancel();
        closePlayerScreens();
    }

    @Unique
    private void playClickSound() {
        if (client.player != null) {
            client.player.playSound(clickSound, 2f, 1.74f);
        }
    }

    @Unique
    private void closePlayerScreens() {
        if (client.player != null) {
            client.player.closeHandledScreen();
            client.player.closeScreen();
        }
    }
}
