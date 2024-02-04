package com.alessandrv.alessandrvenchantments.statuseffects;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;


public class FreezingStatusEffect extends StatusEffect {
    public FreezingStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFFFFFF);
    }



    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        entity.damage(entity.getDamageSources().freeze(), 2.0f);

    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.setFrozenTicks(200);
    }
}