package cech12.usefulhats.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nullable;
import java.util.List;

public class ChoppingHatItem extends AbstractMiningHatItem implements IBreakSpeedChanger {

    public ChoppingHatItem() {
        super("chopping_hat", HatArmorMaterial.CHOPPING, rawColorFromRGB(91, 91, 91));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        int value = (int) (this.getEnchantmentValue(stack) * 100);
        tooltip.add(new StringTextComponent("Chopping with an axe is " + value + "% faster. (stackable with enchantments and potions)"));
    }

    @Override
    public void onBreakSpeedEvent(PlayerEvent.BreakSpeed event, ItemStack headSlotItemStack) {
        if (event.getPlayer().getHeldItemMainhand().getToolTypes().contains(ToolType.AXE) && event.getState().isToolEffective(ToolType.AXE)) {
            event.setNewSpeed(event.getOriginalSpeed() * (1.0F + this.getEnchantmentValue(headSlotItemStack)));
            this.damageHatItemByOne(headSlotItemStack, event.getPlayer());
        }
    }
}
