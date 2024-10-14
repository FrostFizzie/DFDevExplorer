package org.FrostFizzie.dfnodeselecterplus.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.text.Text;
import org.FrostFizzie.dfnodeselecterplus.Node;

import java.util.ArrayList;
import java.util.List;


public class DfnodeselecterplusClient implements ClientModInitializer {
        public static List<Node> NodesList = new ArrayList<>();
        public static Text inventoryName;
        public static MinecraftClient client = MinecraftClient.getInstance();
    @Override
    public void onInitializeClient() {
        NodesList.add(new Node(Items.STRUCTURE_BLOCK, "<red>Node Beta", "<red>Project", "", "beta"));
        NodesList.add(new Node(Items.COMMAND_BLOCK, "<#f7b77c>Node Dev", "<#f7b77c>Parliament", "", "dev"));
        NodesList.add(new Node(Items.CHAIN_COMMAND_BLOCK, "<#9afcd7>Node Dev 2", "<#9afcd7>Palace", "", "dev2"));
        NodesList.add(new Node(Items.REPEATING_COMMAND_BLOCK, "<#9251f5>Node Dev 3", "<#9251f5>Forest", "", "dev3"));
    }

    public static Text miniMessage(String message) {
        return FabricClientAudiences.of().toNative(MiniMessage.miniMessage().deserialize(message).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
    }
}
