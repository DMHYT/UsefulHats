package cech12.usefulhats.item;

import cech12.usefulhats.UsefulHatsMod;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractHatItem extends ArmorItem implements IDyeableArmorItem {

    private final ResourceLocation resourceLocation;
    private final int initColor;

    private ArrayList<Enchantment> allowedEnchantments = new ArrayList<>();
    private ArrayList<Enchantment> allowedAdditionalBookEnchantments = new ArrayList<>();

    public AbstractHatItem(String name, HatArmorMaterial material, int initColor) {
        super(material, EquipmentSlotType.HEAD, (new Properties()).group(ItemGroup.COMBAT));
        this.resourceLocation = new ResourceLocation(UsefulHatsMod.MOD_ID, name);
        this.setRegistryName(this.resourceLocation);
        this.initColor = initColor;
        this.allowedEnchantments.add(Enchantments.UNBREAKING);
        this.allowedEnchantments.add(Enchantments.RESPIRATION);
        this.allowedEnchantments.add(Enchantments.AQUA_AFFINITY);
        this.allowedAdditionalBookEnchantments.add(Enchantments.THORNS);
        this.allowedAdditionalBookEnchantments.add(Enchantments.MENDING);
        this.allowedAdditionalBookEnchantments.add(Enchantments.BINDING_CURSE);
        this.allowedAdditionalBookEnchantments.add(Enchantments.VANISHING_CURSE);
    }

    protected static int rawColorFromRGB(int red, int green, int blue) {
        int rgb = Math.max(Math.min(0xFF, red), 0);
        rgb = (rgb << 8) + Math.max(Math.min(0xFF, green), 0);
        rgb = (rgb << 8) + Math.max(Math.min(0xFF, blue), 0);
        return rgb;
    }

    /**
     * Add an allowed enchantment to this item.
     * Standard allowed enchantments are MENDING and UNBREAKING.
     * @param enchantment enchantment to add
     */
    protected void addAllowedEnchantment(Enchantment enchantment) {
        this.allowedEnchantments.add(enchantment);
    }

    /**
     * Add an allowed additional book enchantment to this item.
     * Standard allowed additional book enchantments are BINDING_CURSE and VANISHING_CURSE.
     * @param enchantment enchantment to add
     */
    protected void addAllowedAdditionalBookEnchantment(Enchantment enchantment) {
        this.allowedAdditionalBookEnchantments.add(enchantment);
    }

    @Override
    public int getColor(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getChildTag("display");
        return compoundnbt != null && compoundnbt.contains("color", 99) ? compoundnbt.getInt("color") : this.initColor;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        ArrayList<Enchantment> allowedE = new ArrayList<>(allowedEnchantments);
        allowedE.addAll(allowedAdditionalBookEnchantments);
        for (Enchantment enchantment : EnchantmentHelper.getEnchantments(book).keySet()) {
            if (!allowedE.contains(enchantment)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return this.allowedEnchantments.contains(enchantment);
    }

    /**
     * Copy of {@link ItemStack#damageItem(int, LivingEntity, Consumer)} to enable own damaging of hat items.
     */
    protected void damageHatItemByOne(ItemStack stack, PlayerEntity entity) {
        if (!entity.world.isRemote && !entity.abilities.isCreativeMode) {
            if (this.isDamageable()) {
                if (stack.attemptDamageItem(1, entity.getRNG(), entity instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity : null)) {
                    entity.sendBreakAnimation(EquipmentSlotType.HEAD);
                    stack.shrink(1);
                    entity.addStat(Stats.ITEM_BROKEN.get(this));
                    stack.setDamage(0);
                }
            }
        }
    }

    /**
     * Disables vanilla damaging.
     */
    @Deprecated
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    /**
     * Disables "When on head" line of ArmorItem Tooltip
     */
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return HashMultimap.create();
    }

    /**
     * Adds "When on head" line to end of tooltip.
     * When hat item has no effect, override this method with an empty method.
     */
    public void onItemToolTipEvent(ItemStack stack, List<ITextComponent> tooltip) {
        //tooltip.add(new StringTextComponent("Durability: " + (stack.getMaxDamage() - stack.getDamage()) + "/" + stack.getMaxDamage()).applyTextStyle(TextFormatting.RED));
        tooltip.add(new StringTextComponent(""));
        tooltip.add((new TranslationTextComponent("item.modifiers." + EquipmentSlotType.HEAD.getName())).applyTextStyle(TextFormatting.GRAY));
    }

}
