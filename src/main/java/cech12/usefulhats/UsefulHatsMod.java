package cech12.usefulhats;

import cech12.usefulhats.client.UsefulHatLayer;
import cech12.usefulhats.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static cech12.usefulhats.UsefulHatsMod.MOD_ID;

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid= MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class UsefulHatsMod {

    public static final String MOD_ID = "usefulhats";

    public UsefulHatsMod() {
        ModItems.addEventListeners();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientRegister(FMLClientSetupEvent event) {
        (Minecraft.getInstance().getRenderManager().renderers).forEach((e, r) -> {
            if (r instanceof LivingRenderer) {
                LivingRenderer renderer = (LivingRenderer) r;
                if (renderer instanceof PlayerRenderer || renderer instanceof ArmorStandRenderer) {
                    renderer.addLayer(new UsefulHatLayer(renderer));
                }
            }
        });
        Minecraft.getInstance().getRenderManager().getSkinMap().values().forEach(r -> {
            r.addLayer(new UsefulHatLayer<>(r));
        });
    }

}
