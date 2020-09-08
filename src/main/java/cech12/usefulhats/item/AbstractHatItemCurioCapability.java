package cech12.usefulhats.item;

import cech12.usefulhats.client.UsefulHatLayer;
import cech12.usefulhats.client.UsefulHatModel;
import cech12.usefulhats.config.Config;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.ArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.capability.ICurio;

import java.util.Map;

/**
 * This class is a capability for the Curios API.
 */
public class AbstractHatItemCurioCapability implements ICurio {

    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

    private final ItemStack stack;
    private Object model;

    public AbstractHatItemCurioCapability(ItemStack stack) {
        this.stack = stack;
    }

    //these methods should not be implemented because of the CurioChangeEvent
    //public void onEquip(String identifier, int index, LivingEntity livingEntity) {}
    //public void onUnequip(String identifier, int index, LivingEntity livingEntity) {}

    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity) {
        return Config.CURIOS_ENABLED.getValue();
    }

    @Override
    public void onCurioTick(String identifier, int index, LivingEntity livingEntity) {
        if (Config.CURIOS_ENABLED.getValue() && livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            this.stack.getItem().onArmorTick(this.stack, player.world, player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private BipedModel<LivingEntity> getModel() {
        if (model == null) {
            if (this.stack.getItem() instanceof IUsefulHatModelOwner) {
                model = new UsefulHatModel<>();
            } else {
                model = new BipedModel<>(0.5F);
            }
        }
        return (BipedModel<LivingEntity>) model;
    }

    @OnlyIn(Dist.CLIENT)
    private ResourceLocation getTexture(String type) {
        ResourceLocation location;
        ArmorItem item = (ArmorItem) this.stack.getItem();
        if (item instanceof IUsefulHatModelOwner) {
            location =  UsefulHatLayer.getTexture(this.stack, type);
        } else {
            //mostly copied from BipedArmorLayer class
            String texture = item.getArmorMaterial().getName();
            String domain = "minecraft";
            int idx = texture.indexOf(':');
            if (idx != -1) {
                domain = texture.substring(0, idx);
                texture = texture.substring(idx + 1);
            }
            String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, 1, type == null ? "" : String.format("_%s", type));
            location = ARMOR_TEXTURE_RES_MAP.get(s1);
            if (location == null) {
                location = new ResourceLocation(s1);
                ARMOR_TEXTURE_RES_MAP.put(s1, location);
            }
        }
        return location;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasRender(String identifier, LivingEntity livingEntity) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doRender(String identifier, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        Item item = this.stack.getItem();
        BipedModel<LivingEntity> model = this.getModel();
        model.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        RenderHelper.followBodyRotations(entity, model);
        //mostly copied from UsefulHatLayer
        model.setModelAttributes(model);
        boolean flag1 = this.stack.hasEffect();
        int i = ((net.minecraft.item.IDyeableArmorItem)item).getColor(this.stack);
        float f = (float)(i >> 16 & 255) / 255.0F;
        float f1 = (float)(i >> 8 & 255) / 255.0F;
        float f2 = (float)(i & 255) / 255.0F;
        Minecraft.getInstance().getTextureManager().bindTexture(this.getTexture(null));
        GlStateManager.color4f(f, f1, f2, 1.0F);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Minecraft.getInstance().getTextureManager().bindTexture(this.getTexture("overlay"));
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        if (flag1) {
            ArmorLayer.func_215338_a(Minecraft.getInstance().getTextureManager()::bindTexture, entity, model, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

}