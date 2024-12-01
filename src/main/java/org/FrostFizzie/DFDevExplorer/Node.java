package org.FrostFizzie.DFDevExplorer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.FrostFizzie.DFDevExplorer.client.DFDevExplorerClient.miniMessage;

public class Node {
    Item icon;
    String name;
    String theme;
    String addr;
    String id;
    public Node(Item Icon, String Name, String Theme, String Addr, String Id) {
        this.icon = Icon;
        this.name = Name;
        this.theme = Theme;
        this.addr = Addr;
        this.id = Id;
    }
    public ItemStack getCompleteStack() {
        ItemStack item = new ItemStack(getIcon(), 1);
        item.set(DataComponentTypes.CUSTOM_NAME, getName());
        List<Text> lore = new ArrayList<>();
        lore.add(getTheme());
        lore.add(Text.empty());
        if (!Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getConnection().getAddressAsString(true).substring(MinecraftClient.getInstance().getNetworkHandler().getConnection().getAddressAsString(true).length() - 20).equals(getIP())) {
            lore.add(miniMessage("<dark_gray><italic>Information cannot be viewed"));
            lore.add(miniMessage("<dark_gray><italic>from your current node."));
            lore.add(Text.empty());
            lore.add(miniMessage("<blue>⇄ Left click to connect"));
        } else {
            lore.add(miniMessage("<#ffd47f>♦ " + MinecraftClient.getInstance().getNetworkHandler().getPlayerList().size() + "player"));
            lore.add(miniMessage("<#7fff7f>✔ Online"));
            lore.add(Text.empty());
            lore.add(miniMessage("<#55aaff>↓ You are here"));
        }
        lore.add(miniMessage("<blue>↔ Right click to cycle"));
        item.set(DataComponentTypes.LORE, new LoreComponent(lore));
        return item;
    }
    public String getIP() {
        return this.addr;
    }
    public String getID() {
        return this.id;
    }
    public Text getName() {
        return miniMessage(this.name);
    }
    public Text getTheme() {
        return miniMessage(this.theme);
    }
    public Item getIcon() {
        return this.icon;
    }
}
