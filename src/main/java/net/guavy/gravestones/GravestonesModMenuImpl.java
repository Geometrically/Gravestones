package net.guavy.gravestones;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.guavy.gravestones.config.GravestonesConfig;
import net.minecraft.client.gui.screen.Screen;

import java.util.Optional;
import java.util.function.Supplier;

public class GravestonesModMenuImpl implements ModMenuApi {
    @Override
    public String getModId() {
        return "gravestones";
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(GravestonesConfig.class, parent).get();
    }
}
