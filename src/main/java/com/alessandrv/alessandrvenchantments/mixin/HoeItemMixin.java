package com.alessandrv.alessandrvenchantments.mixin;

import com.alessandrv.alessandrvenchantments.enchantments.BoneMealEnchantment;
import com.alessandrv.alessandrvenchantments.enchantments.ModEnchantments;
import com.alessandrv.alessandrvenchantments.statuseffects.ModStatuses;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin extends MiningToolItem {


    public HoeItemMixin(float attackDamage, float attackSpeed, ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> ci) {
        PlayerEntity player = context.getPlayer();
        int bonemealLevel = EnchantmentHelper.getLevel(ModEnchantments.BONEMEAL, context.getStack());
        if (bonemealLevel > 0) {
            assert player != null;
            if (!player.hasStatusEffect(ModStatuses.BONEMEALCOOLDOWN) && player.isSneaking() || player.isCreative() && player.isSneaking()) {
                World world = context.getWorld();
                BlockPos blockPos = context.getBlockPos();

                // Calcola la dimensione dell'area in base al livello dell'incantesimo
                int areaSize = switch (bonemealLevel) {
                    case 2 -> 3;
                    case 3 -> 9;
                    default -> 1;
                };

                // Calcola gli offset per scannerizzare l'area
                int offsetStart = -(areaSize / 2);
                int offsetEnd = areaSize / 2;

                // Itera attraverso l'area intorno al blocco centrale
                for (int offsetX = offsetStart; offsetX <= offsetEnd; offsetX++) {
                    for (int offsetZ = offsetStart; offsetZ <= offsetEnd; offsetZ++) {
                        BlockPos targetPos = blockPos.add(offsetX, 0, offsetZ);
                        BlockPos targetPosAbove = targetPos.up();

                        // Esegui l'azione nel blocco centrale o nei blocchi circostanti
                        if (HoeItemMixin.useOnFertilizable(context.getStack(), world, targetPos)) {
                            if (!world.isClient) {
                                for (int i = 0; i < 3; i++) {
                                    world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, targetPos, 0);
                                }
                            }
                        } else if (HoeItemMixin.useOnFertilizable(context.getStack(), world, targetPosAbove)) {
                            if (!world.isClient) {
                                for (int i = 0; i < 3; i++) {
                                    world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, targetPosAbove, 0);
                                }
                            }
                        }
                    }
                }

                player.addStatusEffect(new StatusEffectInstance(ModStatuses.BONEMEALCOOLDOWN, BoneMealEnchantment.getCooldown() *20, 0, false, false, true));

                ci.setReturnValue(ActionResult.SUCCESS);

            }
        }
    }


    private static boolean useOnFertilizable(ItemStack stack, World world, BlockPos pos) {
        Fertilizable fertilizable;
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof Fertilizable && (fertilizable = (Fertilizable) blockState.getBlock()).isFertilizable(world, pos, blockState, world.isClient)) {
            if (world instanceof ServerWorld) {
                if (fertilizable.canGrow(world, world.random, pos, blockState)) {
                    fertilizable.grow((ServerWorld)world, world.random, pos, blockState);
                }
                stack.damage(1, world.random, null);
            }
            return true;
        }
        return false;
    }
}