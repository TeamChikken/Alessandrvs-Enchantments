package com.alessandrv.alessandrvenchantments.particles;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class WaveParticle extends SpriteBillboardParticle {
    private final SpriteProvider sprites;
    private static final Quaternion QUATERNION = new Quaternion(0F, -0.7F, 0.7F, 0F);

    WaveParticle(ClientWorld world, double x, double y, double z, SpriteProvider sprites) {
        super(world, x, y + 0.5, z, 0.0, 0.0, 0.0);
        this.scale = 7F;
        this.setVelocity(0D, 0D, 0D);
        this.maxAge = 15;
        this.sprites = sprites;
        this.setSpriteForAge(sprites);
    }


    @Override
    public void buildGeometry(VertexConsumer buffer, Camera camera, float ticks) {
        Vec3d vec3 = camera.getPos();
        float x = (float) (MathHelper.lerp(ticks, this.prevPosX, this.x) - vec3.getX());
        float y = (float) (MathHelper.lerp(ticks, this.prevPosY, this.y) - vec3.getY());
        float z = (float) (MathHelper.lerp(ticks, this.prevPosZ, this.z) - vec3.getZ());

        Vec3f[] vector3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
        // Additional vertices for underside faces
        Vec3f[] vector3fsBottom = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, -1.0F, 0.0F)};

        float f4 = this.getSize(ticks);

        for (int i = 0; i < 4; ++i) {
            Vec3f vector3f = vector3fs[i];
            vector3f.rotate(QUATERNION);
            vector3f.scale(f4);
            vector3f.add(x, y, z);

            // Create additional vertices for underside faces
            Vec3f vector3fBottom = vector3fsBottom[i];
            vector3fBottom.rotate(QUATERNION);
            vector3fBottom.scale(f4);
            vector3fBottom.add(x, y - 0.1F, z); // Slightly lower to avoid z-fighting
        }

        float f7 = this.getMinU();
        float f8 = this.getMaxU();
        float f5 = this.getMinV();
        float f6 = this.getMaxV();
        int light = this.getBrightness(ticks);

        // Render the top faces
        buffer.vertex(vector3fs[0].getX(), vector3fs[0].getY(), vector3fs[0].getZ()).texture(f8, f6).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        buffer.vertex(vector3fs[1].getX(), vector3fs[1].getY(), vector3fs[1].getZ()).texture(f8, f5).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        buffer.vertex(vector3fs[2].getX(), vector3fs[2].getY(), vector3fs[2].getZ()).texture(f7, f5).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        buffer.vertex(vector3fs[3].getX(), vector3fs[3].getY(), vector3fs[3].getZ()).texture(f7, f6).color(this.red, this.green, this.blue, this.alpha).light(light).next();

        // Render the underside faces
        buffer.vertex(vector3fs[3].getX(), vector3fs[3].getY(), vector3fs[3].getZ()).texture(f7, f6).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        buffer.vertex(vector3fs[2].getX(), vector3fs[2].getY(), vector3fs[2].getZ()).texture(f7, f5).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        buffer.vertex(vector3fs[1].getX(), vector3fs[1].getY(), vector3fs[1].getZ()).texture(f8, f5).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        buffer.vertex(vector3fs[0].getX(), vector3fs[0].getY(), vector3fs[0].getZ()).texture(f8, f6).color(this.red, this.green, this.blue, this.alpha).light(light).next();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.sprites);
    }

    @Environment(EnvType.CLIENT)
    public record Factory(SpriteProvider sprites) implements ParticleFactory<DefaultParticleType> {
        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WaveParticle(world, x, y, z, sprites);
        }
    }
}