package de.eldoria.eldoutilities.entity;

import de.eldoria.eldoutilities.utils.AttributeUtil;
import de.eldoria.eldoutilities.utils.EMath;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EntityBuilder {
    private final LivingEntity entity;

    private EntityBuilder(LivingEntity entity) {
        this.entity = entity;
    }

    public static EntityBuilder of(LivingEntity entity) {
        return new EntityBuilder(entity);
    }

    public static EntityBuilder of(EntityType entity, Location location) {
        return new EntityBuilder((LivingEntity) location.getWorld().spawnEntity(location, entity));
    }

    public EntityBuilder withPassenger(@NotNull Entity... passengers) {
        for (Entity passenger : passengers) {
            entity.addPassenger(passenger);
        }
        return this;
    }

    public EntityBuilder withScoreboardTag(@NotNull String tag) {
        entity.addScoreboardTag(tag);
        return this;
    }

    public EntityBuilder withVelocity(@NotNull Vector velocity) {
        entity.setVelocity(velocity);
        return this;
    }

    public EntityBuilder withRotation(float yaw, float pitch) {
        entity.setRotation(yaw, pitch);
        return this;
    }

    public EntityBuilder withFireTicks(int ticks) {
        entity.setFireTicks(ticks);
        return this;
    }

    public EntityBuilder asPersistent(boolean persistent) {
        entity.setPersistent(persistent);
        return this;
    }

    public EntityBuilder withFallDistance(float distance) {
        entity.setFallDistance(distance);
        return this;
    }

    public EntityBuilder withTicksLived(int value) {
        entity.setTicksLived(value);
        return this;
    }

    public EntityBuilder withVisibleCustomName(boolean flag) {
        entity.setCustomNameVisible(flag);
        return this;
    }

    public EntityBuilder withGlowing(boolean flag) {
        entity.setGlowing(flag);
        return this;
    }

    public EntityBuilder asInvulnerable(boolean flag) {
        entity.setInvulnerable(flag);
        return this;
    }

    public EntityBuilder asSilent(boolean flag) {
        entity.setSilent(flag);
        return this;
    }

    public EntityBuilder withGravity(boolean gravity) {
        entity.setGravity(gravity);
        return this;
    }

    public EntityBuilder withMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) {
        entity.setMetadata(metadataKey, newMetadataValue);
        return this;
    }

    public EntityBuilder withCustomName(@Nullable String name) {
        entity.setCustomName(name);
        return this;
    }

    public EntityBuilder setNBT(Consumer<PersistentDataContainer> change) {
        change.accept(entity.getPersistentDataContainer());
        return this;
    }

    public EntityBuilder withAttribute(Attribute attribute, double value) {
        AttributeUtil.setAttributeValue(entity, attribute, value);
        return this;
    }

    public EntityBuilder withPotionEffect(@NotNull PotionEffect effect) {
        entity.addPotionEffect(effect);
        return this;
    }

    public EntityBuilder withAI(boolean ai) {
        entity.setAI(ai);

    }

    public EntityBuilder setCollidable(boolean collidable) {
        entity.setCollidable(collidable);
    }

    public EntityBuilder setInvisible(boolean invisible) {
        entity.setInvisible(invisible);
    }

    public EntityBuilder withHealth(double health) {
        entity.setHealth(EMath.clamp(0, AttributeUtil.getAttributeValue(entity, Attribute.GENERIC_MAX_HEALTH), health));
        return this;
    }

    public EntityBuilder withAbsorptionAmount(double amount) {
        entity.setAbsorptionAmount(amount);
        return this;
    }
}
