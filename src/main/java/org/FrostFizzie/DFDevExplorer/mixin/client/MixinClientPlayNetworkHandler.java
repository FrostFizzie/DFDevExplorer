package org.FrostFizzie.DFDevExplorer.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import org.FrostFizzie.DFDevExplorer.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

import static org.FrostFizzie.DFDevExplorer.client.DFDevExplorerClient.*;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @ModifyVariable(method = "onInventory", at = @At(value = "HEAD"), argsOnly = true)
    public InventoryS2CPacket onInventory(InventoryS2CPacket packet) {
        if (inventoryName == null) {
            return packet;
        }
        if (packet.getContents().size() == 45 && inventoryName.getString().equals("Select Node") && Objects.requireNonNull(client.player).getScoreboard() != null) {
            List<ItemStack> stacks = packet.getContents();
            Node node = NodesList.stream().anyMatch(search -> search.getIP().equalsIgnoreCase("")) ? NodesList.stream().filter(search -> search.getIP().equalsIgnoreCase("")).findFirst().get() : NodesList.getFirst();
            stacks.set(8, node.getCompleteStack());
            return new InventoryS2CPacket(packet.getSyncId(), packet.getRevision(), (DefaultedList<ItemStack>) packet.getContents(), packet.getCursorStack());
        }
        return packet;
    }
    @Inject(method = "onOpenScreen", at = @At(value = "HEAD"))
    public void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        inventoryName = packet.getName();
    }
    @Inject(method = "onCloseScreen", at = @At(value = "HEAD"))
    public void onCloseScreen(CloseScreenS2CPacket packet, CallbackInfo ci) {
        inventoryName = null;
    }

}
