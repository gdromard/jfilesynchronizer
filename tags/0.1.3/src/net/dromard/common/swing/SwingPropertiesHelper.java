package net.dromard.common.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;

public class SwingPropertiesHelper {

	/**
	 * Retrieve and transform the property value into a Color.
	 * @param name The property name
	 * @return The corresponding color
	 */
	public static Color asColor(String color) {
		if (color != null) {
			String[] rgb = color.split(",");
			if (rgb.length == 3) {
				try {
					return new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Retrieve and transform the property value into a Dimension.
	 * The comma is used to distinguish x from y.
	 * @param name The property name
	 * @return The dimension if the value is valid
	 * @throws IOException If an error occurred while accessing the properties file.
	 */
	public static Dimension asDimension(String color) {
		String[] rgb = color.split(",");
		if (rgb.length == 2) {
			try {
				return new Dimension(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Retrieve and transform the property value into a Font.
	 * @param name The property name
	 * @return A valid font (or null if invalid)
	 * @throws IOException If an error occurred while accessing the properties file.
	 */
	public static Font asFont(String font) {
		if (font != null) return Font.decode(font);
		return null;
	}

}
