package org.FrostFizzie.DFDevExplorer.mixin.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import net.minecraft.util.collection.DefaultedList;
import org.FrostFizzie.DFDevExplorer.Features.Features;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ScreenHandler.class)
public class MixinScreenHandler {

    @Unique


    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        Features.onSlotClick( (ScreenHandler) (Object) this, slotIndex, button, actionType, player, ci);
    }

}
