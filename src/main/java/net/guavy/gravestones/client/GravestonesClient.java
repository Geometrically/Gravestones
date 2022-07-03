package net.guavy.gravestones.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.guavy.gravestones.Gravestones;
import net.guavy.gravestones.client.render.GravestoneBlockEntityRenderer;
import net.guavy.gravestones.config.GravestonesConfig;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;

public class GravestonesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(Gravestones.GRAVESTONE_BLOCK_ENTITY, GravestoneBlockEntityRenderer::new);

        GuiRegistry guiRegistry = AutoConfig.getGuiRegistry(GravestonesConfig.class);

        guiRegistry.registerAnnotationProvider((s, field, config, defaults, guiRegistryAccess) -> {
            GravestonesConfig.UsePercentage bounds = field.getAnnotation(GravestonesConfig.UsePercentage.class);
            return Collections.singletonList(ConfigEntryBuilder.create().startIntSlider(Text.translatable(s), MathHelper.ceil(Utils.getUnsafely(field, config, 0.0) * 100), MathHelper.ceil(bounds.min() * 100), MathHelper.ceil(bounds.max() * 100)).setDefaultValue(() -> MathHelper.ceil((double) Utils.getUnsafely(field, defaults) * 100)).setSaveConsumer((newValue) -> Utils.setUnsafely(field, config, newValue / 100d)).setTextGetter(integer -> Text.literal(String.format("%d%%", integer))).build());
        }, field -> field.getType() == Double.TYPE || field.getType() == Double.class, GravestonesConfig.UsePercentage.class);
    }
}
