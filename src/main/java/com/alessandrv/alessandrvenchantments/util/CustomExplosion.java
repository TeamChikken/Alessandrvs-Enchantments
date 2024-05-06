package com.alessandrv.alessandrvenchantments.util;

import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomExplosion extends Explosion {
    private final World world;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity entity;
    private final float power;

    public CustomExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, DestructionType destructionType) {
        super(world, entity, x, y, z, power, createFire, destructionType);
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public DamageSource getDamageSource() {
        return null;
    }

    public void DamageEntities() {
        this.world.emitGameEvent(this.entity, GameEvent.EXPLODE, new Vec3d(this.x, this.y, this.z));

        List<Entity> entities = this.world.getOtherEntities(this.entity, new Box(this.x - this.power, this.y - this.power, this.z - this.power, this.x + this.power, this.y + this.power, this.z + this.power));
        Vec3d explosionPosition = new Vec3d(this.x, this.y, this.z);

        for (Entity entity : entities) {
            if (!(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrbEntity)){


                if (!entity.isImmuneToExplosion()) {
                    double distanceToEntity = entity.squaredDistanceTo(explosionPosition);
                    double proximity = 1.0 - Math.sqrt(distanceToEntity) / this.power;

                    if (proximity > 0) {
                        double damage = (proximity * proximity + proximity) / 2.0 * 7.0 * this.power + 1.0;
                        entity.damage(this.getDamageSource(), (float) damage);

                        if (entity instanceof LivingEntity livingEntity) {
                            double knockback = ProtectionEnchantment.transformExplosionKnockback(livingEntity, proximity);
                            Vec3d knockbackVector = explosionPosition.subtract(entity.getPos()).normalize().multiply(knockback);
                            entity.setVelocity(entity.getVelocity().add(knockbackVector));
                        }
                    }
                }
            }
        }
    }
}