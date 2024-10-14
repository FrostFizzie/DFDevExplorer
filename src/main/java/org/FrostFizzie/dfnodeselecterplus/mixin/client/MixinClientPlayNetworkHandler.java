package org.FrostFizzie.dfnodeselecterplus.mixin.client;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.FrostFizzie.dfnodeselecterplus.Node;
import org.FrostFizzie.dfnodeselecterplus.client.DfnodeselecterplusClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static org.FrostFizzie.dfnodeselecterplus.client.DfnodeselecterplusClient.*;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @ModifyVariable(method = "onInventory", at = @At(value = "HEAD"))
    public InventoryS2CPacket onInventory(InventoryS2CPacket packet) {
        if (packet.getContents().size() == 45 && inventoryName.getString().equals("Select Node") && client.player.getScoreboard() != null) {
            List<ItemStack> stacks = packet.getContents();
            Node node = NodesList.stream().filter(search -> search.getIP().equalsIgnoreCase("")).count() >=1 ? NodesList.stream().filter(search -> search.getIP().equalsIgnoreCase("")).findFirst().get() : NodesList.get(0);
            stacks.set(8, node.getNodeAsItem());
            InventoryS2CPacket inventoryPacket = new InventoryS2CPacket(packet.getSyncId(), packet.getRevision(), (DefaultedList<ItemStack>) packet.getContents(), packet.getCursorStack());
            return inventoryPacket;
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
