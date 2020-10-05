package de.eldoria.eldoutilities.utils;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

import java.util.logging.Level;

public final class AttributeUtil {
    private AttributeUtil() {
    }

    public static void setAttributeValue(AttributeInstance attribute, double target) {
        if (attribute == null) return;
        attribute.setBaseValue(target / (attribute.getValue() / Math.max(attribute.getBaseValue(), 1)));
    }

    public static void setAttributeValue(LivingEntity entity, Attribute attribute, double target) {
        AttributeInstance a = entity.getAttribute(attribute);
        setAttributeValue(a, target);
    }

    public static void syncAttributeValue(LivingEntity source, LivingEntity target, Attribute attribute) {
        AttributeInstance sourceAttribute = source.getAttribute(attribute);
        AttributeInstance targetAttribute = target.getAttribute(attribute);

        if (sourceAttribute == null) {
            Bukkit.getLogger().log(Level.WARNING, "[EldoUtilities] Attemted to sync attribute "
                    + attribute + " between source " + source.getType() + " and target " + target.getType()
                    + ", but Attribute is not present on source", new IllegalArgumentException());
            return;
        }
        if (targetAttribute == null) {
            Bukkit.getLogger().log(Level.WARNING, "[EldoUtilities] Attemted to sync attribute "
                    + attribute + " between source " + source.getType() + " and target " + target.getType()
                    + ", but Attribute is not present on target", new IllegalArgumentException());
            return;
        }

        setAttributeValue(targetAttribute, sourceAttribute.getValue());
    }

}
