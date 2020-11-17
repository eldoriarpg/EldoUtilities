package de.eldoria.eldoutilities.localization;

import org.bukkit.entity.Player;

public final class Replacement {
	private final String key;
	private String value;
	private boolean caseSensitive;

	private Replacement(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Creates a new replacement.
	 *
	 * @param key     key of replacement
	 * @param value   value for replacement
	 * @param formats format which should be applied on the replacement.
	 *
	 * @return replacement with registered replacement
	 */
	public static Replacement create(String key, String value, char... formats) {
		Replacement replacement = new Replacement("%" + key + "%", value);
		return replacement.addFormatting(formats);
	}

	/**
	 * Creates a new replacement.
	 *
	 * @param key     key of replacement
	 * @param value   value which provides a string via {@link Object#toString()}
	 * @param formats format which should be applied on the replacement.
	 *
	 * @return replacement with registered replacement
	 */
	public static Replacement create(String key, Object value, char... formats) {
		return create(key, value.toString(), formats);
	}

	/**
	 * Creates a new replacement.
	 *
	 * @param key     key of replacement
	 * @param value   value which provides a string via {@link Enum#name()}
	 * @param formats format which should be applied on the replacement.
	 *
	 * @return replacement with registered replacement
	 */
	public static Replacement create(String key, Enum<?> value, char... formats) {
		return create(key, value.name(), formats);
	}

	/**
	 * Creates a new replacement.
	 *
	 * @param key     key of replacement
	 * @param value   value which provides the name of the player
	 * @param formats format which should be applied on the replacement.
	 *
	 * @return replacement with registered replacement
	 */
	public static Replacement create(String key, Player value, char... formats) {
		return create(key, value.getName(), formats);
	}

	/**
	 * Add formatting codes to the replacement. A §r will be appended after the replacement. Only provide the formatting
	 * character. Without § or &.
	 *
	 * @param format      format which should be applied on the replacement.
	 * @param afterFormat The formatting codes which should be applied after the §r.
	 *
	 * @return replacement with formatting set
	 */
	public Replacement addFormatting(char[] format, char... afterFormat) {
		StringBuilder builder = new StringBuilder();
		for (char aChar : format) {
			builder.append("§").append(aChar);
		}
		builder.append(value).append("§r");
		for (char aChar : afterFormat) {
			builder.append("§").append(aChar);
		}
		value = builder.toString();
		return this;
	}

	/**
	 * Add formatting codes to the replacement. A §r will be appended after the replacement. Only provide the formatting
	 * character. Without § or &.
	 *
	 * @param format format which should be applied on the replacement.
	 *
	 * @return replacement with formatting set
	 */
	public Replacement addFormatting(char... format) {
		return addFormatting(format, new char[0]);
	}

	/**
	 * Set the replacement to ignore case of placeholder value
	 *
	 * @return Replacement with value changed
	 */
	public Replacement matchCase() {
		this.caseSensitive = false;
		return this;
	}


	/**
	 * Invoke the replacement on the string.
	 *
	 * @param string string to replace
	 *
	 * @return string with key replaced by value.
	 */
	public String invoke(String string) {
		if (!caseSensitive) {
			return string.replaceAll("(?i)" + key, value);
		}
		return string.replace(key, value);
	}
}
