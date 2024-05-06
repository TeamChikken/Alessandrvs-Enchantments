package com.alessandrv.alessandrvenchantments;

import com.alessandrv.alessandrvenchantments.enchantments.*;
import com.alessandrv.alessandrvenchantments.particles.ModParticles;
import com.alessandrv.alessandrvenchantments.statuseffects.*;
import com.alessandrv.alessandrvenchantments.util.config.ModConfig;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ModInitializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlessandrvEnchantments implements ModInitializer {

    private static ModConfig config;
    public static final Logger LOGGER = LoggerFactory.getLogger("alessandrvenchantments");

    public static final Identifier SPOTTER_OUTLINE = new Identifier("alessandrvenchantments", "textures/misc/spotter_outline.png");




        private static List<String> VALID_IDS;
    private static final ExecutorService em4esExecutor = Executors.newSingleThreadExecutor();

    public static final TagKey<Structure> DESERT_PYRAMID = TagKey.of(Registry.STRUCTURE_KEY, new Identifier("alessandvenchantments", "test"));






    @Override
    public void onInitialize() {


        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();


        //Enchantments
        ModEnchantments.registerEnchantments();
        //Status Effects
        ModStatusEffects.registerStatuses();
        //Particles
        ModParticles.registerParticles();
        //Items


    }

    public static ModConfig getConfig() {
        return config;
    }


}