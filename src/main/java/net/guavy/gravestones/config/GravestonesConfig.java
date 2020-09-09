package net.guavy.gravestones.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Config(name = "gravestones")
public class GravestonesConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public MainSettings mainSettings = new MainSettings();

    public static GravestonesConfig getConfig() {
        return AutoConfig.getConfigHolder(GravestonesConfig.class).getConfig();
    }

    public static class MainSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableGraves = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableGraveLooting = false;

        @ConfigEntry.Gui.Tooltip
        public int minimumOpLevelToLoot = 4;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public GravestoneRetrievalType retrievalType = GravestoneRetrievalType.ON_BREAK;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public GravestoneDropType dropType = GravestoneDropType.PUT_IN_INVENTORY;

        @ConfigEntry.Gui.Tooltip
        @UsePercentage(min = 0, max = 1)
        public double xpPercentage = 0.75D;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface UsePercentage {
        double min();

        double max();
    }
}
