package net.guavy.gravestones.client;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.GuiRegistry;
import me.sargunvohra.mcmods.autoconfig1u.util.Utils;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.guavy.gravestones.Gravestones;
import net.guavy.gravestones.client.render.GravestoneBlockEntityRenderer;
import net.guavy.gravestones.config.GravestonesConfig;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class GravestonesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(Gravestones.GRAVESTONE_BLOCK_ENTITY, GravestoneBlockEntityRenderer::new);

        GuiRegistry guiRegistry = AutoConfig.getGuiRegistry(GravestonesConfig.class);

        guiRegistry.registerAnnotationProvider((s, field, config, defaults, guiRegistryAccess) -> {
            GravestonesConfig.UsePercentage bounds = field.getAnnotation(GravestonesConfig.UsePercentage.class);
            return Collections.singletonList(ConfigEntryBuilder.create().startIntSlider(new TranslatableText(s), MathHelper.ceil(Utils.getUnsafely(field, config, 0.0) * 100), MathHelper.ceil(bounds.min() * 100), MathHelper.ceil(bounds.max() * 100)).setDefaultValue(() -> MathHelper.ceil((double) Utils.getUnsafely(field, defaults) * 100)).setSaveConsumer((newValue) -> Utils.setUnsafely(field, config, newValue / 100d)).setTextGetter(integer -> new LiteralText(String.format("%d%%", integer))).build());
        }, field -> field.getType() == Double.TYPE || field.getType() == Double.class, GravestonesConfig.UsePercentage.class);
    }
}
