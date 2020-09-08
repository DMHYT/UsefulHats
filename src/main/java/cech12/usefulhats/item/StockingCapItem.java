package cech12.usefulhats.item;

import cech12.usefulhats.UsefulHatsUtils;
import cech12.usefulhats.config.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

public class StockingCapItem extends AbstractHatItem {

    public StockingCapItem() {
        super("stocking_cap", HatArmorMaterial.STOCKING, rawColorFromRGB(204, 0, 23), Config.STOCKING_CAP_ENABLED, Config.STOCKING_CAP_DAMAGE_ENABLED);
    }

    /**
     * Has no effect.
     */
    public void onItemToolTipEvent(ItemStack stack, List<ITextComponent> tooltip) {}

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isRemote && random.nextInt(1000) == 0) {
            if (!UsefulHatsUtils.getEquippedHatItemStacks(player).contains(stack)) return; //only one worn stack of this item should add its effect
            this.damageHatItemByOne(stack, player);
        }
    }
}
