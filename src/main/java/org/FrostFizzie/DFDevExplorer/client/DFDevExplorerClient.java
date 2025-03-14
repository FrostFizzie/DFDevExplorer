package org.FrostFizzie.DFDevExplorer.client;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import org.FrostFizzie.DFDevExplorer.Features.Features;
import org.FrostFizzie.DFDevExplorer.Node;

import java.util.ArrayList;
import java.util.List;



public class DFDevExplorerClient implements ClientModInitializer {
        public static List<Node> NodesList = new ArrayList<>();
        public static MinecraftClient client = MinecraftClient.getInstance();
    @Override
    public void onInitializeClient() {
        NodesList.add(new Node(Items.STRUCTURE_BLOCK, "<red>Node Beta", "<red>Project", "", "beta"));
        NodesList.add(new Node(Items.COMMAND_BLOCK, "<#f7b77c>Node Dev", "<#f7b77c>Parliament", "", "dev"));
        NodesList.add(new Node(Items.REPEATING_COMMAND_BLOCK, "<#9251f5>Node Dev 2", "<#9251f5>Palace", "", "dev2"));
        NodesList.add(new Node(Items.CHAIN_COMMAND_BLOCK, "<#9afcd7>Node Dev 3", "<#9afcd7>Forest", "", "dev3"));
        Features.init();
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            Features.tick();
        });

        ItemTooltipCallback.EVENT.register(Features::tooltip);

        HudRenderCallback.EVENT.register(Features::hudRender);

        WorldRenderEvents.LAST.register(Features::worldRenderLast);

        ClientLifecycleEvents.CLIENT_STOPPING.register(Features::clientStop);
    }
    public static Text miniMessage(String message) {
        return MinecraftClientAudiences.of().asNative(MiniMessage.miniMessage().deserialize(message).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

    }
    public static Component component(Text message) {
        return MinecraftClientAudiences.of().asAdventure(message);
    }

}
